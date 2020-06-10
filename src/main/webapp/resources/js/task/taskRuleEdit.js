var taskRuleEdit={};

layui.use(['form','layer'],function(){
	var form = layui.form;
	
	/**layui的监听form提交*/
	form.on('submit(saveInfoBtn)', function(data){
		taskRuleEdit.saveTaskRuleInfo();
		return false;
   	});
	
	/**保存数据信息*/
	taskRuleEdit.saveTaskRuleInfo=function(){
		//判断规则状态，如果是已启用状态，不可保存
		if(!window.parent.taskIndex.checkIsCanOpe()){
			layer.msg("任务已启用，无法修改规则信息");
			return;
		}
		
		//如果比对方式是-，两则之间的 ，要判断下数值是否填写
		var ruleData=$("#ruleData").val();
		var ruleDataSub=$("#ruleDataSub").val();
		var ruleDataSymbol=$("#ruleDataSymbol").val();
		
		if(ruleDataSymbol=="-" && (ruleDataSub==null || ruleDataSub=="")){
			layer.msg("请填写比对区间值");
			$("#ruleDataSub").focus();
			return;
		}
		
		//判断输入的只能是正整数
		var paStr=/(^[0-9]\d*$)/;
		
		//如果不是等于比对，数值不是正整数的，要进行提示
		if(ruleDataSymbol!="==" && !(paStr.test(ruleData))){
			layer.msg("规则数值只能是正整数");
			$("#ruleData").focus();
			return;
		}
		
		var params = $('#editForm').serializeArray();
		var idata = JSON.stringify(params);
		
		var params={
			datas:idata,
			ruleId:$("#ruleId").val(),
			editType:$("#editType").val()
		}
		
		$.ajax({
			type: "post",
			contentType: "application/json",
			data: JSON.stringify(params),
			url :contextPath+"/taskCenter/saveTaskRuleInfo",
			success : function(response){
				if(response.successed){
					layer.msg("保存数据信息成功");
					
					var infoFrom=$("#infoFrom").val();
					
					if(infoFrom=="task"){
						setTimeout("window.parent.taskRule.tableReload('taskRuleList')",1200);
					}else{
						setTimeout("window.parent.taskRewardRule.tableReload('rewardRuleList')",1200);
					}
					
				}else{
					layer.msg(response.data.resultMsg);
				}
			}
		});
	}
	
	form.on('select(ruleType)', function(data){
		taskRuleEdit.changeRuleTyp();
   	});
	
	//如果是下面这些规则类型，默认规则比对方式和比对数据
	var limitOpeRuleType=[8,16,5,12,13,14];
	
	taskRuleEdit.changeRuleTyp=function(){
		var ruleType=$("#ruleType").val();
		
		if($.inArray(parseInt(ruleType),limitOpeRuleType)!=-1){
			$("#ruleDataSymbol").val(">=");
			$("#ruleData").val(1);
		}else{
			$("#ruleDataSymbol").val("");
			$("#ruleData").val("");
		}
		
		//刷新layUi的表单
		form.render("select");
	}
	
	form.on('select(ruleDataSymbol)', function(data){
		taskRuleEdit.changeRuleDataSymbol();
   	});
	
	/**选择不同的比对类型，展示输入框*/
	taskRuleEdit.changeRuleDataSymbol=function(){
		var ruleDataSymbol=$("#ruleDataSymbol").val();
		
		if(ruleDataSymbol=="-"){
			$("#ruleDataSubDiv").show();
		}else{
			$("#ruleDataSubDiv").hide();
		}
	}
});

