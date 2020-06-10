var warningDatas = [];//封装告警处理数据
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
        //查询
        $("body").on("click", ".search_btn", function () {
            searchList();
        }).resize();
        $("body").on("click", ".batch_btn", function () {
            openDealWarning();
        });
    }
    function initTable(){
    	var source = $("#contentList").attr("data-id");
    	if(source==''){
    		laytable.render({
                elem: '#monitortable',
                contentType: "application/json",
                url:contextPath+"/monitor/list",
                method: 'post',
                cellMinWidth: 80,
                page:true,
                limit:pageConf.pageSize,
                cols: [[
                    {field:'WARNING_ID', title: '警告ID', width:80, sort: true}
                    ,{field: 'MEMBER_PHONE',title: '会员号码', width:120}
                    ,{field: 'PRODUCT_CODE',title: '权益编码', width:170}
                    ,{field: 'PRODUCT_TITLE',title: '权益名称', width:170}
                    ,{ field: 'WARNING_TYPE', title: '警告类型'
                        ,width:120, templet: function (res) {
                            if (res.WARNING_TYPE == '101') {
                                return "<span class=\"yellow\">库存不足警告</span>";
                            } else if (res.WARNING_TYPE == '102') {
                                return "<span class=\"yellow\">领取奖励失败警告</span>";
                            }else{
                            	return "<span class=\"yellow\">其他警告</span>";
                            }
                        }
                    }
                    ,{field: 'WARNING_CONTENT',title: '警告内容', width:350}
                    ,{ field: 'OPT_RESULT', title: '处理结果'
                        ,width:80, templet: function (res) {
                            if (res.OPT_RESULT == '101') {
                                return "<span class=\"yellow\">待处理</span>";
                            } else if (res.OPT_RESULT == '102') {
                                return "<span class=\"black\">已处理</span>";
                            }
                        }
                    } 
                    ,{field: 'OPT_REMARK',title: '处理说明', edit: 'text', width:100}
                    ,{field: 'ADD_TIME',title: '告警时间', width:150,
                    	templet: function (res) {
                    		return datetimeFormat(res.ADD_TIME);
    	                }}
                    ,{field: 'UPDATE_DATE',title: '处理时间', width:150,
                    	templet: function (res) {
                    		if (res.OPT_RESULT == '101') {
                                return "";
                            } else if (res.OPT_RESULT == '102') {
                            	return datetimeFormat(res.UPDATE_DATE);
                            }
    	                }}
                    
                    ,{field: '',title: '操作',width:70,toolbar: '#toolBar'
                    }
                ]]
                ,id: 'monitortable'
            });
    	}else{
    		laytable.render({
                elem: '#monitortable',
                contentType: "application/json",
                url:contextPath+"/monitor/list?source=" + source,
                method: 'post',
                cellMinWidth: 80,
                page:true,
                limit:pageConf.pageSize,
                cols: [[
                    {type: 'checkbox'}
                    ,{field: 'MEMBER_PHONE',title: '会员号码', width:120}
                    ,{field: 'PRODUCT_CODE',title: '权益编码', width:170}
                    ,{field: 'PRODUCT_TITLE',title: '权益名称', width:170}
                    ,{ field: 'WARNING_TYPE', title: '警告类型'
                        ,width:120, templet: function (res) {
                            if (res.WARNING_TYPE == '101') {
                                return "<span class=\"yellow\">库存不足警告</span>";
                            } else if (res.WARNING_TYPE == '102') {
                                return "<span class=\"yellow\">领取奖励失败警告</span>";
                            }else{
                            	return "<span class=\"yellow\">其他警告</span>";
                            }
                        }
                    }
                    ,{ field: 'OPT_RESULT', title: '处理结果'
                        ,width:80, templet: function (res) {
                            if (res.OPT_RESULT == '101') {
                                return "<span class=\"yellow\">待处理</span>";
                            } else if (res.OPT_RESULT == '102') {
                                return "<span class=\"black\">已处理</span>";
                            }
                        }
                    } 
                    ,{field: 'OPT_REMARK',title: '处理说明', edit: 'text', width:150}
                ]]
                ,id: 'monitortable'
            });
    	}
		
		//监听单元格编辑
		laytable.on('edit(monitortable)', function(obj){
            var value = obj.value //得到修改后的值
                ,data = obj.data //得到所在行所有键值
                ,field = obj.field; //得到字段
            if(field=='OPT_REMARK'){
                if (value.length <= 0) {
                    layer.msg("处理说明不能为空");
                    reloadTable();
                }else{
                	var warningData = {
                    	"warningId" : data.WARNING_ID,
                        "warningRemark":value
                    }
                	warningDatas.push(warningData);
                }
            }
        });
		if(source!='') {
            //多选框选中事件
            /*laytable.on('checkbox(monitortable)', function(obj){//checkbox 复选框选中事件
        		var res = obj.data;
        		console.log(res);
            	//如果状态是选中的    
            	if (obj.checked) {
            		alert(res.WARNING_ID + "选中了");
            		var warningData = {
                    	"warningId" : res.WARNING_ID,
                        "warningRemark":res.OPT_REMARK
                    }
            		warningDatas.push(warningData);
            	}else{
            		for (var it in warningDatas) {
                        if(warningDatas[it].warningId == res.WARNING_ID){
                        	warningDatas.splice((it + 1), 1);
                        }
                    }
            	}
            });*/
            laytable.on('checkbox(layTableAllChoose)', function(data){
                var id = data.value;
                console.log(data.elem); //得到checkbox原始DOM对象
                console.log(data.elem.checked); //是否被选中，true或者false
                console.log(data.value); //复选框value值，也可以通过data.elem.value得到
                console.log(data.othis); //得到美化后的DOM对象
              });  
        }else{
        	//监听工具条
            laytable.on('tool(monitortable)', function(obj){
                var data = obj.data;
                if(obj.event === 'deal'){//处理
                	updateLogWarning(data.WARNING_ID,warningDatas);
                }
            }); 
        }  
    }
    //列表查询
    function searchList(){
        var rightsName = $.trim($("#rightsName").val());
        var rightsCode = $.trim($("#rightsCode").val());
        var optResult = $.trim($("#optResult").val());
        var params={
        	rightsName : rightsName, 
        	rightsCode : rightsCode,
        	optResult : optResult
        };
        //执行重载
        laytable.reload('monitortable', {
            page: {
                curr: 1 //重新从第 1 页开始
            }
            ,where: params
        });
    }
    
    function updateLogWarning(warningId,warningDatas) {
    	var flag = false;
    	if(warningId == '' || warningId == null || warningId == undefined){//批量处理
    		flag = true;
    	}else{
    		console.log(JSON.stringify(warningDatas));
    		if(warningDatas != null && warningDatas.length > 0){
        		for (var it in warningDatas) {
                    if(warningDatas[it].warningRemark == ''){
                    	layer.msg("处理说明不能为空");
                    	return false;
                    }
                    if(warningDatas[it].warningId == warningId){
                    	flag = true;
                    }
                }
        	}else{
        		layer.msg("处理说明不能为空");
            	return false;
        	}
    	}
    	var params = {};
    	if(flag){
	    	params.warningDatas = warningDatas;
	        var randTime="?t="+new Date().getTime();
	        var index = layer.msg('处理中，请稍候',{icon: 16,time:false,shade:0.8});
	        $.ajax({
	            type: 'post' ,
	            url: contextPath+"/monitor/updateLogWarning"+randTime,
	            cache:false ,
	            async:true ,
	            contentType: "application/json",
	            data: JSON.stringify(params),
	            dataType:'json',
	            success:function(response) {
	                layer.close(index);
	                //展示数据
	                if (response.successed) {
	                    layer.msg("告警处理成功");
	                    searchList();
	                }else{
	                    if(response.data.resultMsg!=null&&response.data.resultMsg!=undefined){
	                        layer.msg(response.data.resultMsg);
	                    }else{
	                        layer.msg("告警处理失败");
	                    }
	                }
	            },
	            error:function(response){
	                layer.close(index);
	                layer.msg("告警处理失败");
	            }
	        });
	        return false;
    	}    	
    }
    
    //打开批量处理
    function openDealWarning() {
        var index = layui.layer.open({
            title: "告警选择",
            type: 2,
            area: ['900px',  "550px"],
            content: contextPath+"/monitor/index?source=choose",
            btn: ['确定', '关闭'],
            yes: function(index, layero){
                var iframeWin = window[layero.find('iframe')[0]['name']];
                var warningDatas = iframeWin.warningDatas;
                alert("这里的个数："+warningDatas.length);
                if(warningDatas != null){
                	updateLogWarning('',warningDatas);
                    layer.close(index);
                }else{
                    layer.msg("请选择需要处理的告警记录");
                }
            }
        });
    }
});

