var prodGroupList={};
layui.use(['form','layer','laydate','element','table'],function() {
    var pageConf={
        pageSize:10,
        currentPage:1
    };
    var laytable = layui.table;
    var laydate = layui.laydate;
    var prodGroupTable;
    //绑定查询按钮点击事件
    $("#searchInfoBtn").click(function(){
        var brandTitle_=$("#brandTitle").val();
        var statusCd_=$("#statusCd").val();
        console.log(brandTitle_);
        console.log(statusCd_);
        if("" !== brandTitle_){
            pageConf.brandTitle = brandTitle_;
        }else{
            pageConf.brandTitle=null;
        }
        if("" !== statusCd_) {
            pageConf.statusCd = statusCd_;
        }else{
            pageConf.statusCd = null;
        }
        //重新加载表格
        prodGroupTable.reload({
            where:pageConf,
            page:{
                curr:1
            }
        })
    });
    $(document).ready(function(){
        prodGroupList.init();
    });
    //初始化
    prodGroupList.init=function(){
        prodGroupList.initTable();

        // //时间范围
        // var ins1=laydate.render({
        //     elem: '#times'
        //     ,range: '~'
        //     ,trigger: 'click'
        // });

    };


    prodGroupList.getCondition=function(params){
        var brandTitle=$.trim($("#brandTitle").val());
        var statusCd=$("#statusCd").val();
        if(actTitle!=""){
            params.brandTitle=brandTitle;
        }else{
            params.brandTitle=null;
        }
        if(statusCd!=""){
            params.statusCd=statusCd;
        }else{
            params.statusCd=null;
        }
        var times=$("#times").val();
        if(times!=''){
            params.qryStartTime=$.trim(times.split("~")[0]);
            params.qryEndTime=$.trim(times.split("~")[1]);
        }else{
            params.qryStartTime=null;
            params.qryEndTime=null;
        }
    };

    //列表查询
    prodGroupList.initTable=function(){
        prodGroupTable=laytable.render({
            id:"prodGroupListTable",
            elem: '#prodGroupListTable',
            contentType: "application/json",
            url: contextPath + "/prodGroup/groupList",
            method: 'post',
            cellMinWidth: 80,
            page: true,
            limit: pageConf.pageSize,
            cols: [[
                {
                    field: 'brandTitle', title: '组名称',templet: function (res) {
                        return "<a href='javascript:prodGroupList.queryPro("+res.brandTitle+");'>"+res.brandTitle+"</a>"
                    }
                },
                {field: 'sortSeq', title: '排序'},
                {
                    field: 'statusCd', title: '状态'
                    , width: 120, templet: function (res) {
                        if (res.statusCd == '101') {
                            return "<span class=\"yellow\">可用</span>";
                        } else if (res.statusCd == '102') {
                            return "<span class=\"black\">不可用</span>";
                        }
                    }
                },
                {field: 'opertionBtn', title: '操作',width:'12%',templet: function(info){
                        var btnsHtml="";
                        var statusCd=info.statusCd;
                        btnsHtml+="<a class=\"layui-btn layui-btn-mini layui-btn-xs\" href=\"javascript:void(0);\" onclick=\"prodGroupList.toEditProdGroup('EDIT',"+info.groupId+");\">详情</a>";
                        if (statusCd=="102") {
                            btnsHtml+="<a class=\"layui-btn  layui-btn-xs\" href=\"javascript:void(0);\" onclick=\"prodGroupList.editGroupStatus('101',"+info.groupId+");\">启用</a> ";
                        }else if(statusCd=="101"){
                            btnsHtml+="<a class=\"layui-btn  layui-btn-xs\" href=\"javascript:void(0);\" onclick=\"prodGroupList.editGroupStatus('102',"+info.groupId+");\">停用</a> ";
                        }
                        // btnsHtml+="<a class=\"layui-btn  layui-btn-xs\" href=\"javascript:void(0);\" onclick=\"prodGroupList.editGroupStatus('103',"+info.groupId+");\">删除 </a> ";

                        return btnsHtml;
                    }},

            ]]
            , id: 'turntableReload'
        });

    };
    /**新增或修改任务信息*/
    prodGroupList.toEditProdGroup=function(editType,groupId){
        var title="";
        var url=contextPath+"/prodGroup/toEditProdGroup?editType="+editType;

        if(editType=="ADD"){
            title="添加任务";
        }else{
            title="修改任务";
            url+="&groupId="+groupId
        }

        var editGroupDialog = layer.open({
            type:2,
            title:title,
            content:url,
            area: ['660px', '450px']
        });

        //layui.layer.full(editGroupDialog);
    };
    /**重载表格*/
    prodGroupList.tableReload=function(){
        layer.closeAll();
        prodGroupTable.reload();
    }


    /**停用，启用，删除*/
    prodGroupList.editGroupStatus=function(statusCd,groupId){
        var msg="";

        if(statusCd=="102"){
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
                url :contextPath+"/prodGroup/saveProdGroup",
                success : function(response){
                    if(response.successed){
                        layer.msg("操作成功");
                        setTimeout("prodGroupList.tableReload()",1200);
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