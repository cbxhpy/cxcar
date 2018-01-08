
	/*menu*/
	var click = document.getElementById('top').getElementsByClassName('menu-little');
	var menu = document.getElementById('menu-little');
	var menubig = document.getElementsByClassName('menu');
	click[0].onclick=function(){
		if(menu.style.display == 'none'){
			menu.style.display='block';
		}else{
			menu.style.display='none';
		}
	}
	
	function phone(){
		var winWidth = window.document.body.clientWidth;
		if(winWidth<800){ 
			menubig[0].style.display='none';
			click[0].style.display='block';
		}else{
			menubig[0].style.display='block';
			click[0].style.display='none';
			menu.style.display='none';
		}
	}
	
	phone();
	
	window.onresize =function(){
		phone();
	}
	
	/*手机的menu里的about us*/
	var phoneAboutUs = document.getElementsByClassName('youraboutus'); 
	var showAboutUs = document.getElementById('menu_na2');
	phoneAboutUs[0].onclick=function(){
		if(showAboutUs.style.display=='block'){
			showAboutUs.style.display='none';
		}else{
			showAboutUs.style.display='block';
		}
	}
	
	/*head.tpl end*/
	//返回顶部
$("body").append('<div class="gotop" id="gotop"><div>');
$(window).scroll(function(){$(document).scrollTop()>300?$("#gotop").fadeIn():$("#gotop").fadeOut()});
$("#gotop").click(function(){$("html,body").animate({scrollTop:0},300)})