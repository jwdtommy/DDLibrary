package com.dd.fakefans.modules.message;

import android.content.Context;
import android.view.View;

import com.dd.fakefans.R;
import com.dd.fakefans.entry.BuDeJieInfo;
import com.dd.fakefans.entry.Image;
import com.dd.fakefans.entry.MessageInfo;
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
public class MessageAdapter extends SuperAdapter<MessageInfo.PagebeanBean.ContentlistBean> {
	public MessageAdapter(Context context, List<MessageInfo.PagebeanBean.ContentlistBean> items) {
		super(context, items,  R.layout.item_type_word);
	}

	@Override
	public void onBind(SuperViewHolder holder, int viewType, int layoutPosition, MessageInfo.PagebeanBean.ContentlistBean item) {
		holder.setText(R.id.tv_content, item.getTitle());
		holder.setText(R.id.tv_createTime, item.getPubDate());
		holder.setText(R.id.tv_author, item.getSource());
		FrescoImageView imageView = holder.findViewById(R.id.iv_image);
		if(item.getImageurls()!=null&&item.getImageurls().size()>0&&item.getImageurls().get(0)!=null&&item.getImageurls().get(0).getUrl()!=null){
			imageView.setVisibility(View.VISIBLE);
			Image image = new Image();
			image.setPath(item.getImageurls().get(0).getUrl());
			ImageDisplayConfig config = ImageDisplayConfig.ImageDisplayConfigBuilder.newBuilder().setAutoResize(true).build();
			FrescoImageLoader.loadImage(imageView, image, config);
		}
		else{
			imageView.setVisibility(View.GONE);
		}
	}

}