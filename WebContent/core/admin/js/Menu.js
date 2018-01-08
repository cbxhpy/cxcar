var currimg;
function onClick(event, treeId, treeNode, clickFlag){
	$("#detail_wraper").load("menu!edit.do?id="+treeNode.menuid,function(){
		//$("#editForm").validate();
		$("#saveEditBtn").click(function(){
			saveEdit();
		});
		$(".icon_wrap img").click(function(){
			currimg=$(this);
			openIconDialog();
		});
	});
}
function saveAdd(){
	
	/*if( !$("form").checkall() ){
		return ;
	}*/
	$.Loading.show('正在保存，请稍侯...');
	var options = {
			url :"menu!saveAdd.do",
			type : "POST",
			dataType : 'json',
			success : function(result) {	
			 	if(result.result==1){
			 		$.Loading.success("保存成功");
			 		$("#menuid").attr("disabled",false).val( result.menuid );
					$("#saveAddBtn").unbind().bind("click",function(){
						saveEdit();
					});
			 	}else{
			 		alert(result.message);
			 	}
			 	
			},
			error : function(e) {
				$.Loading.hide();
				alert("出错啦:(");
				}
		};
	$("form").ajaxSubmit(options);	
	
}


function saveEdit(){
//	if( !$("form").checkall() ){
//		return ;
//	}
	$.Loading.show('正在保存，请稍侯...');
	var options = {
			url :"menu!saveEdit.do",
			type : "POST",
			dataType : 'json',
			success : function(result) {	
				 
			 	if(result.result==1){
			 		
			 		$.Loading.success("保存成功");
			 		 
			 	}else{
			 		alert(result.message);
			 	}
			 	
			},
			error : function(e) {
				$.Loading.hide();
				alert("出错啦:(");
				}
		};

	$("form").ajaxSubmit(options);			
}

function openIconDialog(){
	//var dialog = $.dialog({id: 'iconDlg', title:"选择图标", content:"请稍候...", width:400, lock:true, min:false, max:false});
	
	$.ajax({
	    url:"menu!listIcon.do",	    
	    success:function(html){
	    	$("#orderinfo").empty();
	    	$("#orderinfo").append(html);
	    	$("#sel-icon-btn").attr("disabled",true);
	    	$(".icon_list_wrap li").hover(
	    		function(){
	    			$(this).addClass("hover");
	    		}
	    		,
	    		function(){
	    			$(this).removeClass("hover");
	    		}	
	    	)
	    	.click(function(){
	    		$(".icon_list_wrap li").removeClass("selected");
	    		$(this).addClass("selected");
	    		$("#sel-icon-btn").attr("disabled",false);
	    	});
	    	
	    	$("#sel-icon-btn").click(function(){
	    		var selectedIcon = $(".icon_list_wrap li.selected");
	    		if(selectedIcon.size()<0){alert("请选择图标"); return;}
	    		var iconimg =selectedIcon.find("img");
	    		var src= iconimg.attr("src")
	    		var filename = iconimg.attr("filename");
	    		currimg.attr("src",src)
	    		currimg.prev("input").val("/statics/images/menuicon/"+filename);
	    		$("#orderinfo").empty();
	    	});
	    },
	    error:function(){
	    	alert("出错");
	    }
	    ,
	    cache:false
	});	
}
 

function deleteMenu(id){
	$.Loading.show('请稍侯...');
	$.ajax({
		url:'menu!delete.do?id='+id,
		type:'post',
		dataType:'json',
		success:function(result){
		 	if(result.result==1){
		 		$.Loading.success("删除成功");
		 	 
		 	}else{
		 		$.Loading.hide();
		 		alert(result.message);
		 	}			
		},
		error:function(){
			$.Loading.hide();
			alert("出错啦:(");
		}
	});
}


function beforeRemove(treeId, treeNode) {

	return confirm("确认删除菜单 " + treeNode.name + " 吗？");
}

function onRemove(e, treeId, treeNode) {
	deleteMenu(treeNode.menuid);
}


function onDrop(event, treeId, treeNodes, targetNode, moveType, isCopy){
	var node  = treeNodes[0];
	// alert(moveType+"  "+ node.parentTId +"　　" +targetNode.parentTId);
	moveNode(node.menuid,targetNode.menuid,moveType);
}


function moveNode(menuid,target_menu_id,moveType){
	
	$.Loading.show('请稍侯...');
	$.ajax({
		url:'menu!move.do?id='+menuid+"&targetid="+target_menu_id+"&movetype="+moveType,
		type:'post',
		dataType:'json',
		success:function(result){
			 
		 	if(result.result==1){
		 		$.Loading.success("菜单移动成功");
		 		
		 	}else{
		 		$.Loading.error(result.message);
		 	}	
		 	
		 	
		},
		error:function(){
			$.Loading.hide();
			alert("出错啦:(");
		}
	});	
	
}

 



$(function(){
	
	var setting = {
			async: {
				enable: true,
				url:"menu!json.do",
				autoParam:["menuid"]
			},
			callback: {
				onClick: onClick,
				beforeRemove: beforeRemove,
				onRemove: onRemove,
				onDrop:onDrop
			},
			edit:{
				drag:{
					isCopy:false
				},
				enable:true,
				showRenameBtn:false
			}
			
		};	
	
	$.fn.zTree.init($(".ztree"), setting);
 
	$("#add-menu-btn").click(function(){
		$("#detail_wraper").load("menu!add.do?parentid=0",function(){
			$("#saveAddBtn").click(function(){
				saveAdd();
			});
			$(".icon_wrap img").click(function(){
				currimg=$(this);
				openIconDialog();
			});
		});
	});
	
});

