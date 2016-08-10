package com.iskwon.test.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

import java.io.UnsupportedEncodingException;

public class TestServerHandler extends ChannelHandlerAdapter {
	
	@Override
	public void channelActive(final ChannelHandlerContext ctx) throws UnsupportedEncodingException {
		System.out.println("=== channelActive ===");
		
		ChannelFuture channelFuture = ctx.writeAndFlush("¾È³ç");
	    channelFuture.addListener(ChannelFutureListener.CLOSE);
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		System.out.println("=== channelRead ===");
		
		ByteBuf in = (ByteBuf) msg;
		try {
			while (in.isReadable()) {
				System.out.print((char) in.readByte());
				System.out.flush();
			}
			System.out.println();
		} finally {
			ReferenceCountUtil.release(msg);
		}
		
		ctx.writeAndFlush("hi");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		System.out.println("¿¡·¯");
		// Close the connection when an exception is raised.
		cause.printStackTrace();
		ctx.close();
	}
	
}
