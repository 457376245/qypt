var taskRewardEdit={};

layui.use(['form','layer'],function(){
	var form = layui.form;
	
	/**layui的监听form提交*/
	form.on('submit(saveInfoBtn)', function(data){
		taskRewardEdit.saveTaskRewardInfo();
		return false;
   	});
	
	/**保存数据信息*/
	taskRewardEdit.saveTaskRewardInfo=function(){
		var params = $('#editForm').serializeArray();
		var idata = JSON.stringify(params);
		
		var params={
			datas:idata,
			rewardId:$("#rewardId").val(),
			editType:$("#editType").val()
		}
		
		$.ajax({
			type: "post",
			contentType: "application/json",
			data: JSON.stringify(params),
			url :contextPath+"/taskCenter/editTaskRewardInfo",
			success : function(response){
				if(response.successed){
					layer.msg("保存数据信息成功");
					
					
					setTimeout("window.parent.taskReward.tableReload('rewardList')",1200);
				}else{
					layer.msg(response.data.resultMsg);
				}
			}
		});
	}
});

