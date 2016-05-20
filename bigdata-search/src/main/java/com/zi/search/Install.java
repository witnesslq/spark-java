package com.zi.search;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import com.zi.search.constants.PropertyConstants;
import com.zi.search.entity.EsAndHbaseData;
import com.zi.search.service.HbaseServer;
import com.zi.search.service.SearchService;
import com.zi.search.service.impl.HbaseServerImpl;
import com.zi.search.service.impl.SearchServiceImpl;
import com.zi.search.utils.MysqlUtils;

public class Install {
	private static final Log logger = LogFactory.getLog(Install.class);
	@Test
	public void install(){
		SearchService searchService = new SearchServiceImpl("question","bank");
		HbaseServer hbaseServer = new HbaseServerImpl();
		//1.获取数据库中的记录
		int count = MysqlUtils.getCount();
		if (count == 0)
			return;
		int size = count / PropertyConstants.COUNT;
        if (count % PropertyConstants.COUNT != 0)
            size += 1;
        for (int i = 0; i < size; i++) {
            //2.获取数据
        	EsAndHbaseData esAndHbase = MysqlUtils.getData(i*PropertyConstants.COUNT, PropertyConstants.COUNT);
        	//3.导入数据到es中
            Map<String, String> dataMap = esAndHbase.getDataMap();
            if(dataMap!=null && dataMap.size() != 0){
            	searchService.createDataBatch("question", "bank", dataMap);
            }
            //4.导入数据到hbase中
            Map<String, List<String>> hbaseMap = esAndHbase.getHbaseMap();
            if(hbaseMap.size() != 0){
            	hbaseServer.insertData(hbaseMap);
            }
            logger.info("第 "+i+" 批 ： "+i*PropertyConstants.COUNT+" ~~~~ "+(i+1)*PropertyConstants.COUNT);
        }
	}
}
