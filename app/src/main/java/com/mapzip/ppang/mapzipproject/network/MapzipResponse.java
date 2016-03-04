package com.mapzip.ppang.mapzipproject.network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by myZZUNG on 2016. 3. 5..
 */
public class MapzipResponse {

    private JSONObject mJsonObject;
    private JSONObject mHeaders;
    private JSONObject mFields;
    private JSONObject mDebugs;

    public MapzipResponse(){

    }
    public MapzipResponse(JSONObject response) throws JSONException {
        this.mJsonObject = response;
        this.mHeaders = mJsonObject.getJSONObject("headers");
        this.mFields = mJsonObject.getJSONObject("fields");
        if(mHeaders.getBoolean("debugmode")){
            this.mDebugs = mJsonObject.getJSONObject("debugs");
        }else{
            // ignore
        }

    }
}
