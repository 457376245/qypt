var excObjList={};
var resturnObj={};
function callbackdata() {
    return resturnObj;
}
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
    };




    //列表查询
    excObjList.initTable=function(){
        pageConf.typeId = $("#typeId").val();
        alert(pageConf.typeId);
        excObjTable=laytable.render({
            id:"excObjListTable",
            elem: '#excObjListTable',
            contentType: "application/json",
            url: contextPath + "/excType/excObjListAll",
            method: 'post',
            cellMinWidth: 80,
            page: true,
            limit: pageConf.pageSize,
            where: pageConf,
            cols: [[
                {type:'radio'},
                {
                    field: 'objCode', title: '商品编码',templet:function (res) {
                        return res.objCode;
                    }
                },
                {
                    field: 'effDate', title: '生效时间',templet:function (res) {
                        return res.effDate;
                    }
                },
                {
                    field: 'expDate', title: '失效时间',templet:function (res) {
                        return res.expDate;
                    }
                },
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

            ]]
        });

    };
    //监听行单击事件（单击事件为：rowDouble）
    laytable.on('row(excObjListTable)',function(obj){
        var data = obj.data;
        resturnObj.objId = data.objId;
        //标注选中样式
        obj.tr.addClass('layui-table-click').siblings().removeClass('layui-table-click');
        obj.tr.find('i[class="layui-anim layui-icon"]').trigger("click");
    });


});