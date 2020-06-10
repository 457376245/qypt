package com.web.bmo;

import java.util.Map;

public interface OtherActBmo {
    Map<String, Object> qryOtherActList(Map<String, Object> param) throws Exception;

    Map<String, Object> saveActivities(Map<String, Object> paramsMap) throws Exception;

    Map<String, Object> editActivities(Map<String, Object> paramsMap) throws Exception;

    Map<String, Object>  queryActivity(Map<String,Object> paramsMap)throws Exception;

    Map<String, Object>  deleteAct(Map<String, Object> param)throws Exception;
}
