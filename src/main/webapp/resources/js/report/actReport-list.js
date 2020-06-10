var report={};
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
                report.initSeckillTable();
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
        $("body").on("click", ".seckillSearch_btn", function () {
            report.searchSeckillList();
        }).resize();
        $("body").on("click", ".export_btn", function () {
            report.exportList();
        }).resize();
        $("body").on("click", ".seckillExport_btn", function () {
            report.exportSeckillList();
        }).resize();
        //时间范围
        var ins1=laydate.render({
            elem: '#times'
            ,range: '~'
            ,trigger: 'click'
        });
        //时间范围
        var ins2=laydate.render({
            elem: '#seckillTimes'
            ,range: '~'
            ,trigger: 'click'
        });
    };
    //转盘数据导出
    report.exportList=function () {
        var params={
            "statusCd":null,
            "activityTitle":null,
            "activityMode":"1",
            "page":1,
            "limit":300
        };
        report.getCondition(params);
        report.commonExport(params);
    };
    //秒杀数据导出
    report.exportSeckillList=function () {
        var params={
            "statusCd":null,
            "activityTitle":null,
            "seckillType":null,
            "activityMode":"2",
            "qryStartTime":null,
            "qryEndTime":null,
            "page":1,
            "limit":300
        };
        report.getSeckillCondition(params);
        report.commonExport(params);
    };
    //导出公共调用
    report.commonExport=function (param) {
        location.href=contextPath + "/report/actExport?param="+encodeURI(JSON.stringify(param));
    };
    report.getCondition=function(params){
        var actTitle=$.trim($("#actTitle").val());
        var statusCd=$("#statusCd").val();
        if(actTitle!=""){
            params.activityTitle=actTitle;
        }else{
            params.activityTitle=null;
        }
        if(statusCd!=""){
            params.statusCd=statusCd;
        }else{
            params.statusCd=null;
        }
        var times=$("#times").val();
        if(times!=''){
            params.qryStartTime=$.trim(times.split("~")[0]);
            params.qryEndTime=$.trim(times.split("~")[1]);
        }else{
            params.qryStartTime=null;
            params.qryEndTime=null;
        }
    };
    report.getSeckillCondition=function(params){
        var actTitle=$.trim($("#seckillActTitle").val());
        var statusCd=$("#seckillStatusCd").val();
        if(actTitle!=""){
            params.activityTitle=actTitle;
        }else{
            params.activityTitle=null;
        }
        if(statusCd!="0"){
            params.statusCd=statusCd;
        }else{
            params.statusCd=null;
        }
        var seckillType=$("#seckillType").val();
        if(seckillType!="0"){
            params.seckillType=seckillType;
        }else{
            params.seckillType=null;
        }
        var seckillTimes=$("#seckillTimes").val();
        if(seckillTimes!=''){
            params.qryStartTime=$.trim(seckillTimes.split("~")[0]);
            params.qryEndTime=$.trim(seckillTimes.split("~")[1]);
        }else{
            params.qryStartTime=null;
            params.qryEndTime=null;
        }
    };
    //抽奖活动列表查询
    report.searchList=function(){
        var params={
            "statusCd":null,
            "activityTitle":null,
            "activityMode":"1"
        };
        report.getCondition(params);
        //执行重载
        laytable.reload('turntableReload', {
            page: {
                curr: 1 //重新从第 1 页开始
            }
            ,where: params
        });
        laytable.reload('turnProtableReload', {
            data:[],
            url:''
        });
    };
    //列表查询
    report.searchSeckillList=function(){
        var params={
            "statusCd":null,
            "activityTitle":null,
            "seckillType":null,
            "activityMode":"2"
        };
        report.getSeckillCondition(params);
        //执行重载
        laytable.reload('seckilltable', {
            page: {
                curr: 1 //重新从第 1 页开始
            }
            ,where: params
        });
        laytable.reload('seckillProtableReload', {
            data:[],
            url:''
        });
    };
    report.initTable=function(){
        laytable.render({
            elem: '#turntable',
            contentType: "application/json",
            url: contextPath + "/report/lotteryList",
            method: 'post',
            cellMinWidth: 80,
            page: true,
            limit: pageConf.pageSize,
            cols: [[
                {
                    field: 'ACTIVITY_TITLE', title: '活动标题',templet: function (res) {
                        return "<a href='javascript:report.queryPro("+res.ACTIVITY_ID+");'>"+res.ACTIVITY_TITLE+"</a>"
                    }
                }
                , {field: 'CLICK_COUNT', title: '点击量'}
                , {field: 'USE_ACCOUNT', title: '参与人数'}
                , {field: 'IN_USER_COUNT', title: '参与用户'}
                , {
                    field: 'STATUS_CD', title: '状态'
                    , width: 120, templet: function (res) {
                        if (res.STATUS_CD == '1') {
                            return "<span class=\"yellow\">进行中</span>";
                        } else if (res.STATUS_CD == '2') {
                            return "<span class=\"black\">未开始</span>";
                        } else if (res.STATUS_CD == '3') {
                            return "<span class=\"black\">未发布</span>";
                        } else if (res.STATUS_CD == '4') {
                            return "<span class=\"gray\">已结束</span>";
                        } else {
                            return "<span class=\"gray\">已下架</span>";
                        }
                    }
                }
                , {
                    field: '', title: '活动时间', width: 180
                    , templet: function (res) {
                        return res.START_TIMESTR + '~' + res.END_TIMESTR;
                    }
                }
            ]]
            , id: 'turntableReload'
        });
        laytable.render({
            elem: '#turnProtable',
            contentType: "application/json",
            method: 'post',
            cellMinWidth: 80,
            data:[],
            cols: [[
                {field: 'PRIZE_TITLE',title: '奖品名称'}
                ,{field: 'WEIGHT_VAL', title: '奖品中奖概率'}
                ,{field: 'PRIZE_COUNT', title: '奖品总量'}
                ,{field: 'PRIZE_STOCK', title: '奖品剩余库存'}
                ,{field: 'PRIZE_BUYCOUNT', title: '已中奖数量'}
            ]]
            ,id: 'turnProtableReload'
        });
    };
    report.initSeckillTable=function () {
        laytable.render({
            elem: '#seckilltable',
            contentType: "application/json",
            url:contextPath+"/report/seckillList",
            method: 'post',
            cellMinWidth: 80,
            page:true,
            loading:true,
            limit:pageConf.pageSize,
            cols: [[
                {
                    field: 'ACTIVITY_TITLE', title: '活动标题',templet: function (res) {
                        return "<a href='javascript:report.querySeckillPro("+res.ACTIVITY_ID+");'>"+res.ACTIVITY_TITLE+"</a>"
                    }
                }
                ,{field:'', title: '活动类别'
                    ,templet:function(res) {
                        if (res.SECKILL_TYPE == '1'){
                            return "时会场";
                        }else{
                            return "日会场";
                        }
                    }
                }
                , {field: 'CLICK_COUNT', title: '点击量'}
                , {field: 'USE_ACCOUNT', title: '参与人数'}
                , {field: 'IN_USER_COUNT', title: '参与用户'}
                , {
                    field: 'STATUS_CD', title: '状态'
                    , width: 120, templet: function (res) {
                        if (res.STATUS_CD == '1') {
                            return "<span class=\"yellow\">进行中</span>";
                        } else if (res.STATUS_CD == '2') {
                            return "<span class=\"black\">未开始</span>";
                        } else if (res.STATUS_CD == '3') {
                            return "<span class=\"black\">未发布</span>";
                        } else if (res.STATUS_CD == '4') {
                            return "<span class=\"gray\">已结束</span>";
                        } else {
                            return "<span class=\"gray\">已下架</span>";
                        }
                    }
                }
                , {
                    field: '', title: '活动时间', width: 180
                    , templet: function (res) {
                        return res.START_TIMESTR + '~' + res.END_TIMESTR;
                    }
                }
            ]]
            ,id: 'seckilltable'
        });
        laytable.render({
            elem: '#seckillProtable',
            contentType: "application/json",
            method: 'post',
            cellMinWidth: 80,
            data:[],
            cols: [[
                {field: 'PRODUCT_TITLE',title: '商品名称'}
                ,{field: 'OLD_PRICE', title: '商品原价'}
                ,{field: 'NEW_PRICE', title: '商品秒杀价'}
                ,{field: 'PRODUCT_TOTAL', title: '商品总量'}
                ,{field: 'PRODUCT_STOCK', title: '商品剩余库存'}
                ,{field: 'BUY_COUNT', title: '已订购数量'}
            ]]
            ,id: 'seckillProtableReload'
        });
    };
    report.queryPro=function (activityId) {
        //执行重载
        var params={"activityId":activityId};
        laytable.reload('turnProtableReload', {
            url:contextPath+"/report/lotteryProList"
            ,where: params
        });
    };
    report.querySeckillPro=function (activityId) {
        //执行重载
        var params={"activityId":activityId};
        laytable.reload('seckillProtableReload', {
            url:contextPath+"/report/seckillProList"
            ,where: params
        });
    };
});