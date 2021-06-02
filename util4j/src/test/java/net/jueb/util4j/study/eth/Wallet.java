package net.jueb.util4j.study.eth;

import com.google.common.collect.ImmutableList;
import org.web3j.crypto.*;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * @Classname Wallet
 * @Description TODO
 * @Date 2021/3/29 5:28 下午
 * @Created by helin
 */
public class Wallet {

    /**
     * 创建钱包
     */
    private static void createWallet() throws  CipherException, IOException {
        String pwd="123456";
        Bip39Wallet bip39Wallet = Bip44WalletUtils.generateBip44Wallet(pwd, new File("."));
        String filename = bip39Wallet.getFilename();
        //助记词
        String mnemonic=bip39Wallet.getMnemonic();

        Credentials credentials = Bip44WalletUtils.loadCredentials(pwd,new File(filename));

        //钱包地址
        String address = credentials.getAddress();
        ECKeyPair ecKeyPair = credentials.getEcKeyPair();
        String publicKey = ecKeyPair.getPublicKey().toString(16);
        String privateKey = ecKeyPair.getPrivateKey().toString(16);

        System.out.println();
        System.out.println("filename：");
        System.out.println(filename);
        System.out.println("助记词：");
        System.out.println(mnemonic);
        System.out.println();
        System.out.println("地址：");
        System.out.println(address);
        System.out.println();
        System.out.println("私钥：");
        System.out.println("0x"+privateKey);
        System.out.println();
        System.out.println("公钥：");
        System.out.println("0x"+publicKey);

        Credentials credentials2 = Bip44WalletUtils.loadBip44Credentials(pwd, mnemonic);
        Credentials credentials3 = Bip44WalletUtils.loadBip44Credentials(pwd, mnemonic);
        System.out.println(credentials2.getAddress()+"");
        System.out.println(credentials3.getAddress()+"");
    }

    public static void main(String[] args) throws CipherException, IOException {
        createWallet();
    }
}
