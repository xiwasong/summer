package cn.hn.java.summer.utils;

import cn.hn.java.summer.exception.BusinessException;

/**
 * Created by xw2sy on 2017-03-18.
 */
public interface Func {

    /**
     * 包含参数和返回值的方法接口
     * @param <P>
     * @param <R>
     */
    interface FunPr<P,R>{
        R run(P p) throws BusinessException;
    }

    /**
     * 包含参数的方法接口
     * @param <P>
     */
    interface FunP<P>{
        void run(P p) throws BusinessException;
    }

    /**
     * 包含返回值的方法接口
     * @param <R>
     */
    interface FunR<R>{
        R run() throws BusinessException;
    }

    /**
     * 既无参数也无返回值的方法接口
     */
    interface Fun{
        void run() throws BusinessException;
    }
}
