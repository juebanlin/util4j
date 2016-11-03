package net.jueb.util4j.net.nettyImpl;

import io.netty.util.AttributeKey;
import net.jueb.util4j.net.JConnection;

public class ChannelKeys {

	public static AttributeKey<JConnection> Connection_Key=AttributeKey.newInstance("Connection_Key");
}
