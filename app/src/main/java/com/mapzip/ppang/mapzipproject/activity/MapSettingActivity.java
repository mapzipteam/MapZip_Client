package com.mapzip.ppang.mapzipproject.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mapzip.ppang.mapzipproject.R;
import com.mapzip.ppang.mapzipproject.model.SystemMain;
import com.mapzip.ppang.mapzipproject.model.UserData;
import com.mapzip.ppang.mapzipproject.network.MapzipRequestBuilder;
import com.mapzip.ppang.mapzipproject.network.MapzipResponse;
import com.mapzip.ppang.mapzipproject.network.MyVolley;
import com.mapzip.ppang.mapzipproject.network.NetworkUtil;
import com.mapzip.ppang.mapzipproject.network.ResponseUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ppangg on 2015-08-13.
 */
public class MapSettingActivity extends Activity {
    private final String TAG = "MapSettingActivity";

    private UserData user;
    private EditText mapname;
    private String mapid;
    private EditText hashtag1;
    private EditText hashtag2;
    private EditText hashtag3;
    private EditText hashtag4;
    private EditText hashtag5;
    private String hashtag_send = "";
    private int mapkindnum;

    // toast
    private View layout_toast;
    private TextView text_toast;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mapsetting);
        user = UserData.getInstance();

        ActionBar actionBar = getActionBar();
        actionBar.setTitle("    지도 설정");
        actionBar.setDisplayShowHomeEnabled(false);

        LayoutInflater inflater = this.getLayoutInflater();
        layout_toast = inflater.inflate(R.layout.my_custom_toast, (ViewGroup) findViewById(R.id.custom_toast_layout));
        text_toast = (TextView) layout_toast.findViewById(R.id.textToShow);

        mapname = (EditText) findViewById(R.id.maptext);
        hashtag1 = (EditText) findViewById(R.id.hashtext1);
        hashtag2 = (EditText) findViewById(R.id.hashtext2);
        hashtag3 = (EditText) findViewById(R.id.hashtext3);
        hashtag4 = (EditText) findViewById(R.id.hashtext4);
        hashtag5 = (EditText) findViewById(R.id.hashtext5);

        mapname.setOnFocusChangeListener(ofcl);
        hashtag1.setOnFocusChangeListener(ofcl);
        hashtag2.setOnFocusChangeListener(ofcl);
        hashtag3.setOnFocusChangeListener(ofcl);
        hashtag4.setOnFocusChangeListener(ofcl);
        hashtag5.setOnFocusChangeListener(ofcl);

        mapid = getIntent().getStringExtra("mapid");

        String str_mapname = getIntent().getStringExtra("mapcurname");
        mapname.setText(str_mapname);

        String hashorgin = getIntent().getStringExtra("hashtag");
        String[] hasharr = hashorgin.split("#");

        for (int i = 1; i < hasharr.length; i++) {
            switch (i) {
                case 1:
                    hashtag1.setText(hasharr[i]);
                    break;
                case 2:
                    hashtag2.setText(hasharr[i]);
                    break;
                case 3:
                    hashtag3.setText(hasharr[i]);
                    break;
                case 4:
                    hashtag4.setText(hasharr[i]);
                    break;
                case 5:
                    hashtag5.setText(hasharr[i]);
                    break;
            }
        }

        // map name
        Spinner spinner = (Spinner) findViewById(R.id.spinner_mapsetting);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.spinner_number));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        mapkindnum = Integer.parseInt(getIntent().getStringExtra("mapkindnum"));
        spinner.setSelection(mapkindnum - 1);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mapkindnum = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    View.OnFocusChangeListener ofcl = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus)
                v.setBackgroundResource(R.drawable.editback2);
            else
                v.setBackgroundResource(R.drawable.editback);
        }
    };

    public void saveOnclick(View v) {
        if (NetworkUtil.getConnectivityStatus(getApplicationContext()) == NetworkUtil.TYPE_NOT_CONNECTED) {
            // toast
            text_toast.setText("인터넷 연결이 필요합니다.");
            Toast toast = new Toast(getApplicationContext());
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout_toast);
            toast.show();

            return;
        }

        if (!hashtag1.getText().toString().trim().isEmpty())
            hashtag_send += "#" + hashtag1.getText().toString();

        if (!hashtag2.getText().toString().trim().isEmpty())
            hashtag_send += "#" + hashtag2.getText().toString();

        if (!hashtag3.getText().toString().trim().isEmpty())
            hashtag_send += "#" + hashtag3.getText().toString();

        if (!hashtag4.getText().toString().trim().isEmpty())
            hashtag_send += "#" + hashtag4.getText().toString();

        if (!hashtag5.getText().toString().trim().isEmpty())
            hashtag_send += "#" + hashtag5.getText().toString();

        // hash_tag <= hashtag_send
        // category <= mapkindnum
        // title <= mapname.getText().toString();
        // map_id <= mapid

        DoMapset();
        hashtag_send = "";
    }

    public void cancelOnclick(View v) {
        this.finish();
    }

    public void resetOnClick(View v) { // 초기화
        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(this);
        alert_confirm.setMessage("초기화시 그 동안 작성한 지도와 리뷰정보가 모두 소멸됩니다.\n정말 초기화하시겠습니까?\n").setCancelable(false).setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 'YES' target_id,
                        RequestQueue queue = MyVolley.getInstance(getApplicationContext()).getRequestQueue();


                        MapzipRequestBuilder builder = null;
                        try {
                            builder = new MapzipRequestBuilder();
                            builder.setCustomAttribute(NetworkUtil.USER_ID, user.getUserID());
                            builder.setCustomAttribute(NetworkUtil.MAP_ID, mapid);
                            builder.showInside();
                        } catch (JSONException e) {
                            Log.e(TAG, "제이손 에러");
                        }

                        JsonObjectRequest myReq = new JsonObjectRequest(Request.Method.POST,
                                SystemMain.SERVER_RESETMAPDATA_URL,
                                builder.build(),
                                createMyReqSuccessListener_MapReset(),
                                createMyReqErrorListener()) {
                        };
                        queue.add(myReq);
                    }
                }).setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 'No'
                        return;
                    }
                });
        AlertDialog alert = alert_confirm.create();
        alert.show();
    }

    public void DoMapset() {
        RequestQueue queue = MyVolley.getInstance(this).getRequestQueue();

        MapzipRequestBuilder builder = null;
        try {
            builder = new MapzipRequestBuilder();
            builder.setCustomAttribute(NetworkUtil.USER_ID, user.getUserID());
            builder.setCustomAttribute(NetworkUtil.MAP_HASH_TAG, hashtag_send);
            builder.setCustomAttribute(NetworkUtil.CATEGORY, mapkindnum);
            builder.setCustomAttribute(NetworkUtil.MAP_ID, mapid);
            builder.setCustomAttribute(NetworkUtil.MAP_TITLE, mapname.getText().toString());
            builder.showInside();
        } catch (JSONException e) {
            Log.e(TAG, "제이손 에러");
        }

        JsonObjectRequest myReq = new JsonObjectRequest(Request.Method.POST,
                SystemMain.SERVER_MAPSETTING_URL,
                builder.build(),
                createMyReqSuccessListener(),
                createMyReqErrorListener()) {
        };
        queue.add(myReq);
    }

    private Response.Listener<JSONObject> createMyReqSuccessListener_MapReset() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    MapzipResponse mapzipResponse = new MapzipResponse(response);
                    mapzipResponse.showAllContents();
                    if (mapzipResponse.getState(ResponseUtil.PROCESS_MAP_RESET)) {

                        for (int gunumber = 1; gunumber <= SystemMain.SeoulGuCount; gunumber++)
                            user.setReviewCount(Integer.parseInt(mapid), gunumber, 0);

                        user.setMapforpinNum(Integer.parseInt(mapid), 2); // no review

                        Resources res = getResources();
                        user.setMapImage(Integer.parseInt(mapid), res);

                        hashtag1.setText("해");
                        hashtag2.setText("쉬");
                        hashtag3.setText("태");
                        hashtag4.setText("그");
                        hashtag5.setText("맛집");

                        hashtag_send = "#해#쉬#태#그#맛집";
                        mapname.setText("나만의 지도" + mapid);
                        mapkindnum = SystemMain.SEOUL_MAP_NUM;

                        DoMapset();
                    }
                } catch(JSONException e){
                    Log.e(TAG, "제이손 에러");
                }
            }
        };
    }

    private Response.Listener<JSONObject> createMyReqSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    MapzipResponse mapzipResponse = new MapzipResponse(response);
                    mapzipResponse.showAllContents();
                    if (mapzipResponse.getState(ResponseUtil.PROCESS_MAP_SETTING)) {

                        // 지도 순서 맞추기
                        user.setMapmetaArray(mapzipResponse.setMapMetaOrder());

                        //HomeFragment hf = (HomeFragment) getFragmentManager().findFragmentByTag("home_fragment");
                        //hf.refresh();

                        user.setMapmetaNum(1);

                        text_toast.setText("저장되었습니다.");
                        Toast toast = new Toast(getApplicationContext());
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(layout_toast);
                        toast.show();

                        finish();
                    }else {
                        // toast
                        text_toast.setText("다시 시도해주세요");
                        Toast toast = new Toast(getApplicationContext());
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(layout_toast);
                        toast.show();
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "제이손 에러");
                }
            }
        };
    }

    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                hashtag_send = "";

                try {
                    // toast
                    text_toast.setText("인터넷 연결이 필요합니다.");
                    Toast toast = new Toast(getApplicationContext());
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout_toast);
                    toast.show();

                    Log.e(TAG, error.getMessage());
                } catch (NullPointerException ex) {
                    // toast
                    Log.e(TAG, "nullpointexception");
                }
            }
        };
    }
}
