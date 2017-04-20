package cn.hn.java.summer.utils;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DecimalUtils {

    //默认除法运算精度
    private static final int DEF_DIV_SCALE = 10;
   
	/**
	*4.4增
	*功能  数据值处理 (暂时取整)
	*函数  public long DataRecovertSet(double inData,int onk)
	* @param args
	*输入   inData     --- 双精度小数  double
	        onk        --- 结合c_system_public表参数(0/1)设置，选择取整方式 0（原值）小数,1 四舍五入取整 2去小数直取整 3凑整
	*输出   outData    --- 数值 String
	 */

	public static String DataRecovertSet(double inData,int onk){
	  	 String outData="";
	  	 BigInteger result2;
	  	 DecimalFormat ourForm_00 = new DecimalFormat("################.00");//16位整
		  	if (onk==0){
		  		outData=""+inData;
		  	}else if (onk==1){
		  		outData=""+new BigDecimal(""+inData).setScale(0, BigDecimal.ROUND_HALF_UP);
		  		//outData=(long)Math.rint(inData);
		    }else if (onk==2){
		    	BigDecimal result1 = new BigDecimal(ourForm_00.format(inData));
		    	outData=""+result1;
		    	result2=new BigInteger(""+outData.substring(0,outData.indexOf(".")));
		  		outData=""+result2;
		    }else if (onk==3){
		    	outData=""+(long)Math.ceil(inData);
		    }
	  	return outData;
	}

 /**
     * 提供精确的加法运算。
     * @param v1 被加数
     * @param v2 加数
     * @return 两个参数的和
     */
    public static double add(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }
    /**
     * 提供精确的减法运算。
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差
     */
    public static double sub(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).doubleValue();
    } 
    /**
     * 提供精确的乘法运算。
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    public static double mul(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2).doubleValue();
    }
 
    /**
     * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到
     * 小数点以后10位，以后的数字四舍五入。
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */
    public static double div(double v1,double v2){
        return div(v1,v2,DEF_DIV_SCALE);
    }
 
    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
     * 定精度，以后的数字四舍五入。
     * @param v1 被除数
     * @param v2 除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static double div(double v1,double v2,int scale){
        if(scale<0){
            throw new IllegalArgumentException(
                "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2,scale,BigDecimal.ROUND_HALF_UP).doubleValue();
    }
 
    /**
     * 提供精确的小数位四舍五入处理。
     * @param v 需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    public static double round(double v,int scale){
        if(scale<0){
            throw new IllegalArgumentException(
                "The scale must be a positive integer or zero");
        }
        BigDecimal b = new BigDecimal(Double.toString(v));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one,scale,BigDecimal.ROUND_HALF_UP).doubleValue();
    }
    /**
     * 提供大量数据带小数。
     * @param v 需要带小的大数字
     * @return 结果
     */
    public static BigDecimal getBigDeciamlVaule(double v){        
        BigDecimal b = new BigDecimal(Double.toString(v));       
        return b;
    }
    /**
     * isNumeric用来判断数字
     * @param str（String) 输入项
     * @return(boolean) false为不是数字 
     */
    public static boolean isNumeric(String str)
    {
  	  Pattern pattern = Pattern.compile("[0-9]*");
  	  Matcher isNum = pattern.matcher(str);
  	  if( !isNum.matches() )
  	  {
  	  return false;
  	  }
  	  return true;
    }

}
