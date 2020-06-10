layui.use(['form','layer','laypage','laydate','layedit','table','upload'],function(){
    var table = layui.table;
    var submitData={};
    var prizes=[];//封装奖品数据
    var banners=[];//封装banner图片的上传路径
    var pindex=0;
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
    //地区全选效果
    form.on('checkbox(checkAreaAll)', function(data){
        if(data.elem.checked){
            areaAllCheck('1');
        }else{
            areaAllCheck('0');
        }
    });
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
    //监听发布
    form.on('submit(publishAct)', function(data){
        if(!commonValid()){
            return;
        }
        toSubmit('publishAct');
        return false;
    });
    //监听保存
    form.on('submit(saveAct)', function(data){
        if(!commonValid()){
            return false;
        }
        toSubmit('saveAct');
        return false;
    });
    $(document).ready(function(){
        $("body").on("click","#addPrize",function() {  //选择奖品
            openChoosePrize();
        });
        $("body").on("click","#importAct",function() {  //选择活动
            openChooseAct();
        });
        initTable();
        bannerUpload('102');
        bannerUpload('103');
        bannerUpload('105');
        var activityId=$("#contentDiv").attr("data-id");
        var flag=$("#contentDiv").attr("data-type");
        if(activityId!=undefined&&activityId!=null&&activityId!=''){//编辑初始化
            initEditInfo(activityId,flag,"");
        }
    });
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
    //提交
    function toSubmit(flag) {
    	var statusCd = $("#contentDiv").attr("data-statuscd");
        if(flag=='publishAct'){
            submitData.statusCd='101';
        }else if(statusCd!=null&&statusCd!=''){
            submitData.statusCd=statusCd;
        }else{
            submitData.statusCd='103';
        }
        var activityId = $("#contentDiv").attr("data-id");
        if(activityId!=undefined&&activityId!=null&&activityId!=''){
        	submitData.activityId = activityId;
        }
        var randTime="?t="+new Date().getTime();
        var index = layer.msg('提交中，请稍候',{icon: 16,time:false,shade:0.8});
        $.ajax({
            type: 'post' ,
            url: contextPath+"/lottery/saveActivities"+randTime,
            cache:false ,
            async:true ,
            contentType: "application/json",
            data: JSON.stringify(submitData),
            dataType:'json',
            success:function(response) {
                layer.close(index);
                //展示数据
                if (response.successed) {
                	layer.msg("活动保存成功");
                    location.href = contextPath + '/lottery/index';                	
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
    	 var actTitle=$.trim($("#actTitle").val());
         if(actTitle==''){
             layer.msg("请填写活动标题");
             return false;
         }
         submitData.activityTitle=actTitle;
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
        if(prizes.length<1){
            layer.msg("请至少选择1个权益商品用与转盘抽奖");
            return false;
        }
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
        var rules=getRuleDatas();
        var areaRelRules = rules.areaRelRules;
        if(areaRelRules.length < 1){
            layer.msg("请至少选择1个活动地区用与转盘抽奖");
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
        submitData.showRuleMode=rules.ruleData;//活动表的规则字段
        submitData.ruleDataArray=rules.ruleDataArray;//落规则表数据
        submitData.areaRelRules=areaRelRules;//获取区域落规则集合        
        var content=layedit.getContent(index);//获取富文本框值
        var ruleYD = $('input[name="ruleYD"]:checked').val(); 
        var YdNum = $.trim($("#YdNum").val());
        var activityDesc = $.trim($("#activityDesc").val());//活动简介字段
        submitData.activityRule=content;//活动说明字段
        submitData.banners=banners;
        submitData.prizes=prizes;
        submitData.ruleYD = ruleYD;
        submitData.YdNum = YdNum;
        submitData.activityDesc = activityDesc;
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
                "regionCode":regionCode,
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
    //重载选择的奖品表格
    function reloadTable() {
        table.reload('prizeTableReload', {
            data:prizes
        });
    }
    //初始化奖品表格
    function initTable() {
        //展示已知数据
    	var statusCd = $("#contentDiv").attr("data-statuscd");    
    	if(statusCd == '104'){
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
    	}else{
    		table.render({
                elem: '#prizeTable'
                , cols: [[ //标题栏
                    {field: 'productCode', title: '商品编码'}
                    , {field: 'prizeTitle', title: '奖品名称'}
                    /*, {
                     field: 'prodSupplier', title: '提供平台',
                        templet: function (res) {
                            if (res.prodSupplier == '11') {
                                return "电信自有";
                            } else {
                                return "第三方合作";
                            }
                        }
                    }
                    , {field: 'prizeVal', title: '奖品面额'}*/
                    , {field: 'prizeCount', title: '奖品总数量'}
                    , {field: 'usePrizeCount', title: '奖励数量', edit: 'text'}
                    , {field: 'luckLevel', title: '奖品等级', edit: 'text'}
                    , {field: 'weightVal', title: '奖品概率', edit: 'text'}
                    , {field: 'prizeUse', title: '使用方式', width: 200, edit: 'text'}
                    , {field: '', title: '操作',toolbar: '#toolBar'}
                ]]
                , data: prizes
                , id: 'prizeTableReload'
            });
    		//监听单元格编辑
            table.on('edit(prizeTable)', function(obj){
                var value = obj.value //得到修改后的值
                    ,data = obj.data //得到所在行所有键值
                    ,field = obj.field; //得到字段
                if(field=='usePrizeCount') {
                    if (!(/(^[1-9]\d*$)/.test(value))) {
                        layer.msg("奖励数量请输入正确的数字");
                        reloadTable();
                    }else if(parseInt(value)>data.prizeCount){
                        layer.msg("奖励数量必须大于0并且小于奖品总数量");
                        reloadTable();
                    }else{
                        for (var it in prizes) {
                            if(prizes[it].productCode==data.productCode){
                                prizes[it].usePrizeCount=value;
                            }
                        }
                    }
                }else if(field=='prizeUse'){
                    if (value.length > 2000) {
                        layer.msg("奖品使用方式不得超过2000字符");
                        reloadTable();
                    }else{
                        for (var it in prizes) {
                            if(prizes[it].productCode==data.productCode){
                                prizes[it].prizeUse=value;
                            }
                        }
                    }
                }else if(field=='luckLevel'){
                    if (!(/(^[1-9]\d*$)/.test(value))) {
                        layer.msg("奖品等级请输入1-7的数字");
                        reloadTable();
                    }else if(parseInt(value)>7||parseInt(value)<1){
                        layer.msg("奖品等级请输入1-7的数字");
                        reloadTable();
                    }else{
                    	for (var it in prizes) {
                            if(prizes[it].productCode==data.productCode){
                                prizes[it].luckLevel=value;
                            }
                        }
                    }
                }else if(field=='weightVal'){
                    if (!(/(^[1-9]\d*$)/.test(value))) {
                        layer.msg("奖品概率请输入0-100的数字");
                        reloadTable();
                    }else if(parseInt(value)>100||parseInt(value)<0){
                        layer.msg("奖品概率请输入0-100的数字");
                        reloadTable();
                    }else{
                    	for (var it in prizes) {
                            if(prizes[it].productCode==data.productCode){
                                prizes[it].weightVal=value;
                            }
                        }
                    }
                }
            });
          //监听工具条
            table.on('tool(prizeTable)', function(obj){
                var data = obj.data;
                if(obj.event === 'toChoose'){
                    openChoosePrize(data.prizeIndex);
                }
                if(obj.event === 'toDelete'){
                	delChoosePrize(obj);
                }
            });
    	}
    }
    //打开选择奖品
    function openChoosePrize(prizeIndex) {
        var index = layui.layer.open({
            title: "奖品选择",
            type: 2,
            area: ['900px',  "450px"],
            content: contextPath+"/qyproduct/prizeChoose",
            btn: ['确定', '关闭'],
            yes: function(index, layero){
                var iframeWin = window[layero.find('iframe')[0]['name']];
                var result=iframeWin.validSumit();
                if(result==0){
                    var choosedPrize=iframeWin.returnPrice;
                    //编辑的时候替换
                    if(prizeIndex!=null&&prizeIndex!=undefined){
                        for (var it in prizes) {
                           if(prizes[it].prizeIndex!=prizeIndex){//与非编辑的商品编码冲突
                               if(prizes[it].productCode==choosedPrize.productCode && prizes[it].prizeType==choosedPrize.prizeType){
                                   layer.msg('当前选择的权益商品重复，请重新选择');
                                   return;
                               }
                           }
                        }
                        for (var it in prizes) {
                            if(prizes[it].prizeIndex==prizeIndex){
                                prizes[it]=choosedPrize;
                                prizes[it].prizeIndex=prizeIndex;
                            }
                        }
                    }else{
                        for (var it in prizes) {
                            if(prizes[it].productCode==choosedPrize.productCode && prizes[it].prizeType==choosedPrize.prizeType){
                            	layer.msg('当前选择的权益商品重复，请重新选择');
                                return;
                            }
                        }
                        if(prizes.length>7){
                            layer.msg('最多选择7个权益商品用于抽奖');
                            return;
                        }
                        pindex++;
                        var prize=choosedPrize;
                        prize.prizeIndex=pindex;
                        prize.luckLevel=pindex;
                        prize.weightVal=0;
                        prizes.push(prize);
                    }
                    reloadTable();
                    layer.close(index);
                }else{
                    layer.msg(iframeWin.returnPrice.msg);
                }
            }
        });
    }
    
    //删除选择的奖品
    function delChoosePrize(obj) {
    	var prizeIndex = obj.data.prizeIndex;
    	prizes.splice((prizeIndex - 1), 1);
    	obj.del();
    	pindex = 1;
    	for (var it in prizes) {
    		prizes[it].prizeIndex = pindex;
    		pindex++;
        }
        reloadTable();
    }
    
    //编辑界面初始化
    function initEditInfo(activityId,flag,env) {
        var params={
            "activityId":activityId,
            "env":env
        }
    	var statusCd = $("#contentDiv").attr("data-statuscd");
        if(flag=='edit'){
            params.statusCd = statusCd;
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
    			pindex++;
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
        for(it in actRuleDatas){
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
            if($(this).val() == objId){
                $(this).attr("checked", true);
            }
            layui.form.render();
        });
    }
    
    //打开选择活动
    function openChooseAct(prizeIndex) {
        var index = layui.layer.open({
            title: "活动导入",
            type: 2,
            area: ['900px',  "450px"],
            content: contextPath+"/lottery/index?source=choose",
            btn: ['确定', '关闭'],
            yes: function(index, layero){
            	var iframeWin = window[layero.find('iframe')[0]['name']];
                var result = iframeWin.chooseActId;
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
        prizes=[];//封装奖品数据
        banners=[];//封装banner图片的上传路径
        $("#showTime").val('${startTime}');
        $("#startTime").val('${startTime}');
        $("#endTime").val('${endTime}');
        $("#YdNum").val('');
        $("#prizeTable").html('');
        $('input:checkbox[name="ruleType"]').each(function (index, element) {
            $(this).removeAttr("checked");
        });
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
});
