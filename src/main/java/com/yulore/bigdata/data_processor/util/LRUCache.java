package com.yulore.bigdata.data_processor.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 数据缓存，ps：删除最近最少使用的项
 * @author：wanghaibo 
 * @creattime：2015-10-27 上午11:40:40 
 * 
 */  

public class LRUCache<K, V> extends LinkedHashMap<K, V>{
	//缓存的容量 
    private int capacity; 
    private static final long serialVersionUID = 1L; 
    public LRUCache(int capacity){ 
        super(16,0.75f,true); 
        this.capacity=capacity; 
    } 
    //实现LRU的关键方法，如果map里面的元素个数大于了缓存最大容量，则删除链表的顶端元素 
    @Override 
    public boolean removeEldestEntry(Map.Entry<K, V> eldest){  
        return size()>capacity; 
    }   
}
