/**
 * Copyright (C) 2015 The AppFramework Project
 */
package com.hyena.framework.app.coretext.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;

import android.text.Editable;

import com.hyena.framework.app.coretext.Html.TagHandler;
import com.hyena.framework.clientlog.LogUtil;

/**
 * 默认的TagHandler
 * 
 * @author yangzc
 *
 */
public abstract class DefaultTagHandler implements TagHandler {

	/**
	 * 标签信息
	 */
	public static class TagInfo implements Serializable {

		/** 序列化 */
		private static final long serialVersionUID = -9038460255379771825L;
		// 标签名称
		public String tagName;
		// 标签属性
		public Attributes mAttributes;
		// 开始位置
		public int mStartIndex;
		// 结束位置
		public int mEndIndex;
		// 父节点Tag
		public TagInfo mParent;
		// 所有子节点
		public List<TagInfo> mChilds;
		//是否处理TAG
		protected boolean mIsHandleTag;

		/**
		 * 添加子节点
		 * 
		 * @param tagInfo
		 */
		public void addChild(TagInfo tagInfo) {
			if (mChilds == null) {
				mChilds = new ArrayList<DefaultTagHandler.TagInfo>();
			}
			mChilds.add(tagInfo);
		}

		public void printf() {
			LogUtil.v("yangzc", "tagname: " + tagName + ", startIndex: "
					+ mStartIndex + ", endIndex: " + mEndIndex);
		}
	}

	private TagInfo mRootTag;
	private Stack<TagInfo> mClosingTags;

	public DefaultTagHandler() {
		mRootTag = new TagInfo();
		mClosingTags = new Stack<TagInfo>();
	}

	/**
	 * 获得根节点
	 * 
	 * @return
	 */
	public TagInfo getRootTag() {
		return mRootTag;
	}

	@Override
	public boolean handleTag(boolean opening, String tag,
			Attributes attributes, Editable output, XMLReader xmlReader) {
		LogUtil.v("yangzc", "opening: " + opening + ", tag: " + tag);
		TagInfo tagInfo = null;
		if (opening) {
			tagInfo = new TagInfo();
			tagInfo.tagName = tag;
			tagInfo.mAttributes = attributes;
			tagInfo.mStartIndex = output.length();
			tagInfo.mIsHandleTag = isHandleTag(tag, attributes);

			if (mClosingTags.size() > 0) {
				tagInfo.mParent = mClosingTags.peek();
			}
			if (tagInfo.mParent == null) {
				// 不存在父节点则设定为跟节点
				mRootTag = tagInfo;
			} else {
				tagInfo.mParent.addChild(tagInfo);
			}
			mClosingTags.add(tagInfo);
		} else {
			tagInfo = mClosingTags.pop();
			tagInfo.mEndIndex = output.length();

			tagInfo.printf();
			handleEndTag(tagInfo, output, xmlReader);
		}
		return tagInfo.mIsHandleTag;
	}

	/**
	 * 是否处理TAG
	 * 
	 * @param tag
	 * @param attributes
	 * @return
	 */
	public abstract boolean isHandleTag(String tag, Attributes attributes);

	/**
	 * 处理HtmlTag
	 * 
	 * @param tagInfo
	 * @param output
	 * @param xmlReader
	 */
	public abstract void handleEndTag(TagInfo tagInfo, Editable output,
			XMLReader xmlReader);
}
