package net.jueb.util4j.util;

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

	public static boolean isInRange(String ip, String cidr) {
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

	public static void main(String[] args) {
		System.out.println(isInRange("192.168.1.1", "192.168.0.0/22"));
		System.out.println(isInRange("192.168.1.1", "192.168.0.0/23"));
		System.out.println(isInRange("192.168.1.1", "192.168.0.0/24"));
		System.out.println(isInRange("192.168.1.1", "192.168.0.0/32"));
	}
}
