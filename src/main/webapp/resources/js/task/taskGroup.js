var taskGroupIndex={};

//列表控制，每页展示数目等
var pageConf={
	pageSize:10
}

layui.use(['form','layer','table','laydate'],function(){
	var laydate = layui.laydate;
	var table = layui.table;
	
	$(document).ready(function(){
		taskGroupIndex.initLayTable();
	});
	
	var taskGroupTable;
	
	//绑定查询按钮点击事件
	$("#searchInfoBtn").click(function(){
		var groupTitle_=$("#groupTitle").val();
		var statusCd_=$("#statusCd").val();
		
		pageConf.groupTitle=groupTitle_;
		pageConf.statusCd=statusCd_;
		
		//重新加载表格
		table.reload('taskGroupListTable',{
			where:pageConf,
			page:{
				curr:1
			}
		})
	});
	
	taskGroupIndex.initLayTable=function() {
		taskGroupTable=table.render({
			id:"taskGroupListTable",
			elem: '#taskGroupListTable',
			method: "post",
			contentType: "application/json",
			url :contextPath+"/taskCenter/getTaskGroupList",
			where:pageConf,
			loading:false,
			page:{
				limit:pageConf.pageSize,//每页展示数目
				groups: 5, //连续显示分页数
				prev:'上一页',
				next:'下一页',
				layout:['prev', 'page', 'next','count']
			},
			parseData: function(res){
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
		             	{field: 'groupId', title: 'ID', sort: true, fixed: 'left',hide:true},
		              	{field: 'groupTitle', title: '分组标题'},
		              	{field: 'groupDesc', title: '简介'},
		              	{field: 'groupCode', title: '编码'},
		              	{field: 'sortSeq', title: '排序号'},
		              	{field: 'statusCd', title: '状态',templet: function(info){
		              		var statusCd=info.statusCd;
		              		var statusCdName="";
		              		var statusCdColor="";
		              		if(statusCd=="100"){
		        				statusCdName="待启用";
		        				statusCdColor="gray"
		        			}else if(statusCd=="101"){
		        				statusCdName="已启用";
		        				statusCdColor="yellow"
		        			}else{
		        				statusCdName="其他";
		        			}
		              		
		                    return '<span class="'+statusCdColor+'">'+statusCdName+'</span>';
		                }},
		                {field: 'satrtAndEndTimeShow', title: '起止时间',width:'20%',templet: function(info){
		                	return info.startDate+' ~ '+info.endDate;
		                }},
		                {field: 'opertionBtn', title: '操作',width:'12%',templet: function(info){
		                	var btnsHtml="";
		                	var statusCd=info.statusCd;
		                	
		                	btnsHtml+="<a class=\"layui-btn layui-btn-mini layui-btn-xs\" href=\"javascript:void(0);\" onclick=\"taskGroupIndex.toEditTaskGroup('EDIT',"+info.groupId+");\">详情</a>";
							
							if (statusCd=="100") {
								btnsHtml+="<a class=\"layui-btn  layui-btn-xs\" href=\"javascript:void(0);\" onclick=\"taskGroupIndex.editGroupStatus('101',"+info.groupId+");\">启用</a> ";
							}else if(statusCd=="101"){
								btnsHtml+="<a class=\"layui-btn  layui-btn-xs\" href=\"javascript:void(0);\" onclick=\"taskGroupIndex.editGroupStatus('100',"+info.groupId+");\">停用</a> ";
							}
							
							btnsHtml+="<a class=\"layui-btn  layui-btn-xs\" href=\"javascript:void(0);\" onclick=\"taskGroupIndex.editGroupStatus('102',"+info.groupId+");\">删除 </a> ";
							
							return btnsHtml;
		                }},
		            ]
		        ]
		   }
		);
	}

	/**新增或修改任务组信息*/
	taskGroupIndex.toEditTaskGroup=function(editType,groupId){
		var title="";
		var url=contextPath+"/taskCenter/editTaskGroupInfo?editType="+editType;
		
		if(editType=="ADD"){
			title="添加分组";
		}else{
			title="修改分组";
			url+="&groupId="+groupId
		}
		
		var editGroupDialog = layer.open({
			type:2,
			title:title,
			content:url,
			area: ['600px', '530px']
		});
		
		//layui.layer.full(editGroupDialog);
	}
	
	/**重载表格*/
	taskGroupIndex.tableReload=function(){
		layer.closeAll();
		taskGroupTable.reload();
	}
	
	/**停用，启用，删除*/
	taskGroupIndex.editGroupStatus=function(statusCd,groupId){
		var msg="";
		
		if(statusCd=="100"){
			msg="您确定要停用该分组吗？";
		}else if(statusCd=="101"){
			msg="您确定要启用该分组吗？";
		}else{
			msg="您确定要删除该分组吗？";
		}
		
		layer.confirm(msg, {
			btn: ['确定','取消'] //按钮
		}, function(){
			layer.closeAll();
			
			var params={
				groupId:groupId,
				editType:'EDIT',
				statusCd:statusCd
			}
			
			$.ajax({
				type: "post",
				contentType: "application/json",
				data: JSON.stringify(params),
				url :contextPath+"/taskCenter/saveTaskGroup",
				success : function(response){
					if(response.successed){
						layer.msg("操作成功");
						
						setTimeout("taskGroupIndex.tableReload()",1200);
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

