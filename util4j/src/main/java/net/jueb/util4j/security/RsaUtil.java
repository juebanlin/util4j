package net.jueb.util4j.security;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

/**
 * 非对称加密算法工具类
 * 案例：
1.A要向B发送信息，A和B都要产生一对用于加密和解密的公钥和私钥。
2.A的私钥保密，A的公钥告诉B；B的私钥保密，B的公钥告诉A。
3.A要给B发送信息时，A用B的公钥加密信息，因为A知道B的公钥。
4.A将这个消息发给B（已经用B的公钥加密消息）。
5.B收到这个消息后，B用自己的私钥解密A的消息。其他所有收到这个报文的人都无法解密，因为只有B才有B的私钥。
注意:非对称加密速度慢,随着加密数据长度而增加时间,所以一般用于加密对称加密算法的密钥
 */
public class RsaUtil extends SecurityUtil{

	public final String algorithm="RSA";
    /** 
	 * 签名算法 
	 */  
	public final String SIGN_ALGORITHMS = "SHA1WithRSA";
	/** 
     * 使用默认系统随机源和确定密钥大小来生成密钥对。 
     * @return 
     * @throws NoSuchAlgorithmException 
     */  
    public KeyPair getRandomKeyPair(int keySize){  
        KeyPairGenerator pairgen;
		try {
			pairgen = KeyPairGenerator.getInstance(algorithm);
			SecureRandom random = new SecureRandom();  
	        pairgen.initialize(keySize, random);  
	        return pairgen.generateKeyPair();
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}  
        return null;  
    }
    
    /**
     * 获取一个新的密钥对,默认密钥长度1024
     * @return
     */
    public KeyPair getKeyPair()
    {
    	KeyPairGenerator keyPairGenerator;
		try {
			keyPairGenerator = KeyPairGenerator.getInstance(algorithm);
			return keyPairGenerator.generateKeyPair(); 
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}  
        return null; 
    }
    /**
     * 获取一个新的密钥对
     * @return
     */
    public KeyPair getKeyPair(int keysize)
    {
    	KeyPairGenerator keyPairGenerator;
		try {
			keyPairGenerator = KeyPairGenerator.getInstance(algorithm);
			keyPairGenerator.initialize(keysize);
			return keyPairGenerator.generateKeyPair(); 
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}  
        return null; 
    }
    
    /**
     * 使用公钥加密数据
     * @param key
     * @param data 待加密数据
     * @return
     */
    public byte[] encryptData(PublicKey key,byte[] data)
    {
    	try {
    		if(data==null || data.length<=0)
    		{
    			return null;
    		}
			Cipher cipher=Cipher.getInstance(algorithm);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			return cipher.doFinal(data);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
    	return null;
    }
    
    /**
	 * 使用私钥加密数据
	 * @param key 私钥
	 * @param data 待加密数据
	 * @return
	 */
	public byte[] encryptData(PrivateKey key,byte[] data)
	{
		try {
    		if(data==null || data.length<=0)
    		{
    			return null;
    		}
			Cipher cipher=Cipher.getInstance(algorithm);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			return cipher.doFinal(data);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
    	return null;
	}

	/**
	 * 使用公钥解密数据
	 * @param key 公钥
	 * @param data 被私钥加密过的数据
	 * @return
	 */
	public byte[] decryptData(PublicKey key,byte[] data)
	{
		try {
    		if(data==null || data.length<=0)
    		{
    			return null;
    		}
			Cipher cipher=Cipher.getInstance(algorithm);
			cipher.init(Cipher.DECRYPT_MODE, key);
			return cipher.doFinal(data);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
    	return null;
	}

	/**
	 * 使用私钥解密数据
	 * @param key  私钥
	 * @param data 被公钥加密的数据
	 * @return
	 */
	public byte[] decryptData(PrivateKey key,byte[] data)
	{
		try {
    		if(data==null || data.length<=0)
    		{
    			return null;
    		}
			Cipher cipher=Cipher.getInstance(algorithm);
			cipher.init(Cipher.DECRYPT_MODE, key);
			return cipher.doFinal(data);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
    	return null;
	}
	
	/**
	 * 用密钥包裹密钥
	 * @param key 
	 * @param wrapKey 
	 * @return
	 */
	public byte[] wrapKeyByKey(Key key,Key wrapKey)
	{
		try {
    		if(key==null ||wrapKey==null)
    		{
    			return null;
    		}
			Cipher cipher=Cipher.getInstance(algorithm);
			//使用私钥包裹模式
			cipher.init(Cipher.WRAP_MODE,wrapKey);
			return cipher.wrap(key);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
    	return null;
	}
	
	/**
	 * 使用密钥解包密钥
	 * @param key
	 * @param unwrapKey
	 * @return
	 */
	public PrivateKey unwrapPrivateKeyByKey(byte[] key,Key unwrapKey)
	{
		try {
    		if(key==null || key.length<=0 || unwrapKey==null)
    		{
    			return null;
    		}
			Cipher cipher=Cipher.getInstance(algorithm);
			//使用私钥包裹模式
			cipher.init(Cipher.UNWRAP_MODE,unwrapKey);
			return (PrivateKey) cipher.unwrap(key, algorithm,Cipher.PRIVATE_KEY);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
    	return null;
	}
	
	/**
	 * 使用密钥解包密钥
	 * @param key
	 * @param unwrapKey
	 * @return
	 */
	public PublicKey unwrapPublicKeyByKey(byte[] key,Key unwrapKey)
	{
		try {
    		if(key==null || key.length<=0 || unwrapKey==null)
    		{
    			return null;
    		}
			Cipher cipher=Cipher.getInstance(algorithm);
			//使用私钥包裹模式
			cipher.init(Cipher.UNWRAP_MODE,unwrapKey);
			return (PublicKey) cipher.unwrap(key, algorithm,Cipher.PUBLIC_KEY);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
    	return null;
	}
	
	
    /**
     * 根据公钥字符串获取公钥
     * Only RSAPublicKeySpec and X509EncodedKeySpec supported for RSA public keys
     * @param publicKey
     * @return
     */
    public PublicKey getPublicKey_X509Encoded(byte[] keyBytes){  
       try {
           X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);  
           KeyFactory keyFactory = KeyFactory.getInstance(algorithm);  
           return keyFactory.generatePublic(spec);  
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
       return null;
    }  
    
    /**
     * 根据私钥字符串获取私钥
     * Only RSAPrivate(Crt)KeySpec and PKCS8EncodedKeySpec supported for RSA private keys
     * @param privateKey
     * @return
     */
    public PrivateKey getPrivateKey_PKCS8Encoded(byte[] keyBytes){  
        try {
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);  
            KeyFactory keyFactory = KeyFactory.getInstance(algorithm);  
            return keyFactory.generatePrivate(spec); 
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		} 
    	return null;
    }
    
    /** 
    * RSA签名 
    * @param content 待签名数据 
    * @param privateKey 商户私钥 
    * @return 签名值 
    */  
    public byte[] sign(byte[] content,PrivateKey privateKey)  
    {  
        try   
        {  
            java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);  
            signature.initSign(privateKey);  
            signature.update(content);  
            return signature.sign();  
        }catch (Exception e)   
        {  
            log.error(e.getMessage(),e);
        }  
        return null;  
    }  

    /** 
    * RSA验签名检查 
    * @param content 待签名数据 
    * @param sign 签名值 
    * @param publicKey 分配给开发商公钥 
    * @return 布尔值 
    */  
    public boolean signVerify(byte[] content,byte[] sign,PublicKey pubKey)  
    {  
        try   
        {  
            java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);  
            signature.initVerify(pubKey);  
            signature.update(content);  
            return signature.verify(sign);  
        }catch (Exception e)   
        {  
            log.error(e.getMessage(),e);
        }  
        return false;  
    }  
}
