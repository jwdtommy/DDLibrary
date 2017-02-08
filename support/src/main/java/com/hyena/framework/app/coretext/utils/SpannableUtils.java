/**
 * Copyright (C) 2015 The AppFramework Project
 */
package com.hyena.framework.app.coretext.utils;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;

/**
 * Span操作通用类
 * @author yangzc
 */
public class SpannableUtils {

	/**
	 * 替换Span
	 * 
	 * @param spannable
	 * @param span
	 * @param targetSpan
	 */
	public static <T> void replaceSpan(SpannableStringBuilder spannable,
			Class<T> span, Object targetSpan) {
		T spans[] = spannable.getSpans(0, spannable.length(), span);
		for (int i = 0; i < spans.length; i++) {
			int start = spannable.getSpanStart(spans[i]);
			int end = spannable.getSpanEnd(spans[i]);
			spannable.setSpan(targetSpan, start, end,
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}

	/**
	 * 替换Span
	 * 
	 * @param spannable
	 * @param span
	 * @param targetSpan
	 */
	public static <T> void replaceSpan(SpannableString spannable,
			Class<T> span, Object targetSpan) {
		T spans[] = spannable.getSpans(0, spannable.length(), span);
		for (int i = 0; i < spans.length; i++) {
			int start = spannable.getSpanStart(spans[i]);
			int end = spannable.getSpanEnd(spans[i]);
			spannable.setSpan(targetSpan, start, end,
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}
}
