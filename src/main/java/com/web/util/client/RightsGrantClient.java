package com.web.util.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.web.bmo.LogWarningBmo;
import com.web.bmo.OrderBmo;
import com.web.bmo.RightsBmo;
import com.web.common.Constants;
import com.web.model.ProdTelBind;
import com.web.thirdinterface.ThirdBSSInterface;
import com.web.util.MapUtil;
import com.web.util.StringUtil;
import com.web.util.common.CommonParams;


/**
 * 创建人：yibo
 * 类描述：权益领取定时
 * 创建时间：2020年4月4日下午9:42:49
 */
@Service("com.web.util.client.RightsGrantClient")
public class RightsGrantClient extends Thread {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Resource(name = "com.web.bmo.OrderBmoImpl")
	private OrderBmo orderBmo;	
	
	@Resource(name = "com.web.bmo.RightsBmoImpl")
	private RightsBmo rightsBmo;
	
	@Resource(name = "com.web.bmo.LogWarningBmoImpl")
	private LogWarningBmo logWarningBmo;	
	
	@Resource
    private ThirdBSSInterface thirdBSSInterface;
	
	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		try {
			Map<String,Object> paramMap = new HashMap<String,Object>();			
			Map<String,Object> rightsMap = null;
			Map<String,Object> checkMap = null;
			Map<String,Object> grantMap = null;
			paramMap.put("orderStatus", "2");
			boolean grantFlag = true;
			while (true){
				if(grantFlag){//防止上一次还没领取成功，定时再次发起
					grantFlag = false;
					List<Map<String, Object>> rightsGrantList = orderBmo.getCoOrderList(paramMap);
					if(rightsGrantList != null && rightsGrantList.size() > 0){
						for(Map<String,Object> rightsGrantMap:rightsGrantList){
							rightsMap = new HashMap<String,Object>();
							String productCode = MapUtil.asStr(rightsGrantMap,"PRODUCT_CODE");
							String memberPhone = MapUtil.asStr(rightsGrantMap,"MEMBER_PHONE");
							String areaCode = MapUtil.asStr(rightsGrantMap,"AREA_ID");
							String inCode = MapUtil.asStr(rightsGrantMap,"IN_CODE");
							Long orderId = Long.parseLong(MapUtil.asStr(rightsGrantMap,"ORDER_ID"));
							String productTitle = MapUtil.asStr(rightsGrantMap,"PRODUCT_TITLE");
							String memberId = MapUtil.asStr(rightsGrantMap,"MEMBER_ID");
							rightsMap.put("rightsCode", productCode);
							Map<String,Object> detailMap = rightsBmo.rightsDetail(rightsMap);
							if(Constants.CODE_SUCC.equals(String.valueOf(detailMap.get(CommonParams.RESULT_CODE_STR)))){
								if(detailMap != null && detailMap.size() > 0){
									String rightsSpecCd = String.valueOf(detailMap.get("rightsSpecCd"));//权益分类编码
									/*if("10".equals(rightsSpecCd)){//电信自有权益
										ProdTelBind prodTelBind = rightsBmo.getProdTelBind(rightsMap);
							        	if(prodTelBind != null){
						        			Map<String,Object> params = null;
							        		if("1".equals(prodTelBind.getTelProdType())){//充流量
							        			String telProdId = prodTelBind.getTelProdId();
								        		String offerName = "";
							        			String canExcluse = prodTelBind.getCanExcluse();
							        			boolean submitFlag = true;//默认可受理
							        			if("N".equals(canExcluse)){//不允许互斥
							        				params = new HashMap<String,Object>();
							        				params.put("accNum", memberPhone);
								        			params.put("prodType", "12");
								        			Map<String,Object> offerMap = thirdBSSInterface.oOpOfferInst(params);
								        			if(Constants.CODE_SUCC.equals(MapUtil.asStr(offerMap, CommonParams.RESULT_CODE_STR))){//查询互斥成功
								        				List<Map<String,Object>> existingOffers = (List<Map<String, Object>>) offerMap.get("existingOffers");
								        				if(existingOffers != null && existingOffers.size() > 0){
								        					for(Map<String,Object> existingOffer:existingOffers){
								        						if(telProdId.equals(MapUtil.asStr(existingOffer, "offerId"))){//已开通的业务
								        							offerName = MapUtil.asStr(existingOffer, "offerName");
								        							submitFlag = false;
								        							break;
								        						}
								        					}
								        				}	
								        			}else{
								        				submitFlag = false;
								        			}
							        			}
							        			if(submitFlag){
							        				params = new HashMap<String,Object>();
									        		params.put("telphone", memberPhone);
									        		params.put("offerId", prodTelBind.getTelProdId());
									        		grantMap = thirdBSSInterface.submitCustomerOrder(params);
							        			}else{
							        				Map<String,Object> logMap = new HashMap<String,Object>();
													logMap.putAll(rightsGrantMap);
													if(!StringUtil.isEmptyStr(offerName)){
														logMap.put(CommonParams.RESULT_MSG_STR,"已开通" + offerName + "，无法受理");
							        				}else{
							        					logMap.put(CommonParams.RESULT_MSG_STR,"查询已开通业务失败，无法受理");
							        				}
													try{
														logWarningBmo.insertLogWarning(logMap);
													}catch(Exception e){
														log.error("告警入库异常：", e);
													}
							        			}							        											        		
							        		}else if("2".equals(prodTelBind.getTelProdType())){//充话费
							        			grantMap = thirdBSSInterface.noCardRecharge(memberPhone, Integer.parseInt(prodTelBind.getTelProdId()));
							        		}
							        		if(Constants.CODE_SUCC.equals(MapUtil.asStr(grantMap,CommonParams.RESULT_CODE_STR))){//充值成功
												Map<String,Object> orderMap = new HashMap<String,Object>();
							        			orderMap.put("orderId", orderId);
												orderMap.put("memberId", memberId);									
												orderMap.put("productTitle", productTitle);
												orderMap.put("memberPhone", memberPhone);
												orderMap.put("groupType", rightsSpecCd);
												orderMap.put("prodSupplier", prodTelBind.getTelProdType());	
												orderMap.put("orderStatus", "4");
								        		Map<String,Object> dataMap = (Map<String, Object>) grantMap.get("result");
								        		if(dataMap != null && dataMap.size() > 0){
								        			if("1".equals(prodTelBind.getTelProdType())){//充流量
								        				Map<String,Object> customerOrderRspVo = (Map<String, Object>) dataMap.get("customerOrderRspVo");
														if(customerOrderRspVo != null && customerOrderRspVo.size() > 0){
															String custOrderId = MapUtil.asStr(customerOrderRspVo, "custOrderId");
															String custOrderNbr = MapUtil.asStr(customerOrderRspVo, "custOrderNbr");
															orderMap.put("rightsInstNbr", custOrderId);
															orderMap.put("custOrderId", custOrderId);
															orderMap.put("custOrderNbr", custOrderNbr);
														}
								        			}else if("2".equals(prodTelBind.getTelProdType())){//充话费
								        				String requestId = MapUtil.asStr(dataMap, "requestId");
								        				orderMap.put("rightsInstNbr", requestId);
								        				orderMap.put("custOrderId", requestId);
														orderMap.put("custOrderNbr", requestId);
								        			}
								        		}
								        		try{
													orderBmo.updateOrder(orderMap);
												}catch(Exception e){
													log.error("订单更新异常：", e);
												}
											}else{//充值失败，入日志加告警
												Map<String,Object> orderMap = new HashMap<String,Object>();
							        			orderMap.put("orderId", orderId);
							        			orderMap.put("orderStatus", "9");
							        			orderMap.put("groupType", rightsSpecCd);
												orderMap.put("prodSupplier", prodTelBind.getTelProdType());	
							        			try{
													orderBmo.updateOrder(orderMap);
												}catch(Exception e){
													log.error("订单更新异常：", e);
												}
							        			
												Map<String,Object> logMap = new HashMap<String,Object>();
												logMap.putAll(rightsGrantMap);
												logMap.putAll(grantMap);
												try{
													logWarningBmo.insertLogWarning(logMap);
												}catch(Exception e){
													log.error("告警入库异常：", e);
												}											
											}
							        	}else{
											Map<String,Object> logMap = new HashMap<String,Object>();
											logMap.putAll(rightsGrantMap);
											logMap.put("resultMsg", "权益销售品关系未配置");
											try{
												logWarningBmo.insertLogWarning(logMap);
											}catch(Exception e){
												log.error("告警入库异常：", e);
											}
							        	}
									}else if("11".equals(rightsSpecCd) || "12".equals(rightsSpecCd)){//合作商权益
*/										rightsMap.put("activityCode", inCode);
										rightsMap.put("objType", "2");
										rightsMap.put("objNbr", memberPhone);
										rightsMap.put("objArea", areaCode);
										checkMap = rightsBmo.rightsGrantPreCheck(rightsMap);
										if(Constants.CODE_SUCC.equals(MapUtil.asStr(checkMap,CommonParams.RESULT_CODE_STR))){//领取校验成功
											grantMap = rightsBmo.rightsGrant(rightsMap);
											if(Constants.CODE_SUCC.equals(MapUtil.asStr(grantMap,CommonParams.RESULT_CODE_STR))){//领取成功	
												Map<String,Object> orderMap = new HashMap<String,Object>();
												orderMap.put("orderId", orderId);
												orderMap.put("memberId", memberId);
												orderMap.put("productTitle", productTitle);
												orderMap.put("memberPhone", memberPhone);
												orderMap.put("groupType", rightsSpecCd);
												orderMap.put("orderStatus", "4");
												List<Map<String,Object>> rightsInstList = (List<Map<String, Object>>) grantMap.get("rightsInstList");
												if(rightsInstList != null && rightsInstList.size() > 0){
													String rightsInstNbr = MapUtil.asStr(rightsInstList.get(0), "rightsInstNbr");
													orderMap.put("rightsInstNbr", rightsInstNbr);
												}
												try{
													orderBmo.updateOrder(orderMap);
												}catch(Exception e){
													log.error("订单更新异常：", e);
												}
											}else{//领取失败，入日志加告警									
												String resultMsg = MapUtil.asStr(grantMap,CommonParams.RESULT_MSG_STR);
												Map<String,Object> logMap = new HashMap<String,Object>();
												logMap.putAll(rightsGrantMap);
												logMap.put("resultMsg", resultMsg);
												try{
													logWarningBmo.insertLogWarning(logMap);
												}catch(Exception e){
													log.error("告警入库异常：", e);
												}
											}
										}else{//校验失败，入日志加告警
											String resultMsg = MapUtil.asStr(checkMap,CommonParams.RESULT_MSG_STR);
											Map<String,Object> logMap = new HashMap<String,Object>();
											logMap.putAll(rightsGrantMap);
											logMap.put("resultMsg", resultMsg);
											try{
												logWarningBmo.insertLogWarning(logMap);
											}catch(Exception e){
												log.error("告警入库异常：", e);
											}
										}
									//}
								}else{
									Map<String,Object> logMap = new HashMap<String,Object>();
									logMap.putAll(rightsGrantMap);
									logMap.put("resultMsg", "权益查询接口查无数据");
									try{
										logWarningBmo.insertLogWarning(logMap);
									}catch(Exception e){
										log.error("告警入库异常：", e);
									}
								}
							}else{
								Map<String,Object> logMap = new HashMap<String,Object>();
								logMap.putAll(rightsGrantMap);
								logMap.put("resultMsg", "权益查询接口查询失败");
								try{
									logWarningBmo.insertLogWarning(logMap);
								}catch(Exception e){
									log.error("告警入库异常：", e);
								}
							}	
						}
					}
					grantFlag = true;
				}				
				Thread.sleep(10 * 60 * 1000);//间隔半小时
			}
		} catch (Exception ex) {
			log.error("权益领取定时异常：", ex);
		}
	}
}
