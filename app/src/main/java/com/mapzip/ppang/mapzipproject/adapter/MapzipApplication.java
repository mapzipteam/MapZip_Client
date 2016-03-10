package com.mapzip.ppang.mapzipproject.adapter;

/**
 * Created by Minjeong on 2015-08-23.
 */

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.mapzip.ppang.mapzipproject.main.MainActivity;
import com.mapzip.ppang.mapzipproject.model.SystemMain;
import com.mapzip.ppang.mapzipproject.model.UserData;

import io.fabric.sdk.android.Fabric;
import java.lang.reflect.Field;

public class MapzipApplication extends Application {

    private final String TAG = "MapzipApplication";

    public static boolean mDebugMode;

    @Override
    public void onCreate() {
        super.onCreate();

        initMapzip(true); // release 할때는 false 로 바꿔주셔야 합니다
        initFabric();


        setDefaultFont(this, "DEFAULT", "default_font2.ttf");
        setDefaultFont(this, "SANS_SERIF", "default_font2.ttf");
        setDefaultFont(this, "SERIF", "default_font2.ttf");



    }

    /**
     * Mapzip Application 전체에서 쓰일 전반적인 세팅작업
     * Deployment 를 할때는 debugmode 를 false 로 만들어야 합니다
     * @param debugmode
     */
    private void initMapzip(boolean debugmode){
        this.mDebugMode = debugmode;

        UserData user = UserData.getInstance();
        int app_version = -1;
        try {
            PackageInfo packageInfo = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
            app_version = packageInfo.versionCode;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        user.setBuild_version(app_version);
        MapzipApplication.doLogging(TAG, "userdata_app_version : "+user.getBuild_version());


    }

    /**
     * Fabric을 애플리케이션 실행시에 초기화 시키는 루틴입니다.
     * debuggable의 true 인자는 Play Store에 배포시에 false로 변경해야합니다.
     *
     */
    private void initFabric() {
        final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(mDebugMode) // Play Store 배포시에는 이것을 false로 변경하여야합니다.
                .build();
        try{
            Fabric.with(fabric);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 로그 문자열 출력을 어플리케이션 디버그모드에 따라서 뽑아낼껀지 말껀지를 결정하는 함수입니다
     * 모든 로그 문자열 출력은 이 함수를 통하길 권장합니다
     * @param TAG
     * @param string
     */
    public static void doLogging(String TAG, String string){
        if(mDebugMode){
            Log.d(TAG,string);
        }
    }

    public static void setDefaultFont(Context ctx,
                                      String staticTypefaceFieldName, String fontAssetName) {
        final Typeface regular = Typeface.createFromAsset(ctx.getAssets(),
                fontAssetName);
        replaceFont(staticTypefaceFieldName, regular);
    }

    protected static void replaceFont(String staticTypefaceFieldName,
                                      final Typeface newTypeface) {
        try {
            final Field StaticField = Typeface.class
                    .getDeclaredField(staticTypefaceFieldName);
            StaticField.setAccessible(true);
            StaticField.set(null, newTypeface);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}