var chooseActId = "";
var choosrActTitle="";
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
            location.href = contextPath + '/lottery/add';
        });
        //查询
        $("body").on("click", ".search_btn", function () {
            searchList();
        }).resize();
    }
    function initTable(){
    	var source=$("#contentList").attr("data-id");
    	if(source==''){
    		laytable.render({
                elem: '#turntable',
                contentType: "application/json",
                url:contextPath+"/lottery/list",
                method: 'post',
                cellMinWidth: 80,
                page:true,
                limit:pageConf.pageSize,
                cols: [[
                    {field:'activityId', title: '活动ID', width:80, sort: true}
                    ,{field: 'activityTitle',title: '活动标题'}
                    ,{field:'', title: '抽奖模式'
                        ,templet:function(res){
                            return "翼豆抽奖";
                        }
                    }
                    ,{field: 'useAccount',title: '抽奖价格'}
                    ,{field: 'clickCount',title: '点击量'}
                    ,{field: 'inUserCount',title: '参与用户'}
                    ,{ field: 'statusCd', title: '状态'
                        ,width:120, templet: function (res) {
                            if (res.statusCd == '1') {
                                return "<span class=\"yellow\">进行中</span>";
                            } else if (res.statusCd == '2') {
                                return "<span class=\"black\">未开始</span>";
                            } else if (res.statusCd == '3') {
                                return "<span class=\"black\">未发布</span>";
                            }else if(res.statusCd == '4'){
                                return "<span class=\"gray\">已结束</span>";
                            }else{
                                return "<span class=\"gray\">已下架</span>";
                            }
                        }
                    }
                    ,{field: '',title: '活动时间',width:180
                        ,templet: function (res) {
                            return res.startTimeStr+'~'+res.endTimeStr;
                        }
                    }
                    ,{field: '',title: '操作',width:250,toolbar: '#toolBar'
                    }
                ]]
                ,id: 'turntableReload'
            });
    	}else{
    		laytable.render({
                elem: '#turntable',
                contentType: "application/json",
                url:contextPath+"/lottery/list",
                method: 'post',
                cellMinWidth: 80,
                page:true,
                limit:pageConf.pageSize,
                cols: [[
                       {type: 'radio'}
                    ,{field: 'activityTitle',title: '活动标题'}
                    ,{field:'', title: '抽奖模式'
                        ,templet:function(res){
                            return "翼豆抽奖";
                        }
                    }
                    ,{field: 'useAccount',title: '抽奖价格'}
                    ,{field: 'clickCount',title: '点击量'}
                    ,{field: 'inUserCount',title: '参与用户'}
                    ,{ field: 'statusCd', title: '状态'
                        ,width:120, templet: function (res) {
                            if (res.statusCd == '1') {
                                return "<span class=\"yellow\">进行中</span>";
                            } else if (res.statusCd == '2') {
                                return "<span class=\"black\">未开始</span>";
                            } else if (res.statusCd == '3') {
                                return "<span class=\"black\">未发布</span>";
                            }else if(res.statusCd == '4'){
                                return "<span class=\"gray\">已结束</span>";
                            }else{
                                return "<span class=\"gray\">已下架</span>";
                            }
                        }
                    }
                    ,{field: '',title: '活动时间',width:180
                        ,templet: function (res) {
                            return res.startTimeStr+'~'+res.endTimeStr;
                        }
                    }
                ]]
                ,id: 'turntableReload'
            });
    	}
    	if(source!='') {
            //单选框选中事件
            laytable.on('radio(turntable)', function (obj) {
                var res = obj.data;
                chooseActId = res.activityId;
                choosrActTitle=res.activityTitle;
            });
        }else{
        	//监听工具条
            laytable.on('tool(turntable)', function(obj){
                var data = obj.data;
                if(obj.event === 'detail'){
                    location.href = contextPath + '/lottery/detail?activityId=' + data.activityId;
                }else if(obj.event === 'copydetail'){
                	openActDetail(contextPath + '/lottery/detail?activityId=' + data.activityId);
                }else if(obj.event === 'del'){
                    if (data.statusCd == '1') {
                		layer.msg("进行中的活动无法删除，请先进行下线");
                		return;
                    }
                    layer.confirm("确认要删除吗?", function(index){
                    	deleteActInfo(obj);
                        layer.close(index);
                    });
                }else if(obj.event === 'edit'){
                    location.href = contextPath + '/lottery/edit?activityId=' + data.activityId;
                }else if(obj.event === 'release'){//上线
                	updateActstatusCd(data.activityId,'101');
                }else if(obj.event === 'stop'){//下架
                	updateActstatusCd(data.activityId,'104');
                }else if(obj.event === 'editWeight'){//调整概率、库存、活动结束时间等
                    location.href = contextPath + '/lottery/prizeSet?activityId=' + data.activityId;
                }
            });
        }        
    }
    //列表查询
    function searchList(){
        var params={
        };
        var actTitle=$.trim($("#actTitle").val());
        var statusCd=$("#statusCd").val();
        if(actTitle!=""){
            params.activityTitle=actTitle;
        }
        if(statusCd!=""){
            params.statusCd=statusCd;
        }
        var env=$("#env").val();
        if(env!=undefined&&env!=null){
            params.env=env;
            isTestEnv=env;
        }
        //执行重载
        laytable.reload('turntableReload', {
            page: {
                curr: 1 //重新从第 1 页开始
            }
            ,where: params
        });
    }
    
    //删除活动
    function deleteActInfo(obj) {
    	var data = obj.data;
    	var params = {
    		activityId : data.activityId
    	}
        var randTime="?t="+new Date().getTime();
        var index = layer.msg('删除中，请稍候',{icon: 16,time:false,shade:0.8});
        $.ajax({
            type: 'post' ,
            url: contextPath+"/lottery/deleteActInfo"+randTime,
            cache:false ,
            async:true ,
            contentType: "application/json",
            data: JSON.stringify(params),
            dataType:'json',
            success:function(response) {
                layer.close(index);
                //展示数据
                if (response.successed) {
                    obj.del();
                }else{
                    if(response.data.resultMsg!=null&&response.data.resultMsg!=undefined){
                        layer.msg(response.data.resultMsg);
                    }else{
                        layer.msg("活动删除失败");
                    }
                }
            },
            error:function(response){
                layer.close(index);
                layer.msg("活动删除失败");
            }
        });
        return false;
    }
    
    //更新活动状态
    function updateActstatusCd(activityId,statusCd) {    	
    	var params = {
    		activityId : activityId,
    		statusCd : statusCd
    	}
        var randTime="?t="+new Date().getTime();
    	var remark = '';
    	if(statusCd == '101'){
    		remark = '上线';
    	}else if(statusCd == '103'){
    		remark = '下线';
    	}else if(statusCd == '104'){
    		remark = '下架';
    	}
        var index = layer.msg(remark + '中，请稍候',{icon: 16,time:false,shade:0.8});
        $.ajax({
            type: 'post' ,
            url: contextPath+"/lottery/updateActstatusCd"+randTime,
            cache:false ,
            async:true ,
            contentType: "application/json",
            data: JSON.stringify(params),
            dataType:'json',
            success:function(response) {
                layer.close(index);
                //展示数据
                if (response.successed) {
                    layer.msg("活动" + remark + "成功");
                    searchList();
                }else{
                    if(response.data.resultMsg!=null&&response.data.resultMsg!=undefined){
                        layer.msg(response.data.resultMsg);
                    }else{
                        layer.msg("活动" + remark + "失败");
                    }
                }
            },
            error:function(response){
                layer.close(index);
                layer.msg("活动" + remark + "失败");
            }
        });
        return false;
    }  
    
    //打开活动详情
    function openActDetail(url) {
        var index = layui.layer.open({
            title: "活动详情",
            type: 2,
            area: ['900px',  "600px"],
            content: url,
            btn: ['确定', '关闭'],
            yes: function(index, layero){
            	
            }
        });
    }
});

