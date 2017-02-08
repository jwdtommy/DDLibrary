/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hyena.framework.utils;

import com.hyena.framework.clientlog.LogUtil;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

public class ImageUtils {

	public final static boolean DEBUG = true;

	/**
	 * 增加圆角
	 * 
	 * @param bitmap
	 * @param width
	 * @param height
	 * @param radius
	 * @return
	 */
	public static Bitmap round(Bitmap bitmap, int width, int height,
			int radius, boolean recycleSource) {
		if (width == 0 || height == 0 || radius <= 0 || bitmap == null)
			return bitmap;

		Bitmap ret = null;
		try {
			ret = Bitmap.createBitmap(width, height, Config.RGB_565);
		} catch (OutOfMemoryError e) {
			LogUtil.e("OutOfMemoryError",
					"OutOfMemoryError in ImageUtils.round(): " + e.getMessage());
		}
		if (ret == null)
			return null;

		Canvas canvas = new Canvas(ret);
		Paint paint = new Paint();
		Rect rect = new Rect(0, 0, width, height);
		RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(0xff424242);
		canvas.drawRoundRect(rectF, radius, radius, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		if (recycleSource)
			ImageUtils.clear(bitmap);
		return ret;
	}

	/**
	 * 增加圆角
	 * 
	 * @param bitmap
	 * @param radius
	 * @return
	 */
	public static Bitmap round(Bitmap bitmap, int radius, boolean recycleSource) {
		if (radius <= 0 || bitmap == null)
			return bitmap;
		return round(bitmap, bitmap.getWidth(), bitmap.getHeight(), radius,
				recycleSource);
	}

	/**
	 * 释放bitmap
	 * 
	 * @param bitmap
	 */
	public static void clear(Bitmap bitmap) {
		if (bitmap != null)
			bitmap.recycle();
	}
}
