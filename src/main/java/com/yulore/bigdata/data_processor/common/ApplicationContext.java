package com.yulore.bigdata.data_processor.common;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.HashMultimap;
import com.yulore.bigdata.data_processor.common.config.ProcessorConfig;
import com.yulore.bigdata.data_processor.common.constant.ContextConstant;
import com.yulore.bigdata.data_processor.util.DateUtil;
import com.yulore.bigdata.data_processor.util.Regex;
import com.yulore.bigdata.data_processor.util.ResourceClassLoader;

public class ApplicationContext {
	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationContext.class);
	
	public static final String APP_NAME = "data-correct";
	public static final String TIMESTAMP = DateUtil.getCurrentTimeStr(DateUtil.YYYYMMDDHHMMSS);
	
	private ResourceClassLoader resourceClassLoader;
	private ProcessorConfig correctConfig;
	private File outPutDir;
	
	//需要最后输出数据,key:路径,value:数据
	private HashMultimap<String, String> lastOutputMap = HashMultimap.create(4, 2048);
	private Map<String, Map<String, Long>> statisticsMap = new HashMap<>();
	
	private static class InstanceHandler{
		private static final ApplicationContext INSTANCE = new ApplicationContext();
	}
	public static ApplicationContext getInstance(){
		return InstanceHandler.INSTANCE;
	}
	public ApplicationContext() {
		
		correctConfig = new ProcessorConfig(ContextConstant.PROCESSOR_CONFIG_PATH);
		resourceClassLoader = new ResourceClassLoader(APP_NAME);
		outPutDir = DateUtil.getDirByCurrentDate(correctConfig.getOutputPath());
		try {
			resourceClassLoader.addURL(correctConfig.getPluginDir(), new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return Regex.check(correctConfig.getPluginFilterRegex(), pathname.getName());
				}
			});
		} catch (MalformedURLException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			LOGGER.error("[load plugin failure！]", e);
			System.exit(1);
		}
		Thread.currentThread().setContextClassLoader(resourceClassLoader);
		
	}
	
	public synchronized void addStatistics(String filePath,String key,long inc){
		if(!statisticsMap.containsKey(filePath)){
			statisticsMap.put(filePath, new HashMap<String,Long>());
		}
		if(statisticsMap.get(filePath).containsKey(key)){
			statisticsMap.get(filePath).put(key, statisticsMap.get(filePath).get(key)+inc);
		}else{
			statisticsMap.get(filePath).put(key,inc);
		}
	}
	
	public synchronized void setStatistics(String filePath,String key,long value){
		if(!statisticsMap.containsKey(filePath)){
			statisticsMap.put(filePath, new HashMap<String,Long>());
		}
		statisticsMap.get(filePath).put(key,value);
	}
	
	public long getStatistics(String filePath,String key){
		if(!containsStatisticsKey(filePath,key)){
			return 0;
		}
		return statisticsMap.get(filePath).get(key);
	}
	
	public boolean containsStatisticsKey(String filePath,String key){
		if(!statisticsMap.containsKey(filePath)||!statisticsMap.get(filePath).containsKey(key)){
			return false;
		}
		return true;
	}

	
	public ResourceClassLoader getResourceClassLoader() {
		return resourceClassLoader;
	}
	public ProcessorConfig getCorrectConfig() {
		return correctConfig;
	}
	public File getOutPutDir() {
		return outPutDir;
	}
	public Map<String, Map<String, Long>> getStatisticsMap() {
		return statisticsMap;
	}
	public HashMultimap<String, String> getLastOutputMap() {
		return lastOutputMap;
	}
	
}
