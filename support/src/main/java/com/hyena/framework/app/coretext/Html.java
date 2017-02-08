/**
 * Copyright (C) 2015 The AppFramework Project
 */
package com.hyena.framework.app.coretext;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.text.BidiFormatter;
import android.text.Editable;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.AlignmentSpan;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.ParagraphStyle;
import android.text.style.QuoteSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.TextAppearanceSpan;
import android.text.style.TypefaceSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;

import com.hyena.framework.app.coretext.Html.ImageGetter;
import com.hyena.framework.app.coretext.Html.TagHandler;
import com.hyena.framework.clientlog.LogUtil;

import org.ccil.cowan.tagsoup.HTMLSchema;
import org.ccil.cowan.tagsoup.Parser;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.io.StringReader;

/**
 * 自定义html解析器
 * @author yangzc
 */
public class Html {

    public static interface ImageGetter {
        public Drawable getDrawable(String source);
    }
	
	public static interface TagHandler {
        public boolean handleTag(boolean opening, String tag, Attributes attributes, 
                                 Editable output, XMLReader xmlReader);
    }
	
	private Html() {
	}

	public static Spanned fromHtml(String source) {
		return fromHtml(source, null, null);
	}

	private static class HtmlParser {
		private static final HTMLSchema schema = new HTMLSchema();
	}

	public static Spanned fromHtml(String source, ImageGetter imageGetter,
			TagHandler tagHandler) {
		Parser parser = new Parser();
		try {
			parser.setProperty(Parser.schemaProperty, HtmlParser.schema);
		} catch (org.xml.sax.SAXNotRecognizedException e) {
			throw new RuntimeException(e);
		} catch (org.xml.sax.SAXNotSupportedException e) {
			throw new RuntimeException(e);
		}

		HtmlToSpannedConverter converter = new HtmlToSpannedConverter(source,
				imageGetter, tagHandler, parser);
		return converter.convert();
	}

	/**
	 * Returns an HTML representation of the provided Spanned text.
	 */
	public static String toHtml(Spanned text) {
		StringBuilder out = new StringBuilder();
		withinHtml(out, text);
		return out.toString();
	}

	/**
	 * Returns an HTML escaped representation of the given plain text.
	 */
	public static String escapeHtml(CharSequence text) {
		StringBuilder out = new StringBuilder();
		withinStyle(out, text, 0, text.length());
		return out.toString();
	}

	private static void withinHtml(StringBuilder out, Spanned text) {
		int len = text.length();

		int next;
		for (int i = 0; i < text.length(); i = next) {
			next = text.nextSpanTransition(i, len, ParagraphStyle.class);
			ParagraphStyle[] style = text.getSpans(i, next,
					ParagraphStyle.class);
			String elements = " ";
			boolean needDiv = false;

			for (int j = 0; j < style.length; j++) {
				if (style[j] instanceof AlignmentSpan) {
					Layout.Alignment align = ((AlignmentSpan) style[j])
							.getAlignment();
					needDiv = true;
					if (align == Layout.Alignment.ALIGN_CENTER) {
						elements = "align=\"center\" " + elements;
					} else if (align == Layout.Alignment.ALIGN_OPPOSITE) {
						elements = "align=\"right\" " + elements;
					} else {
						elements = "align=\"left\" " + elements;
					}
				}
			}
			if (needDiv) {
				out.append("<div ").append(elements).append(">");
			}

			withinDiv(out, text, i, next);

			if (needDiv) {
				out.append("</div>");
			}
		}
	}

	private static void withinDiv(StringBuilder out, Spanned text, int start,
			int end) {
		int next;
		for (int i = start; i < end; i = next) {
			next = text.nextSpanTransition(i, end, QuoteSpan.class);
			QuoteSpan[] quotes = text.getSpans(i, next, QuoteSpan.class);

			for (int j = 0; j < quotes.length; j++) {
				out.append("<blockquote>");
			}

			withinBlockquote(out, text, i, next);

			for (int j = 0; j < quotes.length; j++) {
				out.append("</blockquote>\n");
			}
		}
	}

	private static String getOpenParaTagWithDirection(Spanned text, int start,
			int end) {
//		final int len = end - start;
//		final byte[] levels = new byte[ArrayUtils.idealByteArraySize(len)];
//		final char[] buffer = TextUtils.obtain(len);
//		TextUtils.getChars(text, start, end, buffer, 0);
//		int paraDir = AndroidBidi.bidi(Layout.DIR_REQUEST_DEFAULT_LTR, buffer,
//				levels, len, false /* no info */);
//		switch (paraDir) {
//		case Layout.DIR_RIGHT_TO_LEFT:
//			return "<p dir=\"rtl\">";
//		case Layout.DIR_LEFT_TO_RIGHT:
//		default:
//			return "<p dir=\"ltr\">";
//		}
		
		CharSequence str = text.subSequence(start, end);
		boolean isRtl = BidiFormatter.getInstance().isRtl(str.toString());
		if (isRtl) {
			return "<p dir=\"rtl\">";
		} else {
			return "<p dir=\"ltr\">";
		}
	}

	private static void withinBlockquote(StringBuilder out, Spanned text,
			int start, int end) {
		out.append(getOpenParaTagWithDirection(text, start, end));

		int next;
		for (int i = start; i < end; i = next) {
			next = TextUtils.indexOf(text, '\n', i, end);
			if (next < 0) {
				next = end;
			}

			int nl = 0;

			while (next < end && text.charAt(next) == '\n') {
				nl++;
				next++;
			}

			withinParagraph(out, text, i, next - nl, nl, next == end);
		}

		out.append("</p>\n");
	}

	private static void withinParagraph(StringBuilder out, Spanned text,
			int start, int end, int nl, boolean last) {
		int next;
		for (int i = start; i < end; i = next) {
			next = text.nextSpanTransition(i, end, CharacterStyle.class);
			CharacterStyle[] style = text.getSpans(i, next,
					CharacterStyle.class);

			for (int j = 0; j < style.length; j++) {
				if (style[j] instanceof StyleSpan) {
					int s = ((StyleSpan) style[j]).getStyle();

					if ((s & Typeface.BOLD) != 0) {
						out.append("<b>");
					}
					if ((s & Typeface.ITALIC) != 0) {
						out.append("<i>");
					}
				}
				if (style[j] instanceof TypefaceSpan) {
					String s = ((TypefaceSpan) style[j]).getFamily();

					if (s.equals("monospace")) {
						out.append("<tt>");
					}
				}
				if (style[j] instanceof SuperscriptSpan) {
					out.append("<sup>");
				}
				if (style[j] instanceof SubscriptSpan) {
					out.append("<sub>");
				}
				if (style[j] instanceof UnderlineSpan) {
					out.append("<u>");
				}
				if (style[j] instanceof StrikethroughSpan) {
					out.append("<strike>");
				}
				if (style[j] instanceof URLSpan) {
					out.append("<a href=\"");
					out.append(((URLSpan) style[j]).getURL());
					out.append("\">");
				}
				if (style[j] instanceof ImageSpan) {
					out.append("<img src=\"");
					out.append(((ImageSpan) style[j]).getSource());
					out.append("\">");

					// Don't output the dummy character underlying the image.
					i = next;
				}
				if (style[j] instanceof AbsoluteSizeSpan) {
					out.append("<font size =\"");
					out.append(((AbsoluteSizeSpan) style[j]).getSize() / 6);
					out.append("\">");
				}
				if (style[j] instanceof ForegroundColorSpan) {
					out.append("<font color =\"#");
					String color = Integer
							.toHexString(((ForegroundColorSpan) style[j])
									.getForegroundColor() + 0x01000000);
					while (color.length() < 6) {
						color = "0" + color;
					}
					out.append(color);
					out.append("\">");
				}
			}

			withinStyle(out, text, i, next);

			for (int j = style.length - 1; j >= 0; j--) {
				if (style[j] instanceof ForegroundColorSpan) {
					out.append("</font>");
				}
				if (style[j] instanceof AbsoluteSizeSpan) {
					out.append("</font>");
				}
				if (style[j] instanceof URLSpan) {
					out.append("</a>");
				}
				if (style[j] instanceof StrikethroughSpan) {
					out.append("</strike>");
				}
				if (style[j] instanceof UnderlineSpan) {
					out.append("</u>");
				}
				if (style[j] instanceof SubscriptSpan) {
					out.append("</sub>");
				}
				if (style[j] instanceof SuperscriptSpan) {
					out.append("</sup>");
				}
				if (style[j] instanceof TypefaceSpan) {
					String s = ((TypefaceSpan) style[j]).getFamily();

					if (s.equals("monospace")) {
						out.append("</tt>");
					}
				}
				if (style[j] instanceof StyleSpan) {
					int s = ((StyleSpan) style[j]).getStyle();

					if ((s & Typeface.BOLD) != 0) {
						out.append("</b>");
					}
					if ((s & Typeface.ITALIC) != 0) {
						out.append("</i>");
					}
				}
			}
		}

		String p = last ? "" : "</p>\n"
				+ getOpenParaTagWithDirection(text, start, end);

		if (nl == 1) {
			out.append("<br>\n");
		} else if (nl == 2) {
			out.append(p);
		} else {
			for (int i = 2; i < nl; i++) {
				out.append("<br>");
			}
			out.append(p);
		}
	}

	private static void withinStyle(StringBuilder out, CharSequence text,
			int start, int end) {
		for (int i = start; i < end; i++) {
			char c = text.charAt(i);

			if (c == '<') {
				out.append("&lt;");
			} else if (c == '>') {
				out.append("&gt;");
			} else if (c == '&') {
				out.append("&amp;");
			} else if (c >= 0xD800 && c <= 0xDFFF) {
				if (c < 0xDC00 && i + 1 < end) {
					char d = text.charAt(i + 1);
					if (d >= 0xDC00 && d <= 0xDFFF) {
						i++;
						int codepoint = 0x010000 | (int) c - 0xD800 << 10
								| (int) d - 0xDC00;
						out.append("&#").append(codepoint).append(";");
					}
				}
			} else if (c > 0x7E || c < ' ') {
				out.append("&#").append((int) c).append(";");
			} else if (c == ' ') {
				while (i + 1 < end && text.charAt(i + 1) == ' ') {
					out.append("&nbsp;");
					i++;
				}

				out.append(' ');
			} else {
				out.append(c);
			}
		}
	}
}

class HtmlToSpannedConverter implements ContentHandler {

	private static final float[] HEADER_SIZES = { 1.5f, 1.4f, 1.3f, 1.2f, 1.1f,
			1f, };

	private String mSource;
	private XMLReader mReader;
	private SpannableStringBuilder mSpannableStringBuilder;
	private ImageGetter mImageGetter;
	private TagHandler mTagHandler;

	public HtmlToSpannedConverter(String source, ImageGetter imageGetter,
			TagHandler tagHandler, Parser parser) {
		mSource = source;
		mSpannableStringBuilder = new SpannableStringBuilder();
		mImageGetter = imageGetter;
		mTagHandler = tagHandler;
		mReader = parser;
	}

	public Spanned convert() {

		mReader.setContentHandler(this);
		try {
			mReader.parse(new InputSource(new StringReader(mSource)));
		} catch (IOException e) {
			// We are reading from a string. There should not be IO problems.
			throw new RuntimeException(e);
		} catch (SAXException e) {
			// TagSoup doesn't throw parse exceptions.
			throw new RuntimeException(e);
		}

		// Fix flags and range for paragraph-type markup.
		Object[] obj = mSpannableStringBuilder.getSpans(0,
				mSpannableStringBuilder.length(), ParagraphStyle.class);
		for (int i = 0; i < obj.length; i++) {
			int start = mSpannableStringBuilder.getSpanStart(obj[i]);
			int end = mSpannableStringBuilder.getSpanEnd(obj[i]);

			// If the last line of the range is blank, back off by one.
			if (end - 2 >= 0) {
				if (mSpannableStringBuilder.charAt(end - 1) == '\n'
						&& mSpannableStringBuilder.charAt(end - 2) == '\n') {
					end--;
				}
			}

			if (end == start) {
				mSpannableStringBuilder.removeSpan(obj[i]);
			} else {
				mSpannableStringBuilder.setSpan(obj[i], start, end,
						Spannable.SPAN_PARAGRAPH);
			}
		}

		return mSpannableStringBuilder;
	}

	private void handleStartTag(String tag, Attributes attributes) {
		if (mTagHandler != null) {
			boolean isHandle = mTagHandler.handleTag(true, tag, attributes, mSpannableStringBuilder, mReader);
			if (isHandle) {
				return;
			}
		}
		if (tag.equalsIgnoreCase("br")) {
		} else if (tag.equalsIgnoreCase("p")) {
			handleP(mSpannableStringBuilder);
		} else if (tag.equalsIgnoreCase("div")) {
			handleP(mSpannableStringBuilder);
		} else if (tag.equalsIgnoreCase("strong")) {
			start(mSpannableStringBuilder, new Bold());
		} else if (tag.equalsIgnoreCase("b")) {
			start(mSpannableStringBuilder, new Bold());
		} else if (tag.equalsIgnoreCase("em")) {
			start(mSpannableStringBuilder, new Italic());
		} else if (tag.equalsIgnoreCase("cite")) {
			start(mSpannableStringBuilder, new Italic());
		} else if (tag.equalsIgnoreCase("dfn")) {
			start(mSpannableStringBuilder, new Italic());
		} else if (tag.equalsIgnoreCase("i")) {
			start(mSpannableStringBuilder, new Italic());
		} else if (tag.equalsIgnoreCase("big")) {
			start(mSpannableStringBuilder, new Big());
		} else if (tag.equalsIgnoreCase("small")) {
			start(mSpannableStringBuilder, new Small());
		} else if (tag.equalsIgnoreCase("font")) {
			startFont(mSpannableStringBuilder, attributes);
		} else if (tag.equalsIgnoreCase("blockquote")) {
			handleP(mSpannableStringBuilder);
			start(mSpannableStringBuilder, new Blockquote());
		} else if (tag.equalsIgnoreCase("tt")) {
			start(mSpannableStringBuilder, new Monospace());
		} else if (tag.equalsIgnoreCase("a")) {
			startA(mSpannableStringBuilder, attributes);
		} else if (tag.equalsIgnoreCase("u")) {
			start(mSpannableStringBuilder, new Underline());
		} else if (tag.equalsIgnoreCase("sup")) {
			start(mSpannableStringBuilder, new Super());
		} else if (tag.equalsIgnoreCase("sub")) {
			start(mSpannableStringBuilder, new Sub());
		} else if (tag.length() == 2
				&& Character.toLowerCase(tag.charAt(0)) == 'h'
				&& tag.charAt(1) >= '1' && tag.charAt(1) <= '6') {
			handleP(mSpannableStringBuilder);
			start(mSpannableStringBuilder, new Header(tag.charAt(1) - '1'));
		} else if (tag.equalsIgnoreCase("img")) {
			startImg(mSpannableStringBuilder, attributes, mImageGetter);
		} 
//		else if (mTagHandler != null) {
//			mTagHandler.handleTag(true, tag, mSpannableStringBuilder, mReader);
//		}
	}

	private void handleEndTag(String tag) {
		if (mTagHandler != null) {
			boolean isHandle = mTagHandler.handleTag(false, tag, null, mSpannableStringBuilder, mReader);
			if (isHandle) {
				return;
			}
		}
		
		if (tag.equalsIgnoreCase("br")) {
			handleBr(mSpannableStringBuilder);
		} else if (tag.equalsIgnoreCase("p")) {
			handleP(mSpannableStringBuilder);
		} else if (tag.equalsIgnoreCase("div")) {
			handleP(mSpannableStringBuilder);
		} else if (tag.equalsIgnoreCase("strong")) {
			end(mSpannableStringBuilder, Bold.class, new StyleSpan(
					Typeface.BOLD));
		} else if (tag.equalsIgnoreCase("b")) {
			end(mSpannableStringBuilder, Bold.class, new StyleSpan(
					Typeface.BOLD));
		} else if (tag.equalsIgnoreCase("em")) {
			end(mSpannableStringBuilder, Italic.class, new StyleSpan(
					Typeface.ITALIC));
		} else if (tag.equalsIgnoreCase("cite")) {
			end(mSpannableStringBuilder, Italic.class, new StyleSpan(
					Typeface.ITALIC));
		} else if (tag.equalsIgnoreCase("dfn")) {
			end(mSpannableStringBuilder, Italic.class, new StyleSpan(
					Typeface.ITALIC));
		} else if (tag.equalsIgnoreCase("i")) {
			end(mSpannableStringBuilder, Italic.class, new StyleSpan(
					Typeface.ITALIC));
		} else if (tag.equalsIgnoreCase("big")) {
			end(mSpannableStringBuilder, Big.class, new RelativeSizeSpan(1.25f));
		} else if (tag.equalsIgnoreCase("small")) {
			end(mSpannableStringBuilder, Small.class,
					new RelativeSizeSpan(0.8f));
		} else if (tag.equalsIgnoreCase("font")) {
			endFont(mSpannableStringBuilder);
		} else if (tag.equalsIgnoreCase("blockquote")) {
			handleP(mSpannableStringBuilder);
			end(mSpannableStringBuilder, Blockquote.class, new QuoteSpan());
		} else if (tag.equalsIgnoreCase("tt")) {
			end(mSpannableStringBuilder, Monospace.class, new TypefaceSpan(
					"monospace"));
		} else if (tag.equalsIgnoreCase("a")) {
			endA(mSpannableStringBuilder);
		} else if (tag.equalsIgnoreCase("u")) {
			end(mSpannableStringBuilder, Underline.class, new UnderlineSpan());
		} else if (tag.equalsIgnoreCase("sup")) {
			end(mSpannableStringBuilder, Super.class, new SuperscriptSpan());
		} else if (tag.equalsIgnoreCase("sub")) {
			end(mSpannableStringBuilder, Sub.class, new SubscriptSpan());
		} else if (tag.length() == 2
				&& Character.toLowerCase(tag.charAt(0)) == 'h'
				&& tag.charAt(1) >= '1' && tag.charAt(1) <= '6') {
			handleP(mSpannableStringBuilder);
			endHeader(mSpannableStringBuilder);
		} 
//		else if (mTagHandler != null) {
//			mTagHandler.handleTag(false, tag, mSpannableStringBuilder, mReader);
//		}
	}

	private static void handleP(SpannableStringBuilder text) {
		int len = text.length();

		if (len >= 1 && text.charAt(len - 1) == '\n') {
			if (len >= 2 && text.charAt(len - 2) == '\n') {
				return;
			}

			text.append("\n");
			return;
		}

		if (len != 0) {
			text.append("\n\n");
		}
	}

	private static void handleBr(SpannableStringBuilder text) {
		text.append("\n");
	}

	private static Object getLast(Spanned text, Class<?> kind) {
		/*
		 * This knows that the last returned object from getSpans() will be the
		 * most recently added.
		 */
		Object[] objs = text.getSpans(0, text.length(), kind);

		if (objs.length == 0) {
			return null;
		} else {
			return objs[objs.length - 1];
		}
	}

	private static void start(SpannableStringBuilder text, Object mark) {
		int len = text.length();
		text.setSpan(mark, len, len, Spannable.SPAN_MARK_MARK);
	}

	private static void end(SpannableStringBuilder text, Class<?> kind, Object repl) {
		int len = text.length();
		Object obj = getLast(text, kind);
		int where = text.getSpanStart(obj);

		text.removeSpan(obj);

		if (where != len) {
			text.setSpan(repl, where, len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}

	private static void startImg(SpannableStringBuilder text,
			Attributes attributes, ImageGetter img) {
		String src = attributes.getValue("", "src");
		Drawable d = null;

		if (img != null) {
			d = img.getDrawable(src);
		}

		if (d == null) {
			//default drawable
			try {
				d = Resources.getSystem().getDrawable(0);
				d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
			} catch (Exception e) {
				LogUtil.e("Html", e);
			}
		}

		int len = text.length();
		text.append("\uFFFC");

		text.setSpan(new ImageSpan(d, src), len, text.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	}

	private static void startFont(SpannableStringBuilder text,
			Attributes attributes) {
		String color = attributes.getValue("", "color");
		String face = attributes.getValue("", "face");

		int len = text.length();
		text.setSpan(new Font(color, face), len, len, Spannable.SPAN_MARK_MARK);
	}

	private static void endFont(SpannableStringBuilder text) {
		int len = text.length();
		Object obj = getLast(text, Font.class);
		int where = text.getSpanStart(obj);

		text.removeSpan(obj);

		if (where != len) {
			Font f = (Font) obj;

			if (!TextUtils.isEmpty(f.mColor)) {
				if (f.mColor.startsWith("@")) {
					Resources res = Resources.getSystem();
					String name = f.mColor.substring(1);
					int colorRes = res.getIdentifier(name, "color", "android");
					if (colorRes != 0) {
						ColorStateList colors = res.getColorStateList(colorRes);
						text.setSpan(new TextAppearanceSpan(null, 0, 0, colors,
								null), where, len,
								Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
				} else {
					int c = Color.parseColor(f.mColor);
					if (c != -1) {
						text.setSpan(new ForegroundColorSpan(c | 0xFF000000),
								where, len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
				}
			}

			if (f.mFace != null) {
				text.setSpan(new TypefaceSpan(f.mFace), where, len,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
	}

	private static void startA(SpannableStringBuilder text,
			Attributes attributes) {
		String href = attributes.getValue("", "href");

		int len = text.length();
		text.setSpan(new Href(href), len, len, Spannable.SPAN_MARK_MARK);
	}

	private static void endA(SpannableStringBuilder text) {
		int len = text.length();
		Object obj = getLast(text, Href.class);
		int where = text.getSpanStart(obj);

		text.removeSpan(obj);

		if (where != len) {
			Href h = (Href) obj;

			if (h.mHref != null) {
				text.setSpan(new URLSpan(h.mHref), where, len,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
	}

	private static void endHeader(SpannableStringBuilder text) {
		int len = text.length();
		Object obj = getLast(text, Header.class);

		int where = text.getSpanStart(obj);

		text.removeSpan(obj);

		// Back off not to change only the text, not the blank line.
		while (len > where && text.charAt(len - 1) == '\n') {
			len--;
		}

		if (where != len) {
			Header h = (Header) obj;

			text.setSpan(new RelativeSizeSpan(HEADER_SIZES[h.mLevel]), where,
					len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			text.setSpan(new StyleSpan(Typeface.BOLD), where, len,
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}

	public void setDocumentLocator(Locator locator) {
	}

	public void startDocument() throws SAXException {
	}

	public void endDocument() throws SAXException {
	}

	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
	}

	public void endPrefixMapping(String prefix) throws SAXException {
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		handleStartTag(localName, attributes);
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		handleEndTag(localName);
	}

	public void characters(char ch[], int start, int length)
			throws SAXException {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			char c = ch[i + start];

			if (c == ' ' || c == '\n') {
				char pred;
				int len = sb.length();

				if (len == 0) {
					len = mSpannableStringBuilder.length();

					if (len == 0) {
						pred = '\n';
					} else {
						pred = mSpannableStringBuilder.charAt(len - 1);
					}
				} else {
					pred = sb.charAt(len - 1);
				}

				if (pred != ' ' && pred != '\n') {
					sb.append(' ');
				}
			} else {
				sb.append(c);
			}
		}

		mSpannableStringBuilder.append(sb);
	}

	public void ignorableWhitespace(char ch[], int start, int length)
			throws SAXException {
	}

	public void processingInstruction(String target, String data)
			throws SAXException {
	}

	public void skippedEntity(String name) throws SAXException {
	}

	private static class Bold {
	}

	private static class Italic {
	}

	private static class Underline {
	}

	private static class Big {
	}

	private static class Small {
	}

	private static class Monospace {
	}

	private static class Blockquote {
	}

	private static class Super {
	}

	private static class Sub {
	}

	private static class Font {
		public String mColor;
		public String mFace;

		public Font(String color, String face) {
			mColor = color;
			mFace = face;
		}
	}

	private static class Href {
		public String mHref;

		public Href(String href) {
			mHref = href;
		}
	}

	private static class Header {
		private int mLevel;

		public Header(int level) {
			mLevel = level;
		}
	}
	
}
