package com.iskown.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;

import com.iskown.test.handler.ChatClientHandler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class ChatClient {

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		System.out.print("닉네임을 입력하세요: ");
		String name = new Scanner(System.in).next();
		new ChatClient("10.1.3.16", 8000, name).run();
	}
	
	private final String host;
	private final int port;
	private final String name;
	
	public ChatClient(String host, int port, String name) {
		this.host = host;
		this.port = port;
		this.name = name;
	}

	public void run() {
		System.out.println("서버 붙니?");
		EventLoopGroup group = new NioEventLoopGroup();
		
		try {
			Bootstrap bootsrap = new Bootstrap()
				.group(group)
				.channel(NioSocketChannel.class)
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel sc) throws Exception {
						ChannelPipeline pipeline = sc.pipeline();
						
						pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
						pipeline.addLast(new StringDecoder(), new ChatClientHandler());
						pipeline.addLast(new StringEncoder(), new ChatClientHandler());
						
//						pipeline.addLast(new ChatClientHandler());
//						pipeline().addLast(new UserDecoder(), new NettyClientHandler());
					}
				});
			
			Channel channel = bootsrap.connect(host, port).sync().channel();
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			
			while (true) {
				channel.write(name + ": " + in.readLine() + "\r\n");
				channel.flush();
			}
			
		} catch (Exception e) {
			
		} finally {
			System.out.println("Shutdown client!!");
			group.shutdownGracefully();
		}
	}
}
