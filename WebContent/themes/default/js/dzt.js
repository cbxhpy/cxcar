/*f1*/
function dztTab(name,cursel,n){
	for(i=1;i<=n;i++){
		var menu=document.getElementById(name+i);
		var con=document.getElementById("con_"+name+"_"+i);
		if(menu != null)
			menu.className=i==cursel?"current":"";
		if(con != null)
		con.style.display=i==cursel?"block":"none";
	}
}

function dztTab2(cursel){
	for(i=1;i<=3;i++){
		var menu=document.getElementById("dztj" + i);
		var con=document.getElementById("con_dzt_" + i);

		if(menu != null)
			menu.className=i==cursel?"current":"";
		if(con != null)
		{
			if(window.navigator.userAgent.indexOf("MSIE")>=1)
				con.style.display=i==cursel?"block":"none";
			else 
				con.style.display=i==cursel?"table":"none";
		}
	}
}





/*f2*/

function phbTab(name,cursel,n){
	for(i=1;i<=n;i++){
		var menu=document.getElementById(name+i);
		var con=document.getElementById("con_"+name+"_"+i);
		if(menu != null)
			menu.className=i==cursel?"current1":"";
		if(con != null)
		con.style.display=i==cursel?"block":"none";
	}
}

function phbTab2(cursel){
	for(i=1;i<=3;i++){
		var menu=document.getElementById("phbj" + i);
		var con=document.getElementById("con_phb_" + i);

		if(menu != null)
			menu.className=i==cursel?"current1":"";
		if(con != null)
		{
			if(window.navigator.userAgent.indexOf("MSIE")>=1)
				con.style.display=i==cursel?"block":"none";
			else 
				con.style.display=i==cursel?"table":"none";
		}
	}
}



/*f3*/

function jjshescTab(name,cursel,n){
	for(i=1;i<=n;i++){
		var menu=document.getElementById(name+i);
		var con=document.getElementById("con_"+name+"_"+i);
		if(menu != null)
			menu.className=i==cursel?"current2":"";
		if(con != null)
		con.style.display=i==cursel?"block":"none";
	}
}

function jjshescTab2(cursel){
	for(i=1;i<=3;i++){
		var menu=document.getElementById("jjshescj" + i);
		var con=document.getElementById("con_jjshesc_" + i);

		if(menu != null)
			menu.className=i==cursel?"current2":"";
		if(con != null)
		{
			if(window.navigator.userAgent.indexOf("MSIE")>=1)
				con.style.display=i==cursel?"block":"none";
			else 
				con.style.display=i==cursel?"table":"none";
		}
	}
}


/*f4*/

function ssescTab(name,cursel,n){
	for(i=1;i<=n;i++){
		var menu=document.getElementById(name+i);
		var con=document.getElementById("con_"+name+"_"+i);
		if(menu != null)
			menu.className=i==cursel?"current3":"";
		if(con != null)
		con.style.display=i==cursel?"block":"none";
	}
}

function ssescTab2(cursel){
	for(i=1;i<=5;i++){
		var menu=document.getElementById("ssescj" + i);
		var con=document.getElementById("con_ssesc_" + i);

		if(menu != null)
			menu.className=i==cursel?"current3":"";
		if(con != null)
		{
			if(window.navigator.userAgent.indexOf("MSIE")>=1)
				con.style.display=i==cursel?"block":"none";
			else 
				con.style.display=i==cursel?"table":"none";
		}
	}
}



/*f5*/

function hhescTab(name,cursel,n){
	for(i=1;i<=n;i++){
		var menu=document.getElementById(name+i);
		var con=document.getElementById("con_"+name+"_"+i);
		if(menu != null)
			menu.className=i==cursel?"current4":"";
		if(con != null)
		con.style.display=i==cursel?"block":"none";
	}
}

function hhescTab2(cursel){
	for(i=1;i<=5;i++){
		var menu=document.getElementById("hhescj" + i);
		var con=document.getElementById("con_hhesc_" + i);

		if(menu != null)
			menu.className=i==cursel?"current4":"";
		if(con != null)
		{
			if(window.navigator.userAgent.indexOf("MSIE")>=1)
				con.style.display=i==cursel?"block":"none";
			else 
				con.style.display=i==cursel?"table":"none";
		}
	}
}



/*登录注册*/

function shTab(name,cursel,n){
	for(i=1;i<=n;i++){
		var menu=document.getElementById(name+i);
		var con=document.getElementById("con_"+name+"_"+i);
		if(menu != null)
			menu.className=i==cursel?"sel":"";
		if(con != null)
		con.style.display=i==cursel?"block":"none";
	}
}

function shTab2(cursel){
	for(i=1;i<=2;i++){
		var menu=document.getElementById("shj" + i);
		var con=document.getElementById("con_sh_" + i);

		if(menu != null)
			menu.className=i==cursel?"sel":"";
		if(con != null)
		{
			if(window.navigator.userAgent.indexOf("MSIE")>=1)
				con.style.display=i==cursel?"block":"none";
			else 
				con.style.display=i==cursel?"table":"none";
		}
	}
}


/*cp*/

function bnewTab(name,cursel,n){
	for(i=1;i<=n;i++){
		var menu=document.getElementById(name+i);
		var con=document.getElementById("con_"+name+"_"+i);
		if(menu != null)
			menu.className=i==cursel?"curr":"";
		if(con != null)
		con.style.display=i==cursel?"block":"none";
	}
}

function bnewTab2(cursel){
	for(i=1;i<=4;i++){
		var menu=document.getElementById("bnewj" + i);
		var con=document.getElementById("con_bnew_" + i);

		if(menu != null)
			menu.className=i==cursel?"curr":"";
		if(con != null)
		{
			if(window.navigator.userAgent.indexOf("MSIE")>=1)
				con.style.display=i==cursel?"block":"none";
			else 
				con.style.display=i==cursel?"table":"none";
		}
	}
}



/*新闻页面3个切换到*/

function bnewTab(name,cursel,n){
	for(i=1;i<=n;i++){
		var menu=document.getElementById(name+i);
		var con=document.getElementById("con_"+name+"_"+i);
		if(menu != null)
			menu.className=i==cursel?"hover":"";
		if(con != null)
		con.style.display=i==cursel?"block":"none";
	}
}

function bnewTab2(cursel){
	for(i=1;i<=5;i++){
		var menu=document.getElementById("bnewj" + i);
		var con=document.getElementById("con_bnew_" + i);

		if(menu != null)
			menu.className=i==cursel?"hover":"";
		if(con != null)
		{
			if(window.navigator.userAgent.indexOf("MSIE")>=1)
				con.style.display=i==cursel?"block":"none";
			else 
				con.style.display=i==cursel?"table":"none";
		}
	}
}


/*新闻页面3个切换到*/

function bnewrtTab(name,cursel,n){
	for(i=1;i<=n;i++){
		var menu=document.getElementById(name+i);
		var con=document.getElementById("con_"+name+"_"+i);
		if(menu != null)
			menu.className=i==cursel?"hover":"";
		if(con != null)
		con.style.display=i==cursel?"block":"none";
	}
}

function bnewrtTab2(cursel){
	for(i=1;i<=5;i++){
		var menu=document.getElementById("bnewrtj" + i);
		var con=document.getElementById("con_bnewrt_" + i);

		if(menu != null)
			menu.className=i==cursel?"hover":"";
		if(con != null)
		{
			if(window.navigator.userAgent.indexOf("MSIE")>=1)
				con.style.display=i==cursel?"block":"none";
			else 
				con.style.display=i==cursel?"table":"none";
		}
	}
}




