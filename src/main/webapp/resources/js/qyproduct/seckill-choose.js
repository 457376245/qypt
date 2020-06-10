var choosedPrize={};
var returnPrice={};
function validSumit() {
    if (choosedPrize.rightsCode == null || choosedPrize.rightsCode == undefined) {
        returnPrice.msg="请先选择权益商品";
        return -1;
    }
    if(choosedPrize.remainNum<=0){
        returnPrice.msg="选中作为奖品的权益剩余量必须大于0";
        return -1;
    }
    var productStock =  $.trim($("#productStock").val());
    if(productStock==''){
        returnPrice.msg="商品总量必须填写";
        return -1;
    }
    var buyCount = $.trim($("#buyCount").val());
    if(buyCount==''){
        returnPrice.msg="限购数量必须填写";
        return -1;
    }
    var oldPrice = $.trim($("#oldPrice").val());
    if(oldPrice==''){
        returnPrice.msg="商品原价必须填写";
        return -1;
    }
    var newPrice = $.trim($("#newPrice").val());
    if(newPrice==''){
        returnPrice.msg="商品秒杀价必须填写";
        return -1;
    }
    if ((parseInt(productStock)) <= 0) {
        returnPrice.msg="奖品总数量必须大于0";
        return -1;
    }
    if ((parseInt(productStock)) > choosedPrize.remainNum) {
        returnPrice.msg="商品总量必须小于选中的权益剩余量";
        return -1;
    }
    if ((parseInt(buyCount)) <= 0||(parseInt(buyCount))>(parseInt(productStock))) {
        returnPrice.msg="商品总量必须大于0并且小于商品总量";
        return -1;
    }
    returnPrice.msg='选择奖品成功';
    returnPrice.resultCode=0;
    returnPrice.productStock = productStock;
    returnPrice.productTotal=productStock;
    returnPrice.buyCount = buyCount;
    returnPrice.oldPrice = oldPrice;
    returnPrice.newPrice = newPrice;
    returnPrice.productCode = choosedPrize.rightsCode;
    returnPrice.productTitle = choosedPrize.rightsName;
    returnPrice.prodSupplier = choosedPrize.rightsSpecCd;
    returnPrice.productDesc=choosedPrize.rightsDesc;
    return 0;
}
layui.use(['form','layer','laypage','laydate','table'],function(){
    $(document).ready(function(){
        init();
    });
    var pageConf={
        pageSize:10,
        currentPage:1
    };
    var laytable = layui.table;
    //初始化
    function init(){
        initTable();
        //查询
        $("body").on("click",".search_btn",function(){
            searchList();
        }).resize();
    }
    function initTable() {
        laytable.render({
            elem: '#prizeTable',
            contentType: "application/json",
            method: 'post',
            cellMinWidth: 80,
            page:true,
            limit:pageConf.pageSize,
            cols: [[
                {type:'radio'}
                ,{field:'rightsCode', title: '权益编码',sort: true}
                ,{field: 'rightsName',title: '权益名称'}
                ,{field:'', title: '权益面额'
                    ,templet:function(res){
                        return res.faceValue+res.faceValueUnit;
                    }
                }
                ,{field: 'rightsSpecName',title: '权益分类名称'}
                ,{field: '', title: '权益生失效时间'
                    , templet: function (res) {
                        return res.effDate+'~'+res.expDate;
                    }
                }
                ,{field: 'totalNum',title: '权益总量'}
                ,{field: 'remainNum',title: '权益剩余量'}
            ]]
            ,id: 'prizeReload'
        });
        //单选框选中事件
        laytable.on('radio(layPrize)', function(obj){
            var res=obj.data;
            if(res.remainNum>0){
                $("#productStock").val(res.remainNum);
                choosedPrize=res;
            }else{
                layer.msg("选中作为奖品的权益剩余量必须大于0");
            }
        });
    }
    //列表查询
    function searchList() {
        var params = {
        };
        var rightsReqParam = $.trim($("#rightsReqParam").val());
        if (rightsReqParam != "") {
            params.rightsReqParam = rightsReqParam;
            params.rightsReqParamType = "2";
        }
        //执行重载
        laytable.reload('prizeReload', {
            url:contextPath+"/qyproduct/list",
            page: {
                curr: 1 //重新从第 1 页开始
            }
            ,where: params
        });
    }
});
