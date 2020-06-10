var productList = {};
layui.use(['form', 'layer', 'laydate', 'element', 'table'], function () {
    var pageConf = {
        pageSize: 10,
        currentPage: 1
    };
    var laytable = layui.table;
    var laydate = layui.laydate;

    var productTable;
    //绑定查询按钮点击事件
    $("#searchInfoBtn").click(function () {
        var brandTitle_ = $("#brandTitle").val();
        var statusCd_ = $("#statusCd").val();
        if ("" !== brandTitle_) {
            pageConf.brandTitle = brandTitle_;
        } else {
            pageConf.brandTitle = null;
        }
        if ("" !== statusCd_) {
            pageConf.statusCd = statusCd_;
        } else {
            pageConf.statusCd = null;
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


    productList.getCondition = function (params) {
        var brandTitle = $.trim($("#brandTitle").val());
        var statusCd = $("#statusCd").val();
        if (actTitle != "") {
            params.brandTitle = brandTitle;
        } else {
            params.brandTitle = null;
        }
        if (statusCd != "") {
            params.statusCd = statusCd;
        } else {
            params.statusCd = null;
        }
        var times = $("#times").val();
        if (times != '') {
            params.qryStartTime = $.trim(times.split("~")[0]);
            params.qryEndTime = $.trim(times.split("~")[1]);
        } else {
            params.qryStartTime = null;
            params.qryEndTime = null;
        }
    };

    //列表查询
    productList.initTable = function () {
        productTable = laytable.render({
            id: "productListTable",
            elem: '#productListTable',
            contentType: "application/json",
            url: contextPath + "/product/productList",
            method: 'post',
            cellMinWidth: 80,
            page: true,
            limit: pageConf.pageSize,
            cols: [[
                {
                    field: 'productCode', title: '产品编码'
                },
                {
                    field: 'productTitle', title: '产品名称', width: 180
                },
                {
                    field: 'prodSupplier', title: '提供平台', width: 120, templet: function (res) {
                        if (res.prodSupplier == '1') {
                            return "电信销售品权益";
                        } else if (res.prodSupplier == '2') {
                            return "合作商卡卷权益";
                        }
                    }
                },

                {
                    field: 'time', title: '生失效时间'
                    , width: 300, templet: function (res) {
                        return res.startTime + "-" + res.endTime
                    }
                },
                {
                    field: 'statusCd', title: '状态'
                    , width: 60, templet: function (res) {
                        if (res.statusCd == '101') {
                            return "<span class=\"yellow\">可用</span>";
                        } else if (res.statusCd == '102') {
                            return "<span class=\"black\">不可用</span>";
                        }
                    }
                },
                {
                    field: 'stockInst', title: '实例化'
                    , width: 60, templet: function (res) {
                        if (res.stockInst == '1') {
                            return "是";
                        } else if (res.stockInst == '2') {
                            return "否";
                        }
                    }
                },
                {
                    field: 'productStock', title: '库存'
                    , width: 100, templet: function (res) {
                        return res.coProductStock.productStock;
                    }
                },
                {
                    field: 'productUse', title: '已使用库存'
                    , width: 100, templet: function (res) {
                        return res.coProductStock.productUse;
                    }
                },
                {
                    field: 'productPreemption', title: '锁定库存'
                    , width: 100, templet: function (res) {
                        return res.coProductStock.productPreemption;
                    }
                },
                {
                    field: 'productWarn', title: '库存预警'
                    , width: 100, templet: function (res) {
                        return res.coProductStock.productWarn;
                    }
                },

                {
                    field: 'opertionBtn', title: '操作', width: '12%', templet: function (info) {
                        var btnsHtml = "";
                        var statusCd = info.statusCd;
                        btnsHtml += "<a class=\"layui-btn layui-btn-mini layui-btn-xs\" href=\"javascript:void(0);\" onclick=\"productList.toEditProduct('EDIT'," + info.productId + ");\">详情</a>";
                        if (statusCd == "102") {
                            btnsHtml += "<a class=\"layui-btn  layui-btn-xs\" href=\"javascript:void(0);\" onclick=\"productList.editProductStatus('101'," + info.productId + ");\">启用</a> ";
                        } else if (statusCd == "101") {
                            btnsHtml += "<a class=\"layui-btn  layui-btn-xs\" href=\"javascript:void(0);\" onclick=\"productList.editProductStatus('102'," + info.productId + ");\">停用</a> ";
                        }
                        // btnsHtml+="<a class=\"layui-btn  layui-btn-xs\" href=\"javascript:void(0);\" onclick=\"productList.editProductStatus('103',"+info.productId+");\">删除 </a> ";
                        var stockInst = info.stockInst;
                        if (stockInst == "2") {
                            btnsHtml += "<a class=\"layui-btn  layui-btn-xs\" href=\"javascript:void(0);\" onclick=\"productList.addProductStock(" + info.productId + ");\">添加库存</a> ";
                        }
                        return btnsHtml;
                    }
                }

            ]]
        });

    };
    /**新增或修改产品信息*/
    productList.toEditProduct = function (editType, productId) {
        var title = "";
        var url = contextPath + "/product/toEditProduct?editType=" + editType;

        if (editType == "ADD") {
            title = "添加产品";
        } else {
            title = "修改产品";
            url += "&productId=" + productId
        }

        var editProductDialog = layer.open({
            type: 2,
            title: title,
            content: url,
            maxmin: true,
            area: ['700px', '900px']
        });

        layui.layer.full(editProductDialog);
    };
    /**重载表格*/
    productList.tableReload = function () {
        layer.closeAll();
        productTable.reload();
    }


    /**停用，启用，删除*/
    productList.editProductStatus = function (statusCd, productId) {
        var msg = "";

        if (statusCd == "102") {
            msg = "您确定要停用该产品吗？";
        } else if (statusCd == "101") {
            msg = "您确定要启用该产品吗？";
        } else {
            msg = "您确定要删除该产品吗？";
        }

        layer.confirm(msg, {
            btn: ['确定', '取消'] //按钮
        }, function () {
            layer.closeAll();

            var params = {
                productId: productId,
                editType: 'EDIT',
                statusCd: statusCd
            }
            $.ajax({
                type: "post",
                contentType: "application/json",
                data: JSON.stringify(params),
                url: contextPath + "/product/saveProduct",
                success: function (response) {
                    if (response.successed) {
                        layer.msg("操作成功");
                        setTimeout("productList.tableReload()", 1200);
                    } else {
                        layer.msg(response.data.resultMsg);
                    }
                }
            });
        }, function () {
            layer.closeAll();
        });
    }

    productList.addProductStock = function (productId) {
        layer.open({
            type: 1,
            area: ['450px', 'auto'],
            title: ['添加库存', false],
            skin: "layui-layer-rim",
            fixed: false, //不固定
            maxmin: true,
            content: '<div style="width: 420px;  margin-left:7px; margin-top:10px;">请输入添加库存数量:<input type="text" id="addProductStock"  /></div>',
            btn: ["确定", "取消"],
            yes: function () {
                var addProductStock = top.$('#addProductStock').val();
                console.log("按enter没有调用" + addProductStock);
                productList.addProductStockAjax(addProductStock, productId);
            },
            btn2: function (index, lay) {
                layer.close(index);
            },
            success: function (layero, index) {
                this.enterConfirm = function (event) {
                    if (event.keyCode === 13) {
                        console.log("btn:" + $(".layui-layer-btn0").text());
                        $(".layui-layer-btn0").click();
                        //$(".layui-layer-btn0").trigger("click");
                        //return false; //阻止系统默认回车事件
                    }
                };
                $(document).on('keydown', this.enterConfirm); //监听键盘事件
            },
        });
    }


    /**停用，启用，删除*/
    productList.addProductStockAjax = function (productStock, productId) {

        var params = {
            productStock: productStock,
            productId: productId
        }
        $.ajax({
            type: "post",
            contentType: "application/json",
            data: JSON.stringify(params),
            url: contextPath + "/product/addProductStock",
            success: function (response) {
                if (response.successed) {
                    layer.msg("操作成功");
                    setTimeout("productList.tableReload()", 1200);
                } else {
                    layer.msg(response.data.resultMsg);
                }
            }
        });
    }
});