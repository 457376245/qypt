var chooseActId="";
var isTestEnv="";
layui.use(['form','layer','laypage','laydate','table'],function() {
    var pageConf={
        pageSize:10,
        currentPage:1
    };
    var laytable = layui.table;
    $(document).ready(function(){
        init();
    });
    //初始化
    function init(){
        initTable();
        $("body").on("click", ".actAdd_btn", function () {
            location.href = contextPath + '/seckill/add';
        });
        //查询
        $("body").on("click", ".search_btn", function () {
            searchList();
        }).resize();
        $("body").on("click", ".reflash", function () {
            reloadTaskAndRewardRedis();
        });
    }
    function reloadTaskAndRewardRedis() {
        var params={};
        $.ajax({
            type: "post",
            contentType: "application/json",
            data: JSON.stringify(params),
            url :contextPath+"/seckill/reloadTaskAndRewardRedis",
            success : function(response){
                if(response.successed){
                    layer.msg("刷新缓存成功");
                }else{
                    layer.msg(response.data.resultMsg);
                }
            }
        });
    }
    function initTable(){
        var source=$("#contentList").attr("data-id");
        if(source==''){
            laytable.render({
                elem: '#seckilltable',
                contentType: "application/json",
                url:contextPath+"/seckill/list",
                method: 'post',
                cellMinWidth: 80,
                page:true,
                loading:true,
                limit:pageConf.pageSize,
                cols: [[
                    {field:'activityId', title: '活动ID', width:80, sort: true}
                    ,{field: 'activityTitle',title: '活动标题'}
                    ,{field:'', title: '活动类别'
                        ,templet:function(res) {
                            if (res.seckillType == '1'){
                                return "时会场";
                            }else{
                                return "日会场";
                            }
                        }
                    }
                    //,{field: 'clickCount',title: '点击量'}
                    ,{field: 'inUserCount',title: '参与用户'}
                    ,{field: 'buyCount',title: '购买商品'}
                    ,{ field: 'statusCd', title: '状态'
                        ,templet: function (res) {
                            if (res.statusCd == '1') {
                                return "<span class=\"yellow\">进行中</span>";
                            } else if (res.statusCd == '2') {
                                return "<span class=\"black\">未开始</span>";
                            } else if (res.statusCd == '3') {
                                return "<span class=\"black\">未发布</span>";
                            } else if(res.statusCd == '4'){
                                return "<span class=\"gray\">已结束</span>";
                            }else{
                                return "<span class=\"gray\">已下架</span>";
                            }
                        }
                    }
                    ,{field: '',title: '活动时间',width:200
                        ,templet: function (res) {
                            return res.startTimeStr+'~'+res.endTimeStr;
                        }
                    }
                    ,{field: '',title: '操作',width:300,toolbar: '#toolBar'
                    }
                ]]
                ,id: 'seckilltable'
            });
        }else{
            laytable.render({
                elem: '#seckilltable',
                contentType: "application/json",
                url:contextPath+"/seckill/list",
                method: 'post',
                cellMinWidth: 80,
                page:true,
                loading:true,
                limit:pageConf.pageSize,
                cols: [[
                    {type: 'radio'}
                    ,{field: 'activityTitle',title: '活动标题'}
                    ,{field:'', title: '活动类别'
                        ,templet:function(res) {
                            if (res.seckillType == '1'){
                                return "时会场";
                            }else{
                                return "日会场";
                            }
                        }
                    }
                    //,{field: 'clickCount',title: '点击量'}
                    ,{field: 'inUserCount',title: '参与用户'}
                    ,{field: 'buyCount',title: '购买商品'}
                    ,{ field: 'statusCd', title: '状态'
                        ,templet: function (res) {
                            if (res.statusCd == '1') {
                                return "<span class=\"yellow\">进行中</span>";
                            } else if (res.statusCd == '2') {
                                return "<span class=\"black\">未开始</span>";
                            } else if (res.statusCd == '3') {
                                return "<span class=\"black\">未发布</span>";
                            } else if(res.statusCd == '4'){
                                return "<span class=\"gray\">已结束</span>";
                            }else{
                                return "<span class=\"gray\">已下架</span>";
                            }
                        }
                    }
                    ,{field: '',title: '活动时间',width:200
                        ,templet: function (res) {
                            return res.startTimeStr+'~'+res.endTimeStr;
                        }
                    }
                ]]
                ,id: 'seckilltable'
            });
        }
        if(source!='') {
            //单选框选中事件
            laytable.on('radio(seckilltable)', function (obj) {
                var res = obj.data;
                chooseActId=res.activityId;
            });
        }else{
            //监听工具条
            laytable.on('tool(seckilltable)', function(obj){
                var data = obj.data;
                if(obj.event === 'detail'){
                    location.href= contextPath + '/seckill/info?activityId='+data.activityId;
                } else if(obj.event === 'del'){
                    var status="102";
                    var tipMsg="确认要删除吗?";
                    /*if (data.statusCd == '1') {
                        tipMsg="活动正在进行中，确认要停止吗？";
                        status="104";
                    }else if(data.statusCd == '2'){
                        tipMsg="确认要停止吗?";
                        status="103";
                    }*/
                    if (data.statusCd == '1') {
                        tipMsg="活动正在进行中，确认要停止吗？";
                        status="104";
                    }else if(data.statusCd == '2'){
                        tipMsg="活动未开始，确认要停止吗?";
                        status="104";
                    }
                    layer.confirm(tipMsg, function(index){
                        delPro(data,status,obj);
                        layer.close(index);
                    });
                } else if(obj.event === 'edit'){
                    location.href= contextPath + '/seckill/edit?activityId='+data.activityId;
                } else if(obj.event === 'editProd'){
                    location.href= contextPath + '/seckill/proSet?activityId='+data.activityId;
                }else if(obj.event === 'editProdStock'){
                    location.href= contextPath + '/seckill/editProdStock?activityId='+data.activityId;
                }
            });
        }
    }
    function delPro(data,status,obj) {
        var submitData={
            "activityId":data.activityId,
            "statusCd":status
        }
        var randTime="?t="+new Date().getTime();
        var index = layer.msg('提交中，请稍候',{icon: 16,time:false,shade:0.8});
        $.ajax({
            type: 'post' ,
            url: contextPath+"/seckill/del"+randTime,
            cache:false ,
            async:true ,
            contentType: "application/json",
            data: JSON.stringify(submitData),
            dataType:'json',
            success:function(response) {
                debugger;
                layer.close(index);
                //展示数据
                if (response.successed) {
                    if(status!='104'&&status!='103'){
                        obj.del();
                    }else{
                        searchList();
                    }
                }else{
                    if(response.data.resultMsg!=null&&response.data.resultMsg!=undefined){
                        layer.msg(response.data.resultMsg);
                    }else{
                        layer.msg("活动删除/下架失败");
                    }
                }
            },
            error:function(response){
                layer.close(index);
                layer.msg("活动删除/下架失败");
            }
        });
    }
    //列表查询
    function searchList(){
        var params={
            "statusCd":null,
            "activityTitle":null,
            "seckillType":null,
            "env":"0"
        };
        var actTitle=$.trim($("#actTitle").val());
        var statusCd=$("#statusCd").val();
        if(actTitle!=""){
            params.activityTitle=actTitle;
        }
        if(statusCd!="0"){
            params.statusCd=statusCd;
        }
        var seckillType=$("#seckillType").val();
        if(seckillType!="0"){
            params.seckillType=seckillType;
        }
        var env=$("#env").val();
        if(env!=undefined&&env!=null){
            params.env=env;
            isTestEnv=env;
        }
        //执行重载
        laytable.reload('seckilltable', {
            page: {
                curr: 1 //重新从第 1 页开始
            }
            ,where: params
        });
    }
});