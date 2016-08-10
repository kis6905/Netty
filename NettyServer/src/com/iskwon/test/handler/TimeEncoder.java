package com.iskwon.test.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import com.iskwon.test.vo.UnixTime;

public class TimeEncoder extends MessageToByteEncoder<UnixTime> {

	@Override
	protected void encode(ChannelHandlerContext ctx, UnixTime msg, ByteBuf out) throws Exception {
		System.out.println("=== encode ===");
		out.writeInt((int) msg.value());
	}
	
}
