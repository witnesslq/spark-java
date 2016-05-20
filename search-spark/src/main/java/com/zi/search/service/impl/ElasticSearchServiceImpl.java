package com.zi.search.service.impl;

import java.io.Serializable;

import com.zi.search.service.ElasticSearchService;
import com.zi.search.utils.ElasticUtils;

public class ElasticSearchServiceImpl implements ElasticSearchService, Serializable {
	private static final long serialVersionUID = 1L;
	@Override
	public void indexRecord(String index, String type, String rowkey, String value) {
		ElasticUtils
		.getClient()
		.prepareIndex()
		.setIndex(index)
		.setType(type)
		.setId(rowkey)
		.setSource(value)
		.execute()
		.actionGet();
	}
}
