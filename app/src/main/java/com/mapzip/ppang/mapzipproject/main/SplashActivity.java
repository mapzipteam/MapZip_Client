package com.mapzip.ppang.mapzipproject.main;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.mapzip.ppang.mapzipproject.R;
import com.mapzip.ppang.mapzipproject.gcm.QuickstartPreferences;
import com.mapzip.ppang.mapzipproject.gcm.RegistrationIntentService;
import com.mapzip.ppang.mapzipproject.model.UserData;
import com.mapzip.ppang.mapzipproject.network.NetworkUtil;

public class SplashActivity extends Activity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "SplashActivity";

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private ImageView splashText;
    private ImageView StartImage_ma;
    private ImageView StartImage_flag1;
    private ImageView StartImage_zi;
    private ImageView StartImage_flag2;
    private Animation start_text_ani;
    private Animation start_flag_ani_1;
    private Animation start_flag_ani_2;

    private UserData userdata;

    private boolean is_network_check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Log.d(TAG, "on Create");

        userdata = UserData.getInstance();

        splashText = (ImageView) findViewById(R.id.splash_text);
        start_text_ani = AnimationUtils.loadAnimation(this, R.anim.start_alpha);
        splashText.startAnimation(start_text_ani);

        /*
        StartImage_ma = (ImageView)findViewById(R.id.start_ma_image);
        StartImage_flag1 = (ImageView)findViewById(R.id.start_flag1_image);
        StartImage_zi = (ImageView)findViewById(R.id.start_zi_image);
        StartImage_flag2 = (ImageView)findViewById(R.id.start_flag2_image);
        start_text_ani = AnimationUtils.loadAnimation(this,R.anim.start_alpha);
        start_flag_ani_1 = AnimationUtils.loadAnimation(this,R.anim.start_up2down_1);
        start_flag_ani_2 = AnimationUtils.loadAnimation(this,R.anim.start_up2down_2);
        StartImage_ma.startAnimation(start_text_ani);
        StartImage_flag1.startAnimation(start_flag_ani_1);
        StartImage_zi.startAnimation(start_text_ani);
        StartImage_flag2.startAnimation(start_flag_ani_2);
*/
        is_network_check = false;

        checkNetworkState();

    }

    private void checkNetworkState(){
        int network_state_check = NetworkUtil.getConnectivityStatus(getApplicationContext());
        Log.d(TAG, "networkcheck : "+network_state_check);
        if((network_state_check == NetworkUtil.TYPE_MOBILE) || (network_state_check == NetworkUtil.TYPE_WIFI)){
            gcmInit();
            Handler hd = new Handler();
            if(is_network_check){
                hd.postDelayed(new splashhandler(),500);
            }else{
                hd.postDelayed(new splashhandler(), 2500);
            }

        }else{
            is_network_check = true;
            AlertDialog.Builder ab = new AlertDialog.Builder(SplashActivity.this);
            ab.setTitle("네트워크 상태 오류");
            ab.setMessage("인터넷 환경이 아닙니다\n인터넷 연결 후 시도해주세요");
            ab.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    checkNetworkState();
                }
            });
            ab.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "on Resume");
        //gcmInit();

    }

    private class splashhandler implements Runnable{
        public void run() {
            startActivity(new Intent(getApplication(), MainActivity.class)); // 로딩이 끝난후 이동할 Activity
            SplashActivity.this.finish(); // 로딩페이지 Activity Stack에서 제거
        }
    }

    private void gcmInit(){
        registBroadcastReceiver();

        getInstanceIdToken();

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_READY));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_GENERATING));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_NOCHANGE));
    }

    /**
     * Instance ID를 이용하여 디바이스 토큰을 가져오는 RegistrationIntentService를 실행한다.
     */
    private void getInstanceIdToken() {
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            //Toast.makeText(getApplicationContext(),"currVersion : "+getAppVersion(getApplicationContext()),Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    /**
     * LocalBroadcast 리시버를 정의한다. 토큰을 획득하기 위한 READY, GENERATING, COMPLETE 액션에 따라 UI에 변화를 준다.
     */
    private void registBroadcastReceiver(){
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();


                if(action.equals(QuickstartPreferences.REGISTRATION_READY)){
                    // 액션이 READY일 경우
                    Log.d(TAG, "GCM 준비완료");
                } else if(action.equals(QuickstartPreferences.REGISTRATION_GENERATING)){
                    // 액션이 GENERATING일 경우
                    Log.d(TAG, "GCM 토큰생성중");
                } else if(action.equals(QuickstartPreferences.REGISTRATION_COMPLETE)){
                    // 액션이 COMPLETE일 경우
                    Log.d(TAG, "GCM 생성완료" + intent.getStringExtra("token"));
                }else if(action.equals(QuickstartPreferences.REGISTRATION_NOCHANGE)){
                    String token = intent.getStringExtra("token");
                    if(token == null){
                        Log.d(TAG,"GCM 패키지 변화없음 : gcm token empty");
                    }else{
                        Log.d(TAG,"GCM 패키지 변화없음 : "+userdata.getGcm_token());
                    }
                }

            }
        };
    }

    /**
     * Google Play Service를 사용할 수 있는 환경인지를 체크한다.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

}
