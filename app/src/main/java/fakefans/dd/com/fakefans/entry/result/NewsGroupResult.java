package fakefans.dd.com.fakefans.entry.result;

import java.util.List;

import fakefans.dd.com.fakefans.entry.NewsGroup;

public class NewsGroupResult {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8317631778076545732L;
	private List<NewsGroup> data;

	public List<NewsGroup> getData() {
		return data;
	}

	public void setData(List<NewsGroup> data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "NewsGroupResult [data=" + data + "]";
	}

}
