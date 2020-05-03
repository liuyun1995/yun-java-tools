package com.liuyun.github.security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * DES加密(对称加密)
 * @Author: liuyun18
 * @Date: 2018/11/28 下午2:13
 */
public class DESUtils {

    /**
     * 生成密钥
     * @return
     * @throws Exception
     */
    public static byte[] initKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("DES");
            keyGen.init(56);
            SecretKey secretKey = keyGen.generateKey();
            return secretKey.getEncoded();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 加密方法
     * @param data
     * @param key
     * @return
     */
    public static byte[] encrypt(byte[] data, byte[] key) {
        try {
            SecretKey secretKey = new SecretKeySpec(key, "DES");
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] cipherBytes = cipher.doFinal(data);
            return cipherBytes;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 解密方法
     * @param data
     * @param key
     * @return
     */
    public static byte[] decrypt(byte[] data, byte[] key) {
        try {
            SecretKey secretKey = new SecretKeySpec(key, "DES");
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] plainBytes = cipher.doFinal(data);
            return plainBytes;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
