package com.yulore.bigdata.data_processor.dto;

import java.util.List;

/**
 * 任务文件组
 * @author：wanghaibo 
 * @creattime：2015-11-6 下午3:38:12 
 * 
 */  

public class TaskFileGroup {
	private String groupName;
	private List<TaskFile> taskFileList;
	//0表示正常，1表示需要结束
	private int status;
	
	public TaskFileGroup() {
	}
	
	public TaskFileGroup(String groupName, List<TaskFile> taskFileList,
			int status) {
		this.groupName = groupName;
		this.taskFileList = taskFileList;
		this.status = status;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public List<TaskFile> getTaskFileList() {
		return taskFileList;
	}
	public void setTaskFileList(List<TaskFile> taskFileList) {
		this.taskFileList = taskFileList;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public void addTaskFile(TaskFile taskFile){
		taskFileList.add(taskFile);
	}
}
