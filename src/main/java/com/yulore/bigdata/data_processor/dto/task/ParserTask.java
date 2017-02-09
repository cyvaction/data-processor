package com.yulore.bigdata.data_processor.dto.task;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yulore.bigdata.data_processor.common.ApplicationContext;
import com.yulore.bigdata.data_processor.dto.TaskFile;
import com.yulore.bigdata.data_processor.util.file.FileUtil;
import com.yulore.bigdata.data_processor.util.thread.Task;

public class ParserTask implements Task {
	private static final Logger LOGGER = LoggerFactory.getLogger(ParserTask.class);

	public static final int FINISH_CODE = 1;
	public static final int NORMAL_CODE = 0;

	private File inputDir;
	private BlockingQueue<TaskFile> queue;
	private int threadNum;

	public ParserTask(File inputDir, BlockingQueue<TaskFile> queue,
			int threadNum) {
		this.inputDir = inputDir;
		this.queue = queue;
		this.threadNum = threadNum;
	}

	@Override
	public void before() {
		LOGGER.info("[parse start]" + inputDir.getPath());
	}

	@Override
	public void execute() throws InterruptedException {
		// TODO Auto-generated method stub
		List<File> inputFileList = new ArrayList<>();
		File realInputDir = inputDir;
		FileFilter fileFilter = ApplicationContext.getInstance().getCorrectConfig().getFileFilter();
		if (inputDir.isFile()) {
			if(fileFilter!=null&&!fileFilter.accept(inputDir)){
				return;
			}
			inputFileList.add(inputDir);
			realInputDir = inputDir.getParentFile();
		} else {
			FileUtil.getFiles(inputFileList, inputDir,fileFilter);
		}
		for (int i = 0; i < inputFileList.size(); i++) {
			queue.put(new TaskFile(realInputDir, inputFileList.get(i),
					NORMAL_CODE));
		}
		for (int i = 0; i < threadNum; i++) {
			queue.put(new TaskFile(realInputDir, null, FINISH_CODE));
		}
	}

	@Override
	public void exception(Exception e) {
		LOGGER.error("[parse error]", e);
		System.exit(1);
	}

	@Override
	public void after() {
		LOGGER.info("[parse over]" + inputDir.getPath());
	}

}
