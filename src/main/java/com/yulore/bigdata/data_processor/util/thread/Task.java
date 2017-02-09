package com.yulore.bigdata.data_processor.util.thread;

/**
 * 
 * 创建人：wanghaibo <br>
 * 创建时间：2015-3-12 下午4:54:13 <br>
 * 功能描述： <br>
 * 任务
 * 版本： <br>
 * ====================================== <br>
 * 修改记录 <br>
 * ====================================== <br>
 * 序号 姓名 日期 版本 简单描述 <br>
 * 
 */  

public interface Task {
	public void before();
	public void execute() throws Exception;
	public void exception(Exception e);
	public void after();
}
