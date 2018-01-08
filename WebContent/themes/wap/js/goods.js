var goodsjs = {
		//增加减少数量
		goods_stock:function(){
			var store = parseInt($(".goods_add").attr("rel"));
			$(".goods_cut").click(function(){
				var goodsnum = parseInt($(".goodsnum").val());
				var r = /^[0-9]*[1-9][0-9]*$/;
				if(r.test(goodsnum) == false){
					alert("请您输入正确数字");
					return false;
				}
				if(goodsnum>1){
					$(".goodsnum").val(goodsnum-1);
				}
				else{
					alert("抱歉，最少需要一件商品");
				}
			});
			
			$(".goods_add").click(function(){
				var goodsnum = parseInt($(".goodsnum").val());
				var r = /^[0-9]*[1-9][0-9]*$/;
				if(r.test(goodsnum) == false){
					alert("请您输入正确数字");
					return false;
				}
				if(goodsnum<store){
					$(".goodsnum").val(goodsnum+1);
				}
				else{
					alert("抱歉，购买商品不能大于库存");
				}
			});
			$(".goodsnum").blur(function(){
				var goodsnums = parseInt($(this).val());
				if(goodsnums>store){
					alert("请输入小于库存的数量");
				}
				if(goodsnums<1){
					alert("最少买一件哦");
					$(this).val(parseInt(1));
				}
			});
			
		},
		
		//立即购买
		buy:function(){
			
			
			$(".buy_now").click(function(){
				var gnum = $(".goodsnum").val();
				var gid = $(".goods_tools").attr("rel");
				var productid = $("#productid").val();
				var action = $(".cart_way [name='action']").val();
				var spec = $(".cart_way [name='havespec']").val();
				
				var r = /^[0-9]*[1-9][0-9]*$/;
				if(r.test(gnum) == false){
					alert("请您输入正确数字");
					return false;
				}
				
				
				if(gnum<1){	
					location.href="404.html?msg='暂时无货'";
					return false;
				}
				
				if(spec==1){
					$.ajax({
						url:"api/shop/cart!" + action + ".do?productid="+productid+"&num="+gnum+"&havespec=1",
						dataType:"json",
						success : function(data) {	
							if(data.result==1){																
								location.href="cart.html";
							}
							else{							
								alert(data.message);
							}
						}
					});
					
				}else{
					$.ajax({
						url:"api/shop/cart!" + action + ".do?goodsid="+gid+"&num="+gnum,
						dataType:"json",
						success : function(data) {	
							if(data.result==1){																
								location.href="cart.html";
							}
							else{							
								alert(data.message);
							}
						}
					});
				}
				
			});
		},
		//加入购物车
		incart:function(){
			$(".in_cart").click(function(){
				var gnum = $(".goodsnum").val();
				var gid = $(".goods_tools").attr("rel");
				var productid = $("#productid").val();
				var action = $(".cart_way [name='action']").val();
				var spec = $(".cart_way [name='havespec']").val();
				if(spec==1){
					$.ajax({
						url:"api/shop/cart!" + action + ".do?productid="+productid+"&num="+gnum+"&havespec=1",
						dataType:"json",
						success : function(data) {	
							if(data.result==1){	
								alert("加入购物车成功");
								$.ajax({
									url:"api/shop/cart!getCartData.do",
									dataType:"json",
									success:function(result){
										if(result.result==1){
											var goodscartnum = result.data.count;
											$(".my_cart span").empty();
											$(".my_cart span").text(goodscartnum);
										}else{
											alert(result.message);
										}
									},error:function(){
										alert("抱歉，收藏出现意外错误");
									}
								});
							}
							else{							
								alert(data.message);
							}
						}
					});
				}else{
					$.ajax({
						url:"api/shop/cart!" + action + ".do?goodsid="+gid+"&num="+gnum,
						dataType:"json",
						success : function(data) {	
							if(data.result==1){	
								alert("加入购物车成功");
								$.ajax({
									url:"api/shop/cart!getCartData.do",
									dataType:"json",
									success:function(result){
										if(result.result==1){
											var goodscartnum = result.data.count;
											$(".my_cart span").empty();
											$(".my_cart span").text(goodscartnum);
										}else{
											alert(result.message);
										}
									},error:function(){
										alert("抱歉，收藏出现意外错误");
									}
								});
							}
							else{							
								alert(data.message);
							}
						}
					});
				}
				
			});
		},
		//更新购物车数量
		firstnum:function(){
			$.ajax({
				url:"api/shop/cart!getCartData.do",
				dataType:"json",
				success:function(result){
					if(result.result==1){
						var goodscartnum = result.data.count;
						$(".my_cart span").empty();
						$(".my_cart span").text(goodscartnum);
					}else{
						alert(result.message);
					}
				},error:function(){
					alert("抱歉，收藏出现意外错误");
				}
			});
		},
		//加入收藏
		goodscollect:function(){
			$(".goods_collect").click(function(){
				var collect = $(this).attr("rel");
				$.ajax({
					url:"api/shop/collect!addCollect.do?goods_id="+collect,
					dataType:"json",
					success:function(result){
						if(result.result==1){
							alert("收藏成功");
							location.reload();
						}else{
							alert(result.message);
						}
					},error:function(){
						alert("抱歉，收藏出现意外错误");
					}
				});
				
			});
		},
		//图片滚动
		goodsimg:function(){
			var goodsnum= $(".goods_images li").length;    //获取大幅图片中li的总个数。
			for(i=1;i<=goodsnum;i++){    //for循环，定i=1,每次循环就加1; 当i的值由1循环到等于result的值一样时（即“文本框的name='text'的值”）就停止循环
				var createobj = $("<a></a>"); //把div定义为变量createobj
				$("#goods_circle").append(createobj); //把createobj这个变量追加到html中的'body'里
				};
				/*具体我也没太读懂*/
				var active=0,                   
				as=document.getElementById('goods_circle').getElementsByTagName('a');	
				for(var i=0;i<as.length;i++){
					(function(){
						var j=i;
						as[i].onclick=function(){
							t2.slide(j);
							return false;
						}
					})();
				};
				var t2=new TouchSlider({id:'slider', speed:600, timeout:4000, before:function(index){
						as[active].className='';
						active=index;
						as[active].className='active';
					}});
			  /*循环滚动结束*/
		},
};