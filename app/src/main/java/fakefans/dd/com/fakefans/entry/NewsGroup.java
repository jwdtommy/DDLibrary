package fakefans.dd.com.fakefans.entry;

import java.io.Serializable;
import java.util.List;

public class NewsGroup implements Serializable {

	private static final long serialVersionUID = 7682728598214305132L;
	//
	// /**
	// * 旧字段
	// * */
	// private String code;
	// private String title;
	// private String topicTitle;
	//
	// private String group_name;
	// private String group_date;
	//
	// private String group_left_ico;
	// private String group_right_ico;
	// private String group_link_type;
	// private String group_id;
	// private String group_link;
	// private ArrayList<Items> items;
	/**
	 * 新旧都有字段
	 * */
	private String group_code;
	private String group_style;
	private int showNum;
	/**
	 * 新加字段
	 * 
	 * **/
	private String group_card_code;
	private String group_data_type;
	private List<GroupData> group_data;

	// 客户端字段
	private int mediaType;// 下载时需要的字段

	public static final int MEDIA_TYPE_AUDIO = 10;
	public static final int MEDIA_TYPE_VIDEO = 20;

	/**
	 * 分组数据类型（normal-普通，video-视频，audio-音频，paper-报纸，ask-问，help-帮，live-直播，
	 * interview-访谈，flashes-快讯，topic-专题，话题，government-政务，img-图
	 **/
	public static final String GROUP_TYPE_NORMAL = "normal";
	public static final String GROUP_TYPE_VIDEO = "video";
	public static final String GROUP_TYPE_AUDIO = "audio";
	public static final String GROUP_TYPE_PAPER = "paper";
	public static final String GROUP_TYPE_ASK = "ask";
	public static final String GROUP_TYPE_HELP = "help";
	public static final String GROUP_TYPE_LIVE = "live";
	public static final String GROUP_TYPE_INTERVIEW = "interview";
	public static final String GROUP_TYPE_FLASHES = "flashes";
	public static final String GROUP_TYPE_TOPIC = "topic";
	public static final String GROUP_TYPE_GOVERNMENT = "government";

	// 组类型 频道分组001,政务分组004,专题分组005
	// public static final String TYPE_CHANNEL = "001";
	// public static final String TYPE_SUBSCRIPTION = "004";
	// public static final String TYPE_SUBJECT = "005";

	public NewsGroup() {
		// code = "";
		// title = "";
		// topicTitle = "";
		// group_name = "";
		// group_date = "";
		// group_left_ico = "";
		// group_right_ico = "";
		// group_link_type = "";
		// group_link = "";

		/******/
		group_style = "";
		group_code = "";
		/*****/
		group_card_code = "";
		group_data_type = "";
	}

	public int getMediaType() {
		return mediaType;
	}

	public void setMediaType(int mediaType) {
		this.mediaType = mediaType;
	}

	public String getGroup_code() {
		return group_code;
	}

	public void setGroup_code(String group_code) {
		this.group_code = group_code;
	}

	public String getGroup_style() {
		return group_style;
	}

	public void setGroup_style(String group_style) {
		this.group_style = group_style;
	}

	public String getGroup_card_code() {
		return group_card_code;
	}

	public void setGroup_card_code(String group_card_code) {
		this.group_card_code = group_card_code;
	}
	public String getGroup_data_type() {
		return group_data_type;
	}

	public void setGroup_data_type(String group_data_type) {
		this.group_data_type = group_data_type;
	}

	public List<GroupData> getGroup_data() {
		return group_data;
	}

	public void setGroup_data(List<GroupData> group_data) {
		this.group_data = group_data;
	}

	public int getShowNum() {
		return showNum;
	}

	public void setShowNum(int showNum) {
		this.showNum = showNum;
	}

	@Override
	public String toString() {
		return "NewsGroup [group_data=" + group_data + "]";
	}

}
