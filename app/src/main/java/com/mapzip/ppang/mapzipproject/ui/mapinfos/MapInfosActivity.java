package com.mapzip.ppang.mapzipproject.ui.mapinfos;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;

import com.mapzip.ppang.mapzipproject.R;
import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;

import static com.mapzip.ppang.mapzipproject.map.Location.SEOUL;

public class MapInfosActivity extends NMapActivity implements AppCompatCallback{

    private static final String TAG = "MapInfosActivity";

    private AppCompatDelegate mDelegate;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;

    private NMapView mMapView;
    private NMapController mMapController;

    private static final boolean DEBUG = false;
    private NGeoPoint current_point = SEOUL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDelegate = AppCompatDelegate.create(this, this);
        mDelegate.onCreate(savedInstanceState);
        mDelegate.setContentView(R.layout.activity_map_infos);

        mFragmentManager = getFragmentManager();

        initToolbar();
        initNaverMap();
        initInfosFragment();
    }

    private void initInfosFragment() {
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.add(R.id.map_infos_container, InfosListFragment.newInstance())
                .commit();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.map_infos_toolbar);
        mDelegate.setSupportActionBar(toolbar);
    }

    private void initNaverMap() {
        mMapView = (NMapView) findViewById(R.id.map_infos_nmap);
        mMapView.setApiKey(getString(R.string.naver_map_api_key));
        mMapView.setEnabled(true);
        mMapView.setClickable(true);
        mMapView.setFocusable(true);
        mMapView.setFocusableInTouchMode(true);
        mMapView.requestFocus();

        mMapView.setOnMapViewTouchEventListener(onMapViewTouchEventListener);
        mMapView.setOnMapStateChangeListener(onMapStateChangeListener);

        mMapController = mMapView.getMapController();
    }

    private final NMapView.OnMapStateChangeListener onMapStateChangeListener = new NMapView.OnMapStateChangeListener() {
        @Override
        public void onMapInitHandler(NMapView nMapView, NMapError nMapError) {
            if (nMapError == null) {
                mMapController.setMapCenter(current_point, 9);
                //poiDataOverlay.showAllPOIdata(0);//위에 코드와 달리 처음 지도를 불렀을때 모든 poi 플래그들리 보이도록 자동으로 축적이랑 중심을 변경 시켜주는 코드
            } else {
                if (DEBUG) {
                    Log.e("NMAP", "onMapInitHandler : error=" + nMapError.toString());
                }
            }
        }

        @Override
        public void onMapCenterChange(NMapView nMapView, NGeoPoint nGeoPoint) {

        }

        @Override
        public void onMapCenterChangeFine(NMapView nMapView) {

        }

        @Override
        public void onZoomLevelChange(NMapView nMapView, int i) {

            if (DEBUG) {
                Log.e(TAG, "zoom level : " + i);
            }
        }

        @Override
        public void onAnimationStateChange(NMapView nMapView, int i, int i1) {

        }
    };

    /*NMapView 객체에서 발생하는 티치이벤트에 대한 콜백 인터페이스*/
    private final NMapView.OnMapViewTouchEventListener onMapViewTouchEventListener = new NMapView.OnMapViewTouchEventListener() {
        @Override
        public void onLongPress(NMapView nMapView, MotionEvent motionEvent) {

        }

        @Override
        public void onLongPressCanceled(NMapView nMapView) {

        }

        @Override
        public void onTouchDown(NMapView nMapView, MotionEvent motionEvent) {

        }

        @Override
        public void onTouchUp(NMapView nMapView, MotionEvent motionEvent) {

        }

        @Override
        public void onScroll(NMapView nMapView, MotionEvent motionEvent, MotionEvent motionEvent1) {

        }

        @Override
        public void onSingleTapUp(NMapView nMapView, MotionEvent motionEvent) {

        }
    };

    @Override
    public void onSupportActionModeStarted(ActionMode mode) {

    }

    @Override
    public void onSupportActionModeFinished(ActionMode mode) {

    }

    @Nullable
    @Override
    public ActionMode onWindowStartingSupportActionMode(ActionMode.Callback callback) {
        return null;
    }
}
