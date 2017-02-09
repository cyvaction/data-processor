package com.yulore.bigdata.data_processor.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import com.yulore.bigdata.data_processor.util.file.FileUtil;

/**
 * 自定义类加载器，加载程序以外的类
 * 
 * @author：wanghaibo
 * @creattime：2016年9月9日 下午4:58:49
 * 
 */
public class ResourceClassLoader extends URLClassLoader {

	private String name;

	public static ResourceClassLoader getInstance() {
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder {
		private static final ResourceClassLoader INSTANCE = new ResourceClassLoader();
	}

	public ResourceClassLoader() {
		super(new URL[0]);
	}

	public ResourceClassLoader(String name) {
		super(new URL[0]);
		this.setName(name);
	}

	public ResourceClassLoader(String name, ClassLoader parent) {
		super(new URL[0], parent);
		this.setName(name);
	}

	public ResourceClassLoader newInstance() {
		return new ResourceClassLoader();
	}

	/**
	 * 添加一些class到该classloader环境下
	 * 
	 * @param filename
	 *            文件or文件夹
	 * @param filenameFilter
	 * @throws MalformedURLException
	 * @throws FileNotFoundException
	 */
	public void addURL(String filename, FileFilter fileFilter)
			throws MalformedURLException, FileNotFoundException {
		File targetFile = new File(filename);
		if (!targetFile.exists()) {
			throw new FileNotFoundException(filename);
		}
		List<File> jarFileList = new ArrayList<File>();
		FileUtil.getFiles(jarFileList, targetFile, fileFilter);
		for (File file : jarFileList) {
			URL url = file.toURI().toURL();
			super.addURL(url);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}