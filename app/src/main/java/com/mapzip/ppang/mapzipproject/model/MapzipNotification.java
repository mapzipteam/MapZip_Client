package com.mapzip.ppang.mapzipproject.model;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.mapzip.ppang.mapzipproject.R;
import com.mapzip.ppang.mapzipproject.adapter.MapzipApplication;
import com.mapzip.ppang.mapzipproject.main.SplashActivity;

/**
 * Mapzip 서비스에서 Notification 기능을 이용해야할 때 사용합니다
 * Created by myZZUNG on 2016. 4. 16..
 */
public class MapzipNotification {
    private final String TAG = "MapzipNotification";

    public static final int BY_GCM = 1;
    public static final int BY_REVIEW_REGI = 2;

    private Context mContext;

    private final String default_title = "MapZip";

    public MapzipNotification(Context context){
        this.mContext = context;
    }

    /**
     * sendNotification 은 사용목적 및 편의를 고려해 두가지 형태를 지원합니다
     * 기존에 등록된 사용목적에 부합하는 경우 (type 만으로 자동완성을 원할경우) type 만 인자로 넘기면
     * default 값으로 실행합니다
     * 만약 제목과 내용을 직접 담아야 할 경우에는 title, message 를 각각 첫번째 인자, 두번째 인자로 넘기면 됩니다
     * @param type
     */
    public void sendNotification(int type){
        switch (type){
            case BY_REVIEW_REGI:
                inside_sendNotification(default_title, "새로운 리뷰가 성공적으로 등록되었습니다", type);
                break;
            default:
                MapzipApplication.doLogging(TAG, "unknown notification type");
        }

    }
    public void sendNotification(String title, String message, int type) {
        inside_sendNotification(title, message, type);
    }

    private void inside_sendNotification(String title, String message, int type){
        Intent intent = new Intent(mContext, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.drawable.egg)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(type /* ID of notification */, notificationBuilder.build());
    }
}
