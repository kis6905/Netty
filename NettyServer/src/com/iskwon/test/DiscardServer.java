package com.iskwon.test;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import com.iskwon.test.handler.DiscardServerHandler;
import com.iskwon.test.handler.UserEncoder;

public class DiscardServer {
	
	private int port;
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	
	public DiscardServer(int port) {
		this.port = port;
	}
	
	public void run() throws Exception {
		System.out.println("Start Server");
		bossGroup = new NioEventLoopGroup();
		workerGroup = new NioEventLoopGroup();
		
		try {
			ServerBootstrap bootstrap = new ServerBootstrap(); // 서버 설정
			bootstrap.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch) throws Exception {
//						ch.pipeline().addLast(new TimeEncoder(), new DiscardServerHandler());
						ch.pipeline().addLast(new UserEncoder(), new DiscardServerHandler());
					}
				})
				.option(ChannelOption.SO_BACKLOG, 128)
				.childOption(ChannelOption.SO_KEEPALIVE, true);
			
			// Bind and start to accept incoming connections.
			ChannelFuture f = bootstrap.bind(port).sync(); // (7)

			// Wait until the server socket is closed.
			// In this example, this does not happen, but you can do that to gracefully
			// shut down your server.
			f.channel().closeFuture().sync();
		} finally {
//			System.out.println("서버 종료");
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
	
	public static void main(String[] args) throws Exception {
		int port = 8000;
		DiscardServer server = new DiscardServer(port);
		server.run();
	}
	
}
