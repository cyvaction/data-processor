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
 * 每一行数据会分发所有处理器各自处理
 * @author：wanghaibo 
 * @creattime：2015-10-21 下午2:57:52 
 * 
 */  

public class LineDataServiceImpl extends AbstractLineDataService{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LineDataServiceImpl.class);
	

	@Override
	public void handle(TaskFile taskFile) {
		
		LOGGER.info("[process start]"+taskFile.getPendingFile().getPath());
		LineIterator lineIterator = null;
		try {
			lineIterator = FileUtils.lineIterator(taskFile.getPendingFile(), CharSet.UTF_8);
		} catch (IOException e) {
			LOGGER.error("[file load failure]"+taskFile.getPendingFile(), e);
			System.exit(1);
		}
		
		boolean hasNextFlag = true;
		int inputTempNum = 0;
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
				for (LineDataProcessor processor : processorList) {
					try {
						resultList = processor.process(new LineData(taskFile.getInputDir(),taskFile.getPendingFile(),inputLine,ApplicationContext.getInstance().getOutPutDir(),null));
						if(resultList!=null&&resultList.size()>0){
							for (LineData lineData : resultList) {
								if(lineData.getOutputFile()!=null&&StringUtils.isNotBlank(lineData.getLine())){
									resultMap.put(lineData.getOutputFile().getPath(), lineData.getLine());
								}
							}
						}
					} catch (Exception e) {
						LOGGER.error("[process line error]"+inputLine,e);
						System.exit(1);
					}
					
				}
				inputTempNum++;
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
		
		Multimap<String, String> resultMap;
		if(ApplicationContext.getInstance().getCorrectConfig().isRemoveRepeat()){//去重
			resultMap = HashMultimap.create(4, 2048);
		}else{
			resultMap = ArrayListMultimap.create(4, 2048);
		}
		
		//所有任务文件
		List<TaskFile> taskFileList = taskFileGroup.getTaskFileList();
		for (TaskFile taskFile : taskFileList) {
			LOGGER.info("[process start]"+taskFile.getPendingFile().getPath());
			LineIterator lineIterator = null;
			try {
				lineIterator = FileUtils.lineIterator(taskFile.getPendingFile(), CharSet.UTF_8);
			} catch (IOException e) {
				LOGGER.error("[file load failure]"+taskFile.getPendingFile(), e);
				System.exit(1);
			}
			
			String inputLine = null;
			List<LineData> resultList = null;
			LineData inputLineData;
			File defaultOutputFile = LineData.getDefaultOutputFile(taskFile.getInputDir(), taskFile.getPendingFile(), ApplicationContext.getInstance().getOutPutDir());
			try {
				if(defaultOutputFile!=null){
					FileUtil.createFile(defaultOutputFile);
				}
			} catch (IOException e1) {
				LOGGER.error("[create defaultOutputFile error]"+defaultOutputFile.getPath(),e1);
				System.exit(1);
			}
			while(lineIterator.hasNext()){
				inputLine = lineIterator.nextLine();
				for (LineDataProcessor processor : processorList) {
					try {
						inputLineData = new LineData(taskFile.getInputDir(),taskFile.getPendingFile(),inputLine,ApplicationContext.getInstance().getOutPutDir(),defaultOutputFile);
						resultList = processor.process(inputLineData);
						if(resultList!=null&&resultList.size()>0){
							for (LineData lineData : resultList) {
								if(lineData.getOutputFile()!=null&&StringUtils.isNotBlank(lineData.getLine())){
									resultMap.put(lineData.getOutputFile().getPath(), lineData.getLine());
								}
							}
						}
					} catch (Exception e) {
						LOGGER.error("[process line error]"+inputLine,e);
						System.exit(1);
					}
					
				}
			}
			LineIterator.closeQuietly(lineIterator);
			LOGGER.info("[process over]"+taskFile.getPendingFile().getPath());
		}
		
		//该任务文件组结果输出
		if(resultMap.size()>0){
			for (String outputFilePath : resultMap.keySet()) {
				Collection<String> lineDataSet = resultMap.get(outputFilePath);
				if(lineDataSet.size()>0){
					try {
						dataDao.save(outputFilePath, lineDataSet);
					} catch (IOException e) {
						LOGGER.error("[write line error]"+outputFilePath);
						System.exit(1);
					}
				}
			}
			resultMap.clear();
		}
	}
	
	/**
	 * 文件合并，按照linedata的lineKey去重,Q:效率太低，不知道怎么解决
	 * @param args
	 */
//	@Override
//	public void merge(TaskFileGroup taskFileGroup) {
//		//文件组全部处理结果
//		HashMultimap<String, LineData> resultMap = HashMultimap
//				.create(4, 2048);//key为输出文件路径,value为输出的内容list
//		
//		//所有任务文件
//		List<TaskFile> taskFileList = taskFileGroup.getTaskFileList();
//		for (TaskFile taskFile : taskFileList) {
//			LOGGER.info("[process start]"+taskFile.getPendingFile().getPath());
//			LineIterator lineIterator = null;
//			try {
//				lineIterator = FileUtils.lineIterator(taskFile.getPendingFile(), CharSet.UTF_8);
//			} catch (IOException e) {
//				LOGGER.error("[file load failure]"+taskFile.getPendingFile(), e);
//				return;
//			}
//			
//			if(processorList.size()<1){
//				LOGGER.error("[processor size is 0]"+taskFile.getPendingFile());
//				return;
//			}
//			
//			String inputLine = null;
//			List<LineData> resultList = null;
//			while(lineIterator.hasNext()){
//				inputLine = lineIterator.nextLine();
//				for (LineDataProcessor processor : processorList) {
//					try {
//						resultList = processor.process(new LineData(taskFile.getInputDir(),taskFile.getPendingFile(),inputLine,ApplicationContext.getInstance().getOutPutDir(),null));
//						if(resultList!=null&&resultList.size()>0){
//							for (LineData lineData : resultList) {
//								if(lineData.getOutputFile()!=null&&StringUtils.isNotBlank(lineData.getLine())){
//									resultMap.put(lineData.getOutputFile().getPath(), lineData);
//								}
//							}
//						}
//					} catch (Exception e) {
//						LOGGER.error("[process line error]"+inputLine,e);
//					}
//					
//				}
//			}
//			LineIterator.closeQuietly(lineIterator);
//			LOGGER.info("[process over]"+taskFile.getPendingFile().getPath());
//		}
//		
//		//该任务文件组结果输出
//		if(resultMap.size()>0){
//			for (String outputFilePath : resultMap.keySet()) {
//				Set<LineData> lineDataSet = resultMap.get(outputFilePath);
//				if(lineDataSet.size()>0){
//					List<String> lineColl = new ArrayList<>();
//					for (LineData lineData : lineDataSet) {
//						lineColl.add(lineData.getLine());
//					}
//					try {
//						dataDao.save(outputFilePath, lineColl);
//					} catch (IOException e) {
//						LOGGER.error("[write line error]"+outputFilePath);
//					}
//				}
//			}
//			resultMap.clear();
//		}
//	}
	
}
