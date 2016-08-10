package com.iskwon.cc;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ChattingRoomMasterClient {
	
	private static Socket socket = null;
	private static DataInputStream dis = null;
	private static DataOutputStream dos = null;
	
	private static String host = null;
	private static int port;
	
	public static void main(String[] args) {
//		host = "110.45.190.170";
		host = "10.1.2.193";
		port = 8000;
		run();
	}
	
	@SuppressWarnings("resource")
	public static void run() {
		try {
			// 채팅 방 생성
			String resJsonStr = create();
			
			// 채팅 방 생성 시 응답으로 받은 json 문자열을 파싱해 roomId를 추출한다.
			JSONObject jsonObj = (JSONObject) new JSONParser().parse(resJsonStr);
			String roomId = (String) jsonObj.get("roomId");
			System.out.println("roomId: " + roomId);
			
			// 추출한 roomId로 채팅 방 조인
			if (roomId != null && !roomId.isEmpty()) {
				join(roomId);
				
				// 채팅 입력
				System.out.println("===== 0 입력 시 종료 =====");
				while (true) {
					String message = new Scanner(System.in).nextLine();
					
					if (message.equals("0"))
						break;
					
					// 입력한 메시지 전송
					send(message);
				}
				
				disconnect();
				System.out.println("종료되었습니다.");
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 채팅 방 생성
	 */
	public static String create() {
		String result = null;
		String body = "{ \"kind\": \"create\", \"roomName\": \"111111ggggggg\", \"memberId\": \"iskwon\" }";
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
			String body = "{ \"kind\": \"join\", \"roomId\": \"" + roomId + "\", \"memberId\": \"iskwon\" }";
			
			ChattingJoinThread thread = new ChattingJoinThread(socket, dis, dos, body);
			thread.start();
		}
	}
	
	/**
	 * 전송
	 */
	public static void send(String message) {
		String body = "{ \"kind\": \"chat\", \"memberId\": \"iskwon\", \"message\": \"" + message + "\" }";
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
