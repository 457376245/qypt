package com.web.bmo;

import java.util.Map;

import com.web.model.ProdTelBind;

public interface RightsBmo {
	/**
	 * 用户权益领取校验
	 * @param paramMap
	 * @return
	 */
	public Map<String, Object> rightsGrantPreCheck(Map<String, Object> paramMap);

	/**
	 * 权益领取
	 * @param paramMap
	 * @return
	 */
	public Map<String, Object> rightsGrant(Map<String, Object> paramMap);

	/**
	 * 已领取权益查询
	 * @param paramMap
	 * @return
	 */
	public Map<String, Object> rightsInstQry(Map<String, Object> paramMap);
	
	/**
	 * 权益详情查询
	 * @param paramMap
	 * @return
	 */
	public Map<String, Object> rightsDetail(Map<String, Object> paramMap);
	
	/**
	 * 获取权益绑定关系
	 * @param paramMap
	 * @return
	 */
	ProdTelBind getProdTelBind(Map<String,Object> paramMap);
}
