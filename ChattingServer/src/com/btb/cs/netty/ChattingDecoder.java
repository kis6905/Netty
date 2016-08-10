package com.btb.cs.netty;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.btb.cs.ChattingServer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

public class ChattingDecoder extends ReplayingDecoder<Void> {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) {
		int readable = buf.readableBytes();
		if (readable <= 0)
			return;
		
		try {
			byte[] data = new byte[buf.writerIndex()];
			buf.readBytes(data);
			String str = new String(data, "UTF-8");
			out.add(str);
		} catch (UnsupportedEncodingException e) {
			ChattingServer.logger.error("decode() ~~ [An error occurred!]", e);
		}
	}
}
