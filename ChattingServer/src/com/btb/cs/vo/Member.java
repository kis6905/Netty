package com.btb.cs.vo;

import io.netty.channel.Channel;

/**
 * 채팅 사용자
 * 
 * @author iskwon
 */
public class Member {
	
	private String memberId		= null;
	private String password		= null;
	private String memberName	= null;
	private String gradeInRoom	= null;

	private Channel channel		= null;
	
	public Member(String memberId, String memberName, String gradeInRoom, Channel channel) {
		this.memberId = memberId;
		this.memberName = memberName;
		this.gradeInRoom = gradeInRoom;
		this.channel = channel;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public String getGradeInRoom() {
		return gradeInRoom;
	}

	public void setGradeInRoom(String gradeInRoom) {
		this.gradeInRoom = gradeInRoom;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	@Override
	public String toString() {
		return "Member [memberId=" + memberId
				+ ", memberName=" + memberName
				+ ", gradeInRoom=" + gradeInRoom
				+ ", channel=" + channel.hashCode()
				+ "]";
	}
}
