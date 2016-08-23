package fakefans.dd.com.fakefans.business.News;

import android.content.Context;

import org.byteam.superadapter.IMulItemViewType;
import org.byteam.superadapter.SuperAdapter;
import org.byteam.superadapter.internal.SuperViewHolder;

import java.util.List;

import fakefans.dd.com.fakefans.R;
import fakefans.dd.com.fakefans.entry.NewsData;

/**
 * Created by adong on 16/8/22.
 */
public class NewsAdapter extends SuperAdapter<NewsData.PagebeanBean.ContentlistBean> {
    private final String TYPE_PIC = "10";
    private final String TYPE_WORD = "29";
    private final String TYPE_AUDIO = "31";
    private final String TYPE_VIDEO = "41";
    private final int TYPE_PIC_INT = 0;
    private final int TYPE_WORD_INT = 1;
    private final int TYPE_AUDIO_INT = 2;
    private final int TYPE_VIDEO_INT = 3;

    public NewsAdapter(Context context, List<NewsData.PagebeanBean.ContentlistBean> items) {
        super(context, items, null);
    }

    private NewsAdapter(Context context, List<NewsData.PagebeanBean.ContentlistBean> items, int layoutResId) {
        super(context, items, layoutResId);
    }

    private NewsAdapter(Context context, List<NewsData.PagebeanBean.ContentlistBean> items, IMulItemViewType<NewsData.PagebeanBean.ContentlistBean> mulItemViewType) {
        super(context, items, mulItemViewType);
    }

    @Override
    public void onBind(SuperViewHolder holder, int viewType, int layoutPosition, NewsData.PagebeanBean.ContentlistBean item) {
        switch (getItemViewType(layoutPosition)) {
            case TYPE_WORD_INT:
                holder.setText(R.id.tv_name,item.getName());
                break;
        }
    }

    @Override
    protected IMulItemViewType<NewsData.PagebeanBean.ContentlistBean> offerMultiItemViewType() {
        return new IMulItemViewType<NewsData.PagebeanBean.ContentlistBean>() {
            @Override
            public int getViewTypeCount() {
                return 4;
            }

            @Override
            public int getItemViewType(int position, NewsData.PagebeanBean.ContentlistBean mockModel) {
                switch (getData().get(position).getType()) {
//                    case TYPE_PIC:
//                        return TYPE_PIC_INT;
//                    case TYPE_WORD:
//                        return TYPE_WORD_INT;
//                    case TYPE_AUDIO:
//                        return TYPE_AUDIO_INT;
//                    case TYPE_VIDEO:
//                        return TYPE_VIDEO_INT;
                }
                return TYPE_WORD_INT;
            }

            @Override
            public int getLayoutId(int viewType) {
                if (viewType == TYPE_WORD_INT) {
                    return R.layout.item_type_word;
                }
                return TYPE_WORD_INT;
            }
        };
    }

}