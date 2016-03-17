package com.mapzip.ppang.mapzipproject.wannabemap;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;

import com.mapzip.ppang.mapzipproject.R;
import com.mapzip.ppang.mapzipproject.map.NMapPOIflagType;
import com.mapzip.ppang.mapzipproject.map.NMapViewerResourceProvider;
import com.mapzip.ppang.mapzipproject.model.LocationInfo;
import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;

import java.util.List;

public class MapInfosActivity extends NMapActivity implements AppCompatCallback, MapInfosContract.View.Activity {

    private static final String TAG = "MapInfosActivity";

    private AppCompatDelegate mDelegate;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;

    private NMapView mMapView;
    private NMapController mMapController;
    private NMapViewerResourceProvider mMapViewerResourceProvider;
    private NMapOverlayManager mMapOverlayManager;
    private NMapPOIdataOverlay mPoiDataOverlay;

    private MapInfosContract.UserActionListener mActionsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDelegate = AppCompatDelegate.create(this, this);
        mDelegate.onCreate(savedInstanceState);
        mDelegate.setContentView(R.layout.activity_map_infos);

        mActionsListener = new MapInfosPresenter(this);

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
        mMapView.setClientId(getString(R.string.naver_map_client_key));
        mMapView.setEnabled(true);
        mMapView.setClickable(true);
        mMapView.setFocusable(true);
        mMapView.setFocusableInTouchMode(true);
        mMapView.requestFocus();
        mMapView.setBuiltInZoomControls(true, null);
        mMapView.setOnMapViewTouchEventListener(onMapViewTouchEventListener);
        mMapView.setOnMapStateChangeListener(onMapStateChangeListener);

        mMapController = mMapView.getMapController();
        mMapViewerResourceProvider = new NMapViewerResourceProvider(this);
        mMapOverlayManager = new NMapOverlayManager(this, mMapView, mMapViewerResourceProvider);
    }

    /**
     * @param objects : 마커를 생성할 장소들에대한 데이터들
     * @see InfosListFragment
     * 장소들에 대한 데이터를 리스트형태로 가져와서 오버레이 아이템(마커)을 생성하고 출력합니다.
     * 이 함수는 InfosListFragment 에서 호출됩니다.
     * @since 2016. 3. 3
     */
    @Override
    public void showLocationMarker(List<Object> objects) {
        NMapPOIdata poiData = new NMapPOIdata(objects.size(), mMapViewerResourceProvider);

        poiData.beginPOIdata(objects.size());
        for (Object object : objects) {
            LocationInfo locationInfo = (LocationInfo) object;
            poiData.addPOIitem(locationInfo.getLocationLatLng().getLatitude(), locationInfo.getLocationLatLng().getLongitude(), locationInfo.getLocationName(), NMapPOIflagType.PIN, locationInfo.getLocationID());
        }
        poiData.endPOIdata();

        mPoiDataOverlay = mMapOverlayManager.createPOIdataOverlay(poiData, null);
        mPoiDataOverlay.showAllPOIdata(0);
    }

    @Override
    public void showDetailReview() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(R.layout.layout_detailreview);
        dialog.show();
    }

    public MapInfosContract.UserActionListener getActionsListener() {
        return mActionsListener;
    }

    private final NMapView.OnMapStateChangeListener onMapStateChangeListener = new NMapView.OnMapStateChangeListener() {
        @Override
        public void onMapInitHandler(NMapView nMapView, NMapError nMapError) {
        }

        @Override
        public void onMapCenterChange(NMapView nMapView, NGeoPoint nGeoPoint) {
        }

        @Override
        public void onMapCenterChangeFine(NMapView nMapView) {
        }

        @Override
        public void onZoomLevelChange(NMapView nMapView, int i) {
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
