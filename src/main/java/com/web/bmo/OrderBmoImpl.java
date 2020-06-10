package com.web.bmo;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.dao.LogWarningDao;
import com.web.dao.OrderDao;
import com.web.model.CoOrder;
import com.web.model.CoSubOrder;
import com.web.model.LogWarning;
import com.web.thirdinterface.ThirdBSSInterface;
import com.web.util.MapUtil;
import com.web.util.StringUtil;
import com.web.util.common.Log;

@Service("com.web.bmo.OrderBmoImpl")
public class OrderBmoImpl implements OrderBmo {
	
	protected final Log log = Log.getLog(getClass());

	@Autowired
    private OrderDao orderDao;
	
	@Resource
	private ThirdBSSInterface thirdBSSInterface;
	
	@Autowired
    private LogWarningDao logWarningDao;  
	
	@Override
	public void updateOrder(Map<String,Object> paramMap) throws Exception {		
		try{
			//更新订单表状态
			Integer orderId = MapUtil.asInt(paramMap, "orderId");
			String orderStatus = MapUtil.asStr(paramMap, "orderStatus");
			CoOrder coOrder = new CoOrder();
			coOrder.setOrderStatus(orderStatus);
			coOrder.setOrderId(orderId);
			orderDao.updateOrder(coOrder);
			
			CoSubOrder coSubOrder = new CoSubOrder();
			String rightsInstNbr = MapUtil.asStr(paramMap, "rightsInstNbr");
			if(!StringUtil.isEmptyStr(rightsInstNbr)){
				coSubOrder.setoSnoCode(rightsInstNbr);
			}
			coSubOrder.setOrderId(orderId);
			coSubOrder.setGroupType(MapUtil.asStr(paramMap, "groupType"));
			String prodSupplier = MapUtil.asStr(paramMap, "prodSupplier");
			String custOrderId = MapUtil.asStr(paramMap, "custOrderId");
			String custOrderNbr = MapUtil.asStr(paramMap, "custOrderNbr");
			if(!StringUtil.isEmptyStr(prodSupplier)){
				coSubOrder.setProdSupplier(prodSupplier);
			}
			if(!StringUtil.isEmptyStr(custOrderId)){
				coSubOrder.setOlId(custOrderId);
			}
			if(!StringUtil.isEmptyStr(custOrderNbr)){
				coSubOrder.setOlNbr(custOrderNbr);
			}
			orderDao.updateSubOrder(coSubOrder);
			
			if("4".equals(orderStatus)){//成功
				LogWarning logWarning = new LogWarning();
	        	logWarning.setMemberId(Long.parseLong(MapUtil.asStr(paramMap,"memberId")));
	        	logWarning.setOrderId(Long.parseLong(MapUtil.asStr(paramMap,"orderId")));
	        	logWarning.setOptResult("101");
	        	logWarningDao.deleteLogWarning(logWarning);
			}
		}catch (Exception e) {
            log.error("更新权益领取时异常:",e);
            throw new RuntimeException(e.getMessage());
        }
	}

	@Override
	public List<Map<String, Object>> getCoOrderList(Map<String, Object> paramMap) {
		try{
			return orderDao.getCoOrderList(paramMap);
		}catch(Exception ex){
			log.error("获取待领取权益订单异常：", ex);
		}
		return null;
	}

}
