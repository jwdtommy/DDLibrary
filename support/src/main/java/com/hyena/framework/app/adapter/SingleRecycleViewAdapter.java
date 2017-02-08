package com.hyena.framework.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

import java.util.List;

/**
 * Created by yangzc on 16/10/10.
 */
public abstract class SingleRecycleViewAdapter<T> extends RecyclerView.Adapter<SingleRecycleViewAdapter.HashViewHolder> {

    protected Context mContext;
    private List<T> mItems;

    public SingleRecycleViewAdapter(Context context) {
        this.mContext = context;
    }

//    @Override
//    public HashViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
//    }

//    @Override
//    public void onBindViewHolder(HashViewHolder hashViewHolder, int position) {
//
//    }

    @Override
    public int getItemCount() {
        if(mItems == null)
            return 0;
        return mItems.size();
    }

    public T getItem(int position) {
        if(mItems == null)
            return null;
        if(position < mItems.size())
            return mItems.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setItems(List<T> items){
        this.mItems = items;
        notifyDataSetChanged();
    }

    public void addItems(List<T> items) {
        if(mItems != null) {
            this.mItems.addAll(items);
            notifyDataSetChanged();
        }
    }

    public List<T> getItems(){
        return mItems;
    }

    public void removeItem(T t){
        int index = mItems.indexOf(t);
        if(index >= 0) {
            mItems.remove(t);
            notifyItemRemoved(index);
        }
    }

    public void addItem(T t){
        if(!mItems.contains(t)) {
            mItems.add(t);
            notifyItemInserted(getItemCount() - 1);
        }
    }

    public static class HashViewHolder extends RecyclerView.ViewHolder {

        private SparseArray<View> mViewCache = new SparseArray<View>();

        public HashViewHolder(View itemView) {
            super(itemView);
        }

        public void putView(int id) {
            if (itemView != null) {
                mViewCache.put(id, itemView.findViewById(id));
            }
        }

        public View findView(Integer id) {
            return mViewCache.get(id);
        }
    }
}
