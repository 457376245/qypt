layui.use(['form','layer','laypage','table'],function(){
    var table = layui.table;
    var addPro=[];
    var delPro=[];
    var editPro=[];
    var tableDatas=[];
    var form = layui.form;
    form.on('submit(saveAct)', function(data){
        return toSubmit('saveAct');
    });
    form.on('submit(publishAct)', function(data){
        return toSubmit('publishAct');
    });
    $(document).ready(function(){
        initTables();
        $("body").on("click", ".actAdd_btn", function () {
            var actId=$(this).attr("data-id");
            openChoosePro(actId);
        });
    });
    function toSubmit(flag) {
        var statusCd='103';
        if(flag=='saveAct'){
            if(addPro.length==0&&delPro.length==0&&editPro.length==0){
                layer.msg("未做任何操作");
                return false;
            }
        }
        if(flag=='publishAct'){//发布
            for (var it in tableDatas) {
                var tableData=tableDatas[it].data;
                if(tableData.length==0){
                    layer.msg("部分秒杀节点下未配置秒杀商品");
                    return false;
                }
            }
            statusCd='101';
        }
        var params={
            statusCd:statusCd,
            addPro:addPro,
            delPro:delPro,
            editPro:editPro,
            parentId:$("#contentDiv").attr("data-id")
        }
        var randTime="?t="+new Date().getTime();
        var index = layer.msg('提交中，请稍候',{icon: 16,time:false,shade:0.8});
        $.ajax({
            type: 'post' ,
            url: contextPath+"/seckill/saveActPro"+randTime,
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
                    layer.msg("秒杀商品保存成功");
                    addPro=[];
                    delPro=[];
                    editPro=[];
                    if(flag=='publishAct') {//发布
                        $("#bottomTool").html("");
                    }
                    return true;
                }else{
                    if(response.data.resultMsg!=null&&response.data.resultMsg!=undefined){
                        layer.msg(response.data.resultMsg);
                    }else{
                        layer.msg("秒杀商品保存失败");
                    }
                }
            },
            error:function(response){
                layer.close(index);
                layer.msg("秒杀商品保存失败");
            }
        });
        return false;
    }
    //打开选择
    function openChoosePro(actId) {
        var index = layui.layer.open({
            title: "奖品选择",
            type: 2,
            area: ['900px',  "450px"],
            content: contextPath+"/qyproduct/seckillChoose",
            btn: ['确定', '关闭'],
            yes: function(index, layero){
                var iframeWin = window[layero.find('iframe')[0]['name']];
                var result=iframeWin.validSumit();
                if(result==0){
                    var choosedPrize=iframeWin.returnPrice;
                    for (var it in tableDatas) {
                        //if(actId==tableDatas[it].actId){
                            var tableData=tableDatas[it].data;
                            for (var it1 in tableData) {
                                if(tableData[it1].productCode==choosedPrize.productCode){
                                    layer.msg('当前选择的权益商品重复，请重新选择');
                                    return;
                                }
                            }
                        //}
                    }
                    choosedPrize.activityId=actId;
                    addPro.push(choosedPrize);
                    for(var it in tableDatas){
                        if(tableDatas[it].actId==choosedPrize.activityId){
                            tableDatas[it].data.push(choosedPrize);
                        }
                    }
                    reloadTable(actId);
                    layer.close(index);
                }else{
                    layer.msg(iframeWin.returnPrice.msg);
                }
            }
        });
    }
    //重载
    function reloadTable(actId,seckillId) {
        var seckillTemp={};
        if(seckillId!=undefined){
            for(var it in tempDatas) {
                var id = tempDatas[it].activityId;
                if(id==actId){
                    var seckillTemps=tempDatas[it].slist;
                    for(var it1 in seckillTemps) {
                        if(seckillTemps[it1].seckillId==seckillId){
                            seckillTemp=seckillTemps[it1];
                        }
                    }
                }
            }
        }
        var tableData=[];
        for(var it in tableDatas) {
            if (tableDatas[it].actId == actId) {
                tableData=tableDatas[it].data;
                if(seckillId!=undefined) {
                    for (var it1 in tableData) {
                        if (tableData[it1].seckillId == seckillId) {
                            if (seckillTemp.seckillId != undefined) {
                                tableData[it1]=seckillTemp;
                            }
                        }
                    }
                }
            }
        }
        /*var tableData=[];
        for(var it in tableDatas) {
            if (tableDatas[it].actId == actId) {
                tableData = tableDatas[it].slist;
            }
        }*/
        table.reload('seckillTableReload'+actId, {
            data:tableData
        });
    }
    //表格数据编辑后封装
    function setValues(rowData) {
        var isExist=false;
        for (var it1 in editPro) {
            if(editPro[it1].productCode==rowData.productCode){
                isExist=true;
                editPro[it1]=rowData;
            }
        }
        if(!isExist){
            var pro=rowData;
            editPro.push(pro);
        }
        for(var it1 in tableDatas) {
            if (tableDatas[it1].actId == rowData.activityId) {
                var tableData=tableDatas[it1].data;
                for (var it2 in tableData) {
                    if (tableData[it2].productCode == rowData.productCode) {
                        tableDatas[it1].data[it2]=rowData;
                    }
                }
            }
        }
        for(var it1 in tempDatas) {
            if (tempDatas[it1].activityId == rowData.activityId) {
                var tableData=tempDatas[it1].slist;
                for (var it2 in tableData) {
                    if (tableData[it2].productCode == rowData.productCode) {
                        tempDatas[it1].slist[it2]=rowData;
                    }
                }
            }
        }
    }
    //动态表格初始化
    function initTables() {
        for(var it in olddatas){
            var actId=olddatas[it].activityId;
            //展示已知数据
            table.render({
                elem: '#seckilltable'+actId
                , cols: [[ //标题栏
                 {field: 'productTitle', title: '商品名称',width:150}
                 ,{field: 'oldPrice', title: '原价',width:100,edit: 'text'}
                 ,{field: 'newPrice', title: '秒杀价',width:100,edit: 'text'}
                 ,{field: 'buyCount', title: '限购数量',width:100,edit: 'text'}
                 ,{field: 'productTotal', title: '商品总量',width:100}
                 ,{field: 'productStock', title: '商品库存',width:100}
                 ,{field: '',title: '操作',width:100,toolbar: '#toolBar'+actId}
                ]]
                , data: olddatas[it].slist
                , id: 'seckillTableReload'+actId
            });
            var dataT={
                actId:actId,
                data:olddatas[it].slist
            }
            tableDatas.push(dataT);
            //监听单元格编辑
            table.on('edit(seckilltable'+actId+')', function(obj){
                var value = obj.value //得到修改后的值
                    ,data = obj.data //得到所在行所有键值
                    ,field = obj.field; //得到字段
                if(field=='oldPrice') {
                    if($.trim(value)==''){
                        layer.msg("原价请输入正确的数字");
                        reloadTable(data.activityId,data.seckillId);
                        return;
                    }
                    if (!(/(^[1-9]\d*$)/.test(value))) {
                        layer.msg("原价请输入正确的数字");
                        reloadTable(data.activityId,data.seckillId);
                        return;
                    }else if(parseInt(value)<data.newPrice){
                        layer.msg("原价必须大于秒杀价");
                        reloadTable(data.activityId,data.seckillId);
                        return;
                    }else{
                        setValues(data);
                    }
                }else if(field=='newPrice'){
                    if($.trim(value)==''){
                        layer.msg("秒杀价请输入正确的数字");
                        reloadTable(data.activityId,data.seckillId);
                        return;
                    }
                    if (!(/(^[1-9]\d*$)/.test(value))) {
                        layer.msg("秒杀价请输入正确的数字");
                        reloadTable(data.activityId,data.seckillId);
                        return;
                    }else if(parseInt(value)>data.oldPrice){
                        layer.msg("秒杀价必须小于原价");
                        reloadTable(data.activityId,data.seckillId);
                        return;
                    }else{
                        setValues(data);
                    }
                }else if(field=='buyCount'){
                    if($.trim(value)==''){
                        layer.msg("限购数量请输入正确的数字");
                        reloadTable(data.activityId,data.seckillId);
                        return;
                    }
                    if (!(/(^[1-9]\d*$)/.test(value))) {
                        layer.msg("限购数量请输入正确的数字");
                        reloadTable(data.activityId,data.seckillId);
                        return;
                    }else if(parseInt(value)>data.productStock){
                        layer.msg("限购数量不能大于总量");
                        reloadTable(data.activityId,data.seckillId);
                        return;
                    }else{
                        setValues(data);
                    }
                }
            });
            //监听工具条
            table.on('tool(seckilltable'+actId+')', function(obj){
                var data = obj.data;
                if(obj.event === 'del'){
                    layer.confirm('确定要删除吗？', function(index){
                        obj.del();
                        delProduct(data);
                        layer.close(index);
                    });
                }
            });
        }
    }
    //删除
    function delProduct(data) {
        var actId=data.activityId;
        var isAddPro=false;
        for (var it in addPro) {
            if(addPro[it].productCode==data.productCode){
                isAddPro=true;
                addPro.splice(it,1);
            }
        }
        //不是页面上新增的，说明是已有的删除
        if(!isAddPro){
            delPro.push(data);
        }
        for(var it in tableDatas){
            if(tableDatas[it].actId==actId){
                var dataT=tableDatas[it].data;
                for(var it1 in dataT){
                    if(dataT[it1].productCode==data.productCode){
                        dataT.splice(it1,1);
                        return;
                    }
                }
            }
        }
    }
});