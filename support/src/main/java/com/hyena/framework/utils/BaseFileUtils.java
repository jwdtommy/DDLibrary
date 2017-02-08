/**
 * Copyright (C) 2015 The AndroidRCStudent Project
 */
package com.hyena.framework.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

/**
 * @author yangzc on 15/8/24.
 */
public class BaseFileUtils {

    /**
     * 获得特定目录下的文件夹
     * @param parent 父目录
     * @param dirName 目录名
     * @return 目录
     */
    public static File getDir(File parent, String dirName){
        File file = new File(parent, dirName);
        if(!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
        return file;
    }

    /**
     * 生成空文件
     * @param path 文件路径
     * @param size 文件大小
     * @return 是否创建成功
     * @throws IOException 异常
     */
    public static boolean createEmptyFile(String path, long size) throws IOException {
        File file = new File(path);
        File parent = file.getParentFile();
        parent.mkdirs();
        RandomAccessFile raf = null;
        raf = new RandomAccessFile(file, "rw");
        raf.setLength(size);
        raf.close();
        return true;
    }

    /**
     * 流拷贝
     * @param is
     * @param os
     * @throws IOException
     */
    public static void copyStream(InputStream is, OutputStream os) throws IOException{
        byte buffer[] = new byte[1024];
        int len = -1;
        while((len = is.read(buffer, 0, 1024)) != -1){
            os.write(buffer, 0, len);
        }
    }

    /**
     * 拷贝流到文件中
     * @param is
     * @param desc
     * @throws IOException
     */
    public static void copyStream2File(InputStream is, File desc) throws IOException{
        OutputStream os = null;
        try {
            os = new FileOutputStream(desc);
            copyStream(is, os);
        }finally{
            if(os != null)
                os.close();
        }
    }

    /**
     * 拷贝文件
     * @param src
     * @param desc
     * @throws IOException
     */
    public static void copyFile(File src, File desc) throws IOException{
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(src);
            os = new FileOutputStream(desc);
            copyStream(is, os);
        }finally{
            if(is != null)
                is.close();
            if(os != null)
                os.close();
        }
    }

    /**
     * 输入流中数据转换成数据
     * @param is
     * @return
     */
    public static byte[] getBytes(InputStream is) throws IOException{
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
     * @param file
     * @return
     */
    public static byte[] getBytes(File file) throws Exception{
        FileInputStream fis = null;
        ByteArrayOutputStream baos = null;
        try {
            fis = new FileInputStream(file);
            baos = new ByteArrayOutputStream();
            copyStream(fis, baos);
            return baos.toByteArray();
        } finally {
            if(fis != null){
                try {
                    fis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
