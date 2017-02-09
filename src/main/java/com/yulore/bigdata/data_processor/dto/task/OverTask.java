package com.yulore.bigdata.data_processor.dto.task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.HashMultimap;
import com.yulore.bigdata.data_processor.common.ApplicationContext;
import com.yulore.bigdata.data_processor.common.constant.CharSet;
import com.yulore.bigdata.data_processor.common.constant.Constant;
import com.yulore.bigdata.data_processor.processor.Processor;
import com.yulore.bigdata.data_processor.util.collections.MapUtil;
import com.yulore.bigdata.data_processor.util.file.FileUtil;
import com.yulore.bigdata.data_processor.util.thread.Task;

public class OverTask implements Task{
	private static final Logger LOGGER = LoggerFactory.getLogger(OverTask.class);

	
	public OverTask() {
	}

	@Override
	public void before() {
		LOGGER.info("[start over task]");
	}

	@Override
	public void execute() {
		writeStatistics();
		writeLastFile();
	}
	
	/**
	 * 统计结果输出
	 */
	private void writeStatistics(){
		Map<String,Map<String, Long>> statisticsMap = ApplicationContext.getInstance().getStatisticsMap();
		if(statisticsMap.isEmpty()){
			return;
		}
		String filePath;
		Map<String, Long> subStatisticsMap;
		List<String> subStatisticsList;
		for (Entry<String, Map<String, Long>> entry : statisticsMap.entrySet()) {
			filePath = entry.getKey();
			LOGGER.info("[output statistics]"+filePath);
			if(entry.getValue().size()<1){
				continue;
			}
			subStatisticsMap = MapUtil.sortMapByValue(entry.getValue(), false);
			subStatisticsList = new ArrayList<>();
			for (Entry<String, Long> statistics: subStatisticsMap.entrySet()) {
				subStatisticsList.add(statistics.getKey()+"	"+statistics.getValue());
			}
			try {
				FileUtil.write(filePath,CharSet.UTF_8,subStatisticsList, new Processor<String,String>() {
					@Override
					public String process(String target) {
						return target+Constant.LINE_SEPARATOR;
					}
				},true);
			} catch (IOException e) {
				LOGGER.error("[output statistics failure]"+filePath,e);
			}
		}
		
		
	}
	
	/**
	 * 需要最后输出的文件
	 */
	public void writeLastFile(){
		HashMultimap<String, String> lastOutputMap = ApplicationContext.getInstance().getLastOutputMap();
		if(lastOutputMap.size()>0){
			for (String outputFilePath : lastOutputMap.keySet()) {
				Set<String> lineSet = lastOutputMap.get(outputFilePath);
				if(lineSet.size()>0){
					try {
						FileUtil.write(outputFilePath,CharSet.UTF_8,lineSet, new Processor<String,String>() {
							@Override
							public String process(String target) {
								return target+Constant.LINE_SEPARATOR;
							}
						},true);
					} catch (IOException e) {
						LOGGER.error("[write lastOutputMap error]"+outputFilePath);
					}
				}
			}
		}
	}
	
	@Override
	public void exception(Exception e) {
		LOGGER.error("[over task error]",e);
		System.exit(1);
	}

	@Override
	public void after() {
		LOGGER.info("[data processor over]");
	}
}
