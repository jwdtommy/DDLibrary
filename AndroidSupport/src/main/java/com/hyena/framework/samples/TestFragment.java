package com.hyena.framework.samples;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hyena.framework.annotation.AttachViewId;
import com.hyena.framework.app.adapter.SingleRecycleViewAdapter;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.utils.ToastUtils;

/**
 * Created by yangzc on 16/7/25.
 */
public class TestFragment extends BaseUIFragment {

    @AttachViewId(R.id.test_listview)
    private RecyclerView mRecycleView;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        getTitleBar().setTitle("列表测试");
        View view = View.inflate(getActivity(), R.layout.layout_test, null);
        return view;
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        mRecycleView.setAdapter(new ListAdapter(getActivity()));
        mRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    protected void onContentVisibleSizeChange(int height, int rawHeight) {
        super.onContentVisibleSizeChange(height, rawHeight);
    }

    class ListAdapter extends SingleRecycleViewAdapter<String> {

        public ListAdapter(Context context) {
            super(context);
        }

        @Override
        public void onBindViewHolder(HashViewHolder hashViewHolder, final int position) {
            ((TextView)hashViewHolder.itemView).setText("position: " + position);
            hashViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ToastUtils.showShortToast(getActivity(), "position: " + position);
                }
            });
        }

        @Override
        public HashViewHolder onCreateViewHolder(ViewGroup viewGroup, int itemType) {
            TextView textView = new TextView(getActivity());
            textView.setTextColor(Color.BLACK);
            HashViewHolder holder = new HashViewHolder(textView);
            return holder;
        }

        @Override
        public int getItemCount() {
            return 100;
        }
    }
}
