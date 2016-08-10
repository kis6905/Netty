package com.btb.cs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.btb.cs.vo.ChattingRoom;

import io.netty.channel.Channel;

/**
 * 전체 채팅 방을 관리하는 Manager
 * 
 * @author iskwon
 */
public class ChattingRoomManager {
	
	private static ChattingRoomManager chatttingRoomManager = new ChattingRoomManager(); 
	
	public static ChattingRoomManager getInstance() {
		return chatttingRoomManager;
	}
	
	// key: Channel / value: roomId
	// 채팅방 나갈 시 어떤 채팅방에 들어와있는지 빨리 찾기 위해 Channel, roomId를 매핑시켜 보관한다.
	private static Map<Channel, String> memberRoomMap = new HashMap<Channel, String>();
	private static List<ChattingRoom> roomList = new ArrayList<ChattingRoom>();

	public boolean putMemberRoomMap(Channel channel, String roomId) {
		return memberRoomMap.put(channel, roomId) != null; // 리턴 값 의미 없음
	}
	
	public boolean removeMemberRoomMap(Channel channel) {
		return memberRoomMap.remove(channel) != null; // 리턴 값 의미 없음
	}
	
	/**
	 * 채널에 매핑된 roomId 리턴
	 * 
	 * @param channel
	 * @return roomId
	 */
	public String getRoomIdOfChannel(Channel channel) {
		return memberRoomMap.get(channel);
	}
	
	/**
	 * 채팅 방 생성
	 * 
	 * @param roomName
	 * @param creatorId 채팅방을 생성하는 memberId
	 * @return roomId 생성된 채팅방의 id
	 */
	public String createChattingRoom(String roomName, String creatorId) {
		try {
			String roomId = UUID.randomUUID().toString().replace("-", "");
			String registeredDate = Utility.getCurrentDateToString("yyyy-MM-dd HH:mm:ss");
			ChattingRoom chattingRoom = new ChattingRoom(roomId, roomName, creatorId, registeredDate);
			addRoomList(chattingRoom);
			return roomId;
		} catch (Exception e) {
			ChattingServer.logger.error("[An error occurred!]", e);
			return null;
		}
	}
	
	public boolean addRoomList(ChattingRoom chattingRoom) {
		return roomList.add(chattingRoom);
	}
	
	public List<ChattingRoom> getChattingRoomList() {
		return roomList;
	}
	
	public ChattingRoom getChattingRoomOfRoomId(String roomId) {
		for (ChattingRoom cr : roomList) {
			if (roomId.equals(cr.getRoomId()))
				return cr;
		}
		return null;
	}
	
	public boolean removeChattingRoom(ChattingRoom chattingRoom) {
		return roomList.remove(chattingRoom);
	}
}
