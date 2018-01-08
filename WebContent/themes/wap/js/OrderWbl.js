var Order={
	init:function(){
		var self = this;
		//确认收获
		$(".rogBtn").on("click", function(){
			var order_id = $(this).attr("orderid");
			if( confirm( "请您确认已经收到货物再执行此操作！" ) ){
				$.ajax({
					url : "api/shop/order!rogConfirmWbl.do?order_id="+order_id,
					dataType : "json",
					success : function(result){
						if(result.result==1){
							InitShowMessage(result.message);
							location.reload();
						}else{
							InitShowMessage(result.message);
						}
					},
					error : function(){
						InitShowMessage("服务器出错");
					}
				});	
						
			}
		});
		
		//解冻
		$(".thawBtn").click(function(){
			var orderid = $(this).attr("orderid");
			if( confirm( "提前解冻积分后，被冻结积分相关的订单商品，将不能进行退换货操作。确认要解冻吗？" ) ){
				$.ajax({
					url:"api/shop/returnorder!thaw.do?orderid="+orderid,
					dataType:"json",
					success:function(result){
						if(result.result==1){
							location.reload();
						}else{
							alert(result.message);
						}
					},error:function(){
						alert("抱歉，解冻出错现意外错误");
					}
				});		
			}
		});
	}
}