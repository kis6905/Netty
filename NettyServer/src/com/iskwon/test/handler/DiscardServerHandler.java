package com.iskwon.test.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

import com.iskwon.test.vo.User;

public class DiscardServerHandler extends ChannelHandlerAdapter {
	
	@Override
	public void channelActive(final ChannelHandlerContext ctx) {
		System.out.println("=== channelActive ===");
		
		ChannelFuture channelFuture = ctx.writeAndFlush(new User("iskwon", 27));
	    channelFuture.addListener(ChannelFutureListener.CLOSE);
	    
//		final ByteBuf time = ctx.alloc().buffer(4);
//		
//		System.out.println((int) (System.currentTimeMillis() / 1000L + 2208988800L));
//		time.writeInt((int) (System.currentTimeMillis() / 1000L + 2208988800L));
//
//		final ChannelFuture channelFuture = ctx.writeAndFlush(time);
//		channelFuture.addListener(new ChannelFutureListener() {
//			@Override
//			public void operationComplete(ChannelFuture future) {
//				assert channelFuture == future;
//				ctx.close();
//			}
//		});
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
		} finally {
			ReferenceCountUtil.release(msg);
		}
		
		ctx.write(msg);
		ctx.flush();
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		// Close the connection when an exception is raised.
		cause.printStackTrace();
		ctx.close();
	}
	
}
