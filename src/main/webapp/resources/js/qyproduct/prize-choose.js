var choosedPrize={};
var returnPrice={};
function validSumit() {
    var prizeType = $("#prizeType").val();
    if (prizeType != "1"&&prizeType != "5") {
        if (choosedPrize.rightsCode == null || choosedPrize.rightsCode == undefined) {
            returnPrice.msg="请先选择权益商品";
            return -1;
        }
        if(choosedPrize.remainNum<=0){
            returnPrice.msg="选中作为奖品的权益剩余量必须大于0";
            return -1;
        }
    }
    var prizeUse = $("#prizeUse").val();
    if (prizeUse.length > 2000) {
        returnPrice.msg="奖品使用方式不得超过2000字符";
        return -1;
    }
    var prizeCount = $.trim($("#prizeCount").val());
    if(prizeType!="5"){
        if(prizeCount==''){
            returnPrice.msg="奖品数量必须填写";
            return -1;
        }
        if ((parseInt(prizeCount)) <= 0) {
            returnPrice.msg="奖品总数量必须大于0";
            return -1;
        }
        if (prizeType != "1") {
            if ((parseInt(prizeCount)) > choosedPrize.remainNum) {
                returnPrice.msg="奖品总数量必须小于选中的权益剩余量";
                return -1;
            }
        }
    }
    var usePrizeCount= $.trim($("#usePrizeCount").val());
    if(usePrizeCount==''){
        returnPrice.msg="奖励数量必须填写";
        return -1;
    }
    if ((parseInt(usePrizeCount)) <= 0||(parseInt(usePrizeCount))>(parseInt(prizeCount))) {
        returnPrice.msg="奖励数量必须大于0并且小于奖品总数量";
        return -1;
    }
    returnPrice.msg='选择奖品成功';
    returnPrice.resultCode=0;
    returnPrice.prizeUse = prizeUse;
    returnPrice.prizeCount = prizeCount;
    returnPrice.prizeStock = prizeCount;
    returnPrice.prizeType = prizeType;
    returnPrice.usePrizeCount=usePrizeCount;
    if (prizeType != "1"&&prizeType != "5") {
        returnPrice.productCode = choosedPrize.rightsCode;
        returnPrice.prizeTitle = choosedPrize.rightsName;
        returnPrice.prodSupplier = choosedPrize.rightsSpecCd;
        returnPrice.prizeVal = choosedPrize.faceValue + choosedPrize.faceValueUnit;
        returnPrice.prizeDesc = choosedPrize.rightsDesc;
    } else {
        if(prizeType == "1"){
            returnPrice.productCode = 'YD';
            returnPrice.prizeTitle = '翼豆';
            returnPrice.prodSupplier = '11';
            returnPrice.prizeVal = '';
            returnPrice.prizeDesc = '翼豆';
        }else if(prizeType == "5"){
            returnPrice.productCode = 'CS';
            returnPrice.prizeTitle = '抽奖次数';
            returnPrice.prodSupplier = '11';
            returnPrice.prizeVal = '';
            returnPrice.prizeDesc = '抽奖次数';
            returnPrice.prizeUse = '';
            returnPrice.prizeCount = '999999999';
            returnPrice.prizeStock = '999999999';
        }
    }
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
    var form = layui.form;
    //初始化
    function init(){
        initTable();
        //查询
        $("body").on("click",".search_btn",function(){
            searchList();
        }).resize();
        form.on('select(prizeTypeChg)', function(data){
            var p1=$("#prizeType").val();
            if(p1!="1"&&p1!="5"){
                $('#qyproductTable').show();
            }else{
                $('#qyproductTable').hide();
            }
            if(p1!="5"){
                $('#allCountDiv').show();
                $('#useDiv').show();
            }else{
                $('#allCountDiv').hide();
                $('#useDiv').hide();
            }
        });
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
                $("#prizeCount").val(res.remainNum);
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
