var global_appServer= 'http://www.asj.com';
var global_login_appServer= 'https://safe.asj.com';
var global_imageServer= 'http://image.asj.com';
var globalHasGetCart = false;
var globalHasGetFavorite = false;
var footerGlobalHasGetCart = false;
var globalHasAjaxLogin = false;
var globalIsLogin = false;
var globalCountNow = 2;
var globalCountDown = "";
function initServerAddr(appserver, imageServer) {
	global_appServer = appserver;
	global_imageServer = imageServer;
}

function init() {
	initLoginFromCPS();
	writeShoppingCartInfo();
	initLoginInfo();
	getGlobalSearchHotOfInput();
}
/**
 * 统计购物中货品的数量
 */
function writeShoppingCartInfo(totalCount) {
	var $spn = $(".j_shopCart,.j_goBuyCar").find(".j_buyNum");
	if(totalCount == undefined){
		$.ajax({
			url: "/shopCart/ajaxGetGountOfShopCart.htm",
			type: "POST",
			dataType: "json",
			success: function(data){
				var retCount = data["retCount"];
				$spn.html(retCount);
			}
		});
	}else{
		$spn.html(totalCount);
	}
}

/**
 * 判断页面的登录状态
 */
function initLoginInfo() {
	var $tl = $(".j_topLogin");
	$.ajax({
		type : "post",
		url :  '/member/checkLogin.htm',
		dataType : "json",
		success : function(data) {
			var nickname = decodeURIComponent(getCookie('24home.nick'));
			if (data.errorNo == 0) {
				if(nickname == "null" || nickname == null || nickname == ""){
					nickname = decodeURIComponent(data.errorInfo);
				}
				$tl.html('<b>'+ nickname + '</b> <a class="quit" href="' + global_appServer + '/member/logout.htm">[退出]</a>');
				globalIsLogin = true;
				$.ajax({
					url: "/member/ajax/message.htm",
					type: "POST",
					dataType: "json",
					success: function(data) {
						var result = data["result"];  
						$(".j_tmsgSys").html(result.unReadCount);
						$(".j_tmsgAsk").html(result.consultationCount);
						$(".j_tmsgCount").html(result.consultationCount+result.unReadCount);
						initLoginFromCPS();
					}
				});
			}else{
				$tl.html('<a class="login" href="' + global_login_appServer + '/member/register.htm">用户注册</a><span class="split">|</span><a class="reg" href="' + global_login_appServer + '/member/login.htm">登录</a>');
				globalIsLogin = false;
			}
		},
		error : function() {
			$tl.html('<a class="login" href="' + global_login_appServer + '/member/register.htm">用户注册</a><span class="split">|</span><a class="reg" href="' + global_login_appServer + '/member/login.htm">登录</a>');					
		}
	});
}

function openActive(){
	$.ajax({
		type : "POST",
		url :  global_appServer +'/active/new.htm',
		dataType : "json",
		success : function(data) {
			var hrefUrl = data.data["filePath"];
			window.location.href=global_appServer +hrefUrl;
		},
		error : function() {
			window.location.href=global_appServer;					
		}
	});
}

function getQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return unescape(r[2]); return null;
}
//写cookies
function setCookie(name,value,time){
    var strsec = getsec(time);
    var exp = new Date();
    exp.setTime(exp.getTime() + strsec*1);
    document.cookie = name + "="+ escape (value) + ";expires=" + exp.toGMTString();
}
//读取cookies
function getCookie(name){
    var arr,reg=new RegExp("(^| )"+name+"=([^;]*)(;|$)");
    if(arr=document.cookie.match(reg))
        return (arr[2]);
    else
        return null;
}

//删除cookies
function delCookie(name){
    var exp = new Date();
    exp.setTime(exp.getTime() - 1);
    var cval=getCookie(name);
    if(cval!=null)
        document.cookie= name + "="+cval+";expires="+exp.toGMTString();
}
function getsec(str){
   var str1=str.substring(1,str.length)*1;
   var str2=str.substring(0,1);
   if (str2=="s"){
        return str1*1000;
   }else if (str2=="h"){
       return str1*60*60*1000;
   }else if (str2=="d"){
       return str1*24*60*60*1000;
   }
}

/** 页面头部购物车中货品删除  **/
function cartInfoRemove(pid,cid){
	$.ajax({
		url: "/shopCart/delHeaderCart.htm",
		data: {pid: pid, cid:cid},
		type: "POST",
		dataType: "text",
		async: false,
		cache: false,
		success: function(data) {
			$(".j_myCart").find(".j_cartList").html(data);
			writeShoppingCartInfo();
		},
		error: function() {
			alert("购物车删除失败！");
		}
	});
}

/***
 * 初始化搜索框中的搜索词
 */
function getGlobalSearchHotOfInput() {
	var $sipt = $(".j_searchInput");
	if($sipt.val() == ""){
		$.ajax({
			url: "/search/findHotOfInput.htm",
			type: "POST",
			dataType: "text",
			async: false,
			cache: false,
			success: function(data) {
				$sipt.val(data);
			}
		});	
	}
}
//格式化金额
function fmoney(s){ 
	if(s==null)return s;
	s = parseFloat((s + "").replace(/[^\d\.-]/g, "")).toFixed(2) + "";
	var l = s.split(".")[0].split("").reverse(),r = s.split(".")[1];  
	t = "";  
	for(var i = 0; i < l.length; i++ ){  
		t += l[i] + ((i + 1) % 3 == 0 && (i + 1) != l.length ? "," : "");  
	}
	return '¥'+t.split("").reverse().join("") + "." + r.substring(0,2);
}
//将格式化的金额还原为数字
function dmoney(s){  
	var reg = /\,/g;
	if(s==null) return s;
	return s.replace("¥","").replace(reg,"").replace(".00","");
}

var popMsg = {
	/*
	 * 弹出提示消息
	 * itype：消息类型，msg，alert，confirm
	 * showmode：显示状态，0（显示半秒销毁），1（一直显示直到点击按钮）
	 * parent：父对象
	 * imsg：消息内容
	 * style为json参数，包含：
	 * width宽度；top到顶部距离；left到左侧距离
	 */
	disPlay:function(itype,showmode,parent,imsg,icontent,style){
		var thisWidth = style.width?style.width:0;
		var thisTop = style.top?style.top:0;
		var thisLeft = style.left?style.left:0;
		var thisIndex = style.z_index?style.z_index:8;
		$(".j_popMsg").remove();
		$(".j_popMsgBg").remove();
		$(parent).append("<div class='popmsgbg j_popMsgBg'></div><div class='popmsg j_popMsg pop-"+itype+"'><div class='popmsg-tit'><span>"+imsg+"</span></div><div class='popmsg-content'><span class='"+itype+"'></span>"+icontent+"</div><div class='popmsg-btn'><a class='popmsg-sure'>确定</a><a class='popmsg-cancel j_cancel'>取消</a></div></div>");
		$(".popmsgbg").css("z-index",thisIndex);
		$(".popmsg").css({top:($(window).height()-$(".popmsg").height()-thisTop)/2+document.body.scrollTop + document.documentElement.scrollTop,left:($(window).width()-$(".popmsg").width()-thisLeft)/2,"z-index":thisIndex+1});
		if(icontent!=''){
			$("<a class='popmsg-close j_popClose'>×</a>").insertBefore(".popmsg-tit span");
		}
		var clrCloseTime = "";
		if(showmode==0){
			$(".popmsg-btn").html("<span class='j_countTime'>3</span> 秒后自动关闭");
			globalCountDown = setInterval('if(globalCountNow > 0){$(".j_countTime").html(globalCountNow);globalCountNow--;}else{$(".popmsgbg").remove();$(".popmsg").remove();clearInterval(globalCountDown);globalCountNow = 2;}',1000);
		}
	}
};
var spopMsg = {
	/*
	itype:
		alert：		只显示确定按钮
		confirm：	显示确定、取消按钮
	*/
	disPlay:function(itype,imsg,icontent){
		$(".j_popMsg").remove();
		$(".j_popMsgBg").remove();
		$("body").append("<div class='popmsgbg j_popMsgBg'></div><div class='popmsg j_popMsg pop-"+itype+"'><div class='popmsg-tit'><a class='popmsg-close j_popClose'>×</a>"+imsg+"</div><div class='popmsg-content'><span class='"+itype+"'></span>"+icontent+"</div><div class='popmsg-btn'><a class='popmsg-sure'>确定</a><a class='popmsg-cancel j_cancel'>取消</a></div></div>");
		$(".popmsg").css({top:($(window).height()-$(".popmsg").height())/2+document.body.scrollTop + document.documentElement.scrollTop,left:($(window).width()-$(".popmsg").width())/2});
	}
};

var kpopMsg = {
	/*
	itype:
		alert：		只显示确定按钮
		confirm：	显示确定、取消按钮
	*/
	disPlay:function(itype,imsg,icontent,istyle){
		var thisWidth = istyle.width?istyle.width:0;
		var thisTop = istyle.top?istyle.top:0;
		var thisLeft = istyle.left?istyle.left:0;
		var thisIndex = istyle.z_index?istyle.z_index:8;
		var thisOpacity = istyle.opacity?istyle.opacity:0;
		$(".j_popMsg").remove();
		$(".j_popMsgBg").remove();
		$("body").append("<div class='popmsgbg j_popMsgBg'></div><div class='popmsg j_popMsg pop-"+itype+"'><div class='popmsg-tit'><a class='popmsg-close j_popClose'>×</a>"+imsg+"</div><div class='popmsg-content'>"+icontent+"</div><div class='popmsg-btn'><a class='popmsg-sure j_sure'>确定</a><a class='popmsg-cancel j_cancel'>取消</a></div></div>");
		$(".popmsgbg").css("z-index",thisIndex);
		if(thisOpacity>0){
			$(".popmsgbg").css("opacity",thisOpacity);
		}
		if(thisWidth>0){
			$(".popmsg").css("width",thisWidth+"px");
		}
		$(".popmsg").css({top:($(window).height()-$(".popmsg").height()-thisTop)/2+document.body.scrollTop + document.documentElement.scrollTop,left:($(window).width()-$(".popmsg").width()-thisLeft)/2,"z-index":thisIndex+1});
	}
};

/*20140301版公用*/
var s_txt = '';
$(function(){
	/*选择分类下拉特效*/
	$(".ls-select").live("click",function(){
		$(this).toggleClass("h24");
		$(this).find(".ls-class").css({"width":$(this).width()+6}).toggle();
	});
	
	/*选择分类*/
	$(".ls-class a").live("click",function(){
		$(this).parents(".ls-select").find(".ls-cid").val($(this).attr("data-id"))
		.parents(".ls-select").find(".ls-sname").html($(this).html());
		$(this).parents(".ls-select").toggleClass("h24").find(".ls-class").css({"width":$(this).width()+6}).toggle().find("a").removeClass("sel");
		$(this).addClass("sel");
	});
	
	/*弹出层关闭*/
	$(".j_popClose,.j_cancel,.pop-alert .popmsg-sure").live("click",function(){
		$(".j_popMsg,.j_popMsgBg").remove();
	});
	
	/*搜索输入框*/
	$(".j_searchInput").focus(function(){
		var $t = $(this);
		var $p = $t.parent();
		$p.find(".j_searchTip").hide();
		if($t.val()==""){
			selectItem("");
		}
	}).keyup(function(event){
		var queryString = $.trim($(this).val());
		event = event ? event : (window.event ? window.event : null);
		if (event.keyCode!=38&&event.keyCode!=40){
			if(queryString != ''){
				s_txt = $(this).val();
			}
			selectItem(queryString);
		}
	}).blur(function(){
		if($(this).val()==""){
			$(this).parent().find(".j_searchTip").show();
		}
	});
	
	$(".j_searchSubmitBtn").click(function(){
		var $si = $(".j_searchInput");
		var $st = $(".j_searchTip");
		var $sf = $(".j_searchForm");
		if($si.val() == ""){
			$si.val($.trim($.text($st)));
			$st.hide();
			$sf.submit();
		}else{
			$si.val($.trim($si.val()));
			$sf.submit();		
		}
	});
	
	/*初始化价格、销量、评价*/
	initPrice();
	
	/*搜索输入框下拉热词*/
	$(".j_searchHot li:not('.j_searchTit')").live("mouseover",function(){
		$(this).addClass("select");
	}).live("mouseout",function(){
		$(this).removeClass("select");
	}).live("click",function(){
		$(this).parents(".j_searchIpt").find(".j_searchInput").val($(this).find("a").html());
		$(this).parents(".j_searchIpt").find(".j_searchTip").hide();
		$(".j_searchForm").submit();
		$(this).parent().hide();
	});
	
	/*展开分类热门商品已售数量*/
	$(".j_classHot").mouseenter(function(){
		if($(this).find(".j_soldNum").attr("data-get")=="false"){
			var productId = $(this).attr("data-id");
			var $t = $(this);
			$.ajax({
				type : "post",
				url: "/commonAjax/getProductMessage.htm",
				data: {productId : productId},
				dataType : "json",
				success: function(data) {
					var result = data["list"];
					$t.find(".j_soldNum").html(result[0].sellAcount).attr("data-get","true").parent().fadeIn(100);
				}
			});
		}else{
			$(this).find(".j_soldNum").parent().fadeIn(100);
		}
	}).mouseleave(function(){
		$(this).find(".j_soldNum").parent().hide();
	});

	/*分享*/
	$('.j_shareWeibo,.j_shareQq,.j_shareRenren,.j_shareQzone,.j_shareKaixin').live("click",function(){
		var pic = $(this).parent().find(".j_shareGoods").val();
		var title = $(this).parent().find('.j_shareContent').val();
		var path = $(this).parent().find('.j_shareUrl').val();
		var content = title;
		var content_sina = title;
		var u = encodeURIComponent(path);
		content = encodeURIComponent(content);
		content_sina = encodeURIComponent(content_sina);
		pic = encodeURIComponent(pic);
		var openUrl = '';
		if($(this).attr("class").indexOf("j_shareWeibo") != -1){
			openUrl = 'http://v.t.sina.com.cn/share/share.php?title='+ content_sina +'&url='+u+'&appkey=3819135700&pic='+pic;
		}
		if($(this).attr("class").indexOf("j_shareQq") != -1){
			openUrl = 'http://v.t.qq.com/share/share.php?title='+content+'&url='+u+"&pic="+pic;
		}
		if($(this).attr("class").indexOf("j_shareRenren") != -1){
			openUrl = 'http://share.renren.com/share/buttonshare.do?title='+content+"&content="+ content +'&link='+u+"&pic="+pic;
		}
		if($(this).attr("class").indexOf("j_shareQzone") != -1){

			openUrl = 'http://sns.qzone.qq.com/cgi-bin/qzshare/cgi_qzshare_onekey?site=爱尚家网上商城&url='+u+'&desc='+content+'&pics='+pic;
		}
		if($(this).attr("class").indexOf("j_shareKaixin") != -1){
			openUrl = 'http://www.kaixin001.com/repaste/share.php?rtitle='+content+'&rurl='+u;
		}
		window.open(openUrl);
	});
});


/*搜索下拉项目键盘上下选择*/
$(document).keydown(function(event){
	event = event ? event : (window.event ? window.event : null);
	var $sli = $(".j_searchHot li:not('.j_searchTit')");
	var $sipt = $(".j_searchInput");
	var s_li_tot = $sli.length-1;
	var s_li = -1;
	var s_li_next = 0;
	if (event.keyCode==38){
		for(var i = 0; i<s_li_tot+1; i++){
			if($sli.eq(i).attr("class")=="select"){
				s_li = i;
			}
		}
		s_li_next = s_li-1;
		if(s_li_next<-1){
			s_li_next = s_li_tot;
		}
		if(s_li_next == -1){
			$sli.eq(s_li).removeClass("select");
			$sipt.val(s_txt);
		}else{
			$sli.eq(s_li_next).addClass("select");
			$sipt.val($sli.eq(s_li_next).find("a").html());
			if(s_li != -1){
				$sli.eq(s_li).removeClass("select");
			}
		}
	}
	if (event.keyCode==40){
		for(var i = 0; i<s_li_tot+1; i++){
			if($sli.eq(i).attr("class")=="select"){
				s_li = i;
			}
		}
		s_li_next = s_li+1;
		if(s_li_next>s_li_tot+1){
			s_li_next = 0;
		}
		$sli.eq(s_li_next).addClass("select");
		$sipt.val($sli.eq(s_li_next).find("a").html());
		$sli.eq(s_li).removeClass("select");
		if(s_li_next == s_li_tot+1){
			$sipt.val(s_txt);
		}
	}
});

/*初始化价格、销量、市场价、评价数*/
function initPrice(){
	$(".j_comPrice").each(function(i){
		var productId = $(this).attr("data-id");
		var $t = $(this);
		var sellPrice,marketPrice,diffPrice;
		$.ajax({
			type: "post",
			data: {productId:productId},
			url: global_appServer + '/commonAjax/getProductMessage.htm',
			async: true,
			dataType: "json",
			success: function(data){
				var result = data["list"];
				sellPrice = result[0].cost;
				marketPrice = result[0].marketPrice;
				if(marketPrice){
					diffPrice = marketPrice - sellPrice;
				}else{
					diffPrice = 0;
				}
				if(sellPrice.toString().indexOf(".")!=-1){
					if($t.html().indexOf("￥")!=-1){
						$t.find(".j_sellPrice").html(sellPrice.toFixed(2));
					}else{
						$t.find(".j_sellPrice").html("￥"+sellPrice.toFixed(2));
					}
				}else{
					if($t.html().indexOf("￥")!=-1){
						$t.find(".j_sellPrice").html(sellPrice);
					}else{
						$t.find(".j_sellPrice").html("￥"+sellPrice);
					}
				}
				if(diffPrice == 0){
					$t.find(".j_diffPrice").parent().remove();
				}else{
					if(diffPrice.toString().indexOf(".")!=-1){
						if($t.html().indexOf("￥")!=-1){
							$t.find(".j_diffPrice").html(diffPrice.toFixed(2));
						}else{
							$t.find(".j_diffPrice").html("￥"+diffPrice.toFixed(2));
						}
					}else{
						if($t.html().indexOf("￥")!=-1){
							$t.find(".j_diffPrice").html(diffPrice);
						}else{
							$t.find(".j_diffPrice").html("￥"+diffPrice);
						}
					}
				}
				if(marketPrice==""||marketPrice=="undefined"||marketPrice==null){
					$t.find(".j_split,.j_marketPrice").remove();
				}else{
					if(marketPrice.toString().indexOf(".")!=-1){
						$t.find(".j_marketPrice").html("￥"+marketPrice.toFixed(2));
					}else{
						$t.find(".j_marketPrice").html("￥"+marketPrice);
					}
				}
				$t.find(".j_evaluateNum").html(result[0].commentNum);
				$t.find(".j_soldNum").html(result[0].sellAcount);
			}
		});
	});
}

/*购物车数获取*/
function getGlobalCart() {
	if (!globalHasGetCart) {
		$(".j_myCart").find(".j_cartList").html('<div class="cart-loading"><span>加载中，请稍候...</span></div>');
		globalHasGetCart = true;
		$.ajax({
			type : "post",
			url : global_appServer + '/shopCart/headerCart.htm',
			dataType : "text",
			success : function(data) {
				$(".j_myCart").find(".j_cartList").html(data);
			}
		});
	}
}

function selectItem(f){
	if(f != ""){
		$.ajax({
			url: "/search/query.htm",
			data: {queryString:f, t:new Date().getTime()},
			context: document.body,
			dataType: "text",
			success: function(data) {
				 if(data != ""){
					 $(".j_searchHot").html(data).show();				 
				 }else{
					 $(".j_searchHot").html("").hide();
				 }
			}
		});
	}else{
		if($(".j_searchHot").length == 0){
			$.ajax({
				type : "post",
				url: "/commonAjax/searchRecord.htm",
				dataType : "json",
				success: function(data){
					var result = data["list"];
					if(result && result.length > 0){
						var html = '<ul class="search-hot j_searchHot"><li class="j_searchTit"><div class="tit">历史搜索</div></li>';
						for(var i=0;i<result.length;i++){
							html += '<li class="j_notBody"><a title="'+result[i]+'">'+result[i]+'</a></li>';
						}
						html += '</ul>';
						$(html).appendTo($(".j_searchIpt")).show();
					}
				}
			});
		}else{
			$(".j_searchHot").show();
		}
	}
}

var productItem = {
	addFavByNo : function(obj,id){
		$.ajax({
			url : "/member/ajax/addFavoritesByNo.htm",
			data: {externalProductNo : id},
			type : "POST",
			dataType : "json",
			async: false,
			cache: false,
			success : function(data) {
				if (data.errorNo == "0") {
					globalHasGetFavorite = false;
					$('<div class="love-finish"><div class="lbg"></div><div class="cbg">收藏成功</div><div class="rbg"></div><div class="arr"></div></div>').insertAfter(obj).animate({"top":-45,"opacity":"0.2"},500,function(){
						obj.addClass("loved").next().remove();
					});
				}else if(data.errorNo == "1"){
					$('<div class="love-finish"><div class="lbg"></div><div class="cbg">已收藏</div><div class="rbg"></div><div class="arr"></div></div>').insertAfter(obj).animate({"top":-45,"opacity":"0.2"},500,function(){
						obj.addClass("loved").next().remove();
					});
				}
			}
		});
	},
	addFavById : function(obj,id){
		$.ajax({
			url : "/member/ajax/addFavorites.htm",
			data: {goodsId : id},
			type : "POST",
			dataType : "json",
			async: false,
			cache: false,
			success : function(data) {
				if (data.errorNo == "0") {
					globalHasGetFavorite = false;
					obj.addClass("loved");
					/*
					$('<div class="love-finish"><div class="lbg"></div><div class="cbg">收藏成功</div><div class="rbg"></div><div class="arr"></div></div>').insertAfter(obj).animate({"top":-45,"opacity":"0.2"},500,function(){
						obj.addClass("loved").next().remove();
					});
					*/
				}else if(data.errorNo == "1"){
					obj.addClass("loved");
					/*
					$('<div class="love-finish"><div class="lbg"></div><div class="cbg">已收藏</div><div class="rbg"></div><div class="arr"></div></div>').insertAfter(obj).animate({"top":-45,"opacity":"0.2"},500,function(){
						obj.addClass("loved").next().remove();
					});
					*/
				}
			}
		});
	}
};

/*计数器*/
(function($){
	$.countDown = function(settings){
		var sets = $.extend({},$.countDown.settings,settings);
		$(sets.holdplace).html(sets.begin);
		if(sets.begin > sets.end){
			sets.obj = setInterval(function(){
				if(sets.begin > sets.end){
					sets.begin--;
					$(sets.holdplace).html(sets.begin);
				}else{
					sets.finished();
					clearInterval(sets.obj);
				}	
			},sets.time);
		}else{
			sets.obj = setInterval(function(){
				if(sets.begin < sets.end){
					sets.begin++;
					$(sets.holdplace).html(sets.begin);
				}else{
					sets.finished();
					clearInterval(sets.obj);
				}	
			},sets.time);
		}
	}
	
	$.countDown.settings = {
		obj: null,
		begin: 5,
		end: 0,
		holdplace: "#count",
		time: 1000,
		finished: function(){}
	};
})(jQuery);

/*2015新版*/
$(function(){
	/*滚轮事件*/
	$(window).scroll(function(){
		var scrollTop = $(window).scrollTop();
		if(scrollTop > 610){
			$(".j_wrapCat").show();
		}else{
			$(".j_wrapCat").fadeOut(200);
		}
	});
	/*顶部下拉*/
	$(".j_slideSite").mouseenter(function(){
		$(this).find(".j_site").show();
	}).mouseleave(function(){
		$(this).find(".j_site").hide();
	});
	$(".j_slidePanel").mouseenter(function(){
		$(this).find(".j_panel").slideDown(100);
	}).mouseleave(function(){
		$(this).find(".j_panel").slideUp(100);
	});
	/*购物车下拉*/
	$(".j_myCart").mouseenter(function(){
		$(this).find(".j_shopCart").show();
		if($(".j_cartList").html().indexOf("cart-gopay") == -1 || globalHasGetCart == false){
			getGlobalCart();
		}
		$(this).find(".j_cartList").slideDown(200);
	}).mouseleave(function(){
		$(this).find(".j_shopCart,.j_cartList").hide();
	});

	$(".j_shopCart").click(function(){
		location.href = "/shopCart/cart.htm";
	});

	/*全部分类*/
	$(".j_allCat").live("mouseenter",function(){
		$(this).find(".j_extendCat").stop(true,true).slideDown(200);
	}).live("mouseleave",function(){
		$(this).find(".j_extendCat").stop(true,true).slideUp(200);
	});
	/*左侧分类*/
	$(".j_switchCat").live("mouseenter",function(){
		if(parseInt($(this).find(".j_catExtend").css("left"))==210){
			$(this).find(".j_catExtend").fadeIn(100).stop(true,true).animate({"left":210},100,function(){
				$(".j_catExtend").css("left",210);
			});
		}else{
			$(this).find(".j_catExtend").show();
		}
	}).live("mouseleave",function(){
		$(this).find(".j_catExtend").hide();
	});
	$(".j_catBox").live("mouseleave",function(){
		$(".j_catExtend").css("left",210);
	});
	/*在线客服*/
	$(".j_wrapOnline").mouseenter(function(){
		$(".j_wrapExtend").fadeIn(50);
		$(".j_wrapTxt").addClass("sel");
	}).mouseleave(function(){
		$(".j_wrapExtend").fadeOut(50);
		$(".j_wrapTxt").removeClass("sel");
	});
	/*返回顶部*/
	$(".j_goTop").click(function(){
		$('body,html').stop(true,true).animate({scrollTop:0},500); 
	});
	/*点击页面*/
	$("body").click(function(event){
		var event = event ? event : (window.event ? window.event : null);
		var obj = event.target||event.srcElement;
		if(obj.className!="ls-select"&&obj.className!="ls-sname"&&obj.className!="ls-ico"){
			$(".ls-select").addClass("h24").find(".ls-class").hide();
		}
		if(obj.className.indexOf("j_notBody")==-1&&obj.className.indexOf("j_searchInput")==-1){
			$(".j_searchHot").hide();
		}
	});
});