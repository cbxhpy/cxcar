var Speed = 20; //速度(毫秒) 
var Space = 1; //每次移动(px)
var MoveLock = false; 
var MoveTimeObj; 
var Comp = 0;
var CurrentMoveDir = 'ISL_GoDown()';
GetObj("scroll_list_2").innerHTML = GetObj("scroll_list_1").innerHTML+GetObj("scroll_list_1").innerHTML+GetObj("scroll_list_1").innerHTML;

ISL_Start();

function GetObj(objName)
{
	if(document.getElementById)
		return eval('document.getElementById("'+objName+'")')
	else
		return eval('document.all.'+objName)
} 

function ISL_GoUp() //上翻
{
	ISL_StopDown();
	if(MoveLock)
		return;
	
	CurrentMoveDir='ISL_GoUp()';
	MoveLock = true; 
	MoveTimeObj = setInterval('ISL_ScrUp();',Speed); 
} 

function ISL_StopUp() //上翻停止 
{
	clearInterval(MoveTimeObj); 
	MoveLock = false;
} 

function ISL_ScrUp() //上翻动作 
{
	if(GetObj('ISL_Cont').scrollLeft <= 0)
		GetObj('ISL_Cont').scrollLeft = GetObj('ISL_Cont').scrollLeft + GetObj('scroll_list_1').offsetWidth
	
	GetObj('ISL_Cont').scrollLeft -= Space ; 
}

function ISL_GoDown() //下翻 
{
	ISL_StopUp();
	clearInterval(MoveTimeObj); 
	if(MoveLock)
		return;
	
	CurrentMoveDir='ISL_GoDown()';
	MoveLock = true; 
	ISL_ScrDown(); 
	MoveTimeObj = setInterval('ISL_ScrDown()',Speed); 
}

function ISL_StopDown() //下翻停止
{ 
	clearInterval(MoveTimeObj); 
	MoveLock = false;
}

function ISL_Stop()	//停止所有
{
	ISL_StopUp();
	ISL_StopDown();
}

function ISL_Start()	//自动开始
{
	eval(CurrentMoveDir);
}

function ISL_ScrDown() //下翻动作
{ 
	if(GetObj('ISL_Cont').scrollLeft >= GetObj('scroll_list_1').scrollWidth)
		GetObj('ISL_Cont').scrollLeft = GetObj('ISL_Cont').scrollLeft - GetObj('scroll_list_1').scrollWidth;
	
	GetObj('ISL_Cont').scrollLeft += Space ; 
} 

function CompScr()
{ 
	var num;
	
	if(Comp < 0) //上翻
	{ 
		if(Comp < -Space)
		{ 
			Comp += Space; 
			num = Space; 
		}
		else
		{ 
			num = -Comp; 
			Comp = 0; 
		} 
		GetObj('ISL_Cont').scrollLeft -= num; 
		setTimeout('CompScr()',Speed); 
	}
	else //下翻 
	{
		if(Comp > Space)
		{ 
			Comp -= Space; 
			num = Space; 
		}
		else
		{ 
			num = Comp; 
			Comp = 0; 
		} 
		GetObj('ISL_Cont').scrollLeft += num; 
		setTimeout('CompScr()',Speed); 
	}
}