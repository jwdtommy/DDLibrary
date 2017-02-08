package com.hyena.framework.security;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

public class CRCUtil {
	/**
	 * 计算文件的crc32
	 * @param fileName
	 * @return 返回crc32值，失败返回-1
	 */
	public static long fileCRC32(String fileName) {
		CheckedInputStream cis = null;
		try {
			cis = new CheckedInputStream(new FileInputStream(fileName),
				new CRC32());


			byte[] buf = new byte[128];
			while (cis.read(buf) >= 0);

			return cis.getChecksum().getValue();
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		} finally {
			try{
				if(cis != null){
					cis.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
