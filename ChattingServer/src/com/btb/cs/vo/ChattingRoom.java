package com.btb.cs.vo;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import io.netty.channel.Channel;

public class ChattingRoom {
	
	private String roomId			= null;
	private String roomName			= null;
	private String creatorId		= null;
	private String creatorName		= null;
	private String registeredDate	= null;
	
	// 채팅방에 들어와 있는 Member List
	private List<Member> memberList = new ArrayList<Member>();
	
	public ChattingRoom(String roomId, String roomName, String creatorId, String registeredDate) {
		this.roomId = roomId;
		this.roomName = roomName;
		this.creatorId = creatorId;
		this.registeredDate = registeredDate;
	}
	
	public String getRoomId() {
		return roomId;
	}
	
	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}
	
	public String getRoomName() {
		return roomName;
	}
	
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	
	public String getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}

	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}
	
	public String getRegisteredDate() {
		return getRegisteredDate();
	}
	
	public void setRegisteredDate(String registeredDate) {
		this.registeredDate = registeredDate;
	}
	
	public List<String> getMemberIdList() {
		List<String> memberIdList = new ArrayList<String>();
		
		for (Member member : memberList)
			memberIdList.add(member.getMemberId());
		
		return memberIdList;
	}
	
	public List<Member> getMemberList() {
		return memberList;
	}
	
	public Member getMember(Channel channel) {
		for (Member member : memberList) {
			if (member.getChannel() == channel)
				return member;
		}
		return null;
	}
	
	public boolean addMember(Member member) {
		return memberList.add(member);
	}
	
	public boolean removeMember(Member member) {
		return memberList.remove(member);
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject toJSONObject() {
		JSONObject jsonResult = new JSONObject();
		jsonResult.put("roomId", roomId);
		jsonResult.put("roomName", roomName);
		jsonResult.put("creatorId", creatorId);
		jsonResult.put("creatorName", creatorName);
		jsonResult.put("registeredDate", registeredDate);
		return jsonResult;
	}

	@Override
	public String toString() {
		return "ChattingRoom [roomId=" + roomId
				+ ", roomName=" + roomName
				+ ", creatorId=" + creatorId
				+ ", creatorName=" + creatorName
				+ ", registeredDate=" + registeredDate
				+ ", memberListSize=" + memberList.size()
				+ "]";
	}
}
