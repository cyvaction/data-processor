package com.yulore.bigdata.data_processor.common.config;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yulore.bigdata.data_processor.common.ApplicationContext;
import com.yulore.bigdata.data_processor.common.constant.CharSet;

public class ProcessorConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProcessorConfig.class);
	private String outputPath;
	//true表示去重，false表示不去重
	private boolean removeRepeat;
	private String pluginDir;
	private String pluginFilterRegex;
	private String fileFilterClassName;
	private List<String> processorClassNameList;
	public ProcessorConfig(String configFileName) {
		try {
			Document doc = Jsoup.parse(new File(configFileName), CharSet.UTF_8);
			outputPath = doc.select("output").first().attr("dir");
			removeRepeat = Boolean.valueOf(doc.select("remove_repeat").first().text());
			LOGGER.info("是否去重：{}",removeRepeat);
			
			pluginDir = doc.select("plugin").first().attr("dir");
			pluginFilterRegex = doc.select("plugin").first().attr("regex");
			
			processorClassNameList = new ArrayList<>();
			Elements checkerElements = doc.select("processor");
			for (Element element : checkerElements) {
				processorClassNameList.add(element.attr("class"));
			}
			fileFilterClassName = doc.select("file_filter").first().attr("class");
		} catch (IOException e) {
			LOGGER.error("load "+configFileName+" failure",e);
			System.exit(1);
		}
	}
	
	public String getPluginDir() {
		return pluginDir;
	}

	public String getPluginFilterRegex() {
		return pluginFilterRegex;
	}

	public String getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

	public List<String> getProcessorClassNameList() {
		return processorClassNameList;
	}

	public boolean isRemoveRepeat() {
		return removeRepeat;
	}

	public String getFileFilterClassName() {
		return fileFilterClassName;
	}

	public void setFileFilterClassName(String fileFilterClassName) {
		this.fileFilterClassName = fileFilterClassName;
	}
	
	public FileFilter getFileFilter(){
		Object obj = null;
		if (StringUtils.isNotBlank(fileFilterClassName)) {
			try {
				obj = Class.forName(fileFilterClassName,
						false,
						ApplicationContext.getInstance()
								.getResourceClassLoader()).newInstance();
			} catch (InstantiationException | IllegalAccessException
					| ClassNotFoundException e) {
				LOGGER.error("[init filefilter failure!]", e);
				System.exit(1);
			}
		}
		FileFilter fileFilter = null;
		if(obj!=null){
			fileFilter = (FileFilter) obj;
		}
		return fileFilter;
	}
	
}
