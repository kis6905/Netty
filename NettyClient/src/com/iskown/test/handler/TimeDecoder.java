package com.iskown.test.handler;

import java.util.List;

import com.iskwon.test.vo.UnixTime;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

public class TimeDecoder extends ReplayingDecoder<Void> {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		System.out.println("=== time decode ===");
		
		if (in.readableBytes() < 4)
	        return;
		
		out.add(new UnixTime(in.readUnsignedInt()));
	}
	
}
