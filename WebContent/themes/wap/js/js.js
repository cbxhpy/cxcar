$(function () {
    var str = window.location.href;
    $("#menu a").each(function () {
        var mhref = $(this).attr("href")
        if (mhref == str) {
            var mnums = $("#menu a").index($(this));
            $("#temp").attr("value", mnums);
        }
    });
    var temp = $("#temp").attr("value");
    $("#menu a").removeClass("now");
    $("#menu a").eq(temp).addClass("now")
    $("#right .probox:last-child").css("border-bottom", "0px");
    $("#left dl dt").last().find("a").css("border-bottom", "0px");
    $("#bottom .newsbox ul li:last-child").css("border-bottom", "0px");

    /*��ҳ���ߴ硶 990pxʱ ִ��menu ����/��ʾ*/
    $("#top .menu").click(function () {
        var $this = $(this);
        var offset = $(".menu").offset();
        $("#menu").css({ top: offset.top+$this.outerHeight()});
        if ($("#menu").is(":visible")) {
            $("#menu").slideUp("slow");
            $this.removeClass("open");
        } else {
            $("#menu").slideDown("slow");
            $this.addClass("open");
        }
    });

    /*�ߴ�С��700ʱ ҳ���ұߵ���js*/
    if ($(window.document).width() < 700) {
        $(".left").find("dl").hide();
        $(".left").find(".titl").each(function () {
            var $this = $(this);
            $this.click(function () {
                $this.next("dl").toggle("slow");
            });
        });
    }

    //������Ŀ������//
    var objdt = $("#left dt");
    var objdd = $("#left dd");
    objdd.hide();
    objdt.each(function () {
        var $this = $(this);
        $this.hover(function () {
            var index = objdt.index($this);
            var offset = $this.offset();
            if ($this.next("dd").find("li").length < 1) { return false; }
            $this.css({ background: "url(img/list2-bg.png) no-repeat 261px -256px" });
            objdd.parent().children("dd").eq(index).css({ position: "absolute", top: offset.top + "px", left: offset.left + 265 + "px" }).show();
        }, function () {
            objdd.filter(":visible").hide();
            $this.css({ "background-image": "none" });
        })
    });
    objdd.each(function () {
        var $this = $(this);
        $this.hover(function () {
            $this.show();
            $this.prev("dt").css({ background: "url(img/list2-bg.png) no-repeat 261px -256px" });
        }, function () {
            $this.hide();
            $this.prev("dt").css({ "background-image": "none" }); ;
        });
    });
    /*�����л�*/
    var $lang = $("#lang");
    $lang.change(function () {
        var num = $lang.find("option:selected").attr("value");
        switch (num) {
            case "1": window.location.href = "#"; break;
            case "2": window.location.href = "about.html"; break;
            case "3": window.location.href = "#"; break;
            default: break;
        }
    });


})