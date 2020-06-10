var taskImgEdit={};

var imgInfo={
	"imgId":"",
	"fileDir":""
}

layui.use(['form','layer','upload'],function(){
	var form = layui.form;
	var upload = layui.upload;
	
	upload.render({
        elem: '#taskImgFile'
        ,data:{
            linkType:3,
            fileType:3
        }
        ,url: contextPath+'/taskCenter/uploadTaskImg',
        before: function(obj){
            layer.load(); //上传loading
        }
        ,done: function(response){
        	layer.closeAll();
        	
            //上传成功
            if(response.successed){
            	layer.msg("图片上传成功，请继续提交保存");
            	
            	$("#taskImgFile").html("<i class=\"layui-icon\">&#xe67c;</i>重新上传");
            	
            	//暂存获取到的图片路径和图片ID
            	imgInfo.imgId=response.data.imgId;
            	imgInfo.fileDir=response.data.fileDir;
            }else{
            	layer.msg(response.data.resultMsg);
            }
        }
        ,error: function(XMLHttpRequest, textStatus, errorThrown){
        	taskImgEdit.errorShow(XMLHttpRequest, textStatus, errorThrown);
        }
    });
	
	/**展示上传的图片*/
	taskImgEdit.showImgInfo=function(){
		if(imgInfo.imgId==null || imgInfo.imgId==""){
			layer.msg("您还未选择需要上传的图片");
			return;
		}
		
		var title="查看图片";
		
		var imgIp=$("#imgIp").val();
		
		var imgUrl=imgIp+imgInfo.fileDir
		
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
		taskImgEdit.saveTaskImgInfo();
		return false;
   	});
	
	/**保存数据信息*/
	taskImgEdit.saveTaskImgInfo=function(){
		if(imgInfo.imgId==null || imgInfo.imgId==""){
			layer.msg("您还未选择需要上传的图片");
			return;
		}
		
		var params={
			"classCode":$("#classCode").val(),
			"sortSeq":$("#sortSeq").val(),
			"imgId":imgInfo.imgId,
			"imgCode":imgInfo.fileDir,
			"taskId":$("#taskId").val(),
		}
		
		layer.load();
		
		$.ajax({
			type: "post",
			contentType: "application/json",
			data: JSON.stringify(params),
			url :contextPath+"/taskCenter/saveTaskImgInfo",
			success : function(response){
				layer.closeAll();
				
				if(response.successed){
					layer.msg("保存数据信息成功");
					
					setTimeout("window.parent.taskImg.tableReload()",1200);
				}else{
					layer.msg(response.data.resultMsg);
				}
			},
			error: function(XMLHttpRequest, textStatus, errorThrown){
	            taskImgEdit.errorShow(XMLHttpRequest, textStatus, errorThrown);
	        }
		});
	}
	
	taskImgEdit.errorShow=function(XMLHttpRequest, textStatus, errorThrown){
		layer.closeAll();
		var statusCode = XMLHttpRequest.status;
        if (statusCode == "601") {
            layer.msg("您的登录已失效,请重新登陆");
        } else if (statusCode == "701") {
            layer.msg("您没有该功能的操作权限!");
        } else if (textStatus == "timeout") {
            layer.msg("请求超时，请检查网络情况");
        } else {
            layer.msg("超时或系统异常,请稍后再试!");
        }
	}
	
});

