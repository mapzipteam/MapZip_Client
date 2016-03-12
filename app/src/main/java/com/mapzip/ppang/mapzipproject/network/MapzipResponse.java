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

    private JSONObject mJsonObject; // total

    private JSONObject mHeaders;
    private JSONObject mFields;

    private JSONArray mDebugs; // 디버그 정보는 Array 형태로 전달됩니다

    private int mBuildVersion = -1; // build_version

    //private UserData user;

    private final String userName = "user_name";
    private final String mapMetaInfo = "mapmeta_info"; // hashtag, mapname, review count...
    private final String mapId = "map_id";
    private final String guEnrollNum = "gu_enroll_num";

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

    public boolean getState(int process_type) throws JSONException {
        if (mBuildVersion >= SystemMain.Build.GARNET) {
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
                default:
                    MapzipApplication.doLogging(TAG, "getState default logic..");
            }
        }
        return false;

    }

    /*
        LoginFragment
     */
    public String getUserName_login() throws JSONException { // get UserName
        if ((SystemMain.Build.GARNET <= mBuildVersion) && (mBuildVersion <= SystemMain.Build.GARNET_END)) {
            return mFields.getString(userName);
        }
        return null;
    }

    public int getMapCount() throws JSONException { // get UserName
        if ((SystemMain.Build.GARNET <= mBuildVersion) && (mBuildVersion <= SystemMain.Build.GARNET_END)) {
            int mapCount = mFields.getJSONArray(mapMetaInfo).length(); // 지도 갯수
            return mapCount;
        }
        return -1;
    }

    public JSONArray setMapMetaOrder() throws JSONException { // MapMeta 지도 순서 맞추기
        if ((SystemMain.Build.GARNET <= mBuildVersion) && (mBuildVersion <= SystemMain.Build.GARNET_END)) {
            int mapCount = getMapCount(); // 지도 갯수

            int mapMetaOrderArr[] = new int[mapCount + 1];
            mapMetaOrderArr[0] = -1;
            for (int i = 0; i < mapCount; i++) {
                mapMetaOrderArr[Integer.parseInt(mFields.getJSONArray(mapMetaInfo).getJSONObject(i).getString(mapId))] = i;
            }

            JSONArray newMapMetaArr = new JSONArray();
            for (int j = 1; j < mapMetaOrderArr.length; j++) {
                newMapMetaArr.put(mFields.getJSONArray(mapMetaInfo).getJSONObject(mapMetaOrderArr[j]));
            }

            return newMapMetaArr;

        }
        return null;
    }

    public void setMapReviewCount() throws JSONException { // 구별 리뷰갯수 저장
        if ((SystemMain.Build.GARNET <= mBuildVersion) && (mBuildVersion <= SystemMain.Build.GARNET_END)) {
            UserData user = UserData.getInstance();
            JSONObject reviewCountObj = mFields.getJSONObject(guEnrollNum);
            int mapCount = getMapCount(); // 지도 갯수

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
