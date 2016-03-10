package com.mapzip.ppang.mapzipproject.network;

import com.mapzip.ppang.mapzipproject.adapter.MapzipApplication;
import com.mapzip.ppang.mapzipproject.model.UserData;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by myZZUNG on 2016. 3. 10..
 */
public class MapzipRequestBuilder {

    private final String TAG = "MapzipRequestBuilder";

    private final String ATTR_KEY_DEBUG_MODE = "debug_mode";
    private final String ATTR_KEY_BUILD_VERSION = "build_version";

    private JSONObject mRequsetBody;

    private UserData user;

    public MapzipRequestBuilder() throws JSONException {
        user = UserData.getInstance();
        mRequsetBody = new JSONObject();
        mRequsetBody.put(ATTR_KEY_DEBUG_MODE, MapzipApplication.mDebugMode);
        mRequsetBody.put(ATTR_KEY_BUILD_VERSION, user.getBuild_version());
    }

    public void setCustomAttribute(String key, Object value) throws JSONException {
        mRequsetBody.put(key, value);
    }

    public void showInside(){
        MapzipApplication.doLogging(TAG, mRequsetBody.toString());
    }

    public JSONObject build(){
        return this.mRequsetBody;
    }
}
