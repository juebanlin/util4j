package net.jueb.util4j.util;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.Enumeration;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;

/**
证书格式介绍
PKCS 全称是 Public-Key Cryptography Standards ，是由 RSA 实验室与其它安全系统开发商为促进公钥密码的发展而制订的一系列标准，PKCS 目前共发布过 15 个标准。 常用的有：
    PKCS#7 Cryptographic Message Syntax Standard
    PKCS#10 Certification Request Standard
    PKCS#12 Personal Information Exchange Syntax Standard
X.509是常见通用的证书格式。所有的证书都符合为Public Key Infrastructure (PKI) 制定的 ITU-T X509 国际标准。
    PKCS#7常用的后缀是： .P7B .P7C .SPC
    PKCS#12常用的后缀有： .P12 .PFX
    X.509 DER编码(ASCII)的后缀是： .DER .CER .CRT
    X.509 PAM编码(Base64)的后缀是： .PEM .CER .CRT
    .cer/.crt是用于存放证书，它是2进制形式存放的，不含私钥。
    .pem跟crt/cer的区别是它以Ascii来表示。
    pfx/p12用于存放个人证书/私钥，他通常包含保护密码，2进制方式
    p10是证书请求
    p7r是CA对证书请求的回复，只用于导入
    p7b以树状展示证书链(certificate chain)，同时也支持单个证书，不含私钥
 * @author jaci
 */
public class NettyServerSslUtil {

	public static KeyManagerFactory getKeyKeyManagerFactoryByPfx(InputStream keyStore,String password) throws Exception
	{
		String algorithm="sunx509";
		String keyStoreType="PKCS12";
		return getKeyKeyManagerFactory(keyStore,password,algorithm, keyStoreType);
	}
	
	public static KeyManagerFactory getKeyKeyManagerFactory(InputStream keyStore,String password,String algorithm,String keyStoreType) throws Exception
	{
		KeyStore ks=KeyStore.getInstance(keyStoreType);
		ks.load(keyStore, password.toCharArray());
		keyStore.close();
		KeyManagerFactory k=KeyManagerFactory.getInstance(algorithm);
		k.init(ks, password.toCharArray());
		return k;
	}
	
	public static SslContext buildSslContext(InputStream keyStore,String password,String keyStoreType,String algorithm) throws Exception {
		KeyStore ks=KeyStore.getInstance(keyStoreType);
		ks.load(keyStore, password.toCharArray());
		KeyManagerFactory keyManagerFactory=KeyManagerFactory.getInstance(algorithm);
		keyManagerFactory.init(ks, password.toCharArray());
		keyStore.close();
		return SslContextBuilder.forServer(keyManagerFactory).clientAuth(ClientAuth.NONE).build();
	}
	
	public static SslContext buildSslContext_P12_Pfx(InputStream keyStore,String password) throws Exception {
		String algorithm="sunx509";
		String keyStoreType="PKCS12";
		return buildSslContext(keyStore, password, keyStoreType, algorithm);
	}
	
	public  static void printPfxInfo(InputStream pfx, String strPassword){  
        try {  
        	String keyStoreType="PKCS12";
            KeyStore ks = KeyStore.getInstance(keyStoreType);  
            char[] nPassword = null;  
            if ((strPassword == null) || strPassword.trim().equals("")){  
                nPassword = null;  
            }else  
            {  
                nPassword = strPassword.toCharArray();  
            }  
            ks.load(pfx, nPassword);  
            pfx.close();  
            Enumeration<String> enumas = ks.aliases();  
            String keyAlias = null;  
            if (enumas.hasMoreElements())
            {  
                keyAlias = (String)enumas.nextElement();   
                System.out.println("alias=[" + keyAlias + "]");  
            }  
            System.out.println("is key entry=" + ks.isKeyEntry(keyAlias));  
            PrivateKey pkey = (PrivateKey) ks.getKey(keyAlias, nPassword);  
            Certificate cert = ks.getCertificate(keyAlias);  
            PublicKey pubkey = cert.getPublicKey();  
            System.out.println("cert class = " + cert.getClass().getName());  
            System.out.println("cert = " + cert);  
            System.out.println("public key = " + pubkey);  
            System.out.println("private key = " + pkey);  
        }  
        catch (Exception e)  
        {  
            e.printStackTrace();  
        }  
    }
	
	/**
	 * 
	 * @param keyStoreType PKCS12 or JKS
	 * @param keyStore
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public static SSLContext createSSLContext(String keyStoreType ,InputStream keyStore ,String password) throws Exception {
		String pootocol="TLS";
		KeyStore ks = KeyStore.getInstance(keyStoreType);
		ks.load(keyStore, password.toCharArray());
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmf.init(ks, password.toCharArray());
		SSLContext sslContext=SSLContext.getInstance(pootocol);
		sslContext.init(kmf.getKeyManagers(), null, null);
		return sslContext;
	}
	
	public static SslHandler BuildServerSslHandler(SSLContext sslContext)
	{
		SSLEngine sslEngine = sslContext.createSSLEngine();
		sslEngine.setUseClientMode(false); //服务器端模式
		sslEngine.setNeedClientAuth(false); //不需要验证客户端
		return new SslHandler(sslEngine);
	}
	
	public static SslHandler BuildClientSslHandler(SSLContext sslContext,String host,int port)
	{
		SSLEngine sslEngine = sslContext.createSSLEngine(host,port);  
	    sslEngine.setUseClientMode(true);  
		return new SslHandler(sslEngine);
	}
}
