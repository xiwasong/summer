package cn.hn.java.summer.utils.codec;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * AES加密
 * @author sjg
 * @version 1.0.1 2014-2-10
 *
 */
public class AESCoder extends Coder{

	static final Log logger = LogFactory.getLog(AESCoder.class);
	
	/**
	 * 生成密钥
	 * @param password
	 * @return
	 */
	private static SecretKey createSecretKey(String password){
		/*
		//此种写法linux下有问题
		KeyGenerator kgen=null;
		try {
			kgen = KeyGenerator.getInstance("AES");
			kgen.init(128, new SecureRandom(password.getBytes()));   
			return kgen.generateKey();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}   
		*/
    	
    	 //防止linux下 随机生成key        	
	    SecureRandom secureRandom;
		try {
			secureRandom = SecureRandom.getInstance("SHA1PRNG");
		
		    secureRandom.setSeed(password.getBytes()); 
		    // 为我们选择的AES算法生成一个KeyGenerator对象  
		    KeyGenerator kg = null;   
			kg = KeyGenerator.getInstance("AES");		
		    kg.init(secureRandom); 
		    // 生成密钥  
		    return kg.generateKey();  
    	} catch (NoSuchAlgorithmException e) {
    		logger.warn("warinng:createSecretKey failed \n"+e.getMessage());
		}  
    	return null;
	}

	/** 
    * 加密 
    *  
    * @param content 需要加密的内容 
    * @param password  加密密码 
    * @return 
    */   
    public static byte[] encrypt(String content, String password) {   
        try {            
            byte[] enCodeFormat = createSecretKey(password).getEncoded();            
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");   
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器   
            byte[] byteContent = content.getBytes("utf-8");   
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化   
            byte[] result = cipher.doFinal(byteContent);   
            return result; // 加密   
        } catch (Exception e) {
 			logger.warn("warinng:encrypt failed \n"+e.getMessage());
        }
        return null;   
    }   
      
    /**
     * 解密 
     * @param content  待解密内容 
     * @param password 解密密钥 
     * @return 
      */   
     public static byte[] decrypt(byte[] content, String password) {   
         try {  
             byte[] enCodeFormat =  createSecretKey(password).getEncoded();   
             SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");               
             Cipher cipher = Cipher.getInstance("AES");// 创建密码器   
             cipher.init(Cipher.DECRYPT_MODE, key);// 初始化   
             byte[] result = cipher.doFinal(content);   
             return result; // 加密   
         } catch (Exception e) {
 			logger.warn("warinng:decrypt failed \n"+e.getMessage());
         }
         return null;   
     }
     
     /**
      * 加密成Base64字符串
      * @param content
      * @param password
      * @return
      */
     public static String encryptBase64(String content, String password){
    	 try {
			return new String(new Base64().encode(encrypt(content,password)),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.warn("warinng:encryptBase64 failed \n"+e.getMessage());
		}
		return null;
     }
     
     /**
      * 解密成Base64字符串
      * @param content
      * @param password
      * @return
      */
     public static String decryptBase64(String content, String password){
    	try {
    		byte[] bs=decrypt(new Base64().decode(content), password);
    		if(bs==null){
    			return null;
    		}
    		
    		return new String(bs,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.warn("warinng:decryptBase64 failed \n"+e.getMessage());
		}
		return null;
     }
     
     
     public static void main(String[] args) {  
    	     	 
//        String content = "{\"result\":false,\"data\":{\"exception\":\"0004\"}}";   
//        String password = "MDAwMDAwMDAwMDAwMDAwMA==";   
        String content = "10006";   
        String password = "123123123123213";   
        //加密   
        System.out.println("加密前：" + content);   
        for(int i=10001;i<99999;i++){
        	System.out.println(encryptBase64(i+"", password));
        }
        String encryptResult = encryptBase64(content, password);  
        System.out.println(encryptResult);
          
         //解密   
        String decryptResult = decryptBase64("TfY3Fq6BX2horoahmhmR+A==",password);   
        System.out.println("解密后：" + new String(decryptResult));   
   }  
}
