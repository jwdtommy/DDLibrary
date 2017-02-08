/**
 * Copyright (C) 2015 The KnowboxFramework Project
 */
package com.hyena.framework.security;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.annotation.SuppressLint;
import android.util.Base64;

/**
 * AES加解密算法
 * @author yangzc
 */
public class AESUtils {

	public static final String TAG = "AESCrypt";

	/**
	 * 加密算法
	 * @param seed 加密密钥
	 * @param source 加密内容
	 * @return
	 */
    public static String encrypt(String seed, String source) {
        byte[] result = null;
        try {
            byte[] rawkey = getRawKey(seed.getBytes());
            result = encrypt(rawkey, source.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String content = toHex(result);
        return content;

    }

    /**
     * 解密算法
     * @param seed 加密密钥
     * @param source 要解密的内容
     * @return
     */
    public static String decrypt(String seed, String source) {
        byte[] rawKey;
        try {
            rawKey = seed.getBytes();
            byte[] enc = Base64.decode(source, Base64.DEFAULT);
            byte[] result = decrypt(rawKey, enc);
            String content = new String(result);
            return content;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @SuppressLint("TrulyRandom")
	private static byte[] getRawKey(byte[] seed) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        sr.setSeed(seed);
        kgen.init(256, sr);
        SecretKey sKey = kgen.generateKey();
        byte[] raw = sKey.getEncoded();

        return raw;
    }

    private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(
                new byte[cipher.getBlockSize()]));
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    private static byte[] decrypt(byte[] raw, byte[] encrypted)
            throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }

    /**
     * 字符串转16进制后转字符串
     * @param txt
     * @return
     */
    public static String toHex(String txt) {
        return toHex(txt.getBytes());
    }

    /**
     * 从16进制字符串转字符串
     * @param hex
     * @return
     */
    public static String fromHex(String hex) {
        return new String(toByte(hex));
    }

    public static byte[] toByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++) {
            String tmp = hexString.substring(2 * i, 2 * i + 2);
            result[i] = (byte) (Integer.parseInt(tmp, 16) & 0xFF);
        }
        return result;
    }

    public static String toHex(byte[] buf) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2 * buf.length);
        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }

    private static void appendHex(StringBuffer sb, byte b) {
        final String HEX = "0123456789ABCDEF";
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }
}
