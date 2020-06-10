package com.web.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.web.model.CoRegion;

/**
 * <b>项目名称：</b>qy-conf<br>
 * <b>类名称：</b>com.web.dao.CommonDao.java<br>
 * <b>创建人：</b>WJZ<br>
 * <b>类描述：</b>公共数据<br>
 * <b>创建时间：</b>2020年4月13日-下午4:05:42<br>
 */

@Repository("com.web.dao.CommonDao")
public interface CommonDao {
	
	/**查询公共地市数据*/
	public List<CoRegion> getRegionList(Map<String, Object> paramsMap) throws Exception;
}

