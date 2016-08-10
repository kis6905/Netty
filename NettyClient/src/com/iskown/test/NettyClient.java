package com.iskown.test;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import com.iskown.test.handler.NettyClientHandler;
import com.iskown.test.handler.UserDecoder;

public class NettyClient {

	public static void main(String[] args) throws Exception {
		String host = "127.0.0.1";
		int port = 8000;
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(workerGroup);
			bootstrap.channel(NioSocketChannel.class);
			bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
			bootstrap.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
//					ch.pipeline().addLast(new TimeDecoder(), new NettyClientHandler());
					ch.pipeline().addLast(new UserDecoder(), new NettyClientHandler());
				}
			});

			// Start the client.
			ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
			channelFuture.channel().closeFuture().sync();
			
//			ChannelFuture channelFuture = null;
//			for (int inx = 0; inx < 3; inx++) {
//				System.out.println("------------------------ " + inx);
//				channelFuture = bootstrap.connect(host, port).sync();
//				channelFuture.channel().closeFuture().sync();
//				Thread.sleep(2000);
//			}

			// Wait until the connection is closed.
//			channelFuture.channel().closeFuture().sync();
		} finally {
			System.out.println("=== ¿¬°á ³¡ ===");
			workerGroup.shutdownGracefully();
		}
	}

}
