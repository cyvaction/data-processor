package com.yulore.bigdata.data_processor.util.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yulore.bigdata.data_processor.processor.Processor;

public class FileUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);
	
	//文件读写所映射
	public static final Map<String, ReadWriteLock> FILE_LOCK_MAP = new HashMap<>();
	
	public static void read(String filePath,String charset,Processor<String, String> processor) throws IOException{
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), charset));
			String line;
			while((line=reader.readLine())!=null){
				processor.process(line);
			}
		} finally{
			if(reader!=null){
				reader.close();
			}
		}
	}
	
	/**
	 * 输出到文件，不追加
	 * @param fileName
	 * @param charset
	 * @param data
	 * @param processor
	 * @throws IOException
	 */
	public static void write(String filePath,String charset,Collection<String> data,Processor<String,String> processor) throws IOException{
		write(filePath,charset,data,processor,false);
		
	}
	/**
	 * @param fileName
	 * @param charset
	 * @param data
	 * @param processor
	 * @throws IOException
	 */
	public static void write(String filePath,String charset,Collection<String> data,Processor<String,String> processor,boolean append) throws IOException{
		if(data==null||data.size()<1){
			return;
		}
		
		synchronized (FILE_LOCK_MAP) {
			if(!FILE_LOCK_MAP.containsKey(filePath)){
				FILE_LOCK_MAP.put(filePath, new ReentrantReadWriteLock());
			}
		}
		FILE_LOCK_MAP.get(filePath).writeLock().lock();
		createFile(new File(filePath));
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath,append), charset));
			for (String string : data) {
				writer.write(processor.process(string));
			}
		} finally{
			if(writer!=null){
				try {
					writer.flush();
					writer.close();
				} catch (Exception e) {
					LOGGER.error("[write flush or close failure,filepath="+filePath+"]");
				}
			}
			FILE_LOCK_MAP.get(filePath).writeLock().unlock();
		}
		
	}
	
	/**
	 * @param filePath
	 * @param charset
	 * @param line
	 * @param processor
	 * @param append
	 * @throws IOException
	 */
	public static void writeLine(String filePath,String charset,String line,Processor<String,String> processor,boolean append) throws IOException{
		if(line==null){
			return;
		}
		
		synchronized (FILE_LOCK_MAP) {
			if(!FILE_LOCK_MAP.containsKey(filePath)){
				FILE_LOCK_MAP.put(filePath, new ReentrantReadWriteLock());
			}
		}
		FILE_LOCK_MAP.get(filePath).writeLock().lock();
		createFile(new File(filePath));
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath,append), charset));
			writer.write(processor.process(line));
		} finally{
			if(writer!=null){
				try {
					writer.flush();
					writer.close();
				} catch (Exception e) {
					LOGGER.error("[write flush or close failure,filepath="+filePath+"]");
				}
			}
			FILE_LOCK_MAP.get(filePath).writeLock().unlock();
		}
		
	}
	
	
	/**
	 * @param target
	 * @throws IOException
	 */
	public static void createFile(File target) throws IOException{
		if(!target.exists()){
			target.getParentFile().mkdirs();
			target.createNewFile();
		}else if(target.isDirectory()){
			target.createNewFile();
		}
	}
	
	/**
	 * 通过递归得到某一路径下所有的文件
	 * @param fileList
	 * @param root
	 * @param fileFilter 文件过滤器，ps：只针对文件
	 */
	public static void getFiles(List<File> fileList,File root,FileFilter fileFilter) {
		File[] files = root.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				getFiles(fileList,file,fileFilter);
			} else {
				if(fileFilter==null||fileFilter.accept(file)){
					fileList.add(file);
				}
			}
		}
	}
	
	/**
	 * 获取相对路径
	 * @param parentPath
	 * @param path
	 * @return 如果存在相对路径则返回,否则返回null
	 */
	public static String getRelativePath(String parentPath,String path){
		if(StringUtils.isNotBlank(parentPath)&&StringUtils.isNotBlank(path)&&path.startsWith(parentPath)){
			String relativePath = path.substring(parentPath.length());
			if(relativePath.startsWith(File.separator)){
				relativePath = relativePath.substring(1);
			}
			return relativePath;
		}
		return null;
	}
	
	
	/**
	 * 获取相对路径
	 * @param parentFile
	 * @param file
	 * @return
	 */
	public static String getRelativePath(File parentFile,File file){
		if(parentFile==null||file==null){
			return null;
		}
		return getRelativePath(parentFile.getPath(),file.getPath());
	}
	
}
