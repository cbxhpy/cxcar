var AdColumn=$.extend({},Eop.Grid,{
	init:function(){
		var self =this;
		$("#delBtn").click(function(){self.doDelete();});
		$("#toggleChk").click(function(){
			self.toggleSelected(this.checked);}
		);
	},
	doDelete:function(){
		
		if(!this.checkIdSeled()){
			alert("请选择要删除的会员卡");
			return ;
		}
		
		if(!confirm("确认要删除选定的会员卡？")){	
			return ;
		}
		
		$.Loading.show("正在删除会员卡...");
	 
		this.deletePost("washCard!delete.do", "会员卡删除成功");
			
	}
	
});

$(function(){
	AdColumn.opation("idChkName","id");
	AdColumn.init();
});