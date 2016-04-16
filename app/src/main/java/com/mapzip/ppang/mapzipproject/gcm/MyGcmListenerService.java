package com.mapzip.ppang.mapzipproject.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.mapzip.ppang.mapzipproject.R;
import com.mapzip.ppang.mapzipproject.adapter.MapzipApplication;
import com.mapzip.ppang.mapzipproject.main.SplashActivity;
import com.mapzip.ppang.mapzipproject.model.MapzipNotification;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by myZZUNG on 2016. 1. 24..
 */
public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    /**
     *
     * @param from SenderID 값을 받아온다.
     * @param data Set형태로 GCM으로 받은 데이터 payload이다.
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String title = data.getString("title");
        String message = data.getString("message");
        String notification_type = data.getString("notification_type"); // 보낼때는 Boolean 을 기대하지만, 보내지는 값은 string 입니다
        MapzipApplication.doLogging(TAG, data.toString());
        JSONObject json_extra=null;
        try {
            json_extra = new JSONObject(data.getString("extra"));
            Log.d(TAG, "From: " + from);
            Log.d(TAG, "Title: " + title);
            Log.d(TAG, "Message: " + message);
            Log.d(TAG, "extra : "+json_extra.toString());
            if(notification_type.equals("true")){
                /**
                 * MapzipNotification 을 이용하는 예시입니다
                 */
                MapzipNotification mapzipNotification = new MapzipNotification(getApplicationContext());
                mapzipNotification.sendNotification(title, message, MapzipNotification.BY_GCM);
            }else{
                // no not notification
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (NullPointerException e){
            e.printStackTrace();
        }

    }



}
