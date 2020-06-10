package com.web.bmo;

import com.web.common.Constants;
import com.web.thirdinterface.ThirdQYInterface;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 权益商品相关后端逻辑
 *
 * @author
 * @create 2020-04-01
 **/
@Service("com.web.bmo.QyProductBmoImpl")
public class QyProductBmoImpl implements QyProductBmo{
    @Resource
    private ThirdQYInterface thirdInterface;

    @Override
    public Map rightsQry(Map params) {
        Map returnMap=new HashMap();
        returnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_FAIL);
        returnMap.put(Constants.RESULT_MSG_STR, "查询信息失败");
        params.put("pageNum",params.remove("page"));
        params.put("pageSize",params.remove("limit"));
        Map resultMap=thirdInterface.rightsQry(params);
        if (Constants.CODE_SUCC.equals(resultMap.get("resultCode"))) {
            Map obj = (Map) resultMap.get("result");
            returnMap.put("data",obj.get("rightsList"));
            returnMap.put("count",obj.get("totalNum"));
            returnMap.put(Constants.RESULT_CODE_STR, Constants.RESULT_SUCC);
            returnMap.put(Constants.RESULT_MSG_STR, "查询成功");
        }
        return returnMap;
    }
}
