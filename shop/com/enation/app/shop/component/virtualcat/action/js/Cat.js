var Cat ={
    
	opBundle:undefined,
	init:function(){
		var self = this;
		$("#cat_list .delete").click(function(){
			if( confirm("确定要删除此类别吗") ){
				$.Loading.show('正在响应您的请求，请稍侯...');
				self.opBundle=$(this);
				self.doDelete( self.opBundle.attr("catid") );
			}
		});
		
		
	},
	doDelete:function(catid){
		var self =this;
		$.ajax({
			 type: "POST",
			 url: "virtual-cat!delete.do",
			 data: "ajax=yes&vid="+catid,
			 dataType:"json",
			 success: function(result){
				 if(result.result==1){
					$.Loading.hide();
				 	alert(result.message);
			     }else{
			 		self.opBundle.parents("tr").remove();
				    $.Loading.hide();
				 }
			 },
			 error:function(){
				 $.Loading.hide();
				 alert("操作失败，请重试");
			 }
		}); 		
	}
	
};

 
$(function(){
	Cat.init();
	
});