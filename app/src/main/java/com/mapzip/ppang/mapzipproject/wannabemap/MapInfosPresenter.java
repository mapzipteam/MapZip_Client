package com.mapzip.ppang.mapzipproject.wannabemap;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.mapzip.ppang.mapzipproject.model.ReviewData;

import java.util.ArrayList;
import java.util.HashMap;
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
    private List<ReviewData> mDummyDatas;
    private HashMap<Marker, Integer> mLocationHashMap = new HashMap<>();
    private Object markerFromLocationID;

    public MapInfosPresenter(@NonNull MapInfosContract.View.Activity infosActivity) {
        mInfosActivity = checkNotNull(infosActivity, "InfosActivity cannot be null!!");
    }

    @Override
    public void setInfosListFragment(MapInfosContract.View.InfosList fragment) {
        mInfosFragment = checkNotNull(fragment, "InfosFragment cannot be null!!");
    }

    @Override
    public void setUpLocationMarkers() {
        if (mDummyDatas != null) {
            mInfosActivity.showLocationMarker(mDummyDatas);
        }
    }

    @Override
    public void loadLocationInfos(boolean forceUpdate) {
        //EspressoIdlingResource.increment(); // App is busy until further notice

        mDummyDatas = new ArrayList<>();
        mDummyDatas.add(new ReviewData(0, "상도곱창", "서울특별시 동작구 상도동 127-7", new LatLng(37.496815, 126.953565), "#음식점 #좋은장소", "아주 좋아요", "분위기가 별로에요", "다시 가고 싶진 않을듯", "010-2343-2323"));
        mDummyDatas.add(new ReviewData(1, "현선이네", "서울특별시 동작구 상도동 128-7", new LatLng(37.495183, 126.956716), "#음식점 #좋은장소", "아주 좋아요", "분위기가 별로에요", "다시 가고 싶진 않을듯", "010-2343-2323"));
        mDummyDatas.add(new ReviewData(2, "피자헤븐", "서울특별시 동작구 상도동 125-7", new LatLng(37.494955, 126.958133), "#음식점 #좋은장소", "아주 좋아요", "분위기가 별로에요", "다시 가고 싶진 않을듯", "010-2343-2323"));
        mDummyDatas.add(new ReviewData(3, "숭실대학교", "서울특별시 동작구 상도동 127-7", new LatLng(37.4962518, 126.9573151), "#학교 #고통받는장소", "우리학교에요!", "졸업하고싶어요..", "다시 가고 싶진 않을듯", "010-2343-2323"));
        mDummyDatas.add(new ReviewData(4, "노량진 수산시장", "서울특별시 동작구 상도동 128-7", new LatLng(37.5153631, 126.9408988), "#음식점 #", "횟값이 싸요", "냄새가 비려요", "다시 가고 싶진 않을듯", "010-2343-2323"));
        mDummyDatas.add(new ReviewData(5, "공단기고시학원", "서울특별시 동작구 상도동 125-7", new LatLng(37.5122912, 126.9509761), "#낭만 #추억", "여기서 인생 대박낼꺼에요", "5년째 여기서 살고있어", "다시 가고 싶진 않을듯", "010-2343-2323"));

        mInfosFragment.showLocationInfos(mDummyDatas);
    }

    @Override
    public void openUserReview(int locationID) {
        Marker selectedMarker = getMarkerFromLocationID(locationID);
        mInfosActivity.showDetailReview(selectedMarker, mDummyDatas.get(locationID));
    }

    @Override
    public void putMarkerToMap(Marker marker, int locationID) {
        mLocationHashMap.put(marker, locationID);
    }

    @Override
    public int getMarkerFromMap(Marker marker) {
        return mLocationHashMap.get(marker);
    }

    public Marker getMarkerFromLocationID(int locationID) {
        for (Marker marker : mLocationHashMap.keySet()) {
            if (mLocationHashMap.get(marker).equals(locationID)) {
                return marker;
            }
        }

        return null;
    }
}
