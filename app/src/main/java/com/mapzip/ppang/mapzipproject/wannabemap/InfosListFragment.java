package com.mapzip.ppang.mapzipproject.wannabemap;

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
import com.mapzip.ppang.mapzipproject.model.ReviewData;
import com.mapzip.ppang.mapzipproject.ui.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by acekim on 16. 2. 22.
 */
public class InfosListFragment extends Fragment implements MapInfosContract.View.InfosList {

    private RecyclerView mRecyclerView;
    private InfosAdapter mInfosAdapter;
    private MapInfosContract.UserActionListener mActionsListener;

    InfoItemListener mItemListener = new InfoItemListener() {
        @Override
        public void onInfoClick(View view) {
            mActionsListener.openUserReview();
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
        mInfosAdapter = new InfosAdapter(new ArrayList<Object>(0), mItemListener);
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
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.infos_list_recyclerview);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(ContextCompat.getDrawable(getActivity(), android.R.drawable.divider_horizontal_bright), true, false));

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mInfosAdapter);

        return rootView;
    }

    @Override
    public void showLocationInfos(List<Object> infos) {
        mInfosAdapter.replaceData(infos);
        mActionsListener.setUpLocationMarkers(infos);
    }

    private class InfosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int ITEM_LOCATION_INFORMATION = 0;

        private List<Object> mInfos;
        private InfoItemListener mItemListener;

        public InfosAdapter(List<Object> infos, InfoItemListener itemListener) {
            setList(infos);
            mItemListener = itemListener;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            switch (viewType) {
                case ITEM_LOCATION_INFORMATION:
                    View infoView = inflater.inflate(R.layout.item_locationinfo, parent, false);
                    return new LocationInfoViewHolder(infoView, mItemListener);
                default:
                    try {
                        throw new Exception("Cannot find right item view");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }

            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Object object = mInfos.get(position);
            switch (holder.getItemViewType()) {
                case ITEM_LOCATION_INFORMATION:

                    ReviewData info = (ReviewData) object;

                    LocationInfoViewHolder locationInfoViewHolder = (LocationInfoViewHolder) holder;
                    locationInfoViewHolder.nameText.setText(info.getLocationName());
                    locationInfoViewHolder.addressText.setText(info.getLocationAddress());

                    break;
            }
        }

        public void replaceData(List<Object> infos) {
            setList(infos);
            notifyDataSetChanged();
        }

        private void setList(List<Object> infos) {
            mInfos = checkNotNull(infos);
        }

        @Override
        public int getItemViewType(int position) {
            if (mInfos.get(position).getClass().equals(ReviewData.class)) {
                return ITEM_LOCATION_INFORMATION;
            }
            return -1;
        }

        @Override
        public int getItemCount() {
            return mInfos.size();
        }

        public Object getItem(int position) {
            return mInfos.get(position);
        }

        public class LocationInfoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public TextView nameText;
            public TextView addressText;

            private InfoItemListener mItemListener;

            public LocationInfoViewHolder(View itemView, InfoItemListener listener) {
                super(itemView);
                mItemListener = listener;
                nameText = (TextView) itemView.findViewById(R.id.item_locationinfo_name);
                addressText = (TextView) itemView.findViewById(R.id.item_locationinfo_address);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                mItemListener.onInfoClick(view);
            }
        }
    }

    public interface InfoItemListener {
        void onInfoClick(View view);
    }
}
