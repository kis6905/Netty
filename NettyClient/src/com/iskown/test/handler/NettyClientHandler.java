package com.iskown.test.handler;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import com.iskwon.test.vo.User;

public class NettyClientHandler extends ChannelHandlerAdapter {
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		System.out.println("=== channelRead ===");
		
//		UnixTime m = (UnixTime) msg;
//		System.out.println(m);
		
		User user = (User) msg;
	    System.out.println(user.toString());
	    ctx.close();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}

}
