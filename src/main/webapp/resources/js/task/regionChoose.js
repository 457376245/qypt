var regionChoose={};

//列表控制，每页展示数目等
var regionPageConf={
	pageSize:15
}

var chooseDatas=[];

layui.use(['form','layer','table'],function(){
	var table = layui.table;
	
	$(document).ready(function(){
		regionChoose.initLayTable();
	});
	
	var regionTable;
	
	regionChoose.initLayTable=function() {
		regionPageConf.ruleId=$("#ruleId").val();
		
		regionTable=table.render({
			id:"regionList",
			elem: '#regionList',
			method: "post",
			contentType: "application/json",
			url :contextPath+"/taskCenter/getCommonRegion",
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
		             	{field: 'regionId', title: 'regionId',fixed: 'left',type:'checkbox'},
		              	{field: 'regionName', title: '地市名称'},
		              	{field: 'regionCode', title: '编码'},
		              	{field: 'parentRegionName', title: '省份'},
		            ]
		        ]
		   }
		);
	}

	table.on('checkbox(regionList)', function(obj){
//		console.log(obj);
//		console.log(obj.checked); //当前是否选中状态
//		console.log(obj.data); //选中行的相关数据
//		console.log(obj.type); //如果触发的是全选，则为：all，如果触发的是单选，则为：one
		
		var isChecked=obj.checked;//true-选中，false-取消
		var checkStatus = table.checkStatus('regionList');
        var data = checkStatus.data;//获取选中的数据
        
        chooseDatas=data;
	});
	
	
	/**确定选择*/
	regionChoose.sureChooseInfo=function(editType){
		if(chooseDatas==null || chooseDatas.length==0){
			layer.msg("请选择可用地市");
			return;
		}
		
		var params={
			ruleId:$("#ruleId").val(),
			chooseDatas:chooseDatas
		}
		
		$.ajax({
			type: "post",
			contentType: "application/json",
			data: JSON.stringify(params),
			url :contextPath+"/taskCenter/addRuleRegionData",
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
	regionChoose.tableReload=function(){
		layer.closeAll();
		regionTable.reload();
	}
});

