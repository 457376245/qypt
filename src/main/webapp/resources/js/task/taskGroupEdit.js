var taskGroupEdit={};

layui.use(['form','layer','laydate'],function(){
	var laydate = layui.laydate;
	var form = layui.form;
	  
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
	
	form.on('submit(saveInfoBtn)', function(data){
		taskGroupEdit.saveGroupInfo();
		return false;
   	});
	
	taskGroupEdit.saveGroupInfo=function(){

		var params = $('#editForm').serializeArray();
		var idata = JSON.stringify(params);
		
		var params={
			datas:idata,
			groupId:$("#groupId").val(),
			editType:$("#editType").val()
		}
		
		$.ajax({
			type: "post",
			contentType: "application/json",
			data: JSON.stringify(params),
			url :contextPath+"/taskCenter/saveTaskGroup",
			success : function(response){
				if(response.successed){
					layer.msg("保存数据信息成功");
					
					setTimeout("window.parent.taskGroupIndex.tableReload('taskGroupListTable')",1400);
				}else{
					layer.msg(response.data.resultMsg);
				}
			}
		});
	}
});

