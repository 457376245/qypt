var productList = {};
var resturnObj = {};

function callbackdata() {
    return resturnObj;

}
layui.use(['form', 'layer', 'laydate', 'element', 'table'], function () {
    var pageConf = {
        pageSize: 10,
        currentPage: 1
    };
    var laytable = layui.table;
    var laydate = layui.laydate;
    var table = layui.table;
    var productTable;
    //绑定查询按钮点击事件
    $("#searchInfoBtn").click(function () {
        var productTitle_ = $("#productTitle").val();
        var productCode_ = $("#productCode").val();
        var productId_ = $("#oldProductId").val();
        if ("" !== productTitle_) {
            pageConf.productTitle = productTitle_;
        } else {
            pageConf.productTitle = null;
        }
        if ("" !== productCode_) {
            pageConf.productCode = productCode_;
        } else {
            pageConf.productCode = null;
        }

        if ("" !== productId_) {
            pageConf.productId = productId_;
        } else {
            pageConf.productId = null;
        }
        //重新加载表格
        productTable.reload({
            where: pageConf,
            page: {
                curr: 1
            }
        })
    });
    $(document).ready(function () {
        productList.init();
    });
    //初始化
    productList.init = function () {
        productList.initTable();

        // //时间范围
        // var ins1=laydate.render({
        //     elem: '#times'
        //     ,range: '~'
        //     ,trigger: 'click'
        // });

    };


    //列表查询
    productList.initTable = function () {

        productTable = laytable.render({
            id: "productListTable",
            elem: '#productListTable',
            contentType: "application/json",
            url: contextPath + "/excObj/productList",
            method: 'post',
            cellMinWidth: 80,
            page: true,
            where: {
                productId:$("#oldProductId").val()
            },
            limit: pageConf.pageSize,
            cols: [[
                {type:'radio'},
                {
                    field: 'productCode', title: '产品编码'
                },
                {
                    field: 'productTitle', title: '产品名称', width: 180
                }
            ]]
        });

    };
    //监听行单击事件（单击事件为：rowDouble）
    table.on('row(productListTable)',function(obj){
        var data = obj.data;
        resturnObj.productId = data.productId;
        resturnObj.productCode = data.productCode;
        resturnObj.productTitle = data.productTitle;
        //标注选中样式
        obj.tr.addClass('layui-table-click').siblings().removeClass('layui-table-click');
        obj.tr.find('i[class="layui-anim layui-icon"]').trigger("click");
    });

    /**重载表格*/
    productList.tableReload = function () {
        layer.closeAll();
        productTable.reload();
    }








});