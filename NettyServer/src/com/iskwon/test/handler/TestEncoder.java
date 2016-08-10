package com.iskwon.test.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class TestEncoder extends MessageToByteEncoder<String> {

	@Override
	protected void encode(ChannelHandlerContext ctx, String msg, ByteBuf out) throws Exception {
		System.out.println("=== encode ===");
		msg = "echo:" + msg;
		System.out.println(msg);
		
		out.writeBytes(msg.getBytes("UTF-8"));
	}
	
}
