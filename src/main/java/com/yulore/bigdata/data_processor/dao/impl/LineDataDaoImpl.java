package com.yulore.bigdata.data_processor.dao.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.yulore.bigdata.data_processor.common.constant.CharSet;
import com.yulore.bigdata.data_processor.common.constant.Constant;
import com.yulore.bigdata.data_processor.dao.LineDataDao;
import com.yulore.bigdata.data_processor.processor.Processor;
import com.yulore.bigdata.data_processor.util.file.FileUtil;

public class LineDataDaoImpl implements LineDataDao{

	@Override
	public List<String> loadAll(String fileName) throws IOException {
		final List<String> dataList = new ArrayList<>();
		FileUtil.read(fileName,CharSet.UTF_8, new Processor<String,String>() {
			@Override
			public String process(String target) {
				dataList.add(target);
				return target;
			}
		});
		return dataList;
	}

	@Override
	public void save(String fileName, Collection<String> data) throws IOException{
		FileUtil.write(fileName,CharSet.UTF_8,data, new Processor<String,String>() {
			@Override
			public String process(String target) {
				return target+Constant.LINE_SEPARATOR;
			}
		},true);
	}
}
