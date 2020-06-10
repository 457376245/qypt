var prodGroupEdit={};

layui.use(['form','layer','laydate'],function(){
	var laydate = layui.laydate;
	var form = layui.form;
	  
	form.on('submit(saveInfoBtn)', function(data){
		prodGroupEdit.saveGroupInfo();
		return false;
   	});
	
	prodGroupEdit.saveGroupInfo=function(){

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
			url :contextPath+"/prodGroup/saveProdGroup",
			success : function(response){
				if(response.successed){
					layer.msg("保存数据信息成功");
					
					setTimeout("window.parent.prodGroupList.tableReload()",1400);
				}else{
					layer.msg(response.data.resultMsg);
				}
			}
		});
	}
});

