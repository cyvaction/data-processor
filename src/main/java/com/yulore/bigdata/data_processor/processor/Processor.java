package com.yulore.bigdata.data_processor.processor;


/**
 * 
 * @author：wanghaibo 
 * @creattime：2015-10-22 上午10:40:52 
 * 
 */  

public interface Processor<I,O> {
	public O process(I input);
}
