package com.yulore.bigdata.data_processor.dto.task;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CyclicBarrier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yulore.bigdata.data_processor.dto.TaskFileGroup;
import com.yulore.bigdata.data_processor.service.LineDataService;
import com.yulore.bigdata.data_processor.util.thread.Task;

public class MergeTask implements Task{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MergeTask.class);
	
	private CyclicBarrier cb;
	private BlockingQueue<TaskFileGroup> queue;
	private LineDataService dataService;
	
	public MergeTask( CyclicBarrier cb,BlockingQueue<TaskFileGroup> queue,LineDataService dataService) {
		this.cb = cb;
		this.queue = queue;
		this.dataService = dataService;
		
	}

	@Override
	public void before() {
		
	}

	@SuppressWarnings("deprecation")
	@Override
	public void execute() {
		TaskFileGroup taskFileGroup = null;
		while(true){
			try {
				taskFileGroup = queue.take();
			} catch (InterruptedException e) {
				LOGGER.error("[take taskFileGroup interrupted]", e);
				break;
			}
			
			if(taskFileGroup.getStatus()==ParserTask.FINISH_CODE){
				LOGGER.info("[receive finish signal,break]");
				break;
			}
			dataService.merge(taskFileGroup);
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
		}
	}
	
}
