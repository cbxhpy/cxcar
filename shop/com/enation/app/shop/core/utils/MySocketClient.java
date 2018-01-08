package com.enation.app.shop.core.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
 
public class MySocketClient {
 
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        Socket client=new Socket("127.0.0.1", 8000);
        PrintWriter pw=new PrintWriter(client.getOutputStream());  
        BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
        pw.write("0006hello!");
        pw.flush();
        
        while(br.read() != -1) {
        	System.out.println(br.readLine());
        }
        String a = br.readLine();
        System.out.println(a);
        
        pw.close();
        br.close();
    }
 
}
