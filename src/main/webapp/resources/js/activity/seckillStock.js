layui.use(['form','layer','laypage','table'],function(){
    var table = layui.table;
    var editPro=[];
    var tableDatas=[];
    var form = layui.form;
    form.on('submit(saveAct)', function(data){
        return toSubmit('saveAct');
    });
    $(document).ready(function(){
        initTables();
    });
    function toSubmit(flag) {
        if(flag=='saveAct'){
            if(editPro.length==0){
                layer.msg("未做任何操作");
                return false;
            }
        }
        var params={
            editPro:editPro,
            parentId:$("#contentDiv").attr("data-id")
        }
        var randTime="?t="+new Date().getTime();
        var index = layer.msg('提交中，请稍候',{icon: 16,time:false,shade:0.8});
        $.ajax({
            type: 'post' ,
            url: contextPath+"/seckill/saveActProStock"+randTime,
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
                    layer.msg("修改库存保存成功");
                    editPro=[];
                    return true;
                }else{
                    if(response.data.resultMsg!=null&&response.data.resultMsg!=undefined){
                        layer.msg(response.data.resultMsg);
                    }else{
                        layer.msg("修改库存保存失败");
                    }
                }
            },
            error:function(response){
                layer.close(index);
                layer.msg("修改库存保存失败");
            }
        });
        return false;
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
                table.reload('seckillTableReload'+rowData.activityId, {
                    data: tableDatas[it1].data
                });
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
                    ,{field: 'oldPrice', title: '原价',width:100}
                    ,{field: 'newPrice', title: '秒杀价',width:100}
                    ,{field: 'buyCount', title: '限购数量',width:100}
                    ,{field: 'productTotal', title: '商品总量',width:100}
                    ,{field: 'productStock', title: '商品库存',width:100,edit: 'text'}
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
                if(field=='productStock') {
                    if($.trim(value)==''){
                        layer.msg("商品库存请输入正确的数字");
                        reloadTable(data.activityId,data.seckillId);
                        return;
                    }
                    if (!(/(^[1-9]\d*$)/.test(value))) {
                        layer.msg("商品库存请输入大于0的数字");
                        reloadTable(data.activityId,data.seckillId);
                        return;
                    }
                    var editSeckillPro={};
                    var resultTemp=JSON.parse(resultData).data;
                    for(var it in resultTemp) {
                        var id = resultTemp[it].activityId;
                        if (id == data.activityId) {
                            var seckillTemps = resultTemp[it].slist;
                            for (var it1 in seckillTemps) {
                                if (seckillTemps[it1].seckillId == data.seckillId) {
                                    editSeckillPro=seckillTemps[it1];
                                }
                            }
                        }
                    }
                    if(editSeckillPro.seckillId==undefined){
                        layer.msg("修改库存的商品不存在");
                        reloadTable(data.activityId,data.seckillId);
                        return;
                    }
                    if(parseInt(editSeckillPro.productStock)==0) {
                        if(parseInt(value)<=0){
                            layer.msg("商品库存请输入大于0的数字");
                            reloadTable(data.activityId,data.seckillId);
                            return;
                        }else{
                            data.productTotal=parseInt(editSeckillPro.productTotal)+parseInt(value);
                            setValues(data);
                        }
                    }else{
                        layer.msg("库存为0时才能追加");
                        reloadTable(data.activityId,data.seckillId);
                        return;
                    }
                }
            });
        }
    }
});