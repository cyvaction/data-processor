package com.yulore.bigdata.data_processor.util.thread;


/**
 * 
 * 创建人：wanghaibo <br>
 * 创建时间：2015-3-12 下午5:00:44 <br>
 * 功能描述： <br>
 * 安全线程，防止异常造成程序崩溃
 * 版本： <br>
 * ====================================== <br>
 * 修改记录 <br>
 * ====================================== <br>
 * 序号 姓名 日期 版本 简单描述 <br>
 * 
 */  

public class SafeThread implements Runnable{
	Task task;

	
	public SafeThread(Task task) {
		this.task = task;
	}


	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			task.before();
			task.execute();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			task.exception(e);
		}finally{
			task.after();
		}
	}
}
