var taskIndex={};

var ruleTypeParam={
	"1":"星级限制","2":"签到数目",
	"3":"语音量","4":"使用流量",
	"5":"5G终端","10":"每日兑换量",
	"11":"每日参与活动","12":"5G专属",
	"13":"全屋WIFI","14":"智能看家",
	"16":"当日分享次数","8":"地市限制",
	"17":"总计分享次数"
}

//列表控制，每页展示数目等
var pageConf={
	pageSize:5
}

/**保存目前操作的ID，状态等数据*/
var allTaskInfo={};
var nowTaskInfo={};

layui.use(['form','layer','table','laypage','laydate'],function(){
	var laydate = layui.laydate;
	var table = layui.table;
	
	//进入页面，初始化加载列表
	$(document).ready(function(){
		taskIndex.initLayTable();
	});
	
	var taskTable;
	
	//绑定查询按钮点击事件
	$("#searchInfoBtn").click(function(){
		var taskName_=$("#taskName").val();
		var statusCd_=$("#statusCd").val();
		var groupId_=$("#groupId").val();
		
		pageConf.taskName=taskName_;
		pageConf.statusCd=statusCd_;
		pageConf.groupId=groupId_;
		
		//重新加载表格
		table.reload('taskListTable',{
			where:pageConf,
			page:{
				curr:1
			}
		})
		
		$("#taskRuleDiv").hide();
	});
	
	/**初始列表*/
	taskIndex.initLayTable=function() {
		taskTable=table.render({
			id:"taskListTable",
			elem: '#taskListTable',
			method: "post",
			contentType: "application/json",
			url :contextPath+"/taskCenter/getTaskList",
			where:pageConf,
			loading:false,
			page:{
				limit:pageConf.pageSize,//每页展示数目
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
		             	{field: 'taskId', title: 'ID', sort: true, fixed: 'left',hide:true},
		              	{field: 'taskName', title: '任务名称'},
		              	{field: 'taskDesc', title: '简介'},
		              	{field: 'taskCode', title: '编码'},
		              	{field: 'sortSeq', title: '排序号',width:'5%'},
		              	{field: 'ownerGroupTitle', title: '归属分组'},
		              	{field: 'statusCd', title: '状态',width:'5%',templet: function(info){
		              		var statusCd=info.statusCd;
		              		var statusCdName="";
		              		var statusCdColor="";
		              		if(statusCd=="100"){
		        				statusCdName="待启用";
		        				statusCdColor="gray"
		        			}else if(statusCd=="101"){
		        				statusCdName="已启用";
		        				statusCdColor="yellow"
		        			}else{
		        				statusCdName="其他";
		        			}
		              		
		                    return '<span class="'+statusCdColor+'">'+statusCdName+'</span>';
		                }},
		                {field: 'satrtAndEndTimeShow', title: '起止时间',width:'20%',templet: function(info){
		                	return info.startDate+' ~ '+info.endDate;
		                }},
		                {field: 'opertionBtn', title: '操作',width:'20%',templet: function(info){
		                	var btnsHtml="";
		                	var statusCd=info.statusCd;
		                	var taskId=info.taskId;
		                	
		                	allTaskInfo[taskId]=info;
		                	
		                	btnsHtml+="<a class=\"layui-btn layui-btn-mini layui-btn-xs\" href=\"javascript:void(0);\" onclick=\"taskIndex.toEditTaskInfo('EDIT',"+info.taskId+");\">详情</a>";
							
							btnsHtml+="<a class=\"layui-btn  layui-btn-xs\" href=\"javascript:void(0);\" onclick=\"taskIndex.beforeShowInfo("+info.taskId+",'RULE');\">规则 </a> ";
							
							btnsHtml+="<a class=\"layui-btn  layui-btn-xs\" href=\"javascript:void(0);\" onclick=\"taskIndex.beforeShowInfo("+info.taskId+",'REWARD');\">奖品 </a> ";
							
							btnsHtml+="<a class=\"layui-btn  layui-btn-xs\" href=\"javascript:void(0);\" onclick=\"taskIndex.beforeShowInfo("+info.taskId+",'IMAGE');\">图片 </a> ";
							
							btnsHtml+="<a class=\"layui-btn  layui-btn-xs\" href=\"javascript:void(0);\" onclick=\"taskIndex.editTaskStatus('102',"+info.taskId+");\">删除 </a> ";
							
							if (statusCd=="100") {
								btnsHtml+="<a class=\"layui-btn  layui-btn-xs\" href=\"javascript:void(0);\" onclick=\"taskIndex.editTaskStatus('101',"+info.taskId+");\">启用</a> ";
							}else if(statusCd=="101"){
								//暂不是用停用功能
								btnsHtml+="<a class=\"layui-btn  layui-btn-xs\" href=\"javascript:void(0);\" onclick=\"taskIndex.editTaskStatus('100',"+info.taskId+");\">停用</a> ";
							}
							
							return btnsHtml;
		                }},
		            ]
		        ]
		   }
		);
	}

	/**新增或修改任务信息*/
	taskIndex.toEditTaskInfo=function(editType,taskId){
		var title="";
		var url=contextPath+"/taskCenter/toEditTaskInfo?editType="+editType;
		
		if(editType=="ADD"){
			title="添加任务";
		}else{
			title="修改任务";
			url+="&taskId="+taskId
		}
		
		var editGroupDialog = layer.open({
			type:2,
			title:title,
			content:url,
			area: ['660px', '450px']
		});
		
		//layui.layer.full(editGroupDialog);
	}
	
	/**重载表格*/
	taskIndex.tableReload=function(){
		layer.closeAll();
		
		//隐藏规则等展示
		$("#taskRuleDiv").hide();
		
		//重载table
		taskTable.reload();
	}
	
	/**停用，启用，删除*/
	taskIndex.editTaskStatus=function(statusCd,taskId){
		var msg="";
		
		if(statusCd=="100"){
			msg="您确定要停用该任务吗？";
		}else if(statusCd=="101"){
			msg="启用任务后，规则条件和奖品等部分信息将无法修改，您确定要启用该任务吗？";
		}else{
			msg="删除任务后，数据无法恢复，您确定要删除该任务吗？";
		}
		
		layer.confirm(msg, {
			btn: ['确定','取消'] //按钮
		}, function(){
			layer.closeAll();
			
			var params={
				taskId:taskId,
				statusCd:statusCd
			}
			
			$.ajax({
				type: "post",
				contentType: "application/json",
				data: JSON.stringify(params),
				url :contextPath+"/taskCenter/editTaskStatus",
				success : function(response){
					if(response.successed){
						layer.msg("操作成功");
						
						setTimeout("taskIndex.tableReload('taskListTable')",1200);
					}else{
						layer.msg(response.data.resultMsg);
					}
				}
			});
		}, function(){
			layer.closeAll();
		});
	}
	
	taskIndex.beforeShowInfo=function(taskId,opeType){
		var taskInfo=allTaskInfo[taskId];
		
		nowTaskInfo=taskInfo;
		
		var taskName=taskInfo.taskName;
		var taskCode=taskInfo.taskCode;
		var statusCd=taskInfo.statusCd;
		
		//已经启用的任务，添加按钮什么的都隐藏
		if(statusCd=="101"){
			$("#addTaskRuleBtn").hide();
			$("#addTaskRewardBtn").hide();
			$("#addRewardRuleBtn").hide();
		}else{
			$("#addTaskRuleBtn").show();
			$("#addTaskRewardBtn").show();
			$("#addRewardRuleBtn").show();
		}
		
		if(opeType=="RULE"){
			taskIndex.showTaskRule(taskId,taskName,taskCode);
		}else if(opeType=="REWARD"){
			taskIndex.showTaskReward(taskId,taskName,taskCode);
		}else{
			taskIndex.showTaskImg(taskId,taskName,taskCode);
		}
	}
	
	/**展示任务下属的规则数据*/
	taskIndex.showTaskRule=function(taskId,taskName,taskCode){
		$(".taskDivCla_").hide();
		
		$("#taskRuleP").html("任务基础规则(归属："+taskName+"-"+taskCode+")");
		
		$("#taskRuleDiv").show();
		
		taskRule.initLayTable(taskId);
	}
	
	/**展示任务奖励列表*/
	taskIndex.showTaskReward=function(taskId,taskName,taskCode){
		$("#taskTab").hide();
		$("#taskRewardTab").show();
		
		//展示任务奖励
		$("#rewardListP").html("任务奖励列表(归属："+taskName+"-"+taskCode+")");
		//隐藏任务奖励规则
		$("#rewardRuleDiv").hide();
		
		taskReward.initLayTable(taskId);
	}
	
	/**展示任务下属的规则数据*/
	taskIndex.showTaskImg=function(taskId,taskName,taskCode){
		$(".taskDivCla_").hide();
		
		$("#taskImgP").html("任务广告图片(归属："+taskName+"-"+taskCode+")");
		
		$("#taskImgDiv").show();
		
		taskImg.initLayTable(taskId);
	}
	
	/**重载缓存*/
	taskIndex.reloadTaskAndRewardRedis=function(){
		var params={}
			
		$.ajax({
			type: "post",
			contentType: "application/json",
			data: JSON.stringify(params),
			url :contextPath+"/taskCenter/reloadTaskAndRewardRedis",
			success : function(response){
				if(response.successed){
					layer.msg("刷新缓存成功");
				}else{
					layer.msg(response.data.resultMsg);
				}
			}
		});
	}
	
	/**校验是否可进行操作*/
	taskIndex.checkIsCanOpe=function(){
		var taskStatusCd=nowTaskInfo.statusCd;
		
		if(taskStatusCd=='101'){
			return false;
		}else{
			return true;
		}
	}
});

