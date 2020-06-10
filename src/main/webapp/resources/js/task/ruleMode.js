var ruleMode={};

//列表控制，每页展示数目等
var pageConf={
	pageSize:10
}

var _nowRuleMode=[];

layui.use(['form','layer','table'],function(){
	var laydate = layui.laydate;
	var table = layui.table;
	
	$(document).ready(function(){
		ruleMode.initLayTable();
	});
	
	var ruleModeTable;
	
	ruleMode.initLayTable=function() {
		var params={
			taskId:$("#taskId").val(),
			rewardId:$("#rewardId").val(),
			forType:$("#forType").val(),
			editFrom:$("#editFrom").val()
		}
		
		$.ajax({
			type: "post",
			contentType: "application/json",
			data: JSON.stringify(params),
			url :contextPath+"/taskCenter/getRuleModeList",
			success : function(response){
				if(response.successed){
					var ruleModeData=response.data.ruleMode;
					
					if(ruleModeData!=null && ruleModeData!=""){
						_nowRuleMode=JSON.parse(ruleModeData);
					}
					
					ruleModeTable=table.render({
						id:"ruleModeList",
						elem: '#ruleModeList',
						data:response.data.infoList,
						loading:false,
					    cols: 
					    	[
					            [ 
					              	{field: 'ruleModeName', title: '条件'},
					              	{field: 'ruleModeData', title: '条件组成'},
					                {field: 'opertionBtn', title: '操作',width:'20%',templet: function(info){
					                	var btnsHtml="";

					                	if(window.parent.taskIndex.checkIsCanOpe()){
					                		btnsHtml+="<a class=\"layui-btn  layui-btn-xs\" href=\"javascript:void(0);\" onclick=\"ruleMode.delRuleMode('"+info.ruleModeId+"');\">删除 </a> ";
					                	}
										
										return btnsHtml;
					                }},
					            ]
					        ]
					   }
					);
				}else{
					layer.msg(response.data.resultMsg);
				}
			}
		});
	}

	/**展示规则列表*/
	ruleMode.toShowRuleListChoose=function(editType,groupId){
		if(!window.parent.taskIndex.checkIsCanOpe()){
			layer.msg("任务已启用，无法进行该操作");
			return;
		}
		
		var title="";
		var forType=$("#forType").val();
		var editFrom=$("#editFrom").val();
		var taskId=$("#taskId").val();
		var rewardId=$("#rewardId").val();
		
		var url=contextPath+"/taskCenter/toShowRuleListChoose?forType="+forType+"&editFrom="+editFrom+"&taskId="+taskId+"&rewardId="+rewardId;
		
		if(editType=="ADD"){
			title="添加条件";
		}
		
		var openDialog= layer.open({
			type:2,
			title:title,
			content:url,
			area: ['420px', '260px']
		});
	}
	
	/**选择规则返回*/
	ruleMode.chooseRuleBack=function(chooseRules){
		layer.closeAll();
		if(chooseRules!=null && chooseRules.length>0){
			_nowRuleMode.push(chooseRules)
			var ruleModeStr=JSON.stringify(_nowRuleMode);
			
			var forType=$("#forType").val();
			
			var params={
				rewardId:$("#rewardId").val(),
				taskId:$("#taskId").val(),
				forType:$("#forType").val(),
				editFrom:$("#editFrom").val()
			}
			
			if(forType=="SHOW"){
				params.showRuleMode=ruleModeStr;
			}else{
				params.ruleMode=ruleModeStr;
			}
			
			$.ajax({
				type: "post",
				contentType: "application/json",
				data: JSON.stringify(params),
				url :contextPath+"/taskCenter/editRuleMode",
				success : function(response){
					if(response.successed){
						//添加成功后，再将数据放置到数组中
						_nowRuleMode.push(chooseRules);
						
						layer.msg("添加条件成功");
						setTimeout("ruleMode.initLayTable()",1200);
					}else{
						layer.msg(response.data.resultMsg);
						setTimeout("ruleMode.initLayTable()",1200);
					}
				}
			});
		}
	}
	
	/**选择规则返回*/
	ruleMode.delRuleMode=function(delRuleGroupId){
		_nowRuleMode.splice(delRuleGroupId, 1);
		
		var ruleModeJsonStr=JSON.stringify(_nowRuleMode);
		
		if(_nowRuleMode.length==0){
			ruleModeJsonStr="";
		}
		
		var forType=$("#forType").val();
		
		var params={
			rewardId:$("#rewardId").val(),
			taskId:$("#taskId").val(),
			forType:$("#forType").val(),
			editFrom:$("#editFrom").val()
		}
		
		if(forType=="SHOW"){
			params.showRuleMode=ruleModeJsonStr;
		}else{
			params.ruleMode=ruleModeJsonStr;
		}
		
		$.ajax({
			type: "post",
			contentType: "application/json",
			data: JSON.stringify(params),
			url :contextPath+"/taskCenter/editRuleMode",
			success : function(response){
				if(response.successed){
					layer.msg("删除成功");
					setTimeout("ruleMode.initLayTable()",1200);
				}else{
					layer.msg(response.data.resultMsg);
					setTimeout("ruleMode.initLayTable()",1200);
				}
			}
		});
	}
});

