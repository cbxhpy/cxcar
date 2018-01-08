package com.enation.app.shop.core.netty.omen.demo.test;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import org.junit.Test;

import com.enation.app.shop.core.netty.omen.netty.utils.StringUtil;



public class SocketTest {
	
	private static String ip="localhost";
	
	private static Integer port=8001;
	
	@Test
	public void registerActiveTest(){
		String hex = "12007E0C1018FE34A42C3E007E0C1800850DA100";
		byte[] req=StringUtil.parseHexStr2Byte(hex);
		byte[] res = socketConnect(ip, port, req);
//		parseResByte(res);
		System.out.println("return:" + StringUtil.parseByte2HexStr(res));
	}

	public byte[] socketConnect(String ip, Integer port,byte[] req){
		Socket socket = null;  
		DataOutputStream out = null;
		 DataInputStream in = null;
		 byte[] res =null ;
        try {  
        	
        	System.out.println("aaaaa");
            //客户端socket指定服务器的地址和端口号  
            socket = new Socket(ip, port);  
            
            System.out.println("Socket=" + socket);  
            
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            
            out.write(req);
            out.flush();
            
            ByteArrayOutputStream out1 = new ByteArrayOutputStream();
            while (in.available() != 0) {
            out1.write(in.read());
            }
            res = out1.toByteArray(); 
            System.out.println("sleep over");
            out.write(req);
            out.flush();
            System.out.println("send again");
        } catch (Exception e) {  
            e.printStackTrace();  
        }finally{
        	System.out.println("all over");
        }
        return res;
	}
	
	
	
	
	
}
