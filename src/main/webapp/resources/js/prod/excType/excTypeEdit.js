var excTypeEdit={};

layui.use(['form','layer','laydate'],function(){
	var laydate = layui.laydate;
	var form = layui.form;
	  
	form.on('submit(saveInfoBtn)', function(data){
		excTypeEdit.saveExcTypeInfo();
		return false;
   	});
	
	excTypeEdit.saveExcTypeInfo=function(){

		var params = $('#editForm').serializeArray();
		var idata = JSON.stringify(params);
		
		var params={
			datas:idata,
			typeId:$("#typeId").val(),
			editType:$("#editType").val()
		}
		
		$.ajax({
			type: "post",
			contentType: "application/json",
			data: JSON.stringify(params),
			url :contextPath+"/excType/saveExcType",
			success : function(response){
				if(response.successed){
					layer.msg("保存数据信息成功");
					
					setTimeout("window.parent.excTypeList.tableReload()",1400);
				}else{
					layer.msg(response.data.resultMsg);
				}
			}
		});
	}
});

