package com.mapzip.ppang.mapzipproject.ui.mapinfos;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * Created by acekim on 16. 2. 22.
 */
public class InfosAdapter extends RecyclerView.Adapter {
    private final static int LOCATION_ITEM_TYPE = 0;

    private Context mContext;

    public InfosAdapter(Context mContext) {
        this.mContext = mContext;
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
