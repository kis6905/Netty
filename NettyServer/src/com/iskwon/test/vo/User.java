package com.iskwon.test.vo;

import io.netty.buffer.ByteBuf;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class User {
	
	private byte[] data;
	
	private String name = null;
	private Integer age = null;
	
	public User() {
	}
	
	/**
	 * parameter�� ���� String name, int age�� byte[] data�� ��ȯ
	 */
	public User(String name, Integer age) {
		this.name = name;
		this.age = age;
		
		try {
			if (name != null) {
				byte[] ageArr = ByteBuffer.allocate(4).putInt(age).array();
				byte[] nameArr = name.getBytes("UTF-8");
				
				data = new byte[ageArr.length + nameArr.length];
				
				System.arraycopy(ageArr, 0, data, 0, ageArr.length); // ���� �߰�
				System.arraycopy(nameArr, 0, data, ageArr.length, nameArr.length); // �̸� �߰�
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace(); // UTF-8�� �⺻�̹Ƿ� ���� �Ͼ �� ����.
		}
	}
	
	/**
	 * parameter�� ���� byte[] data�� String name, int age�� ��ȯ
	 */
	public User(byte[] data) {
		try {
			byte[] ageArr = new byte[4];
			System.arraycopy(data, 0, ageArr, 0, 4);
			
			ByteBuffer buff = ByteBuffer.allocate(4);
			buff.order(ByteOrder.BIG_ENDIAN);
			buff.put(ageArr);
			buff.flip();
			age = buff.getInt();
			
			byte[] nameArr = new byte[data.length - ageArr.length];
			System.arraycopy(data, ageArr.length, nameArr, 0, data.length - ageArr.length);
			name = new String(nameArr, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace(); // UTF-8�� �⺻�̹Ƿ� ���� �Ͼ �� ����.
		}
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Integer getAge() {
		return age;
	}
	
	public void setAge(Integer age) {
		this.age = age;
	}
	
	public byte[] getData() {
		return data;
	}
	
	public void writeToBuffer(ByteBuf out) {
		out.writeInt(data.length);
		if (data.length > 0)
			out.writeBytes(data);
	}
	
	@Override
	public String toString() {
		String dataSize = data == null ? "0" : Integer.toString(data.length);
		return "User [name=" + name + ", age=" + age + ", dataSize= " + dataSize + "]";
	}
	
}
