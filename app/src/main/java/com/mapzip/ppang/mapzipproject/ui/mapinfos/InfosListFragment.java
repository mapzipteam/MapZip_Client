package com.mapzip.ppang.mapzipproject.ui.mapinfos;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mapzip.ppang.mapzipproject.R;

/**
 * Created by acekim on 16. 2. 22.
 */
public class InfosListFragment extends Fragment {

    private RecyclerView mRecyclerView;

    public InfosListFragment() {
    }

    public static InfosListFragment newInstance() {
        return new InfosListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_infos_list, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.infos_list_recyclerview);
        mRecyclerView.setAdapter(new InfosAdapter(getActivity()));

        return rootView;
    }

    private static class InfosAdapter extends RecyclerView.Adapter {
        private final static int LOCATION_ITEM_TYPE = 0;

        public InfosAdapter(Context mContext) {
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

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView nameText;
        public TextView addressText;
        public TextView reviewCountsText;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
