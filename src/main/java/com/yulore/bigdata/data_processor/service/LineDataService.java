package com.yulore.bigdata.data_processor.service;

import com.yulore.bigdata.data_processor.dto.TaskFile;
import com.yulore.bigdata.data_processor.dto.TaskFileGroup;


public interface LineDataService{
	/**
	 * 数据处理
	 * @param taskFile
	 */
	public void handle(TaskFile taskFile);
	/**
	 * 数据合并，多线程合并数据效率不高，可用单线程的handle方法替代
	 * @param taskFileGroup
	 */
	@Deprecated
	public void merge(TaskFileGroup taskFileGroup);
}
