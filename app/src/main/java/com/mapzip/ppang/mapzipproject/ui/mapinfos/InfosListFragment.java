package com.mapzip.ppang.mapzipproject.ui.mapinfos;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        View rootView = inflater.inflate(R.layout.fragment_reviews_list, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.reviews_list_recyclerview);
        mRecyclerView.setAdapter(new ReviewsAdapter(getActivity()));

        return rootView;
    }
}
