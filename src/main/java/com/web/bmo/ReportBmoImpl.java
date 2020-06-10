package com.web.bmo;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.web.common.Constants;
import com.web.dao.ActivityDao;
import com.web.dao.ReportDao;
import com.web.dao.SecKillDao;
import com.web.model.CoActRule;
import com.web.model.CoActivity;
import com.web.model.SecKill;
import com.web.util.*;
import com.web.util.common.Log;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * 报表相关逻辑
 *
 * @author
 * @create 2020-04-14
 **/
@Service("com.web.bmo.ReportBmoImpl")
public class ReportBmoImpl implements ReportBmo{
    @Autowired
    private ReportDao reportDao;
    @Override
    public Map<String, Object> qryActList(Map<String, Object> param) throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        int pageNum = MapUtil.asInt(param, "page");
        int pageSize = MapUtil.asInt(param, "limit");
        changeStatus(param);
        PageHelper.startPage(pageNum, pageSize);
        List<Map<String, Object>> list = null;
        if ("1".equals(param.get("activityMode"))){
            param.put("activityMode", "1");
            list = reportDao.queryLotteryReport(param);
        }else {
            param.put("parentId","0");//取父节点
            param.put("activityMode","2");
            list = reportDao.querySecKillReport(param);
        }
        PageInfo<Map<String,Object>> info = new PageInfo<>(list);
        for(Map<String,Object> coActivity:info.getList()){
            String statusCd=(String)coActivity.get("STATUS_CD");
            Date startTime=(Date)coActivity.get("START_TIME");
            Date endTime=(Date)coActivity.get("END_TIME");
            Date nowTime=new Date();
            if(Constants.STATUS_NOTPUBLISH.equals(statusCd)){//未发布
                coActivity.put("STATUS_CD","3");
            }else if(Constants.STATUS_DROP.equals(statusCd)){//已下线
                coActivity.put("STATUS_CD","5");
            }else{
                if(nowTime.compareTo(startTime)<0){//未开始
                    coActivity.put("STATUS_CD","2");
                }else if(nowTime.compareTo(endTime)>0){//已结束
                    coActivity.put("STATUS_CD","4");
                }else{//进行中
                    coActivity.put("STATUS_CD","1");
                }
            }
            if(coActivity.get("USE_ACCOUNT")==null){
                coActivity.put("USE_ACCOUNT",0);
            }
            coActivity.put("START_TIMESTR",DateUtil.format(startTime,"yyyy/MM/dd"));
            coActivity.put("END_TIMESTR",DateUtil.format(endTime,"yyyy/MM/dd"));
        }
        resultMap.put("data",info.getList());
        resultMap.put("count",info.getTotal());
        return resultMap;
    }
    private void changeStatus(Map<String, Object> param){
        if(param.get("statusCd")!=null){
            String statusCd=(String)param.remove("statusCd");
            switch(statusCd){
                case "1" ://进行中
                    param.put("nowTime","1");
                    param.put("statusCd", Constants.STATUS_EFFECT);
                    break;
                case "2" ://未开始
                    param.put("beforeStartTime","1");
                    param.put("statusCd", Constants.STATUS_EFFECT);
                    break;
                case "3" ://未发布
                    param.put("statusCd", Constants.STATUS_NOTPUBLISH);
                    break;
                case "4" ://已结束
                    param.put("afterEndTime","1");
                    param.put("statusCd", Constants.STATUS_EFFECT);
                    break;
                case "5" ://已下架
                    param.put("statusCd", Constants.STATUS_DROP);
                    break;
                default :
            }
        }
    }

    @Override
    public Map<String, Object> qryLotteryProList(Map<String, Object> param) throws Exception {
        Map<String, Object> resultMap=new HashMap<String, Object>();
        String activityId=MapUtil.asStr(param,"activityId");
        List<Map<String, Object>> prizeList=reportDao.queryPrizeReport(Long.parseLong(activityId));
        resultMap.put("data",prizeList);
        resultMap.put("count",prizeList.size());
        return resultMap;
    }

    @Override
    public Map<String, Object> qrySeckillProList(Map<String, Object> param) throws Exception {
        Map<String, Object> resultMap=new HashMap<String, Object>();
        List<Map<String,Object>> proList=reportDao.querySecKillProReport(param);
        resultMap.put("data",proList);
        resultMap.put("count",proList.size());
        return resultMap;
    }

    @Override
    public void exportActPros(OutputStream out, Map<String, Object> param) throws Exception {
        Map result=qryActList(param);//限制导出300条
        List<Map<String,Object>> list=(List<Map<String,Object>>)result.get("data");
        if(list!=null&&list.size()>0) {
            for (Map<String, Object> coActivity : list) {
                String actTime=coActivity.remove("START_TIMESTR") + "~"+ coActivity.remove("END_TIMESTR");
                coActivity.put("ACTTIME",actTime);
                String statusCd=(String)coActivity.remove("STATUS_CD");
                String statusCdName="";
                if ("1".equals(statusCd)) {
                    statusCdName="进行中";
                } else if ("2".equals(statusCd)) {
                    statusCdName="未开始";
                } else if ("3".equals(statusCd)) {
                    statusCdName="未发布";
                } else if ("4".equals(statusCd)) {
                    statusCdName="已结束";
                } else {
                    statusCdName="已下架";
                }
                if (coActivity.get("SECKILL_TYPE") != null) {
                    if("1".equals(coActivity.get("SECKILL_TYPE"))){
                        coActivity.put("SECKILL_TYPE","时会场");
                    }else{
                        coActivity.put("SECKILL_TYPE","日会场");
                    }
                }
                coActivity.put("STATUSCD_NAME",statusCdName);
                List<Map<String,Object>> proList=null;
                Map<String,Object> actParam=new HashMap<>();
                actParam.put("activityId",coActivity.get("ACTIVITY_ID"));
                if ("1".equals(param.get("activityMode"))){
                    proList=(List<Map<String,Object>>)qryLotteryProList(actParam).get("data");
                }else{
                    proList=(List<Map<String,Object>>)qrySeckillProList(actParam).get("data");
                }
                coActivity.put("proList",proList);
            }
        }
        List<Map<String, Object>> column=new ArrayList<>();
        Map<String,Object> col=new HashMap<>();
        col.put("key","ACTIVITY_CD");
        col.put("type","1");
        column.add(col);
        col=new HashMap<>();
        col.put("key","ACTIVITY_TITLE");
        col.put("type","1");
        column.add(col);
        if ("2".equals(param.get("activityMode"))) {
            col=new HashMap<>();
            col.put("key","SECKILL_TYPE");
            col.put("type","1");
            column.add(col);
        }
        col=new HashMap<>();
        col.put("key","CLICK_COUNT");
        col.put("type","0");
        column.add(col);
        col=new HashMap<>();
        col.put("key","USE_ACCOUNT");
        col.put("type","0");
        column.add(col);
        col=new HashMap<>();
        col.put("key","IN_USER_COUNT");
        col.put("type","0");
        column.add(col);
        col=new HashMap<>();
        col.put("key","STATUSCD_NAME");
        col.put("type","1");
        column.add(col);
        col=new HashMap<>();
        col.put("key","ACTTIME");
        col.put("type","1");
        column.add(col);
        if ("1".equals(param.get("activityMode"))) {
            col=new HashMap<>();
            col.put("key","PRIZE_TITLE");
            col.put("type","1");
            column.add(col);
            col=new HashMap<>();
            col.put("key","WEIGHT_VAL");
            col.put("type","1");
            column.add(col);
            col=new HashMap<>();
            col.put("key","PRIZE_COUNT");
            col.put("type","0");
            column.add(col);
            col=new HashMap<>();
            col.put("key","PRIZE_STOCK");
            col.put("type","0");
            column.add(col);
            col=new HashMap<>();
            col.put("key","PRIZE_BUYCOUNT");
            col.put("type","0");
            column.add(col);
            String[] str=new String[]{"活动编码","活动标题","点击量","参与人数","参与用户","状态","活动时间","奖品名称",
                    "奖品中奖概率","奖品总量","奖品剩余库存","已中奖数量"};
            List<String> rowName=Arrays.asList(str);
            export(out, list, "抽奖活动报表",column,rowName);
        }else{
            col=new HashMap<>();
            col.put("key","PRODUCT_TITLE");
            col.put("type","1");
            column.add(col);
            col=new HashMap<>();
            col.put("key","OLD_PRICE");
            col.put("type","1");
            column.add(col);
            col=new HashMap<>();
            col.put("key","NEW_PRICE");
            col.put("type","1");
            column.add(col);
            col=new HashMap<>();
            col.put("key","PRODUCT_TOTAL");
            col.put("type","0");
            column.add(col);
            col=new HashMap<>();
            col.put("key","PRODUCT_STOCK");
            col.put("type","0");
            column.add(col);
            col=new HashMap<>();
            col.put("key","BUY_COUNT");
            col.put("type","0");
            column.add(col);
            String[] str=new String[]{"活动编码","活动标题","活动类别","点击量","参与人数","参与用户","状态","活动时间",
                    "商品名称","商品原价","商品秒杀价","商品总量","商品剩余库存","已订购数量"};
            List<String> rowName=Arrays.asList(str);
            export(out, list, "抽奖活动报表",column,rowName);
        }
    }

    @Override
    public Map<String, Object> queryPrivilegeByPrize(Map<String, Object> param) throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        int pageNum = MapUtil.asInt(param, "page");
        int pageSize = MapUtil.asInt(param, "limit");
        PageHelper.startPage(pageNum, pageSize);
        List<Map<String, Object>> list = reportDao.queryUserPrivilege(param);
        PageInfo<Map<String,Object>> info = new PageInfo<>(list);
        resultMap.put("data",info.getList());
        resultMap.put("count",info.getTotal());
        return resultMap;
    }

    @Override
    public Map<String, Object> queryHasPrivilegeUser(Map<String, Object> param) throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        int pageNum = MapUtil.asInt(param, "page");
        int pageSize = MapUtil.asInt(param, "limit");
        PageHelper.startPage(pageNum, pageSize);
        List<Map<String, Object>> list = reportDao.queryHasPrivilegeUser(param);
        PageInfo<Map<String,Object>> info = new PageInfo<>(list);
        resultMap.put("data",info.getList());
        resultMap.put("count",info.getTotal());
        return resultMap;
    }

    @Override
    public Map<String, Object> queryPrizeByUser(Map<String, Object> param) throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        int pageNum = MapUtil.asInt(param, "page");
        int pageSize = MapUtil.asInt(param, "limit");
        PageHelper.startPage(pageNum, pageSize);
        List<Map<String, Object>> list = reportDao.queryUserPrivilege(param);
        PageInfo<Map<String,Object>> info = new PageInfo<>(list);
        resultMap.put("data",info.getList());
        resultMap.put("count",info.getTotal());
        return resultMap;
    }

    @Override
    public void exportUserPrivilege(OutputStream out, Map<String, Object> param) throws Exception {
        List list=reportDao.queryUserPrivilege(param);
        List<Map<String, Object>> column=new ArrayList<>();
        Map col=new HashMap<>();
        col.put("key","MEMBER_NAME");
        col.put("type","1");
        column.add(col);
        col=new HashMap<>();
        col.put("key","MEMBER_PHONE");
        col.put("type","1");
        column.add(col);
        col=new HashMap<>();
        col.put("key","STAR_LEVEL");
        col.put("type","1");
        column.add(col);
        col=new HashMap<>();
        col.put("key","PRIZE_TITLE");
        col.put("type","1");
        column.add(col);
        col=new HashMap<>();
        col.put("key","PRODUCT_CODE");
        col.put("type","1");
        column.add(col);
        col=new HashMap<>();
        col.put("key","STATUS_DATE");
        col.put("type","1");
        column.add(col);
        String[] str=new String[]{"会员名称","会员手机号码","会员星级","奖品名称","权益编码","中奖时间"};
        List<String> rowName=Arrays.asList(str);
        export(out, list, "抽奖活动报表",column,rowName);
    }

    @Override
    public Map<String, Object> queryNewMemberByTime(Map<String, Object> param) throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        int pageNum = MapUtil.asInt(param, "page");
        int pageSize = MapUtil.asInt(param, "limit");
        String dateTime=MapUtil.asStr(param,"dateTime");
        if(!StringUtil.isEmptyStr(dateTime)){
            param.put("qryStartTime",dateTime+" 00:00:00");
            param.put("qryEndTime",dateTime+" 23:59:59");
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Map<String, Object>> list = reportDao.queryNewMemberByTime(param);
        PageInfo<Map<String,Object>> info = new PageInfo<>(list);
        List<Map<String, Object>> resultList=new ArrayList<>();
        for(Map<String,Object> mm:info.getList()){
            String time=MapUtil.asStr(mm,"TIME");
            Map<String, Object> pp=new HashMap<>();
            pp.put("dateTime",time);
            Map<String,Object> byAreaMap=queryNewMemberByArea(pp);
            List<Map<String, Object>>  byAreaList=(List<Map<String, Object>>)byAreaMap.get("data");
            int count=byAreaList.size();
            for(Map<String,Object> bmm:byAreaList){
                bmm.putAll(mm);
                bmm.put("COUNT",count);
                resultList.add(bmm);
            }
        }
        resultMap.put("data",resultList);
        resultMap.put("count",info.getTotal());
        return resultMap;
    }

    @Override
    public Map<String, Object> queryNewMemberByArea(Map<String, Object> param) throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        String dateTime=MapUtil.asStr(param,"dateTime");
        if(!StringUtil.isEmptyStr(dateTime)){
            param.put("qryStartTime",dateTime+" 00:00:00");
            param.put("qryEndTime",dateTime+" 23:59:59");
        }
        List<Map<String, Object>> list = reportDao.queryNewMemberByArea(param);
        resultMap.put("data",list);
        resultMap.put("count",list.size());
        return resultMap;
    }

    @Override
    public Map<String, Object> queryNewMemberDetail(Map<String, Object> param) throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        int pageNum = MapUtil.asInt(param, "page");
        int pageSize = MapUtil.asInt(param, "limit");
        String dateTime=MapUtil.asStr(param,"dateTime");
        if(!StringUtil.isEmptyStr(dateTime)){
            param.put("qryStartTime",dateTime+" 00:00:00");
            param.put("qryEndTime",dateTime+" 23:59:59");
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Map<String, Object>> list = reportDao.queryNewMemberDetail(param);
        PageInfo<Map<String,Object>> info = new PageInfo<>(list);
        resultMap.put("data",info.getList());
        resultMap.put("count",info.getTotal());
        return resultMap;
    }

    @Override
    public void exportNewMember(OutputStream out, Map<String, Object> param) throws Exception {
        String dateTime=MapUtil.asStr(param,"dateTime");
        if(!StringUtil.isEmptyStr(dateTime)){
            param.put("qryStartTime",dateTime+" 00:00:00");
            param.put("qryEndTime",dateTime+" 23:59:59");
        }
        List list=reportDao.queryNewMemberDetail(param);
        List<Map<String, Object>> column=new ArrayList<>();
        Map col=new HashMap<>();
        col.put("key","MEMBER_NAME");
        col.put("type","1");
        column.add(col);
        col=new HashMap<>();
        col.put("key","MEMBER_PHONE");
        col.put("type","1");
        column.add(col);
        col=new HashMap<>();
        col.put("key","STAR_LEVEL");
        col.put("type","1");
        column.add(col);
        col=new HashMap<>();
        col.put("key","MEMBER_LEVEL");
        col.put("type","1");
        column.add(col);
        col=new HashMap<>();
        col.put("key","NICKNAME");
        col.put("type","1");
        column.add(col);
        col=new HashMap<>();
        col.put("key","REGION_NAME");
        col.put("type","1");
        column.add(col);
        String[] str=new String[]{"会员名称","会员手机号码","会员星级","会员等级","会员昵称","所属地区"};
        List<String> rowName=Arrays.asList(str);
        export(out, list, "新增客户报表",column,rowName);
    }

    /**
     * @param out
     * @param dataList:数据集
     * @param title：工作簿标题
     * @param column：列字段key和类型
     * @param rowName：表头
     * @throws Exception
     */
    public void export(OutputStream out, List<Map<String, Object>> dataList,
                        String title, List<Map<String, Object>> column, List<String> rowName){
        try {
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet(title);
            // 产生表格标题行
            HSSFRow rowm = sheet.createRow(0);
            HSSFCell cellTitle = rowm.createCell(0);
            //sheet样式定义
            HSSFCellStyle columnTopStyle = ExportUtil.getColumnTopStyle(workbook);
            HSSFCellStyle style = ExportUtil.getStyle(workbook);
            cellTitle.setCellStyle(columnTopStyle);
            // 定义所需列数
            int columnNum = rowName.size();
            HSSFRow rowRowName = sheet.createRow(0);

            // 将列头设置到sheet的单元格中
            for (int n = 0; n < columnNum; n++) {
                HSSFCell cellRowName = rowRowName.createCell(n);
                cellRowName.setCellType(HSSFCell.CELL_TYPE_STRING);
                HSSFRichTextString text = new HSSFRichTextString(rowName.get(n));
                cellRowName.setCellValue(text);
                cellRowName.setCellStyle(columnTopStyle);
            }
            // 将查询到的数据设置到sheet对应的单元格中
            int rowIndex=1;
            for (int i = 0; i < dataList.size(); i++) {
                Map<String, Object> actList = dataList.get(i);
                List<Map<String,Object>> proList=(List<Map<String,Object>>)actList.get("proList");
                if(proList!=null&&proList.size()>0){
                    for (int k = 0; k < proList.size(); k++) {
                        setConlum(column,actList,style,rowIndex,proList.get(k),sheet, proList.size()-1);
                        rowIndex++;
                    }
                }else{
                    setConlum(column,actList,style,rowIndex,null,sheet,0);
                    rowIndex++;
                }
            }
            // 让列宽随着导出的列长自动适应
            for (int colNum = 0; colNum < columnNum; colNum++) {
                int columnWidth = sheet.getColumnWidth(colNum) / 256;
                for (int rowNum = 0; rowNum < sheet.getLastRowNum(); rowNum++) {
                    HSSFRow currentRow;
                    if (sheet.getRow(rowNum) == null) {
                        currentRow = sheet.createRow(rowNum);
                    } else {
                        currentRow = sheet.getRow(rowNum);
                    }
                    if (currentRow.getCell(colNum) != null) {
                        HSSFCell currentCell = currentRow.getCell(colNum);
                        if (currentCell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                            int length = currentCell.getStringCellValue().getBytes().length;
                            if (columnWidth < length) {
                                columnWidth = length;
                            }
                        }
                    }
                }
                if (colNum == 0) {
                    sheet.setColumnWidth(colNum, (columnWidth - 2) * 256);
                } else {
                    sheet.setColumnWidth(colNum, (columnWidth + 4) * 256);
                }
            }

            if (workbook != null) {
                try {

                    workbook.write(out);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void setConlum(List<Map<String,Object>> column,Map<String,Object> actList,HSSFCellStyle style,
                           int rowIndex,Map<String,Object> proList, HSSFSheet sheet,int merginRow){
        HSSFRow row = sheet.createRow(rowIndex);// 创建所需的行数
        //列
        for (int j = 0; j < column.size(); j++) {
            Map<String,Object> col=column.get(j);
            String key=col.get("key").toString();
            String type=col.get("type").toString();//0,数字，1字符串 2日期
            HSSFCell cell = null;
            switch (type) {
                case "0"://数字
                    cell = row.createCell(j, HSSFCell.CELL_TYPE_NUMERIC);
                    if(actList.get(key)!=null) {
                        cell.setCellValue(Integer.parseInt(actList.get(key).toString()));
                        ExportUtil.merginCell(sheet,rowIndex,rowIndex+merginRow,j,j);
                    }else{
                        if(proList!=null&&proList.get(key)!=null) {
                            cell.setCellValue(Integer.parseInt(proList.get(key).toString()));
                        }else{
                            cell.setCellValue(0);
                        }
                    }
                    break;
                default://字符串
                    cell = row.createCell(j, HSSFCell.CELL_TYPE_STRING);
                    if(actList.get(key)!=null) {
                        cell.setCellValue(actList.get(key).toString());
                       ExportUtil.merginCell(sheet,rowIndex,rowIndex+merginRow,j,j);
                    }else{
                        if(proList!=null&&proList.get(key)!=null) {
                            cell.setCellValue(proList.get(key).toString());
                        }else{
                            cell.setCellValue("");
                        }
                    }
                    break;
            }
            cell.setCellStyle(style);
        }
    }

    public static void main(String[] args) throws Exception{
        ReportBmoImpl bmo=new ReportBmoImpl();
        File file=new File("D://test/aa.xls");
        OutputStream stream=new FileOutputStream(file);
            List<Map<String, Object>> dataList=new ArrayList<>();
            Map<String,Object> obj=new HashMap<>();
            List<Map<String,Object>> proList=new ArrayList<>();
            Map<String,Object> pro=new HashMap<>();
            pro.put("PRODUCT_CODE","S12345556");
            proList.add(pro);
            pro=new HashMap<>();
            pro.put("PRODUCT_CODE","S12345557");
            proList.add(pro);
            pro=new HashMap<>();
            pro.put("PRODUCT_CODE","S12345558");
            proList.add(pro);
            obj.put("proList",proList);
            obj.put("activityId",123213);
            obj.put("title","活动1");
            dataList.add(obj);
            obj=new HashMap<>();
            obj.put("proList",proList);
            obj.put("activityId",123214);
            obj.put("title","活动2");
            dataList.add(obj);
            List<Map<String, Object>> column=new ArrayList<>();
            Map<String,Object> col=new HashMap<>();
            col.put("key","activityId");
            col.put("type","0");
            column.add(col);
            col=new HashMap<>();
            col.put("key","title");
            col.put("type","1");
            column.add(col);
            col=new HashMap<>();
            col.put("key","PRODUCT_CODE");
            col.put("type","1");
            column.add(col);
            List<String> rowName=new ArrayList<>();
            rowName.add("活动id");
            rowName.add("活动名称");
            rowName.add("商品编码");
            //OutputStream out, List<Map<String, Object>> dataList,
        //                        String title, List<Map<String, Object>> column, List<String> rowName
           bmo.export(stream,dataList,"测试",column,rowName);
    }
}
