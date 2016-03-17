package com.mapzip.ppang.mapzipproject.wannabemap;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.graphics.Palette;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mapzip.ppang.mapzipproject.R;
import com.mapzip.ppang.mapzipproject.map.NMapPOIflagType;
import com.mapzip.ppang.mapzipproject.map.NMapViewerResourceProvider;
import com.mapzip.ppang.mapzipproject.model.LocationInfo;
import com.nhn.android.maps.NMapActivity;
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
    private NMapViewerResourceProvider mMapViewerResourceProvider;
    private NMapOverlayManager mMapOverlayManager;
    private NMapPOIdataOverlay mPoiDataOverlay;

    private BottomSheetBehavior mBehavior;
    private ImageView mUpImage;
    private RelativeLayout mNameTagLayout;
    private TextView mNameText;
    private TextView mTagsText;
    private TextView mGoodReviewText;
    private TextView mBadReviewText;
    private TextView mCustomReviewText;
    private TextView mAddressText;
    private TextView mContactText;
    private FloatingActionButton mDeleteFab;

    private MapInfosContract.UserActionListener mActionsListener;

    private Palette.PaletteAsyncListener paletteAsyncListener = new Palette.PaletteAsyncListener() {
        @Override
        public void onGenerated(Palette palette) {
            mNameTagLayout.setBackgroundColor(palette.getMutedColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary)));
        }
    };

    @Override
    protected void onStart() {
        super.onStart();

    }

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
        initDetailReview();
    }

    private void initDetailReview() {
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.map_infos_coordinatorlayout);
        View bottomsheet = coordinatorLayout.findViewById(R.id.map_infos_bottomsheet);
        mBehavior = BottomSheetBehavior.from(bottomsheet);

        mUpImage = (ImageView) findViewById(R.id.detailreview_image);
        mNameTagLayout = (RelativeLayout) findViewById(R.id.detailreview_name_tag_space);
        mNameText = (TextView) findViewById(R.id.detailreview_name);
        mTagsText = (TextView) findViewById(R.id.detailreview_tags);
        mGoodReviewText = (TextView) findViewById(R.id.detailreview_goodreview);
        mBadReviewText = (TextView) findViewById(R.id.detailreview_badreview);
        mCustomReviewText = (TextView) findViewById(R.id.detailreview_customreview);
        mAddressText = (TextView) findViewById(R.id.detailreview_address);
        mContactText = (TextView) findViewById(R.id.detailreview_contact);
        mDeleteFab = (FloatingActionButton) findViewById(R.id.detailreview_delete_button);
        findViewById(R.id.detailreview_exit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
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

        mMapView.getMapController();
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
    public void showDetailReview(LocationInfo locationData) {
        mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        setLayoutColor();
        mNameText.setText(locationData.getLocationName());
        mTagsText.setText(locationData.getTags());
        mGoodReviewText.setText(locationData.getGoodReview());
        mBadReviewText.setText(locationData.getBadReview());
        mCustomReviewText.setText(locationData.getCustumReview());
        mAddressText.setText(locationData.getLocationAddress());
        mContactText.setText(locationData.getContact());
    }

    private void setLayoutColor() {
        mUpImage.buildDrawingCache();
        Bitmap bitmap = mUpImage.getDrawingCache();
        if (bitmap != null && !bitmap.isRecycled()) {
            Palette.from(bitmap).generate(paletteAsyncListener);
        }
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
