package net.jueb.util4j.util;

import org.apache.commons.lang.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

public class Util {

	/**
	 * 取整数某二进制位的值
	 * @param number
	 * @param pos 0开始
	 * @return
	 */
	public static int getPosValue(int number,int pos)
	{
		return (number & (0x1<<pos))>>>pos;
	}

	public static boolean ipIsInRange(String ip, String cidr) {
		String[] ips = ip.split("\\.");
		int ipAddr = (Integer.parseInt(ips[0]) << 24)
				| (Integer.parseInt(ips[1]) << 16)
				| (Integer.parseInt(ips[2]) << 8)
				| Integer.parseInt(ips[3]);
		int type = Integer.parseInt(cidr.replaceAll(".*/", ""));
		int mask = 0xFFFFFFFF << (32 - type);
		String cidrIp = cidr.replaceAll("/.*", "");
		String[] cidrIps = cidrIp.split("\\.");
		int cidrIpAddr = (Integer.parseInt(cidrIps[0]) << 24)
				| (Integer.parseInt(cidrIps[1]) << 16)
				| (Integer.parseInt(cidrIps[2]) << 8)
				| Integer.parseInt(cidrIps[3]);

		return (ipAddr & mask) == (cidrIpAddr & mask);
	}

	public static boolean ipMatches(String address,String ipAddressOrCidr)throws Exception{
		int nMaskBits;
		if (ipAddressOrCidr.indexOf('/') > 0) {
			String[] addressAndMask = StringUtils.split(ipAddressOrCidr, "/");
			ipAddressOrCidr = addressAndMask[0];
			nMaskBits = Integer.parseInt(addressAndMask[1]);
		}
		else {
			nMaskBits = -1;
		}
		InetAddress requiredAddress = InetAddress.getByName(ipAddressOrCidr);
		InetAddress remoteAddress = InetAddress.getByName(address);
		if (!requiredAddress.getClass().equals(remoteAddress.getClass())) {
			return false;
		}
		if (nMaskBits < 0) {
			return remoteAddress.equals(requiredAddress);
		}
		byte[] remAddr = remoteAddress.getAddress();
		byte[] reqAddr = requiredAddress.getAddress();
		int oddBits = nMaskBits % 8;
		int nMaskBytes = nMaskBits / 8 + (oddBits == 0 ? 0 : 1);
		byte[] mask = new byte[nMaskBytes];
		Arrays.fill(mask, 0, oddBits == 0 ? mask.length : mask.length - 1, (byte) 0xFF);

		if (oddBits != 0) {
			int finalByte = (1 << oddBits) - 1;
			finalByte <<= 8 - oddBits;
			mask[mask.length - 1] = (byte) finalByte;
		}
		for (int i = 0; i < mask.length; i++) {
			if ((remAddr[i] & mask[i]) != (reqAddr[i] & mask[i])) {
				return false;
			}
		}
		return true;
	}

	public static void main(String[] args)throws Exception {
		System.out.println(ipIsInRange("192.168.1.1", "192.168.0.0/22"));
		System.out.println(ipIsInRange("192.168.1.1", "192.168.0.0/23"));
		System.out.println(ipIsInRange("192.168.1.1", "192.168.0.0/24"));
		System.out.println(ipIsInRange("192.168.1.1", "192.168.0.0/32"));
		System.out.println(ipMatches("192.168.1.1", "192.168.1.1"));
		System.out.println(ipMatches("192.168.1.1", "192.168.0.0/23"));
		System.out.println(ipMatches("192.168.1.1", "192.168.0.0/24"));
		System.out.println(ipMatches("192.168.1.1", "192.168.0.0/32"));
	}



}
