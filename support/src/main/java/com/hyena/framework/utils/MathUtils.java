/**
 * Copyright (C) 2015 The AppFramework Project
 */
package com.hyena.framework.utils;

import java.util.ArrayList;
import java.util.Stack;

/**
 * 数学相关通用类
 * @author yangzc
 */
public class MathUtils {

	private static final String TAG = "MathUtils";

	public static final float FLOAT_ACCURANCY = 0.01f;

	/**
	 * 字符串转Integer
	 * @param value
	 * @return
	 */
	public static int valueOfInt(String value){
		try {
			return Integer.valueOf(value);
		} catch (Exception e) {
		}
		return -1;
	}
	
	/**
	 * 字符串转Long
	 * @param value
	 * @return
	 */
	public static long valueOfLong(String value){
		try {
			return Long.valueOf(value);
		} catch (Exception e) {
		}
		return -1;
	}
	
	/**
	 * 字符串转Float
	 * @param value
	 * @return
	 */
	public static float valueOfFloat(String value){
		try {
			return Float.valueOf(value);
		} catch (Exception e) {
		}
		return -1;
	}

	/**
	 * 比较两浮点数是否相等，最小误差0.01
	 * @param a 被比较浮点数
	 * @param b 比较浮点数
	 * @return 如果两者相等，返回0；前者小于后者，返回-1;否则返回1
	 */
	public static int compareTo(float a, float b) {

		float s = a -b;
		if (Math.abs(s) <= FLOAT_ACCURANCY) {
			return 0;
		}

		return (int)Math.signum(s);
	}

	/**
	 * 计算简单表达式
	 * @param str
	 * @return
	 */
	public static int eval(String str){
		ArrayList<Character> labelst = new ArrayList<Character>();
		labelst.add('+');
		labelst.add('-');
		labelst.add('*');
		labelst.add('/');

		//处理乘法和除法
		Stack<String> stack = new Stack<String>();
		char ch[] = str.toCharArray();
		int tmpNum = 0;
		for(int i=0; i< ch.length; i++){
			if("".equals(String.valueOf(ch[i]).trim()))
				continue;
			if(labelst.indexOf(ch[i]) != -1){
				if(stack.size() > 0){
					String lastLab = stack.pop();
					if(lastLab.equals("*")){
						String lastNum = stack.pop();
						int result = Integer.valueOf(lastNum) * tmpNum;
						stack.push(result+"");
						stack.push(ch[i]+"");
					}else if(lastLab.equals("/")){
						String lastNum = stack.pop();
						int result = Integer.valueOf(lastNum) / tmpNum;
						stack.push(result+"");
						stack.push(ch[i]+"");
					}else{
						stack.push(lastLab);
						stack.push(tmpNum+"");
						stack.push(ch[i]+"");
					}
				}else{
					stack.push(tmpNum+"");
					stack.push(ch[i]+"");
				}
				tmpNum = 0;
			}else{
				tmpNum = (ch[i] - '0') + tmpNum*10;
			}
		}
		if(stack.size() > 0){
			String lastLab = stack.pop();
			if(lastLab.equals("*")){
				String lastNum = stack.pop();
				int result = Integer.valueOf(lastNum) * tmpNum;
				stack.push(result+"");
			}else if(lastLab.equals("/")){
				String lastNum = stack.pop();
				int result = Integer.valueOf(lastNum) / tmpNum;
				stack.push(result+"");
			}else{
				stack.push(lastLab);
				stack.push(tmpNum + "");
			}
		}

		Stack<String> simplesStack = new Stack<String>();
		int stackSize = stack.size();
		for(int i=0; i< stackSize; i++){
			simplesStack.push(stack.pop());
		}

//		for(int i=0; i< simplesStack.size(); i++){
//			System.out.println(simplesStack.get(i));
//		}

		//剩下的公式只有加减法运算
		while(simplesStack.size() > 1){//如果还存在公式
			String num1 = simplesStack.pop();
			String lastlab = simplesStack.pop();
			String num2 = simplesStack.pop();
			if(simplesStack.size() > 0){
				if("+".equals(lastlab)){
					int value = Integer.valueOf(num1) + Integer.valueOf(num2);
					simplesStack.push(value+"");
				}else if("-".equals(lastlab)){
					int value = Integer.valueOf(num1) - Integer.valueOf(num2);
					simplesStack.push(value+"");
				}
			}else{
				if("+".equals(lastlab)){
					int value = Integer.valueOf(num1) + Integer.valueOf(num2);
					simplesStack.push(value+"");
				}else if("-".equals(lastlab)){
					int value = Integer.valueOf(num1) - Integer.valueOf(num2);
					simplesStack.push(value+"");
				}
			}
		}
		//已经计算出结果
		return Integer.valueOf(simplesStack.get(0));
	}
}
