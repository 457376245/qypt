var prodClassEdit={};

layui.use(['form','layer','laydate'],function(){
	var laydate = layui.laydate;
	var form = layui.form;
	  
	form.on('submit(saveInfoBtn)', function(data){
		prodClassEdit.saveClassInfo();
		return false;
   	});
	
	prodClassEdit.saveClassInfo=function(){

		var params = $('#editForm').serializeArray();
		var idata = JSON.stringify(params);
		
		var params={
			datas:idata,
			oldClassCode:$("#oldClassCode").val(),
			editType:$("#editType").val()
		}
		
		$.ajax({
			type: "post",
			contentType: "application/json",
			data: JSON.stringify(params),
			url :contextPath+"/prodClass/saveProdClass",
			success : function(response){
				if(response.successed){
					layer.msg("保存数据信息成功");
					
					setTimeout("window.parent.prodClassList.tableReload()",1400);
				}else{
					layer.msg(response.data.resultMsg);
				}
			}
		});
	}
});

