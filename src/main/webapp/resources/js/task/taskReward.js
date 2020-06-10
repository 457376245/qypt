var taskReward={};

//列表控制，每页展示数目等
var rewardPageConf={
	pageSize:10
}

/**返回任务列表*/
taskReward.backToTaskList=function(){
	$("#taskTab").show();
	$("#taskRewardTab").hide();
}

layui.use(['form','layer','table','laypage','laydate'],function(){
	var laydate = layui.laydate;
	var table = layui.table;
	
	var taskRewardTable;
	
	taskReward.initLayTable=function(taskId) {
		rewardPageConf.taskId=taskId;
		
		taskRewardTable=table.render({
			id:"rewardList",
			elem: '#rewardList',
			method: "post",
			contentType: "application/json",
			url :contextPath+"/taskCenter/getTaskRewardList",
			where:rewardPageConf,
			loading:false,
			page:{
				limit:rewardPageConf.pageSize,//每页展示数目
				groups: 5, //连续显示分页数
				prev:'上一页',
				next:'下一页',
				layout:['prev', 'page', 'next','count']
			},
			parseData: function(res){
				//需要将数据转化成lay可以解析的数据
				return {
					"code": res.code, //解析接口状态
					"msg": res.data.resultMsg, //解析提示文本
					"count": res.data.infoCount, //解析数据长度
					"data": res.data.infoList //解析数据列表
				};
			}
		    ,cols: 
		    	[
		            [ 
		             	{field: 'rewardId', title: '奖励ID',width:'5%', fixed: 'left'},
		              	{field: 'prizeTitle', title: '奖品名称'},
		              	{field: 'prizeDesc', title: '简介'},
		              	{field: 'prizeType', title: '奖品类型',templet: function(info){
		              		var prizeType=info.prizeType;
		              		var prizeTypeName="";
		              		if(prizeType=="1"){
		              			prizeTypeName="翼豆";
		        			}else if(prizeType=="2"){
		        				prizeTypeName="虚拟卡";
		        			}else if(prizeType=="3"){
		        				prizeTypeName="流量包";
		        			}else if(prizeType=="4"){
		        				prizeTypeName="话费";
		        			}else if(prizeType=="5"){
		        				prizeTypeName="转盘次数";
		        			}else{
		        				prizeTypeName="其他";
		        			}
		              		
		                    return prizeTypeName;
		                }},
		              	{field: 'userPrizeCount', title: '每次奖励数量'},
		              	{field: 'prizeStock', title: '奖品剩余库存'},
		                {field: 'opertionBtn', title: '操作',width:'20%',templet: function(info){
		                	var btnsHtml="";
		                	var statusCd=info.statusCd;
		                	
		                	btnsHtml+="<a class=\"layui-btn layui-btn-mini layui-btn-xs\" href=\"javascript:void(0);\" onclick=\"taskReward.toEditDataInfo('EDIT',"+info.rewardId+");\">详情</a>";
		                	
							btnsHtml+="<a class=\"layui-btn  layui-btn-xs\" href=\"javascript:void(0);\" onclick=\"taskReward.showRewardRule("+info.rewardId+",'"+info.prizeTitle+"');\">奖品规则 </a> ";
							
							
							if(taskIndex.checkIsCanOpe()){
								btnsHtml+="<a class=\"layui-btn  layui-btn-xs\" href=\"javascript:void(0);\" onclick=\"taskReward.editStatus('102',"+info.rewardId+");\">删除 </a> ";
							}
							
							return btnsHtml;
		                }},
		            ]
		        ]
		   }
		);
	}
	
	/**打开选择奖品页面*/
	taskReward.toChooseReward=function(){
		var indexa=layui.layer.open({
	        title: "奖品选择",
	        type: 2,
	        area: ['900px','450px'],
	        content: contextPath+"/qyproduct/prizeChoose",
	        btn: ['确定', '关闭'],
	        yes: function(index, layero){
	            var iframeWin = window[layero.find('iframe')[0]['name']];
	            var result=iframeWin.validSumit();
	            if(result==0){
	            	//返回后，调用奖品新增
	            	taskReward.addTaskRewardInfo(iframeWin.returnPrice);
	                layer.close(indexa);
	            }else{
	                layer.msg(iframeWin.returnPrice.msg);
	            }
	        }
	    });
	}
	
	/**调用任务奖品新增*/
	taskReward.addTaskRewardInfo=function(params){
        params.taskId=rewardPageConf.taskId;
		
		$.ajax({
			type: "post",
			contentType: "application/json",
			data: JSON.stringify(params),
			url :contextPath+"/taskCenter/addTaskRewardInfo",
			success : function(response){
				if(response.successed){
					layer.msg("添加奖励成功");
					
					setTimeout("taskReward.tableReload('rewardList')",1200);
				}else{
					layer.msg(response.data.resultMsg);
				}
			}
		});
	}

	/**新增或修改任务组信息*/
	taskReward.toEditDataInfo=function(editType,rewardId){
		var title="";
		var url=contextPath+"/taskCenter/toEditTaskRewardInfo?editType="+editType+"&taskId="+rewardPageConf.taskId;
		
		if(editType=="ADD"){
			title="添加分组";
		}else{
			title="修改分组";
			url+="&rewardId="+rewardId
		}
		
		var editGroupDialog = layer.open({
			type:2,
			title:title,
			content:url,
			area: ['600px', '180px']
		});
		
		//layui.layer.full(editGroupDialog);
	}
	
	/**重载表格*/
	taskReward.tableReload=function(){
		layer.closeAll();
		
		taskRewardTable.reload();
	}
	
	/**停用，启用，删除*/
	taskReward.editStatus=function(statusCd,rewardId){
		var msg="";
		
		if(statusCd=="100"){
			msg="您确定要停用该奖励吗？";
		}else if(statusCd=="101"){
			msg="您确定要启用该奖励吗？";
		}else{
			msg="您确定要删除该奖励吗？";
		}
		
		layer.confirm(msg, {
			btn: ['确定','取消'] //按钮
		}, function(){
			layer.closeAll();
			
			var params={
				rewardId:rewardId,
				editType:'EDIT',
				statusCd:statusCd
			}
			
			$.ajax({
				type: "post",
				contentType: "application/json",
				data: JSON.stringify(params),
				url :contextPath+"/taskCenter/editTaskRewardStatus",
				success : function(response){
					if(response.successed){
						layer.msg("操作成功");
						
						setTimeout("taskReward.tableReload('rewardList')",1200);
					}else{
						layer.msg(response.data.resultMsg);
					}
				}
			});
		}, function(){
			layer.closeAll();
		});
	}
	
	/**展示任务奖品下属的规则数据*/
	taskReward.showRewardRule=function(rewardId,prizeTitle){
		$("#rewardRuleP").html("奖品基础规则(归属："+prizeTitle+"-"+rewardId+")");
		
		$("#rewardRuleDiv").show();
		
		taskRewardRule.initLayTable(rewardId);
	}
});

