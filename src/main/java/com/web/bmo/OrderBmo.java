package com.web.bmo;

import java.util.List;
import java.util.Map;

/**
 * 创建人：yibo
 * 类描述：订单处理接口
 * 创建时间：2020年4月4日下午9:47:11
 */
public interface OrderBmo {
	
	void updateOrder(Map<String,Object> paramMap) throws Exception;
    
    List<Map<String,Object>> getCoOrderList(Map<String, Object> paramMap);
}
