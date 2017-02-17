package com.dd.news.modules.message;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import com.dd.news.R;
import com.dd.news.entry.MessageInfo;
import com.dd.framework.base.BaseFragment;
import com.dd.framework.base.NavigateActivity;
import com.dd.framework.base.WebFragment;
import com.dd.framework.image.FrescoImageLoader;
import com.dd.framework.image.FrescoImageView;
import com.dd.framework.image.Image;
import com.dd.framework.image.ImageDisplayConfig;

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
	public void onBind(SuperViewHolder holder, int viewType, int layoutPosition, final MessageInfo.PagebeanBean.ContentlistBean item) {
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
		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle bundle=new Bundle();
				bundle.putString("url",item.getLink());
				WebFragment webFragment= BaseFragment.newFragment((Activity) getContext(),WebFragment.class,bundle);
				((NavigateActivity)getContext()).addSubFragment(webFragment);
			}
		});
	}
}