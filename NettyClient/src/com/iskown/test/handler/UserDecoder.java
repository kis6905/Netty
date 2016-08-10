package com.iskown.test.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

import com.iskwon.test.vo.User;

//public class UserDecoder extends ReplayingDecoder<Void> {
public class UserDecoder extends ReplayingDecoder<UserDecoder.DecoderState> {

	private int length;
	
	enum DecoderState {
		READ_LENGTH,
		READ_CONTENT;
	}
	
	public UserDecoder() {
		super(DecoderState.READ_LENGTH);
	}
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
		System.out.println("=== user decode ===");
		
		// 이렇게해도 되긴 된다... 어떤 방법이 맞는 것인지??
//		length = buf.readInt();
//		System.out.println("Length: " + length);
//		byte[] data = new byte[length];
//		buf.readBytes(data);
//		out.add(new User(data));
		
		switch (state()) {
		case READ_LENGTH:
			length = buf.readInt();
			checkpoint(DecoderState.READ_CONTENT);

		case READ_CONTENT:
			if (length >= 0) {
				byte[] data = new byte[length];
				buf.readBytes(data);
				
				checkpoint(DecoderState.READ_LENGTH);
				out.add(new User(data));
			}
			else {
				throw new Exception("Invalid packet length has been detected.");
			}
			break;
			
		default:
			throw new Exception("Invalid decoder state has been detected.");
		}
	}
	
}
