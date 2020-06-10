package com.web.bmo;

import java.io.OutputStream;
import java.util.Map;

public interface ReportBmo {
    /**
     * 活动列表
     * @param param
     * @return
     * @throws Exception
     */
    Map<String, Object> qryActList(Map<String, Object> param) throws Exception;

    /**
     * 统计某个抽奖活动奖品信息
     * @param param
     * @return
     * @throws Exception
     */
    Map<String, Object> qryLotteryProList(Map<String, Object> param) throws Exception;

    /**
     * 统计某个秒杀活动商品信息
     * @param param
     * @return
     * @throws Exception
     */
    Map<String, Object> qrySeckillProList(Map<String, Object> param) throws Exception;

    /**
     * 导出活动数据
     * @param out
     * @param param
     * @throws Exception
     */
    void exportActPros(OutputStream out,Map<String, Object> param)throws Exception;

    /**
     * 按奖品查询中奖会员
     * @param param
     * @return
     * @throws Exception
     */
    Map<String, Object> queryPrivilegeByPrize(Map<String, Object> param) throws Exception;

    /**
     * 查询有中奖记录的会员
     * @param param
     * @return
     * @throws Exception
     */
    Map<String, Object> queryHasPrivilegeUser(Map<String, Object> param) throws Exception;

    /**
     * 按会员查询对应中奖记录
     * @param param
     * @return
     * @throws Exception
     */
    Map<String, Object> queryPrizeByUser(Map<String, Object> param) throws Exception;

    /**
     * 导出中奖记录报表
     * @param out
     * @param param
     * @throws Exception
     */
    void exportUserPrivilege(OutputStream out,Map<String, Object> param)throws Exception;

    /**
     * 按日期统计新增客户量
     * @param param
     * @return
     * @throws Exception
     */
    Map<String, Object> queryNewMemberByTime(Map<String, Object> param) throws Exception;

    /**
     * 按地区日期统计新增客户量
     * @param param
     * @return
     * @throws Exception
     */
    Map<String, Object> queryNewMemberByArea(Map<String, Object> param) throws Exception;

    /**
     * 统计客户详细信息显示
     * @param param
     * @return
     * @throws Exception
     */
    Map<String, Object> queryNewMemberDetail(Map<String, Object> param) throws Exception;

    /**
     * 导出新增客户详情
     * @param out
     * @param param
     * @throws Exception
     */
    void exportNewMember(OutputStream out,Map<String, Object> param)throws Exception;
}
