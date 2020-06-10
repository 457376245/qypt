layui.use(['form','layer','laypage','laydate','layedit','table','upload'],function(){
    var banners=[];//封装banner图片的上传路径
    var submitData={};
    var form = layui.form;
    var layedit = layui.layedit;
    var laydate = layui.laydate;
    //富文本框
    var index =layedit.build('activityRule', {
        tool: [ 'strong' //加粗
            ,'italic' //斜体
            ,'underline' //下划线
            ,'del' //删除线
            ,'|' //分割线
            ,'left' //左对齐
            ,'center' //居中对齐
            ,'right' //右对齐
            ,'link' //超链接
            ,'unlink' //清除链接
        ]
        ,height: 100
    });
    $(document).ready(function(){
        var activityId=$("#contentDiv").attr("data-id");
        var flag=$("#contentDiv").attr("data-type");
        if(flag!='detail'){
            bannerUpload('102');
            bannerUpload('103');
            bannerUpload('105');
        }
        if(activityId!=undefined&&activityId!=null&&activityId!=''){//编辑初始化
            initEditInfo(activityId,flag,"");
        }
        $("body").on("click", "#resetData", function () {
            resetOtherInfo();
        });
        $("body").on("click", "#backList", function () {
            location.href= contextPath + '/otherAct/index';
        });
    });
    var curDate = new Date();
    laydate.render({
        elem: '#startTime',
        type: 'datetime',
        trigger: 'click',
        format: 'yyyy-MM-dd HH:mm:ss',
        min: dateFormat(curDate)
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
        format: 'yyyy-MM-dd HH:mm:ss',
        min: dateFormat(curDate)
    });
    //图片复选框不选时删除图片
    form.on('checkbox(delPic)', function(data){
        var obj=data.elem.value;
        if(!data.elem.checked){
            for(var it in banners){
                if(banners[it].classCode==obj){
                    banners.splice(it,1);
                    $("#banner"+obj).html('<i class="layui-icon">&#xe67c;</i>上传图片');
                }
            }
        }
    });
    form.on('submit(saveAct)', function(data){
        if(!commonValid()){
            return false;
        }
        var flag=$("#contentDiv").attr("data-type");
        if(flag=='add') {
            return toSubmit();
        }else{
            return toEditSumit();
        }
    });
    //重置
    function resetOtherInfo() {
        $("#contentDiv")[0].reset();
        submitData={};
        banners=[];//封装banner图片的上传路径
        $('input:checkbox[name="banners"]').each(function (index, element) {
            $(this).removeAttr("checked");
            var value=$(this).val();
            $("#banner"+value).html("<i class=\"layui-icon\">&#xe67c;</i>上传图片");
        });
        layui.form.render();
    }
    function toEditSumit() {
        var activityId=$("#contentDiv").attr("data-id");
        submitData.activityId=activityId;
        var randTime="?t="+new Date().getTime();
        var index = layer.msg('提交中，请稍候',{icon: 16,time:false,shade:0.8});
        $.ajax({
            type: 'post' ,
            url: contextPath+"/otherAct/editPro"+randTime,
            cache:false ,
            async:true ,
            contentType: "application/json",
            data: JSON.stringify(submitData),
            dataType:'json',
            success:function(response) {
                debugger;
                layer.close(index);
                //展示数据
                if (response.successed) {
                    layer.msg("活动保存成功");
                    return true;
                }else{
                    if(response.data.resultMsg!=null&&response.data.resultMsg!=undefined){
                        layer.msg(response.data.resultMsg);
                    }else{
                        layer.msg("活动保存失败");
                    }
                }
            },
            error:function(response){
                layer.close(index);
                layer.msg("活动保存失败");
            }
        });
        return false;
    }
    //编辑界面初始化
    function initEditInfo(activityId) {
        var params={
            "activityId":activityId
        }
        var randTime="?t="+new Date().getTime();
        var index = layer.msg('数据查询中，请稍候',{icon: 16,time:false,shade:0.8});
        $.ajax({
            type: 'post' ,
            url: contextPath+"/otherAct/qryActEditInfo"+randTime,
            cache:false ,
            async:true ,
            contentType: "application/json",
            data: JSON.stringify(params),
            dataType:'json',
            success:function(response) {
                debugger;
                layer.close(index);
                //展示数据
                if (response.successed) {
                    var coActivity=response.data.coActivity;
                    var coActivityImgs=response.data.coActivityImgs;
                    //初始化活动字段\秒杀节点
                    intiActInfo(coActivity);
                    //初始化活动图片
                    if(coActivityImgs!=undefined&&coActivityImgs!=null&&coActivityImgs.length>0){
                        intiActImg(coActivityImgs);
                    }
                    layui.form.render();
                }else{
                    if(response.data.resultMsg!=null&&response.data.resultMsg!=undefined){
                        layer.msg(response.data.resultMsg);
                    }else{
                        layer.msg("活动查询失败");
                    }
                }
            },
            error:function(response){
                layer.close(index);
                layer.msg("活动查询失败");
            }
        });
    }
    //初始化活动
    function intiActInfo(coActivity) {
        $("#actTitle").val(coActivity.activityTitle);
        $("#showTime").val(datetimeFormat(coActivity.showTime));
        $("#startTime").val(datetimeFormat(coActivity.startTime));
        $("#endTime").val(datetimeFormat(coActivity.endTime));
        $("#activityDesc").val(coActivity.activityDesc);
        $("#ssoUrl").val(coActivity.ssoUrl);
        layedit.setContent(index, coActivity.activityRule,false);
    }
    //初始化活动banner图片
    function intiActImg(coActivityImgs) {
        for(it in coActivityImgs){
            var banner={
                actImgId:coActivityImgs[it].actImgId,
                classCode:coActivityImgs[it].classCode,
                imgId:coActivityImgs[it].imgId,
                imgCode:coActivityImgs[it].imgCode
            }
            banners.push(banner);
            actImgDeal(coActivityImgs[it].classCode);
        }
    }
    function actImgDeal(objId) {
        $("#banner"+objId).html("<i class=\"layui-icon\">&#xe67c;</i>重新上传");
        $('input:checkbox[name="banners"]').each(function (index, element) {
            if($(this).val()==objId){
                $(this).attr("checked", true);
            }
        });
    }
    //提交
    function toSubmit(flag) {
        if(flag=='publishAct'){
            submitData.statusCd='101';
        }else{
            submitData.statusCd='103';
        }
        var randTime="?t="+new Date().getTime();
        var index = layer.msg('提交中，请稍候',{icon: 16,time:false,shade:0.8});
        $.ajax({
            type: 'post' ,
            url: contextPath+"/otherAct/saveActivity"+randTime,
            cache:false ,
            async:true ,
            contentType: "application/json",
            data: JSON.stringify(submitData),
            dataType:'json',
            success:function(response) {
                debugger;
                layer.close(index);
                //展示数据
                if (response.successed) {
                    layer.msg("活动保存成功");
                    resetOtherInfo();
                    return true;
                }else{
                    if(response.data.resultMsg!=null&&response.data.resultMsg!=undefined){
                        layer.msg(response.data.resultMsg);
                    }else{
                        layer.msg("活动保存失败");
                    }
                }
            },
            error:function(response){
                layer.close(index);
                layer.msg("活动保存失败");
            }
        });
        return false;
    }
    //公共校验
    function commonValid() {
        submitData={};
        var actTitle=$.trim($("#actTitle").val());
        if(actTitle==''){
            layer.msg("请填写活动标题");
            return false;
        }
        submitData.activityTitle=actTitle;
        var showTime=$.trim($("#showTime").val());
        if(showTime==''){
            layer.msg("请选择活动展示时间");
            return false;
        }
        submitData.showTime=replaceDate(showTime);
        var startTime=$.trim($("#startTime").val());
        if(startTime==''){
            layer.msg("请选择活动开始时间");
            return false;
        }
        submitData.startTime=replaceDate(startTime);
        var endTime=$.trim($("#endTime").val());
        if(endTime==''){
            layer.msg("请选择活动结束时间");
            return false;
        }
        submitData.endTime=replaceDate(endTime);
        submitData.activityDesc=$.trim($("#activityDesc").val());
        submitData.ssoUrl=$.trim($("#ssoUrl").val());
        var content=layedit.getContent(index);//获取富文本框值
        submitData.activityRule=content;
        submitData.banners=banners;
        return true;
    }
    function replaceDate(dateStr) {
        return dateStr.replace("年",'-').replace("月",'-').replace("日",'');
    }
    //banner文件上传
    function bannerUpload(objId) {
        var index;
        var upload = layui.upload;
        upload.render({
            elem: '#banner'+objId
            ,data:{
                linkType:2,
                fileType:2
            }
            ,before: function(obj){
                index = layer.msg('图片上传中，请稍候',{icon: 16,time:false,shade:0.8});
            }
            ,url: contextPath+'/file/uploadFile' //改成您自己的上传接口
            ,done: function(res){
                layer.close(index);
                //上传成功
                if(res.resultCode==0){
                    var classCode=objId;
                    var isExist=false;
                    for(var it in banners){
                        if(banners[it].classCode==classCode){
                            isExist=true;
                            banners[it].imgId=res.result.imgId;
                            banners[it].imgCode=res.result.fileDir;
                        }
                    }
                    if(!isExist){
                        var banner={
                            classCode:classCode,
                            imgId:res.result.imgId,
                            imgCode:res.result.fileDir
                        }
                        banners.push(banner);
                    }
                    layer.msg('上传图片成功');
                    $("#banner"+objId).html("<i class=\"layui-icon\">&#xe67c;</i>重新上传");
                    $('input:checkbox[name="banners"]').each(function (index, element) {
                        if($(this).val()==objId){
                            $(this).attr("checked",true);
                            layui.form.render();
                        }
                    });
                }else{
                    layer.msg('上传失败');
                }
            }
            ,error: function(){
                layer.close(index);
                layer.msg('上传失败');
            }
        });
    }
});