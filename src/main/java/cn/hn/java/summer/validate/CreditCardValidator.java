package cn.hn.java.summer.validate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 身份证号码验证器
 * @author sjg
 * @version 1.0.1 2013-10-25
 *
 */
public class CreditCardValidator implements ConstraintValidator<CreditCard, String> {


	/**
	 * 验证
	 */
    public boolean isValid(String object, ConstraintValidatorContext constraintContext) {

        if (org.apache.commons.lang3.StringUtils.isBlank(object)){
            return true;
        }
        return Validator.verify(object);
    }

    
    /**
     * 身份证号码验证，来自网络，无注释
     * @author sjg
     * @version 1.0.1 2013-10-25
     *
     */
    private static class Validator{
	    private static final int[] wi = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10,5, 8, 4, 2, 1 };
		private static final int[] vi = { 1, 0, 'X', 9, 8, 7, 6, 5, 4, 3, 2 };  
		private static int[] ai = new int[18];  
		
		/** 
		 * 验证身份证 
		 *  
		 * @param 需要验证的身份证号码 
		 * @return 通过则为 true 
		 */  
		public static boolean verify(String idcard) {  
			idcard=idcard.toUpperCase();
		    if (idcard.length() == 15)  
		        idcard = uptoeighteen(idcard);  
		    if (idcard.length() != 18)  
		        return false;  
		    String verify = idcard.substring(17, 18);  
		    if (verify.equals(getVerify(idcard)))  
		        return true;  
		    return false;  
		}  
		
		private static String getVerify(String eightcardid) {  
		    int remaining = 0;  
		    if (eightcardid.length() == 18) {  
		        eightcardid = eightcardid.substring(0, 17);  
		    }  
		    if (eightcardid.length() == 17) {  
		        int sum = 0;  
		        for (int i = 0; i < 17; i++) {  
		            String k = eightcardid.substring(i, i + 1);  
		            ai[i] = Integer.parseInt(k);  
		        }  
		        for (int i = 0; i < 17; i++) {  
		            int w = wi[i];  
		            int a = ai[i];  
		            sum += w * a;  
		        }  
		        remaining = sum % 11;  
		    }  
		    return remaining == 2 ? "X" : String.valueOf(vi[remaining]);  
		}  
		
		private static String uptoeighteen(String fifteencardid) {  
		    String eightcardid = fifteencardid.substring(0, 6);  
		    eightcardid += "19" + fifteencardid.substring(6, 15);  
		    eightcardid += getVerify(eightcardid);  
		    return eightcardid;  
		}  
    }


	public void initialize(CreditCard arg0) {
	}
}