package com.mapzip.ppang.mapzipproject.ui.mapinfos;

import android.support.annotation.NonNull;

import com.mapzip.ppang.mapzipproject.model.LocationInfo;
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

    private MapInfosContract.View mInfosView;

    public MapInfosPresenter(@NonNull MapInfosContract.View infosView) {
        mInfosView = checkNotNull(infosView, "infosView cannot be null!!");
    }

    @Override
    public void loadLocationInfos(boolean forceUpdate) {
        //Todo : 이곳에서 DummyData를 생성하고 View(Fragment)에 보여주는 작업을 진행해야함.

        //EspressoIdlingResource.increment(); // App is busy until further notice

        List<LocationInfo> dummyDatas = new ArrayList<>(3);
        dummyDatas.add(new LocationInfo(0, "상도곱창", "서울특별시 동작구 상도동 127-7", new NGeoPoint(37.496815, 126.953565), 6));
        dummyDatas.add(new LocationInfo(1, "현선이네", "서울특별시 동작구 상도동 128-7", new NGeoPoint(37.495183, 126.956716), 100));
        dummyDatas.add(new LocationInfo(2, "피자헤븐", "서울특별시 동작구 상도동 125-7", new NGeoPoint(37.494955, 126.958133), 1000));

        mInfosView.showLocationInfos(dummyDatas);
    }

    @Override
    public void openLocationDetails(@NonNull LocationInfo requestedInfo) {
        checkNotNull(requestedInfo, "requestedInfo cannot be null!");
        mInfosView.showLocationDetailUI(requestedInfo);
    }
}
