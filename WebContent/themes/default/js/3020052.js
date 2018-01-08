$(function () {
    // side Menu


    // conditional filter
    function conditionalFilter(){
        // multi select switch
        $('.multiSelect').click(function (){
            // clear status
            $(this).parents('.filter-row').find('.condition-listContent li').removeClass('active');

            $('.filter-row .all').addClass('active');

            $(this).toggleClass('active').parents('.filter-conditional')
                .find('.condition-wrap').toggleClass('active');
        });

        // brand select cancel
        $('.cancelBtn').click(function (){
            $('.filter-row *').removeClass('active');
            $('.filter-row .all').addClass('active');
        });

        // bind condition item
        var parent = $('.filter-conditional');

        parent.find('.condition-list li').click(function (){
            var that = $(this);

            if (that.index() == 0){
                // if  checked 'all'
                that.addClass('active')
                    .siblings().removeClass('active');
            } else {

                // check multi select status
                // multi select model
                if (that.parents('.condition-list').siblings('.multiSelect').hasClass('active')){
                    // is selectAll now
                    if (that.siblings('.all').hasClass('active')) {
                        that.siblings('.all').removeClass('active');
                        that.toggleClass('active');
                    } else {
                        that.toggleClass('active');

                        // if only self has active
                        if (that.parents('.condition-wrap').find('li.active').length == 0) {
                            that.siblings('.all').addClass('active');
                        } else {
                        }
                    }
                } else {
                    // single select model
                    that.toggleClass('active')
                        .siblings().removeClass('active');

                    // if only self has active
                    if (that.parents('.condition-list').find('li.active').length == 0) {
                        that.siblings('.all').addClass('active');
                    } else {
                    }
                }

                var curParent = that.parent();
                if(curParent.is("[isBrand='true']")){
                    var selectBrandIds = $("li.active",curParent).find("input:hidden").map(function(){
                        return $(this).val();
                    }).get().join(",");

                    $(".btnPrimary",parent).attr("href",builtMultiUrl("brands",selectBrandIds));

                }




            }
        });

        // slide more filter
        var moreAttrBtnObj = $('.filter-more');
        var backToggleStr = "";
        if(moreAttrBtnObj && moreAttrBtnObj.length > 0){
            backToggleStr = moreAttrBtnObj.find("p").html();
            moreAttrBtnObj.click(function (){
                if($(this).is(".active")){
                    $('.hzyfilterMod .filter-row:gt(3)').hide();
                    $(this).removeClass("active").find("p").html(backToggleStr)
                }else{
                    $('.hzyfilterMod .filter-row').show();
                    $(this).addClass("active").find("p").html("收起<i></i>")
                }
//            $(this).toggleClass('active');
            });
        }

    }
    conditionalFilter();

    (function(){
        $(".hzyfilterMod-bd").on("click",".showMore",function(){
            var contentList = $(".condition-listContent");
            var bash = ["∨", "∧"],ico = $(this).find(".ico")
            if(contentList.hasClass("show")){
                contentList.removeClass("show");
                ico.addClass("animate");
                setTimeout(function () {
                    ico.removeClass("animate").html(bash[0]);
                }, 500);
            }else{
                contentList.addClass("show");
                ico.addClass("animate");
                setTimeout(function () {
                    ico.removeClass("animate").html(bash[1]);
                }, 500);
            }
        });
    })();


    // product filter
    function productFilter(){
        $('.filter_nav .item').click(function (){
            $('.filter_nav .item').removeClass('active');
            $(this).addClass('active').toggleClass('toggle')
                .siblings('.item').removeClass('toggle');
        });
    }
//    productFilter();


});  