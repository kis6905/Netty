package com.btb.cs;


import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.btb.cs.netty.ChattingDecoder;
import com.btb.cs.netty.ChattingEncoder;
import com.btb.cs.netty.ChattingHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ChattingServer {

	// Global static members.
	public static final Logger logger = Logger.getLogger("ChattingServer"); // thread-safe
	
	private static int port = 8000; // default
	
	private static void setupLogger() {
		Layout layout = new PatternLayout("%-5p: [%d{yyyy-MM-dd HH:mm:ss.SSS}] %M() %m%n");
		
		ConsoleAppender consolAppender = new ConsoleAppender(layout, ConsoleAppender.SYSTEM_OUT);

		logger.setLevel(Config.getLogLevel());
		logger.setAdditivity(false); // appender 상속을 받지 않도록 한다.
		logger.addAppender(consolAppender);
		
		Logger loggerNetty = Logger.getLogger("io.netty"); // thread-safe

		loggerNetty.setLevel(Config.getLogLevel());
		loggerNetty.setAdditivity(false); // appender 상속을 받지 않도록 한다.
		loggerNetty.addAppender(consolAppender);
		
		try {
			DailyRollingFileAppender fileAppender = new DailyRollingFileAppender(); // 매일 새로운 로그파일로 교체하는 appender 이다.

			fileAppender.setName("FileAppender");
			fileAppender.setLayout(layout);
			fileAppender.setFile(Config.getLogFile()); // 파일 이름에 경로가 포함되면 생성자에 파일이름을 넘기면 오동작한다. 기본 생성자로 만들고 setFile()을 호출하는 방식으로 해야 한다.
			fileAppender.activateOptions(); // 파일 이름을 설정하면 해주어야 한다.
			fileAppender.setDatePattern("'.'yyyy-MM-dd");

			logger.addAppender(fileAppender);
			loggerNetty.addAppender(fileAppender);
		} catch (Exception e) {
			e.printStackTrace();

			// appender가 없으면 꺼버리자.
			if (!logger.getAllAppenders().hasMoreElements())
				logger.setLevel(Level.OFF);
		}
	}
	
	private static boolean setupServer() {
		try {
			port = Config.getPort();
			return true;
		} catch (Exception e) {
			logger.error("~~ [An error occurred!]", e);
			return false;
		}
	}
	
	public static void run() {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		
		try {
			ServerBootstrap bootstrap = new ServerBootstrap()
				.group(bossGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast(new ChattingDecoder(), new ChattingHandler());
						ch.pipeline().addLast(new ChattingEncoder(), new ChattingHandler());
					}
				})
				.option(ChannelOption.TCP_NODELAY, false)
				.childOption(ChannelOption.SO_KEEPALIVE, true);
			
			bootstrap.bind(port).sync().channel().closeFuture().sync();
		} catch (Exception e) {
		} finally {
//			bossGroup.shutdownGracefully();
//			workerGroup.shutdownGracefully();
		}
	}

	public static void main(String[] args) {
		if (!Config.load())
			return;

		setupLogger();
	
		logger.info("Setup...");
		if (!setupServer())
			return;
		logger.info("Done Setup!");
		
		logger.info("Start Chatting Server...");
		run();
		
		// 별도로 프로세스를 종료할 인터페이스는 없다.
		// 종료하려면 명령창에서 kill 할 것...
	}
}
