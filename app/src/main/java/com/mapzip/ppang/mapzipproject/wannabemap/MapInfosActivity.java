package com.mapzip.ppang.mapzipproject.wannabemap;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mapzip.ppang.mapzipproject.R;
import com.mapzip.ppang.mapzipproject.model.ReviewData;

import java.util.HashMap;
import java.util.List;

public class MapInfosActivity extends AppCompatActivity implements MapInfosContract.View.Activity, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final String TAG = "MapInfosActivity";
    private HashMap<Marker, Integer> mLocationHashMap = new HashMap<Marker, Integer>();

    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;

    private GoogleMap mMap;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_infos);

        mActionsListener = new MapInfosPresenter(this);

        mFragmentManager = getFragmentManager();

        initToolbar();
        initMap();
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
        setSupportActionBar(toolbar);
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * @param datas : 마커를 생성할 장소들에대한 데이터들
     * @see InfosListFragment
     * 장소들에 대한 데이터를 리스트형태로 가져와서 오버레이 아이템(마커)을 생성하고 출력합니다.
     * 이 함수는 InfosListFragment 에서 호출됩니다.
     * @since 2016. 3. 3
     */
    @Override
    public void showLocationMarker(List<ReviewData> datas) {
        LatLngBounds.Builder boundBuilder = new LatLngBounds.Builder();
        for (ReviewData data : datas) {
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(data.getLatLng())
                    .title(data.getLocationName()));

            mLocationHashMap.put(marker, data.getLocationID());
            boundBuilder.include(data.getLatLng());
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundBuilder.build(), 100));
    }

    @Override
    public void showDetailReview(ReviewData data) {
        mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        setLayoutColor();
        mNameText.setText(data.getLocationName());
        mTagsText.setText(data.getTags());
        mGoodReviewText.setText(data.getGoodReview());
        mBadReviewText.setText(data.getBadReview());
        mCustomReviewText.setText(data.getCustumReview());
        mAddressText.setText(data.getLocationAddress());
        mContactText.setText(data.getContact());
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mActionsListener.setUpLocationMarkers();
            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        int locationID = mLocationHashMap.get(marker);
        Log.i(TAG, "onMarkerClick: " + locationID);
        return true;
    }
}
