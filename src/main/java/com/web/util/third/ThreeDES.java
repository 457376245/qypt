package com.web.util.third;

import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.web.util.JsonUtil;

public class ThreeDES {

    private static final String Algorithm = "DESede"; // 定义 加密算法,可用

    // DES,DESede,Blowfish

    /**加密*/
    public static String encryptMode(String key,String content) {
        try {
        	byte[] keybyte=key.getBytes();
        	byte[] src=content.getBytes();
        	
            // 生成密钥
            SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);

            // 加密
            Cipher c1 = Cipher.getInstance(Algorithm);
            c1.init(Cipher.ENCRYPT_MODE, deskey);
            
            return byte2hex(c1.doFinal(src));
        } catch (java.security.NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (javax.crypto.NoSuchPaddingException e2) {
            e2.printStackTrace();
        } catch (java.lang.Exception e3) {
            e3.printStackTrace();
        }
        
        return null;
    }

    /**解密*/
    public static String decryptMode(String key, String content) {
        try {
        	byte[] keybyte=key.getBytes();
        	byte[] src=hex2byte(content);
            // 生成密钥
            SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);

            // 解密
            Cipher c1 = Cipher.getInstance(Algorithm);
            c1.init(Cipher.DECRYPT_MODE, deskey);
            return new String(c1.doFinal(src));
        } catch (java.security.NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (javax.crypto.NoSuchPaddingException e2) {
            e2.printStackTrace();
        } catch (java.lang.Exception e3) {
            e3.printStackTrace();
        }
        return null;
    }

    // 转换成十六进制字符串
    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";

        for (int n = 0; n < b.length; n++) {
            stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
            if (n < b.length - 1) {
                hs = hs;
            }
        }
        return hs.toUpperCase();
    }

    // 将16进制字符串转换成字节码
    public static byte[] hex2byte(String hex) {
        byte[] bts = new byte[hex.length() / 2];
        for (int i = 0; i < bts.length; i++) {
            bts[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2),
                    16);
        }
        return bts;
    }

    public static void main(String[] args) {
        // 添加新安全算法,如果用JCE就要把它添加进去
        String Key = "DXERBA5A3C85E86KK887KQKQ"; // 必须是8的倍数，如果不足在字符串后面以空格填补
        // 针对ocx控件密文解码，此值不可以变动。
        String szSrc = "ab12d中";
        Key.getBytes();
        
        Map<String, Object> infoMap=new HashMap<String, Object>();
		infoMap.put("TOKEN", "18900000003");
        
		String a=JsonUtil.toString(infoMap);
		
//        System.out.println("//-----------------------------------------");
//        System.out.println("//        Java标准加解密例子");
//        System.out.println("//-----------------------------------------");
//        System.out.println("加密前的字符串:" + a);
//        String encoded = encryptMode(Key, a);
//        System.out.println("加密后的字符串:" + encoded);
        
        System.out.println(new String(decryptMode(Key,"4AD34C1D608DD7332F20C116DF1AC8E4EEE44206421049031183B2D13339FBD73EF1DBC03493E33DE92770C68F450CCE5B2168EF0F26FFA9")));
    }
}