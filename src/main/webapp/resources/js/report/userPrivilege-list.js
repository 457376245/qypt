var report={};
var activityId="";
layui.use(['form','layer','laydate','element','table'],function() {
    var isInit=0;
    var pageConf={
        pageSize:10,
        currentPage:1
    };
    var element = layui.element;
    element.on('tab(docDemoTabBrief)', function(elem){
        var type=$(this).attr('lay-id');
        if(type=='2'){
            if(isInit==0){
                report.initTwoTable();
                isInit=1;
            }
        }
    });
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
        $("body").on("click", "#chooseAct", function () {
            report.openChooseAct();
        }).resize();
        $("body").on("click", "#chooseTwoAct", function () {
            report.openChooseAct();
        }).resize();
        $("body").on("click", ".searchTwo_btn", function () {
            report.searchTwoList();
        }).resize();
        $("body").on("click", ".export_btn", function () {
            report.exportList('1');
        }).resize();
        $("body").on("click", ".exportTwo_btn", function () {
            report.exportList('2');
        }).resize();
        //时间范围
        var ins1=laydate.render({
            elem: '#times'
            ,range: '~'
            ,trigger: 'click'
        });
        //时间范围
        var ins2=laydate.render({
            elem: '#timeTwos'
            ,range: '~'
            ,trigger: 'click'
        });
    };
    //打开选择活动
    report.openChooseAct=function() {
        var index = layui.layer.open({
            title: "活动查询",
            type: 2,
            area: ['900px',  "450px"],
            content: contextPath+"/lottery/index?source=choose",
            btn: ['确定', '关闭'],
            yes: function(index, layero){
                var iframeWin = window[layero.find('iframe')[0]['name']];
                activityId = iframeWin.chooseActId;
                var choosrActTitle=iframeWin.choosrActTitle;
                if(choosrActTitle!=''){
                    $("#chooseAct").val(choosrActTitle);
                    $("#chooseTwoAct").val(choosrActTitle);
                    layer.close(index);
                }else{
                    layer.msg("");
                }
            }
        });
    };
    //转盘数据导出
    report.exportList=function (flag) {
        if(activityId==""){
            layer.msg("请先选择活动");
            return;
        }
        var params={
            "activityId":activityId
        };
        if(flag=='1'){
            report.getCondition(params);
        }else{
            report.getTwoCondition(params);
        }
        report.commonExport(params);
    };
    //导出公共调用
    report.commonExport=function (param) {
        location.href=contextPath + "/report/userPrivilegExport?param="+encodeURI(JSON.stringify(param));
    };
    report.getCondition=function(params){
        var times=$("#times").val();
        if(times!=''){
            params.qryStartTime=$.trim(times.split("~")[0]);
            params.qryEndTime=$.trim(times.split("~")[1]);
        }else{
            params.qryStartTime=null;
            params.qryEndTime=null;
        }
    };
    report.getTwoCondition=function(params){
        var mobilePhone=$.trim($("#mobilePhone").val());
        if(mobilePhone!=""){
            params.mobilePhone=mobilePhone;
        }else{
            params.mobilePhone=null;
        }
        var timeTwos=$("#timeTwos").val();
        if(timeTwos!=''){
            params.qryStartTime=$.trim(timeTwos.split("~")[0]);
            params.qryEndTime=$.trim(timeTwos.split("~")[1]);
        }else{
            params.qryStartTime=null;
            params.qryEndTime=null;
        }
    };
    //抽奖活动列表查询
    report.searchList=function(){
        if(activityId==""){
            layer.msg("请先选择活动");
            return;
        }
        var params={
            "activityId":activityId
        };
        report.getCondition(params);
        //执行重载
        laytable.reload('prizetable', {
            where: params,
            url: contextPath + "/report/lotteryProList"
        });
        laytable.reload('prizeUsertable', {
            data:[],
            url:''
        });
    };
    //列表查询
    report.searchTwoList=function(){
        if(activityId==""){
            layer.msg("请先选择活动");
            return;
        }
        var params={
            "activityId":activityId
        };
        report.getTwoCondition(params);
        //执行重载
        laytable.reload('memberTable', {
            page: {
                curr: 1 //重新从第 1 页开始
            }
            ,where: params,
            url: contextPath + "/report/hasPrivilegeUserList"
        });
        laytable.reload('memberPrizeTable', {
            data:[],
            url:''
        });
    };
    report.initTable=function(){
        laytable.render({
            elem: '#prizetable',
            contentType: "application/json",
            method: 'post',
            cellMinWidth: 80,
            data:[],
            cols: [[
                {field: 'PRIZE_TITLE',title: '奖品名称',templet: function (res) {
                        return "<a href='javascript:report.queryPrizeMember("+res.PRIZE_ID+",1);'>"+res.PRIZE_TITLE+"</a>"
                    }
                }
                ,{field: 'WEIGHT_VAL', title: '奖品中奖概率'}
                ,{field: 'PRIZE_COUNT', title: '奖品总量'}
                ,{field: 'PRIZE_STOCK', title: '奖品剩余库存'}
                ,{field: 'PRIZE_BUYCOUNT', title: '已中奖数量'}
            ]]
            , id: 'prizetable'
        });
        laytable.render({
            elem: '#prizeUsertable',
            contentType: "application/json",
            method: 'post',
            cellMinWidth: 80,
            page: true,
            limit: pageConf.pageSize,
            data:[],
            cols: [[
                ,{field: 'MEMBER_NAME', title: '会员名称'}
                ,{field: 'MEMBER_PHONE', title: '手机号码'}
                ,{field: 'STAR_LEVEL', title: '会员星级'}
                ,{field: 'PRIZE_TITLE', title: '奖品名称'}
                ,{field: 'PRODUCT_CODE', title: '权益编码'}
                ,{field: 'STATUS_DATE', title: '中奖时间'}
            ]]
            , id: 'prizeUsertable'
        });
    };
    report.initTwoTable=function () {
        laytable.render({
            elem: '#memberTable',
            contentType: "application/json",
            method: 'post',
            cellMinWidth: 80,
            page:true,
            loading:true,
            limit:pageConf.pageSize,
            data:[],
            cols: [[
                {
                    field: 'MEMBER_NAME', title: '会员名称',templet: function (res) {
                        return "<a href='javascript:report.queryPrizeMember("+res.MEMBER_ID+",2);'>"+res.MEMBER_NAME+"</a>"
                    }
                }
                , {field: 'MEMBER_PHONE', title: '手机号码'}
                , {field: 'STAR_LEVEL', title: '会员星级'}
                , {field: 'PRIZE_COUNT', title: '中奖次数'}
                , {field: 'IN_COUNT', title: '参与次数'}
            ]]
            ,id: 'memberTable'
        });
        laytable.render({
            elem: '#memberPrizeTable',
            contentType: "application/json",
            method: 'post',
            cellMinWidth: 80,
            page: true,
            limit: pageConf.pageSize,
            data:[],
            cols: [[
                ,{field: 'MEMBER_NAME', title: '会员名称'}
                ,{field: 'MEMBER_PHONE', title: '手机号码'}
                ,{field: 'STAR_LEVEL', title: '会员星级'}
                ,{field: 'PRIZE_TITLE', title: '奖品名称'}
                ,{field: 'PRODUCT_CODE', title: '权益编码'}
                ,{field: 'STATUS_DATE', title: '中奖时间'}
            ]]
            , id: 'memberPrizeTable'
        });
    };
    report.queryPrizeMember=function (id,type) {
        //执行重载
        var params={"activityId":activityId};
        if(type==1){
            params.prizeId=id;
            report.getCondition(params);
            laytable.reload('prizeUsertable', {
                page: {
                    curr: 1 //重新从第 1 页开始
                },
                url:contextPath+"/report/privilegeByPrizeList"
                ,where: params
            });
        }else{
            params.memberId=id;
            report.getTwoCondition(params);
            laytable.reload('memberPrizeTable', {
                page: {
                    curr: 1 //重新从第 1 页开始
                },
                url:contextPath+"/report/prizeByUserList"
                ,where: params
            });
        }
    };
});