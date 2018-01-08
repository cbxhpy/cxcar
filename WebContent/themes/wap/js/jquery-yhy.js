$(function(){
	/*product.tpl start*/
	$('#spec-list .list-h li img').bind('mouseover',function(){
		 var num=$(this).attr('src');
			$('#spec-n1 img').attr('src',num);
	})
	/*product.tpl end*/
	
	/*product-list.tpl start*/
	$('.list-timo').click(function(){ 
		if($('.p_lis_tit2').css('display')=='none'){
			$('.p_lis_tit2').css('display','block');
			$('.list-timo img').attr('src','img/lismo2.png');
		}else{
			$('.p_lis_tit2').css('display','none');
			$('.list-timo img').attr('src','img/lismo.png');
		}
		
	});	
	$('.pro-list li').bind('mouseover',function(){
		var number = $('.pro-list li').index(this);
		$('.p_list_img').css('display','none');
		$('.p_list_img').eq(number).css('display','block');
	});
	
	/*product-list.tpl end*/
	/*about start*/
		$('.ab-li li').bind('mouseover',function(){
		var number = $('.ab-li li').index(this);
		$('.xclove').css('display','none');
		$('.xclove').eq(number).css('display','block');
	});
	/*about end*/
	
})