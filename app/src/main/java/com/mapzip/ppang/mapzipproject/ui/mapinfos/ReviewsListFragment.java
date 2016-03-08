package com.mapzip.ppang.mapzipproject.ui.mapinfos;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mapzip.ppang.mapzipproject.R;
import com.mapzip.ppang.mapzipproject.model.ReviewData;
import com.mapzip.ppang.mapzipproject.ui.ext.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by acekim on 16. 2. 22.
 */
public class ReviewsListFragment extends Fragment implements MapInfosContract.View.ReviewsList {

    private ReviewsAdapter mReviewsAdapter;
    private MapInfosContract.UserActionListener mActionsListener;

    public ReviewsListFragment() {
    }

    public static ReviewsListFragment newInstance() {
        return new ReviewsListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReviewsAdapter = new ReviewsAdapter(new ArrayList<ReviewData>(0));
    }

    @Override
    public void onResume() {
        super.onResume();
        mActionsListener.loadReviewsDatas(false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
        mActionsListener = ((MapInfosActivity) getActivity()).getActionsListener();
        mActionsListener.setReviewsListFragment(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reviews_list, container, false);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.reviews_list_recyclerview);
        recyclerView.addItemDecoration(new DividerItemDecoration(ContextCompat.getDrawable(getActivity(), android.R.drawable.divider_horizontal_bright), true, false));
        recyclerView.setAdapter(mReviewsAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setAutoMeasureEnabled(true);

        recyclerView.setLayoutManager(layoutManager);

        return rootView;
    }

    @Override
    public void showComments(List<ReviewData> reviews) {
        mReviewsAdapter.replaceData(reviews);
    }

    private static class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

        private List<ReviewData> mReviews;

        public ReviewsAdapter(List<ReviewData> reviews) {
            setList(reviews);
        }

        @Override
        public ReviewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View infoView = inflater.inflate(R.layout.item_reviews_list, parent, false);

            return new ViewHolder(infoView);
        }

        @Override
        public void onBindViewHolder(ReviewsAdapter.ViewHolder holder, int position) {
            ReviewData review = mReviews.get(position);

            holder.authorText.setText(review.getAuthor());
            holder.commentText.setText(review.getComment());
            holder.dateText.setText(review.getDate());
        }

        public void replaceData(List<ReviewData> reviews) {
            setList(reviews);
            notifyDataSetChanged();
        }

        private void setList(List<ReviewData> reviews) {
            mReviews = checkNotNull(reviews);
        }

        @Override
        public int getItemCount() {
            return mReviews.size();
        }

        public ReviewData getItem(int position) {
            return mReviews.get(position);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView authorText;
            public TextView commentText;
            public TextView dateText;

            public ViewHolder(View itemView) {
                super(itemView);
                authorText = (TextView) itemView.findViewById(R.id.reviews_author);
                commentText = (TextView) itemView.findViewById(R.id.reviews_comment);
                dateText = (TextView) itemView.findViewById(R.id.reviews_date);
            }
        }
    }
}
