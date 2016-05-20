package com.zi.search.utils;

import java.util.Random;
import java.util.UUID;
/**
 * 产生rowkey的工具类
 * @author Jiaoyan
 *
 */
public class RowkeyUtils {
	static Random r = new Random();
	static int splitNum = Integer.parseInt(PropertyUtils.getSystemProperties("hbase.regionNum"));
	/**
	 * 产生rowkey
	 * @return
	 */
	public static String productRowkey(){
		String rowKey = String.format("%3d", r.nextInt(splitNum)).replaceAll(" ", "0")
				+ UUID.randomUUID().toString();
		return rowKey;
	}

}
