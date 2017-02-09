package com.yulore.bigdata.data_processor.common.constant;

import java.io.File;

import com.yulore.bigdata.data_processor.common.ApplicationContext;

public class Constant {
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");  
	
	//纠错输出目录相关
	public static final String OUTPUT_FILE_TEMPLATE;
	public static final String NEW_DIR_NAME = "new";
	public static final String DEL_DIR_NAME = "delete";
	public static final String UPDATE_DIR_NAME = "update";
	public static final String ALL_DIR_NAME = "all";
	public static final String BAK_DIR_NAME = "bak";
	static{
		OUTPUT_FILE_TEMPLATE = ApplicationContext.getInstance()
				.getOutPutDir().getPath()
				+ File.separator
				+ "{0}"//处理器名称
				+ File.separator
				+ "{1}"//数据结果类型，新增、更新、删除、全部
				+ File.separator
				+ "{2}"//输入目录最后一级目录名称
				+ File.separator
				+ "{3}";//输出的目录
	}
}
