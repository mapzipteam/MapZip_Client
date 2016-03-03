package com.mapzip.ppang.mapzipproject.ui.mapinfos;

import android.app.Fragment;

import com.mapzip.ppang.mapzipproject.model.LocationInfo;

import java.util.List;

/**
 * Created by acekim on 16. 2. 22.
 * 안드로이드 Unit테스팅에 용이한 패턴인 MVP 패턴(Model-View-Presenter)로 구현하기위한 인터페이스입니다.
 * 관련된 자료는 다음 링크를 참조하세요.
 * https://codelabs.developers.google.com/codelabs/android-testing/index.html?index=..%2F..%2Findex#0
 */
public interface MapInfosContract {

    interface View {
        interface Activity {
            void showLocationMarker(List<LocationInfo> locationInfos);
        }

        interface Fragment {
            void showLocationInfos(List<LocationInfo> locationInfos);

            void showLocationDetailUI(LocationInfo locationInfo);
        }
    }

    interface UserActionListener {
        void setFragment(MapInfosContract.View.Fragment fragment);

        void setUpLocationMarkers(List<LocationInfo> locationInfos);

        void loadLocationInfos(boolean forceUpdate);

        void openLocationDetails(LocationInfo clickedInfo);
    }
}
