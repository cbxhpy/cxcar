<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/commons/taglibs.jsp"%>

<div class="infoPanel" style="">
<div class="infoContent" container="true"
	style="visibility: visible; opacity: 1;">
<div class="input">
<table class="form-table">
 
	<tr>
		<th>订单编号：</th>
		<td>${refund.order_sn }</td>
	</tr>
 
	<tr>
		<th>支付类型：</th>
		<td><c:if test="${refund.type==1 }">在线支付</c:if><c:if test="${refund.type==2 }">线下支付</c:if></td>
	</tr>
	<tr>
		<th>银行：</th>
		<td>${refund.pay_method }</td>
	</tr>	

	<tr>
		<th>流水号：</th>
		<td>${refund.sn}</td>
	</tr>
	<tr>
		<th>退款日期：</th>
		<td><html:dateformat pattern="yyyy-MM-dd" time="${refund.pay_date *1000 }"></html:dateformat></td>
	</tr>

	<tr>
		<th>退款人：</th>
		<td>${refund.op_user }</td>
	</tr>
	<tr>
		<th>退款金额：</th>
		<td><fmt:formatNumber value="${refund.money }" type="currency" pattern="￥.00"/></td>
	</tr>
</table>
 

</div>
</div>
</div>