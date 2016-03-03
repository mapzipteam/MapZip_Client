package com.mapzip.ppang.mapzipproject.model;

import android.support.annotation.Nullable;

import com.nhn.android.maps.maplib.NGeoPoint;

/**
 * Created by acekim on 16. 2. 25.
 */
public class LocationInfo {
    private int locationID;
    @Nullable
    private String locationName;
    @Nullable
    private String locationAddress;
    @Nullable
    private NGeoPoint locationLatLng;
    @Nullable
    private int reviewCount;

    public LocationInfo(int locationID, @Nullable String locationName, @Nullable String locationAddress, @Nullable NGeoPoint locationLatLng, int reviewCount) {
        this.locationID = locationID;
        this.locationName = locationName;
        this.locationAddress = locationAddress;
        this.locationLatLng = locationLatLng;
        this.reviewCount = reviewCount;
    }

    public int getLocationID() {
        return locationID;
    }

    public void setLocationID(int locationID) {
        this.locationID = locationID;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public NGeoPoint getLocationLatLng() {
        return locationLatLng;
    }

    public void setLocationLatLng(NGeoPoint locationLatLng) {
        this.locationLatLng = locationLatLng;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }
}
