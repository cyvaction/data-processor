package com.yulore.bigdata.data_processor.facade;

import org.apache.log4j.PropertyConfigurator;

import com.yulore.bigdata.data_processor.common.constant.ContextConstant;
import com.yulore.bigdata.data_processor.controller.LineDataController;

/**
 * 数据合并入口
 * @author：wanghaibo 
 * @creattime：2015-11-6 下午3:22:19 
 * 
 */  

public class DataMergeFacade {
	static{
		PropertyConfigurator.configure(ContextConstant.LOG_PATH);
	}
	public static void main(String[] args) {
		new LineDataController().merge(args);
	}
}
