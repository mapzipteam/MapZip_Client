package com.mapzip.ppang.mapzipproject.model;

import android.support.annotation.Nullable;

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
    private int reviewCount;

    public LocationInfo(int locationID, String locationName, String locationAddress, int reviewCount) {
        this.locationName = locationName;
        this.locationAddress = locationAddress;
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

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }
}
