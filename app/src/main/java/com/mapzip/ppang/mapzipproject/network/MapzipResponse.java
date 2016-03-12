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


    public MapzipResponse(){

    }
    public MapzipResponse(JSONObject response) throws JSONException {
        //user = UserData.getInstance();

        this.mJsonObject = response;
        this.mHeaders = mJsonObject.getJSONObject(RESPONSE_HEADERS);
        this.mFields = mJsonObject.getJSONObject(RESPONSE_FIELDS);
        this.mBuildVersion = mHeaders.getInt("build_version");
        if(mHeaders.getBoolean("debug_mode")){
            this.mDebugs = mJsonObject.getJSONArray(RESPONSE_DEBUGS);
        }else{
            // ignore
        }
    }
    public void showAllContents(){
        MapzipApplication.doLogging(TAG, mJsonObject.toString());
    }
    public void showHeaders(){
        MapzipApplication.doLogging(TAG, mHeaders.toString());
    }
    public void showDebugs() throws JSONException {
        if(mHeaders.getBoolean("debug_mode")){
            for(int i=0; i<mDebugs.length(); i++){
                MapzipApplication.doLogging(TAG, mDebugs.getString(i));
            }
        }

    }
    public boolean getState(int process_type) throws JSONException{
        if(mBuildVersion >= SystemMain.Build.EMERALD){
            switch (process_type){
                case ResponseUtil.PROCESS_JOIN :
                    if(mFields.getInt("state") == SystemMain.JOIN_SUCCESS){
                        return true;
                    }else{
                        return false;
                    }
                default:
                    MapzipApplication.doLogging(TAG, "getState default logic..");
            }
        }
        return false;
    }
}
