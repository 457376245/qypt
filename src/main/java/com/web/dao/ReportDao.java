package com.web.dao;

import com.web.model.CoActivity;
import com.web.model.SecKill;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository("com.web.dao.ReportDao")
public interface ReportDao {

    List<Map<String,Object>> querySecKillReport(Map<String,Object> param)throws Exception;

    List<Map<String,Object>> queryPrizeReport(Long activityId)throws Exception;

    List<Map<String,Object>> queryLotteryReport(Map<String,Object> param)throws Exception;

    List<Map<String,Object>> querySecKillProReport(Map<String,Object> param)throws Exception;

    List<Map<String,Object>> queryUserPrivilege(Map<String,Object> param)throws Exception;

    List<Map<String,Object>> queryHasPrivilegeUser(Map<String,Object> param)throws Exception;

    List<Map<String,Object>> queryNewMemberByTime(Map<String,Object> param)throws Exception;

    List<Map<String,Object>> queryNewMemberByArea(Map<String,Object> param)throws Exception;

    List<Map<String,Object>> queryNewMemberDetail(Map<String,Object> param)throws Exception;
}
