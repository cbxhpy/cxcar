package com.enation.app.shop.core.netty.omen.demo.utils;

import java.util.Arrays;

/**
 * @see ClassName：ByteUtil
 * @see Function：处理byte的工具类
 * @see Date：2015-06-02 14:57:27
 * @author xuyiming
 * @version v1.0
 * @since JDK 1.7
 */
public class ByteUtil {

	/**
	 * byte转为16进制
	 * 2017-6-28
	 * @param  
	 * @param buffer
	 * @return 
	 */
	public static String byte2hex(byte [] buffer){  
        String h = "";  
          
        for(int i = 0; i < buffer.length; i++){  
            String temp = Integer.toHexString(buffer[i] & 0xFF);  
            if(temp.length() == 1){  
                temp = "0" + temp;  
            }  
            h = h + " "+ temp;  
        }  
          
        return h;  
          
    } 
	
	/**
	 * 合并两个byte[]
	 * 
	 * @param byte_1
	 * @param byte_2
	 * @return 合并后的byte数组，byte_1在前，byte_2在后
	 */
	public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
		byte[] byte_3 = new byte[byte_1.length + byte_2.length];
		System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
		System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
		return byte_3;
	}

	/**
	 * 将int数值转换为占N个字节的byte数组，本方法适用于(低位在前，高位在后)的顺序。
	 * 
	 * @param value
	 *            要转换的值
	 * @return byte数组
	 */
	public static byte[] int2BytesLittleEndian(int value, Integer byteLength) {
		byte[] byte_src = new byte[byteLength];

		for (Integer i = 0; i < byteLength; i++) {
			byte_src[i] = (byte) ((value & (0xFF << 8 * i)) >> 8 * i);
		}
		return byte_src;
	}
	
	
	public static byte[] int2BytesLittleEndian(int value) {
		byte[] byte_src = new byte[4];

		for (Integer i = 0; i < 4; i++) {
			byte_src[i] = (byte) ((value & (0xFF << 8 * i)) >> 8 * i);
		}
		return byte_src;
	}
	
	
	/**
	 * 
	* @Title: intToBytes2
	* @Description: bigEndian 高位在前，低位在后
	* @param @param value
	* @param @return    
	* @return byte[]    
	* @throws
	* @author omen  www.liyidong.com 
	* @date 2016年4月12日 下午3:27:49
	 */
	public static byte[] int2Bytes(int value)   
	{   
	    byte[] src = new byte[4];  
	    src[0] = (byte) ((value>>24) & 0xFF);  
	    src[1] = (byte) ((value>>16)& 0xFF);  
	    src[2] = (byte) ((value>>8)&0xFF);    
	    src[3] = (byte) (value & 0xFF);       
	    return src;  
	}  

	/**
	 * 将byte数组转换为int数值，本方法适用于(高位在前，低位在后)的顺序。
	 * 
	 * @param byte_src
	 *            要转换的byte数组
	 * @return int值
	 */
	public static Integer bytes2Int(byte[] byte_src) {
		Integer num = 0;

		for (Integer i = 0; i < byte_src.length; i++) {
			if (byte_src[i] < 0)
				num = num
						+ ((256 + byte_src[i]) << 8 * (byte_src.length - 1 - i));
			else
				num = num + (byte_src[i] << 8 * (byte_src.length - 1 - i));
		}
		return num;
	}
	
	
	   /** 
     * 将short转成byte[2] 
     * @param a 
     * @param b 
     * @param offset b中的偏移量 
     */  
    public static void short2Bytes(short a, byte[] b, int offset){  
        b[offset] = (byte) (a >> 8);  
        b[offset+1] = (byte) (a);  
    }  
    
    /** 
     * 将short转成byte[2] 
     * @param a 
     * @param b 
     * @param offset b中的偏移量 
     */  
    public static byte[] short2Bytes(short a){  
    	byte[] b = new byte[2];
        b[0] = (byte) (a >> 8);  
        b[1] = (byte) (a);  
        return b;
    }  
    
      
    /** 
     * 将byte[2]转换成short 
     * @param b 
     * @return 
     */  
    public static short bytes2Short(byte[] b){  
        return (short) (((b[0] & 0xff) << 8) | (b[1] & 0xff));  
    }  
	
	
    public static long bytes2long(byte[] b) {  
        long temp = 0;  
        long res = 0;  
        for (int i=0;i<8;i++) {  
            res <<= 8;  
            temp = b[i] & 0xff;  
            res |= temp;  
        }  
        return res;  
    }  
	
    public static byte[] long2bytes(long num) {  
        byte[] b = new byte[8];  
        for (int i=0;i<8;i++) {  
            b[i] = (byte)(num>>>(56-(i*8)));  
        }  
        return b;  
    }  
    
    
    public static void inverted(byte[] bytes){
    	for(int i=0;i<(bytes.length)/2;i++){
    		byte temp = bytes[i];
    		bytes[i] = bytes[bytes.length - i - 1];
    		bytes[bytes.length - i - 1] = temp;
    	}    
    }

	public static void main(String[] args) {
		/*Integer i = 0x6001;
		byte[] bts = intToBytes(i, 2);
		System.out.println(bts[0] + "," + bts[1]);*/
		
		/*byte[] bt = new byte[5];
		byte[] bts = intToBytes(51001, 5);
		bt[0] = bts[4];
		bt[1] = bts[3];
		bt[2] = bts[2];
		bt[3] = bts[1];
		bt[4] = bts[0];
		System.out.println(bytesToInt(bt));*/
		
		long timeStamp  = System.currentTimeMillis();
		System.out.println("time stamp=" + timeStamp);
		byte[] timeB = long2bytes(timeStamp);
		System.out.println(StringUtil.parseByte2HexStr(timeB));
		System.out.println(bytes2long(timeB));
		
		byte[] a={1,3,5,2,4,9};
		inverted(a);
		System.out.println(Arrays.toString(a));
		
		
		Short as = 12;
		byte[] bs = short2Bytes(as);
		System.out.println(Arrays.toString(bs));
		
		int i = 53333;
		System.out.println(StringUtil.parseByte2HexStr(int2Bytes(i)));
		
		
	}
}
