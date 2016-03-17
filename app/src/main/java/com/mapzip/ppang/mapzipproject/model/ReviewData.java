package com.mapzip.ppang.mapzipproject.model;

import android.support.annotation.Nullable;

import com.nhn.android.maps.maplib.NGeoPoint;

/**
 * Created by acekim on 16. 2. 25.
 */
public class ReviewData {
    private int locationID;
    @Nullable
    private String locationName;
    @Nullable
    private String locationAddress;
    @Nullable
    private NGeoPoint locationLatLng;
    @Nullable
    private String tags;
    @Nullable
    private String goodReview;
    @Nullable
    private String badReview;
    @Nullable
    private String custumReview;
    @Nullable
    private String contact;

    public ReviewData(int locationID, String locationName, String locationAddress, NGeoPoint locationLatLng, String tags, String goodReview, String badReview, String custumReview, String contact) {
        this.locationID = locationID;
        this.locationName = locationName;
        this.locationAddress = locationAddress;
        this.locationLatLng = locationLatLng;
        this.tags = tags;
        this.goodReview = goodReview;
        this.badReview = badReview;
        this.custumReview = custumReview;
        this.contact = contact;
    }

    public void setTags(@Nullable String tags) {
        this.tags = tags;
    }

    public void setGoodReview(@Nullable String goodReview) {
        this.goodReview = goodReview;
    }

    public void setBadReview(@Nullable String badReview) {
        this.badReview = badReview;
    }

    public void setCustumReview(@Nullable String custumReview) {
        this.custumReview = custumReview;
    }

    public void setContact(@Nullable String contact) {
        this.contact = contact;
    }

    @Nullable
    public String getTags() {
        return tags;
    }

    @Nullable
    public String getGoodReview() {
        return goodReview;
    }

    @Nullable
    public String getBadReview() {
        return badReview;
    }

    @Nullable
    public String getCustumReview() {
        return custumReview;
    }

    @Nullable
    public String getContact() {
        return contact;
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
}
