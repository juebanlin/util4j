package net.jueb.util4j.tools.security;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

/**
 * 非对称加密算法工具类
 * 案例：
1.A要向B发送信息，A和B都要产生一对用于加密和解密的公钥和私钥。
2.A的私钥保密，A的公钥告诉B；B的私钥保密，B的公钥告诉A。
3.A要给B发送信息时，A用B的公钥加密信息，因为A知道B的公钥。
4.A将这个消息发给B（已经用B的公钥加密消息）。
5.B收到这个消息后，B用自己的私钥解密A的消息。其他所有收到这个报文的人都无法解密，因为只有B才有B的私钥。
注意:非对称加密速度慢,随着加密数据长度而增加时间,所以一般用于加密对称加密算法的密钥
 * @author Administrator
 *
 */
public class RsaUtil {

	private String algorithm="RSA";
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
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
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
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
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
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
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
			e.printStackTrace();
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
			e.printStackTrace();
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
			e.printStackTrace();
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
			e.printStackTrace();
		}
    	return null;
	}
	
	/**
	 * 使用私钥加密密钥
	 * @param key
	 * @return
	 */
	public byte[] encryptKeyByPrivateKey(Key key,PrivateKey privateKey)
	{
		try {
    		if(key==null ||privateKey==null)
    		{
    			return null;
    		}
			Cipher cipher=Cipher.getInstance(algorithm);
			//使用私钥包裹模式
			cipher.init(Cipher.WRAP_MODE,privateKey);
			return cipher.wrap(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return null;
	}
	/**
	 * 使用公钥加密密钥
	 * @param key
	 * @return
	 */
	public byte[] encryptKeyByPublicKey(Key key,PublicKey publicKey)
	{
		try {
    		if(key==null ||publicKey==null)
    		{
    			return null;
    		}
			Cipher cipher=Cipher.getInstance(algorithm);
			//使用私钥包裹模式
			cipher.init(Cipher.WRAP_MODE,publicKey);
			return cipher.wrap(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return null;
	}
	
	/**
	 * 使用公钥解密出私钥密钥密钥
	 * @param key 被公钥加密后的私钥数据
	 * @param publicKey 公钥
	 * @return
	 */
	public PrivateKey decryptPrivateKey(byte[] key,PublicKey publicKey)
	{
		try {
    		if(key==null || key.length<=0 || publicKey==null)
    		{
    			return null;
    		}
			Cipher cipher=Cipher.getInstance(algorithm);
			//使用私钥包裹模式
			cipher.init(Cipher.UNWRAP_MODE,publicKey);
			return (PrivateKey) cipher.unwrap(key, algorithm,Cipher.PRIVATE_KEY);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return null;
	}
	/**
	 * 使用私钥解密出加密后的公钥
	 * @param key 被私钥加密过的数据
	 * @param privateKey 私钥
	 * @return
	 */
	public PublicKey decryptPublicKey(byte[] key,PrivateKey privateKey)
	{
		try {
    		if(key==null || key.length<=0 || privateKey==null)
    		{
    			return null;
    		}
			Cipher cipher=Cipher.getInstance(algorithm);
			//使用私钥包裹模式
			cipher.init(Cipher.UNWRAP_MODE,privateKey);
			return (PublicKey) cipher.unwrap(key, algorithm,Cipher.PUBLIC_KEY);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return null;
	}
	
    /**
     * 保存公钥字符串到文件
     * @param publicKey
     * @param keyFile
     * @return
     */
    public File savePublicKeyStrFile(PublicKey publicKey,File keyFile)
    {     
    	if(publicKey==null)
    	{
    		return null;
    	}
    	try {
    		String str=getPublicKeyStr(publicKey);
        	FileOutputStream fos=new FileOutputStream(keyFile);
        	fos.write(str.getBytes());
        	fos.flush();fos.close();
        	return keyFile;
		} catch (Exception e) {
			return null;
		}
    }  
    /**
     * 保存私钥字符串到文件
     * @param privatekey
     * @param keyFile
     * @return
     */
    public File savePrivateKeyStrFile(PrivateKey privatekey,File keyFile)
    {     
    	if(privatekey==null)
    	{
    		return null;
    	}
    	try {
    		String str=getPrivateKeyStr(privatekey);
        	FileOutputStream fos=new FileOutputStream(keyFile);
        	fos.write(str.getBytes());
        	fos.flush();fos.close();
        	return keyFile;
		} catch (Exception e) {
			return null;
		}
    }   
    
    /**
     * 从一个公钥文本文件获取一个公钥对象
     * @param publickeyFile
     * @return
     */
    public PublicKey readPublicKeyByStrFile(File publickeyFile)   
    {   
    	byte[] keyBytes=readFileBytes(publickeyFile);
    	if(keyBytes==null)
    	{
    		return null;
    	}
    	return getPublicKey(new String(keyBytes));
    } 
    /**
     * 从一个私钥文本文件获取一个私钥对象
     * @param privatekeyFile
     * @return
     */
    public PrivateKey readPrivateKeyByStrFile(File privatekeyFile)   
    {   
    	byte[] keyBytes=readFileBytes(privatekeyFile);
    	if(keyBytes==null)
    	{
    		return null;
    	}
        return getPrivateKey(new String(keyBytes));
    } 
    
    /**
     * 获取公钥的base64字符串形式
     * @param publicKey
     * @return
     */
    public String getPublicKeyStr(PublicKey publicKey)
    {
    	if(publicKey==null)
    	{
    		return null;
    	}
    	return Base64.encode(publicKey.getEncoded());
    }
    
    /**
     * 获取私钥的base64字符串形式
     * @param privateKey
     * @return
     */
    public String getPrivateKeyStr(PrivateKey privateKey)
    {
    	if(privateKey==null)
    	{
    		return null;
    	}
    	return Base64.encode(privateKey.getEncoded());
    }
    
    /**
     * 根据公钥字符串获取公钥
     * @param publicKey
     * @return
     */
    public PublicKey getPublicKey(String publicKey){  
       try {
    	   byte[] keyBytes = Base64.decode(publicKey);  
           X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);  
           KeyFactory keyFactory = KeyFactory.getInstance(algorithm);  
           return keyFactory.generatePublic(spec);  
		} catch (Exception e) {
			e.printStackTrace();return null;
		}
    }  
    /**
     * 根据私钥字符串获取私钥
     * @param privateKey
     * @return
     */
    public PrivateKey getPrivateKey(String privateKey){  
        try {
        	byte[] keyBytes = Base64.decode(privateKey);  
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);  
            KeyFactory keyFactory = KeyFactory.getInstance(algorithm);  
            return keyFactory.generatePrivate(spec); 
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
    }
    /**  
	 * 序列化密钥对象到文件
	 * @param key  
	 * @param keyName  
	 */  
	public  File saveKeyObject(Key key,File keyfile){  
	    try {
	    	FileOutputStream foskey=new FileOutputStream(keyfile);  
	        ObjectOutputStream oos=new ObjectOutputStream(foskey);  
	        oos.writeObject(key);  
	        oos.close();  
	        foskey.close();  
		} catch (Exception e) {
			return null;
		}
	    return keyfile;
	}

	/**  
     * 反序列化读取公钥对象
     * @param keyName  
     * @return Key  
     * @throws Exception  
     */  
    public PublicKey readPublicKeyObject(File publickeyFile) throws Exception{  
        FileInputStream fiskey=new FileInputStream(publickeyFile);  
        ObjectInputStream oiskey=new ObjectInputStream(fiskey);  
        PublicKey key=(PublicKey)oiskey.readObject();  
        oiskey.close();  
        fiskey.close();  
        return key;  
    }
    /**  
     * 反序列化读取私钥对象
     * @param keyName  
     * @return Key  
     * @throws Exception  
     */  
    public PrivateKey readPrivateKeyObject(File privatekeyFile) throws Exception{  
        FileInputStream fiskey=new FileInputStream(privatekeyFile);  
        ObjectInputStream oiskey=new ObjectInputStream(fiskey);  
        PrivateKey key=(PrivateKey)oiskey.readObject();  
        oiskey.close();  
        fiskey.close();  
        return key;  
    }   
	/**
	 * 根据文件拿到此文件的byte数组
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public byte[] readFileBytes(File file)
	{
		try {
			FileInputStream fis=new FileInputStream(file);
			BufferedInputStream bis=new BufferedInputStream(fis);
			ByteArrayOutputStream data=new ByteArrayOutputStream();//定义一个内存输出流
			int i=-1;
			while((i=bis.read())!=-1)//如果没读取完，在继续
			{
				data.write(i);//保存到内存数组
			}
			data.flush();
			data.close();
			bis.close();
			return data.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 根据字节数组保存到文件
	 * @param data
	 * @param file
	 * @throws IOException
	 */
	public File saveBytesToFile(byte[] data,File file)
	{
		try {
			FileOutputStream fos=new FileOutputStream(file);//定义一个输出到文件的流
			BufferedOutputStream bos=new BufferedOutputStream(fos);//包装该流
			bos.write(data);
			bos.flush();//写出数据
			bos.close();
			return file;
		} catch (Exception e) {
			return null;
		}
	}
}
