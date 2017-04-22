package cn.hn.java.summer.utils;

import cn.hn.java.summer.exception.SummerException;

public interface IReadString {

	public <T> T read(String[] str) throws SummerException;
}
