package com.mapzip.ppang.mapzipproject.network;

import com.mapzip.ppang.mapzipproject.adapter.MapzipApplication;
import com.mapzip.ppang.mapzipproject.model.SystemMain;
import com.mapzip.ppang.mapzipproject.model.UserData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by myZZUNG on 2016. 3. 5..
 */
public class MapzipResponse {

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

    public Object getFieldsMember(int type, String name) throws JSONException{
        switch (type){
            case TYPE_STRING:
                return mFields.getString(name);
            case TYPE_INT:
                return mFields.getInt(name);
            case TYPE_JSON_OBJECT:
                return mFields.getJSONObject(name);
            case TYPE_JSON_ARRAY:
                return mFields.getJSONArray(name);
            default:
                return null;
        }
    }

    public boolean getState(int process_type) throws JSONException {
        if ((mBuildVersion >= SystemMain.Build.GARNET) && (mBuildVersion < SystemMain.Build.GARNET_END) ) {
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
                    if(mFields.getInt("state") == SystemMain.CLIENT_REVIEW_META_DOWN_SUCCESS){
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

    /*
        LoginFragment
     */
    public JSONArray setMapMetaOrder() throws JSONException { // MapMeta 지도 순서 맞추기
        if ((SystemMain.Build.GARNET <= mBuildVersion) && (mBuildVersion < SystemMain.Build.GARNET_END)) {
            int mapCount =  mFields.getJSONArray(NetworkUtil.MAP_META_INFO).length(); // 지도 갯수

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

    public void setMapReviewCount() throws JSONException { // 구별 리뷰갯수 저장
        if ((SystemMain.Build.GARNET <= mBuildVersion) && (mBuildVersion < SystemMain.Build.GARNET_END)) {
            UserData user = UserData.getInstance();
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
                        user.setReviewCount(mapnum, gunumber, reviewnum);
                    }
                } else {
                    for (int gunumber = 1; gunumber <= SystemMain.SeoulGuCount; gunumber++)
                        user.setReviewCount(mapnum, gunumber, 0);
                }
            }
        }

    }
}
