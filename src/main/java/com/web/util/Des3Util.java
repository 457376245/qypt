package com.web.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;




/**
 * android java互通加密方案
 * 
 * @date 2013-06-25
 */
public class Des3Util {
	// 密钥外传，不固定到类中,方便修改
	// 入网协议地址址(测试环境地址);
	public final static int PRINT_HTTP_OK = 98;
	public final static int PRINT_HTTP_FAIL = 99;
	
	private final static String secretKey = "2013%Linkage&Asiainfo123";

	/**
	 * 加密入口
	 * 
	 * @param src
	 *            加密原文
	 * @param key
	 *            密钥
	 * @return
	 * @throws Exception
	 */
	public static String encryptThreeDESECB(String src, String key)
			throws Exception {
		DESedeKeySpec dks = new DESedeKeySpec(key.getBytes("UTF-8"));
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("desede");
		SecretKey securekey = keyFactory.generateSecret(dks);

		Cipher cipher = Cipher.getInstance("desede/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, securekey);
		byte[] b = cipher.doFinal(src.getBytes());
		String mi = Base64.encodeToString(b, Base64.DEFAULT)
				.replaceAll("\r", "").replaceAll("\n", "");
		mi = mi.replace("=", "dddeeefff");
		mi = mi.replace("+", "aaabbbccc");
		return mi;

	}

	/**
	 * 解密入口
	 * 
	 * @param encryptText
	 *            解密文本
	 * @param secretKey
	 *            密钥
	 * @param iv
	 *            密钥
	 * @return
	 * @throws Exception
	 */
	public static String decryptThreeDESECB(String src, String key)
			throws Exception {
		src = src.replace("dddeeefff", "=");
		src = src.replace("aaabbbccc", "+");
		// --通过base64,将字符串转成byte数组
		// BASE64Decoder decoder = new BASE64Decoder();
		byte[] bytesrc = Base64.decode(src, Base64.DEFAULT);
		// --解密的key
		DESedeKeySpec dks = new DESedeKeySpec(key.getBytes("UTF-8"));
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("desede");
		SecretKey securekey = keyFactory.generateSecret(dks);

		// --Chipher对象解密
		Cipher cipher = Cipher.getInstance("desede/ECB/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, securekey);
		byte[] retByte = cipher.doFinal(bytesrc);
		// 加入编码解决中文问题
		return new String(retByte, "UTF-8");
	}


}