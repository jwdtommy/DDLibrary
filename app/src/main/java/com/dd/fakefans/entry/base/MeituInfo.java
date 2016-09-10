package com.dd.fakefans.entry.base;

import java.io.Serializable;
import java.util.List;

/**
 * Created by J.Tommy on 16/9/10.
 */
public class MeituInfo implements Serializable {
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

        public int getMaxResult() {
            return maxResult;
        }

        public void setMaxResult(int maxResult) {
            this.maxResult = maxResult;
        }

        public List<ContentlistBean> getContentlist() {
            return contentlist;
        }

        public void setContentlist(List<ContentlistBean> contentlist) {
            this.contentlist = contentlist;
        }

        public static class ContentlistBean {
            private String typeName;
            private String title;
            private int type;
            private String itemId;
            private String ct;
            /**
             * big : http://image.tianjimedia.com/uploadImages/2014/336/02/77977OQ96YQQ.jpg
             * small : http://image.tianjimedia.com/uploadImages/2014/336/02/77977OQ96YQQ_113.jpg
             * middle : http://image.tianjimedia.com/uploadImages/2014/336/02/77977OQ96YQQ_680x500.jpg
             */

            private List<ListBean> list;

            public String getTypeName() {
                return typeName;
            }

            public void setTypeName(String typeName) {
                this.typeName = typeName;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public String getItemId() {
                return itemId;
            }

            public void setItemId(String itemId) {
                this.itemId = itemId;
            }

            public String getCt() {
                return ct;
            }

            public void setCt(String ct) {
                this.ct = ct;
            }

            public List<ListBean> getList() {
                return list;
            }

            public void setList(List<ListBean> list) {
                this.list = list;
            }

            public static class ListBean {
                private String big;
                private String small;
                private String middle;

                public String getBig() {
                    return big;
                }

                public void setBig(String big) {
                    this.big = big;
                }

                public String getSmall() {
                    return small;
                }

                public void setSmall(String small) {
                    this.small = small;
                }

                public String getMiddle() {
                    return middle;
                }

                public void setMiddle(String middle) {
                    this.middle = middle;
                }
            }
        }
    }
}

