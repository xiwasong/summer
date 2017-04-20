package cn.hn.java.summer.exception;

/**
 * 异常数据接口
 * 当对外提供的接口抛出的异常是本接口的异常时，
 * 表示异常中包含了其它需要返回的数据，
 * 在生成json结果是需要将异常和数据共同输出。
 * @author sjg
 * 2017年1月11日 下午2:15:16
 *
 */
public interface IExceptionData {

	public void setData(Object data);
	
	public Object getData();
}
