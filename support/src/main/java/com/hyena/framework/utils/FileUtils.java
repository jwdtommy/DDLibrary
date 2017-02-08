/**
 * Copyright (C) 2015 The AppFramework Project
 */
package com.hyena.framework.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 文件处理通用类
 * 
 * @author yangzc
 *
 */
public class FileUtils {

	// 文件分隔符
	public static final String SYSTEM_SEPARATOR = System
			.getProperty("file.separator");

	/**
	 * 流拷贝
	 * 
	 * @param is
	 * @param os
	 * @throws IOException
	 */
	public static void copyStream(InputStream is, OutputStream os)
			throws IOException {
		byte buffer[] = new byte[1024];
		int len = -1;
		while ((len = is.read(buffer, 0, 1024)) != -1) {
			os.write(buffer, 0, len);
		}
	}

	/**
	 * 拷贝流到文件中
	 * 
	 * @param is
	 * @param desc
	 * @throws IOException
	 */
	public static void copyStream2File(InputStream is, File desc)
			throws IOException {
		OutputStream os = null;
		try {
			os = new FileOutputStream(desc);
			copyStream(is, os);
		} finally {
			if (os != null)
				os.close();
		}
	}

	/**
	 * 拷贝文件
	 * 
	 * @param src
	 * @param desc
	 * @throws IOException
	 */
	public static void copyFile(File src, File desc) throws IOException {
		InputStream is = null;
		OutputStream os = null;
		try {
			is = new FileInputStream(src);
			os = new FileOutputStream(desc);
			copyStream(is, os);
		} finally {
			if (is != null)
				is.close();
			if (os != null)
				os.close();
		}
	}

	/**
	 * 读文件
	 * 
	 * @param file
	 * @param charset
	 * @return
	 */
	public static String readFile2String(File file, String charset) {
		if (null == file)
			return "";
		FileInputStream is = null;
		ByteArrayOutputStream os = null;
		try {
			is = new FileInputStream(file);
			os = new ByteArrayOutputStream();
			copyStream(is, os);
			return new String(os.toByteArray(), charset);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 输入流中数据转换成数据
	 * 
	 * @param is
	 * @return
	 */
	public static byte[] getBytes(InputStream is) throws IOException {
		ByteArrayOutputStream os = null;
		try {
			os = new ByteArrayOutputStream();
			copyStream(is, os);
		} finally {
			os.close();
		}
		return os.toByteArray();
	}

	/**
	 * 读取文件
	 * 
	 * @param file
	 * @return
	 */
	public static byte[] getBytes(File file) throws Exception {
		FileInputStream fis = null;
		ByteArrayOutputStream baos = null;
		try {
			fis = new FileInputStream(file);
			baos = new ByteArrayOutputStream();
			copyStream(fis, baos);
			return baos.toByteArray();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 写数据到文件中
	 * @param file
	 * @param value
	 * @param isAppend
	 * @return
	 */
	public static boolean write2File(File file, String value, boolean isAppend) {
		boolean isWriteOk = false;
		if (null == file || null == value) {
			return isWriteOk;
		}
		FileWriter fw = null;
		try {
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			fw = new FileWriter(file, isAppend);
			fw.write(value, 0, value.length());
			fw.flush();
			isWriteOk = true;
		} catch (Exception e) {
			isWriteOk = false;
			e.printStackTrace();
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					isWriteOk = false;
					e.printStackTrace();
				}
			}
		}
		return isWriteOk;
	}
}
