var ShortMsg={
		init:function(){
			var self = this;
			self.checkNewMsg();
			$("#short_msg").hover(
				function(){
					$("#short_msg>.msglist").show();
					$(".sysmenu .num").show();
				},
				function(){
					$("#short_msg>.msglist").hide();
					$(".sysmenu .num").hide();
				}
			);
			function check(){
				self.checkNewMsg();
				//self.checkNewOrder();
			}
			$('body').everyTime('20s',check);
		} 
		,
		loadNewMessage:function(msgList){
			var box =$(".sysmenu .msglist ul").empty();
			$.each(msgList,function(i,msg){
				box.append("<li><span class='name'><a class='mes' href='javascript:void(0);' onclick='addTab1(\""+msg.title+"\",\".."+msg.url+"\")' >"+msg.content+"</a></span></li>");
			});
			
		},
		checkNewMsg:function(){
			var self= this;
			$.ajax({
				url:'../core/admin/shortMsg!listNew.do',
				dataType:'json',
				cache:false,
				success:function(result){
					var count = result.length;
					if(count>0){
						$(".sysmenu .num").text(count);
						$(".sysmenu .msglist ul").find("li").remove();
						$(".sysmenu .num").show();
					}else{
						$(".sysmenu .num").hide();
						$(".sysmenu .msglist").hide();
					}
					self.loadNewMessage(result);
				},
				error:function(){
				}
			});
		},
		checkNewOrder:function(){
			var select_time = 20;
			$.ajax({
				url:'../shop/admin/order!checkNewOrder.do?select_time='+select_time,
				dataType:'json',
				cache:false,
				success:function(json){
					if(json.result = 1){
						if(json.message != '0'){
							$.Loading.success("有"+json.message+"个新订单");
							$('body').append('<embed src="../statics/images/new_order.mp3" autostart="true" hidden="true" loop="false">');
							//$("#new_order_audio").play();
						}
					}else{
					}
				},
				error:function(){
				}
			});
		}

};

$(function(){
	ShortMsg.init();
});