package com.dd.fakefans.modules.message;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import com.dd.fakefans.Subscriber.SubscriberOnNextListener;
import com.dd.fakefans.base.BaseListFragment;
import com.dd.fakefans.entry.MessageInfo;
import com.dd.fakefans.entry.Tabs;
import com.dd.fakefans.utils.Consts;

/**
 * Created by J.Tommy on 17/2/7.
 */
public class MessageFragment extends BaseListFragment implements SubscriberOnNextListener<MessageInfo>{
	private MessagePresenter mMessagePresenter;
	private MessageAdapter mMessageAdapter;
	private int page=1;
	private Tabs mTabs;
	@Override
	public void onViewCreatedImpl(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreatedImpl(view, savedInstanceState);
		mTabs= (Tabs) getArguments().getSerializable(Consts.KEY_TAB);
		mMessagePresenter=new MessagePresenter();
		mMessagePresenter.getMessages(getActivity(),this,mTabs.type,page+"");
	}

	@Override
	public void onRefreshData() {
		page=1;
		mMessagePresenter.getMessages(getActivity(),this,mTabs.type,"");
	}

	@Override
	public void onLoadMoreData() {
		page++;
		mMessagePresenter.getMessages(getActivity(),this,mTabs.type,page+"");
	}

	@Override
	public void onNext(MessageInfo data) {
		swipeRefreshLayout.setRefreshing(false);
		if (page == 1) {
			mMessageAdapter = new MessageAdapter(getActivity(), data.getPagebean().getContentlist());
			mMessageAdapter.setOnlyOnce(false);
			recyclerView.setAdapter(mMessageAdapter);
		} else {
			mMessageAdapter.addAll(data.getPagebean().getContentlist());
		}
	}
}
