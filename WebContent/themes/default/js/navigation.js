$(function(){
    $("dt[name^=midd]").hover(
        function(){
            var navid = $(this).attr("name");
            //var position = $(this).offset();
            //var top = position.top + $(this).height();
            //var left = position.left;
            $("dt[name="+navid+"]").find("a").css("background-position","-333px -4px");
            $("dt[name="+navid+"]").find("a").css("color","#fff");
        //$("#"+navid).css("top",top+"px");
        //$("#"+navid).css("left",left+"px");
        //$("#"+navid).show();
        },
        function(){
            var navid = $(this).attr("name");
            $("dt[name="+navid+"]").find("a").css("background-position","-433px -4px");
            $("dt[name="+navid+"]").find("a").css("color","#666");
        //$("#"+navid).hide();
        }
        );
})


/*种类菜单*/
function showInfoMsg(msg){
    $("#p_showmsg").html(msg);
}
// 特别推荐产品div调整
/*$(function(){
    $('#cate_tree .popup-box').each(function(n){
        $('#recommend_product_list ul:first').prependTo(this);
    });
});
 */ 
// 分类二级显示
$(function(){
    $('.cate_left > li').hover(
        function(){
			$(this).addClass("current");
            $('.popup-box', this).show();
        },
        function(){
			$(this).removeClass("current");
            $('.popup-box', this).hide();
        }
        );
});