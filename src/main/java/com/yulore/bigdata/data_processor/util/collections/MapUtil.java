package com.yulore.bigdata.data_processor.util.collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.TreeMultimap;

public class MapUtil {
	/**
	 * map排序
	 * @param comparator
	 * @param map
	 * @param isPositive true表示正序，false表示反序
	 * @return
	 */
	public static <K,V> Map<K, V> sortMap(Comparator<Entry<K, V>> comparator,Map<K, V> srcMap,boolean isPositive ) {
		if (srcMap == null || srcMap.isEmpty()) {
			return null;
		}
		Map<K, V> sortedMap = new LinkedHashMap<K, V>();
		List<Entry<K, V>> entryList = new ArrayList<Entry<K, V>>(srcMap.entrySet());
		Collections.sort(entryList, comparator);
		if(isPositive){
			Iterator<Entry<K, V>> iter = entryList.iterator();
			Entry<K, V> tmpEntry = null;
			while (iter.hasNext()) {
				tmpEntry = iter.next();
				sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
			}
		}else{
			for (int i = entryList.size()-1; i >= 0; i--) {
				sortedMap.put(entryList.get(i).getKey(), entryList.get(i).getValue());
			}
		}
		
		return sortedMap;
	}
	
	/**
	 * 按key排列
	 * @param map
	 * @param isPositive
	 * @return
	 */
	public static <K extends Comparable<K>,V> Map<K,V> sortMapByKey(Map<K,V> srcMap,boolean isPositive ) {
		return sortMap(new Comparator<Entry<K, V>>() {
			@Override
			public int compare(Entry<K, V> o1, Entry<K, V> o2) {
				return o1.getKey().compareTo(o2.getKey());
			}
		}, srcMap, isPositive);
	}
	
	
	/**
	 * 按value排列
	 * @param map
	 * @param isPositive
	 * @return
	 */
	public static <K,V extends Comparable<V>> Map<K,V> sortMapByValue(Map<K,V> srcMap,boolean isPositive ) {
		return sortMap(new Comparator<Entry<K, V>>() {
			@Override
			public int compare(Entry<K, V> o1, Entry<K, V> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		}, srcMap, isPositive);
	}
	
	/**
	 * 有序的Multimap
	 * @param keySort true表示key正序，反之逆序
	 * @param valueSort true表示key正序，反之逆序
	 * @return
	 */
	public static <K extends Comparable<K>,V extends Comparable<V>> TreeMultimap<K,V> getTreeMultimap(final boolean keySort,final boolean valueSort){
		return TreeMultimap
		.create(new Comparator<K>() {
			@Override
			public int compare(K key, K key2) {
				if(keySort){
					return key.compareTo(key2);
				}else{
					return key2.compareTo(key);
				}
			}
		}, new Comparator<V>() {
			@Override
			public int compare(V value, V value2) {
				if(valueSort){
					return value.compareTo(value2);
				}else{
					return value2.compareTo(value);
				}
			}
		});
	}
	
	public static void main(String[] args) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("KFC", 2);
		map.put("WNBA", 4);
		map.put("NBA", 7);
		map.put("CBA", 12);
		map.put("asg", 123);
		Map<String, Integer> resultMap = MapUtil.sortMapByValue(map,false); //按Value进行排序
		for (Entry<String, Integer> entry : resultMap.entrySet()) {
			System.out.println(entry.getKey() + " " + entry.getValue());
		}
		
		resultMap = MapUtil.sortMapByKey(map,false); //按Value进行排序
		for (Entry<String, Integer> entry : resultMap.entrySet()) {
			System.out.println(entry.getKey() + " " + entry.getValue());
		}
	}
}
