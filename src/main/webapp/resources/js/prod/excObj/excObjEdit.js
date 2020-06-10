var excObjEdit = {};

layui.use(['form', 'layer', 'laydate'], function () {
    var laydate = layui.laydate;
    var form = layui.form;
    laydate.render({
        elem: '#expDate',
        type: 'datetime',
        trigger: 'click',
        format: 'yyyy-MM-dd HH:mm:ss'
    });
    laydate.render({
        elem: '#effDate',
        type: 'datetime',
        trigger: 'click',
        format: 'yyyy-MM-dd HH:mm:ss'
    });

    form.on('select(payType)', function(data){
        if(data.value == 1 || data.value == 2){
            $("#payMoney").attr("disabled","true");
            $("#payMoney").val("0");
            form.render('select');
        }else{
            $("#payMoney").removeAttr("disabled");
            $("#payMoney").val("");
            form.render('select');//select是固定写法 不是选择器
        }
    });

    form.on('submit(saveInfoBtn)', function (data) {
        excObjEdit.saveExcObjInfo();
        return false;
    });

    excObjEdit.saveExcObjInfo = function () {
        var params = $('#editForm').serializeArray();
        var idata = JSON.stringify(params);
        alert($("#productId").val());
        var params = {
            objId: $("#objId").val(),
            editType: $("#editType").val(),
            productId: $("#productId").val(),
            objCode: $("#objCode").val(),
            payType: $("#payType").val(),
            payVal: $("#payVal").val(),
            payMoney: $("#payMoney").val(),
            effDate: $("#effDate").val(),
            expDate: $("#expDate").val(),
            sortSeq: $("#sortSeq").val(),
            statusCd: $("#statusCd").val()
        }

        $.ajax({
            type: "post",
            contentType: "application/json",
            data: JSON.stringify(params),
            url: contextPath + "/excObj/saveExcObj",
            success: function (response) {
                if (response.successed) {
                    layer.msg("保存数据信息成功");
                    setTimeout("window.parent.excObjList.tableReload()", 1400);
                } else {
                    layer.msg(response.data.resultMsg);
                }
            }
        });
    }


    excObjEdit.openProductList = function (productId) { //模拟操作

        layer.open({
            type: 2
            , area: ['600px', '600px']
            , title: '请选择'
            , content: contextPath + "/excObj/toProduct?productId=" + productId
            , maxmin: true
            , btn: ['确认选择', '关闭']
            , yes: function (index, layero) {
                var iframeWindow = window['layui-layer-iframe' + index];
                var row = window["layui-layer-iframe" + index].callbackdata();
                form.val("editForm", {
                        "productTitle": row.productTitle + "(" + row.productCode + ")",
                        "productId": row.productId
                    }
                );

                layer.close(index);
            }, btn2: function (index, layero) {
                layer.close(index);
            }
        });
    }
});

