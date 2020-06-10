var taskRewardRule={};

//列表控制，每页展示数目等
var rewardRulePageConf={
	pageSize:10
}

layui.use(['form','layer','table','laypage','laydate'],function(){
	var laydate = layui.laydate;
	var table = layui.table;
	
	var taskRewardRuleTable;
	
	taskRewardRule.initLayTable=function(rewardId) {
		rewardRulePageConf.rewardId=rewardId;
		
		taskRewardRuleTable=table.render({
			id:"rewardRuleList",
			elem: '#rewardRuleList',
			method: "post",
			contentType: "application/json",
			url :contextPath+"/taskCenter/getTaskRewardRuleList",
			where:rewardRulePageConf,
			loading:false,
			page:{
				limit:rewardRulePageConf.pageSize,//每页展示数目
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
						{field: 'ruleId', title: 'ruleId', sort: true, fixed: 'left',hide:true},
						{field: 'ruleTitle', title: '规则名称'},
						{field: 'ruleDesc', title: '简介'},
						{field: 'ruleCode', title: '编码'},
						{field: 'statusCd', title: '规则类型',width:'15%',templet: function(info){
							var ruleType=info.ruleType;
							var ruleTypeName=ruleTypeParam[ruleType];
							
							if(ruleTypeName==null || ruleTypeName==""){
								ruleTypeName="其他";
							}
								
						    return ruleTypeName;
						}},
						{field: 'ruleData', title: '规则数据'},
						{field: 'opertionBtn', title: '操作',width:'20%',templet: function(info){
							var btnsHtml="";
							var statusCd=info.statusCd;
							var ruleType=info.ruleType;
							
							btnsHtml+="<a class=\"layui-btn layui-btn-mini layui-btn-xs\" href=\"javascript:void(0);\" onclick=\"taskRewardRule.toEditRewardRuleInfo('EDIT',"+info.ruleId+");\">详情</a>";
							
							if(ruleType=="8"){
		                		btnsHtml+="<a class=\"layui-btn  layui-btn-xs\" href=\"javascript:void(0);\" onclick=\"taskRule.showReguionRuleData("+info.ruleId+");\">查看地市 </a> ";
		                		btnsHtml+="<a class=\"layui-btn  layui-btn-xs\" href=\"javascript:void(0);\" onclick=\"taskRule.chooseRuleDataArea("+info.ruleId+");\">添加地市 </a> ";
		                	}
							
							if(taskIndex.checkIsCanOpe()){
								btnsHtml+="<a class=\"layui-btn  layui-btn-xs\" href=\"javascript:void(0);\" onclick=\"taskRewardRule.editStatus('102',"+info.ruleId+");\">删除 </a> ";
							}
							
							return btnsHtml;
						}}
		            ]
		        ]
		   }
		);
	}

	/**新增或修改任务组信息*/
	taskRewardRule.toEditRewardRuleInfo=function(editType,ruleId){
		var title="";
		var url=contextPath+"/taskCenter/toEditTaskRuleInfo?editType="+editType+"&infoFrom=reward";;
		var rewardId=rewardRulePageConf.rewardId;
		
		if(editType=="ADD"){
			title="添加奖品规则";
			url+="&rewardId="+rewardId;
		}else{
			title="修改奖品规则";
			url+="&ruleId="+ruleId+"&rewardId="+rewardId
		}
		
		var editGroupDialog = layer.open({
			type:2,
			title:title,
			content:url,
			area: ['600px', '410px']
		});
	}
	
	/**重载表格*/
	taskRewardRule.tableReload=function(tableId){
		layer.closeAll();
		
		taskRewardRuleTable.reload();
	}
	
	/**停用，启用，删除*/
	taskRewardRule.editStatus=function(statusCd,ruleId){
		var msg="";
		
		if(statusCd=="100"){
			msg="您确定要停用该奖品规则吗？";
		}else if(statusCd=="101"){
			msg="您确定要启用该奖品规则吗？";
		}else{
			msg="您确定要删除该奖品规则吗？";
		}
		
		layer.confirm(msg, {
			btn: ['确定','取消'] //按钮
		}, function(){
			layer.closeAll();
			
			var params={
				ruleId:ruleId,
				statusCd:statusCd,
				editFrom:"reward",
				rewardId:rewardRulePageConf.rewardId
			}
			
			$.ajax({
				type: "post",
				contentType: "application/json",
				data: JSON.stringify(params),
				url :contextPath+"/taskCenter/editTaskOrRewardRuleStatus",
				success : function(response){
					if(response.successed){
						layer.msg("操作成功");
						
						setTimeout("taskRewardRule.tableReload('rewardRuleList')",1200);
					}else{
						layer.msg(response.data.resultMsg);
					}
				}
			});
		}, function(){
			layer.closeAll();
		});
	}
	
	/**跳转规则方式组装页面*/
	taskRewardRule.toEditMode=function(forType){
		//forType-标识是组装展示还是完成条件
		//editfrom-标识来源是任务还是奖励
		var title="组装领取条件";
		var rewardId=rewardRulePageConf.rewardId;
		var url=contextPath+"/taskCenter/toEditRuleMode?forType="+forType+"&editFrom=reward"+"&rewardId="+rewardId;
		
		var editGroupDialog = layer.open({
			type:2,
			title:title,
			content:url,
			area: ['600px', '420px']
		});
	}
});

