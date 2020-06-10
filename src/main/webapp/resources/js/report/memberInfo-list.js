var report={};
var expParam={};
layui.use(['form','layer','laydate','table'],function() {
    var isInit=0;
    var pageConf={
        pageSize:10,
        currentPage:1
    };
    var laytable = layui.table;
    var laydate = layui.laydate;
    $(document).ready(function(){
        report.init();
    });
    //初始化
    report.init=function(){
        report.initTable();
        //查询
        $("body").on("click", ".search_btn", function () {
            report.searchList();
        }).resize();
        $("body").on("click", ".export_btn", function () {
            report.exportList();
        }).resize();
        var curDate = new Date();
        //日期
        laydate.render({
            elem: '#times'
            ,trigger: 'click'
            ,format: 'yyyy-MM-dd'
            ,max: dateFormat(curDate)
        });
    };
    //数据导出
    report.exportList=function () {
        report.commonExport(expParam);
    };
    //导出公共调用
    report.commonExport=function (param) {
        location.href=contextPath + "/report/exportNewMember?param="+encodeURI(JSON.stringify(param));
    };
    //抽奖活动列表查询
    report.searchList=function(){
        var dateTime=$("#times").val();
        var params={
        };
        if(dateTime!=''){
            params.dateTime=dateTime;
        }
        //执行重载
        laytable.reload('custByTime', {
            where: params,
            page: {
                curr: 1 //重新从第 1 页开始
            }
        });
        laytable.reload('custDetail', {
            data:[],
            url:'',
            page: {
                curr: 1 //重新从第 1 页开始
            }
        });
    };
    report.initTable=function(){
        laytable.render({
            elem: '#custByTime',
            contentType: "application/json",
            method: 'post',
            cellMinWidth: 80,
            page: true,
            limit: pageConf.pageSize,
            url: contextPath + "/report/queryNewMemberByTime",
            cols: [[
                {field: 'TIME', title: '日期'}
                ,{field: 'TOTAL_COUNT',title: '总数',templet: function (res) {
                      var qryTime="'"+res.TIME+"'";
                      return '<a href="javascript:report.queryMemberByDate('+qryTime+');">'+res.TOTAL_COUNT+'</a>'
                    }
                }
                ,{field: 'REGION_NAME', title: '地区'}
                ,{field: 'AREA_COUNT',title: '按地区总数',templet: function (res) {
                        return "<a href='javascript:report.queryByDetailArea("+res.REGION_CODE+");'>"+res.AREA_COUNT+"</a>"
                    }
                }
            ]]
            , id: 'custByTime'
            ,done: function (res,curr,count) {
                report.merge(res);
            }
        });
        laytable.render({
            elem: '#custDetail',
            contentType: "application/json",
            method: 'post',
            cellMinWidth: 80,
            page:true,
            loading:true,
            limit:pageConf.pageSize,
            data:[],
            cols: [[
                {field: 'MEMBER_NAME', title: '会员名称'}
                , {field: 'MEMBER_PHONE', title: '手机号码'}
                , {field: 'STAR_LEVEL', title: '会员星级'}
                , {field: 'MEMBER_LEVEL', title: '会员等级'}
                , {field: 'NICKNAME', title: '会员昵称'}
                , {field: 'REGION_NAME', title: '所属地区'}
            ]]
            ,id: 'custDetail'
        });
    };
    report.merge=function (res) {
        var data = res.data;
        var mergeIndex = 0;//定位需要添加合并属性的行数
        var mark = 1; //这里涉及到简单的运算，mark是计算每次需要合并的格子数
        var columsName = ['TIME','TOTAL_COUNT'];//需要合并的列名称
        var columsIndex = [0,1];//需要合并的列索引值
        for (var k = 0; k < columsName.length; k++) { //这里循环所有要合并的列
            var trArr = $(".layui-table-body>.layui-table").find("tr");//所有行
            for (var i = 1; i < res.data.length; i++) { //这里循环表格当前的数据
                var tdCurArr = trArr.eq(i).find("td").eq(columsIndex[k]);//获取当前行的当前列
                var tdPreArr = trArr.eq(mergeIndex).find("td").eq(columsIndex[k]);//获取相同列的第一列
                if (data[i][columsName[0]] === data[i-1][columsName[0]]) { //后一行的值与前一行的值做比较，相同就需要合并
                    mark += 1;
                    tdPreArr.each(function () {//相同列的第一列增加rowspan属性
                        $(this).attr("rowspan", mark);
                    });
                    tdCurArr.each(function () {//当前行隐藏
                        $(this).css("display", "none");
                    });
                }else {
                    mergeIndex = i;
                    mark = 1;//一旦前后两行的值不一样了，那么需要合并的格子数mark就需要重新计算
                }
            }
            mergeIndex = 0;
            mark = 1;
        }
    };
    report.queryMemberByDate=function (time) {
        //执行重载
        expParam.dateTime=time;
        expParam.areaId=null;
        var params={"dateTime":time};
        params.areaId=null;
        laytable.reload('custDetail', {
            page: {
                curr: 1 //重新从第 1 页开始
            },
            url:contextPath+"/report/queryNewMemberDetail"
            ,where: params
        });
    };
    report.queryByDetailArea=function (areaId) {
        expParam.areaId=areaId;
        var params={"dateTime":expParam.dateTime};
        params.areaId=areaId;
        laytable.reload('custDetail', {
            page: {
                curr: 1 //重新从第 1 页开始
            },
            url:contextPath+"/report/queryNewMemberDetail"
            ,where: params
        });
    };
});