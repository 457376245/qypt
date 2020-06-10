/***
 * 未修改时的文件
 */
//tools
var tools = {
    isPC: function() {
            var userAgentInfo = navigator.userAgent;
            var Agents = ["Android", "iPhone",
                "SymbianOS", "Windows Phone",
                "iPad", "iPod"
            ];
            var flag = true;
            for (var v = 0; v < Agents.length; v++) {
                if (userAgentInfo.indexOf(Agents[v]) > 0) {
                    flag = false;
                    break;
                }
            }
            return flag;
        },
        eventName: function() {
            var eventName = tools.isPC() ? "click" : "touchstart";
            return eventName;
        },
        stateBox:function(title,fn){
            var dom = $('<div class="sateBoxCon">'+title+'</div>');
            dom.appendTo($('body'));
            dom.css({
                marginLeft:-dom.outerWidth()/2+"px",
                marginTop:-dom.outerHeight()/2+"px"
            });
            setTimeout(function(){
                $(".sateBoxCon").remove();
                fn && fn();
            },2600);
        },//倒计时效果
        countDown:function(o,fn){
            var dom = o.dom,s = o.time,temp=o.time;
            $(dom).addClass("my-disabled").html(temp+"<span> 秒</span>").attr("disabled","true");
            var inter = setInterval(function(){
                if(temp>1){
                    temp--;
                    $(dom).addClass("my-disabled").html(temp +"<span> 秒</span>").attr("disabled","true");
                }else{
                	$(dom).removeAttr("disabled");
                    $(dom).removeClass("my-disabled").html(commonPageInfos.resent);
                    fn && fn();
                    clearInterval(inter);
                }
            },1000);
        }

};

// selectbox
(function($) {
    var selectboxCounter = 0;

    $.fn.sbCustomSelect = function(options) {
        var iOS = (navigator.userAgent.match(/iPhone/i)) || (navigator.userAgent.match(/iPod/i)) || (navigator.userAgent.match(/iPad/i)),
            android = (navigator.userAgent.match(/Android/i)),
            UP = 38,
            DOWN = 40,
            SPACE = 32,
            RETURN = 13,
            TAB = 9,
            matchString = '',
            settings = $.extend({
                appendTo: false
            }, options);

        // Sync custom display with original select box and set selected class and the correct <li>
        var updateSelect = function() {
            var $this = $(this),
                $dropdown = $('.sb-dropdown[data-id=' + $this.parent().data('id') + ']'),
                $sbSelect = $this.siblings('.sb-select');

            if (this.selectedIndex != -1) {
                var _this = this;
                $sbSelect.val(this[this.selectedIndex].innerHTML);

                $dropdown.children().removeClass('selected').filter(function() {
                    return $(this).data('value') == _this[_this.selectedIndex].value;
                }).addClass('selected');
            }
        };

        // Update original select box, hide <ul>, and fire change event to keep everything in sync
        var dropdownSelection = function(e) {
            var $target = $(e.target),
                id = $target.closest('ul').attr('data-id'),
                $option = $('.sb-custom[data-id=' + id + ']').find('option').filter('[value="' + $target.parent().data('value') + '"]');

            e.preventDefault();

            $option[0].selected = true;
            $target.closest('ul').fadeOut('fast');
            $option.parent().trigger('change');
        };

        // Create the <ul> that will be used to change the selection on a non iOS/Android browser
        var createDropdown = function($select, i) {
            var $options = $select.children(),
                $dropdown = $('<ul data-id="' + i + '" class="sb-dropdown"/>');

            $options.each(function() {
                $this = $(this);
                $dropdown.append('<li data-value="' + $this.val() + '"><a href=".">' + $this.text() + '</a></li>');
            });
            $dropdown.bind('click', dropdownSelection);

            return $dropdown;
        };

        // Clear keystroke matching string and show dropdown
        var viewList = function(e) {
            var $this = $(this),
                id = $this.data('id');
            clearKeyStrokes();

            $('.sb-dropdown').filter('[data-id!=' + id + ']').fadeOut('fast');
            $('.sb-dropdown').filter('[data-id=' + id + ']').fadeIn('fast');

            e.preventDefault();
        };

        // Hide the custom dropdown
        var hideDropdown = function(e) {
            if (!$(e.target).closest('.sb-custom').length) {
                $('.sb-dropdown').fadeOut('fast');
            }
        };

        // Manage keypress to replicate browser functionality
        var selectKeypress = function(e) {
            var $this = $(this),
                $current = $('.sb-dropdown[data-id=' + $this.data('id') + ']').find('.selected');


            // if ($('.sb-dropdown[data-id=' + $this.data('id') + ']').find('.selected') || $('.sb-dropdown[data-id=' + $this.data('id') + ']');
            // $this.siblings('ul').find('.selected');

            if ((e.keyCode == UP || e.keyCode == DOWN || e.keyCode == SPACE) && $current.is(':hidden')) {
                $current.focus();
                return;
            }

            if (e.keyCode == UP && $current.prev().length) {
                e.preventDefault();
                $current.removeClass('selected');
                $current.prev().addClass('selected');
            } else if (e.keyCode == DOWN && $current.next().length) {
                e.preventDefault();
                $current.removeClass('selected');
                $current.next().addClass('selected');
            }

            if (e.keyCode == RETURN || e.keyCode == SPACE) {
                $current.trigger('click');
                return;
            }

            if (e.keyCode >= 48 && e.keyCode <= 90) {
                matchString += String.fromCharCode(e.keyCode);
                checkforMatch(e);
            }

            if (e.keyCode == TAB && $current.is(':visible')) {
                e.preventDefault();
                $current.trigger('click');
                hideDropdown(e);
            }
        };

        // Check keys pressed to see if there is a text match with one of the options
        var checkforMatch = function(e) {

            var re = '/' + matchString + '.*/';

            $(e.target).siblings('ul').find('a').each(function() {
                if (this.innerHTML.toUpperCase().indexOf(matchString) === 0) {
                    $(this).trigger('click');
                    return;
                }
            });
        };

        // Clear the string used for matching keystrokes to select options
        var clearKeyStrokes = function() {
            matchString = '';
        };



        /* jQuery Plugin Loop
         *
         * Take the select box out of the tab order.
         *
         * Add the field that will show the currently selected item and attach the change event to update the .sb-select input.
         *
         * If this is iOS or Android then we want to use the browsers standard UI controls. Set the opacity of the select to 0
         * and lay it over our custom display of the current value.
         * Otherwise, we're going to create a custom <ul> for the dropdown
         *
         * After all of the setup is complete, trigger the change event on the original select box to update the .sb-select input
         */
        this.each(function(index) {
            var $self = $(this);

            $self.attr('tabindex', -1)
                .wrap('<div data-id="' + selectboxCounter + '" class="sb-custom ' + ($self.data('class') || '') + '"/>')
                .after('<input data-id="' + selectboxCounter + '" type="text" class="sb-select" readonly="readonly" />')
                .bind('change', updateSelect);


            if (iOS || android) {
                $self.show().css({
                    'display': 'block',
                    'height': $self.next().innerHeight(),
                    'opacity': 0,
                    'position': 'absolute',
                    'width': '100%',
                    'z-index': 1000
                });
            } else {

                $self.next().bind('click', viewList);



                if (!settings.appendTo) {
                    $self.after(createDropdown($self, selectboxCounter));
                } else {
                    var offset = $self.parent().offset();

                    $self.next().on('click', function(){
                        $('.sb-dropdown').eq(index).css({
                            'top': $self.parent().outerHeight() + $self.parent().offset().top,
                            'left': $self.parent().offset().left
                        })
                        // $(settings.appendTo).append(createDropdown($self, selectboxCounter).css({
                        //     'top': $self.parent().offset().top,
                        //     'left': $self.parent().offset().left,
                        //     'width': $self.parent().outerWidth()
                        // }));
                    })

                    $(settings.appendTo).append(createDropdown($self, selectboxCounter).css({
                        'top': offset.top + $self.parent().outerHeight(),
                        'left': offset.left,
                        'width': $self.parent().outerWidth()
                    }));
                }
            }

            $self.trigger('change');
            selectboxCounter++;
        });

        // Hide dropdown when click is outside of the input or dropdown
        $(document).bind('click', hideDropdown);

        $('.sb-custom').find('.sb-select').on('keydown', selectKeypress);
        $('.sb-custom').bind('blur', clearKeyStrokes);
        $(document).on('scroll', function(){
            $('.sb-dropdown').hide();
        })
        $(document).delegate('.sb-dropdown', 'focus', viewList);

        return this;
    };
})(jQuery);

// radio checkbox
(function($) {
    $.fn.radioCheckbox = function(options) {
        if (!this[0]) {
            return;
        }

        var checkedSuffix = '-checked';

        var hiddenInputClass = 'radio-checkbox-hidden';

        var forceChange = function() {
            var $this = $(this);
            if (!$this.closest('label')[0]) {
                $this.prev().trigger('change.crc', [true]);
            }
        };

        var insertFakeInput = function(inputs) {
            var type = inputs.type;
            var l = inputs.length;
            var fakeInputElem, input;

            while (l--) {
                input = inputs[l];

                fakeInputElem = $('<i>')
                    .addClass('common-icon-' + type + (input.checked ? ' common-icon-' + type + checkedSuffix : ''))
                    .bind('click.crc', forceChange);

                input.parentNode.insertBefore(fakeInputElem[0], input.nextSibling);
            }
        };

        return this.each(function() {

            var $context = $(this);

            var rds = $context.find('.radio-box input[type=radio]:not(.' + hiddenInputClass + ')')
                .addClass(hiddenInputClass);

            var chs = $context.find('.check-box input[type=checkbox]:not(.' + hiddenInputClass + ')')
                .addClass(hiddenInputClass);

            var rdsCache = {};

            var rdsLength = rds.length;
            var rd;

            if (rds.length) {
                rds.type = 'radio';

                insertFakeInput(rds);

                while (rdsLength--) {
                    rd = rds[rdsLength];
                    if ( $(rd).attr('disabled') ) {
                        $(rd).parent().addClass('common-icon-radio-disabled');
                    }
                    if (rd.checked) {
                        (rdsCache[rd.name] = {}).checked = $(rd.nextSibling);
                    }
                }
                rds.bind('change.crc', function(e, force) {
                    if ($(this).attr('disabled')) return;
                    if (!force || !this.checked) {

                        if (!rdsCache[this.name]) {
                            rdsCache[this.name] = {};
                        }

                        if (rdsCache[this.name].checked) {
                            rdsCache[this.name].checked.removeClass('common-icon-' + rds.type + checkedSuffix);
                        }

                        rdsCache[this.name].checked = $(this.nextSibling).addClass('common-icon-' + rds.type + checkedSuffix);
                    }
                    if (force && !this.checked) {
                        this.checked = true;
                    }
                });
            }

            if (chs.length) {
                chs.type = 'checkbox';

                insertFakeInput(chs);

                chs.bind('change.crc', function(e, force) {
                    if (force) {
                        this.checked = !this.checked;
                    }

                    $(this.nextSibling).toggleClass('common-icon-' + chs.type + checkedSuffix);
                });
            }
        });
    };
}(jQuery));

// popWin
var moogo = moogo || {};
(function(BS) {
    BS.popWin = function(option) {
        var self = this;
        this.settings = $.extend({
            width: 500,
            height: 300,
            isFixed: true
        }, option || {});
        this.container = option.container;

        //获取弹出框类型
        var dialogType=option.dialogType;

        this.scrollTop = 0;
        this.wall = $('<div class="popWall"></div>').appendTo(this.container);

        if(dialogType!=null && dialogType!="" && dialogType!=undefined && dialogType!="null" && dialogType=="login"){
        	//判断是否登录弹出框
        	this.dom = $('<div class="popDom" ><div class="titleDom" style="padding: 0 0;"><span class="titleCon">' + this.settings.title + '</span><span class="close"></span></div><div class="bodyDom"></div></div>').appendTo(this.container);
        }else{
        	this.dom = $('<div class="popDom" ><div class="titleDom"><span class="titleCon">' + this.settings.title + '</span><span class="close"></span></div><div class="bodyDom"></div></div>').appendTo(this.container);
        }

        this.dom.css({
            width: this.settings.width + "px",
            height: this.settings.height + "px",
            "left": self.dom.offset().left - (this.settings.width / 2) + "px",
            "top": self.dom.offset().top - document.body.scrollTop - (this.settings.height / 2) + "px"
        });
        this.title = this.dom.find(".titleDom");
        this.title = this.dom.find(".titleDom");
        this.body = this.dom.find(".bodyDom").append(self.settings.content);
        this.closeDom = this.dom.find(".close");
        this.closeDom.click(function() {
            self.close();
        });
        this.close();
        this.title.mousedown(function(e) {
            var temp = self.mousePos(e);
            self.settings.x = temp.x;
            self.settings.y = temp.y;
            self.settings.left = self.dom.offset().left,
            self.settings.top = self.dom.offset().top,
            bindEvent();
        });
        this.title.mouseup(function() {
            unbindEvent();
        });
        if (!self.settings.isFixed) {
            $(self.container).css({
                "position": "absolute"
            });
            $(self.dom).css({
                "position": "absolute"
            });
            $(self.wall).css({
                "position": "absolute"
            });
        }
        $("body").mouseup(function() {
            unbindEvent();
        });

        function bindEvent() {
            self.title.bind("mousemove", move);
            $("body").bind("mousemove", move);
        }

        function unbindEvent() {
            self.title.unbind("mousemove", move);
            $("body").unbind("mousemove", move);
        }

        function move(e) {
            var tx = self.settings.x - e.clientX,
                ty = self.settings.y - e.clientY,
                left = self.settings.left + (-tx);
            t = self.settings.top + (-ty);
            if (left > 0 && left < $(window).width() - self.settings.width) {
                self.dom.css({
                    "left": left + "px"
                });
            }
            if (t > 0 && t < $(window).height() - self.settings.height) {
                self.dom.css({
                    "top": t + "px"
                });
            }
        }
        //点击空白地方关闭弹窗
        self.wall.on("click",function(){
            self.close();
        });

        $(self.dom).on("DOMMouseScroll mousewheel",function(e){
            e.stopPropagation();
        });
    };
    BS.popWin.prototype = {
        close: function() {
        	var $body = $("body");
            var self = this;
            self.container.hide();
            self.settings.closeCallback.call(self);
           // $('html').css({height:'auto',overflow:'auto'})
            //$('body').css({height:'auto',overflow:'hidden'})
            //$("body").removeClass("modal-open");
            if (!self.settings.isFixed) {
                $("body").animate({
                    scrollTop: self.scrollTop + "px"
                }, 500);
            }

            $body.css("overflow-x",this._bodyOverflow.x);
            $body.css("overflow-y",this._bodyOverflow.y);
            this._bodyOverflow = {};
        },
        _bodyOverflow:{
        },
        remove: function() {
        	var $body = $("body");
            var self = this;
            self.container.remove();
            //$('html').css({height:'auto',overflow:'auto'})
           // $('body').css({height:'auto',overflow:'hidden'})
            //$("body").removeClass("modal-open");
            $body.css("overflow-x",this._bodyOverflow.x);
            $body.css("overflow-y",this._bodyOverflow.y);
            this._bodyOverflow = {};
        },
        show: function() {
            var self = this;
            // recalc window size on resizing:
            $(window).resize(function() {
                self.setMaskHeight();
            });
            var $body = $("body");
            this._bodyOverflow = {
                x:$body.css("overflow-x"),
                y:$body.css("overflow-y")
            };
            $body.css("overflow","hidden");
           // $('html,body').css({height:'100%',overflow:'hidden'})
            self.setMaskHeight();
            self.settings.content.show();
            self.container.show();
            //$("body").addClass("modal-open");
            if (!self.settings.isFixed) {
                self.scrollTop = $("body").scrollTop();
                $("body").animate({
                    scrollTop: 0
                }, 500);
            }
            var _timer = setTimeout(function(){
               if(self.settings.showCallback) self.settings.showCallback.call(self);
            },100)
        },
        setContent: function(o) {
            var self = this;
            self.body.html(o);
        },
        setMaskHeight: function() {
            try {
                var self = this;
                // get height of page for the mask:
                var body = document.body,
                    html = document.documentElement;
                //$(".popWall").height(Math.max(body.scrollHeight, body.offsetHeight, html.clientHeight, html.scrollHeight, html.offsetHeight) + "px");
                var popDom = self.settings.content.parents(".popDom");

                var scrollTop=body.scrollTop;
                var screenHeight=window.screen.height;
                var thisSettingHeight=this.settings.height;
                var screenAvailHeight=window.screen.availHeight;
               var top = (html.clientHeight - popDom.height()) / 2;
               // var top=(screenHeight-thisSettingHeight)/2-40;
                var top=(screenAvailHeight-popDom.height())/2
                if(top<0){
                	top=10;
                }else if(top>40){
                	top=top-40;
                }
                var left = (html.clientWidth - popDom.width()) / 2;
                if (left < 20) {
                    left = 20;
                }
                if (top < 20) {
                    top = 20;
                }
                popDom.css({
                    left: left + "px",
                    top: top + "px"
                });
            } catch (e) {}
        },

        mousePos: function(e) {
            var x, y;
            var e = e || window.event;
            return {
                x: e.clientX + document.body.scrollLeft + document.documentElement.scrollLeft,
                y: e.clientY + document.body.scrollTop + document.documentElement.scrollTop
            }
        }
    };

})(moogo);


(function($) {
    $.extend({
        ms_DatePicker: function(options) {
                var defaults = {
                    YearSelector: "#sel_year",
                    MonthSelector: "#sel_month",
                    DaySelector: "#sel_day",
                    FirstText: "--",
                    FirstValue: 0
                };
                var opts = $.extend({}, defaults, options);
                var $YearSelector = $(opts.YearSelector);
                var $MonthSelector = $(opts.MonthSelector);
                var $DaySelector = $(opts.DaySelector);
                var FirstText = opts.FirstText;
                var FirstValue = opts.FirstValue;

                // 初始化
                var str = "<option value=\"" + FirstValue + "\">" + FirstText + "</option>";
                $YearSelector.html(str);
                $MonthSelector.html(str);
                $DaySelector.html(str);

                // 年份列表
                var yearNow = new Date().getFullYear();
                var yearSel = $YearSelector.attr("rel");
                for (var i = yearNow; i >= 1900; i--) {
                    var sed = yearSel == i ? "selected" : "";
                    var yearStr = "<option value=\"" + i + "\" " + sed + ">" + i + "</option>";
                    $YearSelector.append(yearStr);
                }

                // 月份列表
                var monthSel = $MonthSelector.attr("rel");
                for (var i = 1; i <= 12; i++) {
                    var sed = monthSel == i ? "selected" : "";
                    var monthStr = "<option value=\"" + i + "\" " + sed + ">" + i + "</option>";
                    $MonthSelector.append(monthStr);
                }

                // 日列表(仅当选择了年月)
                function BuildDay() {
                    if ($YearSelector.val() == 0 || $MonthSelector.val() == 0) {
                        // 未选择年份或者月份
                        $DaySelector.html(str);
                    } else {
                        $DaySelector.html(str);
                        var year = parseInt($YearSelector.val());
                        var month = parseInt($MonthSelector.val());
                        var dayCount = 0;
                        switch (month) {
                            case 1:
                            case 3:
                            case 5:
                            case 7:
                            case 8:
                            case 10:
                            case 12:
                                dayCount = 31;
                                break;
                            case 4:
                            case 6:
                            case 9:
                            case 11:
                                dayCount = 30;
                                break;
                            case 2:
                                dayCount = 28;
                                if ((year % 4 == 0) && (year % 100 != 0) || (year % 400 == 0)) {
                                    dayCount = 29;
                                }
                                break;
                            default:
                                break;
                        }

                        var daySel = $DaySelector.attr("rel");
                        for (var i = 1; i <= dayCount; i++) {
                            var sed = daySel == i ? "selected" : "";
                            var dayStr = "<option value=\"" + i + "\" " + sed + ">" + i + "</option>";
                            $DaySelector.append(dayStr);
                        }
                    }
                }
                $MonthSelector.change(function() {
                    BuildDay();
                });
                $YearSelector.change(function() {
                    BuildDay();
                    $('#JSelectDay').sbCustomSelect({
                        appendTo: 'body'
                    });
                });
                if ($DaySelector.attr("rel") != "") {
                    BuildDay();
                }
            } // End ms_DatePicker
    });
})(jQuery);

// 日期联动
var calendar = (function() {

    function changeDays(yearId, monthId, dayId) {
        var $year = $('#' + yearId),
            $month = $('#' + monthId),
            $day = $('#' + dayId),
            _maxDays = $day.get(0).value,
            _defaultDayValue = $day.find('option:first').val(),
            _defaultDayText = $day.find('option:first').text();

        $year.on('change', function() {
            _maxDays = _getDaysNum(yearId, monthId);
            if ($day.val() > _maxDays) {
                $day.next().val(_defaultDayText);
                $day.next().text(_defaultDayText);
                $day.get(0).selectedIndex = 0;
            }
            updateUl();
        })
        $month.on('change', function() {
            _maxDays = _getDaysNum(yearId, monthId);
            if ($day.val() > _maxDays) {
                $day.next().val(_defaultDayValue);
                $day.next().text(_defaultDayText);
                $day.get(0).selectedIndex = 0;
            }
            updateUl();
        })
        $day.on('change', function() {})

        function updateUl() {
            var _id = $day.next().data('id'),
                $ul = $('ul.sb-dropdown[data-id="' + _id + '"]'),
                $options = $day.children()
            _option_html = '';

            $options.each(function(index) {
                $this = $(this);
                if (index + 1 <= _maxDays) {
                    _option_html += '<li data-value="' + $this.val() + '"><a href=".">' + $this.text() + '</a></li>';
                }
            });
            $ul.html(_option_html);
        }
    }

    function _getDaysNum(year, month) {
        var year = typeof year === 'number' ? year : parseInt($('#' + year).val());
        var month = parseInt($('#' + month).val());
        var dayCount = 0;
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                dayCount = 31;
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                dayCount = 30;
                break;
            case 2:
                dayCount = 28;
                if ((year % 4 == 0) && (year % 100 != 0) || (year % 400 == 0)) {
                    dayCount = 29;
                }
                break;
            default:
                break;
        }
        return dayCount;
    }
    return {
        changeDays: changeDays
    };
}());

(function($) {
    // 初始化代码
    $(function() {
        $('#JTopdDropdrow,#UserDropdrow').hover(function() {
            $(this).addClass('current');
        }, function() {
            $(this).removeClass('current');
        });
        $('body').radioCheckbox();
        $('select').sbCustomSelect({
            appendTo: 'body'
        });

        //头部购物袋
        var t;
        $("#my-shopping-bag").hover(function() {
            window.clearTimeout(t);
            $(this).addClass('active');

            //调用购物车展示JS
            cartProduct.showCartProInfo();
        }, function() {
            var $this = $(this);
            t = window.setTimeout(function() {
                $this.removeClass('active');
            }, 100);
        });
        //头部二级菜单
        $('.main-nav > a').hover(function(){
            var _index = $(this).index();
            if ( !$('.dropdown-menu[data-index="'+_index+'"]').length ) return;
            $(this).addClass('hover');
            $('.dropdown-menu').hide();
            $('.dropdown-menu[data-index="'+_index+'"]').show()
        },function(){
            $(this).removeClass('hover');
            $('.dropdown-menu').hide();
        })
        $('.main-nav .dropdown-menu').on('mousemove', function(){
            $(this).show();
        }).on('mouseout', function(){
          $(this).hide();
        });
        var $topBar = $(".top-bar");
        //頭部搜索
        var autoCompleteTimer;
        var $search = $topBar.find(".search-box");
        var $autoComplete = $topBar.find(".auto-complete");
        $topBar.on("keyup",".search-box :text",function(){
            var self = this;
            var keyword = $(self).val();
            if(keyword==null||keyword==undefined||$.trim(keyword)==''){
            	return;
            }
            if(keyword==null||keyword==undefined||$.trim(keyword)==''){
            	return;
            }
            var param={"searchKeyWord":$.trim(keyword)};
			$.ajax({
				type: 'post' ,
				url: contextPath+'/home/getSearchProductAutoList' ,
				cache:false ,
				async:true ,
				data: param,
				dataType:'json' ,
				success:function(response){
					if(response.code=="0"){
						var productAutoList=response.data;
						if(productAutoList!=null&&productAutoList!=undefined&&productAutoList!=''&&productAutoList.length>0){
							var autoProductHtml="";
							for(var autoI=0;autoI<productAutoList.length;autoI++){
								var productAuto=productAutoList[autoI];
								autoProductHtml+="<li><a href=\""+contextPath+"/ecProduct/productDetail?productId="+productAuto.productId+"\" title=\""+productAuto.showTitle+"\">"+productAuto.showTitle+"</a></li>";
							}
							autoProductHtml&&$autoComplete.html(autoProductHtml).show();
						}else{
							$autoComplete.html("").hide();
						}
					}else{
						$autoComplete.html("").hide();
					}
				} ,
				error:function(result){
					$autoComplete.html("").hide();
				}
			});

           /* autoCompleteTimer = window.setTimeout(function(){
            },100);*/
        });
        $search.find("form").on("submit",function(e){
        	var $form=$(this);
        	var keyword=$form.find("input[name='searchKeyWord']").val();
        	if(keyword!=null&&keyword!=undefined&&keyword!=''&&keyword.length>100){
        		layer.alert(loginPageData.searchKeyErro,{
        			title:loginPageData.msgTitle,
					offset: ['40%'],
					btn:[commonPageInfos.definitely]
        		});
        		return false;
        	}
        	return true;
        });

        $(document).on("click",function(e){
            if(!$(e.target).closest($search)[0]){
                $autoComplete.hide();
            }
        });

        //語言切換
        $("#JTopdDropdrow [data-name='language-item']").on('click',function(){
        	$this=$(this);
        	var languageType=$this.attr('data-type');
        	if(languageType!=null&&languageType!=''){
        		var url=contextPath+"/language/"+languageType;
        		$.ajax({
        			type:"POST",
        			url:url,
        			dataType:"json",
        			cache:false,//不使用缓存
        			success:function(response){
        				if(response.code==0){
        					window.location.reload(true);
        				}else{
        					alert(response.data);
        				}
        			},
        			error:function(){
        			}
        		});
        	}
        });
    })
}(jQuery));


var wechatPopup = (function(title){
    var _html = function(title){
        var temp = title || "掃描二維碼關注中國電信(澳門)官方公眾號";
        return '<div class="wechatPopup_mask"></div>'+
                '<div class="wechatPopup">'+
                    '<div class="wechatPopup_hd">'+
                        '<i class="icon"></i>'+
                        '<a href="javascript:;" class="close-a"></a>'+
                    '</div>'+
                    '<div class="wechatPopup_bd">'+
                    '</div>'+
                    '<div class="wechatPopup_ft">'+
                        '<p>'+commonPageInfos.footTitle13+'</p>'+
                        '<p>'+temp+'</p>'+
                    '</div>'+
                '</div>'
    };

    $('body').on('click', '.wechatPopup .close-a', function(){
        _close();
    })

    function _open(title) {
        $('body').append(_html(title));
        $('body').on('click', '.wechatPopup .close-a', function(){
            _close();
        });
    }

    function _close() {
        $(".wechatPopup_mask").remove();
       $(".wechatPopup").remove();
    }

    return {
        open: _open,
        close: _close
    }
})();
