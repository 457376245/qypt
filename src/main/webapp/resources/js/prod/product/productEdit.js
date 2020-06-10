var productEdit = {};

layui.use(['form', 'layer','jquery', 'laydate', 'layedit','upload'], function () {
    var laydate = layui.laydate;
    laydate.render({
        elem: '#startTime',
        type: 'datetime',
        trigger: 'click',
        format: 'yyyy-MM-dd HH:mm:ss'
    });
    laydate.render({
        elem: '#endTime',
        type: 'datetime',
        trigger: 'click',
        format: 'yyyy-MM-dd HH:mm:ss'
    });
    laydate.render({
        elem: '#showTime',
        type: 'datetime',
        trigger: 'click',
        format: 'yyyy-MM-dd HH:mm:ss'
    });
    var upload = layui.upload;
    var loadIndex;
    //普通图片上传
    var detailImgInst = upload.render({
        elem: '#detailImg'
        ,url:  contextPath + "/file/uploadFileProduct"   //改成您自己的上传接口
        ,before: function(obj){
            layer.load(); //上传loading 不加载
            //预读本地文件示例，不支持ie8
            obj.preview(function(index, file, result){
                $('#detailImge').attr('src', result); //图片链接（base64）
            });
        }
        ,done: function(res){
            layer.closeAll('loading');
            //如果上传失败
            if(res.code > 0){
                return layer.msg('上传失败');
            }
            var data = res.data
            $('#detailImgeId').attr("value",data.imgId);
            //上传成功
            layer.msg('上传成功');
        }
        ,error: function(){

        }
    });

    var thumbnailInst = upload.render({
        elem: '#thumbnail'
        ,url:  contextPath + "/file/uploadFileProduct"   //改成您自己的上传接口
        ,before: function(obj){
            layer.load(); //上传loading 不加载
            //预读本地文件示例，不支持ie8
            obj.preview(function(index, file, result){
                $('#thumbnailImge').attr('src', result); //图片链接（base64）
            });
        }
        ,done: function(res){
            layer.closeAll('loading');
            //如果上传失败
            if(res.code > 0){
                return layer.msg('上传失败');
            }
            var data = res.data
            $('#thumbnailId').attr("value",data.imgId);
            //上传成功
            layer.msg('上传成功');
        }
        ,error: function(){

        }
    });

    //编辑器
    var layedit = layui.layedit;


    var index=layedit.build('productDes', {
        uploadImage: {
            url: contextPath + "/file/uploadFileProduct"   //上传接口url
            , type: 'post' //默认post
        }
        ,tool: [ 'strong' //加粗
            ,'italic' //斜体
            ,'underline' //下划线
            ,'del' //删除线
            ,'|' //分割线
            ,'left' //左对齐
            ,'center' //居中对齐
            ,'right' //右对齐
            ,'link' //超链接
            ,'unlink' //清除链接
            ,'image'
        ]
        ,height: 500
        ,width:250
    });
    var form = layui.form;
    form.verify({
        content: function(value) {
            return layedit.sync(index);
        }
    });
    form.on('submit(saveInfoBtn)', function (data) {
        productEdit.saveProductInfo();
        return false;
    });

    form.on('select(stockInst)', function(data){
        if(data.value == 1){
            $("#stockCount").attr("disabled","true");
            $("#stockCount").val("0");
            form.render('select');
        }else{
            $("#stockCount").removeAttr("disabled");
            form.render('select');//select是固定写法 不是选择器
        }
    });

    form.on('select(prodSupplier)', function(data){
        if(data.value == 1){
            productEdit.unReadonly();
            form.render('select');
        }else{
            productEdit.readonly();
            form.render('select');//select是固定写法 不是选择器
        }
    });

    productEdit.unReadonly = function(){
        $("#productCode").removeAttr("disabled");
        form.val("editForm",{
            "productCode": "" // "name": "value"
        });
        $("#stockInst").find("option[value=2]").prop("selected",true);
        $("#stockInst").attr("disabled","true");
        $("#stockCount").removeAttr("disabled");
    }

    productEdit.readonly = function(){
        form.val("editForm",{
            "productCode": $("#genProdCode").val() // "name": "value"
        });
        $("#productCode").attr("disabled","true");
        $("#stockInst").removeAttr("disabled");
        $("#stockCount").attr("disabled","true");
        $("#stockCount").val("0");
    }

    productEdit.saveProductInfo = function () {
        var params = {
            productId: $("#productId").val(),
            editType: $("#editType").val(),
            prodSupplier:$("#prodSupplier").val(),
            productCode:$("#productCode").val(),
            productTitle:$("#productTitle").val(),
            showTime:$("#showTime").val(),
            startTime:$("#startTime").val(),
            endTime:$("#endTime").val(),
            detailImgeId:$("#detailImgeId").val(),
            thumbnailId:$("#thumbnailId").val(),
            stockInst: $("#stockInst").val(),
            stockCount: $("#stockCount").val(),
            statusCd: $("#statusCd").val(),
            productDes: $("#productDes").val(),
            productWarn: $("#productWarn").val()
        }

        $.ajax({
            type: "post",
            contentType: "application/json",
            data: JSON.stringify(params),
            url: contextPath + "/product/saveProduct",
            success: function (response) {
                if (response.successed) {
                    layer.msg("保存数据信息成功");
                    setTimeout("window.parent.productList.tableReload()", 1400);
                } else {
                    layer.msg(response.data.resultMsg);
                }
            }
        });
    }
});

