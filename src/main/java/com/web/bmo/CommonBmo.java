package com.web.bmo;

import com.web.model.CoDicItem;
import com.web.model.CoRegion;

import java.util.List;
import java.util.Map;

/**
 * <b>项目名称：</b>web-portal<br>
 * <b>类名称：</b>com.web.bmo.CommonBmo.java<br>
 * <b>创建人：</b>WJZ<br>
 * <b>类描述：</b>公共操作<br>
 * <b>创建时间：</b>2016年5月23日-上午10:30:53<br>
 */

public interface CommonBmo {
	
	/**
	 * 公共调用接口
	 * @param serviceType
	 * @param serviceCode
	 * @param eMap
	 * @return
	 * @throws Exception
	 */
	Map<String, Object> qryInfoForSys(String serviceType, String serviceCode, Map<String, Object> eMap) throws Exception;
	
	/**
	 * 公共调用接口
	 * @param serviceType
	 * @param serviceCode
	 * @param eMap
	 * @return
	 * @throws Exception
	 */
	Map<String, Object> qryInterfaceByJson(String serviceType, String serviceCode,String jsonInfo) throws Exception;
	
	/**
	 * 设置可以清除的redis缓存对象
	 * @param key
	 * @throws Exception
	 */
	public void setDelRedisKey(String key) throws Exception;

	/**
	 * 删除redis缓存对象
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public void delRedisKey() throws Exception;
	
	/**
	 * 删除扫描数据
	 * @param requestNbr
	 */
	public void delCertByNbr(String requestNbr);
	
	/**
	 * 验证扫描数据是否一致（根据身份证名称和证件号码）
	 * @param staffCode
	 * @param partyName
	 * @param certNumber
	 * @param requestNbr
	 * @return
	 */
	public boolean validationCert(String staffCode,String partyName, String certNumber, String requestNbr) throws Exception;;
	
	/**
	 * 验证扫描数据是否一致（根据证件号码）
	 * @param staffCode
	 * @param certNumber
	 * @param requestNbr
	 * @return
	 * @throws Exception
	 */
	public boolean validationCert(String staffCode, String certNumber, String requestNbr) throws Exception;
	
	/**
	 * 调用接口，保存单点日志等
	 * @param infoMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> addInterfaceLog(Map<String, Object> infoMap) throws Exception;
	
	//==========CRM3.0方法开始==================
	
	/**
	 * 调用CRM3.0接口公共方法
	 * @param sysType 调用中心
	 * @param interCode 接口编码
	 * @param serviceCode 方法编码
	 * @param jsonStr 参数（Json）
	 * @return
	 */
	public Map<String, Object> httpServiceForJson(String sysType,String interCode,String serviceCode,String jsonStr,Map<String,Object> otherParam);
	
	
	/**
	 * 调用CRM3.0接口公共方法(捕获异常)
	 * @param sysType 调用中心
	 * @param interCode 接口编码
	 * @param serviceCode 方法编码
	 * @param jsonStr 参数（Json）
	 * @return
	 */
	public Map<String, Object> httpServiceForCatchJson(String sysType,String interCode,String serviceCode,String jsonStr,Map<String,Object> otherParam);
	/**
	 * 获取统一登录accessToken
	 * @param paramsMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> queryOauthToken(Map<String,Object> paramsMap) throws Exception;
	
	/**
	 * 进行客户定位
	 * @param paramsMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> qryCustomerList(Map<String, Object> paramsMap) throws Exception;
	
	/**
	 * 新建客户
	 * @param paramsMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> addCustomerList(Map<String, Object> paramsMap) throws Exception;	
	
	/**
	 * 查询客户套餐产品数据
	 * @param paramsMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> qryOfferInstListView(Map<String, Object> paramsMap) throws Exception;
	
	/**
	 * 查询可订购附属(只可选包)
	 * @param paramsMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> qryOptionalOffer(Map<String, Object> paramsMap) throws Exception;
	
	/**
	 * 查询可订购附属
	 * @param paramsMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> qryOptionalOfferAndProd(Map<String, Object> paramsMap) throws Exception;
	
	/**
	 * 串码查询预占
	 * @param paramsMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> queryMaterial(Map<String, Object> paramsMap) throws Exception;
	
	/**
	 * 串码释放
	 * @param paramsMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> releaseMaterial(Map<String, Object> paramsMap) throws Exception;
	
	/**
	 * UIM卡查询预占
	 * @param paramsMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> queryUIMMaterial(Map<String, Object> paramsMap) throws Exception;
	
	/**
	 * UIM卡释放
	 * @param paramsMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> releaseUIMMaterial(Map<String, Object> paramsMap) throws Exception;	

	/**
	 * 提交订购附属
	 * @param paramsMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> submitOptionalOffer(Map<String, Object> paramsMap) throws Exception;
	
	/**
	 * 查询校园宽带套餐
	 * @param paramsMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> qrySchoolOffer(Map<String, Object> paramsMap) throws Exception;	
	
	/**
	 * 查询校园宽带礼包
	 * @param paramsMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> qrySchoolGiftsOffer(Map<String, Object> paramsMap) throws Exception;		
	
	/**
	 * 查询校园宽带礼包成员
	 * @param paramsMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> qrySchoolGiMberOffer(Map<String, Object> paramsMap) throws Exception;

	/**
	 * 查询是否为AB类套餐, 即是否需要传4G标识
	 * @param paramsMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> isABPackage(Map<String, Object> paramsMap) throws Exception ;
	/**
	 * 查询套餐构成
	 * @param paramsMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> qryNetConsist(Map<String, Object> paramsMap) throws Exception;	

	/**
	 * 提交校园宽带
	 * @param paramsMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> submitSchoolNetOffer(Map<String, Object> paramsMap) throws Exception;
	
	/**
	 * 一证五号
	 * @param paramsMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> qryOneToFiveNum(Map<String, Object> paramsMap) throws Exception;
	
	/**
	 * 基础套餐查询
	 * @param paramsMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> qryBaseDinner(Map<String, Object> paramsMap) throws Exception;
	
	/**
	 * 我的订单查询
	 * @param paramsMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> qryMyOrderList(Map<String, Object> paramsMap) throws Exception;

	/**
	 * 附属销售品详情查询
	 * @param paramsMap
	 * @return
	 * @throws Exception
	 */	
	public Map<String, Object> qryDetails(Map<String, Object> paramsMap) throws Exception;
	
	/**
	 * 查询用户已有产品列表(简略信息)
	 * @param paramsMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> qryCustOwnerTotal(Map<String, Object> paramsMap) throws Exception;
	/**
	 * 查询号码余额,欠费信息
	 * @param paramsMap
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> qryCustCostInfo(Map<String, Object> paramsMap) throws Exception;
	
	/**
	 * 调用CRM3.0接口公共方法
	 * @param sysType 调用中心
	 * @param interCode 接口编码
	 * @param serviceCode 方法编码
	 * @param jsonStr 参数（Json）
	 * @return
	 */
	public Map<String, Object> httpEopServiceForJson(String sysType,String interCode,String serviceCode,String jsonStr,Map<String,Object> otherParam,String eopType);


	public List<CoDicItem> getSaopRouteCodes() ;
	
	/**查询公共地市数据*/
	public List<CoRegion> getRegionList(Map<String, Object> paramsMap) throws Exception;



	public List<CoDicItem> getImageServerUrls();

	public String getImageServerUrl();

	List<CoDicItem> getDicItems(Map<String, Object> paramsMap)throws Exception;
}
