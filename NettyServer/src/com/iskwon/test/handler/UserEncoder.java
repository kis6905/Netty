package com.iskwon.test.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import com.iskwon.test.vo.User;

public class UserEncoder extends MessageToByteEncoder<User> {

	@Override
	protected void encode(ChannelHandlerContext ctx, User user, ByteBuf out) throws Exception {
		System.out.println("=== encode ===");
		System.out.println(user.toString());
		
		user.writeToBuffer(out);
	}
	
}
