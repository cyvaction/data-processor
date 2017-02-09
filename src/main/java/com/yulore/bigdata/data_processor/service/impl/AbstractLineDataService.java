package com.yulore.bigdata.data_processor.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yulore.bigdata.data_processor.common.ApplicationContext;
import com.yulore.bigdata.data_processor.dao.LineDataDao;
import com.yulore.bigdata.data_processor.dao.impl.LineDataDaoImpl;
import com.yulore.bigdata.data_processor.processor.LineDataProcessor;
import com.yulore.bigdata.data_processor.service.LineDataService;

public abstract class AbstractLineDataService implements LineDataService{
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractLineDataService.class);
	
	protected LineDataDao dataDao;
	protected List<LineDataProcessor> processorList;
	public AbstractLineDataService() {
		dataDao = new LineDataDaoImpl();
		// 初始化所有检查处理器
		processorList = new ArrayList<>();
		List<String> processorClassNameList = ApplicationContext.getInstance().getCorrectConfig().getProcessorClassNameList();
		if(processorClassNameList.size()<1){
			LOGGER.error("[processor size is 0]");
			System.exit(0);
		}
		for (String processorClassName : processorClassNameList) {
			try {
				Object obj = Class.forName(processorClassName,false,ApplicationContext.getInstance().getResourceClassLoader()).newInstance();//这个方法可以初始化(即可以执行static中的代码),loadClass不会初始化
				if(obj instanceof LineDataProcessor){
					processorList.add((LineDataProcessor) obj);
					LOGGER.info("add class:"+processorClassName);
				}
				if(processorList.size()<1){
					LOGGER.error("[processor size is 0]");
					System.exit(1);
				}
			} catch (Exception e) {
				LOGGER.error("[class load failure]", e);
				System.exit(1);
			}
		}
	}
	
}
