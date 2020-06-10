package com.web.bmo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageInfo;
import com.web.common.Constants;
import com.web.dao.CoMemberDao;
import com.web.dao.ProdTelBindDao;
import com.web.model.CoMember;
import com.web.model.ProdTelBind;
import com.web.thirdinterface.ThirdQYInterface;
import com.web.util.MapUtil;
import com.web.util.common.CommonParams;
import com.web.util.common.Log;

/**
 * 创建人：yibo
 * 类描述：权益操作实现
 * 创建时间：2020年4月4日下午10:09:44
 */
@Service("com.web.bmo.RightsBmoImpl")
public class RightsBmoImpl implements RightsBmo {

	protected final Log log = Log.getLog(getClass());
	
	@Resource
    private ThirdQYInterface thirdInterface;
	
	@Autowired
    private CoMemberDao coMemberDao;
	
	@Autowired
    private ProdTelBindDao prodTelBindDao;
	
	/**
	 * 权益领取校验
	 */
	@Override
	public Map<String, Object> rightsGrantPreCheck(Map<String, Object> paramMap) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put(CommonParams.RESULT_CODE_STR,Constants.CODE_FAIL);
		resultMap.put(CommonParams.RESULT_MSG_STR,"权益领取校验失败");
		try{
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("rightsCode", paramMap.get("rightsCode"));			
	        map.put("objType", paramMap.get("objType"));
	        map.put("objNbr", paramMap.get("objNbr"));
	        map.put("objArea", paramMap.get("objArea"));		        
	        Map<String, Object> rightsMap = thirdInterface.rightsGrantPreCheck(map);
	        if(rightsMap != null && rightsMap.size() > 0){
	        	if(Constants.CODE_SUCC.equals(String.valueOf(rightsMap.get(CommonParams.RESULT_CODE_STR)))){
	        		resultMap.put(CommonParams.RESULT_CODE_STR,Constants.CODE_SUCC);
        			resultMap.put(CommonParams.RESULT_MSG_STR,"权益领取校验成功");
	        	}else{
        			resultMap.put(CommonParams.RESULT_MSG_STR,rightsMap.get(CommonParams.RESULT_MSG_STR));
	        	}
	        }
	        log.error("rightsGrantPreCheck：" + rightsMap);
		}catch(Exception ex){
			resultMap.put(CommonParams.RESULT_CODE_STR,Constants.CODE_ANALYSIS);
			resultMap.put(CommonParams.RESULT_MSG_STR,"权益领取校验异常");
		}
		return resultMap;
	}

	/**
	 * 用户权益领取
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> rightsGrant(Map<String, Object> paramMap) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put(CommonParams.RESULT_CODE_STR,Constants.CODE_FAIL);
		resultMap.put(CommonParams.RESULT_MSG_STR,"用户权益领取失败");
		try{
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("rightsCode", paramMap.get("rightsCode"));			
			map.put("activityCode", paramMap.get("activityCode"));
	        map.put("objType", paramMap.get("objType"));
	        map.put("objNbr", paramMap.get("objNbr"));
	        map.put("objArea", paramMap.get("objArea"));	        
	        Map<String, Object> rightsMap = thirdInterface.rightsGrant(map);
	        if(rightsMap != null && rightsMap.size() > 0){
	        	if(Constants.CODE_SUCC.equals(String.valueOf(rightsMap.get(CommonParams.RESULT_CODE_STR)))){
	        		Map<String, Object> dataMap = (Map<String, Object>) rightsMap.get("result");
	        		if(dataMap != null && dataMap.size() > 0){
	        			resultMap.put("success", dataMap.get("success"));
	        			resultMap.put("rightsInstList", dataMap.get("rightsInstList"));
	        			resultMap.put(CommonParams.RESULT_CODE_STR,Constants.CODE_SUCC);
	        			resultMap.put(CommonParams.RESULT_MSG_STR,"用户权益领取成功");
	        		}
	        	}else{
        			resultMap.put(CommonParams.RESULT_MSG_STR,rightsMap.get(CommonParams.RESULT_MSG_STR));
	        	}
	        }
	        log.error("rightsGrant：" + rightsMap);
		}catch(Exception ex){
			resultMap.put(CommonParams.RESULT_CODE_STR,Constants.CODE_ANALYSIS);
			resultMap.put(CommonParams.RESULT_MSG_STR,"用户权益领取异常");
		}
		return resultMap;
	}

	/**
	 * 已领权益查询
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> rightsInstQry(Map<String, Object> paramMap) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put(CommonParams.RESULT_CODE_STR,Constants.CODE_FAIL);
		resultMap.put(CommonParams.RESULT_MSG_STR,"用户已领权益查询失败");
		try{
			String bindingNbr = MapUtil.asStr(paramMap, "objNbr");
			CoMember coMemberParam = new CoMember();
            coMemberParam.setMemberPhone(bindingNbr);
            Map<String,Object> coMember = coMemberDao.getCoMember(coMemberParam);
            if(coMember == null || coMember.size() <= 0){
        		resultMap.put(CommonParams.RESULT_MSG_STR,"用户不是会员，无法查询");
        		return resultMap;
            }
			Map<String, Object> map = new HashMap<String, Object>();
	        map.put("objType", "2");
	        map.put("objNbr", paramMap.get("objNbr"));
	        map.put("objArea", coMember.get("AREA_ID"));
		    map.put("rightsInstNbr", paramMap.get("rightsInstNbr"));
		    map.put("pageNum", paramMap.get("page"));
		    map.put("pageSize", paramMap.get("limit"));		    
	        Map<String, Object> rightsMap = thirdInterface.rightsInstQry(map);
	        if(rightsMap != null && rightsMap.size() > 0){
	        	if(Constants.CODE_SUCC.equals(String.valueOf(rightsMap.get(CommonParams.RESULT_CODE_STR)))){
	        		Map<String, Object> dataMap = (Map<String, Object>) rightsMap.get("result");
	        		if(dataMap != null && dataMap.size() > 0){
	        	        PageInfo<Map<String, Object>> info = new PageInfo<>((List<Map<String,Object>>)dataMap.get("rightsInstList"));
	        	        resultMap.put("data",info.getList());
	        	        resultMap.put("count",info.getTotal());
	        			resultMap.put(CommonParams.RESULT_CODE_STR,Constants.CODE_SUCC);
	        			resultMap.put(CommonParams.RESULT_MSG_STR,"用户已领权益查询成功");
	        		}
	        	}
	        }
	        log.error("rightsInstQry：" + rightsMap);
		}catch(Exception ex){
			resultMap.put(CommonParams.RESULT_CODE_STR,Constants.CODE_ANALYSIS);
			resultMap.put(CommonParams.RESULT_MSG_STR,"用户已领权益查询异常");
		}
		return resultMap;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> rightsDetail(Map<String, Object> paramMap) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put(CommonParams.RESULT_CODE_STR,Constants.CODE_FAIL);
		resultMap.put(CommonParams.RESULT_MSG_STR,"权益查询失败");
		try{			
	        Map<String, Object> rightsMap = thirdInterface.rightsDetail(String.valueOf(paramMap.get("rightsCode")));
	        if(rightsMap != null && rightsMap.size() > 0){
	        	if(Constants.CODE_SUCC.equals(String.valueOf(rightsMap.get(CommonParams.RESULT_CODE_STR)))){
	        		Map<String, Object> dataMap = (Map<String, Object>) rightsMap.get("result");
	        		if(dataMap != null && dataMap.size() > 0){
	        			resultMap.putAll(dataMap);
	        			resultMap.put(CommonParams.RESULT_CODE_STR,Constants.CODE_SUCC);
	        			resultMap.put(CommonParams.RESULT_MSG_STR,"权益查询成功");
	        		}
	        	}
	        }
	        log.error("rightsQry：" + rightsMap);
		}catch(Exception ex){
			resultMap.put(CommonParams.RESULT_CODE_STR,Constants.CODE_ANALYSIS);
			resultMap.put(CommonParams.RESULT_MSG_STR,"权益查询异常");
		}
		return resultMap;	
	}

	@Override
	public ProdTelBind getProdTelBind(Map<String, Object> paramMap) {
		try{
			List<ProdTelBind> prodTelBindList = prodTelBindDao.getProdTelBindList(paramMap);
			if(prodTelBindList != null && prodTelBindList.size() > 0){
				return prodTelBindList.get(0);
			}
		}catch(Exception ex){
			log.error("查询权益销售品关系失败：", ex);
		}
		return null;
	}
}
