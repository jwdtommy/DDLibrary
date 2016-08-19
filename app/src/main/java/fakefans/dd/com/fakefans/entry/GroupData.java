package fakefans.dd.com.fakefans.entry;

import java.io.Serializable;
import java.util.List;

public class GroupData implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 7807688437410193400L;

    private String category_id;
    private String id;
    private List<String> news_class_id;
    private String type;
    private String view_type;
    // private String title;
    private String short_title;
    private String description;
    private List<String> image;
    private String copyfrom;
    private String authors;
    private String news_timestamp;
    private String news_datetime;

    private String comment_count;
    private String likes_count;
    // private String help_love_count;// 爱心数
    private String subscribe_count;
    private String join_count;

    private String tags;
    private String news_link;
    private long downLoadTime;
    private String show_title;//暂时只有专题头图用到了 2016.1.28

    private String column_id;//点击后跳转的栏目id

    public static final int REDIRECT_TYPE_NONE = 0;// 不做跳转,点击无动作
    public static final int REDIRECT_TYPE_NORMAL = 1;// 跳转，普通新闻
    public static final int REDIRECT_TYPE_WEB_OUTTER = 2;// 跳转，外链
    public static final int REDIRECT_TYPE_WEB_INNER = 3;// 跳转，内链
    public static final int REDIRECT_TYPE_INSTITUTIONS = 4;// 跳转， 机构
    public static final int REDIRECT_TYPE_SUBJECT = 5;// 跳转， 专题
    public static final int REDIRECT_TYPE_AUDIO_SUBJECT = 6;// 跳转，音频 专题
    public static final int REDIRECT_TYPE_VIDEO = 10;// 不做跳转,直接播放视频
    public static final int REDIRECT_TYPE_AUDIO = 11;// 不做跳转，直接播放音频
    public static final int REDIRECT_TYPE_CLOUMN = 12;// 不做跳转，直接播放音频
    public static final int REDIRECT_TYPE_LIFE_SERVICE = 32;// 生活服务
    public static final int REDIRECT_TYPE_LOADING_AD = 31;// 开屏图广告
    /**
     * NewsDetailActivity
     **/
    public static final String TYPE_NORMAL = "normal";
    public static final String TYPE_VIDEO = "video";
    public static final String TYPE_AUDIO = "audio";
    public static final String TYPE_PAPER = "paper";
    public static final String TYPE_HELP = "help";
    public static final String TYPE_FLASHS = "flashs";
    public static final String TYPE_GOVERMENT = "government";
    public static final String TYPE_COMMENT = "comment";
    public static final String TYPE_TOPIC = "topic";
    public static final String TYPE_AUDIO_SUBJECT = "album";
    public static final String TYPE_COLUMN = "column";//跳转到栏目
    /**
     * AskDetailActivigty
     **/
    public static final String TYPE_ASK = "ask";
    public static final String TYPE_ASK_1 = "ask1";//推送过来的type叫ask_1,为了和老版本作区分
    /**
     * 问话题
     */
    public static final String TYPE_ASK_TOPIC = "asktopic";
    /**
     * LivingRoomActivity
     **/
    public static final String TYPE_LIVE = "live";
    public static final String TYPE_INTERVIEW = "interview";
    /**
     * PictureDetailActivity
     **/
    public static final String TYPE_IMG = "img";// 图集
    /**
     * FlexibleFormActivity
     **/
    public static final String TYPE_COLLECTION = "collection";
    /**
     * VRPlayerActivity
     **/
    public static final String TYPE_VRVIDEO = "vrvideo";

    public static final String TYPE_ADVERT = "advert";// 广告

    public static final String TYPE_RANK = "rank";//“问"排行榜

    public static final String TYPE_ASK_DISCUSS = "ask_discuss";//问题讨论页

    public static final String[] ACTIVITY_NORMAL = {TYPE_NORMAL, TYPE_VIDEO, TYPE_AUDIO, TYPE_PAPER, TYPE_HELP,
            TYPE_FLASHS, TYPE_GOVERMENT};

    public static final String[] ACTIVITY_ASK = {TYPE_ASK, TYPE_ASK_1};//可以跳转到问详情的type
    public static final String[] ACTIVITY_ASK_TOPIC = {TYPE_ASK_TOPIC};//可以跳转到问话题的type
    public static final String[] ACTIVITY_LIVING = {TYPE_LIVE, TYPE_INTERVIEW};//可以跳转到直播的type
    public static final String[] ACTIVITY_IMG = {TYPE_IMG};//可以跳转到图集的type
    public static final String[] ACTIVITY_COLLECTION = {TYPE_COLLECTION};//可以跳转到活动征集的type
    public static final String[] ACTIVITY_VRVIDEO = {TYPE_VRVIDEO};//可以跳转到VR视频的type
    public static final String[] ACTIVITY_RANK = {TYPE_RANK};//可以跳转到问排行榜的type
//    public static final String[] ACTIVITY_ASK_DISCUSS={TYPE_ASK_DISCUSS};//可以跳转到问题讨论页；

    private String source_link;
    private String source_id;
    private String video_link_1;
    private String video_link_2;
    private String audio_link;
    private String medias_times;
    private String share_url;
    private String reply_count;
    private String categories;
    private String share_logo;

    private String img_count;// 图集 中一共有多少张图

    private int status;// 直播、帮、或者问的状态（直播（1：预告，2：直播中，3：直播结束）。帮（1：募集中，2：募集结束，3：募集反馈）。问（1：有回复，2是没有回复））

    /**
     * 注：接口只返回status字段（为了节省字段）。由于动态数据会返回live状态的原因，导致客户端不得不自行做不同状态的区分。
     */
    private int status_ask;
    private int status_help;
    private int status_live;

    public static final int STATUS_LIVE_FORECAST = 1;// 直播（访谈） 预告
    public static final int STATUS_LIVE_GOING = 2;// 直播（访谈）进行中
    public static final int STATUS_LIVE_END = 3;// 直播（访谈）结束

    public static final int STATUS_HELP_GOING = 1;// 帮 募集中
    public static final int STATUS_HELP_END = 2;// 帮 结束
    public static final int STATUS_HELP_FEEDBACK = 3;// 帮 反馈

    public static final int STATUS_ASK_HAS_REPLY = 1;// 问 有回复
    public static final int STATUS_ASK_HAS_NO_REPLY = 2;// 问 没回复

    /****
     * “帮” 需要的字段
     *****/
    private String help_start_date;// 开始时间
    private String help_state;// 声明
    /*********************/

    /*******
     * 广告需要字段
     *******/
    private int position;
    private String start_time;
    private String end_time;
    private String pv_link;
    private String click_link;

    /*********
     * 搜索
     ********/
    private String listen;

    /**
     * 直播需要的字段
     **/
    private String start_date;

    public static final String NORMAL_1 = "normal_1";
    public static final String NORMAL_2 = "normal_2";
    public static final String VIDEO_1 = "video_1";
    public static final String VIDEO_2 = "video_2";
    public static final String VIDEO_3 = "video_3";
    public static final String VIDEO_4 = "video_4";
    public static final String AUDIO_1 = "audio_1";
    public static final String AUDIO_2 = "audio_2";
    public static final String AUDIO_3 = "audio_3";
    public static final String AUDIO_4 = "audio_4";
    public static final String AUDIO_8 = "audio_8";
    public static final String AUDIO_9 = "audio_9";
    public static final String AUDIO_5 = "audio_5";
    public static final String PAPER_1 = "paper_1";
    public static final String PAPER_2 = "paper_2";
    public static final String PAPER_3 = "paper_3";
    public static final String PAPER_4 = "paper_4";
    public static final String ASK_1 = "ask_1";
    public static final String ASK_2 = "ask_2";
    public static final String HELP_1 = "help_1";
    public static final String HELP_2 = "help_2";
    public static final String HELP_3 = "help_3";
    public static final String LIVE_1 = "live_1";
    public static final String INTERVIEW_1 = "interview_1";
    public static final String FLASHES_1 = "flashes_1";
    public static final String TOPIC_1 = "topic_1";
    public static final String TOPIC_2 = "topic_2";
    public static final String GOVERNMENT_1 = "government_1";
    public static final String IMG_1 = "img_1";
    public static final String IMG_2 = "img_2";
    public static final String IMG_3 = "img_3";
    public static final String IMG_4 = "img_4";
    public static final String IMG_5 = "img_5";
    public static final String IMG_6 = "img_6";
    public static final String IMG_7 = "img_7";
    public static final String ADVERT_1 = "advert_1";
    public static final String ADVERT_2 = "advert_2";
    public static final String ADVERT_3 = "advert_3";

    public GroupData() {
        category_id = "";
        id = "";
        view_type = "";
        // title = "";
        short_title = "";
        description = "";
        copyfrom = "";
        authors = "";
        news_timestamp = "";
        news_datetime = "";
        comment_count = "";
        likes_count = "";
        tags = "";
        news_link = "";
        source_link = "";
        video_link_1 = "";
        video_link_2 = "";
        audio_link = "";
        medias_times = "";
        share_url = "";
        reply_count = "";
        categories = "";
        listen = "0";
        share_logo = "";
        downLoadTime = 0;
        column_id = "";
    }

    public String getColumn_id() {
        return column_id;
    }

    public void setColumn_id(String column_id) {
        this.column_id = column_id;
    }

    public String getShare_logo() {
        return share_logo;
    }

    public void setShare_logo(String share_logo) {
        this.share_logo = share_logo;
    }

    public String getJoin_count() {
        return join_count;
    }

    public void setJoin_count(String join_count) {
        this.join_count = join_count;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getId() {

        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getView_type() {
        return view_type;
    }

    public void setView_type(String view_type) {
        this.view_type = view_type;
    }

    public String getImg_count() {
        return img_count;
    }

    public void setImg_count(String img_count) {
        this.img_count = img_count;
    }

    // public String getTitle() {
    // return title;
    // }
    //
    // public void setTitle(String title) {
    // this.title = title;
    // }

    public String getShort_title() {
        return short_title;
    }

    public void setShort_title(String short_title) {
        this.short_title = short_title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getImage() {
        return image;
    }

    public void setImage(List<String> image) {
        this.image = image;
    }

    public String getCopyfrom() {
        return copyfrom;
    }

    public void setCopyfrom(String copyfrom) {
        this.copyfrom = copyfrom;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getNews_timestamp() {
        return news_timestamp;
    }

    public void setNews_timestamp(String news_timestamp) {
        this.news_timestamp = news_timestamp;
    }

    public String getNews_datetime() {
        return news_datetime;
    }

    public void setNews_datetime(String news_datetime) {
        this.news_datetime = news_datetime;
    }

    public String getComment_count() {
                return comment_count;
    }

    public void setComment_count(String comment_count) {
        this.comment_count = comment_count;
    }

    public String getLikes_count() {
        return likes_count;
    }

    public void setLikes_count(String likes_count) {
        this.likes_count = likes_count;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getNews_link() {
        return news_link;
    }

    public void setNews_link(String news_link) {
        this.news_link = news_link;
    }

    public String getSource_link() {
        return source_link;
    }

    public void setSource_link(String source_link) {
        this.source_link = source_link;
    }

    public String getVideo_link_1() {
        return video_link_1;
    }

    public void setVideo_link_1(String video_link_1) {
        this.video_link_1 = video_link_1;
    }

    public String getVideo_link_2() {
        return video_link_2;
    }

    public void setVideo_link_2(String video_link_2) {
        this.video_link_2 = video_link_2;
    }

    public String getAudio_link() {
        return audio_link;
    }

    public void setAudio_link(String audio_link) {
        this.audio_link = audio_link;
    }

    public String getMedias_times() {
        return medias_times;
    }

    public void setMedias_times(String medias_times) {
        this.medias_times = medias_times;
    }

    public List<String> getNews_class_id() {
        return news_class_id;
    }

    public void setNews_class_id(List<String> news_class_id) {
        this.news_class_id = news_class_id;
    }

    public String getShare_url() {
        return share_url;
    }

    public void setShare_url(String share_url) {
        this.share_url = share_url;
    }

    public String getReply_count() {
        return reply_count;
    }

    public void setReply_count(String replay_count) {
        this.reply_count = replay_count;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String getSubscribe_count() {
        return subscribe_count;
    }

    public void setSubscribe_count(String subscribe_count) {
        this.subscribe_count = subscribe_count;
    }

    public String getHelp_state() {
        return help_state;
    }

    public void setHelp_state(String help_state) {
        this.help_state = help_state;
    }

    public String getHelp_start_date() {
        return help_start_date;
    }

    public void setHelp_start_date(String help_start_date) {
        this.help_start_date = help_start_date;
    }

    // public String getHelp_love_count() {
    // int count =
    // DataCountsUtils.getInstance(App.getInstance()).getLoveCount(id);
    // if (CheckUtils.isNoEmptyStr(help_love_count)) {
    // if (count > Integer.valueOf(help_love_count)) {
    // return count + "";
    // }
    // } else {
    // return count + "";
    // }
    //
    // return help_love_count;
    // }
    //
    // public void setHelp_love_count(String help_love_count) {
    // this.help_love_count = help_love_count;
    // }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSource_id() {
        return source_id;
    }

    public void setSource_id(String source_id) {
        this.source_id = source_id;
    }

//	public int getStatus() {
//		return status;
//	}

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus_ask() {
        return status;
    }

    public void setStatus_ask(int status_ask) {
        this.status_ask = status_ask;
    }

    public int getStatus_help() {
        return status;
    }

    public void setStatus_help(int status) {
        this.status = status;
    }

    public int getStatus_live() {
        return status;
    }

    public void setStatus_live(int status_live) {
        this.status_live = status_live;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getPv_link() {
        return pv_link;
    }

    public void setPv_link(String pv_link) {
        this.pv_link = pv_link;
    }

    public String getClick_link() {
        return click_link;
    }

    public void setClick_link(String click_link) {
        this.click_link = click_link;
    }

    public String getListen() {
        return listen;
    }

    public void setListen(String listen) {
        this.listen = listen;
    }

    public long getDownLoadTime() {
        return downLoadTime;
    }

    public void setDownLoadTime(long downLoadTime) {
        this.downLoadTime = downLoadTime;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getShow_title() {
        return show_title;
    }

    public void setShow_title(String show_title) {
        this.show_title = show_title;
    }

    @Override
    public String toString() {
        return "GroupData{" +
                "category_id='" + category_id + '\'' +
                ", id='" + id + '\'' +
                ", news_class_id=" + news_class_id +
                ", type='" + type + '\'' +
                ", view_type='" + view_type + '\'' +
                ", short_title='" + short_title + '\'' +
                ", description='" + description + '\'' +
                ", image=" + image +
                ", copyfrom='" + copyfrom + '\'' +
                ", authors='" + authors + '\'' +
                ", news_timestamp='" + news_timestamp + '\'' +
                ", news_datetime='" + news_datetime + '\'' +
                ", comment_count='" + comment_count + '\'' +
                ", likes_count='" + likes_count + '\'' +
                ", subscribe_count='" + subscribe_count + '\'' +
                ", join_count='" + join_count + '\'' +
                ", tags='" + tags + '\'' +
                ", news_link='" + news_link + '\'' +
                ", downLoadTime=" + downLoadTime +
                ", show_title='" + show_title + '\'' +
                ", column_id='" + column_id + '\'' +
                ", source_link='" + source_link + '\'' +
                ", source_id='" + source_id + '\'' +
                ", video_link_1='" + video_link_1 + '\'' +
                ", video_link_2='" + video_link_2 + '\'' +
                ", audio_link='" + audio_link + '\'' +
                ", medias_times='" + medias_times + '\'' +
                ", share_url='" + share_url + '\'' +
                ", reply_count='" + reply_count + '\'' +
                ", categories='" + categories + '\'' +
                ", share_logo='" + share_logo + '\'' +
                ", img_count='" + img_count + '\'' +
                ", status=" + status +
                ", status_ask=" + status_ask +
                ", status_help=" + status_help +
                ", status_live=" + status_live +
                ", help_start_date='" + help_start_date + '\'' +
                ", help_state='" + help_state + '\'' +
                ", position=" + position +
                ", start_time='" + start_time + '\'' +
                ", end_time='" + end_time + '\'' +
                ", pv_link='" + pv_link + '\'' +
                ", click_link='" + click_link + '\'' +
                ", listen='" + listen + '\'' +
                ", start_date='" + start_date + '\'' +
                '}';
    }
}
