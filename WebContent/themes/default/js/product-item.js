/***********各种参数******************/
var imgView = {
	flipNum:null,//图片列表可翻页数
	flipNow:1,//当前翻到第几页
	imgNum:null,//图片数量
	combImgWidth:null,//组合购买商品块宽度
	plusBlockWidth:null,//加号的宽度
	imgPageNum:5,   //每页显示的商品图片数
	imgFlipWidth:null,	 //每次翻页距离
	imgOffset:null,      //中图框相对页面的位置
	viewWidth:null,     //大图浏览框的宽度
	viewHeight:null,   //大图浏览框的高度
	largeImgSize:800,  //大图的尺寸
	middleViewSize:null
}

var itemArgs = {  //商品参数
	maxAmount : 999,//最大购买数量
	nowAmount :1,   //当前购买数量
	rateScore :null,//商品综合评分   
	maxScore:5,
	goodId:null
}
var otherArgs = {  //其他交互参数
	switchOffset: null,
	beginInterval:null,
	endTime:null,
	sysTime:null,
	isadd:true
}
var countDown = {
	day:null,
	hour:null,
	minute:null,
	second:null,
	leftSec:null,
	interVal:null,
	init:function(d,h,m,s){
		countDown.day = d;
		countDown.hour = h;
		countDown.minute = m;
		countDown.second = s;
	},
	countDown:function(endtime,nowtime){
		countDown.leftSec=Math.floor((endtime - nowtime.getTime())/1000);
		if(countDown.leftSec > 0){
			countDown.loop();
			countDown.interVal = setInterval(countDown.loop,1000);
		}else{
			countDown.day.html("0");
			countDown.hour.html("0");
			countDown.minute.html("0");
			countDown.second.html("0");
		}
	},
	loop:function(){
		if(countDown.leftSec<=0){
			clearInterval(countDown.interVal);
		}else{
			countDown.leftSec --;
			var sec_d=Math.floor((countDown.leftSec/3600)/24);
			var sec_h=Math.floor((countDown.leftSec/3600))%24;
			var sec_m=Math.floor((countDown.leftSec/60))%60;
			var sec_s=Math.floor(countDown.leftSec)%60;
			countDown.day.html(sec_d);
			countDown.hour.html(sec_h);
			countDown.minute.html(sec_m);
			countDown.second.html(sec_s);
		}
	}
}
/**************各种方法***********************/
var productItem = {
	addFavorites : function(obj){
		$.ajax({
			url : "/member/ajax/addFavorites.htm",
			data: {goodsId : obj},
			type : "POST",
			dataType : "json",
			async: false,
			cache: false,
			success : function(data) {
				$(".popmsgbg").remove();
				$(".popmsg").remove();
				if (data.errorNo == "0") {
					globalHasGetFavorite = false;
					$(".j_favDown").replaceWith("<span>收藏成功</span>");
					add2favPop();

				}else if(data.errorNo == "1"){
					$(".j_favDown").replaceWith("<span>已收藏</span>");
				}
			},
			error : function() {
				$(".popmsgbg").remove();
				$(".popmsg").remove();
				$(".j_favDown").replaceWith("<span>收藏失败</span>");
			}
		});
	}
}
function argsReformat($targets){
	var length = $targets.length;
	if(length >0){
		for(var i = 0; i<length; i++){
			var content = $targets.eq(i).html();
			var patt = /[^u4E00-u9FA5]/;
			var temp = content.replace(/：/g,"");
			if(temp.length == 2){
				var newStr = temp.substring(0,1)+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+temp.substring(1,2);
				$targets.eq(i).html(newStr+"：");
			}else if(temp.length == 3){
				var newStr = temp.substring(0,1)+"&nbsp;&nbsp;"+temp.substring(1,2)+"&nbsp;&nbsp;" + temp.substring(2,3);
				$targets.eq(i).html(newStr+"：");
			}
		}
	}
}
function selectFormat($targets){
	var length = $targets.length;
	if(length > 0){
		for(var i =0; i<length; i++){
			var content = $targets.eq(i).html();
			var temp = content.replace(/：/g,"");
			var patt = /[^u4E00-u9FA5]/;
			if(temp.length ==2){
				var newStr = temp.substring(0,1) + "&nbsp;&nbsp;&nbsp;"+temp.substring(1,2);
				$targets.eq(i).html(newStr);
			}
		}
	}
}
function myBrowser(){
	var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
	var isOpera = userAgent.indexOf("Opera") > -1; //判断是否Opera浏览器
	var isIE = userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1 && !isOpera ; //判断是否IE浏览器
	var isFF = userAgent.indexOf("Firefox") > -1 ; //判断是否Firefox浏览器
	var isSafari = userAgent.indexOf("Safari") > -1 ; //判断是否Safari浏览器
	if(isIE){
	   var start = userAgent.indexOf("MSIE");
	   var ver = userAgent.slice(start+5 , start+8);
	   return ver;
	}
	return "not IE";
}
/**
 * 初始化促销的中限时特价的倒计时<br/>
 * 1. 首先获取标签中倒计时结束时间，如果获取到结束时间为空则return。
 * 2. 通过ajax异步获取服务器当前时间，时间的获取都是Long类型。
 * 3. 计算结束时间和服务器当前时间的差限时倒计时
 * @returns {Boolean}
 */
function initProSaleTime(){
	otherArgs.endTime = $(".j_v_d_h_time").attr("data-time");
	if(otherArgs.endTime==undefined || otherArgs.endTime.length < 1){
		return false;
	}
	var start_rq_time = new Date().getTime(),end_rq_time,diff_end_star=0;
	$.ajax({
		type: "POST",
		url: "/product/ajax/getSysTime.htm",
		dataType: "json",
		success: function(data){
			otherArgs.sysTime= data["sysTime"];
			end_rq_time = new Date().getTime();
			diff_end_star = (end_rq_time - start_rq_time);
			otherArgs.sysTime = (otherArgs.sysTime-diff_end_star);
			otherArgs.beginInterval= setInterval(sec_time,1000);
		}
	});
}
function sec_time(){
	if(otherArgs.isadd==false){
		otherArgs.sysTime = (otherArgs.sysTime + 1000);
	}else{
		otherArgs.isadd=false;
	}
	var subsecond=parseInt((otherArgs.endTime-otherArgs.sysTime)/1000);
	if(subsecond > 0){
		var sec_d=parseInt((subsecond/3600)/24);
		var sec_h=parseInt((subsecond/3600))%24;
		var sec_m=parseInt((subsecond/60))%60;
		var sec_s=parseInt(subsecond)%60;
		$(".j_v_d_h_time").empty();
		$(".j_v_d_h_time").append("剩余");
		if(sec_d > 0){
			$(".j_v_d_h_time").append("<span>"+sec_d+"</span>天");
		}
		if(sec_h > 0){
			$(".j_v_d_h_time").append("<span>"+sec_h+"</span>小时");
		}
		if(sec_m > 0){
			$(".j_v_d_h_time").append("<span>"+sec_m+"</span>分");
		}
		$(".j_v_d_h_time").append("<span>"+sec_s+"</span>秒");
	}else{
		clearInterval(otherArgs.beginInterval);
		$(".j_v_d_h_time").html("已结束");
	}
} 
function imgViewInit(){ //参数初始化
	var lth = $(".J-pl-prap li").length;
	imgView.imgNum = lth;
	imgView.flipNum = Math.ceil(lth/imgView.imgPageNum);
	imgView.combImgWidth = parseInt($(".J-comb .pro-item").css("width"));
	imgView.plusBlockWidth = parseInt($(".J-comb .plus-block").css("width"));
	var simgWidth =  parseInt($(".J-pl-prap li").css("width"));
	var mr = parseInt($(".J-pl-prap li").css("margin-right"));
	var borderWidth = parseInt($(".J-pl-prap li").css("border-width"));
	imgView.imgFlipWidth = (simgWidth+mr +2*borderWidth) * imgView.imgPageNum;
	imgView.imgOffset = $(".J-proimg").offset();
	imgView.viewHeight = parseInt($(".J-large-view").css("height"));
	imgView.viewWidth = parseInt($(".J-large-view").css("width"));
	imgView.middleViewSize = parseInt($(".J-proimg").css("width"));
}
function productArgsInit(){
	var maxAmount = parseInt($(".J_buynum").attr("max-amount"));
	if(maxAmount < 999){
		itemArgs.maxAmount = maxAmount;
	}
	itemArgs.rateScore = parseFloat($(".score").attr("score"));
}
function combineInit(){ //组合购买宽度初始化
	var boxSet = $(".J-comb .combine-list");
	var lth = boxSet.length;
	for(var i =0; i<lth;i++){
		var itSet = boxSet.eq(i).find(".pro-item");
		var amt = itSet.length;
		width = (imgView.combImgWidth+imgView.plusBlockWidth)*amt - imgView.plusBlockWidth+10;
		boxSet.eq(i).css({"width":width})
	}
}
function pageInit(){  // 页面布局初始化函数
	otherArgs.switchOffset = $(".J-switch-bar").offset();
	$(".navbar").css("width",1070);
}
function viewComment(){
	var top = $("#comment").offset().top;
	$('body,html').animate({scrollTop:top -60},500);
}
function imgFlip($obj){  // 图片列表翻页
	var dir;
	if($obj.hasClass("proimg-next")){
		dir = 1;
	}else{
		dir = -1;
	}
	var $ul = $obj.parents(".proimg-list").find(".J-pl-prap ul");
	var marginNow = parseInt($ul.css("margin-left"));
	var targetMargin = marginNow - dir * imgView.imgFlipWidth;
	$ul.animate({"margin-left":targetMargin},200);
	imgView.flipNow +=dir;
	if(dir ==1){
		$obj.parent().find(".proimg-prev").removeClass("disable");
	}else{
		$obj.parent().find(".proimg-next").removeClass("disable");
	}
	if(imgView.flipNow ==1){
		$obj.parent().find(".proimg-prev").addClass("disable");
	}
	if(imgView.flipNow ==imgView.flipNum){
		$obj.parent().find(".proimg-next").addClass("disable");
	}
}
function imgMove(x,y){  //大图移动浏览
	var oriX = imgView.viewWidth /2;
	var oriY = imgView.viewHeight /2;
	var adjX =( imgView.largeImgSize/imgView.middleViewSize)*x;
	var adjY = ( imgView.largeImgSize/imgView.middleViewSize)*y;
	adjX -= oriX;
	adjY -= oriY;
	var xTop = imgView.largeImgSize - imgView.viewWidth;
	var yTop = imgView.largeImgSize - imgView.viewHeight;
	if(adjX < 0){ // x下限
		adjX = 0;
	}else if(adjX > xTop){ // x上限
		adjX = xTop;
	}
	if(adjY < 0){   // y下限
		adjY = 0;
	}else if( adjY > yTop){   // y上限
		adjY = yTop;
	}
	$(".J-large-view img").css({"top":-adjY,"left":-adjX});
}
function onlyNumberFilter(str){//过滤非数字
	var patt = /[^0-9]/g;
	var check_val = str.replace(patt,"");
 	return check_val;
}
function storageAlert(str){
	$(".J-storage-alert").html(str).fadeIn(200);
}
function storageAlertFade(){
	$(".J-storage-alert").fadeOut(200);
}
function amtCheck(num,$obj){  //检查购买数量
	var returnVal = 0;
	if(""== num){
		$obj.val(1);
		returnVal = 1;
	}else{
		if(typeof num == "string"){
			num = parseInt(num);
		}
		if(num >= itemArgs.maxAmount){
			$obj.val(itemArgs.maxAmount);
			var limitType = $(".instore").attr("_limit");
			if(limitType =="1"){
				// storageAlert("要超过库存了");
				// setTimeout(storageAlertFade,2000);
			}else if(limitType =="0"){
				storageAlert("达到限购数量了");
				setTimeout(storageAlertFade,2000);
			}
			returnVal = 2;
		}else if(num  == itemArgs.maxAmount){
			returnVal = 2;
		}else if(1==num){
			returnVal = 1; 
		}else{
			storageAlertFade();
		}
	}
	return returnVal;
}
function numButtonAdj(val,$obj){  //增减按钮 disable调整
	switch(val){
	case 1:
		$obj.find(".numMinus").addClass("disable");
		$obj.find(".numPlus").removeClass("disable");
		break;
	case 2:
		$obj.find(".numPlus").addClass("disable");
		$obj.find(".numMinus").removeClass("disable");
		break;
	default:
		$obj.find(".J_numAdj").removeClass("disable");
	}
}
function amountAdj($obj){ // 购买数量增减
	var val =parseInt($obj.parent().find(".J_buynum").val());
	if(val > 0){
		var incre;
		if($obj.hasClass("numPlus")){
			incre = 1;
		}else{
			incre = -1;
		}
		var val =parseInt($obj.parent().find(".J_buynum").val());
		val +=incre;
		$obj.parent().find(".J_buynum").val(val);
	}
	return val;
}
function rateScoreAnimate(rateScore){
	var des_deg = (rateScore/itemArgs.maxScore)*360;
	var $circle1 = $(".half-circle");
	var $circle2 = $(".other-half-circle");
	var $score = $(".score-info .score");
	var degCount = 0; 
	var score = 0.0;
	var interval1;
	var interval2;
	var scoreInter;
	var browserVersion = myBrowser();
	function loop(){
		$circle1.css({"-webkit-transform":"rotate("+degCount+"deg)","transform":"rotate("+degCount+"deg)","-ms-transform":"rotate("+degCount+"deg)","-o-tranform":"rotate("+degCount+"deg)","-moz-transform":"rotate("+degCount+"deg)"});
		degCount++;
		if(degCount >= des_deg){
			clearInterval(interval1);
		}
		if(degCount >= 180){
			$circle2.css({"-webkit-transform":"rotate("+degCount+"deg)","transform":"rotate("+degCount+"deg)","-ms-transform":"rotate("+degCount+"deg)","-o-tranform":"rotate("+degCount+"deg)","-moz-transform":"rotate("+degCount+"deg)"});
			clearInterval(interval1);
			$circle2.css({"z-index":4});
			interval2 = setInterval(loop2,2);
		}
	}
	function loop2(){
		$circle2.css({"-webkit-transform":"rotate("+degCount+"deg)","transform":"rotate("+degCount+"deg)","-ms-transform":"rotate("+degCount+"deg)","-o-tranform":"rotate("+degCount+"deg)","-moz-transform":"rotate("+degCount+"deg)"});
		degCount++;
		if(degCount >= des_deg){
			clearInterval(interval2);
		}
	}
	function scoreLoop(){
		if(score >=rateScore){
			clearInterval(scoreInter);
			$score.html(rateScore.toFixed(1));
			return false;
		}		
		score += 0.1;
		$score.html(score.toFixed(1));
	}
	if((browserVersion !="6.0")&&(browserVersion !="7.0")&&(browserVersion !="8.0"))
	interval1 = setInterval(loop,2);
	scoreInter = setInterval(scoreLoop,40);
}
function switchContent(id){
	if(id == 1){
		if($(".detail-block.service-instru").hasClass("dis_n")){
			$(".detail-block.service-instru").removeClass("dis_n");
		}
		$(".detail-switch .switch-items.on").removeClass("on");
		$(".detail-switch .switch-items.pro-attribute").addClass("on");
		$(".switch-items.pro-instru").addClass("on");
	}else if(id ==2){
		if($(".detail-block.service-instru").hasClass("dis_n")){
			$(".detail-block.service-instru").removeClass("dis_n");
		}
		$(".switch-items.on").removeClass("on");
		$(".pro-args.switch-items").addClass("on");
	}else if(id ==3){
		$(".detail-block.service-instru").addClass("dis_n");
		$(".switch-items.on").removeClass("on");
	}else if(id == 4){
		if($(".detail-block.service-instru").hasClass("dis_n")){
			$(".detail-block.service-instru").removeClass("dis_n");
		}
		$(".switch-items.on").removeClass("on");
		$(".package-list.switch-items").addClass("on");
	}else if(id == 5){
		if($(".detail-block.service-instru").hasClass("dis_n")){
			$(".detail-block.service-instru").removeClass("dis_n");
		}
		$(".switch-items.on").removeClass("on");
		$(".service.switch-items ").addClass("on");
	}
}
function getArgsList(){
	var selSet = $(".select-item.selected");
	var lth = selSet.length;
	var returnVal= "";
	for(var i= 0; i<lth; i++){
		if(returnVal.length > 0){
			temp = selSet.eq(i).find(".select-info").attr("data-id");
			returnVal =returnVal +"_"+temp;
		}else{
			returnVal = selSet.eq(i).find(".select-info").attr("data-id");
		}
	}
	return returnVal;
}
function add2favPop(){
	$(".add2fav-wrap").css({top:($(window).height()-$(".add2fav-wrap").height())/2+document.body.scrollTop + document.documentElement.scrollTop,left:($(window).width()-$(".add2fav-wrap").width())/2}).fadeIn(100);
	$(".add2fav-bkg").fadeIn(100);
}
function add2favFade(){
	$(".add2fav-wrap").fadeOut(100);
	$(".add2fav-bkg").fadeOut(100);
}
function countInit(){
	var $promo = $("#promoteEndTime");
	if($promo.length != 0 ){
		var proEndTime = parseInt($promo.val());
		var proNowTime=new Date();
		countDown.init($(".leftDay"),$(".leftHour"),$(".leftMin"),$(".leftSec"));
		countDown.countDown(proEndTime,proNowTime);
	}
}
/*************触发*******************/
$(function(){
	//原24home图片路径动态替换
	$(".pro-instru img").each(function(){
		if($(this).attr("src").indexOf("24home.") != -1){
			var src=$(this).attr("src");
			$(this).attr("src",$(this).attr("src").replace("24home.","asj."));
		}
	});
	changeAddressWidth();
	var imgH = $(".j_imageInfo").height();
	var proH = $(".j_productInfo").height()+16;
	var othH = $(".j_otherInfo").height();
	proH = (imgH > proH)?imgH:proH;
	othH = (othH > proH)?othH:proH;
	$(".j_imageInfo,.j_otherInfo").css("height",othH);
	$(".j_productInfo").css("height",othH-52);
	$(".j_favIcons").mouseenter(function(){
		$(this).find(".j_shareTxt").hide();
		$(this).find(".j_shares").show();
	}).mouseleave(function(){
		$(this).find(".j_shares").hide();
		$(this).find(".j_shareTxt").show();
	});	
	imgViewInit();
	combineInit();
	pageInit();
	productArgsInit();
	countInit();
	rateScoreAnimate(itemArgs.rateScore);
	var $targets = $(".pro-attribute .attr-head");
	argsReformat($targets);
	var $tobj = $(".select .item-list dt");
	selectFormat($tobj);
	initProSaleTime();
	/*到货通知*/
	$(".j_arrival").live("mouseenter",function(){
		var $t = $(this);
		if($t.find(".j_arrivalPop").length==0){
			$.ajax({
				type : "post",
				url: "/commonAjax/getBaseMemberInfo.htm",
				async:false,
				dataType : "json",
				success: function(data) {
					var result = data["result"];
					var popHtml = "";
					if(result == false){
						//未登录
						popHtml = '<div class="arr"></div><div class="tbg"></div><div class="mbg"><div class="tip"><a class="close j_arrivalClose">×</a></div><div class="con">您需要先<a class="login j_arrivalLogin">登录</a>，才可登记信息哦！</div></div><div class="bbg"></div>';
					}else{
						var email = "邮箱",mobile = "手机";
						if(result.email != ""){
							email = result.email;
						}
						if(result.mobile != ""){
							mobile = result.mobile;
						}
						popHtml = '<div class="arr"></div><div class="tbg"></div><div class="mbg"><div class="tip"><a class="close j_arrivalClose">×</a></div><div class="con j_arrivalCon">我们将在商品到货后第一时间通知您！<ul><li><input type="checkbox" checked class="chk j_emailSelected" /><input type="text" class="ipt j_email" value="'+email+'" /></li><li><input type="checkbox" checked class="chk j_mobileSelected" /><input type="text" class="ipt j_mobile" value="'+mobile+'" /></li><li><a class="btn j_arrivalSub">确定</a></li></ul></div></div><div class="bbg"></div>';
					}
					$('<div class="arrival-pop j_arrivalPop">'+popHtml+'</div>').appendTo($t);					
				}
			});
		}else{
			$t.find(".j_arrivalPop").show();
		}
	});
	$(".j_arrivalPop").live("mouseleave",function(e){
		$(this).hide();
	});
	$(".j_arrivalClose").live("click",function(){
		$(this).parents(".j_arrivalPop").remove();
	});
	$(".j_arrivalLogin").live("click",function(){
		var pid = $(this).parents(".j_info").attr("data-pid");
		popupCheckCross(productItem.arrivalNotice,pid);
	});
	$(".j_emailSelected,.j_mobileSelected").live("click",function(){
		if(!$(this).parents(".j_arrivalCon").find(".j_emailSelected").is(":checked")&&!$(this).parents(".j_arrivalCon").find(".j_mobileSelected").is(":checked")){
			$(this).parents(".j_arrivalCon").find(".j_arrivalSub").hide();
		}else{
			$(this).parents(".j_arrivalCon").find(".j_arrivalSub").show();
		}
	});
	$(".j_arrivalSub").live("click",function(){
		var $chkemail = $(this).parents(".j_arrivalCon").find(".j_emailSelected");
		var $chkmobile = $(this).parents(".j_arrivalCon").find(".j_mobileSelected");
		var emailval = $(this).parents(".j_arrivalCon").find(".j_email").val().replace("邮箱","");
		var mobileval = $(this).parents(".j_arrivalCon").find(".j_mobile").val().replace("手机","");
		var phoneReg = /^0?(13|14|15|17|18)[0-9]{9}$/;
		var emailReg = /^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/;
		var canSub = true;
		var productId = $("#productId").val();
		var productName = $("#productName").val();
		var $this = $(this);
		if(!$chkemail.is(":checked")&&!$chkmobile.is(":checked")){
			canSub = false;
			if($(".j_arrivalCon li").eq(0).find(".j_err").length == 0){
				$('<div class="err j_err">邮箱、手机至少选择一项</div>').appendTo($(".j_arrivalCon li").eq(0));
			}else{
				$(".j_arrivalCon li").eq(0).find(".j_err").html("邮箱、手机至少选择一项");
			}
		}
		if($chkemail.is(":checked")){
			if(emailval == ""){
				canSub = false;
				if($(".j_arrivalCon li").eq(0).find(".j_err").length == 0){
					$('<div class="err j_err">请输入邮箱地址</div>').appendTo($(".j_arrivalCon li").eq(0));
				}else{
					$(".j_arrivalCon li").eq(0).find(".j_err").html("请输入邮箱地址");
				}
			}else{
				if(!emailReg.test(emailval)){
					canSub = false;
					if($(".j_arrivalCon li").eq(0).find(".j_err").length == 0){
						$('<div class="err j_err">邮箱地址有误</div>').appendTo($(".j_arrivalCon li").eq(0));
					}else{
						$(".j_arrivalCon li").eq(0).find(".j_err").html("邮箱地址有误");
					}
				}
			}
		}else{
			emailval = "";
		}
		if($chkmobile.is(":checked")){
			if(mobileval == ""){
				canSub = false;
				if($(".j_arrivalCon li").eq(1).find(".j_err").length == 0){
					$('<div class="err j_err">请输入手机号</div>').appendTo($(".j_arrivalCon li").eq(1));
				}else{
					$(".j_arrivalCon li").eq(1).find(".j_err").html("请输入手机号");
				}
			}else{
				if(!phoneReg.test(mobileval)){
					canSub = false;
					if($(".j_arrivalCon li").eq(1).find(".j_err").length == 0){
						$('<div class="err j_err">手机号有误</div>').appendTo($(".j_arrivalCon li").eq(1));
					}else{
						$(".j_arrivalCon li").eq(1).find(".j_err").html("手机号有误");
					}
				}
			}
		}else{
			mobileval = "";
		}
		if(canSub == true){
			$.getJSON("/commonAjax/addProductRegist.htm",{email:emailval,mobile:mobileval,productId:productId,productName:productName},function(data){
				var subresult = data["result"];
				var replaceHtml = "";
				switch(subresult){
				case "unlogin":
					$(".j_arrivalPop").html('<div class="arr"></div><div class="tbg"></div><div class="mbg"><div class="tip"><a class="close j_arrivalClose">×</a></div><div class="con">登录超时，您需要重新<a class="login j_arrivalLogin">登录</a>再登记！</div></div><div class="bbg"></div>');
					break;
				case "empty":
					
					break;
				case "success":
					replaceHtml = "<h3><s></s>订阅成功</h3>";
					if(emailval!=""){
						replaceHtml += "<p>我们会将到货信息及时发送至您邮箱：<span class='j_tipEmail'>"+emailval+"</span><a class='j_editPop'>修改</a></p>";
						if(mobileval!=""){
							replaceHtml += "<p>同时发送至您手机：<span class='j_tipMobile'>"+mobileval+"</span><a class='j_editPop'>修改</a></p>";
						}
					}else{
						replaceHtml += "<p>我们会将到货信息及时发送至您手机：<span class='j_tipMobile'>"+mobileval+"</span><a class='j_editPop'>修改</a></p>";
					}
					$(".j_arrivalPop").html('<div class="arr"></div><div class="tbg"></div><div class="mbg"><div class="tip"><span class="j_tips">5</span>秒后关闭<a class="close j_arrivalClose">×</a></div><div class="con">'+replaceHtml+'</div></div><div class="bbg"></div>');
					globalCountDown = setInterval('if(globalCountNow > 0){$(".j_tips").html(globalCountNow);globalCountNow--;}else{$(".j_arrivalPop").remove();clearInterval(globalCountDown);globalCountNow = 4;}',1000);
					break;
				case "unsuccess":
					break;
				}
			});
		}
	});
	$(".j_editPop").live("click",function(){
		var e = $(this).parents(".j_arrivalPop").find(".j_tipEmail").html();
		if(e == null) e = "";
		var m = $(this).parents(".j_arrivalPop").find(".j_tipMobile").html();
		if(m == null) m = "";
		var echk = " checked",mchk = " checked";
		if(e == ""){
			echk = "";
			e = "邮箱";
		}
		if(m == ""){
			mchk = "";
			m = "手机";
		}
		$(this).parents(".j_arrivalPop").html('<div class="arr"></div><div class="tbg"></div><div class="mbg"><div class="tip"><a class="close j_arrivalClose">×</a></div><div class="con j_arrivalCon">我们将在商品到货后第一时间通知您！<ul><li><input type="checkbox"'+echk+' class="chk j_emailSelected" /><input type="text" class="ipt j_email" value="'+e+'" /></li><li><input type="checkbox"'+mchk+' class="chk j_mobileSelected" /><input type="text" class="ipt j_mobile" value="'+m+'" /></li><li><a class="btn j_arrivalSub">确定</a></li></ul></div></div><div class="bbg"></div>');
	});
	$(".j_email").live("focus",function(){
		if($(this).val()=="邮箱"){
			$(this).val("");
		}
	}).live("blur",function(){
		if($(this).val()==""){
			$(this).val("邮箱");
		}
	});
	$(".j_mobile").live("focus",function(){
		if($(this).val()=="手机"){
			$(this).val("");
		}
	}).live("blur",function(){
		if($(this).val()==""){
			$(this).val("手机");
		}
	});
	$(".J-pl-prap li").mouseenter(function(){
		if(!$(this).hasClass("on")){
			var liSet = $(".J-pl-prap li");
			liSet.siblings(".on").removeClass("on");
			$(this).addClass("on");
			var q  = liSet.index($(this));
			var imgSet = $(".J-proimg img");
			imgSet.siblings(".on").removeClass("on");
			imgSet.eq(q).addClass("on");
		}
	});
	$(".J-proimg-nav").click(function(){
		if(!$(this).hasClass("disable")){
			imgFlip($(this)); 
		}
		return false;
	});
	$(".J-proimg img").mouseenter(function(){
		$(this).parent().parent().css("z-index",7);
		var src = $(this).attr("data-large");
		var img = '<img src="'+src+'" width="'+imgView.largeImgSize+'" height="'+imgView.largeImgSize+'" />';
		$(".J-large-view").html(img).stop(true,true).fadeIn(200);
	}).mouseleave(function(){
		$(this).parent().parent().css("z-index",3);
		$(".J-large-view").stop(true,true).hide();
	}).mousemove(function(e){
		var y = e.pageY;
		var x = e.pageX;
		coorX = x - imgView.imgOffset.left;
		coorY = y - imgView.imgOffset.top;
		imgMove(coorX,coorY);
	});

	$(".j_colorSelect li").mouseenter(function(){
		if($(this).find(".j_colorImg").length==0){
			var $t = $(this);
			var ids = $("#goodId").val()+"-"+$(this).find("a").attr("data-id");
			$(".j_selectList:not(.j_colorSelect) li.selected").each(function(){
				ids += "_"+$(this).find("a").attr("data-id");
			});
			$.ajax({
				type: "get",
				data: {ids:ids},
				url: global_appServer + '/commonAjax/getImageForProductDetail.htm',
				async: true,
				dataType: "json",
				success: function(data){
					var result = data["imagelist"];
					$("<img class='j_colorImg' src='"+global_imageServer+"/goodsImage/70X70/"+result[0].path+"' onerror=this.src='"+global_imageServer+"/images/2015/noimg/70.jpg' width='40' height='40'>").appendTo($t.find("a"));
				}	
			});
		}
		$(this).find(".txt").css("z-index",3).fadeIn(50);
	}).mouseleave(function(){
		$(this).find(".txt").css("z-index",1).hide();
	}).click(function(){
		if(!$(this).hasClass("selected")){
			$(this).parent().find("li.selected").removeClass("selected").find("a.select-info").css({"margin-top":0});
			$(this).addClass("selected");
		}
		return false;
	});
	$(".j_selectItem:not(.selected)").mouseenter(function(){
		$(this).addClass("hover");
	}).mouseleave(function(){
		$(this).removeClass("hover");
	}).click(function(){
		$(this).parent().find("li.selected").removeClass("selected");
		$(this).addClass("selected");
		var argsList = getArgsList();
		var goodId = $("#goodId").val();
		if(goodId != null){
			var jsonSPDB = jsonSpecProductIds["jsonSpecProductIds"];
			var pid = jsonSPDB[argsList];
			if(pid != undefined && pid != ""){
				window.location.href="/product/"+pid+".htm";
			}else{
				window.location.reload();
			}
		}
	});
	$(".J_buynum").keyup(function(){
		var val = $(this).val();
		val = onlyNumberFilter(val);
		$(this).val(val);
	}).blur(function(){
		if(!$(this).attr("readonly")){
			var val =  $(this).val();
			var returnVal = amtCheck(val,$(this));
			var $obj = $(this).parent();
			numButtonAdj(returnVal,$obj);
			itemArgs.nowAmount = parseInt($(this).val());
		}
	}).focus(function(){
		$(".J-storage-alert").hide();
	});
	$(".J_numAdj").click(function(){
		if(!$(this).hasClass("disable")){
			var amt =  amountAdj($(this));
			var $obj = $(this).parent();
			var $target = $obj.find(".J_buynum");
			var returnVal = amtCheck(amt,$target);
			numButtonAdj(returnVal,$obj);
			itemArgs.nowAmount = parseInt($target.val());
		}
		return false;
	});
	$(window).scroll(function(){
		$target = $(".J-switch-bar");
		if(($(window).scrollTop() - otherArgs.switchOffset.top)>0 ){
			if(!$target.hasClass("fixedTop")){
				$target.addClass("fixedTop").find(".j_add2CartBtn").show();
			}
		}else{
			if($target.hasClass("fixedTop")){
				$target.removeClass("fixedTop").find(".j_add2CartBtn").hide();
			}
		}
	});
	$(".J-switch-bar li.switch-item").click(function(){
		if(!$(this).hasClass("on")){
			$(".J-switch-bar li.switch-item.on").removeClass("on");
			$(this).addClass("on");
			var tarId = $(this).attr("data-target");
			switchContent(tarId);
			var pos = $(".J-switch-bar").css("position");
			if(pos !="fixed"){
				return false;
			}
		}else{
			return false;
		}
	});

	$(".post-fee .instrument").mouseenter(function(){
		var data = $(this).attr("_data");
		var offset = $(this).offset();
		popUp.type1.popDisp(data,offset.top+18,offset.left);
	}).mouseleave(function(){
		popUp.type1.popFade();
	});
	$(".J-view-comment").click(function(){
		viewComment();
		return false;
	});
	
	$.ajax({
		type: "get",
		data: {goodId:$("#goodId").val()},
		url: global_appServer + '/favorites/ajax/getcountbygoodid.htm',
		async: true,
		dataType: "json",
		success: function(data){
			$(".j_favDown").html('<span class="collect"></span>收藏商品<span class="num">'+data["count"]+'</span>人');
		}
	});

	$(".so-img-list img").imgview();//图片浏览

/*********add by chuzengwen *************************/	
	$(".j_switch_consul li").click(function(){
		var goodId = $("#goodId").val();
		var type = parseInt($(this).attr("data-val"));
		$(".j_switch_consul li").removeClass("on");
		$(this).addClass("on");
		$.ajax({
			url: bullAppServer+"/product/ajax/advisory.htm",
			data:{"goodId":goodId,"type":type},
			type:'POST',
			dataType:"json",
			success:function(data){
				$(".j_consult").html("");
				$.each(data.result.advisorys,function(item){
					var str="";
					if(this.reply==""){
						 str = "暂无回复";
					}else{
						 str = this.reply; 
					}
					var headPic = "";
					if(data.result['headPic'+this.memberId]!=null && data.result['headPic'+this.memberId]!=""){
						headPic = bullImageServer+'/memberImage/60X60/'+data.result['headPic'+this.memberId];
					}else{
						headPic = bullImageServer+data.result['rankIco'+this.memberId];
					}
					var rankName = "";
					if(this.memberId!=null){
						rankName = data.result['rankMap'+this.memberId];
					}
					var errorImage = bullImageServer+"/images/demo/head_default.jpg";
					$('<li class="user-con-item clearfix">'+
					'<div class="user-info">'+
						'<img class="user-img" src="'+headPic+'" onerror=this.src="'+errorImage+'" width="56" height="56">'+
						'<p class="user-name">'+this.memberName+'</p>'+
						'<p class="user-level">'+rankName+'</p>'+
					'</div>'+
					'<div class="user-message">'+
						'<dl class="user-message-item clearfix cons-ask">'+
							'<dt>'+
								'咨询内容：'+
							'</dt>'+
							'<dd class=" clearfix">'+
								'<div class="cons-content">'+
									''+this.question+''+
								'</div>'+
								'<div class="cons-time">'+new Date(this.gmtCreate).format("yyyy-MM-dd hh:mm:ss")+'</div>'+
							'</dd>'+
						'</dl>'+
						'<dl class="user-message-item clearfix cons-answer">'+
							'<dt>'+
								'客服回复：'+
							'</dt>'+
							'<dd>'+
								'<div class="cons-content">'+
									''+str+''+
								'</div>'+
								'<div class="cons-time">'+new Date(this.gmtReply).format("yyyy-MM-dd hh:mm:ss")+''+
							'</dd>'+
						'</dl>'+
					'</div>'+
				'</li>').appendTo($(".j_consult"));
				});
			},
			error:function(){
			}
		});
	});
	
	$(".cons-keyword").focus(function(){
		$(".ck-search-instru").hide();
	});
	$(".cons-keyword").blur(function(){
		if($(".cons-keyword").val()==""){
			$(".ck-search-instru").show();
		}
	});
	
	$(".add2fav-close").click(function(){
		add2favFade();
		return false;
	});
	//咨询搜索 
	$(".ck-sub").click(function(){
		var goodId = $("#goodId").val();
		$(".j_switch_consul li").removeClass("on");
		$(this).addClass("on");
		var content = $(".cons-keyword").val();
		$.ajax({
			url: bullAppServer+"/product/ajax/advisory.htm",
			data:{"goodId":goodId,"type":0,"content":content},
			type:'POST',
			dataType:"json",
			success:function(data){
				$(".j_consult").html("");
				$.each(data.result.advisorys,function(item){
					var str="";
					if(this.reply==""){
						str = "暂无回复";
					}else{
						str = this.reply; 
					}
					var headPic = "";
					if(data.result['headPic'+this.memberId]!=null && data.result['headPic'+this.memberId]!=""){
						headPic = bullImageServer+'/memberImage/60X60/'+data.result['headPic'+this.memberId];
					}else{
						headPic = bullImageServer+data.result['rankIco'+this.memberId];
					}
					var rankName = "";
					if(this.memberId!=null){
						rankName = data.result['rankMap'+this.memberId];
					}
					var errorImage = bullImageServer+"/images/demo/head_default.jpg";
					$('<li class="user-con-item clearfix">'+
						'<div class="user-info">'+
							'<img class="user-img" src="'+headPic+'" onerror=this.src="'+errorImage+'" width="56" height="56">'+
							'<p class="user-name">'+this.memberName+'</p>'+
							'<p class="user-level">'+rankName+'</p>'+
						'</div>'+
						'<div class="user-message">'+
							'<dl class="user-message-item clearfix cons-ask">'+
                            	'<dt>'+
                                	'咨询内容：'+
                                '</dt>'+
                                '<dd class=" clearfix">'+
                                	'<div class="cons-content">'+
                                    	''+this.question+''+
                                    '</div>'+
                                    '<div class="cons-time">'+new Date(this.gmtCreate).format("yyyy-MM-dd hh:mm:ss")+'</div>'+
                                '</dd>'+
							'</dl>'+
							'<dl class="user-message-item clearfix cons-answer">'+
                            	'<dt>'+
                                	'客服回复：'+
                                '</dt>'+
                                '<dd>'+
                                	'<div class="cons-content">'+
                                     	''+str+''+
                                    '</div>'+
                                    '<div class="cons-time">'+new Date(this.gmtReply).format("yyyy-MM-dd hh:mm:ss")+''+
                                '</dd>'+
							'</dl>'+
						'</div>'+
					'</li>').appendTo($(".j_consult"));
				});
			},
			error:function(){
			}
		});
	});
	
	/*选择地址*/
	$(".j_addressSelect").mouseenter(function(){
		$(".j_addressVals").hide();
		$(".j_addressPop").show();
		changeAddressWidth();
	}).mouseleave(function(){
		$(".j_addressVals").show();
		$(".j_addressPop").hide();
	});
	$(".j_addressClose").click(function(){
		changeAddressWidth();
		$(".j_addressSelect").unbind("mouseleave");
		$(".j_addressVals").show();
		$(".j_addressPop").hide();
	});
	$(".j_provinceList a").click(function(){
		$(".j_addressSelect").unbind("mouseleave");
		var $t = $(this);
		var id = $t.attr("data-id");
		var txt = $t.html();
		$(".j_province").html(txt+"<b></b>").attr("data-id",id).removeClass("sel");
		$(".j_provinceId").val(id);
		$(".j_city").html("请选择<b></b>").addClass("sel");
		$(".j_area").html("");
		changeAddressWidth();
		$.ajax({
			type: "POST",
			url: "/member/ajax/getArea.htm",
			dataType: "json",
			data: "id="+id,
			success: function(data){
				var result = data.data;
				if(result.length>0){
					$(".j_cityList").html("");
					var html = "";
					for(var i=0;i<result.length;i++){
						var val = result[i]["name"];
						var aid = result[i]["areaId"]
						html += '<a data-id="'+aid+'">'+val+'</a>';
					}
					$(".j_provinceList").hide();
					$(".j_cityList").html(html).show();
				}
			}
		});
	});
	$(".j_cityList a").live("click",function(){
		$(".j_addressSelect").unbind("mouseleave");
		var $t = $(this);
		var id = $t.attr("data-id");
		var txt = $t.html();
		$(".j_city").html(txt+"<b></b>").attr("data-id",id).removeClass("sel");
		$(".j_cityId").val(id);
		$(".j_area").html("请选择<b></b>").addClass("sel");
		changeAddressWidth();
		$.ajax({
			type: "POST",
			url: "/member/ajax/getArea.htm",
			dataType: "json",
			data: "id="+id,
			success: function(data){
				var result = data.data;
				if(result.length>0){
					$(".j_areaList").html("");
					var html = "";
					for(var i=0;i<result.length;i++){
						var val = result[i]["name"];
						var aid = result[i]["areaId"]
						html += '<a data-id="'+aid+'">'+val+'</a>';
					}
					$(".j_cityList").hide();
					$(".j_areaList").html(html).show();
				}
			}
		});
	});
	$(".j_areaList a").live("click",function(){
		$(".j_addressSelect").unbind("mouseleave");
		var $t = $(this);
		var id = $t.attr("data-id");
		var txt = $t.html();
		$(".j_area").html(txt+"<b></b>").attr("data-id",id);
		$(".j_areaId").val(id);
		var province = $(".j_province").text();
		var city = $(".j_city").text();
		$(".j_addressVals").html("<span>"+province+"</span> <span class=\"s\">></span> <span>"+city+"</span> <span class=\"s\">></span> <span>"+txt+"</span>");
		changeAddressWidth();
		$(".j_addressClose").click();
		$.ajax({
			url: "/product/ajax/isSell.htm",
			data: {areaId:id, productId:$("#productId").val()},
			dataType: "json",
			async: false,
			cache: false,
			success: function(data) {
				if(data.isSell){
					$(".j_buyNow,.j_addToCart").removeClass("dis");
					$(".j_distri").html("");
				}else{
					$(".j_buyNow,.j_addToCart").addClass("dis");
					$(".j_distri").html("该地区不支持配送");
				}
			}
		});
		var provinceId = $(".j_provinceId").val();
		var cityId = $(".j_cityId").val();
		$.ajax({
			type: "POST",
			url: "/commonAjax/addAreaCookie.htm",
			dataType: "json",
			data: {provinceId:provinceId,cityId:cityId,areaId:id},
			success: function(data){
			}
		});
	});
	$(".j_province").click(function(){
		$(".j_addressSelect").unbind("mouseleave");
		$(".j_provinceList").show();
		$(".j_cityList,.j_areaList").hide();
		$(".j_province").addClass("sel");
		$(".j_city,.j_area").removeClass("sel");
	});
	$(".j_city").click(function(){
		$(".j_addressSelect").unbind("mouseleave");
		if($(".j_cityList").find("a").length == 0){
			$.ajax({
				type: "POST",
				url: "/member/ajax/getArea.htm",
				dataType: "json",
				data: "id="+$(".j_provinceId").val(),
				success: function(data){
					var result = data.data;
					if(result.length>0){
						$(".j_cityList").html("");
						var html = "";
						for(var i=0;i<result.length;i++){
							var val = result[i]["name"];
							var aid = result[i]["areaId"]
							html += '<a data-id="'+aid+'">'+val+'</a>';
						}
						$(".j_cityList").html(html);
					}
				}
			});
		}
		$(".j_cityList").show();
		$(".j_provinceList,.j_areaList").hide();
		$(".j_city").addClass("sel");
		$(".j_province,.j_area").removeClass("sel");
	});
	$(".j_area").click(function(){
		$(".j_addressSelect").unbind("mouseleave");
		if($(".j_areaList").find("a").length == 0){
			$.ajax({
				type: "POST",
				url: "/member/ajax/getArea.htm",
				dataType: "json",
				data: "id="+$(".j_cityId").val(),
				success: function(data){
					var result = data.data;
					if(result.length>0){
						$(".j_cityList").html("");
						var html = "";
						for(var i=0;i<result.length;i++){
							var val = result[i]["name"];
							var aid = result[i]["areaId"]
							html += '<a data-id="'+aid+'">'+val+'</a>';
						}
						$(".j_areaList").html(html);
					}
				}
			});
		}
		$(".j_areaList").show();
		$(".j_provinceList,.j_cityList").hide();
		$(".j_area").addClass("sel");
		$(".j_province,.j_city").removeClass("sel");
	});
	
	$(".j_buyNow").live("click",function(){
		if($(this).attr("class").indexOf("dis")==-1){
			popupCheckCross(shopcart.ajaxBuyCartAdd);
		}
	});
	$(".j_addToCart").live("click",function(){
		if($(this).attr("class").indexOf("dis")==-1){
			var id = $("#productId").val();
			shopcart.ajaxAddGoodsToCart(id);
		}
	});
});
function changeAddressWidth(){
	var display = $(".j_addressPop").css("display");
	$(".j_addressPop").css("display","block");
	$(".j_addressSelect").css({"min-width":$(".j_addressPop .checked li").eq(0).width()+$(".j_addressPop .checked li").eq(1).width()+$(".j_addressPop .checked li").eq(2).width()+38});
	$(".j_addressPop").css("display",display);
}
Date.prototype.format = function(format){
	var o = {
	"M+" : this.getMonth() + 1,
	"d+" : this.getDate(),
	"h+" : this.getHours(),
	"m+" : this.getMinutes(),
	"s+" : this.getSeconds(),
	"q+" : Math.floor((this.getMonth() + 3) / 3),
	"S" : this.getMilliseconds()
	};
	if (/(y+)/.test(format)){
		format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4-RegExp.$1.length));
	}
	for (var k in o){
		if (new RegExp("(" + k + ")").test(format)){
			format = format.replace(RegExp.$1, RegExp.$1.length == 1? o[k]: ("00" + o[k]).substr(("" + o[k]).length));
		}
	}
	return format;
};