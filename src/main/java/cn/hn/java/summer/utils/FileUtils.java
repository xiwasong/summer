package cn.hn.java.summer.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.hn.java.summer.exception.SnException;

public class FileUtils extends org.apache.commons.io.FileUtils{
	static Log logger=LogFactory.getLog(FileUtils.class);

	/**
	 * 从输入流中读取字符串
	 * @param input
	 * @return
	 * @throws IOException
	 */
	public static String readString(InputStream input) throws IOException{
		return IOUtils.toString(input, "utf-8");
	}
	
	/**
	 * 将输入流的内容按\r\n或\n换行，以任意空白字符为列分割，转换成T对象列表
	 * @param <T>
	 * @param input 
	 * @param reader
	 * @return
	 * @throws SnException 
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> transfer(InputStream input,IReadString reader) throws SnException{
		String str="";
		try {
			str = readString(input);
		} catch (IOException e) {
			new SnException("read input to string error!",e);
		}
		
		if(StringUtils.isBlank(str)){
			return null;
		}
		str+="\n";
		
		List<T> list=new ArrayList<T>();
		Matcher match= Pattern.compile("[^\\n]+\\r?\\n").matcher(str);
		while(match.find()){
			//列
			String[] cols=match.group().split("[\\s　]+");
			//读取列调用read返回对象
			T t=(T)reader.read(cols);
			if(t!=null){
				list.add(t);
			}
		}
		return list;
	}
	
	/**
	 * 取文件夹下按文件名排序的所有文件
	 * @param fileDir
	 * @param desc
	 * @return
	 */
	public static File[] getSortedFilesByName(String fileDir,boolean desc){
		File[] files = new File(fileDir).listFiles();
		if(files!=null && files.length>0){
			Arrays.sort(files, new Comparator<File>() {
				public int compare(File f1, File f2) {
					return f1.getName().compareToIgnoreCase(f2.getName());
				}
			});
		}
		return files;
	}
	
}
