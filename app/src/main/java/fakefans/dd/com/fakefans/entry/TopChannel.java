package fakefans.dd.com.fakefans.entry;

import java.io.Serializable;


public class TopChannel  implements Serializable, Comparable<TopChannel>, Cloneable {

    private static final long serialVersionUID = 3349158083395306384L;

    private String category_id;
    private String name;
    private String alias_name;
    private String english_name;
    private String sys_type;
    private String category_type;


    private String avatar;
    private String click_avatar;
    private String parent_id;
    private boolean visible;
    private int ordercol;
//	public static final String E_NAME_NEWS = "news";
//	public static final String E_NAME_COMMENT = "comment";
//	public static final String E_NAME_AUDIO = "listen";// english_name
//	public static final String E_NAME_HELP = "help";
//	public static final String E_NAME_ASK = "wen";// english_name
//	public static final String E_NAME_VIDEO = "look";// english_name
//	public static final String E_NAME_PAPER = "paper";// english_name
//	public static final String E_NAME_PICTURE = "image";

    public static final String CTYPE_LOCAL = "local";// category_type
    public static final String CTYPE_CLASSIFY = "classify";// category_type
    public static final String CTYPE_SUBJECT = "subject";
    public static final String CTYPE_HELP = "help";
    public static final String CTYPE_NORMAL = "normal";

    public static final String CATEGORY_ID_NEWS = "3";
    public static final String CATEGORY_ID_PING = "4";
    public static final String CATEGORY_ID_TING = "5";
    public static final String CATEGORY_ID_WEN = "18";
    public static final String CATEGORY_ID_SHI = "7";
    public static final String CATEGORY_ID_BAO = "17";
    public static final String CATEGORY_ID_TU = "6";
    public static final String CATEGORY_ID_BANG = "8";
    public static final String CATEGORY_ID_LIANGHUI = "100000";//两会
    public static final String CATEGORY_ID_ACT = "";// 我的界面 活动广告获取所需id

    public TopChannel() {
        category_id = "";
        name = "";
        alias_name = "";
        english_name = "";
        sys_type = "";
        category_type = "";
        avatar = "";
        click_avatar = "";
        parent_id = "";
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias_name() {
        return alias_name;
    }

    public void setAlias_name(String alias_name) {
        this.alias_name = alias_name;
    }

    public String getEnglish_name() {
        return english_name;
    }

    public void setEnglish_name(String english_name) {
        this.english_name = english_name;
    }

    public String getSys_type() {
        return sys_type;
    }

    public void setSys_type(String sys_type) {
        this.sys_type = sys_type;
    }

    public String getCategory_type() {
        return category_type;
    }

    public void setCategory_type(String category_type) {
        this.category_type = category_type;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getClick_avatar() {
        return click_avatar;
    }

    public void setClick_avatar(String click_avatar) {
        this.click_avatar = click_avatar;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public int getOrdercol() {
        return ordercol;
    }

    public void setOrdercol(int ordercol) {
        this.ordercol = ordercol;
    }

    @Override
    public int compareTo(TopChannel another) {
        return this.ordercol - another.ordercol;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (this == o)
            return true;

        if (o instanceof TopChannel) {
            TopChannel other = (TopChannel) o;
            return (other.category_id).equals(this.category_id);
        }

        return false;
    }

    @Override
    public int hashCode() {
        try {
            return Integer.parseInt(category_id);
        } catch (Exception e) {
        }
        return super.hashCode();
    }

    @Override
    public Object clone() {
        TopChannel o = null;
        try {
            // o=(TopChannel)super.clone();//Object 中的clone()识别出你要复制的是哪一个对象。
            o = new TopChannel();
            o.setAlias_name(this.alias_name);
            o.setAvatar(this.avatar);
            o.setCategory_id(this.category_id);
            o.setCategory_type(this.category_type);
            o.setClick_avatar(this.click_avatar);
            o.setEnglish_name(this.english_name);
            o.setName(this.name);
            o.setOrdercol(this.ordercol);
            o.setParent_id(this.parent_id);
            o.setSys_type(this.sys_type);
            o.setVisible(this.visible);
            return o;
        } catch (Exception e) {
        }
        return o;
    }

    @Override
    public String toString() {
        return "TopChannel [id=" + category_id + ", name=" + name + ", alias_name=" + alias_name + ", e_name=" + english_name + ", avatar=" + avatar + ", click_avatar="
                + click_avatar + ", sys_type=" + sys_type + ", cate_type=" + category_type + ", parent_id=" + parent_id + ", visible=" + visible + ",order=" + ordercol + " ]";
    }

}
