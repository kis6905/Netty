package com.btb.cs;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Utility {

	private static String encryptSha256(String password) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(password.getBytes());

		byte byteData[] = md.digest();

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
			sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
		}
		 
		 return sb.toString();
	 }

	 /**
	  * password 암호화( SHA256(SHA256(password)) )
	  * 
      * @throws NoSuchAlgorithmException 
	  */
	 public static String getEncryptedPassword(String input) throws NoSuchAlgorithmException {
		String password = "";
		
		password = encryptSha256(input);
		password = encryptSha256(password);
		
		return password;
	 }
	 
	 /**
	  * Date 형식의 문자열 변환
	  * 
	  * @param inDate
	  * @param inFormat
	  * @param outFormat
	  */
	 public static String convertDateFormat(String inDate, String inFormat, String outFormat) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(inFormat);
			Date date = sdf.parse(inDate);
			sdf = new SimpleDateFormat(outFormat);
			
			return sdf.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	 }
	 
	 /**
	  * 입력받은 날짜와 현재 날짜의 년-월-일 비교
	  * 
	  * @param inDate
	  * @param inFormat
	  * @return 입력받은 날짜 = 현재 날짜 -> 0,
	  * 		입력받은 날짜 < 현재 날짜 -> 음수,
	  * 		입력받은 날짜 > 현재 날짜 -> 양수
	  */
	public static int compareSysdate(String inDate, String inFormat) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(inFormat);
			Date date = sdf.parse(inDate);
			
			sdf = new SimpleDateFormat("yyyy-MM-dd");
			inDate = sdf.format(date);
			String sysDate = sdf.format(new Date());
			
			return inDate.compareTo(sysDate);
		} catch (ParseException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * 현재 시간 문자열로 리턴
	 * 
	 * @param format
	 * @return
	 */
	public static String getCurrentDateToString(String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(new Date());
	}
	
}
