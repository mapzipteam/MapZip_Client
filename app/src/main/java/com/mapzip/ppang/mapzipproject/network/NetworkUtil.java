package com.mapzip.ppang.mapzipproject.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 어플리케이션의 네트워크 상태를 조회할 수 있는 Util 입니다
 * Created by myZZUNG on 2016. 3. 4..
 */
public class NetworkUtil {
    public static final String USER_NAME = "user_name";
    public static final String USER_ID = "user_id";
    public static final String USER_PW = "user_pw";
    public static final String GCM_KEY = "gcm_key";
    public static final String MAP_META_INFO = "mapmeta_info"; // hashtag, mapname, review count...
    public static final String MAP_ID = "map_id";
    public static final String GU_ENROLL_NUM = "gu_enroll_num";
    public static final String MAP_TITLE = "title";
    public static final String MAP_CATEGORY = "category";
    public static final String MAP_HASH_TAG = "hash_tag";
    public static final String REVIEW_META = "map_meta"; // review flags data

    public static final int TYPE_WIFI = 1;
    public static final int TYPE_MOBILE = 2;
    public static final int TYPE_NOT_CONNECTED = -1;

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }
}
