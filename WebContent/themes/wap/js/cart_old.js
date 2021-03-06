var Cart={
		init:function(staticserver){
			var self=this;			
			this.bindEvent();
		},
		bindEvent:function(){
			var self=this;
			
			//购物数量调整
			$(".Numinput .increase,.Numinput .decrease").click(function(){
				$this = $(this);
				var number = $this.parents(".Numinput");
				var itemid =number.attr("itemid");
				var productid =number.attr("productid");
				var objipt = number.find("input");
				var num=objipt.val();
				num =parseInt(num);
				if (!isNaN(num)){
					if($this.hasClass("increase")){
						num++;
					}
					if($this.hasClass("decrease")){
						 if(num == 1 ){
							 if(confirm("确定要删除该商品?")){
								 self.deleteGoodsItem(itemid);
							 }
							 return false;
						} 
						num--;
					}
					 num = (num <=1 || num > 100000) ? 1 : num;
					 self.updateNum(itemid, num, productid,objipt);
				}
			});
			
			//购物数量手工输入
            $(".Numinput input").keydown(function(e){
                var kCode = $.browser.msie ? event.keyCode : e.which;
                //判断键值  
                if (((kCode > 47) && (kCode < 58)) 
                    || ((kCode > 95) && (kCode < 106)) 
                    || (kCode == 8) || (kCode == 39) 
                    || (kCode == 37)) { 
                    return true;
                } else{ 
                    return false;  
                }
            }).focus(function() {
                this.style.imeMode='disabled';// 禁用输入法,禁止输入中文字符
            }).keyup(function(){
                var pBuy   = $(this).parent();//获取父节点
                var itemid  = pBuy.attr("itemid");
                var productid  = pBuy.attr("productid");
                var numObj = pBuy.find("input[name='num']");//获取当前商品数量
                var num    = parseInt(numObj.val());
                if (!isNaN(num)) {
                    var numObj = $(this);
                    var num    = parseInt(numObj.val());
                    num = (num <=1 || num > 100000) ? 1 : num;
                    self.updateNum(itemid, num, productid,numObj);
                }
            });
			
			//去结算
			$(".go_checkout .checkout_btn").click(function(){
				var cart_tiem_num = $("#cart_wrapper ul li").length;  //判断购物车购物项数量
				if(cart_tiem_num == "0"){
					alert("您还没有购买任何东西哦！");
					return false;
				}
					location.href="checkout.html";
			});
		},
		
		//删除一个购物项
		deleteGoodsItem:function(itemid){
			var self=this;
			$.ajax({
				url:"api/shop/cart!delete.do?ajax=yes",
				data:"cartid="+itemid,
				dataType:"json",
				success:function(result){
					if(result.result==1){
						self.refreshTotal();
						self.removeItem(itemid);
						
						
						
					}else{
						$.alert(result.message);
					}	
					$.Loading.hide();
				},
				error:function(){
					$.Loading.hide();
					$.alert("出错了:(");
				}
			});
		},
		//移除商品项
		removeItem:function(itemid){
			$("#cart_wrapper li[itemid="+itemid+"]").remove();
			var cart_tiem_num = $("#cart_wrapper ul li").length;  //判断购物车购物项数量
			if(cart_tiem_num == "0"){
				$(".cart_list").empty();
				$(".cart_list").html("<div class='cart_blank'>您的购物车中暂无商品，赶快<a href='index.html'>挑选心爱的商品</a>吧！</div>");
			}
		},
		
		//清空购物车
		clean:function(){
			$.Loading.show("请稍候...");
			var self=this;
			$.ajax({
				url:"/api/shop/cart!clean.do?ajax=yes",
				dataType:"json",
				success:function(result){
					$.Loading.hide();
					if(result.result==1){
						location.href='cart.html';
					}else{
						$.alert("清空失败:"+result.message);
					}				 
				},
				error:function(){
					$.Loading.hide();
					$.alert("出错了:(");
				}
			});		
		},
		
		//更新数量
		updateNum:function(itemid,num,productid,num_input){
			var self = this;
			$.ajax({
				url:"api/shop/cart!updateNum.do?ajax=yes",
				data:"cartid="+itemid +"&num="+num +"&productid="+productid,
				dataType:"json",
				success:function(result){
					if(result.result==1){
						if(result.store>=num){
							self.refreshTotal();
							var price = parseFloat($("li[itemid="+itemid+"]").attr("price"));
							//price =price* num;
							price =self.changeTwoDecimal_f(price* num);
							$("tr[itemid="+itemid+"] .itemTotal").html("￥"+price);
							num_input.val(num);
						}else{
							num_input.val(result.store);
							alert("抱歉！您所选选择的货品库存不足。");
						}
					}else{
						alert("更新失败");
					}
				},
				error:function(){
					alert("出错了:(");
				}
			});		
		},
		
		//刷新价格
		refreshTotal:function(){
			var self = this;
			$.ajax({
				url:"cart/cartTotal.html",
				dataType:"html",
				success:function(html){
					 $(".total_wrapper").html(html);
				},
				error:function(){
					alert("糟糕，出错了:(");
				}
			});
		},
		
		changeTwoDecimal_f:function(x) {
	        var f_x = parseFloat(x);
	        if (isNaN(f_x)) {
	            alert('参数为非数字，无法转换！');
	            return false;
	        }
	        var f_x = Math.round(x * 100) / 100;
	        var s_x = f_x.toString();
	        var pos_decimal = s_x.indexOf('.');
	        if (pos_decimal < 0) {
	            pos_decimal = s_x.length;
	            s_x += '.';
	        }
	        while (s_x.length <= pos_decimal + 2) {
	            s_x += '0';
	        }
	        return s_x;
	    }
};

$(function(){
	Cart.init();
});