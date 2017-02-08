
package com.hyena.framework.security;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 编码处理类
 * @author yangzc
 *
 */
public class Base64 {   
	
	static String baseSting [] ={
		"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
	};
	
	/**
	 * 把byte数组进行base64编码，变成字符串
	 * @param data byte数组
	 * @return
	 */
	public static String encode(byte [] data) { 
		return  encode(data, 0, data.length, null).toString ();    
	}
	
	/** Encodes the part of the given byte array denoted by start and len to the Base64 format.  
	 * The encoded data is appended to the given StringBuffer. 
	 * If no StringBuffer is given, a new one is created automatically. 
	 * The StringBuffer is the return value of this method. 
	 */     
	public static StringBuffer encode(byte [] data, int start, int len, StringBuffer buf) { //编码基础方法
		int key = 0;
		char[] charT = baseSting[key].toCharArray();
		
		if (buf == null){
			buf = new StringBuffer (data.length * 3 / 2); //自动创建字符串缓冲
		}
		int end = len - 3; 
		int i = start; 
		while (i <= end) {     
			int d = ((((int) data [i]) & 0x0ff) << 16)   
					| ((((int) data [i+1]) & 0x0ff) << 8)  
					| (((int) data [i+2]) & 0x0ff);     
			buf.append (charT [(d >> 18) & 63]);     
			buf.append (charT [(d >> 12) & 63]);     
			buf.append (charT [(d >> 6) & 63]);     
			buf.append (charT [d & 63]);     
			i += 3;      
		} 
		
		if (i == start + len - 2) {     
			int d = ((((int) data [i]) & 0x0ff) << 16)   
					| ((((int) data [i+1]) & 255) << 8);     
			buf.append (charT [(d >> 18) & 63]);     
			buf.append (charT [(d >> 12) & 63]);     
			buf.append (charT [(d >> 6) & 63]);     
		} else if (i == start + len - 1) {     
			int d = (((int) data [i]) & 0x0ff) << 16;     
			buf.append (charT [(d >> 18) & 63]);     
			buf.append (charT [(d >> 12) & 63]);     
		} 
		return buf;    
	}    
	
	public static int decode(char c,int key) { //解码基础方法  //返回字符所在字符表的位置
//		Util.D("c: "+c+" "+"int: "+(int)c);
		char[] charT = baseSting[key].toCharArray();
		if(c == '='){
			return 0;
		}
		for(int i=0;i<64;i++){
			if(charT[i] == c){
				return i;
			}
		}
		return 0;
		
	}      
	   
	/**
	 * 把一个进行base64编码的字符串进行解码还原成byte数组
	 * @param s 已经经过base64编码的字符串
	 * @return
	 */
	public static byte [] decode(String s) {
		byte[] temp=null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream ();
		if(s == null){
			return null;
		}
		try {
			decode (s, bos); 
			temp=bos.toByteArray ();
			bos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return temp;  
	}    
	
	public static void decode(String s, ByteArrayOutputStream bos) { 
		int i = 0; 
		int len = s.length ();  
		int nKey = 0;
		int nAddNum = (s.length()) % 4 ;
		if(nAddNum > 0){
			nAddNum = 4-nAddNum;
		}
		while(nAddNum > 0){
			s += '=';
			nAddNum--;
		}
		while (true) {      
			while (i < len && s.charAt (i) <= ' ') //跳过空格位置
				i++;     
			if (i == len){
				break;     
			} 
			int tri = (decode (s.charAt (i),nKey) << 18)  
					+ (decode (s.charAt (i+1),nKey) << 12)  
					+ (decode (s.charAt (i+2),nKey) << 6)  
					+ (decode (s.charAt (i+3),nKey));          
			bos.write ((tri >> 16) & 255);     
			if (s.charAt (i+2) == '='){
				break;     
			}
			bos.write ((tri >> 8) & 255);     
			if (s.charAt (i+3) == '='){
				break;     
			}
			bos.write (tri & 255);     
			i += 4; 
		}   
	}
	
	public static boolean needBase64(String s) {	
		for(int i=0;i<s.length();i++){//除了大小写英文字母和数字外全部需要进行base64加密
			
			if(s.charAt(i) == '-'){
				continue;
			}
			
			if(s.charAt(i)>0x7A || (s.charAt(i)<0x61 & s.charAt(i)>0x5A) || (s.charAt(i)<0x41 & s.charAt(i)>0x39) || s.charAt(i)<0x30){
				return true;
			}
		}
		return false;
	}
}