package com.mapzip.ppang.mapzipproject.wannabemap;


import com.mapzip.ppang.mapzipproject.model.ReviewData;

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
            void showLocationMarker(List<ReviewData> datas);

            void showDetailReview(ReviewData data);
        }

        interface InfosList {
            void showLocationInfos(List<ReviewData> datas);
        }
    }

    interface UserActionListener {
        void setInfosListFragment(MapInfosContract.View.InfosList fragment);

        void setUpLocationMarkers(List<ReviewData> datas);

        void loadLocationInfos(boolean forceUpdate);

        void openUserReview();
    }
}
