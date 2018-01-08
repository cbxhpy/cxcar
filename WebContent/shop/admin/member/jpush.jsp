<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/commons/taglibs.jsp" %>
<script type="text/javascript" src="../js/jquery.js"></script>
<script type="text/javascript" src="../js/jquery.datetimepicker.js"></script>
<link rel="stylesheet" type="text/css" href="../css/jquery.datetimepicker.css"/>


<div class="input">
 <form class="validate" method="post"  name="jpushForm" id="jpushForm"  >
   <table cellspacing="1" cellpadding="3" width="100%" class="form-table"> 
     <tr>
     	<th><label class="text">通知消息：</label></th>
     	<td><input type="radio" name="message_notification"  value="0" checked />通知&nbsp;&nbsp;<input type="radio" name="message-notification"  value="1"  disabled/>消息</td>
     </tr>
     <tr>
     	<th><label class="text">设备类型：</label></th>
     	<td><input type="radio" name="audience" value="1" checked />android&ios&nbsp;&nbsp;<input type="radio" name="audience" value="2"  />android&nbsp;&nbsp;<input type="radio" name="audience" value="3"  />ios&nbsp;&nbsp;<input type="radio" name="audience" value="4" disabled />winphone</td>
     </tr>
     <tr><th><label class="text">alias或tags：</label></th>
     <td><input type="radio" name="alias_tags_broadcast"  value="0"  />alias&nbsp;&nbsp;<input type="radio" name="alias_tags_broadcast"  value="1"  />tags&nbsp;&nbsp;<input type="radio" name="alias_tags_broadcast"  value="2" checked />广播</td>
	</tr> 
	
      <tr >
     	<th><label class="text">用户号码或tags</label></th>
     	<td><textarea rows="10" cols="40" name="jpush_names"></textarea>(alias、tags需要填写,广播不用填写)</td>
     </tr> 
     <tr><th><label>标题：</label></th><td><textarea name="title"  rows="1" cols="40"></textarea>(输入小于20个字)</td>
     <tr><th><label>内容：</label></th><td><textarea name="content"  rows="4" cols="40"></textarea>(输入小于60个字)</td>
     </tr>
     <tr>
     	<th><label>是否启动定时推送：</label></th>
     	<td><input type="radio" name="enabletiming" value="0" disabled/>是<input type="radio" name="enabletiming" value="1" checked/>否</td>
     </tr>
     <tr>
     	<th><label class="text">推送定时：(最好在1天之内)</label></th>
     	<td><input type="text" value="2015/12/25 05:06" id="datetimepicker" disabled/>(yyyy-MM-dd HH:mm)</td>
     </tr>
   </table>
<div class="submitlist" align="center">
 <table>
 <tr><td >
  <a href="javascript:;" id="jpushs">推送</a>
   </td>
   </tr>
 </table>
</div>     
 </form>

 </div>
  <script type="text/javascript">
  $(document).ready(function(){
  		//时间选择 
  		$('#datetimepicker').datetimepicker();
  		 
  		//点击发送按钮触发事件
		$("#jpushs").click(function(){
	  	  var notification = $("input[name='message_notification']:checked").val();
	  	  var audience = $("input[name='audience']:checked").val();
	  	  var alias_tags_broadcast = $("input[name='alias_tags_broadcast']:checked").val();
	  	  var jpush_names = $("textarea[name='jpush_names']").val();
	  	  var title = $("textarea[name='title']").val();
	  	  var content = $("textarea[name='content']").val();
	  	  var enabletiming =$("input[name='enabletiming']:checked").val();
	  	  var time = $("#datetimepicker").val();
	  	  if(enabletiming == 1){
	  	  	  time = 0;
	  	  }
		    $.ajax({
		  		//url:"member!jpush.do?ajax=yes&message_notification="+notification+"&audience="+audience+"&alias_tags_broadcast="+alias_tags_broadcast+"&jpush_names="+jpush_names,
		  		url:"../member!jpush.do?ajax=yes",
		  		dataType:"json",
		  		type:"get",
		  		data:{'message_notification':notification,'audience':audience,'alias_tags_broadcast':alias_tags_broadcast,'content':content,'jpush_names':jpush_names,'time':time,'title':title},
		  		success:function(json_message){
		  			if(json_message.result==1){
		  				alert(json_message.message);
		  				 
		  			}else{
		  				alert(json_message.message);
		  			}
		  		},
		  		error:function(json_message){
		  			if(json_message.result==0){
		  				alert(json_message.message);
		  			}
		  		}
		  }); 
		  
		  
		    
		   
	  });
	   
  });

</script>
 