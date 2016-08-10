package com.iskwon.test.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;

public class ChatServerHandler extends ChannelHandlerAdapter {

	private static final ChannelGroup clients = new DefaultChannelGroup(null);
	
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		System.out.println("=== handlerAdded ===");
		Channel client = ctx.channel();
		for (Channel channel : clients) {
			if (channel != client) {
				channel.write("[SERVER] - " + client.remoteAddress() + " has joined! \n");
				channel.flush();
			}
		}
		clients.add(client);
	}
	
	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		System.out.println("=== handlerRemoved ===");
		Channel incoming = ctx.channel();
		for (Channel channel : clients) {
			if (channel != incoming) {
				channel.write("[SERVER] - " + incoming.remoteAddress() + " has left! \n");
				channel.flush();
			}
		}
		clients.remove(incoming);
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		System.out.println("=== channelRead ===");
		System.out.println((String) msg);
		Channel incoming = ctx.channel();
		for (Channel channel : clients) {
			if (channel != incoming) {
				channel.write("[" + incoming.remoteAddress() + "] " + (String) msg + "\n");
				channel.flush();
			}
		}
//		ctx.close(); 채팅 서버기 때문에 메시지를 한번 읽고 채널을 닫지 않는다.
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
	
}
