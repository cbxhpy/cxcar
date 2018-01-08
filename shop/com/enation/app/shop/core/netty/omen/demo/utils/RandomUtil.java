/**
 * Project Name:nbseOrder
 * File Name:RandomUtil.java
 * Package Name:cn.net.nbse.open.util
 * Date:2013年9月12日下午3:54:38
 * Copyright (c) 2013, liyidong@nbse.net.cn All Rights Reserved.
 *
*/

package com.enation.app.shop.core.netty.omen.demo.utils;

import java.util.Random;

/**
 * ClassName:RandomUtil <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2013年9月12日 下午3:54:38 <br/>
 * @author   Liyidong
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public class RandomUtil {
		
	public static String getRpid(){
		Random rd = new Random();
		String n="";
		int getNum;
		
		
		int i;
		i=new java.util.Random().nextInt(9); //生成0-9的随机数
		do {
			if(rd.nextBoolean()){
				
				getNum =new java.util.Random().nextInt(9) + 48;//产生数字0-9的随机数
			}else{
				getNum = new java.util.Random().nextInt(25) + 97;//产生字母a-z的随机数
			}
			char num1 = (char)getNum;
			n +=Character.toString(num1);
		} while (n.length()<8);
		return n;
	}
	
	public static void main(String[] args) {
		for(int j =0;j<=50;j++){
			System.out.println(RandomUtil.getRpid());
			getRpid();
			
//			int i;
////			Random rand = new Random(47);
//			i=new java.util.Random().nextInt(9); //生成0-9的随机数
//			
//			
//			
//			Random rd = new Random();
//			int getNum = Math.abs(rd.nextInt())%26;
//			System.out.println(getNum);
		}
	}

}
