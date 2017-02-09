package com.yulore.bigdata.data_processor.dto;

import java.io.File;

public class TaskFile {
	private File inputDir;
	private File pendingFile;
	//0表示正常，1表示需要结束
	private int status;
	
	public TaskFile(File inputDir,File pendingFile, int status) {
		this.inputDir = inputDir;
		this.pendingFile = pendingFile;
		this.status = status;
	}
	
	public File getInputDir() {
		return inputDir;
	}
	public void setInputDir(File inputDir) {
		this.inputDir = inputDir;
	}
	public File getPendingFile() {
		return pendingFile;
	}
	public void setPendingFile(File pendingFile) {
		this.pendingFile = pendingFile;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
}
