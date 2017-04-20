package cn.hn.java.summer.utils.codec;

import java.security.Key;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.apache.commons.codec.binary.Base64;

/**
 * pbe加解密
 * @author sjg
 * @version 1.0.1 2014-1-19
 *
 */
public abstract class PBECoder extends Coder {  
    /** 
     * 支持以下任意一种算法 
     *  
     * <pre> 
     * PBEWithMD5AndDES  
     * PBEWithMD5AndTripleDES  
     * PBEWithSHA1AndDESede 
     * PBEWithSHA1AndRC2_40 
     * </pre> 
     */  
    public static final String ALGORITHM = "PBEWITHMD5andDES";  
  
    /** 
     * 盐初始化 
     *  
     * @return 
     * @throws Exception 
     */  
    public static byte[] initSalt() throws Exception {  
        byte[] salt = new byte[8];  
        Random random = new Random();  
        random.nextBytes(salt);  
        return salt;  
    }  
  
    /** 
     * 转换密钥<br> 
     *  
     * @param password 
     * @return 
     * @throws Exception 
     */  
    private static Key toKey(String password) throws Exception {  
        PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray());  
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);  
        SecretKey secretKey = keyFactory.generateSecret(keySpec);  
  
        return secretKey;  
    }  
  
    /** 
     * 加密 
     *  
     * @param data 
     *            数据 
     * @param password 
     *            密码 
     * @param salt 
     *            盐 
     * @return 
     * @throws Exception 
     */  
    public static byte[] encrypt(byte[] data, String password, byte[] salt)  
            throws Exception {  
  
        Key key = toKey(password);  
  
        PBEParameterSpec paramSpec = new PBEParameterSpec(salt, 100);  
        Cipher cipher = Cipher.getInstance(ALGORITHM);  
        cipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);  
  
        return cipher.doFinal(data);  
  
    }  
    
    /**
     * 固定盐加密
     * @param data
     * @param pwd
     * @return
     * @throws Exception
     */
    public static String encrypt(String data, String pwd) throws Exception  {
    	return Base64.encodeBase64String(encrypt(data.getBytes(),pwd,saltFromPwd(pwd)));
    }
    
  
    /** 
     * 解密 
     *  
     * @param data 
     *            数据 
     * @param password 
     *            密码 
     * @param salt 
     *            盐 
     * @return 
     * @throws Exception 
     */  
    public static byte[] decrypt(byte[] data, String password, byte[] salt)  
            throws Exception {  
  
        Key key = toKey(password);  
  
        PBEParameterSpec paramSpec = new PBEParameterSpec(salt, 100);  
        Cipher cipher = Cipher.getInstance(ALGORITHM);  
        cipher.init(Cipher.DECRYPT_MODE, key, paramSpec);  
  
        return cipher.doFinal(data);  
  
    }  
    
    /**
     * 固定盐解密
     * @param data
     * @param pwd
     * @return
     * @throws Exception 
     */
    public static String decrypt(String data, String pwd) throws Exception{    	
    	return new String(decrypt(Base64.decodeBase64(data),pwd,saltFromPwd(pwd)),"UTF-8");
    }
    
    private static byte[] saltFromPwd(String pwd) throws Exception{
    	String saltPwd=pwd.replaceAll("[\\d]", "");
    	byte[] salt=new byte[8];
    	System.arraycopy(encryptBASE64(saltPwd), 0, salt, 0, 8);
    	return salt;
    }
}