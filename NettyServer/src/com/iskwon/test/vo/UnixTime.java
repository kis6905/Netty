package com.iskwon.test.vo;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UnixTime {
	
	private final long value;
	
	public UnixTime() {
		this(System.currentTimeMillis() / 1000L + 2208988800L);
	}

	public UnixTime(long value) {
		this.value = value;
	}

	public long value() {
		return value;
	}

	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date((value() - 2208988800L) * 1000L));
	}

}
