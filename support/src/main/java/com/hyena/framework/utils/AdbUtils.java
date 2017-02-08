package com.hyena.framework.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.hyena.framework.clientlog.LogUtil;

/**
 * ADB操作通用类
 * @author yangzc
 *
 */
public class AdbUtils {

	/**
	 * 执行Root指令
	 * @param command
	 * @return
	 */
	public static boolean runRootCommand(String command){
		Process process = null;
		DataOutputStream os = null;
		try {
			process = Runtime.getRuntime().exec("/system/xbin/su");
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(command + "\n");
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
			process.exitValue();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				os.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try{process.destroy();}catch(Exception e){}
		}
		return true;
	}
	
	/**
	 * 设置属性
	 * @param key
	 * @param value
	 * @return
	 */
	public static boolean setProp(String key, String value){
		return runRootCommand("setprop " + key + " " + value);
	}
	
	/**
	 * 执行普通命令
	 * adb shell ***
	 * @param command
	 * @return
	 */
	public static boolean runCommand(String command){
		Process process = null;
		BufferedReader localBufferedReader = null;
		DataOutputStream os = null;
		try {
			process = Runtime.getRuntime().exec(command);
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
			process.exitValue();
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(os != null)
				try {
					os.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			if(localBufferedReader != null)
				try {
					localBufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			try{process.destroy();}catch(Exception e){}
		}
		return false;
	}
	
	/**
	 * 返回Adb命令返回结果
	 * @param command
	 * @return
	 */
	public static String getCommandResult(String command){
		Process process = null;
		BufferedReader localBufferedReader = null;
		StringBuffer buffer = new StringBuffer();
		try {
			process = Runtime.getRuntime().exec(command + "\n");
			localBufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = localBufferedReader.readLine();
			while(line != null){
				buffer.append(line + "\n");
				line = localBufferedReader.readLine();
			}
			process.waitFor();
			process.exitValue();
			return buffer.toString();
		} catch (Exception e) {
			LogUtil.e("yangzc", e);
		} finally {
			if(localBufferedReader != null)
				try {
					localBufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			try{process.destroy();}catch(Exception e){}
		}
		return null;
	}
}
