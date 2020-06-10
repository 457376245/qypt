layui.use(['form','layer','laypage','laydate','table'],function() {
    var pageConf={
        pageSize:10,
        currentPage:1
    };
    var laytable = layui.table;
    $(document).ready(function(){
    	init();
    });
    //初始化
    function init(){
    	initTable();
        //查询
        $("body").on("click", ".search_btn", function () {
            searchList();
        }).resize();
    }
    function initTable(){
        laytable.render({
            elem: '#turntable',
            contentType: "application/json",
            url:contextPath+"/rights/rightsInstQry",
            method: 'post',
            cellMinWidth: 80,
            page:true,
            limit:pageConf.pageSize,
            cols: [[{field: 'rightsCode',title: '权益编码'}
            	,{field: 'rightsName',title: '权益名称'}
            	,{field: 'showAmount',title: '显示额度'}
            	,{field: 'receiveDate',title: '领取时间'}  
            	,{field: 'effDate',title: '权益生效时间'}  
            	,{field: 'expDate',title: '权益失效时间'}  
                ,{field: 'rightsInstNbr',title: '实例编码'}
                ,{ field: 'instStateCd', title: '实例状态'
                    ,templet: function (res) {
                        if (res.instStateCd == '0') {
                            return "<span class=\"yellow\">已预占</span>";
                        } else if (res.instStateCd == '1') {
                            return "<span class=\"yellow\">待使用</span>";
                        } else if (res.instStateCd == '2') {
                            return "<span class=\"black\">使用</span>";
                        }  else if (res.instStateCd == '3') {
                            return "<span class=\"gray\">已失效</span>";
                        } else {
                            return "<span class=\"gray\">其他</span>";
                        }
                    }
                }
            ]]
            ,id: 'turntableReload'
        });
    }
    //列表查询
    function searchList(){
        var params={
        };
        var objNbr=$.trim($("#objNbr").val());
        var rightsInstNbr=$("#rightsInstNbr").val();
        if(objNbr=="" || objNbr == null){
        	layer.msg('领取对象为空');
        	return false;
        }
        params.objNbr=objNbr;
        if(rightsInstNbr!=""){
            params.rightsInstNbr=rightsInstNbr;
        }
        //执行重载
        laytable.reload('turntableReload', {
            page: {
                curr: 1 //重新从第 1 页开始
            }
            ,where: params
        });
    }   
});

