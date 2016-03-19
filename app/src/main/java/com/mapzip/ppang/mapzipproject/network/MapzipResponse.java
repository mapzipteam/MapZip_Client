package com.mapzip.ppang.mapzipproject.network;

import com.mapzip.ppang.mapzipproject.adapter.MapzipApplication;
import com.mapzip.ppang.mapzipproject.model.FriendData;
import com.mapzip.ppang.mapzipproject.model.SystemMain;
import com.mapzip.ppang.mapzipproject.model.UserData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;

/**
 * Created by myZZUNG on 2016. 3. 5..
 */
public class MapzipResponse{

    private final String TAG = "MapzipResponse";

    private final String RESPONSE_HEADERS = "headers";
    private final String RESPONSE_FIELDS = "fields";
    private final String RESPONSE_DEBUGS = "debugs";

    public static final int TYPE_STRING = 1;
    public static final int TYPE_INT = 2;
    public static final int TYPE_JSON_OBJECT = 3;
    public static final int TYPE_JSON_ARRAY = 4;

    private JSONObject mJsonObject; // total

    private JSONObject mHeaders;
    private JSONObject mFields;

    private JSONArray mDebugs; // 디버그 정보는 Array 형태로 전달됩니다

    private int mBuildVersion = -1; // build_version

    //private UserData user;

    public MapzipResponse() {

    }

    public MapzipResponse(JSONObject response) throws JSONException {
        //user = UserData.getInstance();

        this.mJsonObject = response;
        this.mHeaders = mJsonObject.getJSONObject(RESPONSE_HEADERS);
        this.mFields = mJsonObject.getJSONObject(RESPONSE_FIELDS);
        this.mBuildVersion = mHeaders.getInt("build_version");
        if (mHeaders.getBoolean("debug_mode")) {
            this.mDebugs = mJsonObject.getJSONArray(RESPONSE_DEBUGS);
        } else {
            // ignore
        }
    }

    public void showAllContents() {
        MapzipApplication.doLogging(TAG, mJsonObject.toString());
    }

    public void showHeaders() {
        MapzipApplication.doLogging(TAG, mHeaders.toString());
    }

    public void showDebugs() throws JSONException {
        if (mHeaders.getBoolean("debug_mode")) {
            for (int i = 0; i < mDebugs.length(); i++) {
                MapzipApplication.doLogging(TAG, mDebugs.getString(i));
            }
        }
    }

    public boolean getFieldsBoolean(String name) throws JSONException {
        return mFields.getBoolean(name);
    }

    public String getFieldsString(String name) throws JSONException {
        return mFields.getString(name);
    }

    public JSONArray getFieldsJSONArray(String name) throws JSONException {
        return mFields.getJSONArray(name);
    }

    public JSONObject getFieldsJSONObject(String name) throws JSONException {
        return mFields.getJSONObject(name);
    }

    public int getFieldsInt(String name) throws JSONException {
        return mFields.getInt(name);
    }

    public boolean getState(int process_type) throws JSONException {
        if ((mBuildVersion >= SystemMain.Build.GARNET) && (mBuildVersion < SystemMain.Build.GARNET_END)) {
            switch (process_type) {
                case ResponseUtil.PROCESS_JOIN:
                    if (mFields.getInt("state") == SystemMain.JOIN_SUCCESS) {
                        return true;
                    } else {
                        return false;
                    }
                case ResponseUtil.PROCESS_LOGIN:
                    if (mFields.getInt("state") == SystemMain.LOGIN_SUCCESS) {
                        return true;
                    } else {
                        return false;
                    }
                case ResponseUtil.PROCESS_HOME_GET_REVIEW_META:
                    if (mFields.getInt("state") == SystemMain.CLIENT_REVIEW_META_DOWN_SUCCESS) {
                        return true;
                    } else {
                        return false;
                    }
                case ResponseUtil.PROCESS_SEARCH_MAP:
                    if (mFields.getInt("state") == SystemMain.MAP_SEARCH_SUCCESS) {
                        return true;
                    } else {
                        return false;
                    }
                case ResponseUtil.PROCESS_FRIEND_GET_REVIEW_META:
                    if (mFields.getInt("state") == SystemMain.FRIEND_HOME_SUCCESS) {
                        return true;
                    } else {
                        return false;
                    }
                case ResponseUtil.PROCESS_FRIEND_DELETE:
                    if (mFields.getInt("state") == SystemMain.FRIEND_ITEM_DELETE_SUCCESS) {
                        return true;
                    } else {
                        return false;
                    }
                case ResponseUtil.PROCESS_FRIEND_LIST:
                    if (mFields.getInt("state") == SystemMain.FRIEND_ITEM_SHOW_SUCCESS) {
                        return true;
                    } else {
                        return false;
                    }
                case ResponseUtil.PROCESS_SETTING_LEAVE:
                    if (mFields.getInt("state") == SystemMain.LEAVE_ALL_SUCCESS) {
                        return true;
                    } else {
                        return false;
                    }
                case ResponseUtil.PROCESS_SETTING_NOTICE:
                    if (mFields.getInt("state") == SystemMain.PATCH_NOTE_GET_SUCCESS) {
                        return true;
                    } else {
                        return false;
                    }
                case ResponseUtil.PROCESS_FRIEND_SEARCH_BY_NAME:
                    if (mFields.getInt("state") == SystemMain.FRIEND_SEARCH_SUCCESS) {
                        return true;
                    } else {
                        return false;
                    }
                case ResponseUtil.PROCESS_FRIEND_ADD:
                    if (mFields.getInt("state") == SystemMain.FRIEND_ITEM_ENROLL_SUCCESS) {
                        return true;
                    } else {
                        return false;
                    }
                case ResponseUtil.PROCESS_MAP_SETTING:
                    if (mFields.getInt("state") == SystemMain.MAP_SETTING_SUCCESS) {
                        return true;
                    } else {
                        return false;
                    }
                case ResponseUtil.PROCESS_MAP_RESET:
                    if (mFields.getInt("state") == SystemMain.CLIENT_MAP_ONE_CLEAR_SUCCESS) {
                        return true;
                    } else {
                        return false;
                    }
                case ResponseUtil.PROCESS_SUGGEST:
                    if (mFields.getInt("state") == SystemMain.USER_SOUND_INSERT_SUCCESS) {
                        return true;
                    } else {
                        return false;
                    }
                case ResponseUtil.PROCESS_REVIEW_DELETE:
                    if (mFields.getInt("state") == SystemMain.CLIENT_REVIEW_DATA_DELETE_SUCCESS) {
                        return true;
                    } else {
                        return false;
                    }
                case ResponseUtil.PROCESS_REVIEW_ENROLL:
                    if (mFields.getInt("state") == SystemMain.CLIENT_REVIEW_DATA_ENROLL_SUCCESS) {
                        return true;
                    } else {
                        return false;
                    }
                case ResponseUtil.PROCESS_REVIEW_UPDATE:
                    if (mFields.getInt("state") == SystemMain.CLIENT_REVIEW_DATA_UPDATE_SUCCESS) {
                        return true;
                    } else {
                        return false;
                    }
                case ResponseUtil.PROCESS_REVIEW_MAKE_IMG_DIR:
                    if ((mFields.getInt("state") == SystemMain.CLIENT_REVIEW_IMAGE_MKDIR_SUCCESS)||(mFields.getInt("state") == SystemMain.CLIENT_REVIEW_IMAGE_MKDIR_EXIST)) {
                        return true;
                    } else {
                        return false;
                    }
                case ResponseUtil.PROCESS_REVIEW_IMAGE_SEND:
                    if (mFields.getInt("state") == SystemMain.CLIENT_REVIEW_IMAGE_ENROLL_SUCCESS) {
                        return true;
                    } else {
                        return false;
                    }
                case ResponseUtil.PROCESS_REVIEW_DETAIL:
                    if (mFields.getInt("state") == SystemMain.CLIENT_REVIEW_DETAIL_DOWN_SUCCESS) {
                        return true;
                    } else {
                        return false;
                    }
                default:
                    MapzipApplication.doLogging(TAG, "getState default logic..");
            }
        }
        return false;

    }

    public JSONArray setMapMetaOrder() throws JSONException { // MapMeta 지도 순서 맞추기
        if ((SystemMain.Build.GARNET <= mBuildVersion) && (mBuildVersion < SystemMain.Build.GARNET_END)) {
            int mapCount = mFields.getJSONArray(NetworkUtil.MAP_META_INFO).length(); // 지도 갯수

            int mapMetaOrderArr[] = new int[mapCount + 1];
            mapMetaOrderArr[0] = -1;
            for (int i = 0; i < mapCount; i++) {
                mapMetaOrderArr[Integer.parseInt(mFields.getJSONArray(NetworkUtil.MAP_META_INFO).getJSONObject(i).getString(NetworkUtil.MAP_ID))] = i;
            }

            JSONArray newMapMetaArr = new JSONArray();
            for (int j = 1; j < mapMetaOrderArr.length; j++) {
                newMapMetaArr.put(mFields.getJSONArray(NetworkUtil.MAP_META_INFO).getJSONObject(mapMetaOrderArr[j]));
            }

            return newMapMetaArr;

        }
        return null;
    }

    public void setMapReviewCount(int type) throws JSONException { // 구별 리뷰갯수 저장
        if ((SystemMain.Build.GARNET <= mBuildVersion) && (mBuildVersion < SystemMain.Build.GARNET_END)) {
            UserData user = UserData.getInstance();
            FriendData fuser = FriendData.getInstance();

            JSONObject reviewCountObj = mFields.getJSONObject(NetworkUtil.GU_ENROLL_NUM);
            int mapCount = mFields.getJSONArray(NetworkUtil.MAP_META_INFO).length(); // 지도 갯수

            for (int mapnum = 1; mapnum <= mapCount; mapnum++) {
                if (reviewCountObj.has(String.valueOf(mapnum))) {
                    JSONObject tmp = reviewCountObj.getJSONObject(String.valueOf(mapnum));
                    int gunumber = 1;
                    int reviewnum = 0;
                    for (gunumber = 1; gunumber <= SystemMain.SeoulGuCount; gunumber++) {
                        if (tmp.has(String.valueOf(gunumber))) {
                            reviewnum = tmp.getInt(String.valueOf(gunumber));
                            //배열에 추가
                        } else {
                            reviewnum = 0;
                            //배열에 0 추가
                        }
                        if (type == SystemMain.TYPE_USER)
                            user.setReviewCount(mapnum, gunumber, reviewnum);
                        else
                            fuser.setReviewCount(mapnum, gunumber, reviewnum);
                    }
                } else {
                    for (int gunumber = 1; gunumber <= SystemMain.SeoulGuCount; gunumber++) {
                        if (type == SystemMain.TYPE_USER)
                            user.setReviewCount(mapnum, gunumber, 0);
                        else
                            fuser.setReviewCount(mapnum, gunumber, 0);
                    }
                }
            }
        }

    }
}
