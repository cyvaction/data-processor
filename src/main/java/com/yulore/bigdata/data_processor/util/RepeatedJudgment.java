package com.yulore.bigdata.data_processor.util;
import java.util.BitSet;

/**
 * 去重工具类
 * @author admin
 *
 */
public class RepeatedJudgment {
	BitSet onebittree=null,twobittree=null,threebittree=null;
	int size=268435455;
	public RepeatedJudgment() {
		onebittree = new BitSet(size);
		twobittree = new BitSet(size);
		threebittree = new BitSet(size);
	}
	public RepeatedJudgment(int size) {
		onebittree = new BitSet(size);
		twobittree = new BitSet(size);
		threebittree = new BitSet(size);
	}

	public synchronized boolean isRepeated(String source) {
		int h1 = hash1(source);
		int h2 = hash2(source);
		int h3 = hash3(source);
		if (onebittree.get(h1) && twobittree.get(h2) && threebittree.get(h3))
			return true;
		return false;
	}

	/**
	 * bitset不安全
	 * @param source
	 * @return
	 */
	public synchronized String setRepeated(String source) {
		int h1 = hash1(source);
		int h2 = hash2(source);
		int h3 = hash3(source);
		onebittree.set(h1);
		twobittree.set(h2);
		threebittree.set(h3);
		return h1+":"+":"+h2+":"+h3;
		
	}
	
	/**
	 * true表示重复,false表示不重复
	 * @param source
	 * @return
	 */
	public synchronized boolean  isAndSetRepeated(String source){
		if(!isRepeated(source)){
			setRepeated(source);
			return false;
		}
		return true;
	}

	private  int hash1(String str) {
		int b = 378551;
		int a = 63689;
		int hash = 0;

		for (int i = 0; i < str.length(); i++) {
			hash = hash * a + str.charAt(i);
			a = a * b;
		}
		return getEight((hash&0x7FFFFFFF));
	}

	private  int getEight(int str) {
		return 0xfffffff & str;
	}

	private  int hash2(String key) {
		int hash = 0;
		int i;
		for (i = 0; i < key.length(); ++i)
			hash = 33 * hash + key.charAt(i);
		return getEight(hash);
	}
	
	private  int hash3(String str) {
		int BitsInUnsignedInt = (int) (4 * 8);
		int ThreeQuarters = (int) ((BitsInUnsignedInt * 3) / 4);
		int OneEighth = (int) (BitsInUnsignedInt / 8);
		int HighBits = (int) (0xFFFFFFFF)<<(BitsInUnsignedInt - OneEighth);
		int hash = 0;
		int test = 0;
		for (int i = 0; i < str.length(); i++) {
			hash = (hash << OneEighth) + str.charAt(i);
			if ((test = hash&HighBits) != 0) {
				hash = ((hash ^ (test >> ThreeQuarters)) & (~HighBits));
			}
		}
		return getEight(hash);
	}

	public static void main(String[] args) {
		RepeatedJudgment rj=new RepeatedJudgment();
		System.out.println(rj.setRepeated("http://www.baidu.com/1"));
		System.out.println(rj.setRepeated("http://www.baidu.com/3"));
		System.out.println(rj.setRepeated("http://www.baidu.com/4"));
		System.out.println(rj.isRepeated("http://www.baidu.com/1"));
		System.out.println(rj.isRepeated("http://www.baidu.com/9"));
	}
	
}
