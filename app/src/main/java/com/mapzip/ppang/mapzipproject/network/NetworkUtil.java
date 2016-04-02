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
    public static final String CATEGORY = "category";
    public static final String MAP_HASH_TAG = "hash_tag";
    public static final String REVIEW_META = "map_meta"; // review flags data
    public static final String SEARCH_TARGET = "target";
    public static final String SEARCH_SEQ_NUM = "more";
    public static final String SEARCH_TYPE = "type";
    public static final String SEARCH_MAP = "map_search";
    public static final String TARGET_ID = "target_id";
    public static final String FRIEND_LIST = "friend_list";
    public static final String FRIEND_ID = "friend_id";
    public static final String FRIEND_NAME = "friend_name";
    public static final String IS_FRIEND = "is_friend";
    public static final String TOTAL_REVIEW = "total_review";
    public static final String STATE = "state";
    public static final String NOTICE_VERSION = "version";
    public static final String CONTENTS = "contents";
    public static final String STORE_ID = "store_id";

    public static final String FLAG_TYPE= "flag_type";

    public static final String REVIEW_DATA_STORE_X = "store_x";
    public static final String REVIEW_DATA_STORE_Y = "store_y";
    public static final String REVIEW_DATA_STORE_NAME = "store_name";
    public static final String REVIEW_DATA_STORE_ADDRESS = "store_address";
    public static final String REVIEW_DATA_STORE_CONTACT = "store_contact";
    public static final String REVIEW_DATA_EMOTION = "review_emotion";
    public static final String REVIEW_DATA_TEXT = "review_text";
    public static final String REVIEW_DATA_IMAGE_NUM = "image_num";
    public static final String REVIEW_DATA_GU_NUM = "gu_num";
    public static final String REVIEW_DATA_POSITIVE_TEXT = "positive_text";
    public static final String REVIEW_DATA_NEGATIVE_TEXT = "negative_text";
    public static final String REVIEW_DATA_IMAGE_STRING = "image_string";
    public static final String REVIEW_DATA_IMAGE_NAME = "image_name";
    public static final String REVIEW_DETAIL = "map_detail";

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
