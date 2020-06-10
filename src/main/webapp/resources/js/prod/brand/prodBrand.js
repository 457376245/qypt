var prodBrandList={};
layui.use(['form','layer','laydate','element','table'],function() {
    var pageConf={
        pageSize:10,
        currentPage:1
    };
    var laytable = layui.table;
    var laydate = layui.laydate;
    var prodBrandTable;
    //绑定查询按钮点击事件
    $("#searchInfoBtn").click(function(){
        var brandCode_=$("#brandCode").val();
        var brandTitle_=$("#brandTitle").val();
        var statusCd_=$("#statusCd").val();
        if("" !== brandCode_){
            pageConf.brandCode = brandCode_;
        }else{
            pageConf.brandCode=null;
        }
        if("" !== brandTitle_){
            pageConf.brandTitle = brandTitle_;
        }else{
            pageConf.brandTitle=null;
        }
        if("" !== statusCd_) {
            pageConf.statusCd = statusCd_;
        }else{
            pageConf.statusCd = null;
        }
        //重新加载表格
        prodBrandTable.reload({
            where:pageConf,
            page:{
                curr:1
            }
        })
    });
    $(document).ready(function(){
        prodBrandList.init();
    });
    //初始化
    prodBrandList.init=function(){
        prodBrandList.initTable();

        // //时间范围
        // var ins1=laydate.render({
        //     elem: '#times'
        //     ,range: '~'
        //     ,trigger: 'click'
        // });

    };


    prodBrandList.getCondition=function(params){
        var brandCode_=$("#brandCode").val();
        var brandTitle=$.trim($("#brandTitle").val());
        var statusCd=$("#statusCd").val();
        if(brandCode_!=""){
            params.brandCode=brandCode_;
        }else{
            params.brandCode=null;
        }
        if(brandTitle!=""){
            params.brandTitle=brandTitle;
        }else{
            params.brandTitle=null;
        }
        if(statusCd!=""){
            params.statusCd=statusCd;
        }else{
            params.statusCd=null;
        }
        var times=$("#times").val();
        if(times!=''){
            params.qryStartTime=$.trim(times.split("~")[0]);
            params.qryEndTime=$.trim(times.split("~")[1]);
        }else{
            params.qryStartTime=null;
            params.qryEndTime=null;
        }
    };

    //列表查询
    prodBrandList.initTable=function(){
        prodBrandTable=laytable.render({
            id:"prodBrandListTable",
            elem: '#prodBrandListTable',
            contentType: "application/json",
            url: contextPath + "/prodBrand/brandList",
            method: 'post',
            cellMinWidth: 80,
            page: true,
            limit: pageConf.pageSize,
            cols: [[
                {field: 'brandCode', title: '商品编码'},
                {
                    field: 'brandTitle', title: '商品名称'
                },
                {field: 'sortSeq', title: '排序'},
                {
                    field: 'statusCd', title: '状态'
                    , width: 120, templet: function (res) {
                        if (res.statusCd == '101') {
                            return "<span class=\"yellow\">可用</span>";
                        } else if (res.statusCd == '102') {
                            return "<span class=\"black\">不可用</span>";
                        }
                    }
                },
                {field: 'opertionBtn', title: '操作',width:'12%',templet: function(info){
                        var btnsHtml="";
                        var statusCd=info.statusCd;
                        btnsHtml+="<a class=\"layui-btn layui-btn-mini layui-btn-xs\" href=\"javascript:void(0);\" onclick=\"prodBrandList.toEditProdBrand('EDIT','"+info.brandCode+"');\">详情</a>";
                        if (statusCd=="102") {
                            btnsHtml+="<a class=\"layui-btn  layui-btn-xs\" href=\"javascript:void(0);\" onclick=\"prodBrandList.editBrandStatus('101','"+info.brandCode+"');\">启用</a> ";
                        }else if(statusCd=="101"){
                            btnsHtml+="<a class=\"layui-btn  layui-btn-xs\" href=\"javascript:void(0);\" onclick=\"prodBrandList.editBrandStatus('102','"+info.brandCode+"');\">停用</a> ";
                        }
                        // btnsHtml+="<a class=\"layui-btn  layui-btn-xs\" href=\"javascript:void(0);\" onclick=\"prodBrandList.editBrandStatus('103','"+info.brandCode+"');\">删除 </a> ";

                        return btnsHtml;
                    }},

            ]]
            , id: 'turntableReload'
        });

    };
    /**新增或修改任务信息*/
    prodBrandList.toEditProdBrand=function(editType,brandCode){
        var title="";
        var url=contextPath+"/prodBrand/toEditProdBrand?editType="+editType;

        if(editType=="ADD"){
            title="添加任务";
        }else{
            title="修改任务";
            url+="&brandCode="+brandCode
        }

        var editBrandDialog = layer.open({
            type:2,
            title:title,
            content:url,
            area: ['660px', '450px']
        });

        //layui.layer.full(editGroupDialog);
    };
    /**重载表格*/
    prodBrandList.tableReload=function(){
        layer.closeAll();
        prodBrandTable.reload();
    }


    /**停用，启用，删除*/
    prodBrandList.editBrandStatus=function(statusCd,brandCode){
        var msg="";

        if(statusCd=="102"){
            msg="您确定要停用该分类吗？";
        }else if(statusCd=="101"){
            msg="您确定要启用该分类吗？";
        }else{
            msg="您确定要删除该分类吗？";
        }

        layer.confirm(msg, {
            btn: ['确定','取消'] //按钮
        }, function(){
            layer.closeAll();

            var params={
                brandCode:brandCode,
                oldBrandCode:brandCode,
                editType:'EDIT',
                statusCd:statusCd
            }

            $.ajax({
                type: "post",
                contentType: "application/json",
                data: JSON.stringify(params),
                url :contextPath+"/prodBrand/saveProdBrand",
                success : function(response){
                    if(response.successed){
                        layer.msg("操作成功");
                        setTimeout("prodBrandList.tableReload()",1200);
                    }else{
                        layer.msg(response.data.resultMsg);
                    }
                }
            });
        }, function(){
            layer.closeAll();
        });
    }


});