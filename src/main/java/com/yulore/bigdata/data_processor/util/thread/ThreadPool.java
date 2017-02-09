package com.yulore.bigdata.data_processor.util.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 线程池工具类，使用ExecutorService如果线程等待的太多会影响性能，
 * 这里可以保证之后线程池有空余线程时才将runnable加入线程
 * @author admin
 *
 */
public class ThreadPool {
	//线程池大小限制,默认为30
	private int threadLimit = 30;
	//线程池数量限制
	private int submitLimit = 300;
	//提交线程许可信号量
	private volatile Semaphore semaphore;
	private ExecutorService executorService; 
	
	public static ThreadPool getThreadPool(){
		ThreadPool pool = new ThreadPool();
		pool.semaphore = new Semaphore(pool.submitLimit, true);
		pool.executorService = Executors.newFixedThreadPool(pool.threadLimit); 
		return pool;
	}
	
	public static ThreadPool getThreadPool(int threadLimit,int submitLimit){
		ThreadPool pool = new ThreadPool();
		pool.threadLimit = threadLimit;
		pool.submitLimit = submitLimit;
		pool.semaphore = new Semaphore(pool.submitLimit, true);
		pool.executorService = Executors.newFixedThreadPool(pool.threadLimit); 
		return pool;
	}
	/**
	 * 启动线程，若是达到限制则主线程睡眠2秒
	 * @param t
	 */
	public void startThread(Runnable r){
		try {
			while (!semaphore.tryAcquire(500, TimeUnit.MILLISECONDS)) {
				Thread.sleep(2000);
			}
			executorService.submit(r);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	/**
	 * 关闭线程
	 */
	public void closeThread(){
		semaphore.release();
	}
	
	public void shutdown(){
		executorService.shutdown();
	}
	
	public boolean isShutdown(){
		return executorService.isShutdown();
	}
}
