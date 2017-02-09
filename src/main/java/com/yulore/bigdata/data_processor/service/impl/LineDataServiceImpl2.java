package com.yulore.bigdata.data_processor.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.yulore.bigdata.data_processor.bean.LineData;
import com.yulore.bigdata.data_processor.common.ApplicationContext;
import com.yulore.bigdata.data_processor.common.constant.CharSet;
import com.yulore.bigdata.data_processor.dto.TaskFile;
import com.yulore.bigdata.data_processor.dto.TaskFileGroup;
import com.yulore.bigdata.data_processor.processor.LineDataProcessor;
import com.yulore.bigdata.data_processor.util.file.FileUtil;


/**
 * 责任链处理模式，每个处理器处理的都是上一个处理器处理的结果
 * @author：wanghaibo 
 * @creattime：2016年9月6日 下午5:34:02 
 * 
 */  
public class LineDataServiceImpl2 extends AbstractLineDataService{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LineDataServiceImpl2.class);

	@Override
	public void handle(TaskFile taskFile) {
		LOGGER.info("[process start]"+taskFile.getPendingFile().getPath());
		LineIterator lineIterator = null;
		try {
			lineIterator = FileUtils.lineIterator(taskFile.getPendingFile(), CharSet.UTF_8);
		} catch (IOException e) {
			LOGGER.error("[file load failure]"+taskFile.getPendingFile(), e);
			return;
		}
		
		boolean hasNextFlag = true;
		int inputTempNum = 0;//待输出记录数
		int batchCount = 100_000;//输出阈值
		String inputLine = null;
		
		
		Multimap<String, String> resultMap;
		if(ApplicationContext.getInstance().getCorrectConfig().isRemoveRepeat()){//去重
			resultMap = HashMultimap.create(4, 2048);
		}else{
			resultMap = ArrayListMultimap.create(4, 2048);
		}
		List<LineData> resultList = null;
		while(hasNextFlag){
			hasNextFlag = lineIterator.hasNext();
			if(hasNextFlag){//处理下一行
				inputLine = lineIterator.nextLine();
				LineData lineData = new LineData(taskFile.getInputDir(),taskFile.getPendingFile(),inputLine,ApplicationContext.getInstance().getOutPutDir(),null);
				for (LineDataProcessor processor : processorList) {
					try {
						resultList = processor.process(lineData);
						if(resultList!=null&&resultList.size()==1){
							lineData = resultList.get(0);
						}else{
							lineData = null;
							break;
						}
					} catch (Exception e) {
						LOGGER.error("[process line error]"+inputLine,e);
						System.exit(1);
					}
					
				}
				if(null!=lineData&&lineData.getOutputFile()!=null&&StringUtils.isNotBlank(lineData.getLine())){
					resultMap.put(lineData.getOutputFile().getPath(), lineData.getLine());
					inputTempNum++;
				}
			}
			
			if(!ApplicationContext.getInstance().getCorrectConfig().isRemoveRepeat()){//不去重的时候可以分批输出
				if(inputTempNum==batchCount||!hasNextFlag){//分批次或者到结尾时输出结果
					if(resultMap.size()>0){
						for (String outputFilePath : resultMap.keySet()) {
							Collection<String> lineColl = resultMap.get(outputFilePath);
							if(lineColl.size()>0){
								try {
									dataDao.save(outputFilePath, lineColl);
								} catch (IOException e) {
									LOGGER.error("[write line error]"+outputFilePath,e);
									System.exit(1);
								}
							}
						}
						resultMap.clear();
					}
					inputTempNum = 0;
				}
			}
			
		}
		LineIterator.closeQuietly(lineIterator);
		//输出处理结果
		if(resultMap.size()>0){
			for (String outputFilePath : resultMap.keySet()) {
				Collection<String> lineColl = resultMap.get(outputFilePath);
				if(lineColl.size()>0){
					try {
						dataDao.save(outputFilePath, lineColl);
					} catch (IOException e) {
						LOGGER.error("[write line error]"+outputFilePath,e);
						System.exit(1);
					}
				}
			}
			resultMap.clear();
		}
		
		//没有文件输出则输出空白文件
		if(taskFile.getInputDir()!=null&&taskFile.getPendingFile()!=null&&ApplicationContext.getInstance().getOutPutDir()!=null){
			String outputFileRelativePath = FileUtil.getRelativePath(taskFile.getInputDir(), taskFile.getPendingFile());
			if(StringUtils.isNotBlank(outputFileRelativePath)){
				String outputFilePath = ApplicationContext.getInstance().getOutPutDir().getPath()+ File.separator+taskFile.getInputDir().getName()+File.separator+outputFileRelativePath;
				File outputFile = new File(outputFilePath);
				try {
					FileUtil.createFile(outputFile);
				} catch (IOException e) {
					LOGGER.error("[create file failure]"+outputFilePath);
					System.exit(1);
				}
			}
		}
		LOGGER.info("[process over]"+taskFile.getPendingFile().getPath());
	}


	@Override
	public void merge(TaskFileGroup taskFileGroup) {
	}
	

}
