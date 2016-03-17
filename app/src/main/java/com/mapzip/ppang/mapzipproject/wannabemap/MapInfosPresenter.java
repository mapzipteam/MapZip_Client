package com.mapzip.ppang.mapzipproject.wannabemap;

import android.support.annotation.NonNull;
import android.view.View;

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
    private List dummyDatas;

    public MapInfosPresenter(@NonNull MapInfosContract.View.Activity infosActivity) {
        mInfosActivity = checkNotNull(infosActivity, "InfosActivity cannot be null!!");
    }

    @Override
    public void setInfosListFragment(MapInfosContract.View.InfosList fragment) {
        mInfosFragment = checkNotNull(fragment, "InfosFragment cannot be null!!");
    }

    @Override
    public void setUpLocationMarkers(List<Object> locationInfos) {

        mInfosActivity.showLocationMarker(locationInfos);
    }

    @Override
    public void loadLocationInfos(boolean forceUpdate) {
        //EspressoIdlingResource.increment(); // App is busy until further notice

        dummyDatas = new ArrayList<>();
        dummyDatas.add(new LocationInfo(0, "상도곱창", "서울특별시 동작구 상도동 127-7", new NGeoPoint(37.496815, 126.953565), "#음식점 #좋은장소", "아주 좋아요", "분위기가 별로에요", "다시 가고 싶진 않을듯", "010-2343-2323"));
        dummyDatas.add(new LocationInfo(1, "현선이네", "서울특별시 동작구 상도동 128-7", new NGeoPoint(37.495183, 126.956716), "#음식점 #좋은장소", "아주 좋아요", "분위기가 별로에요", "다시 가고 싶진 않을듯", "010-2343-2323"));
        dummyDatas.add(new LocationInfo(2, "피자헤븐", "서울특별시 동작구 상도동 125-7", new NGeoPoint(37.494955, 126.958133), "#음식점 #좋은장소", "아주 좋아요", "분위기가 별로에요", "다시 가고 싶진 않을듯", "010-2343-2323"));
        dummyDatas.add(new LocationInfo(3, "상도곱창", "서울특별시 동작구 상도동 127-7", new NGeoPoint(37.496815, 126.953565), "#음식점 #좋은장소", "아주 좋아요", "분위기가 별로에요", "다시 가고 싶진 않을듯", "010-2343-2323"));
        dummyDatas.add(new LocationInfo(4, "현선이네", "서울특별시 동작구 상도동 128-7", new NGeoPoint(37.495183, 126.956716), "#음식점 #좋은장소", "아주 좋아요", "분위기가 별로에요", "다시 가고 싶진 않을듯", "010-2343-2323"));
        dummyDatas.add(new LocationInfo(5, "피자헤븐", "서울특별시 동작구 상도동 125-7", new NGeoPoint(37.494955, 126.958133), "#음식점 #좋은장소", "아주 좋아요", "분위기가 별로에요", "다시 가고 싶진 않을듯", "010-2343-2323"));
        dummyDatas.add(new LocationInfo(6, "상도곱창", "서울특별시 동작구 상도동 127-7", new NGeoPoint(37.496815, 126.953565), "#음식점 #좋은장소", "아주 좋아요", "분위기가 별로에요", "다시 가고 싶진 않을듯", "010-2343-2323"));
        dummyDatas.add(new LocationInfo(7, "현선이네", "서울특별시 동작구 상도동 128-7", new NGeoPoint(37.495183, 126.956716), "#음식점 #좋은장소", "아주 좋아요", "분위기가 별로에요", "다시 가고 싶진 않을듯", "010-2343-2323"));
        dummyDatas.add(new LocationInfo(8, "피자헤븐", "서울특별시 동작구 상도동 125-7", new NGeoPoint(37.494955, 126.958133), "#음식점 #좋은장소", "아주 좋아요", "분위기가 별로에요", "다시 가고 싶진 않을듯", "010-2343-2323"));

        mInfosFragment.showLocationInfos(dummyDatas);
    }

    @Override
    public void openUserReview() {
        LocationInfo data = (LocationInfo) dummyDatas.get(0);
        mInfosActivity.showDetailReview(data);
    }
}
