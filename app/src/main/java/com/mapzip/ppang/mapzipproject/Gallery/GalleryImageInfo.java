package com.mapzip.ppang.mapzipproject.Gallery;

/**
 * Created by Song  Ji won on 2016-02-26.
 */
public class GalleryImageInfo {
    private String mId;
    private String mPath;
    private String mFileName;
    private int mOrientation;
    private int mModifiedTime;
    private String mAlbumName;

    private boolean mCheckedState;

    public GalleryImageInfo(){

    }

    public GalleryImageInfo(String id, String path, String fileName, int orientation, int modifiedTime, String albumName) {

        this.mId = id;
        this.mPath = path;
        this.mFileName = fileName;
        this.mOrientation = orientation;
        this.mModifiedTime = modifiedTime;
        this.mAlbumName = albumName;

        this.mCheckedState = false;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmPath() {
        return mPath;
    }

    public void setmPath(String mPath) {
        this.mPath = mPath;
    }

    public String getmFileName() {
        return mFileName;
    }

    public void setmFileName(String mFileName) {
        this.mFileName = mFileName;
    }

    public int getmOrientation() {
        return mOrientation;
    }

    public void setmOrientation(int mOrientation) {
        this.mOrientation = mOrientation;
    }

    public int getmModifiedTime() {
        return mModifiedTime;
    }

    public void setmModifiedTime(int mModifiedTime) {
        this.mModifiedTime = mModifiedTime;
    }

    public String getmAlbumName() {
        return mAlbumName;
    }

    public void setmAlbumName(String mAlbumName) {
        this.mAlbumName = mAlbumName;
    }

    public boolean ismCheckedState() {
        return mCheckedState;
    }

    public void setmCheckedState(boolean mCheckedState) {
        this.mCheckedState = mCheckedState;
    }
}
