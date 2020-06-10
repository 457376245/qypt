var excTypeList={};
layui.use(['form','layer','laydate','element','table'],function() {
    var pageConf={
        pageSize:10,
        currentPage:1
    };
    var laytable = layui.table;
    var laydate = layui.laydate;
    var excTypeTable;
    //绑定查询按钮点击事件
    $("#searchInfoBtn").click(function(){
        var typeCode_=$("#typeCode").val();
        var typeDesc_=$("#typeDesc").val();
        var statusCd_=$("#statusCd").val();
        if("" !== typeCode_.trim()){
            pageConf.typeCode = typeCode_;
        }else{
            pageConf.typeCode = null;
        }
        if("" !== typeDesc_.trim()){
            pageConf.typeDesc = typeDesc_;
        }else{
            pageConf.typeDesc = null;
        }
        if("" !== statusCd_) {
            pageConf.statusCd = statusCd_;
        }else{
            pageConf.statusCd = null;
        }
        //重新加载表格
        excTypeTable.reload({
            where:pageConf,
            page:{
                curr:1
            }
        })
    });
    $(document).ready(function(){
        excTypeList.init();
    });
    //初始化
    excTypeList.init=function(){
        excTypeList.initTable();
    };

    //列表查询
    excTypeList.initTable=function(){
        excTypeTable=laytable.render({
            id:"excTypeListTable",
            elem: '#excTypeListTable',
            contentType: "application/json",
            url: contextPath + "/excType/typeList",
            method: 'post',
            cellMinWidth: 80,
            page: true,
            limit: pageConf.pageSize,
            cols: [[
                {
                    field: 'typeCode', title: '分类编码'
                },
                {
                    field: 'typeDesc', title: '分类名称'
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
                        btnsHtml+="<a class=\"layui-btn layui-btn-mini layui-btn-xs\" href=\"javascript:void(0);\" onclick=\"excTypeList.toEditExcType('EDIT',"+info.typeId+");\">详情</a>";
                        if (statusCd=="102") {
                            btnsHtml+="<a class=\"layui-btn  layui-btn-xs\" href=\"javascript:void(0);\" onclick=\"excTypeList.editExcTypeStatus('"+info.statusCd+"','"+info.typeId+"','"+info.typeCode+"');\">启用</a> ";
                        }else if(statusCd=="101"){
                            btnsHtml+="<a class=\"layui-btn  layui-btn-xs\" href=\"javascript:void(0);\" onclick=\"excTypeList.editExcTypeStatus('"+info.statusCd+"','"+info.typeId+"','"+info.typeCode+"');\">停用</a> ";
                        }
                        // btnsHtml+="<a class=\"layui-btn  layui-btn-xs\" href=\"javascript:void(0);\" onclick=\"excTypeList.editExcTypeStatus('103',"+info.typeId+");\">删除 </a> ";
                        btnsHtml+="<a class=\"layui-btn  layui-btn-xs\" href=\"javascript:void(0);\" onclick=\"excTypeList.openExcObjList("+info.typeId+");\">关联商品 </a> ";
                        return btnsHtml;
                    }},

            ]]
            , id: 'turntableReload'
        });

    };
    /**新增或修改任务信息*/
    excTypeList.toEditExcType=function(editType,typeId){
        var title="";
        var url=contextPath+"/excType/toEditExcType?editType="+editType;

        if(editType=="ADD"){
            title="添加分类";
        }else{
            title="修改分类";
            url+="&typeId="+typeId
        }

        var editExcTypeDialog = layer.open({
            type:2,
            title:title,
            content:url,
            area: ['660px', '450px']
        });

        //layui.layer.full(editGroupDialog);
    };
    /**重载表格*/
    excTypeList.tableReload=function(){
        layer.closeAll();
        excTypeTable.reload();
    };


    /**停用，启用，删除*/
    excTypeList.editExcTypeStatus=function(oldStatusCd,typeId,oldTypeCode){
        var msg="";
        var statusCd="";
        if(oldStatusCd=="101"){
            statusCd="102"
            msg="您确定要停用该分类吗？";
        }else if(oldStatusCd=="102"){
            statusCd="101"
            msg="您确定要启用该分类吗？";
        }else{
            msg="您确定要删除该分类吗？";
        }

        layer.confirm(msg, {
            btn: ['确定','取消'] //按钮
        }, function(){
            layer.closeAll();

            var params={
                typeId:typeId,
                editType:'EDIT',
                oldStatusCd:oldStatusCd,
                statusCd:statusCd,
                oldClassCode:oldTypeCode
            }

            $.ajax({
                type: "post",
                contentType: "application/json",
                data: JSON.stringify(params),
                url :contextPath+"/excType/saveExcType",
                success : function(response){
                    if(response.successed){
                        layer.msg("操作成功");
                        setTimeout("excTypeList.tableReload()",1200);
                    }else{
                        layer.msg(response.data.resultMsg);
                    }
                }
            });
        }, function(){
            layer.closeAll();
        });
    };

    excTypeList.openExcObjList=function(typeId){
        layer.open({
            type: 2
            , area: ['800px', '800px']
            , title: '查看商品'
            , content: contextPath + "/excType/toExcObjList?typeId=" + typeId
            , maxmin: true
            , btn: ['关闭']
            , yes: function (index, layero) {
                layer.close(index);
            }
        })
    }

});