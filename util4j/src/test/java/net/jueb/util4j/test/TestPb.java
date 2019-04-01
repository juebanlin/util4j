package net.jueb.util4j.test;

import net.jueb.util4j.bytesStream.bytes.HexUtil;
import net.jueb.util4j.msg.ProtobufTest.CreateRoleRequest;

public class TestPb {

	public static void main(String[] args) {
		byte[] data=CreateRoleRequest.newBuilder()
		.setBallerCode(1)
		.setInviteCode("1122")
		.setName("3344").build().toByteArray();
		System.out.println(HexUtil.prettyHexDump(data));
	}
}