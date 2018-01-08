<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/commons/taglibs.jsp"%>

<style>
.input table td{font-weight:normal;}
.input .goodsList th,.input .goodsList td,.input .loglist th,.input .loglist td{text-align:left;padding-left:8px;}
.input table.goodsList th,.input .loglist th{background:#EBF1F4;height:26px;line-height:26px;}
.input table.goodsList td,.input table.goodsList th,.input .loglist th,.input .loglist td{  border: 1px solid #C4D5E0;}
span#pay{padding:8px 0px;height:56px;}
#cancelRemark{display:none;}
</style>
<div class="input">
	<form action="sellBack!save.do" method="post" id="theForm">
		<table>
			<tr>
				<td align="right" style="width:100px;">退货单号：</td>
				<td colspan="3">
					${sellBackList.tradeno}
				</td>
			</tr>
			<tr>
				<td align="right">关联订单号：</td>
				<td>
					${sellBackList.ordersn}
				</td>
				<td>订单使用：</td>
				<td>
					<span id="meta"></span>
					 已支付：￥${order.paymoney}
				</td>
			</tr>
			<tr>
				<td align="right">退货人：</td>
				<td>
					${sellBackList.sndto}
				</td>
				<td style="width:80px">物流公司：</td>
				<td>
					${ sellBackList.logi_name}
					&nbsp;&nbsp;
					物流单号：
					${sellBackList.logi_no }
				</td>
			</tr>
			<tr>
				<td align="right">退款金额：</td>
				<td width="300">
					${sellBackList.alltotal_pay } 元
				</td>
				<td>退款运费： </td>
				<td>
					<c:if test='${sellBackList.fare=="" || sellBackList.fare==null }'>0</c:if>
					<c:if test='${sellBackList.fare!="" && sellBackList.fare!=null }'>${sellBackList.fare}</c:if>元         
					&nbsp;&nbsp;
					合计：<c:if test='${sellBackList.total=="" || sellBackList.total==null}'>0</c:if>
					<c:if test='${sellBackList.total!=null &&sellBackList.total!="" }'>${sellBackList.total }</c:if>元
				</td>
			</tr>
			<tr>
				<td align="right">是否全部退货：</td>
				<td colspan="3">
					<input type="radio" value="1" name="sellBackList.isall" <c:if test="${sellBackList.isall==1 }"> checked="checked" </c:if>>全部退货
					<input type="radio" value="0" name="sellBackList.isall" <c:if test="${sellBackList.isall==0 }"> checked="checked" </c:if>>部分退货
				</td>
			</tr>
			<tr>
				<td align="right" valign="top">退款方式：</td>
				<td colspan="3">
					<c:if test="${sellBackList.refund_way==0}"><span id="way_0">银行退款</span></c:if>
					<c:if test="${sellBackList.refund_way==1}"><span id="way_1">退还用户：</span>&nbsp;&nbsp;
						<span id="pay">
							余额：${sellBackList.surplus }	<input type="hidden" value="${sellBackList.integral }" name="sellBackList.integral" />&nbsp;&nbsp;
							积分：${sellBackList.integral }<input type="hidden" value="${sellBackList.surplus }" name="sellBackList.surplus" />
						</span>
					</c:if>
				</td>
			</tr>
			<tr>
				<td align="right">退款备注：</td>
				<td colspan="3">
					${sellBackList.remark }
				</td>
			</tr>
			<tr>
				<td align="right">客服操作备注：</td>
				<td colspan="3">
					${sellBackList.seller_remark }
				</td>
			</tr>
			<tr>
				<td align="right">操作备注：</td>
				<td colspan="3">
					<textarea rows="3" id="seller_remark" name="sellBackList.warehouse_remark">${sellBackList.warehouse_remark }</textarea>
				</td>
			</tr>
			<tr>
				<td align="right">状态：</td>
				<td colspan="3">
					<c:if test="${sellBackList.tradestatus==0}">新建</c:if>
					<c:if test="${sellBackList.tradestatus==1}">待入库</c:if>
					<c:if test="${sellBackList.tradestatus==2}">已入库</c:if>
					<c:if test="${sellBackList.tradestatus==3}">已完成</c:if>
					<c:if test="${sellBackList.tradestatus==4}">已取消</c:if>
				</td>	
			</tr>
			<tr>
				<td colspan="4" style="border:none">
					<p>请选择入库的商品：</p>
					<table class="goodsList">
						<tr>
							<th style="width:30px;">选择</th>
							<th>商品名称</th>
							<th>金额</th>
							<th>购买数量</th>
							<th>退货数量</th>
							<th>已入库数量</th>
							<th>本次入库</th>
						</tr>
						<c:forEach items="${goodsList}" var="data" varStatus="n">
							<c:if test="${data.return_num!=null &&data.return_num!=0 }">
							<tr>					
								<td>
									<input type="checkbox" <c:if test="${data.goodsId!=null}"> checked="checked" </c:if> value="${data.goods_id }"  name="goodsId" id="${data.goods_id }" />
									<input type="hidden" checked="" value="${data.goods_id }"  name="gid" />
								</td>
								<td>
									${data.name }
									<input type="hidden" name="goodsName" value="${data.name }" />
								</td>
								<td>
									${data.price }
									<input type="hidden" name="goodsPrice" value="${data.price }" />
								</td>
								<td>
									${data.num }
									<input type="hidden" name="payNum" value="${data.num }" />
								</td>
								<td>
									${data.return_num }
									<input type="hidden" name="returnNum" value="${data.return_num }" />
								</td>
								<td>
									<input type="hidden" class="input"  value="${data.storage_num }" name="oldStorageNum" style="width:20px;"  >
									${data.storage_num }
								</td>
								<td>
									<input type="text" class="input" returnNum="${ data.return_num}" oldStorageNum="${ data.storage_num}"  value="0" name="storageNum" style="width:20px;"  >
								</td>
							</tr>
							</c:if>
						</c:forEach>
					</table>
				</td>
			</tr>
			<tr id="cancelRemark">
				<td colspan="4" align="center">取消原因：<input type="text" class="input" value="" name="cancelRemark" style="width:150px;height:26px;line-height:26px;" /></td>
			</tr>
			<tr>
				<td colspan="4" align="center">
					<input type="hidden" value="${sellBackList.id }" name="id" />
					
					
					<html:permission actid="depot_ship">
				<!-- 	<input type="button" class="saveBtn" value="保存" id="save" status="1"/> -->
					<input type="button" class="saveBtn" value="确认入库" id="submit" />
					</html:permission>
					
					<html:permission actid="customer_service">
					<input type="button" class="cancelBtn" value="取消退货" status="${sellBackList.tradestatus }"/>
					</html:permission>
				</td>
			</tr>
		</table>
		<table class="loglist" style="margin:20px auto;">
			<tr>
				<td colspan="3" style="border:none;font-weight:bold;">操作日志</td>
			</tr>
			<tr>
				<th>操作人员</th>
				<th>操作详情</th>
				<th>操作时间</th>
			</tr>
			<c:forEach items="${logList }" var="log">
				<tr>
					<td>${log.operator }</td>
					<td>${log.logdetail }</td>
					<td><html:dateformat pattern="yyyy-MM-dd HH:mm:ss" time="${log.logtime}"></html:dateformat></td>
				</tr>
			</c:forEach>

		</table>
	</form>
</div>
<script>
$(function(){
	var click = 1;
	var point = "积分：￥0.0，";
	var pay = "余额：￥0.0，";
	<c:forEach items="${metaList}" var="meta">
		<c:if test="${meta.meta_key=='marketPoint' }">
			point ="积分：￥${meta.meta_value}，";
		</c:if>
		<c:if test="${meta.meta_key=='creditpay' }">
			pay = "余额：￥${meta.meta_value}，";
		</c:if>
	</c:forEach>
	$("#meta").html(point+pay);
	$('.saveBtn').click(function(){
		//var status = $(this).attr("status");
		<c:if test="${is_all==1}">var status="2"</c:if>
		<c:if test="${is_all==0}">var status="5"</c:if>
		var options = {
			url : "sellBack!update.do",
			data : {"ajax" : "yes"},
			type : "POST",
			dataType : 'json',
			success : function(result) {	
				$.Loading.hide();
				alert(result.message);
				 window.location.href = "/wine/admin/sellBack!list.do";
			},
			error : function(e) {
				$.Loading.hide();
				alert("出现错误 ，请重试");
			}
		};
		$('#theForm').ajaxSubmit(options);
	});
	
	 $("input[name='storageNum']").change(function(){
	 	
		$("input[name='storageNum']").each(function(){
			var returnNum = parseInt( $(this).attr("returnNum") );
			var oldStorageNum =parseInt( $(this).attr("oldStorageNum"));
		    var val =  parseInt($(this).val());
		    if(val+oldStorageNum>returnNum){
		    	alert("入库数量不能大于退货数量["+returnNum+"]");
		    	 $(this).val(0);
		    	return false;
		    }
		 
		});
		 
	}); 
	
	$('.cancelBtn').click(function(){
		if(click==1){
			$("#cancelRemark").show();
			click = click +1;
		}else{
			status = $(this).attr("status");
			cancel(status);
		}
	});
	function cancel(status){
		var options = {
			url : "sellBack!cancel.do",
			data : {"ajax" : "yes","status" : status},
			type : "POST",
			dataType : 'json',
			success : function(result) {	
				$.Loading.hide();
				alert(result.message);
				 window.location.href = "/wine/admin/sellBack!list.do";
			},
			error : function(e) {
				$.Loading.hide();
				alert("出现错误 ，请重试");
			}
		};
		$('#theForm').ajaxSubmit(options);
	}
})
</script>