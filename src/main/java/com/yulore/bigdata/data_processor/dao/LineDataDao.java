package com.yulore.bigdata.data_processor.dao;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public interface LineDataDao{
	public List<String> loadAll(String fileName) throws IOException;
	public void save(String fileName, Collection<String> data) throws IOException;
}
