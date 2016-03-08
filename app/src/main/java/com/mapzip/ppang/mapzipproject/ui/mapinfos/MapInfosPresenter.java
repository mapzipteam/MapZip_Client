package com.mapzip.ppang.mapzipproject.ui.mapinfos;

import android.support.annotation.NonNull;

import com.mapzip.ppang.mapzipproject.model.LocationInfo;
import com.mapzip.ppang.mapzipproject.model.ReviewData;
import com.nhn.android.maps.maplib.NGeoPoint;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by acekim on 16. 2. 22.
 * 안드로이드 Unit테스팅에 용이한 패턴인 MVP 패턴(Model-View-Presenter)로 구현하기위한 인터페이스입니다.
 * 관련된 자료는 다음 링크를 참조하세요.
 * https://codelabs.developers.google.com/codelabs/android-testing/index.html?index=..%2F..%2Findex#0
 */
public class MapInfosPresenter implements MapInfosContract.UserActionListener {

    private MapInfosContract.View.Activity mInfosActivity;
    private MapInfosContract.View.InfosList mInfosFragment;
    private MapInfosContract.View.ReviewsList mReviewsFragment;

    public MapInfosPresenter(@NonNull MapInfosContract.View.Activity infosActivity) {
        mInfosActivity = checkNotNull(infosActivity, "InfosActivity cannot be null!!");
    }

    @Override
    public void setInfosListFragment(MapInfosContract.View.InfosList fragment) {
        mInfosFragment = checkNotNull(fragment, "InfosFragment cannot be null!!");
    }

    @Override
    public void setReviewsListFragment(MapInfosContract.View.ReviewsList fragment) {
        mReviewsFragment = checkNotNull(fragment, "ReviewsFragment cannot be null!!");
    }

    @Override
    public void setUpLocationMarkers(List<LocationInfo> locationInfos) {
        mInfosActivity.showLocationMarker(locationInfos);
    }

    @Override
    public void loadLocationInfos(boolean forceUpdate) {
        //Todo : 이곳에서 DummyData를 생성하고 View(Fragment)에 보여주는 작업을 진행해야함.

        //EspressoIdlingResource.increment(); // App is busy until further notice

        List<LocationInfo> dummyDatas = new ArrayList<>(3);
        dummyDatas.add(new LocationInfo(0, "상도곱창", "서울특별시 동작구 상도동 127-7", new NGeoPoint(37.496815, 126.953565), 6));
        dummyDatas.add(new LocationInfo(1, "현선이네", "서울특별시 동작구 상도동 128-7", new NGeoPoint(37.495183, 126.956716), 100));
        dummyDatas.add(new LocationInfo(2, "피자헤븐", "서울특별시 동작구 상도동 125-7", new NGeoPoint(37.494955, 126.958133), 1000));

        mInfosFragment.showLocationInfos(dummyDatas);
    }

    @Override
    public void openLocationDetails(@NonNull LocationInfo requestedInfo) {
        checkNotNull(requestedInfo, "requestedInfo cannot be null!");
        mInfosFragment.showLocationDetailUI(requestedInfo);
    }

    @Override
    public void loadReviewsDatas(boolean forceUpdate) {
        List<ReviewData> dummyDatas = new ArrayList<>(3);
        dummyDatas.add(new ReviewData("Ace Kim", "맛있어요!", "2016-03-27"));
        dummyDatas.add(new ReviewData("ppang", "맛없어요.\n맛없어요.\n맛없어요.\n맛없어요\n맛없어요\n맛없어요\n맛없어", "2016-04-27"));
        dummyDatas.add(new ReviewData("brain", "그저그래요.", "2016-05-27"));

        mReviewsFragment.showComments(dummyDatas);
    }

    @Override
    public void loadReviewFragment() {
        mInfosActivity.changeFragment();
    }
}
