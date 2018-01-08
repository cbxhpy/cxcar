String.prototype.Right = function(i){
	return this.slice(this.length-i,this.length);
};
$(function(){	
	$(".j_lazyLoad").lazyload({
		effect : "fadeIn"
	});
	$(".j_limitSold").each(function(){
		var $t = $(this);
		var no = $t.attr("data-no");
		$.ajax({
			url:"/commonAjax/isHavaStore.htm",
			data:{externalProductNo:no},
			type:"POST",
			dataType:"json",
			success:function(data){
				if(data.result=="empty"){
					$t.parent().parent().append("<div class='sold-out'>抢光啦</div>");
				}
			}
		});
	});
	//区域站点
	$(".j_showArea").click(function(){
		var $t = $(this);
		var pid = $t.attr("data-id");
		$(".j_provinceId").val(pid);
		$(".j_sArea").hide();
		$(".j_showArea").removeClass("sel").removeAttr("style");
		if($t.find(".j_cArea").length == 0){
			var id = $t.attr("data-id");
			$.ajax({
				type: "POST",
				url: "/member/ajax/getArea.htm",
				dataType: "json",
				async: false,
				data: "id="+id,
				success: function(data){
					var result = data.data;
					if(result.length>0){
						var html = '<b></b><div class="sarea"><a class="close j_cArea">×</a>';
						for(var i=0;i<result.length;i++){
							var val = result[i]["name"];
							var aid = result[i]["areaId"]
							html += '<a data-id="'+aid+'">'+val+'</a> ';
						}
						html += '</div>';
						$t.find(".j_sArea").html(html);
					}
				}
			});
		}
		$t.addClass("sel").css("z-index",1).find(".j_sArea").show();;
	});
	$(".j_sArea a[data-id]").live("click",function(){
		var $t = $(this);
		var id = $t.attr("data-id");
		var provinceId = $(".j_provinceId").val();
		$t.parent().find("a").removeClass("sel");
		$t.addClass("sel");
		$(".j_nSite").html($t.html()+' <b class="arr"></b>');
		$(".j_city").html($t.html());
		$(".j_site").hide();
		$.ajax({
			type: "POST",
			url: "/commonAjax/addAreaCookie.htm",
			dataType: "json",
			data: {provinceId:provinceId,cityId:id,areaId:""},
			success: function(data){
			}
		});
		event.stopPropagation();
	});
	$(".j_cArea").live("click",function(event){
		$(this).parents(".j_sArea").hide().parents(".j_showArea").removeClass("sel");
		event.stopPropagation();
	});
	/*限时抢购、潮流新品*/
	$(".j_limitTit").click(function(){
		var i = $(".j_limitTit").index($(this));
		$(".j_limitTip").hide().eq(i).show();
		$(".j_limitCon").hide().eq(i).show();
		$(".j_limitTit").removeClass("sel").eq(i).addClass("sel");
		if(i == 0){
			$(".j_limit").removeClass("nobg");
			$(".j_limitCon").eq(0).find("img").addClass("flipToFront");
			$(".j_limitCon").eq(1).find("img").removeClass("flipToFront");
		}else{
			$(".j_limit").addClass("nobg");
			$(".j_limitCon").eq(0).find("img").removeClass("flipToFront");
			$(".j_limitCon").eq(1).find("img").addClass("flipToFront");
		}
	});
	/*收藏图标*/
	$(".j_trendItem").mouseenter(function(){
		$(this).find(".j_icon").fadeIn(1000);
	}).mouseleave(function(){
		$(this).find(".j_icon").fadeOut(1000);
	});
	$(".j_love").click(function(){
		var $t = $(this);
		var id = $(this).attr("data-id");
		var no = $(this).attr("data-no");
		if(id){
			popupCheckCross(productItem.addFavById,$t,id);
		}else{
			popupCheckCross(productItem.addFavByNo,$t,no);
		}
	});
	/*楼层切换*/
	$(".j_layerTit").mouseenter(function(){
		var $t = $(this);
		var $p = $t.parents(".j_layer");
		var i = $p.find(".j_layerTit").index($t);
		$p.find(".j_layerCon").stop(true,true).animate({"margin-left":-1000*i},200);
		$p.find(".j_arr").stop(true,true).animate({"left":(100*i+50)+"%"},200,function(){
			$p.find(".j_layerTit").removeClass("sel")
			$t.addClass("sel");
		});
	});
	/*其它热门类目翻转*/
	$(".j_classHover").mouseenter(function(){
		var $t = $(this);
		$t.find(".j_classBack").removeClass("flipToFront").find("span").css("opacity",1);
		$t.find(".j_classFront").addClass("flipToBack");
		setTimeout(function(){
			$t.find(".j_classBack").show();
			$t.find(".j_classFront").hide();
		},250);
	}).mouseleave(function(){
		var $t = $(this);
		$t.find(".j_classFront").removeClass("flipToBack");
		$t.find(".j_classBack").addClass("flipToFront").find("span").css("opacity",0);
		setTimeout(function(){
			$t.find(".j_classBack").hide();
			$t.find(".j_classFront").show();
		},250);
	});
	/*热评商品*/
	$(".j_commentGoods").mouseenter(function(){
		$(this).find(".j_comment").stop(true,true).animate({"bottom":0},200);
	}).mouseleave(function(){
		$(this).find(".j_comment").stop(true,true).animate({"bottom":-30},200);
	});
	/*右侧导航定位*/
	$(".j_goLayer").click(function(){
		var $t = $(this);
		var layer = $t.attr("href").replace(/\#/,"");
		$(".j_goLayer").removeClass("sel");
		$t.addClass("sel");
		var scrollTop = $("[name="+layer+"]").offset().top;
		$('body,html').stop(true,true).animate({scrollTop:scrollTop},500); 
	});
	/*滚轮事件*/
	$(window).scroll(function(){
		var scrollTop = $(window).scrollTop();
		$(".j_goLayer").removeClass("sel");
		if(scrollTop > $("[name^='layer']").eq(0).offset().top - 1){
			for(var i = 0;i < $("[name^='layer']").length;i++){
				var $t = $("[name^='layer']").eq(i);
				if(scrollTop < $t.offset().top + 515){
					$(".j_goLayer").eq(i).addClass("sel");
					break;
				}
			}
		}
	});
	//生鲜果蔬
	$(".j_fruitMenu a").mouseenter(function(){
		var $t = $(this);
		var i = $(".j_fruitMenu a").index($t);
		$(".j_fruitMenu a").removeClass("sel").eq(i).addClass("sel");
		var id = $t.attr("data-id");
		var $con = $(".j_fruitGoods").eq(i);
		if(id && $con.find("li").length == 0){
			$.ajax({
				type : "post",
				url: "/commonAjax/getImageAdInsideSampleAd.htm",
				data: {id : id},
				async: false,
				dataType : "json",
				success: function(data) {
					var result = data["list"];
					var listHtml = "";
					if(result){
						for(var i=0;i<result.length;i++){
							listHtml += '<li><div class="item">';
							if(result[i].isUpload){
								listHtml += '<a href="'+result[i].url+'" target="_blank"><img width="180" height="180" src="'+global_imageServer+result[i].imagePath+'"></a>';
							}else{
								listHtml += '<a href="'+result[i].url+'" target="_blank"><img width="180" height="180" src="'+global_imageServer+"/goodsImage/210X210/"+result[i].imagePath+'"></a>';
							}
							listHtml += '<a class="tit" href="'+result[i].url+'" target="_blank">'+result[i].productName+'</a>';
							listHtml += '<div class="price j_comPrice" data-id="'+result[i].productId+'">';
							var sellPrice,marketPrice;
							$.ajax({
								type: "post",
								data: {productId:result[i].productId},
								url: "/commonAjax/getProductMessage.htm",
								async: false,
								dataType: "json",
								success: function(data){
									var result = data["list"];
									sellPrice = result[0].cost;
									marketPrice = result[0].marketPrice;
									var diffPrice = marketPrice - sellPrice;
									if(sellPrice.toString().indexOf(".")!=-1){
										listHtml += '<div class="pri"><span>￥</span><b class="j_sellPrice">'+sellPrice.toFixed(2)+'</b></div>';
									}else{
										listHtml += '<div class="pri"><span>￥</span><b class="j_sellPrice">'+sellPrice+'</b></div>';
									}
									if(marketPrice){
										if(marketPrice.toString().indexOf(".")!=-1){
											listHtml += '<div class="tip"><div>直降<span class="j_diffPrice">'+diffPrice.toFixed(2)+'</span>元</div><del class="j_marketPrice">'+"￥"+marketPrice.toFixed(2)+'</del></div>';
										}else{
											listHtml += '<div class="tip"><div>直降<span class="j_diffPrice">'+diffPrice.toFixed(2)+'</span>元</div><del class="j_marketPrice">'+"￥"+marketPrice+'</del></div>';
										}
									}
								}
							});
							listHtml += '</div><a class="buy" href="'+result[i].url+'" target="_blank">立即抢购</a>';
							listHtml += '</div></li>';
						}
						$con.html(listHtml);
					}
				}
			});
		}
		$(".j_fruitGoods").hide().eq(i).show();
	});
	$.ajax({
		url:"/commonAjax/cityNameFromIndex.htm",
		type:"POST",
		dataType:"json",
		success:function(data){
			if(data){
				$(".j_city").html(data.data);
				$(".j_nSite").html(data.data+'<b class="arr"></b>');
			}
		}
	});
});
/*轮播*/
(function(){
	for(var i=0;i<$(".j_slide").length;i++){
		slide(i);
	}
	
	for(var j=0;j<$(".j_limitTime").length;j++){
		initLimit(j);
	}
})();
function slide(index){
	var movetoleft=null;
	var $con = $('.j_mainSlide').eq(index);
	var $page = $('.j_slidePage').eq(index);
	var w = $con.find('li:first').width();
	$con.find('li:first').clone().appendTo($con);
	$con.hover(function(){
		if(movetoleft){
			clearInterval(movetoleft);
			movetoleft=null;
		}
	},function(){
		$page.find("span[class*='page-sel']").addClass("page-current");
		if(movetoleft==null){
			movetoleft=setInterval(function(){
				var i = $page.find("span").index($page.find("span[class*='page-sel']"));
				var l = $page.find("span").length;
				var n = (i+1)==l?0:(i+1);
				$page.find("span").eq(i).removeClass("page-sel").removeClass("page-current");
				$page.find("span").eq(n).addClass("page-sel").addClass("page-current");
				$con.animate({"margin-left":-w*(i+1)},500,function(){$con.css({"margin-left":-w*n});});
			},6000);
		}
	}).trigger('mouseleave');
	$page.find("span").each(function(i){
		$(this).mouseenter(function(){
			$page.find("span").removeClass("page-current");
			clearInterval(movetoleft);
			movetoleft=null;
			$con.stop(true,true).animate({marginLeft:-w*i + 'px'},1000);
			$page.find("span").removeClass("page-sel").eq(i).addClass("page-sel");
		});
	});
}
/*限时抢购倒计时*/
function initLimit(i){
	var $t = $(".j_limitTime").eq(i);
	var endtime,nowtime,proId = $t.attr("data-pid");
	var j_countDown = null;
	$.ajax({
		type: "post",
		url: "/system/serverTime.htm",
		async: false,
		dataType: "json",
		success: function(data){
			nowtime = new Date(data["sysTime"]);
		}
	});
	$.ajax({
		type: "post",
		data: {id:proId},
		url: "/commonAjax/getImageAdEndTime.htm",
		async: false,
		dataType: "json",
		success: function(data){
			endtime = new Date(data["time"]);
		}
	});
//	if($t.find(".j_limitBuyTime").val()==""){
		$t.find(".j_limitBuyTime").val(Math.floor((endtime-nowtime)/1000));
		j_countDown = setInterval(function(){
			var leftsecond = $t.find(".j_limitBuyTime").val();
			var sec_d=Math.floor((leftsecond/3600)/24);
			var sec_h=Math.floor((leftsecond/3600))%24;
			var sec_m=Math.floor((leftsecond/60))%60;
			var sec_s=Math.floor(leftsecond)%60;
			if(sec_h < 0) sec_h = 0;
			if(sec_m < 0) sec_m = 0;
			if(sec_s < 0) sec_s = 0;
			if(sec_h == 0 && sec_m == 0 && sec_s == 0){
				clearInterval(j_countDown);
			}else{
				$t.find(".j_day").html(sec_d);
				$t.find(".j_hour").html(("0"+sec_h).Right(2));
				$t.find(".j_min").html(("0"+sec_m).Right(2));
				$t.find(".j_sec").html(("0"+sec_s).Right(2));
				$t.find(".j_limitBuyTime").val(leftsecond-1);
			}
		},1000);
//	}
}
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