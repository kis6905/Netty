package com.btb.cs.netty;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.btb.cs.ChattingRoomManager;
import com.btb.cs.ChattingServer;
import com.btb.cs.Utility;
import com.btb.cs.vo.ChattingRoom;
import com.btb.cs.vo.Member;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class ChattingHandler extends ChannelHandlerAdapter {

	// 사용자 정보 및 채팅 방 정보 관리
	private ChattingRoomManager chattingRoomManager = ChattingRoomManager.getInstance();

	@SuppressWarnings("unchecked")
	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		
		String roomId = chattingRoomManager.getRoomIdOfChannel(channel);
		if (roomId == null) { // channel에 매핑된 roomId가 없는 경우, 즉 이 channel(client)은 어떤 채팅방에도 속하지 않은 경우이다.
			ctx.close();
			return;
		}
		
		ChattingRoom chattingRoom = chattingRoomManager.getChattingRoomOfRoomId(roomId);
		if (chattingRoom == null) { // chattingRoom이 삭제된 경우
			ctx.close();
			return;
		}
		
		Member member = chattingRoom.getMember(channel);
		if (member == null) { // chattingRoom에 해당 channel의 member 객체가 없는 경우
			ctx.close();
			return;
		}
		
		ChattingServer.logger.info("-> [memberId = " + member.getMemberId() + "], [roomId = " + chattingRoom.getRoomId() + "]");
		
		chattingRoom.removeMember(member); // 채팅 방에 현재 사용자 삭제
		List<Member> memberList = chattingRoom.getMemberList();
		
		// { "type": "notice", "message": "iskwon님이 퇴장하셨습니다.", "memberCount": 5 }
		JSONObject jsonResult = new JSONObject();
		
		if (chattingRoom.getCreatorId().equals(member.getMemberId())) { // 방장이 나가는 경우
			jsonResult.put("type", "destroy");
			jsonResult.put("roomId", chattingRoom.getRoomId());
			chattingRoomManager.removeChattingRoom(chattingRoom); // 방장이 나갈 시 방 삭제
		}
		else { // 일반 사용자가 나가는 경우
			chattingRoomManager.removeMemberRoomMap(channel);
			jsonResult.put("type", "notice");
			jsonResult.put("roomId", chattingRoom.getRoomId());
			jsonResult.put("message", member.getMemberId() + "님이 퇴장하셨습니다.");
			jsonResult.put("memberCount", memberList.size());
		}
		
		// Broadcast
		if (memberList != null) {
			for (Member client : memberList) {
				client.getChannel().writeAndFlush(jsonResult.toString());
			}
		}
		ChattingServer.logger.info("<- [jsonResult = " + jsonResult.toString() + "]");
		ctx.close();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		
		String requestMsg = (String) msg;
		ChattingServer.logger.info("-> [requestMsg = " + requestMsg + "]");
		
		Channel channel = ctx.channel();
		JSONObject reqJsonObject = msgToJSONObject(requestMsg);
		JSONObject jsonResult = new JSONObject();
		
		if (requestMsg == null) {
			jsonResult.put("result", 400);
			channel.writeAndFlush(jsonResult.toString());
			ctx.close();
			return;
		}
		
		// JSON Parse Error
		if (reqJsonObject == null) {
			jsonResult.put("result", 400);
			channel.writeAndFlush(jsonResult.toString());
			ctx.close();
			return;
		}
		
		/*
		 * 1. kind 확인 (roomList, create, join, chat)
		 * 2. 각 kind 별 json 예시
		 *    * roomList
		 *       -> { "kind": "roomList", "memberId": "iskwon" }
		 *       <- { "roomList": [ { "roomId": "12345ABCDE", "roomName": "어서오세요", "creatorId": "iskwon", "registeredDate": "2015-12-18 11:22:13" }, {}, {}... ] }
		 * 
		 *    * create 
		 *       -> { "kind": "create", "roomName": "어서오세요", "memberId": "iskwon" }
		 *       <- { "result": 0, "roomId": "12345ABCDE" }
		 *      response 후 연결 끊음
		 *    
		 *    * join
		 *       -> { "kind": "join", "roomId": "12345ABCDE", "memberId": "iskwon" }
		 *       <- { "type": "notice", "roomId": "12345ABCDE", "message": "권일수님이 입장하셨습니다.", "memberCount": 6 }
		 *          { "type": "destroy", "roomId": "12345ABCDE" } // roomId의 해당하는 채팅 방이 없을 때
		 *    
		 *    * chat
		 *       -> { "kind": "chat", "memberId": "iskwon", "message": "안녕하세요~" }
		 *       <- { "type": "chat", "roomId": "12345ABCDE", "message": "안녕하세요~", "memberId": "iskwon", "date": "2015-12-17 19:35:52", "grade": "master" }
		 *    
		 *    * survey
		 *       -> { "kind": "survey", "memberId": "iskwon", "contents": { "question": "방송 재밌나요?", "answers": [ "별로", "그럭저럭", "볼만함", "재밌어요", ... ] } }
		 *       <- { "type": "survey", "roomId": "12345ABCDE", "memberId": "iskwon", "date": "2015-12-17 19:35:52", "grade": "master", "contents": { "question": "방송 재밌나요?", "answers": [ "별로", "그럭저럭", "볼만함", "재밌어요", ... ] } }
		 *    
		 *    * 채팅방 나갈 시
		 *       <- { "type": "notice", "roomId": "12345ABCDE", "message": "iskwon님이 퇴장하셨습니다.", "memberCount": 5 }
		 *    
		 *    * 방장이 나갈 시
		 *       <- { "type": "destroy", "roomId": "12345ABCDE" }
		 *      response 후 연결 끊음
		 */
		
		String roomId = null, roomName = null, memberId = null, message = null;
		List<Member> memberList = null;
		ChattingRoom chattingRoom = null;
		Member member = null;
		JSONObject contents = null;
		
		String kind = (String) reqJsonObject.get("kind");
		switch (kind) {
		case "roomList":
			JSONArray jsonArray = new JSONArray();
			List<ChattingRoom> roomList = chattingRoomManager.getChattingRoomList();
			for (ChattingRoom room : roomList)
				jsonArray.add(room.toJSONObject());
			
			jsonResult.put("roomList", jsonArray);
			channel.writeAndFlush(jsonResult.toString());
			ChattingServer.logger.info("<- [jsonResult = " + jsonResult.toString() + "]");
			ctx.close(); // 방 생성은 응답 후 연결 끊음
			return;
		
		case "create":
			roomName = (String) reqJsonObject.get("roomName");
			memberId = (String) reqJsonObject.get("memberId");
			roomId = chattingRoomManager.createChattingRoom(roomName, memberId);
			jsonResult.put("result", 0);
			jsonResult.put("roomId", roomId);
			channel.writeAndFlush(jsonResult.toString());
			ChattingServer.logger.info("<- [jsonResult = " + jsonResult.toString() + "]");
			ctx.close(); // 방 생성은 응답 후 연결 끊음
			return;
			
		case "join":
			roomId = (String) reqJsonObject.get("roomId");
			memberId = (String) reqJsonObject.get("memberId");
			
			chattingRoom = chattingRoomManager.getChattingRoomOfRoomId(roomId);
			if (chattingRoom == null) { // 채팅 방을 클릭해 조인 했지만 그 사이 채팅 방이 삭제된 경우
				jsonResult.put("type", "destroy");
				jsonResult.put("roomId", roomId);
				channel.writeAndFlush(jsonResult.toString());
				ChattingServer.logger.info("<- [jsonResult = " + jsonResult.toString() + "]");
				ctx.close();
				return;
			}
			member = new Member(memberId, null, chattingRoom.getCreatorId().equals(memberId) ? "master" : "normal", channel);
			chattingRoom.addMember(member);
			chattingRoomManager.putMemberRoomMap(channel, roomId);
			
			memberList = chattingRoom.getMemberList();
			
			jsonResult.put("type", "notice");
			jsonResult.put("roomId", roomId);
			jsonResult.put("roomName", chattingRoom.getRoomName());
			jsonResult.put("message", memberId + "님이 입장하셨습니다.");
			jsonResult.put("memberCount", memberList.size());
			break;
			
		case "chat":
			roomId = chattingRoomManager.getRoomIdOfChannel(channel);
			memberId = (String) reqJsonObject.get("memberId");
			message = (String) reqJsonObject.get("message");
			
			chattingRoom = chattingRoomManager.getChattingRoomOfRoomId(roomId);
			memberList = chattingRoom.getMemberList();
			member = chattingRoom.getMember(channel);
			
			jsonResult.put("type", "chat");
			jsonResult.put("roomId", roomId);
			jsonResult.put("message", message);
			jsonResult.put("memberId", memberId);
			jsonResult.put("date", Utility.getCurrentDateToString("yyyy-MM-dd HH:mm:ss"));
			jsonResult.put("grade", member.getGradeInRoom());
			break;
			
		case "survey":
			roomId = chattingRoomManager.getRoomIdOfChannel(channel);
			memberId = (String) reqJsonObject.get("memberId");
			contents = (JSONObject) reqJsonObject.get("contents");
			
			chattingRoom = chattingRoomManager.getChattingRoomOfRoomId(roomId);
			memberList = chattingRoom.getMemberList();
			member = chattingRoom.getMember(channel);
			
			jsonResult.put("type", "survey");
			jsonResult.put("roomId", roomId);
			jsonResult.put("contents", contents);
			jsonResult.put("memberId", memberId);
			jsonResult.put("date", Utility.getCurrentDateToString("yyyy-MM-dd HH:mm:ss"));
			jsonResult.put("grade", member.getGradeInRoom());
			break;

		default:
			break;
		}
		
		// Broadcast
		if (memberList != null) {
			for (Member client : memberList) {
				if (kind.equals("survey")) { // 설문을 보낸 경우 자신한텐 보내지 않는다.
					if (client.getChannel() != channel)
						client.getChannel().writeAndFlush(jsonResult.toString());
				}
				else
					client.getChannel().writeAndFlush(jsonResult.toString());
			}
		}
		
		ChattingServer.logger.info("<- [jsonResult = " + jsonResult.toString() + "]");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		ChattingServer.logger.error("~~ [An error occurred!]", cause);
		ctx.close();
	}
	
	private JSONObject msgToJSONObject(String msg) {
		JSONObject jsonObject = null;
		try {
			jsonObject = (JSONObject) new JSONParser().parse(msg);
		} catch (ParseException e) {
			ChattingServer.logger.error("~~ [An error occurred!]", e);
		}
		return jsonObject;
	}
	
}
