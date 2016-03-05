package com.mapzip.ppang.mapzipproject.network;

import com.mapzip.ppang.mapzipproject.adapter.MapzipApplication;

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

    private JSONObject mJsonObject;
    private JSONObject mHeaders;
    private JSONObject mFields;
    private JSONArray mDebugs;

    public MapzipResponse(){

    }
    public MapzipResponse(JSONObject response) throws JSONException {
        this.mJsonObject = response;
        this.mHeaders = mJsonObject.getJSONObject(RESPONSE_HEADERS);
        this.mFields = mJsonObject.getJSONObject(RESPONSE_FIELDS);
        if(mHeaders.getBoolean("debugmode")){
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
        for(int i=0; i<mDebugs.length(); i++){
            MapzipApplication.doLogging(TAG, mDebugs.getString(i));
        }
    }
}
