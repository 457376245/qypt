var excObjList={};
layui.use(['form','layer','laydate','element','table'],function() {
    var pageConf={
        pageSize:10,
        currentPage:1
    };
    var laytable = layui.table;
    var laydate = layui.laydate;
    var excObjTable;
    //绑定查询按钮点击事件
    $("#searchInfoBtn").click(function(){
        var objCode_=$("#objCode").val();
        var statusCd_=$("#statusCd").val();
        if("" !== objCode_.trim()){
            pageConf.objCode = objCode_;
        }else{
            pageConf.objCode = null;
        }

        if("" !== statusCd_) {
            pageConf.statusCd = statusCd_;
        }else{
            pageConf.statusCd = null;
        }
        //重新加载表格
        excObjTable.reload({
            where:pageConf,
            page:{
                curr:1
            }
        })
    });
    $(document).ready(function(){
        excObjList.init();
    });
    //初始化
    excObjList.init=function(){
        excObjList.initTable();

        // //时间范围
        // var ins1=laydate.render({
        //     elem: '#times'
        //     ,range: '~'
        //     ,trigger: 'click'
        // });

    };




    //列表查询
    excObjList.initTable=function(){
        excObjTable=laytable.render({
            id:"excObjListTable",
            elem: '#excObjListTable',
            contentType: "application/json",
            url: contextPath + "/excType/excObjList",
            method: 'post',
            cellMinWidth: 80,
            page: true,
            limit: pageConf.pageSize,
            where:{
                typeId:$("#typeId").val()
            },
            cols: [[
                {
                    field: 'objCode', title: '商品编码',templet:function (res) {
                        return res.coExcObj.objCode;
                    }
                },
                {
                    field: 'payType', title: '支付类型',templet:function (res) {
                        return res.coExcObj.payType;
                    }
                },
                {
                    field: 'payVal', title: '支付值',templet:function (res) {
                        return res.coExcObj.payVal;
                    }
                },
                {
                    field: 'payMoney', title: '金额',templet:function (res) {
                        return res.coExcObj.payMoney;
                    }
                },
                {
                    field: 'effDate', title: '生效时间',templet:function (res) {
                        return res.coExcObj.effDate;
                    }
                },
                {
                    field: 'expDate', title: '失效时间',templet:function (res) {
                        return res.coExcObj.expDate;
                    }
                },
                {field: 'sortSeq', title: '排序',templet:function (res) {
                        return res.coExcObj.sortSeq;
                    }},
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
                        if (statusCd=="102") {
                            btnsHtml+="<a class=\"layui-btn  layui-btn-xs\" href=\"javascript:void(0);\" onclick=\"excObjList.editExcObjStatus('"+info.statusCd+"','"+info.objId+"','"+info.objCode+"');\">启用</a> ";
                        }else if(statusCd=="101"){
                            btnsHtml+="<a class=\"layui-btn  layui-btn-xs\" href=\"javascript:void(0);\" onclick=\"excObjList.editExcObjStatus('"+info.statusCd+"','"+info.objId+"','"+info.objCode+"');\">停用</a> ";
                        }
                        // btnsHtml+="<a class=\"layui-btn  layui-btn-xs\" href=\"javascript:void(0);\" onclick=\"excObjList.editExcObjStatus('103',"+info.objId+");\">删除 </a> ";

                        return btnsHtml;
                    }},

            ]]
            , id: 'turntableReload'
        });

    };
    /**新增或修改任务信息*/
    excObjList.toEditExcObj=function(editType,objId){
        var title="";
        var url=contextPath+"/excType/addExcTypeRela?editType="+editType;

        if(editType=="ADD"){
            title="添加商品";
        }else{
            title="修改商品";
            url+="&objId="+objId
        }

        var editExcObjDialog = layer.open({
            type:2,
            title:title,
            content:url,
            area: ['700px', '650px']
        });

        layui.layer.full(editExcObjDialog);
    };
    /**重载表格*/
    excObjList.tableReload=function(){
        layer.closeAll();
        excObjTable.reload();
    }


    /**停用，启用，删除*/
    excObjList.editExcObjStatus=function(oldStatusCd,objId,oldObjCode){
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
                objId:objId,
                editType:'EDIT',
                oldStatusCd:oldStatusCd,
                statusCd:statusCd,
                oldClassCode:oldobjCode
            }

            $.ajax({
                obj: "post",
                contentType: "application/json",
                data: JSON.stringify(params),
                url :contextPath+"/excObj/saveExcObj",
                success : function(response){
                    if(response.successed){
                        layer.msg("操作成功");
                        setTimeout("excObjList.tableReload()",1200);
                    }else{
                        layer.msg(response.data.resultMsg);
                    }
                }
            });
        }, function(){
            layer.closeAll();
        });
    }

    excObjList.toAddExcTypeRela=function(editType){
        var typeId = $("#typeId").val();
        layer.open({
            type: 2
            , area: ['600px', '600px']
            , title: '请选择需要添加的商品'
            , content: contextPath + "/excType/toAddExcTypeRela?typeId=" + typeId
            , maxmin: true
            , btn: ['确认选择', '关闭']
            , yes: function (index, layero) {
                var iframeWindow = window['layui-layer-iframe' + index];
                var row = window["layui-layer-iframe" + index].callbackdata();
                alert("row.objId = " + row.objId)
                var objId = row.objId;
                excObjList.addExcTypeRela(objId,typeId);
                layer.close(index);
            }, btn2: function (index, layero) {
                layer.close(index);
            }
        })
    }

    excObjList.addExcTypeRela=function (objId,typeId) {
        layer.alert(objId);
        layer.alert(typeId);
        var params={
            objId:objId,
            typeId:typeId
        }
        $.ajax({
            type: "post",
            contentType: "application/json",
            data: JSON.stringify(params),
            url :contextPath+"/excType/addExcTypeRela",
            success : function(response){
                if(response.successed){
                    layer.msg("保存数据信息成功");
                    setTimeout("window.parent.excTypeList.tableReload()",1400);
                }else{
                    layer.msg(response.data.resultMsg);
                }
            }
        });
    }
});