package com.web.util;

import java.io.UnsupportedEncodingException;

public class CodeUtils
{
  public static byte[] EncodeUCS2B(String src)
  {
    byte[] bytes = (byte[])null;
    try {
      bytes = src.getBytes("UTF-16BE");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return bytes;
  }

  public static String EncodeUCS2(String src) {
    byte[] bytes = (byte[])null;
    try {
      bytes = src.getBytes("UTF-16BE");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }

    StringBuffer reValue = new StringBuffer();
    StringBuffer tem = new StringBuffer();
    for (int i = 0; i < bytes.length; i++) {
      tem.delete(0, tem.length());
      tem.append(Integer.toHexString(bytes[i] & 0xFF));
      if (tem.length() == 1) {
        tem.insert(0, '0');
      }
      reValue.append(tem);
    }
    return reValue.toString().toUpperCase();
  }

  public static byte[] encode8bit(String src) {
    byte[] result = (byte[])null;
    result = src.getBytes();
    return result;
  }

  public static void main(String[] args) {
    String s = "中国人民aaa";
    byte[] buffer = EncodeUCS2B(s);
    printByte(buffer);
    printByte(s, "UTF-8");
    printByte(s, "UTF-16BE");
    printByte(s, "GBK");
  }

  private static void printByte(String str, String encoding) {
    try {
      byte[] buffer = str.getBytes(encoding);
      printByte(buffer);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }

  private static void printByte(byte[] code) {
    for (int i = 0; i < code.length; i++) {
      System.out.print(code[i] + ",");
    }
    System.out.println("------" + code.length);
  }
}