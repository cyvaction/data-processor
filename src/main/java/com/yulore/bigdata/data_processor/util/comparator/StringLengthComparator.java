package com.yulore.bigdata.data_processor.util.comparator;

import java.util.Comparator;

public class StringLengthComparator  implements Comparator<String>{
	
	private boolean isPositive;
	public StringLengthComparator(boolean isPositive) {
		this.isPositive = isPositive;
	}
	@Override
	public int compare(String o1, String o2) {
		int result;
		if(isPositive){
			result = Integer.compare(o1.length(), o2.length());
			if(0==result){
				return o1.compareTo(o2);
			}
		}else{
			result = Integer.compare(o2.length(), o1.length());
			if(0==result){
				return o2.compareTo(o1);
			}
		}
		return result;
	}
	
	
}
