package com.yulore.bigdata.data_processor.dto.task;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CyclicBarrier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yulore.bigdata.data_processor.dto.TaskFile;
import com.yulore.bigdata.data_processor.service.LineDataService;
import com.yulore.bigdata.data_processor.util.thread.Task;

public class ProcessorTask implements Task{
	
	
	private CyclicBarrier cb;
	private BlockingQueue<TaskFile> queue;
	private LineDataService dataService;
	private static final Logger LOGGER = LoggerFactory.getLogger(ProcessorTask.class);
	
	public ProcessorTask( CyclicBarrier cb,BlockingQueue<TaskFile> queue,LineDataService dataService) {
		this.cb = cb;
		this.queue = queue;
		this.dataService = dataService;
		
	}

	@Override
	public void before() {
		
	}

	@Override
	public void execute() {
		TaskFile taskFile = null;
		while(true){
			try {
				taskFile = queue.take();
			} catch (InterruptedException e) {
				LOGGER.error("[take taskFile interrupted]", e);
				break;
			}
			
			if(taskFile.getStatus()==ParserTask.FINISH_CODE){
				LOGGER.info("[receive finish signal,break]");
				break;
			}
			dataService.handle(taskFile);
		}
		
	}

	@Override
	public void exception(Exception e) {
		LOGGER.error("[process error]",e);
		System.exit(1);
	}

	@Override
	public void after() {
		try {
			cb.await();
		} catch (Exception e) {
			LOGGER.error("[CyclicBarrier await error]",e);
			System.exit(1);
		}
	}
	
}
