package com.dd.fakefans.entry;

import java.util.List;

/**
 * Created by J.Tommy on 17/2/7.
 */

public class MessageInfo {
		private int ret_code;
		private PagebeanBean pagebean;

		public int getRet_code() {
			return ret_code;
		}

		public void setRet_code(int ret_code) {
			this.ret_code = ret_code;
		}

		public PagebeanBean getPagebean() {
			return pagebean;
		}

		public void setPagebean(PagebeanBean pagebean) {
			this.pagebean = pagebean;
		}

		public static class PagebeanBean {
			private int allPages;
			private int currentPage;
			private int allNum;
			private int maxResult;

			private List<ContentlistBean> contentlist;

			public int getAllPages() {
				return allPages;
			}

			public void setAllPages(int allPages) {
				this.allPages = allPages;
			}

			public int getCurrentPage() {
				return currentPage;
			}

			public void setCurrentPage(int currentPage) {
				this.currentPage = currentPage;
			}

			public int getAllNum() {
				return allNum;
			}

			public void setAllNum(int allNum) {
				this.allNum = allNum;
			}

			public int getAxResult() {
				return maxResult;
			}

			public void setAxResult(int axResult) {
				maxResult = axResult;
			}

			public List<ContentlistBean> getContentlist() {
				return contentlist;
			}

			public void setContentlist(List<ContentlistBean> contentlist) {
				this.contentlist = contentlist;
			}

			public static class ContentlistBean {
				private String pubDate;
				private boolean havePic;
				private String title;
				private String channelName;
				private String desc;
				private String source;
				private String channelId;
				private String link;
				private String html;
				/**
				 * height : 814
				 * width : 600
				 * url : http://inews.gtimg.com/newsapp_bt/0/380646410/641
				 */
				/**
				 * height : 814
				 * width : 600
				 * url : http://inews.gtimg.com/newsapp_bt/0/380646410/641
				 */

				private List<ImageurlsBean> imageurls;

				public String getPubDate() {
					return pubDate;
				}

				public void setPubDate(String pubDate) {
					this.pubDate = pubDate;
				}

				public boolean isHavePic() {
					return havePic;
				}

				public void setHavePic(boolean havePic) {
					this.havePic = havePic;
				}

				public String getTitle() {
					return title;
				}

				public void setTitle(String title) {
					this.title = title;
				}

				public String getChannelName() {
					return channelName;
				}

				public void setChannelName(String channelName) {
					this.channelName = channelName;
				}

				public String getDesc() {
					return desc;
				}

				public void setDesc(String desc) {
					this.desc = desc;
				}

				public String getSource() {
					return source;
				}

				public void setSource(String source) {
					this.source = source;
				}

				public String getChannelId() {
					return channelId;
				}

				public void setChannelId(String channelId) {
					this.channelId = channelId;
				}

				public String getLink() {
					return link;
				}

				public void setLink(String link) {
					this.link = link;
				}

				public String getHtml() {
					return html;
				}

				public void setHtml(String html) {
					this.html = html;
				}


				public List<ImageurlsBean> getImageurls() {
					return imageurls;
				}

				public void setImageurls(List<ImageurlsBean> imageurls) {
					this.imageurls = imageurls;
				}

				public static class AllListBean {
					private int height;
					private int width;
					private String url;

					public int getHeight() {
						return height;
					}

					public void setHeight(int height) {
						this.height = height;
					}

					public int getWidth() {
						return width;
					}

					public void setWidth(int width) {
						this.width = width;
					}

					public String getUrl() {
						return url;
					}

					public void setUrl(String url) {
						this.url = url;
					}
				}

				public static class ImageurlsBean {
					private int height;
					private int width;
					private String url;

					public int getHeight() {
						return height;
					}

					public void setHeight(int height) {
						this.height = height;
					}

					public int getWidth() {
						return width;
					}

					public void setWidth(int width) {
						this.width = width;
					}

					public String getUrl() {
						return url;
					}

					public void setUrl(String url) {
						this.url = url;
					}
				}
			}
		}
}
