layui.use(['form','layer','laypage','laydate','layedit','table','upload'],function(){
    var actHourTimes=[];//时会场秒杀节点
    var actDayTimes=[];//日会场秒杀节点
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
        $("body").on("click", ".date-close", function () {
            for(var it in actDayTimes){
                if(actDayTimes[it]== $(this).attr("data-id")){
                    actDayTimes.splice(it,1);
                }
            }
            $(this).parent().remove();
        });
        $("body").on("click", "#importAct", function () {
            openChooseAct();
        });
        $("body").on("click", "#resetData", function () {
            resetOtherInfo();
        });
    });
    var curDate = new Date();
    laydate.render({
        elem: '#startTime',
        format: 'yyyy年MM月dd日',
        trigger: 'click',
        min: dateFormat(curDate)
    });
    laydate.render({
        elem: '#showTime',
        format: 'yyyy-MM-dd HH:mm:ss',
        type: 'datetime',
        trigger: 'click',
        min: dateFormat(curDate)
    });
    //时间范围
    var ins1=laydate.render({
        elem: '#dayTimeRang'
        ,type: 'time'
        ,range: true,
        value: '00:00:00 - 23:59:59',
        trigger: 'click',
        change: function(value, date, endDate){
            var startDt=$.trim(value.split("-")[0]);
            var endDt=$.trim(value.split("-")[1]);
            startDt=startDt.replace(":","").replace(":","");
            endDt=endDt.replace(":","").replace(":","");
            if(startDt>=endDt){
                ins1.hint("开始时间必须小于结束时间");
                $("#dayTimeRang").val("");
            }
        }
    });
    //日会场增加日期
    laydate.render({
        elem: '#addTime',
        format: 'yyyy年MM月dd日',
        trigger: 'click',
        min: dateFormat(curDate),
        done: function(value, date, endDate){
            var valueFormat=replaceDate(value);
            var isExist=false;
            for(it in actDayTimes){
                if(actDayTimes[it]==valueFormat){
                    layer.msg("日期选择重复");
                    isExist=true;
                }
            }
            if(!isExist){
                actDayTimes.push(valueFormat);
                $("#dateTimeShow").append("<p class=\"seckill-day\">" +value + "<a class=\"date-close\" data-id=\""+valueFormat+"\">-</a></p>");
            }
            $("#addTime").val("");
        }
    });
    //活动类型单选框效果
    form.on('radio(isCheckType)', function(data){
        if(data.elem.checked){
            if(data.elem.value=='1'){
                $("#hourTimes").show();
                $("#dayTimes").hide();
            }else{
                $("#hourTimes").hide();
                $("#dayTimes").show();
            }
        }
    });
    //地区全选效果
    form.on('checkbox(checkAreaAll)', function(data){
        if(data.elem.checked){
            areaAllCheck('1');
        }else{
            areaAllCheck('0');
        }
    });

    //星级规则选择
    form.on('checkbox(isCheckXj)', function(data){
        if(data.elem.value=='1'){
            if(data.elem.checked){
                $("#ruleDataDiv1").show();
            }else{
                $("#ruleDataDiv1").hide();
            }
        }
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
    //时间点选择效果
    form.on('checkbox(isCheckHour)', function(data){
        if(data.elem.name=='hourTime'){
            if(data.elem.checked){
                var isExist=false;
                for(it in actHourTimes){
                    if(actHourTimes[it]==data.elem.value){
                        isExist=true;
                    }
                }
                if(!isExist){
                    actHourTimes.push(data.elem.value);
                }
            }else{
                for(it in actHourTimes){
                    if(actHourTimes[it]==data.elem.value){
                        actHourTimes.splice(it,1);
                    }
                }
            }
        }
    });
    form.on('submit(publishAct)', function(data){
        if(!commonValid()){
            return false;
        }
        return toEditSumit('publishAct');
    });
    form.on('submit(saveAndNext)', function(data){
        if(!commonValid()){
            return false;
        }
        var flag=$("#contentDiv").attr("data-type");
        if(flag=='add') {
            return toSubmit('saveAndNext');
        }else{
            return toEditSumit('saveAndNext');
        }
    });
    form.on('submit(saveAct)', function(data){
        if(!commonValid()){
            return false;
        }
        var flag=$("#contentDiv").attr("data-type");
        if(flag=='add') {
            return toSubmit('saveAct');
        }else{
            return toEditSumit('saveAct');
        }
    });
    //全选和全不选
    function areaAllCheck(flag) {
        if(flag=='1'){
            $(":checkbox[name='areaRel']").attr("checked", true);
        }else{
            $(":checkbox[name='areaRel']").removeAttr("checked");
        }
        layui.form.render();
    }
    //重置
    function resetOtherInfo() {
        $("#contentDiv")[0].reset();
        submitData={};
        actHourTimes=[];//时会场秒杀节点
        actDayTimes=[];//日会场秒杀节点
        banners=[];//封装banner图片的上传路径
        $("#dateTimeShow").html("");
        var flag=$("#contentDiv").attr("data-type");
        if(flag=='add') {
            $('input:radio[name="actType"]').each(function (index, element) {
                if ($(this).val() == '1') {
                    $(this).attr("checked", true);
                } else {
                    $(this).removeAttr("checked");
                }
                $("#hourTimes").show();
                $("#dayTimes").hide();
            });
        }
        $(":checkbox[name='hourTime']").removeAttr("checked");
        $(":checkbox[name='ruleType']").removeAttr("checked");
        $('input:checkbox[name="banners"]').each(function (index, element) {
            $(this).removeAttr("checked");
            var value=$(this).val();
            $("#banner"+value).html("<i class=\"layui-icon\">&#xe67c;</i>上传图片");
        });
        $("#ruleDataDiv1").hide();
        $(":checkbox[name='areaRelAll']").removeAttr("checked");
        $(":checkbox[name='areaRel']").removeAttr("checked");
        layui.form.render();
    }
    //打开选择
    function openChooseAct() {
        var index = layui.layer.open({
            title: "活动选择",
            type: 2,
            area: ['900px',  "450px"],
            content: contextPath+"/seckill/index?source=choose",
            btn: ['确定', '关闭'],
            yes: function(index, layero){
                var iframeWin = window[layero.find('iframe')[0]['name']];
                var result=iframeWin.chooseActId;
                var isTestEnv=iframeWin.isTestEnv;
                if(result!=''){
                    //先重置
                    resetOtherInfo();
                    //加载初始化
                    initEditInfo(result,'',isTestEnv);
                    layer.close(index);
                }else{
                    layer.msg("");
                }
            }
        });
    }
    function toEditSumit(flag) {
        var activityId=$("#contentDiv").attr("data-id");
        submitData.activityId=activityId;
        if(flag=='publishAct'){
            submitData.statusCd='101';
        }else{
            submitData.statusCd='103';
        }
        var randTime="?t="+new Date().getTime();
        var index = layer.msg('提交中，请稍候',{icon: 16,time:false,shade:0.8});
        $.ajax({
            type: 'post' ,
            url: contextPath+"/seckill/editPro"+randTime,
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
                    if(flag=='saveAndNext'){
                        location.href=contextPath+"/seckill/proSet?activityId="+response.data.activityId
                    }else if(flag=='publishAct'){
                        $("#bottomTool").html("");
                        return true;
                    }else{
                        return true;
                    }
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
    function initEditInfo(activityId,flag,env) {
        var params={
            "activityId":activityId,
            "env":env
        }
        if(flag=='edit'){
            params.statusCd='103'
        }
        var randTime="?t="+new Date().getTime();
        var index = layer.msg('数据查询中，请稍候',{icon: 16,time:false,shade:0.8});
        $.ajax({
            type: 'post' ,
            url: contextPath+"/seckill/qryActEditInfo"+randTime,
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
                    var sonActivities=response.data.sonActivities;
                    var coActRules=response.data.coActRules;
                    var coActivityImgs=response.data.coActivityImgs;
                    var coActRuleDatas=response.data.coActRuleDatas;
                    //初始化活动字段\秒杀节点
                    intiActInfo(coActivity,sonActivities,flag);
                    //初始化活动规则
                    if(coActRules!=undefined&&coActRules!=null&&coActRules.length>0){
                        var showRuleMode=coActivity.showRuleMode;
                        initRuleData(coActRules,showRuleMode,coActRuleDatas);
                    }
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
    function intiActInfo(coActivity,sonActivities,flag) {
        $("#actTitle").val(coActivity.activityTitle);
        $("#showTime").val(coActivity.showTimeStr);
        $("#activityDesc").val(coActivity.activityDesc);
        layedit.setContent(index, coActivity.activityRule,false);
        $('input:radio[name="actType"]').each(function (index, element) {
            if($(this).val()==coActivity.seckillType){
                $(this).attr("checked", true);
            }
            if(flag=='edit'){
                $(this).attr("disabled", true);
            }
        });
       if(coActivity.seckillType=='1'){//时会场
           $("#startTime").val(coActivity.startTimeStr);
           $("#continueTime").val(coActivity.endTimeStr);
           for(it in sonActivities) {
               $('input:checkbox[name="hourTime"]').each(function (index, element) {
                   if($(this).val()==sonActivities[it].startTimeStr){
                       $(this).attr("checked", true);
                       actHourTimes.push($(this).val());
                   }
               });
           }
           $("#hourTimes").show();
           $("#dayTimes").hide();
       }else{
           $("#dayTimeRang").val(coActivity.endTimeStr);
           for(it in sonActivities) {
               var time=sonActivities[it].startTimeStr;
               var valueFormat=replaceDate(time);
               $("#dateTimeShow").append("<p class=\"seckill-day\">" +time + "<a class=\"date-close\" data-id=\""+valueFormat+"\">-</a></p>");
               actDayTimes.push(valueFormat);
           }
           $("#dayTimes").show();
           $("#hourTimes").hide();
       }
    }
    //初始化规则显示
    function initRuleData(coActRules,showRuleMode,coActRuleDatas) {
        //复选框处理
        for(it in coActRules){
            var ruleType=coActRules[it].ruleType;
            var ruleData=coActRules[it].ruleData;
            $('input:checkbox[name="ruleType"]').each(function (index, element) {
                if($(this).val()==ruleType){
                    $(this).attr("checked", true);
                    $(this).attr("ruleId", coActRules[it].ruleId);
                    if(ruleType=='1'){//星级
                        ruleData=ruleData.replace(/>/g,"");
                        ruleData=ruleData.replace(/=/g,"");
                        $("#ruleData1").val(ruleData);
                        $("#ruleDataDiv1").show();
                    }
                }
            });
            if(ruleType=='8'){
                coActRules.splice(it,1);
            }
        }
        //单选框条件处理
        var jsonArray=JSON.parse(showRuleMode);
        if(coActRules.length>1){//1个以上处理或和与关系
            if(jsonArray.length>1){//或
                $('input:radio[name="ruleContion"]').each(function (index, element) {
                    if($(this).val()=='OR'){
                        $(this).attr("checked", true);
                    }
                });
            }else{//与
                $('input:radio[name="ruleContion"]').each(function (index, element) {
                    if($(this).val()=='AND'){
                        $(this).attr("checked", true);
                    }
                });
            }
        }
        //区域处理
        for(var it in coActRuleDatas) {
            var ruleId = coActRuleDatas[it].ruleId;
            var ruleData = coActRuleDatas[it].ruleData;
            var expId = coActRuleDatas[it].expId;
            $('input:checkbox[name="areaRel"]').each(function (index, element) {
                if($(this).val()==ruleData) {
                    $(this).attr("checked", true);
                    $(this).attr("ruleId", ruleId);
                    $(this).attr("expId", expId);
                }
            });
        }
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
            url: contextPath+"/seckill/saveActivity"+randTime,
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
                    if(flag=='saveAndNext'){
                        location.href=contextPath+"/seckill/proSet?activityId="+response.data.activityId
                    }else if(flag=='publishAct'){
                        $("#bottomTool").html("");
                        return true;
                    }else{
                        resetOtherInfo();
                        return true;
                    }
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
        var actType=$('input:radio[name="actType"]:checked').val();
        submitData.seckillType=actType;
        if(actType=='1'){//时会场
            if(actHourTimes.length==0){
                layer.msg("请选择秒杀节点");
                return false;
            }
            submitData.times=actHourTimes;
            var startTime=$.trim($("#startTime").val());
            if(startTime==''){
                layer.msg("请选择活动日期");
                return false;
            }
            submitData.startTime=replaceDate(startTime);
            var continueTime=$.trim($("#continueTime").val());
            if(continueTime==''){
                layer.msg("请填写秒杀节点持续时间");
                return false;
            }
            if (!(/(^[1-9]\d*$)/.test(continueTime))) {
                layer.msg("持续时间请输入大于0的分钟数");
                return false;
            }
            if(parseInt(continueTime)>60){
                layer.msg("时会场持续时间需要小于60分钟");
                return false;
            }
            submitData.continueTime=continueTime;
        }else{
            if(actDayTimes.length==0){
                layer.msg("请选择秒杀节点");
                return false;
            }
            submitData.times=actDayTimes;
            var dayTime=$.trim($("#dayTimeRang").val());
            if(dayTime==''){
                layer.msg("请填写秒杀时间范围");
                return false;
            }
            var startDt=$.trim(dayTime.split("-")[0]);
            var endDt=$.trim(dayTime.split("-")[1]);
            startDt=startDt.replace(":","").replace(":","");
            endDt=endDt.replace(":","").replace(":","");
            if(startDt>=endDt){
                layer.msg("秒杀时间范围开始时间必须小于结束时间");
                return false;
            }
            submitData.continueTime=dayTime;
        }
        var rules=getRuleDatas();
        if(rules.areaRelRules.length==0){
            layer.msg("请至少选择一个地市");
            return false;
        }
        submitData.showRuleMode=rules.ruleData;
        submitData.ruleDataArray=rules.ruleDataArray;
        submitData.areaRelRules=rules.areaRelRules;
        submitData.activityDesc=$.trim($("#activityDesc").val());
        var content=layedit.getContent(index);//获取富文本框值
        submitData.activityRule=content;
        submitData.banners=banners;
        return true;
    }
    //拼装规则数据
    function getRuleDatas() {
        var result={};
        var rules=[];
        var areaRelRules=[];
        var ruleCode8="RULE_8";
        $('input:checkbox[name="areaRel"]:checked').each(function (index, element) {
            var regionCode=$(this).val();
            var ruleId=$(this).attr("ruleId");
            if(ruleId==undefined){
                ruleId="";
            }
            var expId=$(this).attr("expId");
            if(expId==undefined){
                expId="";
            }
            var areaRel={
                "ruleId" : ruleId,
                "ruleCode":ruleCode8,
                "expId":expId,
                "ruleTitle":'区域限制',
                "ruleType":'8',
                "ruleData":regionCode,
                "isShow":'1'
            };
            areaRelRules.push(areaRel);
        });
        var ruleContion=$('input:radio[name="ruleContion"]:checked').val();
        if(ruleContion==null){//设置默认选中
            ruleContion="OR";
        }
        var ruleDataPz=[];
        var ruleDataAnd=[];
        $('input:checkbox[name="ruleType"]:checked').each(function (index, element) {
            var type=$(this).val();
            var title=$(this).attr("title");
            var ruleCode="RULE_"+type;
            var ruleId=$(this).attr("ruleId");
            var rule={
                "ruleId" : ruleId,
                "ruleCode":ruleCode,
                "ruleTitle":title,
                "ruleType":type,
                "isShow":'1'
            }
            if(type=='1'){//星级需要取数
                var ruleData=">="+$("#ruleData1").val();
                rule.ruleData=ruleData;
            }else{
                rule.ruleData=">=1";
            }
            rules.push(rule);
            if(ruleContion=='OR'){
                var ruleDataOr=[];
                ruleDataOr.push(ruleCode);
                ruleDataOr.push(ruleCode8);
                ruleDataPz.push(ruleDataOr);
            }else{
                ruleDataAnd.push(ruleCode);
                ruleDataAnd.push(ruleCode8)
            }
        });
        if(ruleContion=='AND'&&ruleDataAnd.length>0){
            ruleDataPz.push(ruleDataAnd);
        }
        if(ruleDataPz.length==0){//没有其它条件，只有地市
            var areaRuleData=[];
            areaRuleData.push(ruleCode8);
            ruleDataPz.push(areaRuleData);
        }
        result.ruleData=JSON.stringify(ruleDataPz);//活动中的规则表达式
        result.ruleDataArray=rules;
        result.areaRelRules=areaRelRules;
        return result;
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