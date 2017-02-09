package com.yulore.bigdata.data_processor.controller;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yulore.bigdata.data_processor.common.ApplicationContext;
import com.yulore.bigdata.data_processor.dto.TaskFile;
import com.yulore.bigdata.data_processor.dto.TaskFileGroup;
import com.yulore.bigdata.data_processor.dto.task.MergeTask;
import com.yulore.bigdata.data_processor.dto.task.OverTask;
import com.yulore.bigdata.data_processor.dto.task.ParserTask;
import com.yulore.bigdata.data_processor.dto.task.ProcessorTask;
import com.yulore.bigdata.data_processor.service.impl.LineDataServiceImpl;
import com.yulore.bigdata.data_processor.service.impl.LineDataServiceImpl2;
import com.yulore.bigdata.data_processor.util.file.FileUtil;
import com.yulore.bigdata.data_processor.util.thread.SafeThread;

public class LineDataController {
	private static final Logger LOGGER = LoggerFactory.getLogger(LineDataController.class);
	
	/**
	 * 默认数据处理
	 * @param threadNum
	 * @param inputDir
	 */
	public void start(String[] args){
		if(args.length<2){
			System.err.println("应用程序至少需要两个参数");
			System.err.println("1、处理器方式，1、并行；2、链式处理");
			System.err.println("2、线程数(若小于或等于0,则使用1线程)");
			System.err.println("3、待处理目录或文件");
			System.err.println("4、处理器class(可选，按逗号分隔)");
			return;
		}
		int processType = Integer.parseInt(args[0]);
		int threadNum = Integer.parseInt(args[1]);
		if(threadNum<1){
			threadNum = 1;
		}
		
		File inputDir = new File(args[2]);
		if(!inputDir.exists()){
			System.out.println("待处理目录不存在");
			return;
		}
		if(args.length>2){
			String[] processClassNames = args[3].split(",");
			for (String processClassName : processClassNames) {
				ApplicationContext.getInstance().getCorrectConfig().getProcessorClassNameList().add(processClassName);
			}
		}
		
		LOGGER.info("[data correct start]	"+inputDir.getPath());
		
		BlockingQueue<TaskFile> queue = new LinkedBlockingQueue<TaskFile>();
		
		CyclicBarrier cb = new CyclicBarrier(threadNum,
				new SafeThread(new OverTask()));
		
		ExecutorService threadPool = Executors.newFixedThreadPool(threadNum+1);
		
		threadPool.submit(new SafeThread(new ParserTask(inputDir, queue, threadNum)));
		
		for(int i=0;i<threadNum;i++){
			switch (processType) {
			case 1:
				threadPool.submit(new SafeThread(new ProcessorTask(cb,queue,new LineDataServiceImpl())));
				break;
			case 2:
				threadPool.submit(new SafeThread(new ProcessorTask(cb,queue,new LineDataServiceImpl2())));
				break;
			default:
				break;
			}
		}
		threadPool.shutdown();
	}
	
	/**
	 * 
	 * 数据合并
	 * @param threadNum
	 * @param inputDir
	 */
	public void merge(String[] args){
		if(args.length<2){
			System.err.println("应用程序至少需要两个参数");
			System.err.println("1、线程数(若小于或等于0,则使用1线程)");
			System.err.println("2、待处理目录(可以多个)");
			System.err.println("3、处理器class(可选，按逗号分隔)");
			return;
		}
		int threadNum = Integer.parseInt(args[0]);
		if(threadNum<1){
			threadNum = 1;
		}
		
		List<File> inputDirList = new ArrayList<>();
		File input;
		for (int i = 1; i < args.length; i++) {
			input = new File(args[i]);
			if(input.exists()){
				inputDirList.add(input);
			}else{
				LOGGER.error("[dir not exist]"+args[i]);
			}
		}
		if(args.length>2){
			String[] processClassNames = args[2].split(",");
			for (String processClassName : processClassNames) {
				ApplicationContext.getInstance().getCorrectConfig().getProcessorClassNameList().add(processClassName);
			}
		}
		
		
		LOGGER.info("[data merge start]");
		
		BlockingQueue<TaskFileGroup> queue = new LinkedBlockingQueue<TaskFileGroup>();
		Map<String, TaskFileGroup> taskFileGroupMap = new HashMap<>();
		//每个待处理目录下的所有文件
		List<File> inputFileList = new ArrayList<>();
		
		//子路径，回作为任务文件组的名字
		String subPath;
		TaskFile taskFile;
		for (File inputDir : inputDirList) {
			File realInputDir = inputDir;//实际的输入目录
			FileFilter fileFilter = ApplicationContext.getInstance().getCorrectConfig().getFileFilter();
			if(inputDir.isFile()){
				if(fileFilter!=null&&!fileFilter.accept(inputDir)){
					continue;
				}
				inputFileList.add(inputDir);
				realInputDir = inputDir.getParentFile();
			}else{
				FileUtil.getFiles(inputFileList, inputDir,fileFilter);
			}
			for (File inputFile : inputFileList) {
				subPath = FileUtil.getRelativePath(realInputDir, inputFile);
				taskFile = new TaskFile(realInputDir, inputFile, 0);
				if(!taskFileGroupMap.containsKey(subPath)){
					TaskFileGroup taskFileGroup = new TaskFileGroup(subPath, new ArrayList<TaskFile>(), 0);
					taskFileGroupMap.put(subPath, taskFileGroup);
				}
				taskFileGroupMap.get(subPath).addTaskFile(taskFile);
			}
			inputFileList.clear();
		}
		queue.addAll(taskFileGroupMap.values());
		
		//结束标志
		for (int i = 0; i < threadNum; i++) {
			queue.add(new TaskFileGroup(null,null,1));
		}
		
		CyclicBarrier cb = new CyclicBarrier(threadNum,
				new SafeThread(new OverTask()));
		
		ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
		
		for(int i=0;i<threadNum;i++){
			threadPool.submit(new SafeThread(new MergeTask(cb,queue,new LineDataServiceImpl())));
		}
		threadPool.shutdown();
	}
}
