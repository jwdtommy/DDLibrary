package com.dd.news.modules.message;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import com.dd.news.entry.MessageInfo;
import com.dd.news.entry.Tabs;
import com.dd.news.services.net.DataFetcher;
import com.dd.news.utils.Consts;
import com.dd.framework.base.BaseListFragment;
/**
 * Created by J.Tommy on 17/2/7.
 */
public class MessageFragment extends BaseListFragment {
	private MessageAdapter mMessageAdapter;
	private int page = 1;
	private Tabs mTabs;
	int ACTION_GET_MESSAGE = 0;

	@Override
	public void onViewCreatedImpl(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreatedImpl(view, savedInstanceState);
		mTabs = (Tabs) getArguments().getSerializable(Consts.KEY_TAB);
		loadData(ACTION_GET_MESSAGE,true);
	}

	@Override
	public void onProcessImpl(int action) {
		if (action == ACTION_GET_MESSAGE) {
			new DataFetcher(this, new CustomSubscriberAdapter<MessageInfo>() {
				@Override
				public void onNext(MessageInfo baseResult) {
					mSwipeRefreshLayout.setRefreshing(false);
					if (page == 1) {
						mMessageAdapter = new MessageAdapter(getActivity(), baseResult.getPagebean().getContentlist());
						mMessageAdapter.setOnlyOnce(false);
						mRecyclerView.setAdapter(mMessageAdapter);
					} else {
						mMessageAdapter.addAll(baseResult.getPagebean().getContentlist());
					}
				}
			}).getMessages(mTabs.type,page+"");
		}
	}

	@Override
	public void onRefreshData() {
		page = 1;
		loadData(ACTION_GET_MESSAGE,false);
	}

	@Override
	public void onLoadMoreData() {
		page++;
		loadData(ACTION_GET_MESSAGE,false);
	}
}
