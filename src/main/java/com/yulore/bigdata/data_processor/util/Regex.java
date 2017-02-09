package com.yulore.bigdata.data_processor.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * 正则工具类
 * @author admin
 *
 */
public class Regex {
	
	//字母或者数字的字符串
	public static final String REG_LETTER_OR_NUM = "^[A-Za-z0-9]+$";

	//字母
	public static final String REG_LETTER = "^[A-Za-z]+$";
	/**
	 * 读取所有匹配结果
	 * @param content
	 * @param regex
	 * @return
	 */
	public static List<String> regexRead(String content,String regex){
		Pattern p=Pattern.compile(regex);
		Matcher m=p.matcher(content);
		List<String> list = new ArrayList<String>();
		while(m.find()){
			list.add(m.group());
		}
		return list;
	}
	
	
	/**
	 * 读取所有匹配结果并生成字符串
	 * @param content
	 * @param regex
	 * @return
	 */
	public static String regexReadToStr(String content,String regex){
		List<String> list = regexRead(content,regex);
		StringBuffer sb = new StringBuffer();
		for (String string : list) {
			sb.append(string);
		}
		return sb.toString();
	}
	
	
	/**
	 * 读取第一次匹配成功的字符串
	 * @param content
	 * @param regex
	 * @return
	 */
	public static String regexReadOne(String content,String regex){
		return regexReadOne(content,regex,1);
	}
	/**
	 * 匹配一个字符串，并返回第几个匹配成功的子串
	 * @param content
	 * @param regex
	 * @param index
	 * @return 默认返回空字符串
	 */
	public static String regexReadOne(String content,String regex,int index){
		Pattern p=Pattern.compile(regex);
		Matcher m=p.matcher(content);
		String result = "";
		while(m.find()){
			result = m.group(index);
		}
		return result;
	}
	
	/**
	 * 匹配一个字符串，并返回第几个匹配成功的子串
	 * @param content
	 * @param regex
	 * @param index
	 * @return
	 */
	public static List<String> regexReadList(String content,String regex,int index){
		Pattern p=Pattern.compile(regex);
		Matcher m=p.matcher(content);
		List<String> list = new ArrayList<String>();
		String result = "";
		while(m.find()){
			result = m.group(index);
			list.add(result);
		}
		return list;
	}
	
	/**
	 * 检查字符串是否匹配某个正则
	 * @param regex
	 * @param input
	 * @return
	 */
	public static boolean check(String regex, String input) {
		if (StringUtils.isNotEmpty(regex) && StringUtils.isNotEmpty(input)) {
			return Pattern.matches(regex, input);
		}
		return false;
	}

	public static void main(String[] args) {
		System.out.println(Regex.check("\\d{3,4}-\\d{7,8}", "021-111111"));
	}
}
