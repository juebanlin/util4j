package net.jueb.util4j.beta.tools.security;

import java.io.File;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
public class Test
{
    RsaUtil rsa=new RsaUtil();
	KeyPair kp=rsa.getKeyPair();
	PublicKey puk=kp.getPublic();
	PrivateKey prk=kp.getPrivate();
    	
	/**
	 * 加密解密测试
	 */
	public void testEnDe(String str)
	{
    	byte[] data=str.getBytes();
    	System.out.println(System.currentTimeMillis()+":测试数据:"+str);
    	byte[] enData=rsa.encryptData(puk,data);//加密数据
    	String enStr=new String(enData);
    	System.out.println(System.currentTimeMillis()+":公钥加密后数据字符串："+enStr);
    	byte[] deData=rsa.decryptData(prk, enData);//解密数据
    	String deStr=new String(deData);
    	System.out.println(System.currentTimeMillis()+":私钥解密后数据字符串："+deStr);
	}
	/**
	 * key字符串转换测试
	 */
	public void testToStr()
	{
		
		String pukStr=rsa.getPublicKeyStr(puk);
		String prkStr=rsa.getPrivateKeyStr(prk);
		System.out.println("转换为base64公钥字符串:"+pukStr);
		System.out.println("转换为base64私钥字符串:"+prkStr);
		puk=rsa.getPublicKey(pukStr);
		prk=rsa.getPrivateKey(prkStr);
		System.out.println("已根据字符串重新生成key对象,再次展示字符串");
		pukStr=rsa.getPublicKeyStr(puk);
		prkStr=rsa.getPrivateKeyStr(prk);
		System.out.println("转换为base64公钥字符串:"+pukStr);
		System.out.println("转换为base64私钥字符串:"+prkStr);
	}
	
	/**
	 * 测试文件保存和加载
	 */
	public void testFileLoad()
	{
		File f1=new File("D:/publicKey.rsa");
		File f2=new File("D:/privateKey.rsa");
		rsa.savePublicKeyStrFile(puk, f1);
		rsa.savePrivateKeyStrFile(prk, f2);
		System.out.println("保存key到文件完成");
		puk=rsa.readPublicKeyByStrFile(f1);
		prk=rsa.readPrivateKeyByStrFile(f2);
		System.out.println("加载文件key完成");
		System.out.println("重新测试加密解密");
		testEnDe("文件测试后的加密解密测试");
	}
	
	public static void main(String[] args) 
	{
    	Test t=new Test();
    	t.testEnDe("字符串加密解密测试");
    	t.testToStr();
    	t.testFileLoad();
    }
}