layui.use(['form','layer','laypage','laydate','layedit','table','upload'],function(){
    var table = layui.table;
    var submitData={};
    var prizes=[];//封装奖品数据
    var banners=[];//封装banner图片的上传路径
    var pindex=0;   
    var layedit = layui.layedit;
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
    var form = layui.form;
    form.on('checkbox(isCheckXj)', function(data){
        if(data.elem.name=='ruleType'){
            if(data.elem.checked){
                $("#ruleDataDiv1").show();
            }else{
                $("#ruleDataDiv1").hide();
            }
        }
    });
    form.on('radio(isCheckYd)', function(data){
        if(data.elem.checked){
            $("#YdNum").val("0");
        }
    });
    form.on('submit(back)', function(data){
    	location.href = contextPath + "/lottery/index";
    	return false;
    });
    $(document).ready(function(){
        initTable();
        bannerUpload('listBanner');
        bannerUpload('topBanner');
        bannerUpload('firstBanner');
        var activityId=$("#contentDiv").attr("data-id");
        if(activityId!=undefined&&activityId!=null&&activityId!=''){//编辑初始化
            initQueryInfo(activityId);
        }
    });
    //banner文件上传
    function bannerUpload(objId) {
        var upload = layui.upload;
        upload.render({
            elem: '#'+objId
            ,data:{
                linkType:2,
                fileType:2
            }
            ,url: contextPath+'/file/uploadFile' //改成您自己的上传接口
            ,done: function(res){
                //上传成功
                if(res.resultCode==0){
                    var classCode="";
                    if(objId=='listBanner'){
                        classCode='101';
                    }else if(objId=='topBanner'){
                        classCode='102';
                    }else if(objId=='firstBanner'){
                        classCode='103';
                    }
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
                    $("#"+objId).html("<i class=\"layui-icon\">&#xe67c;</i>重新上传");
                }else{
                    layer.msg('上传失败');
                }
            }
            ,error: function(){
                layer.msg('上传失败');
            }
        });
    }
 
    //重载选择的奖品表格
    function reloadTable() {
        table.reload('prizeTableReload', {
            data:prizes
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
                , {field: 'usePrizeCount', title: '奖励数量'}
                , {field: 'luckLevel', title: '奖品等级'}
                , {field: 'weightVal', title: '奖品概率'}
                , {field: 'prizeUse', title: '使用方式', width: 200}
            ]]
            , data: prizes
            , id: 'prizeTableReload'
        });
    }
      
    //编辑界面初始化
    function initQueryInfo(activityId) {
        var params={
            "activityId":activityId
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
        $("#probability").val(coActivity.probability);
    	$("#activityDesc").val(coActivity.activityDesc);
        layedit.setContent(index, coActivity.activityRule,false);
    }
    //初始化奖品节点
    function intiPrizeInfo(prizeList) {
    	if(prizeList != null && prizeList != undefined){
    		var pindex = 0;
    		for(i in prizeList){
    			pindex++;
    			var prize = {};
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
                prize.prizeIndex=pindex;
                prize.luckLevel=prizeList[i].LUCK_LEVEL;
                prize.weightVal=prizeList[i].WEIGHT_VAL;
                prizes.push(prize);
        	}
    		reloadTable();
    	}
    }
    //初始化规则显示
    function initRuleData(coActRules,showRuleMode) {
        //复选框处理
        for(var it in coActRules){
            var ruleType=coActRules[it].ruleType;
            var ruleData=coActRules[it].ruleData;
            var ruleCode=coActRules[it].ruleCode;
            $('input:checkbox[name="ruleType"]').each(function (index, element) {
            	if($(this).val() == ruleType){
                    $(this).attr("checked", true);
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
                    layui.form.render();
                }    
            });
        }
    }
    //初始化活动banner图片
    function intiActImg(coActivityImgs) {
        for(var it in coActivityImgs){
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
            if($(this).val() == objId){
                $(this).attr("checked", true);
            }
            layui.form.render();
        });
    }
});
