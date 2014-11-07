package net.jueb.tools.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import javax.crypto.Cipher;

/**
 * 非对称加密算法工具类
 * 案例：
A要发送一个消息给B
1.A生成一个对称秘钥：PublicKeyA和privateKeyA
2.A用B的公钥PublicKeyB加密第一步生成的这个对称秘钥并把自己的密钥发送给B
3.A用第一步生成的这个对称秘钥加密实际要发的消息并发送给B
4.B收到A发来的对称秘钥,这个秘钥是用B的公钥加密过的,所以B需要用自己的私钥来解密这个秘钥
然后B又收到A发来的密文,这时候用刚才解密出来的秘钥来解密密文这样子的整个过程既保证了安全,又保证了效率.
 * @author Administrator
 *
 */
public class RsaUtil {

    /** 
     * 使用默认系统随机源和确定密钥大小来生成密钥对。 
     * @return 
     * @throws NoSuchAlgorithmException 
     */  
    public KeyPair generateRandomKeyPair(int keySize){  
        KeyPairGenerator pairgen;
		try {
			pairgen = KeyPairGenerator.getInstance("RSA");
			SecureRandom random = new SecureRandom();  
	        pairgen.initialize(keySize, random);  
	        return pairgen.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}  
        return null;  
    }
    
    /**
     * 获取一个新的密钥对
     * @return
     */
    public KeyPair generateKeyPair()
    {
    	KeyPairGenerator keyPairGenerator;
		try {
			keyPairGenerator = KeyPairGenerator.getInstance("RSA");
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
			Cipher cipher=Cipher.getInstance("RSA");
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
			Cipher cipher=Cipher.getInstance("RSA");
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
			Cipher cipher=Cipher.getInstance("RSA");
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
			Cipher cipher=Cipher.getInstance("RSA");
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
			Cipher cipher=Cipher.getInstance("RSA");
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
			Cipher cipher=Cipher.getInstance("RSA");
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
			Cipher cipher=Cipher.getInstance("RSA");
			//使用私钥包裹模式
			cipher.init(Cipher.UNWRAP_MODE,publicKey);
			return (PrivateKey) cipher.unwrap(key, "RSA",Cipher.PRIVATE_KEY);
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
			Cipher cipher=Cipher.getInstance("RSA");
			//使用私钥包裹模式
			cipher.init(Cipher.UNWRAP_MODE,privateKey);
			return (PublicKey) cipher.unwrap(key, "RSA",Cipher.PUBLIC_KEY);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return null;
	}
	
    /**  
     * 保存密钥的方法  
     * @param key  
     * @param keyName  
     * @throws Exception  
     */  
    public void saveKey(Key key,File keyfile) throws Exception{  
        FileOutputStream foskey=new FileOutputStream(keyfile);  
        ObjectOutputStream oos=new ObjectOutputStream(foskey);  
        oos.writeObject(key);  
        oos.close();  
        foskey.close();  
    }  
    /**  
     * 读取密钥的方法  
     * @param keyName  
     * @return Key  
     * @throws Exception  
     */  
    public Key readKey(File keyfile) throws Exception{  
        FileInputStream fiskey=new FileInputStream(keyfile);  
        ObjectInputStream oiskey=new ObjectInputStream(fiskey);  
        Key key=(Key)oiskey.readObject();  
        oiskey.close();  
        fiskey.close();  
        return key;  
    }
    
    
}
