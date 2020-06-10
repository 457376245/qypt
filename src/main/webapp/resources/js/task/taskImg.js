var taskImg={};

var imgClassCodes={
	"101":"列表banner图片","102":"首页顶部banner图片",
	"103":"首页宣传banner图片","104":"用户下单后图片",
	"105":"权益平台弹窗图片","106":"任务banner"
}

var imgIp="";

//列表控制，每页展示数目等
var taskImgPageConf={
	pageSize:3
}

layui.use(['form','layer','table'],function(){
	var laydate = layui.laydate;
	var table = layui.table;
	
	/**初始列表*/
	taskImg.initLayTable=function(taskId) {
		taskImgPageConf.taskId=taskId;
		
		taskImgTable=table.render({
			id:"taskImgListTable",
			elem: '#taskImgListTable',
			method: "post",
			contentType: "application/json",
			url :contextPath+"/taskCenter/getTaskImgList",
			where:taskImgPageConf,
			loading:false,
			page:{
				limit:pageConf.pageSize,//每页展示数目
				groups: 5, //连续显示分页数
				prev:'上一页',
				next:'下一页',
				layout:['prev', 'page', 'next','count']
			},
			parseData: function(res){
				imgIp=res.data.imgIp;
				
				console.log(res.data.imgIp);
				
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
		             	{field: 'taskImgId', title: 'ID', sort: true, fixed: 'left',hide:true},
		              	{field: 'oldName', title: '图片原名称'},
		              	{field: 'newName', title: '图片新名称'},
		              	{field: 'sortSeq', title: '图片排序'},
		              	{field: 'classCode', title: '类型',templet: function(info){
		              		var classCodeName=imgClassCodes[info.classCode];
		              		
		              		if(classCodeName==null || classCodeName==""){
		              			classCodeName="其他";
		              		}
		              		
							return classCodeName;
		                }},
		              	{field: 'addDate', title: '添加时间'},
		                {field: 'opertionBtn', title: '操作',width:'20%',templet: function(info){
		                	var btnsHtml="";
		                	
		                	btnsHtml+="<a class=\"layui-btn layui-btn-mini layui-btn-xs\" href=\"javascript:void(0);\" onclick=\"taskImg.toEditTaskImgInfo('EDIT',"+info.taskImgId+",'"+info.imgCode+"');\">查看</a>";
							
							btnsHtml+="<a class=\"layui-btn  layui-btn-xs\" href=\"javascript:void(0);\" onclick=\"taskImg.editTaskImgStatus('102',"+info.taskImgId+");\">删除 </a> ";
							
							return btnsHtml;
		                }}
		            ]
		        ]
		   }
		);
	}

	/**新增或修改任务信息*/
	taskImg.toEditTaskImgInfo=function(editType,taskImgId,fileDir){
		var title="";
		var taskId=taskImgPageConf.taskId;
		
		var url=contextPath+"/taskCenter/toEditTaskImgInfo?taskId="+taskId;
		
		if(editType=="ADD"){
			title="添加任务图片";
			
			var editGroupDialog = layer.open({
				type:2,
				title:title,
				content:url,
				area: ['660px', '320px']
			});
		}else{
			title="查看图片";
			
			var imgUrl=imgIp+fileDir
			
			var content="<img src=\""+imgUrl+"\" style=\"max-width:350px;max-height:310px;margin-top:10px;\" />";
			
			var editGroupDialog = layer.open({
				type:1,
				title:title,
				content:content,
				area: ['350px', '350px']
			});
		}
	}
	
	/**重载表格*/
	taskImg.tableReload=function(){
		layer.closeAll();
		
		//重载table
		taskImgTable.reload();
	}
	
	/**停用，启用，删除*/
	taskImg.editTaskImgStatus=function(statusCd,taskImgId){
		var msg="";
		
		var taskId=taskImgPageConf.taskId;
		
		if(statusCd=="100"){
			msg="您确定要停用该任务吗？";
		}else if(statusCd=="101"){
			msg="您确定要启用该任务吗？";
		}else{
			msg="您确定要删除该任务图片吗？";
		}
		
		layer.confirm(msg, {
			btn: ['确定','取消'] //按钮
		}, function(){
			layer.closeAll();
			
			var params={
				taskId:taskId,
				taskImgId:taskImgId,
				statusCd:statusCd
			}
			
			$.ajax({
				type: "post",
				contentType: "application/json",
				data: JSON.stringify(params),
				url :contextPath+"/taskCenter/editTaskImgStatus",
				success : function(response){
					if(response.successed){
						layer.msg("操作成功");
						
						setTimeout("taskImg.tableReload()",1200);
					}else{
						layer.msg(response.data.resultMsg);
					}
				}
			});
		}, function(){
			layer.closeAll();
		});
	}
});

