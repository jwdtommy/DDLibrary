package com.dd.fakefans.entry;

import java.util.List;

/**
 * Created by adong on 16/8/22.
 */
public class BuDeJieInfo {

    private int ret_code;
    /**
     * allPages : 2226
     * currentPage : 1
     * allNum : 44519
     * maxResult : 20
     */

    private final String TYPE_PIC = "10";
    private final String TYPE_WORD = "29";
    private final String TYPE_AUDIO = "31";
    private final String TYPE_VIDEO = "41";
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
        /**
         * text :  笑的好灿烂，爱笑的女孩子运气不会差！
         * hate : 66
         * videotime : 0
         * voicetime : 0
         * weixin_url : http://m.budejie.com/detail-20082115.html/
         * profile_image : http://wimg.spriteapp.cn/profile/large/2016/07/28/57995e8dc41aa_mini.jpg
         * width : 0
         * voiceuri :
         * type : 10
         * image0 : http://wimg.spriteapp.cn/ugc/2016/08/21/57b9c84dcc86b.gif
         * id : 20082115
         * love : 170
         * image2 : http://wimg.spriteapp.cn/ugc/2016/08/21/57b9c84dcc86b.gif
         * image1 : http://wimg.spriteapp.cn/ugc/2016/08/21/57b9c84dcc86b.gif
         * height : 0
         * name : 污图达人
         * create_time : 2016-08-22 11:16:01
         * image3 : http://wimg.spriteapp.cn/ugc/2016/08/21/57b9c84dcc86b.gif
         */

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
            private String text;
            private String hate;
            private String videotime;
            private String voicetime;
            private String weixin_url;
            private String profile_image;
            private String width;
            private String voiceuri;
            private String type;
            private String image0;
            private String id;
            private String love;
            private String image2;
            private String image1;
            private String height;
            private String name;
            private String create_time;
            private String image3;

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }

            public String getHate() {
                return hate;
            }

            public void setHate(String hate) {
                this.hate = hate;
            }

            public String getVideotime() {
                return videotime;
            }

            public void setVideotime(String videotime) {
                this.videotime = videotime;
            }

            public String getVoicetime() {
                return voicetime;
            }

            public void setVoicetime(String voicetime) {
                this.voicetime = voicetime;
            }

            public String getWeixin_url() {
                return weixin_url;
            }

            public void setWeixin_url(String weixin_url) {
                this.weixin_url = weixin_url;
            }

            public String getProfile_image() {
                return profile_image;
            }

            public void setProfile_image(String profile_image) {
                this.profile_image = profile_image;
            }

            public String getWidth() {
                return width;
            }

            public void setWidth(String width) {
                this.width = width;
            }

            public String getVoiceuri() {
                return voiceuri;
            }

            public void setVoiceuri(String voiceuri) {
                this.voiceuri = voiceuri;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getImage0() {
                return image0;
            }

            public void setImage0(String image0) {
                this.image0 = image0;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getLove() {
                return love;
            }

            public void setLove(String love) {
                this.love = love;
            }

            public String getImage2() {
                return image2;
            }

            public void setImage2(String image2) {
                this.image2 = image2;
            }

            public String getImage1() {
                return image1;
            }

            public void setImage1(String image1) {
                this.image1 = image1;
            }

            public String getHeight() {
                return height;
            }

            public void setHeight(String height) {
                this.height = height;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getCreate_time() {
                return create_time;
            }

            public void setCreate_time(String create_time) {
                this.create_time = create_time;
            }

            public String getImage3() {
                return image3;
            }

            public void setImage3(String image3) {
                this.image3 = image3;
            }

            public float getAspectRatio() {
                float fWidth = Float.parseFloat(width);
                float fHeight = Float.parseFloat(height);

                if (fHeight > 0f) {
                    return fWidth / fHeight;
                }
                return 0;
            }


            @Override
            public String toString() {
                return "ContentlistBean{" +
                        "text='" + text + '\'' +
                        ", hate='" + hate + '\'' +
                        ", videotime='" + videotime + '\'' +
                        ", voicetime='" + voicetime + '\'' +
                        ", weixin_url='" + weixin_url + '\'' +
                        ", profile_image='" + profile_image + '\'' +
                        ", width='" + width + '\'' +
                        ", voiceuri='" + voiceuri + '\'' +
                        ", type='" + type + '\'' +
                        ", image0='" + image0 + '\'' +
                        ", id='" + id + '\'' +
                        ", love='" + love + '\'' +
                        ", image2='" + image2 + '\'' +
                        ", image1='" + image1 + '\'' +
                        ", height='" + height + '\'' +
                        ", name='" + name + '\'' +
                        ", create_time='" + create_time + '\'' +
                        ", image3='" + image3 + '\'' +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "PagebeanBean{" +
                    "allPages=" + allPages +
                    ", currentPage=" + currentPage +
                    ", allNum=" + allNum +
                    ", maxResult=" + maxResult +
                    ", contentlist=" + contentlist +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "BuDeJieInfo{" +
                "ret_code=" + ret_code +
                ", pagebean=" + pagebean +
                '}';
    }
}

