package com.mybank.backend.util;

import java.nio.charset.StandardCharsets;

/**
 * 身份证号加密/解密工具类。
 * 推荐后续改用AES加密，此处演示用Base64。
 */
public class IdNumberUtil {
    /**
     * Base64加密身份证号，严格指定UTF-8编码。
     */
    public static String encryptIdNumber(String plain) {
        return java.util.Base64.getEncoder().encodeToString(plain.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Base64解密身份证号，严格指定UTF-8编码，防止乱码。
     */
    public static String decryptIdNumber(String cipher) {
        if (cipher == null) return "";
        try {
            return new String(java.util.Base64.getDecoder().decode(cipher), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "";
        }
    }
}