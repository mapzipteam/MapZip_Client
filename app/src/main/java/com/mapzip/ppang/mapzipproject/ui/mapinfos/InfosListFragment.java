package com.mapzip.ppang.mapzipproject.ui.mapinfos;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mapzip.ppang.mapzipproject.R;
import com.mapzip.ppang.mapzipproject.model.LocationInfo;
import com.mapzip.ppang.mapzipproject.ui.ext.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by acekim on 16. 2. 22.
 */
public class InfosListFragment extends Fragment implements MapInfosContract.View.InfosList {

    private InfosAdapter mInfosAdapter;
    private MapInfosContract.UserActionListener mActionsListener;

    InfoItemListener mItemListener = new InfoItemListener() {
        @Override
        public void onInfoClick(LocationInfo clickedInfo) {
            mActionsListener.openLocationDetails(clickedInfo);
        }
    };

    public InfosListFragment() {
        // Requires empty public constructor
    }

    public static InfosListFragment newInstance() {
        return new InfosListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInfosAdapter = new InfosAdapter(new ArrayList<LocationInfo>(0), mItemListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        mActionsListener.loadLocationInfos(false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
        mActionsListener = ((MapInfosActivity) getActivity()).getActionsListener();
        mActionsListener.setInfosListFragment(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_infos_list, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.infos_list_recyclerview);
        recyclerView.addItemDecoration(new DividerItemDecoration(ContextCompat.getDrawable(getActivity(), android.R.drawable.divider_horizontal_bright)));
        recyclerView.setAdapter(mInfosAdapter);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return rootView;
    }

    @Override
    public void showLocationInfos(List<LocationInfo> infos) {
        mInfosAdapter.replaceData(infos);
        mActionsListener.setUpLocationMarkers(infos);
    }

    @Override
    public void showLocationDetailUI(LocationInfo locationInfo) {
        mActionsListener.loadReviewFragment();
    }

    private static class InfosAdapter extends RecyclerView.Adapter<InfosAdapter.ViewHolder> {

        private List<LocationInfo> mInfos;
        private InfoItemListener mItemListener;

        public InfosAdapter(List<LocationInfo> infos, InfoItemListener itemListener) {
            setList(infos);
            mItemListener = itemListener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View infoView = inflater.inflate(R.layout.item_infos_list, parent, false);

            return new ViewHolder(infoView, mItemListener);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            LocationInfo info = mInfos.get(position);

            holder.nameText.setText(info.getLocationName());
            holder.addressText.setText(info.getLocationAddress());
            holder.reviewCountsText.setText(String.format(Locale.KOREAN, "%d개의 리뷰 >", info.getReviewCount()));
        }

        public void replaceData(List<LocationInfo> infos) {
            setList(infos);
            notifyDataSetChanged();
        }

        private void setList(List<LocationInfo> infos) {
            mInfos = checkNotNull(infos);
        }

        @Override
        public int getItemCount() {
            return mInfos.size();
        }

        public LocationInfo getItem(int position) {
            return mInfos.get(position);
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public TextView nameText;
            public TextView addressText;
            public TextView reviewCountsText;

            private InfoItemListener mItemListener;

            public ViewHolder(View itemView, InfoItemListener listener) {
                super(itemView);
                mItemListener = listener;
                nameText = (TextView) itemView.findViewById(R.id.infos_list_name);
                addressText = (TextView) itemView.findViewById(R.id.infos_list_address);
                reviewCountsText = (TextView) itemView.findViewById(R.id.infos_list_review_count);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                LocationInfo info = getItem(position);
                mItemListener.onInfoClick(info);
            }
        }
    }

    public interface InfoItemListener {
        void onInfoClick(LocationInfo clickedInfo);
    }
}
