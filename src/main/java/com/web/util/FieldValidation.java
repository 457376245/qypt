package com.web.util;

import org.apache.commons.lang3.StringUtils;

import com.web.common.Constants;

import java.util.HashMap;
import java.util.Map;



/**
 *  请求字段是否为空的验证工具
 */
public class FieldValidation {
	// 同步锁
	private static byte[] lock = new byte[0];
	private static FieldValidation instance = null;

	private FieldValidation() {
	}

	public FieldValidation getInstance() {
		if (null == instance) {
			synchronized (lock) {
				if (null == instance) {
					instance = new FieldValidation();
				}
			}
		}
		return instance;
	}

	/**
	 * @description 字段验证工具
	 * @author hexiaohong
	 * @version V1.0 2015-9-11
	 * @createDate 2015-9-11
	 * @modifyDate hexiaohong 2015-9-11 <BR>
	 * @copyRight 亚信
	 */
	@SuppressWarnings("rawtypes")
	public static Map<String, Object> validation(Map<String, Object> reqMap,String[] fields) {
		Map<String, Object> rspMap = new HashMap<String, Object>();
		rspMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
		rspMap.put(Constants.RESULT_MSG_STR, "校验参数失败");
		
		if (reqMap!=null && !reqMap.isEmpty()) {
			if (null == fields || fields.length == 0) {
				//校验全部参数
				for (Map.Entry entry : reqMap.entrySet()) {
					if (StringUtils.isBlank((String) entry.getValue())) {
						rspMap.put(Constants.RESULT_MSG_STR, "字段["+(String) entry.getKey()+"]不允许为空");
						return rspMap;
					}
				}
			} else {
				//判断指定需要验证的字段
				for (String str : fields) {
					if (StringUtils.isBlank(MapUtil.asStr(reqMap, str))||"null".equals(MapUtil.asStr(reqMap, str))) {
						rspMap.put(Constants.RESULT_MSG_STR,"字段["+str+"]不允许为空");
						return rspMap;
					}
				}
			}
		} else {
			rspMap.put(Constants.RESULT_MSG_STR, "[LOCAL]缺少必要参数");
			return rspMap;
		}
		
		rspMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
		rspMap.put(Constants.RESULT_MSG_STR, "校验通过");
		return rspMap;
	}
	
	
}
