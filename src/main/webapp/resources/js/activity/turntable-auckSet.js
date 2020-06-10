layui.use(['form','layer','laypage','table','laydate','layedit'],function() {
    var prizes=[];//封装奖品数据
    var prizesTemp="";
    var editPrize=[];
    var submitData={};
    var table = layui.table;
    var laydate = layui.laydate;
    var form = layui.form;
    var layedit = layui.layedit;
    laydate.render({
        elem: '#endTime',
        type: 'datetime',
        trigger: 'click',
        min:$("#startTime").val(),
        format: 'yyyy-MM-dd HH:mm:ss'
    });
    //自定义工具栏
    var index=layedit.build('activityRule', {
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
    //监听保存
    form.on('submit(saveAct)', function(data){
        if(!commonValid()){
            return false;
        }
        toSubmit();
        return false;
    });
    $(document).ready(function(){
        initTable();
        var activityId=$("#contentDiv").attr("data-id");
        if(activityId!=undefined&&activityId!=null&&activityId!=''){//编辑初始化
            initEditInfo(activityId);
        }
    });
    function toSubmit() {
        var activityId = $("#contentDiv").attr("data-id");
        if(activityId!=undefined&&activityId!=null&&activityId!=''){
            submitData.activityId = activityId;
        }
        var randTime="?t="+new Date().getTime();
        var index = layer.msg('提交中，请稍候',{icon: 16,time:false,shade:0.8});
        $.ajax({
            type: 'post' ,
            url: contextPath+"/lottery/editActivities"+randTime,
            cache:false ,
            async:true ,
            contentType: "application/json",
            data: JSON.stringify(submitData),
            dataType:'json',
            success:function(response) {
                layer.close(index);
                //展示数据
                if (response.successed) {
                    layer.msg("活动修改成功");
                    location.href = contextPath + '/lottery/index';
                }else{
                    if(response.data.resultMsg!=null&&response.data.resultMsg!=undefined){
                        layer.msg(response.data.resultMsg);
                    }else{
                        layer.msg("活动修改失败");
                    }
                }
            },
            error:function(response){
                layer.close(index);
                layer.msg("活动修改失败");
            }
        });
        return false;
    }
    //公共校验
    function commonValid() {
        var probability=$.trim($("#probability").val());
        if(probability==''){
            layer.msg("请填写活动中奖概率");
            return false;
        }
        if (!(/(^[1-9]\d*$)/.test(probability))) {
            layer.msg("中奖概率请输入0-100的数字");
            return false;
        }else if(parseInt(probability)>100||parseInt(probability)<0){
            layer.msg("中奖概率请输入0-100的数字");
            return false;
        }
        submitData.probability = probability;
        var endTime=$.trim($("#endTime").val());
        if(endTime==''){
            layer.msg("请选择活动结束时间");
            return false;
        }
        var countRatebox=$('input:radio[name="countRatebox"]:checked').val();
        if(countRatebox=='1'){
            var countRate=$.trim($("#countRate").val());
            if(countRate==''){
                layer.msg("请输入限制的抽奖次数");
                return false;
            }
            submitData.countRate=countRate;
        }
        submitData.endTime=replaceDate(endTime);
        var content=layedit.getContent(index);//获取富文本框值
        var activityDesc = $.trim($("#activityDesc").val());//活动简介字段
        submitData.activityRule=content;//活动说明字段
        submitData.prizes=editPrize;
        submitData.activityDesc = activityDesc;
        return true;
    }
    function replaceDate(dateStr) {
        return dateStr.replace("年",'-').replace("月",'-').replace("日",'');
    }
    //编辑界面初始化
    function initEditInfo(activityId) {
        var params={
            "activityId":activityId,
            "env":""
        }
        var randTime="?t="+new Date().getTime();
        var index = layer.msg('数据查询中，请稍候',{icon: 16,time:false,shade:0.8});
        $.ajax({
            type: 'post' ,
            url: contextPath+"/lottery/qryActEditInfo"+randTime,
            cache:false ,
            async:true ,
            contentType: "application/json",
            data: JSON.stringify(params),
            dataType:'json',
            success:function(response) {
                layer.close(index);
                //展示数据
                if (response.successed) {
                    var coActivity=response.data.coActivity;
                    var prizeList=response.data.prizeList;
                    var coActRules=response.data.coActRules;
                    var actRuleDatas = response.data.actRuleDatas;
                    var coActivityImgs=response.data.coActivityImgs;
                    //初始化活动字段
                    intiActInfo(coActivity);
                    //初始化奖品节点
                    intiPrizeInfo(prizeList);
                    //初始化活动规则
                    if(coActRules!=undefined&&coActRules!=null&&coActRules.length>0){
                        var showRuleMode=coActivity.showRuleMode;
                        initRuleData(coActRules,showRuleMode);
                    }
                    //初始化地市规则
                    intiAreaInfo(actRuleDatas);
                    //初始化活动图片
                    if(coActivityImgs!=undefined&&coActivityImgs!=null&&coActivityImgs.length>0){
                        intiActImg(coActivityImgs);
                    }
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
        $("#YdNum").val(coActivity.useAccount);
        $("#activityDesc").val(coActivity.activityDesc);
        $("#probability").val(coActivity.probability);
        layedit.setContent(index, coActivity.activityRule,false);
    }
    //初始化奖品节点
    function intiPrizeInfo(prizeList) {
        if(prizeList != null && prizeList != undefined){
            for(i in prizeList){
                var prize = {};
                prize.luckId = prizeList[i].LUCK_ID;
                prize.prizeId = prizeList[i].PRIZE_ID;
                prize.prizeUse = prizeList[i].PRIZE_USE;
                prize.prizeCount = prizeList[i].PRIZE_COUNT;
                prize.prizeStock = prizeList[i].PRIZE_STOCK;
                var prizeType = prizeList[i].PRIZE_TYPE;
                prize.prizeType = prizeType;
                prize.usePrizeCount = prizeList[i].USER_PRIZE_COUNT;
                prize.productCode = prizeList[i].PRODUCT_CODE;
                prize.prizeTitle = prizeList[i].PRIZE_TITLE;
                prize.prodSupplier =  prizeList[i].PROD_SUPPLIER;
                prize.prizeVal =  prizeList[i].PRIZE_VAL;
                prize.prizeDesc =  prizeList[i].PRIZE_DESC;
                prize.luckLevel=prizeList[i].LUCK_LEVEL;
                prize.weightVal=prizeList[i].WEIGHT_VAL;
                prizes.push(prize);
            }
            prizesTemp=JSON.stringify(prizes);
            reloadTable();
        }
    }
    //初始化规则显示
    function initRuleData(coActRules,showRuleMode) {
        //复选框处理
        for(it in coActRules){
            var ruleType=coActRules[it].ruleType;
            var ruleData=coActRules[it].ruleData;
            var ruleCode=coActRules[it].ruleCode;
            $('input:checkbox[name="ruleType"]').each(function (index, element) {
                if($(this).val() == ruleType){
                    $(this).attr("checked", true);
                    $(this).attr("ruleId", coActRules[it].ruleId);
                    if(ruleType=='1'){//星级
                        ruleData=ruleData.replace(/>/g,"");
                        ruleData=ruleData.replace(/=/g,"");
                        $("#ruleData1").val(ruleData);
                        $("#ruleDataDiv1").show();
                    }
                    layui.form.render('select');
                }
            });
            if(ruleCode=='ACT_RULE_RATECOUNT'){
                $('input:radio[name="countRatebox"]').each(function (index, element) {
                    if($(this).val()=='1'){
                        $(this).attr("checked", true);
                    }
                });
                ruleData=ruleData.replace(/>/g,"");
                ruleData=ruleData.replace(/=/g,"");
                $("#countRate").val(ruleData);
            }
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
            layui.form.render();
        }
    }
    //初始化活动地区
    function intiAreaInfo(actRuleDatas) {
        //复选框处理
        for(var it in actRuleDatas){
            var ruleData=actRuleDatas[it].ruleData;
            $('input:checkbox[name="areaRel"]').each(function (index, element) {
                if($(this).val() == ruleData){
                    $(this).attr("checked", true);
                    $(this).attr("expId", actRuleDatas[it].expId);
                    layui.form.render();
                }
            });
        }
    }
    //初始化活动banner图片
    function intiActImg(coActivityImgs) {
        for(var it in coActivityImgs){
            actImgDeal(coActivityImgs[it].classCode);
        }
    }
    function actImgDeal(objId) {
        $("#banner"+objId).html("<i class=\"layui-icon\">&#xe67c;</i>重新上传");
        $('input:checkbox[name="banners"]').each(function (index, element) {
            if($(this).val() == objId){
                $(this).attr("checked", true);
            }
            layui.form.render();
        });
    }
    //初始化奖品表格
    function initTable() {
        //展示已知数据
        table.render({
            elem: '#prizeTable'
            , cols: [[ //标题栏
                {field: 'productCode', title: '商品编码'}
                , {field: 'prizeTitle', title: '奖品名称'}
                , {field: 'prizeCount', title: '奖品总数量'}
                , {field: 'prizeStock', title: '奖品库存',edit: 'text'}
                , {field: 'usePrizeCount', title: '奖励数量'}
                , {field: 'luckLevel', title: '奖品等级'}
                , {field: 'weightVal', title: '奖品概率',edit: 'text'}
                , {field: 'prizeUse', title: '使用方式', width: 200}
            ]]
            , data: prizes
            , id: 'prizeTableReload'
        });
        //监听单元格编辑
        table.on('edit(prizeTable)', function(obj){
            var value = obj.value //得到修改后的值
                ,data = obj.data //得到所在行所有键值
                ,field = obj.field; //得到字段
            if(field=='weightVal') {
                if (!(/(^[1-9]\d*$)/.test(value))) {
                    layer.msg("奖品概率请输入0-100的数字");
                    reloadTable();
                }else if(parseInt(value)>100||parseInt(value)<0){
                    layer.msg("奖品概率请输入0-100的数字");
                    reloadTable();
                }else{
                    var isExist=false;
                    for (var it in editPrize) {
                        if(editPrize[it].luckId==data.luckId){
                            editPrize[it].weightVal=value;
                            isExist=true;
                        }
                    }
                    if(!isExist){
                        editPrize.push(data);
                    }
                    for (var it in prizes) {
                        if(prizes[it].luckId==data.luckId){
                            prizes[it].weightVal=value;
                        }
                    }
                }
            }else if(field=='prizeStock'){
                if($.trim(value)==''){
                    layer.msg("商品库存请输入正确的数字");
                    reloadTable();
                    return;
                }
                if (!(/(^[1-9]\d*$)/.test(value))) {
                    layer.msg("商品库存请输入大于0的数字");
                    reloadTable();
                    return;
                }
                var resultTemp=JSON.parse(prizesTemp);
                var editPrizeTemp={};
                for (var it in resultTemp) {
                    if (resultTemp[it].prizeId == data.prizeId) {
                        editPrizeTemp=resultTemp[it];
                    }
                }
                if(parseInt(editPrizeTemp.prizeStock)==0) {
                    if(parseInt(value)<=0){
                        layer.msg("商品库存请输入大于0的数字");
                        reloadTable();
                        return;
                    }else{
                        var isExist=false;
                        for (var it in editPrize) {
                            if(editPrize[it].prizeId==data.prizeId){
                                editPrize[it].prizeStock=value;
                                editPrize[it].isEditStock="1";
                                isExist=true;
                            }
                        }
                        if(!isExist){
                            data.prizeCount=parseInt(editPrizeTemp.prizeCount)+parseInt(value);
                            data.isEditStock="1";
                            editPrize.push(data);
                        }
                        for (var it in prizes) {
                            if(prizes[it].prizeId==data.prizeId){
                                prizes[it].prizeStock=value;
                                prizes[it].prizeCount=parseInt(prizes[it].prizeCount)+parseInt(value);
                            }
                        }
                        reloadTable();
                    }
                }else{
                    layer.msg("库存为0时才能追加");
                    reloadTable();
                    return;
                }
            }
        });
    }
    //重载选择的奖品表格
    function reloadTable() {
        table.reload('prizeTableReload', {
            data:prizes
        });
    }
});