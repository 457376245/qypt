var commonUtil={};

layui.use(['jquery','layer'],function(){
	/**重写AJAX调用，简化参数设置*/
	$(document).ready(function () {
	    (function($){
	        //首先备份下jquery的ajax方法
	        var _ajax=$.ajax;
	        var showLoadDiaog;

	        //重写jquery的ajax方法
	        $.ajax = function(opt) {
	            //备份opt中error和success方法
	            var fn = {
	                error: function(XMLHttpRequest, textStatus, errorThrown){},
	                success: function(data, textStatus){},
	                complete: function(XMLHttpRequest, textStatus){}
	            }

	            if (opt.error) {
	                fn.error = opt.error;
	            }
	            if (opt.success) {
	                fn.success = opt.success;
	            }
	            if (opt.complete) {
	                fn.complete = opt.complete;
	            }

	            //扩展增强处理
	            var _opt = $.extend(opt, {
	                type: "post",
	                cache: false,
	                contentType: "application/json",
	                timeout: 90000,
	                async: opt.async!=null?opt.async:true,
	                error: function(XMLHttpRequest, textStatus, errorThrown) {
	                    layer.closeAll();
	                    var statusCode = XMLHttpRequest.status;
	                    if (statusCode == "601") {
	                        layer.msg("您的登录已失效,请重新登陆");
	                    } else if (statusCode == "701") {
	                        layer.msg("您没有该功能的操作权限!");
	                    } else if (textStatus == "timeout") {
	                        layer.msg("请求超时，请检查网络情况");
	                    } else {
	                        layer.msg("超时或系统异常,请稍后再试!");
	                    }
	                    fn.error(XMLHttpRequest, textStatus, errorThrown);
	                },
	                success: function(data) {
	                    //成功回调方法增强处理
	                	fn.success(data);
	                },
	                beforeSend: function(XHR) {
	                    var token
	                    try {
	                        token = getToken();
	                    } catch (e) {
	                    }
	                    if (!token) {
	                        token = "";
	                    }
	                    var headers = {
	                        "_al_ec_token": token
	                    };
	                    $.each(headers, function(key, value) {
	                        XHR.setRequestHeader(key, value);
	                    });
	                    
	                    if(opt.isCloseDialog != "N"){//有些场景不需要遮罩
	                        if (opt.tipContent == null) {
	                            showLoadDiaog = layer.load(1, {shade: [0.1,'#fff'], offset: ['40%']});
	                        } else {
	                            showLoadDiaog = layer.load(1, {
	                                content: opt.tipContent,
	                                offset: ['40%'],
	                                shade: [0.1,'#fff'],
	                                success: function(layero) {
	                                    layero.find('.layui-layer-content').css({
	                                        'padding-top': '39px',
	                                        'width': '120px'
	                                    });
	                                }
	                            });
	                        }
	                    }
	                },
	                complete: function(XMLHttpRequest, textStatus) {
	                    if (opt.isCloseDialog != "N") {
	                        layer.close(showLoadDiaog);
	                    }
	                    fn.complete(XMLHttpRequest,textStatus);
	                }
	            });

	            return _ajax(_opt);
	        };
	    })(jQuery);
	});

	/*节流函数，通过控制每次事件执行的时间间隔控制短时间多次执行方法
	handler:要执行的方法
	wait：每次点击事件执行的时间间隔(毫秒)
		var schoolSubmitBut = document.getElementById('schoolSubmitBut');
		schoolSubmitBut.addEventListener('click', commonUtil.throttle(schoolBusi.submit,3000), false);
	*/

	commonUtil.throttle=function(handler, wait) {
	    var lastTime = 0;
	    return function () {
	        var nowTime = new Date().getTime();
	        if (nowTime - lastTime > wait) {
	            handler.apply();
	            lastTime = nowTime;
	        }
	    }
	};

	/**转化字符*/
	commonUtil.clearStrInfo=function(str){
		if(str==null || str=="" || str==undefined || str=="undefined" || str=="null"){
			str="";
		}
		
		return str;
	};

	var loadIndex;//等待浮层

	//弹出等待
	commonUtil.showLoadDialog=function(){
		loadIndex = layer.load(1, {
		    shade: [0.1,'#fff'], //0.1透明度的白色背景
		    offset: ['40%','45%']
		});
	};
	//关闭等待
	commonUtil.closeLoadDialog=function(){
	    layer.close(loadIndex);
	};
	commonUtil.tipMsg=function(message){
	    layer.msg(message, {
	        offset: ['40%'],
	        time: 1600
	    });
	};
	/**展示异常信息*/
	commonUtil.showStatusInfo=function(statusCode){
		if(statusCode=="601"){
			layer.msg("您的登录已失效,请重新登录!");
		}else if(statusCode=="701"){
			layer.msg("您没有该功能的操作权限!");
		}else{
			layer.msg("超时或系统异常,请稍后再试!");
		}
	};
	
	commonUtil.datetimeFormat=function(longTypeDate){
		 var dateTypeDate = "";  
		 var date = new Date();  
		 date.setTime(longTypeDate);  
		 dateTypeDate += date.getFullYear(); //年  
		 dateTypeDate += "-" + getMonth(date); //月  
		 dateTypeDate += "-" + getDay(date); //日  
		 dateTypeDate += " " + getHours(date); //时  
		 dateTypeDate += ":" + getMinutes(date);  //分 
		 dateTypeDate += ":" + getSeconds(date);  //分 
		 return dateTypeDate; 
	};
})
