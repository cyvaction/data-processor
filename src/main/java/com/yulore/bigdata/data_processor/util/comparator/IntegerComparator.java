package com.yulore.bigdata.data_processor.util.comparator;

import java.util.Comparator;

public class IntegerComparator implements Comparator<Integer>{
	private boolean isPositive;
	public IntegerComparator(boolean isPositive) {
		this.isPositive = isPositive;
	}
	@Override
	public int compare(Integer o1, Integer o2) {
		if(isPositive){
			return Integer.compare(o1, o2);
		}else{
			return Integer.compare(o2, o1);
		}
	}
	
}
