package com.hyena.framework.network.listener;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

/**
 * <description>
 *
 * @author zhoulu
 * @date 14-2-18
 */
public class JsonHttpListener extends DataHttpListener {

	public String getJson() throws IOException {
		if (getData() == null) {
			return null;
		}
		ByteArrayInputStream is = new ByteArrayInputStream(getData());
		StringWriter writer = new StringWriter();

		char[] buffer = new char[1024];
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "UTF-8"));
			int n;
			while ((n = reader.read(buffer)) != -1) {
				writer.write(buffer, 0, n);
			}
		} finally {
			is.close();
		}
		return writer.toString();

	}

}
