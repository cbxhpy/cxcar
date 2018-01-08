var Sys = {};
var ua = navigator.userAgent.toLowerCase();
var s;
(s = ua.match(/msie ([\d.]+)/)) ? Sys.ie = s[1] :
(s = ua.match(/firefox\/([\d.]+)/)) ? Sys.firefox = s[1] :
(s = ua.match(/chrome\/([\d.]+)/)) ? Sys.chrome = s[1] :
(s = ua.match(/opera.([\d.]+)/)) ? Sys.opera = s[1] :
(s = ua.match(/version\/([\d.]+).*safari/)) ? Sys.safari = s[1] : 0;
function clearLetter(p){
	var reg=/px$/g;
	if(reg.test(p)){
		p=p.replace(reg,"");
	}
	return p;
}
function getNumber(i){
	var reg=/[^0-9]/g;
	if(reg.test(i)){
		i=i.replace(reg,"");
	}
	return i;
}
function getTop(o){
    var osTop=o.offsetTop;
    if(o.offsetParent!=null){
		osTop+=getTop(o.offsetParent);
	}
    return osTop;
}
function goods_Switch(o){
		$(o).parent().find("li").removeClass("current");
		$(o).addClass("current");
		$(o).parent().parent().siblings().hide();
		$("."+o.id).show();
}
function goodsEnlarge(o){
	$(o).css("z-index","10");
	$(o).children(".hidelayer").show();
	o.onmouseout=function(){
		$(o).children(".hidelayer").hide();
		$(o).css("z-index","0");
	}
}
/* $Id : common.js 4865 2007-01-31 14:04:10Z paulgao $ */
/* 
 * 添加商品到购物车 
 */
function addToCart(goodsId, type, parentId)
{
  var goods        = new Object();
  var spec_arr     = new Array();
  var fittings_arr = new Array();
  var number       = 1;
  var formBuy      = document.forms['ECS_FORMBUY'];
  var quick		   = 0;
  var add_to       = type ? parseInt(type) : 0;
  // 检查是否有商品规格 
  if (formBuy)
  {
    spec_arr = getSelectedAttributes(formBuy);
    if (formBuy.elements['number'])
    {
      number = formBuy.elements['number'].value;
    }
	quick = 1;
  }
  goods.quick    = quick;
  goods.spec     = spec_arr;
  goods.goods_id = goodsId;
  goods.number   = number;
  goods.parent   = (typeof(parentId) == "undefined") ? 0 : parseInt(parentId);
  goods.add_to   = add_to;
  
  Ajax.call('/flow.php?step=add_to_cart', 'goods=' + $.toJSON(goods), addToCartResponse, 'POST', 'JSON');
}
function drop_goods(id){
    Ajax.call("/flow.php?step=drop_goods_ajax", "id=" + id, addToCartResponse, "GET", "JSON");
}
/*
 * 获得选定的商品属性
 */
function getSelectedAttributes(formBuy)
{
  var spec_arr = new Array();
  var j = 0;
  for (i = 0; i < formBuy.elements.length; i ++ )
  {
    var prefix = formBuy.elements[i].name.substr(0, 5);
    if (prefix == 'spec_' && (
      ((formBuy.elements[i].type == 'radio' || formBuy.elements[i].type == 'checkbox') && formBuy.elements[i].checked) ||
      formBuy.elements[i].tagName == 'SELECT'))
    {
      spec_arr[j] = formBuy.elements[i].value;
      j++ ;
    }
  }
  return spec_arr;
}
/* *
 * 处理添加商品到购物车的反馈信息
 */
function addToCartResponse(result)
{
  if (result.error > 0)
  {
    // 如果需要缺货登记，跳转
    if (result.error == 2)
    {
      if (confirm(result.message))
      {
        location.href = '/user.php?act=add_booking&id=' + result.goods_id + '&spec=' + result.product_spec;
      }
    }
    // 没选规格，弹出属性选择框
    else if (result.error == 6)
    {
      openSpeDiv(result.message, result.goods_id, result.parent);
    }
    else
    {
      alert(result.message);
    }
  }
  else
  {
    var cartInfo = document.getElementById('ECS_CARTINFO');
    var cart_goods_info = document.getElementById('LI_CARTINFO');
    var cartnumb_info_side = document.getElementById('cartnumb_info_side');
    var cart_goods_info_side = document.getElementById('cart_goods_info_side');
    var cart_url = '/flow.php?step=cart';
    if (cartInfo)
    {
      cartInfo.innerHTML = result.content;
    }
    if (result.one_step_buy == '1')
    {
      location.href = cart_url;
    }
    else if (result.add_to == 1 || result.add_to == 2)
    {
        cart_goods_info.innerHTML = result.content;
        cartnumb_info_side.innerHTML = result.cart_goods_info.total.total_goods_number ? result.cart_goods_info.total.total_goods_number : 0;
        cart_goods_info_side.innerHTML = result.content_side;
		if (result.add_to == 2) {
		// 商品图片渐变到购物车特效
		var cartxy = $('.floatcart').offset();
		var cartx = cartxy.left;
		var carty = cartxy.top;
		var $img = $('#zoom1 .img2cart');
		var $imgx = $img.offset().left;
		var $imgy = $img.offset().top;
		var imgclone = $img.clone();
		$('.cloneimg').remove();
		imgclone.appendTo('body').css({'z-index':'100','position':'absolute','top':$imgy,left:$imgx,'width':$img.css('width'),'hegiht':$img.css('height')}).addClass('cloneimg').animate({left:cartx+7, top:carty+7, width:"35px",height:"35px", opacity:0.5},500);
		imgclone.animate({opacity:0}, 800, function(){
			imgclone.css({'z-index':'1'})
			});
		}
    }
    else
    {
      switch(result.confirm_type)
      {
        case '1' :
          if (confirm(result.message)) location.href = cart_url;
          break;
        case '2' :
          if (!confirm(result.message)) location.href = cart_url;
          break;
        case '3' :
          location.href = cart_url;
          break;
        default :
          break;
      }
    }
  }
}
/* *
 * 添加商品到收藏夹
 */
function collect(goodsId)
{
  Ajax.call('/user.php?act=collect', 'id=' + goodsId, collectResponse, 'GET', 'JSON');
}
/* *
 * 处理收藏商品的反馈信息
 */
function collectResponse(result)
{
  alert(result.message);
}
/*从收藏夹中删除*/
function collect_del(goodsrecID){
	if(confirm('您确定要从收藏夹中删除选定的礼物吗？')){
		location.href="user.php?act=delete_collection&collection_id="+goodsrecID+"'";
	}
}

/* 
 * 添加兑换商品到购物车
 */

function exchange_goods(goodsId)
{

  var goods        = new Object();
  var spec_arr     = new Array();
  var fittings_arr = new Array();
  var number       = 1;
  var formBuy      = document.forms['ECS_FORMBUY'];
  var quick		   = 0;

  // 检查是否有商品规格 
  if (formBuy)
  {
    spec_arr = getSelectedAttributes(formBuy);
    if (formBuy.elements['number'])
    {
      number = formBuy.elements['number'].value;
    }
	
	quick = 1;
  }

  goods.quick    = quick;
  goods.spec     = spec_arr;
  goods.goods_id = goodsId;
  goods.number   = number;

  Ajax.call('/flow.php?step=exchange_goods', 'goods=' + $.toJSON(goods), exchangeResponse, 'POST', 'JSON');
}

/* 
 * 处理兑换商品的反馈信息
 */
function exchangeResponse(result)
{
  if (result.error > 0)
  {
    // 如果需要缺货登记，跳转
    if (result.error == 2)
    {
      if (confirm(result.message))
      {
        location.href = '/user.php?act=add_booking&id=' + result.goods_id + '&spec=' + result.product_spec;
      }
    }
    // 没选规格，弹出属性选择框
    else if (result.error == 6)
    {
      openSpeDiv(result.message, result.goods_id, result.parent);
    }
    else
    {
      alert(result.message);
    }
  }
  else
  {
    exchangeDiv(result.content);
  }
}

/* *
 * 处理会员登录的反馈信息
 */
function signInResponse(result)
{
  toggleLoader(false);
  var done    = result.substr(0, 1);
  var content = result.substr(2);
  if (done == 1)
  {
    document.getElementById('member-zone').innerHTML = content;
  }
  else
  {
    alert(content);
  }
}
/* *
 * 评论的翻页函数
 */
function gotoPage(page, id, type, rank)
{
  Ajax.call('/comment.php?act=gotopage', 'page=' + page + '&id=' + id + '&type=' + type+ '&rank=' + rank, gotoPageResponse, 'GET', 'JSON');
}
function gotoPageResponse(result)
{
  if(result.type==1){
       document.getElementById("ECS_COMMENT").innerHTML = result.content;
  }else{
       document.getElementById(result.model+"_comment").innerHTML = result.content;
  }
}
/* *
 * 商品购买记录的翻页函数
 */
function gotoBuyPage(page, id)
{
  Ajax.call('goods.php?act=gotopage', 'page=' + page + '&id=' + id, gotoBuyPageResponse, 'GET', 'JSON');
}
function gotoBuyPageResponse(result)
{
  document.getElementById("ECS_BOUGHT").innerHTML = result.result;
}
/* *
 * 取得格式化后的价格
 * @param : float price
 */
function getFormatedPrice(price)
{
  if (currencyFormat.indexOf("%s") > - 1)
  {
    return currencyFormat.replace('%s', advFormatNumber(price, 2));
  }
  else if (currencyFormat.indexOf("%d") > - 1)
  {
    return currencyFormat.replace('%d', advFormatNumber(price, 0));
  }
  else
  {
    return price;
  }
}
/* *
 * 夺宝奇兵会员出价
 */
function bid(step)
{
  var price = '';
  var msg   = '';
  if (step != - 1)
  {
    var frm = document.forms['formBid'];
    price   = frm.elements['price'].value;
    id = frm.elements['snatch_id'].value;
    if (price.length == 0)
    {
      msg += price_not_null + '\n';
    }
    else
    {
      var reg = /^[\.0-9]+/;
      if ( ! reg.test(price))
      {
        msg += price_not_number + '\n';
      }
    }
  }
  else
  {
    price = step;
  }
  if (msg.length > 0)
  {
    alert(msg);
    return;
  }
  Ajax.call('snatch.php?act=bid&id=' + id, 'price=' + price, bidResponse, 'POST', 'JSON')
}
/* *
 * 夺宝奇兵会员出价反馈
 */
function bidResponse(result)
{
  if (result.error == 0)
  {
    document.getElementById('ECS_SNATCH').innerHTML = result.content;
    if (document.forms['formBid'])
    {
      document.forms['formBid'].elements['price'].focus();
    }
    newPrice(); //刷新价格列表
  }
  else
  {
    alert(result.content);
  }
}
/* *
 * 夺宝奇兵最新出价
 */
function newPrice(id)
{
  Ajax.call('snatch.php?act=new_price_list&id=' + id, '', newPriceResponse, 'GET', 'TEXT');
}
/* *
 * 夺宝奇兵最新出价反馈
 */
function newPriceResponse(result)
{
  document.getElementById('ECS_PRICE_LIST').innerHTML = result;
}
/* *
 *  返回属性列表
 */
function getAttr(cat_id)
{
  var tbodies = document.getElementsByTagName('tbody');
  for (i = 0; i < tbodies.length; i ++ )
  {
    if (tbodies[i].id.substr(0, 10) == 'goods_type')tbodies[i].style.display = 'none';
  }
  var type_body = 'goods_type_' + cat_id;
  try
  {
    document.getElementById(type_body).style.display = '';
  }
  catch (e)
  {
  }
}
/* *
 * 截取小数位数
 */
function advFormatNumber(value, num) // 四舍五入
{
  var a_str = formatNumber(value, num);
  var a_int = parseFloat(a_str);
  if (value.toString().length > a_str.length)
  {
    var b_str = value.toString().substring(a_str.length, a_str.length + 1);
    var b_int = parseFloat(b_str);
    if (b_int < 5)
    {
      return a_str;
    }
    else
    {
      var bonus_str, bonus_int;
      if (num == 0)
      {
        bonus_int = 1;
      }
      else
      {
        bonus_str = "0."
        for (var i = 1; i < num; i ++ )
        bonus_str += "0";
        bonus_str += "1";
        bonus_int = parseFloat(bonus_str);
      }
      a_str = formatNumber(a_int + bonus_int, num)
    }
  }
  return a_str;
}
function formatNumber(value, num) // 直接去尾
{
  var a, b, c, i;
  a = value.toString();
  b = a.indexOf('.');
  c = a.length;
  if (num == 0)
  {
    if (b != - 1)
    {
      a = a.substring(0, b);
    }
  }
  else
  {
    if (b == - 1)
    {
      a = a + ".";
      for (i = 1; i <= num; i ++ )
      {
        a = a + "0";
      }
    }
    else
    {
      a = a.substring(0, b + num + 1);
      for (i = c; i <= b + num; i ++ )
      {
        a = a + "0";
      }
    }
  }
  return a;
}
/* *
 * 根据当前shiping_id设置当前配送的的保价费用，如果保价费用为0，则隐藏保价费用
 *
 * return       void
 */
function set_insure_status()
{
  // 取得保价费用，取不到默认为0
  var shippingId = getRadioValue('shipping');
  var insure_fee = 0;
  if (shippingId > 0)
  {
    if (document.forms['theForm'].elements['insure_' + shippingId])
    {
      insure_fee = document.forms['theForm'].elements['insure_' + shippingId].value;
    }
    // 每次取消保价选择
    if (document.forms['theForm'].elements['need_insure'])
    {
      document.forms['theForm'].elements['need_insure'].checked = false;
    }
    // 设置配送保价，为0隐藏
    if (document.getElementById("ecs_insure_cell"))
    {
      if (insure_fee > 0)
      {
        document.getElementById("ecs_insure_cell").style.display = '';
        setValue(document.getElementById("ecs_insure_fee_cell"), getFormatedPrice(insure_fee));
      }
      else
      {
        document.getElementById("ecs_insure_cell").style.display = "none";
        setValue(document.getElementById("ecs_insure_fee_cell"), '');
      }
    }
  }
}
/* *
 * 当支付方式改变时出发该事件
 * @param       pay_id      支付方式的id
 * return       void
 */
function changePayment(pay_id)
{
  // 计算订单费用
  calculateOrderFee();
}
function getCoordinate(obj)
{
  var pos =
  {
    "x" : 0, "y" : 0
  }
  pos.x = document.body.offsetLeft;
  pos.y = document.body.offsetTop;
  do
  {
    pos.x += obj.offsetLeft;
    pos.y += obj.offsetTop;
    obj = obj.offsetParent;
  }
  while (obj.tagName.toUpperCase() != 'BODY')
  return pos;
}
function showCatalog(obj)
{
  var pos = getCoordinate(obj);
  var div = document.getElementById('ECS_CATALOG');
  if (div && div.style.display != 'block')
  {
    div.style.display = 'block';
    div.style.left = pos.x + "px";
    div.style.top = (pos.y + obj.offsetHeight - 1) + "px";
  }
}
function hideCatalog(obj)
{
  var div = document.getElementById('ECS_CATALOG');
  if (div && div.style.display != 'none') div.style.display = "none";
}
function sendHashMail()
{
  Ajax.call('user.php?act=send_hash_mail', '', sendHashMailResponse, 'GET', 'JSON')
}
function sendHashMailResponse(result)
{
  alert(result.message);
}
/* 订单查询 */
function orderQuery()
{
  var div = document.getElementById('ECS_ORDER_QUERY');
  var logistics = document.getElementById('logistics_information');
  var order_sn = document.forms['ecsOrderQuery']['order_sn'].value;
  var reg = /^[\d]+/;
  
  if (order_sn.length == 0)
  {
	 alert('请输入您要查询的订单号');
	 return;
  }
  else if ((order_sn.length == 10 || order_sn.length == 13) && reg.test(order_sn))
  {
      logistics.innerHTML = "";
      div.innerHTML = '<img src="/themes/BigSale/images/loading.gif" alt="查询中···"/>';
      Ajax.call('/user.php?act=order_query&order_sn=s' + order_sn, '', orderQueryResponse, 'GET', 'JSON');
  }else{
	  alert("请输入有效的订单号或礼品册号");
      return;
  }
}
function wuliu_query(order_sn)
{
  var reg = /^[\d]+/;
  if (reg.test(order_sn))
  {
    Ajax.call('/user.php?act=order_query&order_sn=s' + order_sn, '', orderQueryResponse, 'GET', 'JSON');
  }else{
    alert("请输入有效的订单号或礼品册号");
    return false;
  }
}
function ontimeout(){ 
    var div = document.getElementById('logistics_information');
	div.innerHTML = '<span style="color:#f00">物流信息查询超时，请单击<a href="javascript:onclick=orderQuery();">刷新</a>或稍后再试！</span>';
}
function CountDown(obj){
	var obj=eval("("+obj+")");
	
	if(obj.status!=200){   
		ontimeout();
	}   
}
function message_detail(dataObj){
    var dataObj=eval("("+dataObj+")");
	var html = '<table width="520px" cellspacing="0" cellpadding="0" border="0" style="border-collapse: collapse; border-spacing: 0pt;"><tr><td style="border-bottom:1px solid #ff9e40;font-size:14px;font-weight:bold;height:20px;">物流信息</td></tr></table><br />';
	
    if(dataObj.status==200){                   
          html+='<table width="520px" cellspacing="0" cellpadding="0" border="0" style="border-collapse: collapse; border-spacing: 0pt;">'; 
          html+='<tr>';                   
          html+='<td width="26%" style="background-color:#fff8ed;border:1px solid #ff9e40;border-right:none;font-size:14px;font-weight:bold;height:20px;text-indent:5px;">';
          html+='时间';                   
          html+='</td>';                   
          html+='<td width="10%" style="background-color:#fff8ed;border-top:1px solid #ff9e40;border-bottom:1px solid #ff9e40;font-size:14px;font-weight:bold;height:20px;text-indent:5px;">&nbsp;</td>';
          html+='<td style="background-color:#fff8ed;border:1px solid #ff9e40;font-size:14px;font-weight:bold;height:20px;text-indent:5px;">';                 
          html+='地点和跟踪进度';                   
          html+='</td>';                   
          html+='</tr>';
		  
		  dataObj.data.reverse();
          //输出data的子对象变量                   
          $.each(dataObj.data,function(idx,item){
			 if(item.time != "" ){
              html+='<tr>';                           
              html+='<td style="border:1px solid #d8d8d8; border-right: none; background-color:#fbfbfb;font-size: 12px;padding:3px 5px;">';
              html+=item.time;// 每条数据的时间
              html+='</td>';
			  if(idx == 0 ){html+='<td style="border-top:1px solid #d8d8d8;border-bottom:1px solid #d8d8d8;font-size: 12px;padding:3px 5px; background:#fbfbfb url(../themes/BigSale/images/ico_status.gif) no-repeat -2px -22px; height: 51px;">';}else if(idx == dataObj.data.length-1){if( dataObj.state == 3 ){html+='<td style="border-top:1px solid #d8d8d8;border-bottom:1px solid #d8d8d8;font-size: 12px;padding:3px 5px; background:#fbfbfb url(../themes/BigSale/images/ico_status.gif) no-repeat -152px -22px; height: 51px;">';}else{html+='<td style="border-top:1px solid #d8d8d8;border-bottom:1px solid #d8d8d8;font-size: 12px;padding:3px 5px; background:#fbfbfb url(../themes/BigSale/images/ico_status.gif) no-repeat -102px -22px; height: 51px;">';}}else{html+='<td style="border-top:1px solid #d8d8d8;border-bottom:1px solid #d8d8d8;font-size: 12px;padding:3px 5px; background:#fbfbfb url(../themes/BigSale/images/ico_status.gif) no-repeat -52px -22px; height: 51px;">';};
			  html+='</td>';
              html+='<td style="border:1px solid #d8d8d8; border-left: none; background-color:#fbfbfb;font-size: 12px;padding:3px 5px;">';                           
              html+=item.context;// 每条数据的状态                          
              html+='</td>';
           	  html+='</tr>';
			 }
           });
          html+='</table>';
    }else{
		//查询不到
		html+='<span style="color:#f00">'+dataObj.message+'</span>';
 	}
	clearTimeout(timer);
	var div = document.getElementById('logistics_information');
	div.innerHTML = html;
}
function orderQueryResponse(result)
{
  var div = document.getElementById('ECS_ORDER_QUERY');
  if (div && result.message.length > 0){
    div.innerHTML = '';
  }
  if (div && result.error == 0){
    div.innerHTML = result.content;
  }
  
  if (result.message.length > 0)
  {
    alert(result.message);
  }
  if (result.error == 0)
  {
    if (result.status == 1)
	{   
	    var logistics = document.getElementById('logistics_information');
	    logistics.innerHTML = '<img src="/themes/BigSale/images/loading.gif" alt="查询中···"/>';
		timer = setTimeout(function(){CountDown(result.information);},10000);
		message_detail(result.information);
	}
  }
}
function display_mode(str)
{
    document.getElementById('display').value = str;
    setTimeout(doSubmit, 0);
    function doSubmit() {document.forms['listform'].submit();}
}
function display_mode_wholesale(str)
{
    document.getElementById('display').value = str;
    setTimeout(doSubmit, 0);
    function doSubmit() 
    {
        document.forms['wholesale_goods'].action = "wholesale.php";
        document.forms['wholesale_goods'].submit();
    }
}

function hash(string, length)
{
  var length = length ? length : 32;
  var start = 0;
  var i = 0;
  var result = '';
  filllen = length - string.length % length;
  for(i = 0; i < filllen; i++)
  {
    string += "0";
  }
  while(start < string.length)
  {
    result = stringxor(result, string.substr(start, length));
    start += length;
  }
  return result;
}
function stringxor(s1, s2)
{
  var s = '';
  var hash = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
  var max = Math.max(s1.length, s2.length);
  for(var i=0; i<max; i++)
  {
    var k = s1.charCodeAt(i) ^ s2.charCodeAt(i);
    s += hash.charAt(k % 52);
  }
  return s;
}
var evalscripts = new Array();
function evalscript(s)
{
  if(s.indexOf('<script') == -1) return s;
  var p = /<script[^\>]*?src=\"([^\>]*?)\"[^\>]*?(reload=\"1\")?(?:charset=\"([\w\-]+?)\")?><\/script>/ig;
  var arr = new Array();
  while(arr = p.exec(s)) appendscript(arr[1], '', arr[2], arr[3]);
  return s;
}
function $(id)
{
    return document.getElementById(id);
}
function appendscript(src, text, reload, charset)
{
  var id = hash(src + text);
  if(!reload && in_array(id, evalscripts)) return;
  if(reload && $(id))
  {
    $(id).parentNode.removeChild($(id));
  }
  evalscripts.push(id);
  var scriptNode = document.createElement("script");
  scriptNode.type = "text/javascript";
  scriptNode.id = id;
  //scriptNode.charset = charset;
  try
  {
    if(src)
    {
      scriptNode.src = src;
    }
    else if(text)
    {
      scriptNode.text = text;
    }
    $('append_parent').appendChild(scriptNode);
  }
  catch(e)
  {}
}
function in_array(needle, haystack)
{
  if(typeof needle == 'string' || typeof needle == 'number')
  {
    for(var i in haystack)
    {
      if(haystack[i] == needle)
      {
        return true;
      }
    }
  }
  return false;
}
var pmwinposition = new Array();
var userAgent = navigator.userAgent.toLowerCase();
var is_opera = userAgent.indexOf('opera') != -1 && opera.version();
var is_moz = (navigator.product == 'Gecko') && userAgent.substr(userAgent.indexOf('firefox') + 8, 3);
var is_ie = (userAgent.indexOf('msie') != -1 && !is_opera) && userAgent.substr(userAgent.indexOf('msie') + 5, 3);
function pmwin(action, param)
{
  var objs = document.getElementsByTagName("OBJECT");
  if(action == 'open')
  {
    for(i = 0;i < objs.length; i ++)
    {
      if(objs[i].style.visibility != 'hidden')
      {
        objs[i].setAttribute("oldvisibility", objs[i].style.visibility);
        objs[i].style.visibility = 'hidden';
      }
    }
    var clientWidth = document.body.clientWidth;
    var clientHeight = document.documentElement.clientHeight ? document.documentElement.clientHeight : document.body.clientHeight;
    var scrollTop = document.body.scrollTop ? document.body.scrollTop : document.documentElement.scrollTop;
    var pmwidth = 800;
    var pmheight = clientHeight * 0.9;
    if(!$('pmlayer'))
    {
      div = document.createElement('div');div.id = 'pmlayer';
      div.style.width = pmwidth + 'px';
      div.style.height = pmheight + 'px';
      div.style.left = ((clientWidth - pmwidth) / 2) + 'px';
      div.style.position = 'absolute';
      div.style.zIndex = '999';
      $('append_parent').appendChild(div);
      $('pmlayer').innerHTML = '<div style="width: 800px; background: #666666; margin: 5px auto; text-align: left">' +
        '<div style="width: 800px; height: ' + pmheight + 'px; padding: 1px; background: #FFFFFF; border: 1px solid #7597B8; position: relative; left: -6px; top: -3px">' +
        '<div onmousedown="pmwindrag(event, 1)" onmousemove="pmwindrag(event, 2)" onmouseup="pmwindrag(event, 3)" style="cursor: move; position: relative; left: 0px; top: 0px; width: 800px; height: 30px; margin-bottom: -30px;"></div>' +
        '<a href="###" onclick="pmwin(\'close\')"><img style="position: absolute; right: 20px; top: 15px" src="images/close.gif" title="关闭" /></a>' +
        '<iframe id="pmframe" name="pmframe" style="width:' + pmwidth + 'px;height:100%" allowTransparency="true" frameborder="0"></iframe></div></div>';
    }
    $('pmlayer').style.display = '';
    $('pmlayer').style.top = ((clientHeight - pmheight) / 2 + scrollTop) + 'px';
    if(!param)
    {
        pmframe.location = 'pm.php';
    }
    else
    {
        pmframe.location = 'pm.php?' + param;
    }
  }
  else if(action == 'close')
  {
    for(i = 0;i < objs.length; i ++)
    {
      if(objs[i].attributes['oldvisibility'])
      {
        objs[i].style.visibility = objs[i].attributes['oldvisibility'].nodeValue;
        objs[i].removeAttribute('oldvisibility');
      }
    }
    hiddenobj = new Array();
    $('pmlayer').style.display = 'none';
  }
}
var pmwindragstart = new Array();
function pmwindrag(e, op)
{
  if(op == 1)
  {
    pmwindragstart = is_ie ? [event.clientX, event.clientY] : [e.clientX, e.clientY];
    pmwindragstart[2] = parseInt($('pmlayer').style.left);
    pmwindragstart[3] = parseInt($('pmlayer').style.top);
    doane(e);
  }
  else if(op == 2 && pmwindragstart[0])
  {
    var pmwindragnow = is_ie ? [event.clientX, event.clientY] : [e.clientX, e.clientY];
    $('pmlayer').style.left = (pmwindragstart[2] + pmwindragnow[0] - pmwindragstart[0]) + 'px';
    $('pmlayer').style.top = (pmwindragstart[3] + pmwindragnow[1] - pmwindragstart[1]) + 'px';
    doane(e);
  }
  else if(op == 3)
  {
    pmwindragstart = [];
    doane(e);
  }
}
function doane(event)
{
  e = event ? event : window.event;
  if(is_ie)
  {
    e.returnValue = false;
    e.cancelBubble = true;
  }
  else if(e)
  {
    e.stopPropagation();
    e.preventDefault();
  }
}
/* *
 * 添加礼包到购物车
 */
function addPackageToCart(packageId)
{
  var package_info = new Object();
  var number       = 1;
  package_info.package_id = packageId
  package_info.number     = number;
  Ajax.call('flow.php?step=add_package_to_cart', 'package_info=' + $.toJSON(package_info), addPackageToCartResponse, 'POST', 'JSON');
}
/* *
 * 处理添加礼包到购物车的反馈信息
 */
function addPackageToCartResponse(result)
{
  if (result.error > 0)
  {
    if (result.error == 2)
    {
      if (confirm(result.message))
      {
        location.href = '/user.php?act=add_booking&id=' + result.goods_id;
      }
    }
    else
    {
      alert(result.message);    
    }
  }
  else
  {
    var cartInfo = document.getElementById('ECS_CARTINFO');
    var cart_url = 'flow.php?step=cart';
    if (cartInfo)
    {
      cartInfo.innerHTML = result.content;
    }
    if (result.one_step_buy == '1')
    {
      location.href = cart_url;
    }
    else
    {
      switch(result.confirm_type)
      {
        case '1' :
          if (confirm(result.message)) location.href = cart_url;
          break;
        case '2' :
          if (!confirm(result.message)) location.href = cart_url;
          break;
        case '3' :
          location.href = cart_url;
          break;
        default :
          break;
      }
    }
  }
}
function setSuitShow(suitId)
{
    var suit    = document.getElementById('suit_'+suitId);
    if(suit == null)
    {
        return;
    }
    if(suit.style.display=='none')
    {
        suit.style.display='';
    }
    else
    {
        suit.style.display='none';
    }
}
/* 以下四个函数为属性选择弹出框的功能函数部分 */
//检测层是否已经存在
function docEle() 
{
  return document.getElementById(arguments[0]) || false;
}
//生成属性选择层
function openSpeDiv(message, goods_id, parent) 
{
  var _id = "speDiv";
  var m = "mask";
  if (docEle(_id)) document.removeChild(docEle(_id));
  if (docEle(m)) document.removeChild(docEle(m));
  //计算上卷元素值
  var scrollPos; 
  if (typeof window.pageYOffset != 'undefined') 
  { 
    scrollPos = window.pageYOffset; 
  } 
  else if (typeof document.compatMode != 'undefined' && document.compatMode != 'BackCompat') 
  { 
    scrollPos = document.documentElement.scrollTop; 
  } 
  else if (typeof document.body != 'undefined') 
  { 
    scrollPos = document.body.scrollTop; 
  }
  var i = 0;
  var sel_obj = document.getElementsByTagName('select');
  while (sel_obj[i])
  {
    sel_obj[i].style.visibility = "hidden";
    i++;
  }
  // 新激活图层
  var newDiv = document.createElement("div");
  newDiv.id = _id;
  newDiv.style.position = "absolute";
  newDiv.style.zIndex = "10000";
  newDiv.style.width = "420px";
  newDiv.style.height = "220px";
  newDiv.style.top = (parseInt(scrollPos + 200)) + "px";
  newDiv.style.left = (parseInt(document.body.offsetWidth) - 420) / 2 + "px"; // 屏幕居中
  newDiv.style.overflow = "auto"; 
  newDiv.style.background = "#FFF";
  newDiv.style.border = "1px solid #b41b1c";
  //生成层内内容
  var html_code = '<h4>' + select_spe + "<a href='javascript:cancel_div()'></a></h4>";
  html_code += "<ul class='goodprop'>";
  for (var spec = 0; spec < message.length; spec++)
  {
      html_code += '<li><span>' +  message[spec]['name'] + '：</span>';
      if (message[spec]['attr_type'] == 1)
      { html_code += "<div class='catt'>";
        for (var val_arr = 0; val_arr < message[spec]['values'].length; val_arr++)
        {
          if (val_arr == 0)
          {
            html_code += "<a class='cattsel' onclick='changeAtt(this)' href='javascript:;' name='" + message[spec]['values'][val_arr]['id'] + "' title='" + message[spec]['values'][val_arr]['label'] + "'>" + message[spec]['values'][val_arr]['label'] + "<input style='display:none' id='spec_value_" + message[spec]['values'][val_arr]['id'] + "' type='radio' name='spec_" + message[spec]['attr_id'] + "' value='" + message[spec]['values'][val_arr]['id'] + "' checked /><i></i></a>";      
          }
          else
          {
            html_code += "<a onclick='changeAtt(this)' href='javascript:;' name='" + message[spec]['values'][val_arr]['id'] + "' title='" + message[spec]['values'][val_arr]['label'] + "'>" + message[spec]['values'][val_arr]['label'] + "<input style='display:none' id='spec_value_" + message[spec]['values'][val_arr]['id'] + "' type='radio' name='spec_" + message[spec]['attr_id'] + "' value='" + message[spec]['values'][val_arr]['id'] + "' /><i></i></a>";      
          }
        } 
		html_code += "</div>";
        html_code += "<input type='hidden' name='spec_list' value='" + val_arr + "' />";
      }
      else
      {
        for (var val_arr = 0; val_arr < message[spec]['values'].length; val_arr++)
        {
          html_code += "<input style='margin-left:15px;' type='checkbox' name='spec_" + message[spec]['attr_id'] + "' value='" + message[spec]['values'][val_arr]['id'] + "' id='spec_value_" + message[spec]['values'][val_arr]['id'] + "' /><font color=#555555>" + message[spec]['values'][val_arr]['label'] + ' [' + message[spec]['values'][val_arr]['format_price'] + ']</font><br />';     
        }
        html_code += "<input type='hidden' name='spec_list' value='" + val_arr + "' />";
      }
	  html_code += "</li>";
  }
  html_code += "</ul>";
  html_code += "<a href='javascript:submit_div(" + goods_id + "," + parent + ")' class='buynow' >立即购买</a><a href='javascript:cancel_div()' class='cancel' >" + is_cancel + "</a>";
  newDiv.innerHTML = html_code;
  document.body.appendChild(newDiv);
  // mask图层
  var newMask = document.createElement("div");
  newMask.id = m;
  newMask.style.position = "absolute";
  newMask.style.zIndex = "9999";
  newMask.style.width = document.body.scrollWidth + "px";
  newMask.style.height = document.body.scrollHeight + "px";
  newMask.style.top = "0px";
  newMask.style.left = "0px";
  newMask.style.background = "#FFF";
  newMask.style.filter = "alpha(opacity=30)";
  newMask.style.opacity = "0.40";
  document.body.appendChild(newMask);
} 
//获取选择属性后，再次提交到购物车
function submit_div(goods_id, parentId)
{
  var goods        = new Object();
  var spec_arr     = new Array();
  var fittings_arr = new Array();
  var number       = 1;
  var input_arr      = document.getElementsByTagName('input'); 
  var quick		   = 1;
  var spec_arr = new Array();
  var j = 0;
  for (i = 0; i < input_arr.length; i ++ )
  {
    var prefix = input_arr[i].name.substr(0, 5);
    if (prefix == 'spec_' && (((input_arr[i].type == 'radio' || input_arr[i].type == 'checkbox') && input_arr[i].checked)))
    {
      spec_arr[j] = input_arr[i].value;
      j++ ;
    }
  }
  goods.quick    = quick;
  goods.spec     = spec_arr;
  goods.goods_id = goods_id;
  goods.number   = number;
  goods.parent   = (typeof(parentId) == "undefined") ? 0 : parseInt(parentId);
  Ajax.call('/flow.php?step=add_to_cart', 'goods=' + $.toJSON(goods), addToCartResponse, 'POST', 'JSON');
  document.body.removeChild(docEle('speDiv'));
  document.body.removeChild(docEle('mask'));
  var i = 0;
  var sel_obj = document.getElementsByTagName('select');
  while (sel_obj[i])
  {
    sel_obj[i].style.visibility = "";
    i++;
  }
}
// 关闭mask和新图层
function cancel_div() 
{
  document.body.removeChild(docEle('speDiv'));
  document.body.removeChild(docEle('mask'));
  var i = 0;
  var sel_obj = document.getElementsByTagName('select');
  while (sel_obj[i])
  {
    sel_obj[i].style.visibility = "";
    i++;
  }
}
/*
 * 兑换弹出层
 */
function exchangeDiv(content) 
{
  var id = "speDiv";
  var m = "mask";
  if (docEle(id)) document.removeChild(docEle(id));
  if (docEle(m)) document.removeChild(docEle(m));
  //计算上卷元素值
  var scrollPos; 
  if (typeof window.pageYOffset != 'undefined') 
  { 
    scrollPos = window.pageYOffset; 
  } 
  else if (typeof document.compatMode != 'undefined' && document.compatMode != 'BackCompat') 
  { 
    scrollPos = document.documentElement.scrollTop; 
  } 
  else if (typeof document.body != 'undefined') 
  { 
    scrollPos = document.body.scrollTop; 
  }
  var i = 0;
  var sel_obj = document.getElementsByTagName('select');
  while (sel_obj[i])
  {
    sel_obj[i].style.visibility = "hidden";
    i++;
  }
  
  var isIE = (document.all) ? true : false;
  var isIE6 = isIE && ([/MSIE (\d)\.0/i.exec(navigator.userAgent)][0][1] == 6);
  // 新激活图层
  var newDiv = document.createElement("div");
  newDiv.id = id;
  newDiv.style.position = !isIE6 ? "fixed" : "absolute";
  newDiv.style.zIndex = "10000";
  newDiv.style.width = "540px";
  newDiv.style.height = "290px";
  newDiv.style.top = (parseInt(document.documentElement.clientHeight)-290) / 2 + "px";
  newDiv.style.left = (parseInt(document.body.offsetWidth) - 540) / 2 + "px"; // 屏幕居中
  newDiv.style.overflow = "auto"; 
  newDiv.style.background = "#FFF";
  newDiv.style.border = "1px solid #CCC";
  //生成层内内容
  newDiv.innerHTML = content;
  document.body.appendChild(newDiv);
  // mask图层
  var newMask = document.createElement("div");
  newMask.id = m;
  newMask.style.position = "absolute";
  newMask.style.zIndex = "9999";
  newMask.style.width = document.body.scrollWidth + "px";
  newMask.style.height = document.body.scrollHeight + "px";
  newMask.style.top = "0px";
  newMask.style.left = "0px";
  newMask.style.background = "#FFF";
  newMask.style.filter = "alpha(opacity=50)";
  newMask.style.opacity = "0.40";
  document.body.appendChild(newMask);
  
  if(isIE6){
	  newDiv.style.top = (parseInt(scrollPos)+(parseInt(document.documentElement.clientHeight)-290) / 2) + "px";
	  window.attachEvent("onscroll",function(){                              
		  newDiv.style.top = (parseInt(document.documentElement.scrollTop)+(parseInt(document.documentElement.clientHeight)-290) / 2) + "px";
	  });
  }
  
  newDiv.getElementsByTagName("a")[0].onclick=function(){
	 cancel_div();
  }
} 
/*
 * 帮助中心
 */
function changeBg(id1,id2,id3){
	var node=document.getElementById(id3);
    var arr1=node.childNodes;
	var arr2=new Array;
	for (var i=0;i<arr1.length;i++){
		arr2[i]=arr1[i].className;
	}
	if (arr2.join('')=='current') {
		return false;
	}
	else {
		if(node.style.display==""){
			document.getElementById(id1).style.backgroundPosition="0 0";
			document.getElementById(id2).style.backgroundPosition="0 -90px";
		}
		else {
			document.getElementById(id1).style.backgroundPosition="0 0";
			document.getElementById(id2).style.backgroundPosition="0 -60px";
		}
	}
}
function changeBgback(id1,id2,id3){
	var node=document.getElementById(id3);
    var arr1=node.childNodes;
	var arr2=new Array;
	for (var i=0;i<arr1.length;i++){
		arr2[i]=arr1[i].className;
	}
	if (arr2.join('')=='current') {
		return false;
	}
	else {
		if(node.style.display==""){
			document.getElementById(id1).style.backgroundPosition="0 -30px";
			document.getElementById(id2).style.backgroundPosition="0 -150px";
		}
		else {
			document.getElementById(id1).style.backgroundPosition="0 -30px";
			document.getElementById(id2).style.backgroundPosition="0 -120px";
		}
	}
}
function openSub(id1,id2){
	var sta=document.getElementById(id1).style.display;
	if(sta==""){
		document.getElementById(id1).style.display="none";
		document.getElementById(id2).style.backgroundPosition="0 -60px";
	}
	else {
		document.getElementById(id1).style.display="";
		document.getElementById(id2).style.backgroundPosition="0 -90px";
	}
}
function showAnswer(id){
    var sta=document.getElementById(id).style.display;
    if(sta==""){
	document.getElementById(id).style.display="none";	
	}
	else {
	document.getElementById(id).style.display="";	
	}
}
function helpmenuSwitch(t){
	$(t).next().toggle();
	$(t).toggleClass("shrink");
}

// 评价图片地址设置
var imgSrc = '/themes/BigSale/images/star_1.gif';
var imgSrc_2 = '/themes/BigSale/images/star_2.gif';
var imgSrc_3 = '/themes/BigSale/images/star_3.gif';
function rate(obj,oEvent){
	var e = oEvent || window.event;
	var target = e.target || e.srcElement; 
	var imgArray = obj.getElementsByTagName("img");
	for(var i=0;i<imgArray.length;i++){
	   imgArray[i]._num = i;
	   imgArray[i].onclick=function(){  
		   document.getElementById("star").value=this._num+1;
	   };
	}
	if(target.tagName=="IMG"){
	   for(var j=0;j<imgArray.length;j++){
		if(j<=target._num){
		 imgArray[j].src=imgSrc_3;
		} else {
		 imgArray[j].src=imgSrc;
		}
	   }
	}
}
function rating(){
	var imgArray = $('.starWrapper').find("img");
	var star = document.getElementById("star");
	for(var i=0;i<imgArray.length;i++){
		if(i<star.value){
		    imgArray[i].src=imgSrc_2;
		} else {
		    imgArray[i].src=imgSrc;
		}
	}
}
function setfocus(x){
   var user_login = document.getElementById(x);
   if (user_login.value == '用户名/手机号码'){
	   user_login.style.color='#ddd';
   }else{
	   user_login.style.color='#000';
   }
}
function setkeydown(x){
   var user_login = document.getElementById(x);
   if (user_login.value == '用户名/手机号码'){
	   user_login.value = "";
	   user_login.style.color='#000';
   }else{
	   user_login.style.color='#000';
   }
}
function setkeyup(x){
   var user_login = document.getElementById(x);
   if (user_login.value == '用户名/手机号码'){
	   user_login.style.color='#ddd';
   }else{
	   user_login.style.color='#000';
   }
}
function setblur(x){
   var user_login = document.getElementById(x);
   if (user_login.value == '') {
       user_login.value = '用户名/手机号码';
   }
   if (user_login.value == '用户名/手机号码'){
	   user_login.style.color='#999';
   }else{
	   user_login.style.color='#000';
   }
}
function is_login(data){
   if (data.code == 0){
	 var getPosLeft = ($(window).width() - 400) / 2; 
	 var getPosTop = ($(window).height() - 250) / 2;
	 $("#t_login").dialog({
			modal: true,
			resizable: false,
			width: 400,
			height: 250,
			position:[getPosLeft,getPosTop],
			bgiframe: true
	});
	if (data.enabled_captcha==true){
	    $('#loginbox tr').each(function (idx) {                
			if (idx==2){
				$(this ).html("<td class='bfont'>验证码：</td><td class='inputbox left'><input type='hidden' value='true' name='enabled_captcha' id='set_captcha' /><input type='text' class='txtbox' size='5' name='captcha' style='width: 80px; margin-right: 5px;' /><img src='captcha.php?is_login=1&"+Math.random()+"' alt='captcha' style='vertical-align: middle;cursor: pointer;' onClick='this.src=\"captcha.php?is_login=1&\"+Math.random()' /></td>");
				$(this).show();
			}
		});
    }
	$(window).scroll(function(){
		var mytop = $(document).scrollTop();
		var getPosLeft = ($(window).width() - 400) / 2; 
		var getPosTop = ($(window).height() - 250) / 2;
		$(".ui-dialog").css({"left":getPosLeft,"top":getPosTop+mytop});
	});
	$(window).resize(function(){
		var mytop = $(document).scrollTop();
		var getPosLeft = ($(window).width() - 400) / 2; 
		var getPosTop = ($(window).height() - 250) / 2; 
		$(".ui-dialog").css({"left":getPosLeft,"top":getPosTop+mytop});
	});
  }else{
	 window.location.href = $('#to_pay').attr("name");
  }
}
function check_user(data){
	if (data.message.length > 0){
	   $("#point").html(data.message);
	   if (data.code=='enabled_captcha'){
		   $('#loginbox').find('[name=password]').attr('value','');
		   $('#loginbox').find('[name=password]').focus();
		   $('#loginbox tr').each(function (idx) {                
                if (idx==2){
					$(this ).html("<td class='bfont'>验证码：</td><td class='inputbox left'><input type='hidden' value='true' name='enabled_captcha' id='set_captcha' /><input type='text' size='5' class='txtbox' name='captcha' style='width: 80px; margin-right: 5px;' /><img src='captcha.php?is_login=1&"+Math.random()+"' alt='captcha' style='vertical-align: middle;cursor: pointer;' onClick='this.src=\"captcha.php?is_login=1&\"+Math.random()' /></td>").show();
	            }
            });
	   }
	}else{
		window.location.href = $('#to_pay').attr("name");
	}
}
/**
 * 点选可选属性或改变数量时修改商品价格的函数
 */
function changeAtt(t) {
	//t.lastChild.checked='checked';
	$(t).children("input").attr('checked','checked');
	var len = $(t).parent().children().length;
	for (var i = 0; i<len;i++) {
			if (t.parentNode.childNodes[i].className == 'cattsel') {
			   t.parentNode.childNodes[i].className = '';
			}
	}
	
	t.className = "cattsel";
	changePrice();
	//changeNumber();
}

function colorStyle(id,color1,color2){
  var elem = document.getElementById(id);
  if(elem.getAttribute("id") == id){
	  //elem.className = color1;
	  if(elem.className == color1)
		   elem.className = color2;
		else
		   elem.className = color1; 
	}
}
function picturs(){
	var goodsimg = document.getElementById("goodsimg");
	var imglist = document.getElementById("imglist");
	var imgnum = imglist.getElementsByTagName("img");
	for(var i = 0; i<imgnum.length; i++){
		imgnum[i].onclick=function(){
			for(var j=0; j<imgnum.length; j++){
				if(imgnum[j].getAttribute("class") =="onbg" || imgnum[j].getAttribute("className") =="onbg"){
					imgnum[j].className = "autobg";
					break;
				}
			}
			this.className = "onbg";
		}
	}
}
function Switch(t,id1,id2,ele1,ele2){
	var a_con=$("#"+id1+" "+ele1);
	var ct=$("#"+id2+" > "+ele2);
	if($(t).hasClass("current")){return;}
	else {
		for(var i=0;i<a_con.length;i++){
			a_con[i].num=i;
		}
		a_con.removeClass("current");
		$(t).addClass("current");
		ct.hide();
		ct.eq(t.num).show();
	}
}
function showCat(obj){
	$(obj).css('background-position','0px -45px');
	$(obj).find(".allcat_noidx").show();
	obj.onmouseout=function(){
		$(obj).css('background-position','0px -0px');
		$(obj).find(".allcat_noidx").hide();
	}
}
function changeTips(th){
	var passtext = document.getElementById('passtext_login');
	var password = document.getElementById('password_login');
	if (th.id ==='password_login'){
		if(th.value =='' || th.value.length == 0){
			th.style.display = 'none';
			passtext.style.display = 'block';
			}
	}
	else {
		th.style.display = 'none';
		password.style.display = 'block';
		password.focus();
	}
}
function check_giftcard() {
  var card_sn = document.getElementById('lpcnumb').value.trim();
  var card_pwd = document.getElementById('lpcpassword').value;
  if (card_sn.length == 0) {
    alert('请输入礼品册号！');
    return false;
  } else {
    if (card_sn.match(/^([0-9]{9,11})$/) == null) {
      alert('对不起，您输入的礼品册格式不正确!');
      return false;
    } else if (card_pwd.length == 0) {
      alert('请输入礼品册密码！');
      return false;
    }
  }
}