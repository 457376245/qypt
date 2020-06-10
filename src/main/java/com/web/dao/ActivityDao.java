package com.web.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.web.model.CoActLuck;
import com.web.model.CoActPrize;
import com.web.model.CoActRule;
import com.web.model.CoActRuleData;
import com.web.model.CoActivity;
import com.web.model.CoActivityImg;
import com.web.model.CoPrize;
@Repository("com.web.dao.ActivityDao")
public interface ActivityDao {
    /**
     * 查询活动列表
     * @param param
     * @return
     * @throws Exception
     */
    List<CoActivity> queryActivityList(Map param)throws Exception;
    /**
     * 查询活动规则
     * @param param
     * @return
     * @throws Exception
     */
    List<CoActRule> queryActRules(Map param)throws Exception;

    /**
     * 查询活动banner图片
     * @param param
     * @return
     * @throws Exception
     */
    List<CoActivityImg> getCoActivityImgList(Map param)throws Exception;
    /**
     * 查询参与人数
     * @param paramMap
     * @return
     * @throws Exception
     */
    Integer queryActUserCount(Map<String,Object> paramMap)throws Exception;

    /**
     * 查询秒杀的参与人数
     * @param activityId
     * @return
     * @throws Exception
     */
    Integer querySekillUserCount(Long activityId)throws Exception;

    /**
     * 查询活动的商品购买数量
     * @param activityId
     * @return
     * @throws Exception
     */
    Integer queryProBuyCount(Long activityId)throws Exception;

    /**
     * 插入规则
     * @param coActRule
     * @return
     * @throws Exception
     */
    void insertActRule(CoActRule coActRule)throws Exception;

    /**
     * 插入活动图片
     * @param coActivityImg
     * @throws Exception
     */
    void insertActImg(CoActivityImg coActivityImg)throws Exception;

    /**
     * 插入活动
     * @param coActivity
     * @throws Exception
     */
    void insertActivity(CoActivity coActivity)throws Exception;
    
    /**
     * 入活动规则
     * @param coActRule
     * @throws Exception
     */
    void insertActRules(CoActRule coActRule)throws Exception;

    /**
     * 入奖品表
     * @param coPrize
     * @throws Exception
     */
    void insertCoPrize(CoPrize coPrize)throws Exception;
    
    /**
     * 入抽奖类活动
     * @param coActLuck
     * @throws Exception
     */
    void insertCoActLuck(CoActLuck coActLuck)throws Exception;
    
    /**
     * 入活动奖品
     * @param coActPrize
     * @throws Exception
     */
    void insertCoActPrize(CoActPrize coActPrize)throws Exception;    

    /**
     * 查询奖品列表
     * @param activityId
     * @return
     * @throws Exception
     */
	List<Map<String, Object>> queryPrizeList(Long activityId) throws Exception;
	
	/**
	 * 删除转盘活动
	 * @param activityId
	 * @throws Exception
	 */
	void deleteActivity(Long activityId)throws Exception;
	
	/**
	 * 删除活动规则
	 * @param activityId
	 * @throws Exception
	 */
	void deleteActRules(Map<String,Object> paramMap)throws Exception;
	
	/**
	 * 删除活动奖品
	 * @param luckId
	 * @throws Exception
	 */
	void deleteCoActPrize(Long luckId)throws Exception; 
	
	/**
	 * 删除奖品
	 * @param prizeId
	 * @throws Exception
	 */
	void deleteCoPrize(Long prizeId)throws Exception;
	
	/**
	 * 删除抽奖类活动
	 * @param paramMap
	 * @throws Exception
	 */
    void deleteCoActLuck(Map<String,Object> paramMap)throws Exception;

    /**
     * 删除活动图片
     * @param paramMap
     * @throws Exception
     */
    void deleteActImg(Map<String,Object> paramMap)throws Exception;

    /**
     * 更新规则
     * @param coActRule
     * @throws Exception
     */
    void updateActRule(CoActRule coActRule)throws Exception;

    /**
     * 更新活动图片
     * @param coActivityImg
     * @throws Exception
     */
    void updateActImg(CoActivityImg coActivityImg)throws Exception;

    /**
     * 更新活动
     * @param coActivity
     * @throws Exception
     */
    void updateActivity(CoActivity coActivity)throws Exception;
    
    /**
     * 更新活动规则
     * @param coActRule
     * @throws Exception
     */
    void updateActRules(CoActRule coActRule)throws Exception;
    
    /**
     * 更新奖品表
     * @param coPrize
     * @throws Exception
     */
    void updateCoPrize(CoPrize coPrize)throws Exception;
    
    /**
     *更新抽奖类活动
     * @param coActLuck
     * @throws Exception
     */
    void updateCoActLuck(CoActLuck coActLuck)throws Exception;
    
    /**
     * 更新活动奖品
     * @param coActPrize
     * @throws Exception
     */
    void updateCoActPrize(CoActPrize coActPrize)throws Exception;

    /**
     * 根据子活动的Code查询父节点的活动Code
     * @param param activityCd 子活动Code
     * @return 父活动Code
     */
    String findParentActivitycd(Map param);

    /**
     * 查询规则的区域数据
     * @param param
     * @return
     * @throws Exception
     */
    List<CoActRuleData> queryActRuleData(Map param)throws Exception;

    /**
     * 插入区域的规则数据
     * @param coActRuleData
     * @throws Exception
     */
    void insertActRuleData(CoActRuleData coActRuleData)throws Exception;

    /**
     * 删除区域的规则数据
     * @param expId
     * @throws Exception
     */
    void deleteActRuleData(long expId)throws Exception;
    
    /**
     * 更新区域的规则数据
     * @param coActRuleData
     * @throws Exception
     */
    void updateActRuleData(CoActRuleData coActRuleData)throws Exception;
}
