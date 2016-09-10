package com.dd.fakefans.business.News;

import android.content.Context;

import org.byteam.superadapter.IMulItemViewType;
import org.byteam.superadapter.SuperAdapter;
import org.byteam.superadapter.internal.SuperViewHolder;
import java.util.List;
import com.dd.fakefans.R;
import com.dd.fakefans.entry.BuDeJieInfo;
import com.dd.fakefans.entry.Image;
import com.dd.fakefans.fresco.FrescoImageLoader;
import com.dd.fakefans.fresco.FrescoImageView;
import com.dd.fakefans.fresco.ImageDisplayConfig;

/**
 * Created by adong on 16/8/22.
 */
public class NewsAdapter extends SuperAdapter<BuDeJieInfo.PagebeanBean.ContentlistBean> {
    private final int TYPE_PIC_INT = 0;
    private final int TYPE_WORD_INT = 1;
    private final int TYPE_AUDIO_INT = 2;
    private final int TYPE_VIDEO_INT = 3;

    public NewsAdapter(Context context, List<BuDeJieInfo.PagebeanBean.ContentlistBean> items) {
        super(context, items, null);
    }

    private NewsAdapter(Context context, List<BuDeJieInfo.PagebeanBean.ContentlistBean> items, int layoutResId) {
        super(context, items, layoutResId);
    }

    private NewsAdapter(Context context, List<BuDeJieInfo.PagebeanBean.ContentlistBean> items, IMulItemViewType<BuDeJieInfo.PagebeanBean.ContentlistBean> mulItemViewType) {
        super(context, items, mulItemViewType);
    }

    @Override
    public void onBind(SuperViewHolder holder, int viewType, int layoutPosition, BuDeJieInfo.PagebeanBean.ContentlistBean item) {
        switch (getItemViewType(layoutPosition)) {
            case TYPE_WORD_INT:
                holder.setText(R.id.tv_name, item.getText(
                ));
                FrescoImageView imageView = holder.findViewById(R.id.iv_image);
                Image image = new Image();
                image.setPath(item.getImage0());
                ImageDisplayConfig config= ImageDisplayConfig.ImageDisplayConfigBuilder.newBuilder().setAutoResize(true).build();
                FrescoImageLoader.loadImage(imageView,image,config);
                break;
        }
    }

    @Override
    protected IMulItemViewType<BuDeJieInfo.PagebeanBean.ContentlistBean> offerMultiItemViewType() {
        return new IMulItemViewType<BuDeJieInfo.PagebeanBean.ContentlistBean>() {
            @Override
            public int getViewTypeCount() {
                return 4;
            }

            @Override
            public int getItemViewType(int position, BuDeJieInfo.PagebeanBean.ContentlistBean mockModel) {
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