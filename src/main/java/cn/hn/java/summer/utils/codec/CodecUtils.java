package cn.hn.java.summer.utils.codec;

import java.io.UnsupportedEncodingException;

public class CodecUtils {


	private static final String charset="Unicode";
	
	/**
	 * 加，解密
	 * @param b
	 * @param flag
	 * @return
	 */
	private static byte[] deOrEncrypt2Byte(	byte b[],
											String flag)
	{
		/**C/C++/C#版(参数中的b为不包含BOM头字符串以Unicode编码转换而来,BOM头的3个字节分别为:-2 -1 0)
		 * 
		 	byte key;
		    byte[] returnByte = b;
		    for (int i = 0; i < b.Length; i++)
		    {
			    key = b[i];
			    if (flag=="-")
			    {
				    key = (byte) (((key >> 4) & 0x0f) | ((key << 4) & 0xf0));
				    key = (byte) (key ^ 0x9f);
			    }
			    else
			    {
				    key = (byte) (key ^ 0x9f);
				    key = (byte) (((key >> 4) & 0x0f) | ((key << 4) & 0xf0));
			    }
			    returnByte[i] = key;
		    }
		 */
		
		//java版要去掉BOM头
		b=removeBOM(b);				
				
		byte key;
		byte returnByte[] = b;
		for (int i = 0; i < b.length; i++)
		{
			key = b[i];
			if (flag.equals("-"))
			{
				key = (byte) (((key >> 4) & 0x0f) | ((key << 4) & 0xf0));
				key = (byte) (key ^ 0x9f);
			}
			else
			{
				key = (byte) (key ^ 0x9f);
				key = (byte) (((key >> 4) & 0x0f) | ((key << 4) & 0xf0));
			}
			returnByte[i] = key;
		}
		return returnByte;
	}	


	/**
	 * 加密,返回字节
	 * @param str
	 * @return
	 */
	public static byte[] encrypt2Byte(String str)
	{
		return deOrEncrypt2Byte(getBytes(str),"+");
	}

	/**
	 * 加密,返回字符串
	 * @param b
	 * @param flag
	 * @return
	 */
	public static String encrypt2String(byte b[])
	{
		try {
			return new String(deOrEncrypt2Byte(removeBOM(b),"+"),charset);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
	


	/**
	 * 解密,返回字节
	 * @param str
	 * @return
	 */
	public static byte[] decrypt2Byte(String str)
	{
		return addBOM(deOrEncrypt2Byte(getBytes(str),"-"));
	}

	/**
	 * 解密,返回字符串
	 * @param b
	 * @param flag
	 * @return
	 */
	public static String decrypt2String(byte b[])
	{
		return getString(deOrEncrypt2Byte(removeBOM(b),"-"));
	}
	
	/**
	 * 取去掉BOM头的字符串字节数组
	 * @param str
	 * @return
	 */
	private static byte[] getBytes(String str){
		if(str==null){
			return null;
		}
		try {
			return removeBOM(str.getBytes(charset));
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
	
	/**
	 * 生成包含BOM头的字符串
	 * @param b
	 * @return
	 */
	private static String getString(byte[] b){
		try {
			return new String(addBOM(b),charset);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
	
	/**
	 * 去掉BOM头
	 * @param b
	 * @return
	 */
	private static byte[] removeBOM(byte[] b){

		//java版要去掉BOM头
		int startIndex=0;
		if(b.length>=2 && b[0]==-2 && b[1]==-1){
			startIndex=2;
		}
		//调整后的字节长度
		int realLength=b.length-startIndex;			
		byte[] tmp=new byte[realLength];
		System.arraycopy(b, startIndex, tmp, 0, realLength);
		return tmp;
	}
	
	/**
	 * 加上BOM头
	 * @param b
	 * @return
	 */
	private static byte[] addBOM(byte[] b){
		if(b.length<2 || (b[0]==-2 && b[1]==-1)){
			return b;
		}

		//调整后的字节长度
		int realLength=b.length+2;
		
		byte[] tmp=new byte[realLength];
		tmp[0]=-2;
		tmp[1]=-1;
		System.arraycopy(b, 0, tmp, 2, b.length);
		return tmp;
	}
}
