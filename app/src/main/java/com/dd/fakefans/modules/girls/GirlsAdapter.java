package com.dd.fakefans.modules.girls;

import android.content.Context;

import com.dd.fakefans.R;
import com.dd.fakefans.entry.Image;
import com.dd.fakefans.entry.MeituInfo;
import com.dd.fakefans.fresco.FrescoImageLoader;
import com.dd.fakefans.fresco.FrescoImageView;
import com.dd.fakefans.fresco.ImageDisplayConfig;

import org.byteam.superadapter.IMulItemViewType;
import org.byteam.superadapter.SuperAdapter;
import org.byteam.superadapter.internal.SuperViewHolder;

import java.util.List;

/**
 * Created by adong on 16/8/22.
 */
public class GirlsAdapter extends SuperAdapter<MeituInfo.PagebeanBean.ContentlistBean> {
    private final int TYPE_PIC_INT = 0;
    private final int TYPE_WORD_INT = 1;
    private final int TYPE_AUDIO_INT = 2;
    private final int TYPE_VIDEO_INT = 3;

    public GirlsAdapter(Context context, List<MeituInfo.PagebeanBean.ContentlistBean> items) {
        super(context, items, null);
    }

    private GirlsAdapter(Context context, List<MeituInfo.PagebeanBean.ContentlistBean> items, int layoutResId) {
        super(context, items, layoutResId);
    }

    private GirlsAdapter(Context context, List<MeituInfo.PagebeanBean.ContentlistBean> items, IMulItemViewType<MeituInfo.PagebeanBean.ContentlistBean> mulItemViewType) {
        super(context, items, mulItemViewType);
    }


    @Override
    protected IMulItemViewType<MeituInfo.PagebeanBean.ContentlistBean> offerMultiItemViewType() {
        return new IMulItemViewType<MeituInfo.PagebeanBean.ContentlistBean>() {
            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public int getItemViewType(int position, MeituInfo.PagebeanBean.ContentlistBean mockModel) {
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

    @Override
    public void onBind(SuperViewHolder holder, int viewType, int layoutPosition, MeituInfo.PagebeanBean.ContentlistBean item) {
        holder.setText(R.id.tv_author, item.getTitle());
        FrescoImageView imageView = holder.findViewById(R.id.iv_image);
        Image image = new Image();
        image.setPath(item.getList().get(0).getBig());
        ImageDisplayConfig config = ImageDisplayConfig.ImageDisplayConfigBuilder.newBuilder().setAutoResize(true).build();
        FrescoImageLoader.loadImage(imageView, image, config);
    }
}