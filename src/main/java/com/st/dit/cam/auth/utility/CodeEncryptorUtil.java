package com.st.dit.cam.auth.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;


public class CodeEncryptorUtil{

    private static Logger LOGGER = LoggerFactory.getLogger(CodeEncryptorUtil.class);

    private static final String ALGO = "AES";

    private static final byte[] keyValue = new byte[32];


    public static String encrypt(String stringValue) {
        String encodedPwd = "";
        try {
            Key key = generateKey();
            Cipher c = Cipher.getInstance(ALGO);
            c.init(Cipher.ENCRYPT_MODE, key);
            byte[] encVal = c.doFinal(stringValue.getBytes());
            encodedPwd = Base64.getEncoder().encodeToString(encVal);
        } catch (Exception ex) {
            LOGGER.error("error::error encrypting: {} :", stringValue, ex);
        }
        return encodedPwd;
    }

    public static String decrypt(String stringValue) {
        String decodedPWD = "";
        try {
            Key key = generateKey();
            Cipher c = Cipher.getInstance(ALGO);
            c.init(Cipher.DECRYPT_MODE, key);
            byte[] decordedValue = Base64.getDecoder().decode(stringValue);
            byte[] decValue = c.doFinal(decordedValue);
            decodedPWD = new String(decValue);
        } catch (Exception ex) {
            LOGGER.error("error::error decrypting: {} :", stringValue, ex);
        }
        return decodedPWD;
    }

    private static Key generateKey() {
        SecretKeySpec key = new SecretKeySpec(keyValue, ALGO);
        return key;
    }

    private CodeEncryptorUtil(){}
}
