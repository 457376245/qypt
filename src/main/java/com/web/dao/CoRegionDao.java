package com.web.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.web.model.CoRegion;

@Repository("com.web.dao.CoRegionDao")
public interface CoRegionDao {

	List<CoRegion> queryCoRegionList(Map<String,Object> paramsMap);
	
}
