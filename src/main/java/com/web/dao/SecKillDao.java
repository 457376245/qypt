package com.web.dao;

import com.web.model.SecKill;
import com.web.model.SecKillActivity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository("com.web.dao.SecKillDao")
public interface SecKillDao {
	
	List<SecKill> querySecKill(Map<String, Object> param)throws Exception;
	void insertSeckill(SecKill secKill)throws Exception;
    void deleteSeckill(Map<String, Object> param)throws Exception;
    Integer queryIsExist(Map<String, Object> param)throws Exception;
    List<Map> queryIsAllSet(Long parentId)throws Exception;
    Integer updateSeckill(SecKill secKill);
    Integer queryActivityList(Map<String, Object> param);
}
