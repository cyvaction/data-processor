package com.yulore.bigdata.data_processor.facade;

import org.apache.log4j.PropertyConfigurator;

import com.yulore.bigdata.data_processor.common.constant.ContextConstant;
import com.yulore.bigdata.data_processor.controller.LineDataController;

/**
 * 行数据默认处理入口
 * @author：wanghaibo 
 * @creattime：2015-10-21 下午2:58:11 
 * 
 */  

public class LineDataFacade {
	static{
		PropertyConfigurator.configure(ContextConstant.LOG_PATH);
	}
	public static void main(String[] args) {
		new LineDataController().start(args);
	}
}
