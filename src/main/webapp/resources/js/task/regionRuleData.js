var regionRuleData={};

//列表控制，每页展示数目等
var regionPageConf={
	pageSize:15
}

var chooseDatas=[];

layui.use(['form','layer','table'],function(){
	var table = layui.table;
	
	$(document).ready(function(){
		regionRuleData.initLayTable();
	});
	
	var regionRuleDataTable;
	
	regionRuleData.initLayTable=function() {
		regionPageConf.ruleId=$("#ruleId").val();
		
		regionRuleDataTable=table.render({
			id:"regionList",
			elem: '#regionList',
			method: "post",
			contentType: "application/json",
			url :contextPath+"/taskCenter/getRegionRuleData",
			where:regionPageConf,
			loading:false,
			page:{
				limit:regionPageConf.pageSize,//每页展示数目
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
		             	{field: 'expId', title: 'expId',fixed: 'left',type:'checkbox'},
		              	{field: 'regionName', title: '地市名称'},
		              	{field: 'ruleData', title: '编码'},
		              	{field: 'parentRegionName', title: '省份'},
		            ]
		        ]
		   }
		);
	}

	table.on('checkbox(regionList)', function(obj){
		var isChecked=obj.checked;//true-选中，false-取消
		var checkStatus = table.checkStatus('regionList');
        var data = checkStatus.data;//获取选中的数据
        
        chooseDatas=data;
	});
	
	
	/**确定选择*/
	regionRuleData.delChooseInfo=function(editType){
		if(chooseDatas==null || chooseDatas.length==0){
			layer.msg("请选择需要删除的地市");
			return;
		}
		
		var params={
			ruleId:$("#ruleId").val(),
			statusCd:"102",
			ruleRegions:chooseDatas
		}
		
		$.ajax({
			type: "post",
			contentType: "application/json",
			data: JSON.stringify(params),
			url :contextPath+"/taskCenter/delRegionRuleData",
			success : function(response){
				if(response.successed){
					layer.msg("操作成功");
					
					setTimeout("parent.layer.closeAll()",1200);
				}else{
					layer.alert(response.data.resultMsg);
				}
			}
		});
	}
	
	/**重载表格*/
	regionRuleData.tableReload=function(){
		layer.closeAll();
		regionRuleDataTable.reload();
	}
});

