package com.mapzip.ppang.mapzipproject.ui.mapinfos;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * Created by acekim on 16. 2. 22.
 */
public class ReviewsAdapter extends RecyclerView.Adapter {

    private final static int MY_REVIEW_TYPE = 0;
    private final static int OUTER_REVIEW_TYPE = 1;

    private Context mContext;

    public ReviewsAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
