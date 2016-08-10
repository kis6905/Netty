package com.iskwon.test;


import com.iskwon.test.handler.ChatServerHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class ChatServer {

	public static void main(String[] args) {
		new ChatServer(8000).run();
	}
	
	private int port;
	
	public ChatServer(int port) {
		this.port = port;
	}
	
	public void run() {
		System.out.println("Start Server!!");
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		
		try {
			ServerBootstrap bootstrap = new ServerBootstrap()
				.group(bossGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
						ch.pipeline().addLast("decoder", new StringDecoder());
						ch.pipeline().addLast("encoder", new StringEncoder());
						
						ch.pipeline().addLast("handler", new ChatServerHandler());
					}
				})
				.childOption(ChannelOption.SO_KEEPALIVE, true);
			
			
			bootstrap.bind(port).sync().channel().closeFuture().sync();
			
		} catch (Exception e) {
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	
}
