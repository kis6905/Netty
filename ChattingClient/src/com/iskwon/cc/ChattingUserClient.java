package com.iskwon.cc;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ChattingUserClient {
	
	private static Socket socket = null;
	private static DataInputStream dis = null;
	private static DataOutputStream dos = null;
	
	private static String host = null;
	private static int port;
	
	public static void main(String[] args) {
		host = "110.45.190.170";
//		host = "127.0.0.1";
		port = 8000;
		run();
//		System.out.println(getChattingRoomList());
	}
	
	@SuppressWarnings({ "resource", "unchecked" })
	public static void run() {
		// 생성 된 채팅 방 roomId
		// 원랜 roomList를 요청해 그 중 하나를 입력해야한다.
		join("30b83eba252940e3a66bd9059071ee74");
		
		// 채팅 입력
		System.out.println("===== 0 입력 시 종료 / 1 입력 시 질문 =====");
		while (true) {
			String message = new Scanner(System.in).nextLine();
			String body = null;
			if (message.equals("0"))
				break;
			else if (message.equals("1")) {
				JSONObject contentsJson = new JSONObject();
				JSONArray answers = new JSONArray();
				answers.add("별로");
				answers.add("그럭저럭");
				answers.add("재밌어요");
				contentsJson.put("question", "방송 재밌나요?");
				contentsJson.put("answers", answers);
				body = "{ \"kind\": \"survey\", \"memberId\": \"iskwon2\", \"contents\": " + contentsJson.toString() + " }";
			}
			else
				body = "{ \"kind\": \"chat\", \"memberId\": \"iskwon2\", \"message\": \"" + message + "\" }";
			
			// 입력한 메시지 전송
			send(body);
		}
		
		disconnect();
		System.out.println("종료되었습니다.");
	}
	
	/**
	 * 채팅 방 리스트
	 */
	public static String getChattingRoomList() {
		String result = null;
		String body = "{ \"kind\": \"roomList\", \"memberId\": \"iskwon\" }";
		try {
			connect();
			dos = new DataOutputStream(socket.getOutputStream());
			dos.write(body.getBytes("UTF-8"));
			dos.flush();
			
			dis = new DataInputStream(socket.getInputStream());
			
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				int bufferSize = 1024;
				byte[] buffer = new byte[bufferSize];
				int read;
				while (true) {
					read = dis.read(buffer, 0, bufferSize);
					baos.write(buffer, 0, read);
					if (read < bufferSize)
						break;
				}
				result = new String(baos.toByteArray(), "UTF-8");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			disconnect();
		}
		return result;
	}
	
	/**
	 * 채팅 방 입장
	 */
	public static void join(String roomId) {
		connect();
		
		if (socket != null) {
			String body = "{ \"kind\": \"join\", \"roomId\": \"" + roomId + "\", \"memberId\": \"iskwon2\" }";
			
			ChattingJoinThread thread = new ChattingJoinThread(socket, dis, dos, body);
			thread.start();
		}
	}
	
	/**
	 * 전송
	 */
	public static void send(String body) {
		try {
			dos = new DataOutputStream(socket.getOutputStream());
			dos.write(body.getBytes("UTF-8"));
			dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void connect() {
		try {
			socket = new Socket(host, port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void disconnect() {
		try {
			if (dis != null)
				dis.close();
			if (dos != null)
				dos.close();
			if (socket != null)
				socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

