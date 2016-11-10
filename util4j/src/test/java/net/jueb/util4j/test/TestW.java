package net.jueb.util4j.test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Scanner;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.logging.LogLevel;
import net.jueb.util4j.buffer.ArrayBytesBuff;
import net.jueb.util4j.buffer.BytesBuff;
import net.jueb.util4j.net.nettyImpl.handler.LoggerHandler;
import net.jueb.util4j.net.nettyImpl.server.NettyServer;

public class TestW {
	
	public static void main(String[] args) throws URISyntaxException, IOException {
		NettyServer ns=new NettyServer("0.0.0.0",1234,new ChannelInitializer<Channel>() {

			@Override
			protected void initChannel(Channel ch) throws Exception {
				ch.pipeline().addLast(new LoggerHandler(LogLevel.INFO));
				ch.pipeline().addLast(new ByteToMessageCodec<ByteBuf>() {

					@Override
					protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
						
 					}

					@Override
					protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
						int size=in.readInt();
						short code=in.readShort();
						int len=size-2;
						byte[] data=new byte[len];
						in.readBytes(data);
						BytesBuff buff=new ArrayBytesBuff(data);
						byte type=buff.readByte();
						String token=null;
						if(buff.readByte()!=0)
						{
							int slen=buff.readInt();
							token=new String(buff.readBytes(slen).getBytes());
						}
						System.out.println("code="+code+",type="+type+",token="+token);
					}
				});
			}
		});
		ns.start();
		new Scanner(System.in).nextLine();
	}
}
