layui.use(['form','layer','laypage','laydate','table'],function() {
    var pageConf={
        pageSize:10,
        currentPage:1
    };
    var laytable = layui.table;
    $(document).ready(function(){
        init();
    });
    //初始化
    function init(){
        initTable();
        $("body").on("click", ".actAdd_btn", function () {
            location.href = contextPath + '/otherAct/add';
        });
        //查询
        $("body").on("click", ".search_btn", function () {
            searchList();
        });
    }
    function initTable(){
        laytable.render({
            elem: '#othertable',
            contentType: "application/json",
            url:contextPath+"/otherAct/list",
            method: 'post',
            cellMinWidth: 80,
            page:true,
            loading:true,
            limit:pageConf.pageSize,
            cols: [[
                {field:'activityId', title: '活动ID', width:80, sort: true}
                ,{field: 'activityTitle',title: '活动标题'}
                ,{ field: 'statusCd', title: '状态'
                    ,templet: function (res) {
                        if (res.statusCd == '1') {
                            return "<span class=\"yellow\">进行中</span>";
                        } else if (res.statusCd == '2') {
                            return "<span class=\"black\">未开始</span>";
                        } else if(res.statusCd == '4'){
                            return "<span class=\"gray\">已结束</span>";
                        }
                    }
                }
                ,{field: '',title: '活动时间',width:200
                    ,templet: function (res) {
                        return res.startTimeStr+'~'+res.endTimeStr;
                    }
                }
                ,{field: '',title: '操作',width:300,toolbar: '#toolBar'
                }
            ]]
            ,id: 'othertable'
        });
        laytable.on('tool(othertable)', function(obj){
            var data = obj.data;
            if(obj.event === 'detail'){
                location.href= contextPath + '/otherAct/detail?activityId='+data.activityId;
            } else if(obj.event === 'del'){
                var tipMsg="确认要删除吗?";
                layer.confirm(tipMsg, function(index){
                    delPro(data,status,obj);
                    layer.close(index);
                });
            } else if(obj.event === 'edit'){
                location.href= contextPath + '/otherAct/edit?activityId='+data.activityId;
            }
        });
    }
    function delPro(data,status,obj) {
        var submitData={
            "activityId":data.activityId
        }
        var randTime="?t="+new Date().getTime();
        var index = layer.msg('提交中，请稍候',{icon: 16,time:false,shade:0.8});
        $.ajax({
            type: 'post' ,
            url: contextPath+"/otherAct/deleteAct"+randTime,
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
                    obj.del();
                }else{
                    if(response.data.resultMsg!=null&&response.data.resultMsg!=undefined){
                        layer.msg(response.data.resultMsg);
                    }else{
                        layer.msg("活动删除失败");
                    }
                }
            },
            error:function(response){
                layer.close(index);
                layer.msg("活动删除/下架失败");
            }
        });
    }
    //列表查询
    function searchList(){
        var params={
            "statusCd":null,
            "activityTitle":null
        };
        var actTitle=$.trim($("#actTitle").val());
        var statusCd=$("#statusCd").val();
        if(actTitle!=""){
            params.activityTitle=actTitle;
        }
        if(statusCd!="0"){
            params.statusCd=statusCd;
        }
        //执行重载
        laytable.reload('othertable', {
            page: {
                curr: 1 //重新从第 1 页开始
            }
            ,where: params
        });
    }
});