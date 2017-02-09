package com.yulore.bigdata.data_processor.bean;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yulore.bigdata.data_processor.util.file.FileUtil;

public class LineData{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LineData.class);
	
	private File inputDir;//文件所在目录
	private File inputFile;//正在处理的文件
	private String line;//文件行
	private File outputDir;//输出文件的目录
	private File outputFile;//输出文件
	private String lineKey;//数据key,用来判断数据是否相等
	
	
	private LineData(){
		
	}
	public LineData(File inputDir, File inputFile, String line, File outputDir,
			File outputFile) {
		this.inputDir = inputDir;
		this.inputFile = inputFile;
		this.line = line;
		this.lineKey = this.line;
		this.outputDir = outputDir;
		this.outputFile = outputFile;
		if(outputFile==null){
			//不提供输出文件时,默认输出的文件与输入文件目录结构一致
			if(inputDir!=null&&inputFile!=null&&outputDir!=null){
				if(outputDir!=null){
					this.outputFile = getDefaultOutputFile(inputDir, inputFile, outputDir);
				}
			}
		}else{
			this.outputDir = outputDir;
		}
	}
	
	public static File getDefaultOutputFile(File inputDir, File inputFile,File outputDir){
		String outputFileRelativePath = FileUtil.getRelativePath(inputDir, inputFile);
		if(StringUtils.isNotBlank(outputFileRelativePath)){
			return new File(outputDir.getPath()+ File.separator+inputDir.getName()+File.separator+outputFileRelativePath);
		}
		return null;
	}
	public File getInputDir() {
		return inputDir;
	}
	public File getInputFile() {
		return inputFile;
	}
	public String getLine() {
		return line;
	}
	public void setLine(String line) {
		this.line = line;
		this.lineKey = line;
	}
	public File getOutputDir() {
		return outputDir;
	}
	public File getOutputFile() {
		return outputFile;
	}
	public String getLineKey() {
		return lineKey;
	}
	public void setLineKey(String lineKey) {
		this.lineKey = lineKey;
	}
	public void setOutputFile(File outputFile){
		this.outputFile = outputFile;
		if(inputFile.getPath().equals(outputFile.getPath())){
			LOGGER.error("[输入文件和输出文件路径不能相同？ inputfile="+inputFile.getPath()+"]");
			System.exit(1);
		}
	}
	
	public LineData clone(){
		LineData lineData = new LineData();
		lineData.inputDir = this.inputDir;
		lineData.inputFile = this.inputFile;
		lineData.line = this.line;
		lineData.lineKey = this.lineKey;
		lineData.outputDir = this.outputDir;
		lineData.outputFile = this.outputFile;
		return lineData;
	}
	@Override
	public int hashCode() {
		return lineKey.hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if(null==obj||!(obj instanceof LineData)){
			return false;
		}
		return this.lineKey.equals(((LineData)obj).getLineKey());
	}
	
	
}
