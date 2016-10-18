package net.jueb.util4j.common.game.http;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class TrustAnyHostnameVerifier implements HostnameVerifier {
	
	public boolean verify(String hostname, SSLSession session) {
		return true;
	}
}