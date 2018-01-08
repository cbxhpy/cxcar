var Cart={
	init:function(staticserver){
		$.ajaxSetup({ cache:false,		error:function(XMLHttpRequest, textStatus){       
		    
		} });
		
		var self=this;
		function changeEvent(input){
			self.numChange(input);
		}
		$.getScript(staticserver+"/js/common/jquery.numinput.js",function(){
			$(".Numinput").numinput({name:"num",onChange:changeEvent});
		});
		
		this.bindEvent();
		self.refreshTotal();
	},
	bindEvent:function(){
		var self=this;
		//删除商品
		$("#goodsbody .quiet").click(function(){
			self.deleteGoodsItem($(this).parents("tr").attr("itemid"));
		});
		
		$("#giftbody .quiet").click(function(){
			self.deleteGoodsItem($(this).parents("tr").attr("itemid"));
		});
		
		$("#pgkbody .quiet").click(function(){
			self.deleteGoodsItem($(this).parents("tr").attr("itemid"));
		});
		
		$("input.btn-clearcat").click(function(){
			if(confirm("确认清空？"))
			self.clean();
		});

		$("input.btn-return").click(function(){
			location.href='index.html';
		});
	},
	deleteGoodsItem:function(itemid){
		var self=this;
		$.ajax({
			url:"widget?type=shop_cart&ajax=yes&action=delete",
			data:"cartid="+itemid,
			dataType:"html",
			success:function(){
				self.refreshTotal();
				$("tr[itemid="+itemid+"]").remove();
			},
			error:function(){
				alert("出错了:(");
			}
		});
	}
	,
	clean:function(){
		var self=this;
		$.ajax({
			url:"widget?type=shop_cart&ajax=yes&action=clean",
			dataType:"json",
			success:function(result){
				if(result.result==0){
//					self.refreshTotal();
//					$("#cartItems table>tbody").empty();
					location.href='cart.html';
				}else{
					alert("清空失败");
				}				 
			},
			error:function(){
				alert("出错了:(");
			}
		});		
	}
	,
	numChange:function(input){
		
		var tr = input.parents("tr");
		var itemid = tr.attr("itemid");
		var productid = tr.attr("productid");
		var limitnum = tr.attr("limitnum");
		var num= input.val();
		//alert("update num :" +num);
		if(limitnum && parseInt( num )> parseInt(limitnum)){
			alert("数量已经大于限购数量");
			input.val(num-1);
			return ;
		}
		this.updateNum(itemid, num,productid);
	},
	updateNum:function(itemid,num,productid){
		var self = this;
		$.ajax({
			url:"widget?type=shop_cart&ajax=yes&action=update",
			data:"cartid="+itemid +"&num="+num+"&productid="+productid,
			dataType:"json",
			success:function(result){
				if(result.result==0){
					if(result.store>=num){
					self.refreshTotal();
					var price = parseFloat($("tr[itemid="+itemid+"]").attr("price"));
					//price =price* num;
					price =self.changeTwoDecimal_f(price* num);
					$("tr[itemid="+itemid+"] .itemTotal").html("￥"+price);
					}else{
						input.val(result.store);
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
	refreshTotal:function(){
	//	alert("refresh total");
		$.ajax({
			url:"widget?type=shop_cart&ajax=yes&action=getTotal&rmd="+new Date().getTime(),
			dataType:"html",
			success:function(totalHtml){
				//alert(totalHtml);
				$("#cartTotal").html(totalHtml);
			},
			error:function(){
				alert("出错了:(");
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