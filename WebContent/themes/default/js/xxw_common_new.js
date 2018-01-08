

//公司列表，滚动
(function() {
    jq.fn.scrollList = function(settings) {
        var defaults = {
            child:"li",//要滚动的元素
            num:3,//小于这个数不滚动
            time:1000,//滚动一行的时间
            interval:3000,//滚动间隔
            direct:"down"   //滚动方向
        },
        settings = jq.extend(defaults,settings),
        obj = jq(this),myScroll;
        
        if(obj.find(settings.child).length > settings.num) {
            obj.hover(function() {
                clearInterval(myScroll);    
            }, function() {
                if(settings.direct == "up") {
                    myScroll = setInterval(function() {
                        var h1 = obj.find(settings.child+":first").height();
                        obj.animate({"margin-top":-h1+"px"},settings.time,function() {
                            jq(this).css("margin-top",0).find(settings.child+":first").appendTo(this);
                        });  
                    },settings.interval);   
                } else {
                    myScroll = setInterval(function() {
                        var h1 = obj.find(settings.child+":last").height();
                        obj.animate({"margin-bottom":-h1+"px"},settings.time,function() {
                            jq(this).css("margin-bottom",0).find(settings.child+":last").insertBefore(jq(this).find(settings.child+":first"));
                        }); 
                    },settings.interval);
                }
            }).trigger("mouseleave");   
        }
    };
        
})(jQuery);
 // 轮换图组件
!function(jq){
    jq.fn.slider=function(settings,t){
        if(!this.length){returnFalse()};
        settings=jq.extend({},jq.slider.defaults,settings);
        var obj=this;
        var scroller={};
        scroller.fn={};
        scroller.li = obj.find('li');
        scroller.sliderName = jq('.'+settings.sliderName+'');
        scroller.onNum = 0;
        scroller.auto = settings.auto;
        scroller.itemSum = scroller.li.length;
        scroller.bLeftBtn= obj.parent('div').find('a.bLeft');
        scroller.bRightBtn=obj.parent('div').find('a.bRight');
        scroller.bLeftBtnPer = settings.bLeft;
        scroller.bRightBtnPer = settings.bRight;
        scroller.moveSlider = settings.moveSlider;
        scroller.times = settings.time;
        scroller.opacity = settings.opacity;
        scroller.colorCout = 0;
        
        if(settings.fontLi) {
          scroller.font = jq('.slider_font');
          scroller.fontLi  =jq('.slider_font').find('li');
          scroller.font.find('li[class="'+settings.play+'"]').css("opacity","1");
        };
        /*if(!scroller.opacity && scroller.moveSlider){
            obj.css('left','-'+scroller.li.width()+'px')
          }*/
          if(settings.bgColor != "" && settings.bgLayer !="" && settings.bgColor.length == scroller.itemSum ){
                jq('.'+settings.bgLayer+'').css('background',''+settings.bgColor[0]+'');
              
          };
         // 方法：开始
        scroller.fn.on=function(){

          //alert("342")
          scroller.fn.off();
          scroller.fn.removeControl();
          scroller.fn.addControl();

          
          if(!scroller.auto){return;};
          scroller.run=setTimeout(function(){
            scroller.fn.goto(settings.direction);
          },scroller.times);
        };
        // 方法：停止
        scroller.fn.off=function(){
          if(typeof(scroller.run)!=="undefined"){clearTimeout(scroller.run);};
        };
        // 方法：增加控制
        scroller.fn.addControl=function(){
          if(scroller.bLeftBtnPer&&scroller.bLeftBtn.length){
            scroller.bLeftBtn.bind("click",function(){
              scroller.fn.goto("bLeft");
            });
          };
          if(scroller.bRightBtnPer&&scroller.bRightBtn.length){
            scroller.bRightBtn.bind("click",function(){
              scroller.fn.goto("bRight");
            });
          };
        };
        // 方法：解除控制
        scroller.fn.removeControl=function(){
          if(scroller.bLeftBtn.length){scroller.bLeftBtn.unbind("click");};
          if(scroller.bRightBtn.length){scroller.bRightBtn.unbind("click");};
        };

        //有轮播标记
        if(settings.markSlider && !scroller.moveSlider) {
          scroller.markLi  =obj.siblings('.'+settings.markClass+'').find('li');
          // 方法：点击轮播标记切换
          scroller.markLi.mouseenter(function(){
              scroller.fn.off();
              scroller.markNum = scroller.markLi.index(jq(this));
              scroller.li.addClass(''+settings.play+'').stop(1,1).css({
                opacity:"1",
                filter:"alpha(opacity=100)",
                display:"none"
              },settings.speed);
              scroller.li.eq(scroller.markNum-1).stop(1,1).css("opacity",'0.5').addClass(''+settings.play+'').css("display",'block').animate({opacity:"0"},settings.speed,function(){
                jq(this).css('display','none');
              }); 
              scroller.li.eq(scroller.markNum).stop(1,1).css('opacity','0.5').removeClass(''+settings.play+'').css("display",'block').animate({opacity:"1"},settings.speed); 
              scroller.markLi.removeClass(''+settings.markLiClass+'');
              jq(this).addClass(''+settings.markLiClass+'');
              scroller.fn.on();
          });    
        }else if(settings.markSlider && scroller.moveSlider){
          scroller.markLi  =obj.siblings('.'+settings.markClass+'').find('li');
          scroller.markLi.on(settings.markEvent,function() {
                scroller.markNum = scroller.markLi.index(jq(this));
                scroller.li.removeClass(''+settings.play+'');
                scroller.li.eq(scroller.markNum+1).addClass(''+settings.play+'');
                scroller.markLi.removeClass(''+settings.markLiClass+'');
                obj.animate({
                    left:'-'+(scroller.markNum+1)*scroller.li.width()+'px'
                });
                jq(this).addClass(''+settings.markLiClass+'');  
          });   
         
          /*scroller.markLi.click(function(){
            scroller.markNum = scroller.markLi.index(jq(this));
            scroller.li.removeClass(''+settings.play+'');
            scroller.li.eq(scroller.markNum+1).addClass(''+settings.play+'');
            scroller.markLi.removeClass(''+settings.markLiClass+'');
            obj.animate({
                left:'-'+(scroller.markNum+1)*scroller.li.width()+'px'
            });
            jq(this).addClass(''+settings.markLiClass+'');

          });*/
        };
        scroller.li.hover(function(){
                scroller.fn.off();
        },function(){
            scroller.fn.on();
            scroller.colorCout == 1
        });
        // 方法：滚动
        scroller.fn.goto=function(d){
          scroller.fn.off();
          if(settings.bLeft && settings.bRight){
            scroller.fn.removeControl();
          };
          
          obj.stop(true);
          if(!scroller.moveSlider){
             scroller.onCurNum = scroller.li.index(obj.find('li:not(.'+settings.play+')'))  ;//play 位置
          }else{
             scroller.onCurNum = scroller.li.index(obj.find('li[class="'+settings.play+'"]'))  ;//play 位置   
          };
         
          if(scroller.opacity && !scroller.moveSlider){
            scroller.li.eq(scroller.onCurNum).addClass(''+settings.play+'').stop(1, 1).animate({
              opacity:"0",
              filter:"alpha(opacity=0)"
            },settings.speed,function(){
                jq(this).css({display:"none",opacity:"1"});
            });
            //console.log(scroller.onCurNum)
          
           /*scroller.li.eq(scroller.onCurNum).css("opacity",'0.5').removeClass(''+settings.play+'').animate({
              opacity:"0",
              filter:"alpha(opacity=0)"
            },settings.speed,function(){
                jq(this).css("display","none");
            });*/
          };
          
          if(settings.fontLi){
            scroller.fontLi.addClass(''+settings.play+'').stop(1, 1).animate({
              opacity:"0",
              filter:"alpha(opacity=0)"
            },0,function(){
                jq(this).css({display:"none",opacity:"1"});
            });;
          };
          switch(d){

            case "bRight":
            //滑动
            if(scroller.moveSlider && (scroller.onCurNum+1) == scroller.itemSum){//5
                scroller.totalWidth = scroller.itemSum * scroller.li.width();
                obj.css('left','-'+scroller.li.width()+'px');
                obj.animate({left:'-'+2*scroller.li.width()+'px'});
                scroller.li.removeClass(''+settings.play+'');
                scroller.li.eq(2).addClass(''+settings.play+'');
                if(settings.markSlider){scroller.markLi.removeClass(''+settings.markLiClass+'').eq(1).addClass(''+settings.markLiClass+'');}

            }else if(scroller.moveSlider && scroller.onCurNum == 1){
                obj.animate({left:'-'+2*scroller.li.width()+'px'});
                scroller.li.removeClass(''+settings.play+'');
                scroller.li.eq(2).addClass(''+settings.play+'');
                if(settings.markSlider){scroller.markLi.removeClass(''+settings.markLiClass+'').eq(1).addClass(''+settings.markLiClass+'');}
            }else if(scroller.moveSlider && scroller.onCurNum != scroller.itemSum){//1-4
                obj.animate({
                 left:'-'+(scroller.onCurNum+1)*scroller.li.width()+'px'
                });
                scroller.li.removeClass(''+settings.play+'');
                scroller.li.eq(scroller.onCurNum+1).addClass(''+settings.play+'');
                
                /*if(settings.markSlider && (scroller.onCurNum+2) == scroller.itemSum){
                    scroller.markLi.removeClass(''+settings.markLiClass+'').eq(0).addClass(''+settings.markLiClass+'');
                }else{
                    scroller.markLi.removeClass(''+settings.markLiClass+'').eq(scroller.onCurNum).addClass(''+settings.markLiClass+'');
                }*/
                if(settings.markSlider) {
                    if((scroller.onCurNum+2) == scroller.itemSum){
                        scroller.markLi.removeClass(''+settings.markLiClass+'').eq(0).addClass(''+settings.markLiClass+'');
                    }else{
                        scroller.markLi.removeClass(''+settings.markLiClass+'').eq(scroller.onCurNum).addClass(''+settings.markLiClass+'');
                    }   
                }
            }
            //渐隐
            if(((scroller.onCurNum+1) == scroller.itemSum) && !scroller.moveSlider ){
                if(settings.bgColor != "" && settings.bgLayer !="" && settings.bgColor.length == scroller.itemSum && settings.bgColor != false){
                    jq('.'+settings.bgLayer+'').css('background',''+settings.bgColor[0]+'');
                };
                jq('.'+settings.numClass+'').html("<em>1</em> / "  + scroller.itemSum);
                scroller.li.eq(0).stop(1, 1).css('opacity','0.5').removeClass(''+settings.play+'').css("display",'block').animate({opacity:"1"},settings.speed); 
                if(settings.fontLi) scroller.fontLi.eq(0).removeClass(''+settings.play+'').css("display",'block').animate({opacity:"1"},settings.speed); 

                //sisi
                if(settings.markSlider){scroller.markLi.removeClass(''+settings.markLiClass+'').eq(0).addClass(''+settings.markLiClass+'');}
                //sisi
                

            }else if(((scroller.onCurNum+1) != scroller.itemSum) && !scroller.moveSlider ){
                if(settings.bgColor != "" && settings.bgLayer !="" && settings.bgColor.length == scroller.itemSum && settings.bgColor != false){
                    jq('.'+settings.bgLayer+'').css('background',''+settings.bgColor[scroller.onCurNum+1]+'');
                }; 
                jq('.'+settings.numClass+'').html("<em>"+( scroller.onCurNum + 2)+"</em> / "  + scroller.itemSum);

                scroller.li.eq(scroller.onCurNum+1).stop(1, 1).css('opacity','0.5').removeClass(''+settings.play+'').css("display",'block').animate({opacity:"1"},settings.speed);


                if(settings.fontLi) scroller.fontLi.eq(scroller.onCurNum+1).removeClass(''+settings.play+'').css("opacity",'1').animate({opacity:"1"},settings.speed,function(){
                    jq(this).css('display','block');
                
            });

            //sisi
            if(settings.markSlider){scroller.markLi.removeClass(''+settings.markLiClass+'').eq(scroller.onCurNum+1).addClass(''+settings.markLiClass+'');}  
            //sisi
            };
            break;
            case "bLeft":
            //滑动
            if(scroller.moveSlider && scroller.onCurNum == 0){//0
                scroller.totalWidth = scroller.itemSum * scroller.li.width();
                obj.css('left','-'+(scroller.itemSum-2)*scroller.li.width()+'px');
                obj.animate({left:'-'+(scroller.itemSum-3)*scroller.li.width()+'px'});
                scroller.li.removeClass(''+settings.play+'');
                scroller.li.eq(scroller.onCurNum-3).addClass(''+settings.play+'');
                if(settings.markSlider){scroller.markLi.removeClass(''+settings.markLiClass+'').eq(scroller.onCurNum-2).addClass(''+settings.markLiClass+'');};
                
            }else if(scroller.moveSlider && scroller.onCurNum == 1){
                obj.animate({left:'0px'});
                scroller.li.removeClass(''+settings.play+'');
                scroller.li.eq(0).addClass(''+settings.play+'');
                if(settings.markSlider){scroller.markLi.removeClass(''+settings.markLiClass+'').eq(scroller.onCurNum+2).addClass(''+settings.markLiClass+'');};
                
            }else if(scroller.moveSlider && scroller.onCurNum != scroller.itemSum){//1-4
                obj.animate({
                 left:'-'+(scroller.onCurNum-1)*scroller.li.width()+'px'
                });
                scroller.li.removeClass(''+settings.play+'');
                scroller.li.eq(scroller.onCurNum-1).addClass(''+settings.play+'');
                if(settings.markSlider){scroller.markLi.removeClass(''+settings.markLiClass+'').eq(scroller.onCurNum-2).addClass(''+settings.markLiClass+'');}
                
            };
            //渐隐
            if(scroller.onCurNum == 0  && !scroller.moveSlider ){
                if(settings.bgColor != "" && settings.bgLayer !="" && settings.bgColor.length == scroller.itemSum && settings.bgColor != false){
                      jq('.'+settings.bgLayer+'').css('background',''+settings.bgColor[scroller.itemSum-1]+'');
                  };
                scroller.li.eq(scroller.itemSum-1).stop(1, 1).css('opacity','0.5').removeClass(''+settings.play+'').css("display",'block').animate({opacity:"1"},settings.speed,function(){
                    jq(this).css('display','block');
                });

                if(settings.fontLi) scroller.li.eq(scroller.itemSum-1).removeClass(''+settings.play+'').css("display",'block').animate({opacity:"1"},settings.speed); 
                jq('.'+settings.numClass+'').html("<em>"+scroller.itemSum+"</em> / "  + scroller.itemSum);

                //sisi
                if(settings.markSlider){scroller.markLi.removeClass(''+settings.markLiClass+'').eq(scroller.itemSum-1).addClass(''+settings.markLiClass+'');};
                //sisi
                

            }else if(scroller.onCurNum != 0  && !scroller.moveSlider ){
                if(settings.bgColor != "" && settings.bgLayer !="" && settings.bgColor.length == scroller.itemSum && settings.bgColor != false){
                    jq('.'+settings.bgLayer+'').css('background',''+settings.bgColor[scroller.onCurNum-1]+'');
                };
                scroller.li.eq(scroller.onCurNum-1).stop(1, 1).css('opacity','0.5').removeClass(''+settings.play+'').css("display",'block').animate({opacity:"1"},settings.speed);

                if(settings.fontLi) scroller.fontLi.eq(scroller.onCurNum-1).removeClass(''+settings.play+'').css("display",'block').animate({opacity:"1"},settings.speed);  
                jq('.'+settings.numClass+'').html("<em>"+( scroller.onCurNum )+"</em> / "  + scroller.itemSum);

                //sisi
                if(settings.markSlider){scroller.markLi.removeClass(''+settings.markLiClass+'').eq(scroller.onCurNum-1).addClass(''+settings.markLiClass+'');};
                //sisi
                
            }
            break;

          }
          obj.queue(function(){
            if(settings.bLeft && settings.bRight ){
                 scroller.fn.removeControl();
                 scroller.fn.addControl();
            };
            if(scroller.auto){
                scroller.run=setTimeout(function(){
                     scroller.fn.goto(settings.direction);
               },scroller.times);
            };
           
            
            jq(this).dequeue();
          });
        };
            
        scroller.fn.on();
  };

  // 默认值
  jq.slider={defaults:{
      speed:800,      // 滚动速度
      time:4000,      // 自动滚动间隔时间
      play:"on",         //选中样式
      num:true,        //是否出现总数
      numClass:"slider_num" ,    // 总数显示区域
      auto:true,
      bLeft:true,                 //左控
      bRight:true ,            //右控
      direction:"bRight",  // 顺序
      fontLi:true,             //是否开启描述
      addControl:false,
      moveSlider:false,
      opacity:true,
      bgColor:false,
      //sisi
      markSlider:true,           //是否有轮播标记
      markClass:"slider_mark",       //轮播结构
      markLiClass:"mark_dot_on",        //轮播当前态class
      markEvent:"click"//点击跳转
      //sisi
  }};
}(jQuery);
function returnFalse(){
    return false;
};
// 浏览器判断
function checkBrowser(){
   var u = window.navigator.userAgent.toLocaleLowerCase(),
    msie = /(msie) ([\d.]+)/,
    chrome = /(chrome)\/([\d.]+)/,
    firefox = /(firefox)\/([\d.]+)/,
    safari = /(safari)\/([\d.]+)/,
    opera = /(opera)\/([\d.]+)/,
    ie11 = /(trident)\/([\d.]+)/,
    b = u.match(msie)||u.match(chrome)||u.match(firefox)||u.match(safari)||u.match(opera)||u.match(ie11);
    return {name: b[1], version: parseInt(b[2])};

};

//标签切换组件
!function(jq){
    jq.fn.tabSelect = function(settings){
       if(!this.length){returnFalse();};
       settings=jq.extend({},jq.slider.defaults,settings);
       var tabS = {},
           obj = this;
       tabS.fn = {};
       tabS.fn.curr = settings.play;
       obj.find('li').mouseenter(function(){
          jq(this).parent().find('li').removeClass(''+tabS.fn.curr+'');
          jq(this).addClass(''+tabS.fn.curr+'');
          var n = jq(this).parent().find('li').index(jq(this));
          jq(this).parent().siblings('div').hide();
          jq(this).parent().siblings('div').eq(n).show();
          if(jq(this).parent().attr('v') != '1'){
              jq("img.lazy1").lazyload({
                    placeholder : "http://img.to8to.com/front_end/bg/grey.gif",effect : "fadeIn",failurelimit : 54
              });
          };
          jq(this).parent().attr('v','1');

        });
    };
    jq.tabSelect = {defaults:{
       play:"on"  //选中样式

    }};
}(jQuery);

//判断是否安装Flash插件
function IsFlashEnabled() {
   var obj = checkBrowser(),
       re = false;
   if(obj.name == "msie" && obj.version == 6) {
       try{
            //IE
            var swf1 = new ActiveXObject("ShockwaveFlash.ShockwaveFlash");
       }catch(e){
           try{
                //FireFox,Chrome
                var swf2=navigator.plugins["Shockwave Flash"];
                if(swf2==undefined){
                    re = true;
                };
            }catch(ee){
               re = true;
            }
        };
  };
  return re;//false启用 true未启用
} 

jq(function() {
    jq("body").on("focus","input[type=text],input[type=password],textarea",function() {
        if (!jq(this).hasClass('outcontrol')) {
            jq(this).css("border-color","#96d5b9");
        }
    });
    jq("body").on("blur","input[type=text],input[type=password],textarea,select",function() {
        if (!jq(this).hasClass('outcontrolblur')) {
            jq(this).css("border-color","#ddd");
        }
    });
});

//设置初始UL宽度
(function(jq) {
    jq.fn.setWidth = function(margin) {
        var ul = jq(this),
            len = ul.find("li").length,
            w = ul.find("li").width() + margin,
            wAll = len*w;
                
        ul.css("width",wAll+"px");
    };
})(jQuery);

//图片切换
(function(jq) {
    jq.fn.slideTxq = function(settings) {
        var defaults = {
            derect:"left",//默认方向
            margin:13,
            leftBtn:".slide_l",
            rightBtn:".slide_r",
            time:500,//滑动一张时间
            num:5,//大于这个数滑动
            btnWrap:false//btn位置
        };
        settings = jq.extend(defaults,settings);
        var par = jq(this).parent(),
            ul = par.find("ul:eq(0)"),
            len = ul.find("li").length,
            w = ul.find("li").width() + settings.margin,
            btnL = par.nextAll(settings.leftBtn),
            btnR = par.nextAll(settings.rightBtn),
            that = jq(this),
            on_index = 0;
            
            
        if(settings.btnWrap) {
            btnL = par.find(settings.leftBtn);
            btnR = par.find(settings.rightBtn); 
        };

        jq(this).setWidth(settings.margin);
        if(settings.num < len) {
            jq(btnR).click(function() {
                if(!jq(that).is(":animated")) {
                    ul.animate({"margin-left":"-"+w+"px"},settings.time,function() {
                        ul.find("li:first").appendTo(ul);
                        ul.css("margin-left",0);
                    });
                };
            });
            jq(btnL).click(function() {
                if(!jq(that).is(":animated")) {
                    ul.find("li:last").prependTo(ul).parent().css("margin-left",-w+"px");   
                    ul.animate({"margin-left":"+="+w+"px"},settings.time);  
                };
            }); 
        };
    };
})(jQuery);

//HTML模板引擎
;(function(root){
    //模版语句块正则
    var tempTag = /<%([\s\S]*?)%>/;

    //最后将被eval解析的js字符串
    var temphtml_js = '';

    /**
     * [htmltemp 所有定义和使用的变量都只在selector所包含的范围内有效]
     * @param  {[string]} selector [必须,jquery选择器]
     * @param  {[string || json]} data     [可选,若传入第三个参数则该参数为字符串(第三个参数的变量名),否则为json数据]
     * @param  {[object]} dataArr  [可选,可被第三个参数接收的对象]
     * @return {[void]}   
     */
    function htmltemp(elem , data , dataArr){
        temphtml_js = '';
        //模版语法解析后的html字符串放在templateHtml里
        var templateHtml = "";
        if(typeof data === 'string'){
            if(dataArr){
                eval('var ' + data + '= dataArr;');
            }
        }else{
            parseData(data);
        }
        elem.jQuery ? parseTemp(elem.html()) : parseTemp(elem);
        eval(temphtml_js.replace(/\r|\n/g , ' '));
        return templateHtml;
    }

    //html代码解析
    function parseTemp(str){
        var pos = execTag(str);
        if(pos){
            var html = trim(str.substr(0 , pos.index)).replace(/'/g , '&apos;');
            if(html.length){
                temphtml_js += contactHtml() + html + endLine();
            }
            temphtml_js += getTempVal(pos[1]);
            str = str.substr(pos.index + pos[0].length);
            parseTemp(str);
        }else{
            temphtml_js += contactHtml() + str + endLine();
        }
    }

    //模版块匹配
    function execTag(str){
        return tempTag.exec(str);
    }

    //连接html代码
    function contactHtml(){
        return 'templateHtml +=\'';
    }

    //获取'{%= varName %}'里面的varName的值
    function getTempVal(str){
        str = parseJs(trim(str));
        if(str.charAt(0) === '='){
            return 'templateHtml +=' + str.substr(1) + ';';
        }
        return str;
    }

    //结束一行js代码
    function endLine(){
        return '\';';
    }

    //被格式化的'>'与'<'转化回来
    function parseJs(str){
        return str.replace(/&lt;/g , '<').replace(/&gt;/g , '>');
    }

    //将被引进的json数据定义为局部的变量
    function parseData(data){
        if(!data)return;
        for(var i in data){
            temphtml_js += 'var ' + i + '= "' + data[i] + '";';
        }
    }

    function trim( str ){
        return str.replace(/(^\s*)|(\s*$)/ , '');
    }

    root.htmltemp = htmltemp;
})(window);
