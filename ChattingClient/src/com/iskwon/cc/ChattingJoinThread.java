package com.iskwon.cc;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ChattingJoinThread extends Thread {
	
	private Socket socket = null;
	private DataInputStream dis = null;
	private DataOutputStream dos = null;
	private String body = null;
	
	public ChattingJoinThread(Socket socket, DataInputStream dis, DataOutputStream dos, String body) {
		this.socket = socket;
		this.dis = dis;
		this.dos = dos;
		this.body = body;
	}
	
	@Override
	public void run() {
		String result = null;
		try {
			dos = new DataOutputStream(socket.getOutputStream());
			dos.write(body.getBytes("UTF-8"));
			dos.flush();
			
			// 채팅 방에 조인한 후엔 들어오는 데이터를 계속 받는다.
			while (true) {
				dis = new DataInputStream(socket.getInputStream());
				result = read();
				System.out.println("-> " + result); // TODO 이 부분을 파싱하여 채팅 화면에 뿌려줘야 함!!
				
				if (result == null)
					break;
				
				JSONObject jsonObj = (JSONObject) new JSONParser().parse(result);
				String type = (String) jsonObj.get("type");
				if (type.equals("destroy"))
					break;
			}
		} catch (SocketException e) {
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			disconnect();
		}
	}
	
	public String read() {
		String result = null;
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
		} catch(SocketException e) {
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public void disconnect() {
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
