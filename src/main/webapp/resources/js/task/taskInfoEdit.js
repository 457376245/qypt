var taskInfoEdit={};

layui.use(['form','layer','laydate'],function(){
	var laydate = layui.laydate;
	var form=layui.form;
	  
	laydate.render({
		elem: '#showTime', //指定元素
		type: 'datetime', //日期格式类型
		trigger: 'click'
	});
	
	laydate.render({
		elem: '#startTime', //指定元素
		type: 'datetime', //日期格式类型
		trigger: 'click'
	});
	
	laydate.render({
		elem: '#endTime', //指定元素
		type: 'datetime', //日期格式类型
		trigger: 'click'
	});
	
	/**查看小图标*/
	taskInfoEdit.showImgInfo=function(){
		var title="查看小图标";
		
		var imgCode=$("#imgCode").val();
		
		if(imgCode==null || imgCode==""){
			layer.msg("请选择小图标");
			return;
		}
		
		var imgUrl=contextPath+"/images/"+imgCode;
		
		var content="<img src=\""+imgUrl+"\" style=\"max-width:240px;max-height:210px;margin-top:10px;\" />";
		
		var editGroupDialog = layer.open({
			type:1,
			title:title,
			content:content,
			area: ['240px', '240px']
		});
	}
	
	/**layui的监听form提交*/
	form.on('submit(saveInfoBtn)', function(data){
		taskInfoEdit.saveTaskInfo();
		return false;
   	});
	
	taskInfoEdit.saveTaskInfo=function(){

		var params = $('#editForm').serializeArray();
		var idata = JSON.stringify(params);
		
		var params={
			datas:idata,
			taskId:$("#taskId").val(),
			editType:$("#editType").val()
		}
		
		$.ajax({
			type: "post",
			contentType: "application/json",
			data: JSON.stringify(params),
			url :contextPath+"/taskCenter/saveTaskInfo",
			success : function(response){
				if(response.successed){
					layer.msg("保存数据信息成功");
					
					setTimeout("window.parent.taskIndex.tableReload('taskListTable')",1200);
				}else{
					layer.msg(response.data.resultMsg);
				}
			}
		});
	}
});

