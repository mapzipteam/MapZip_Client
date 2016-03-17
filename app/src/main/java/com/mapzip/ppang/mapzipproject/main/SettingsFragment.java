package com.mapzip.ppang.mapzipproject.main;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mapzip.ppang.mapzipproject.R;
import com.mapzip.ppang.mapzipproject.activity.SuggestActivity;
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
 * Created by ppangg on 2015-12-29.
 */
public class SettingsFragment extends Fragment {
    private final String TAG = "SettingsFragment";

    private View v;

    // user data
    private UserData user;

    // toast
    private View layout_toast;
    private TextView text_toast;

    // notice
    private SharedPreferences pref;
    private String noticeString = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        //getActivity().getActionBar().setTitle("설정");
        user = UserData.getInstance();
        pref = getActivity().getSharedPreferences(SystemMain.SHARED_PREFERENCE_AUTOFILE, getActivity().MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_setting, container, false);

        layout_toast = inflater.inflate(R.layout.my_custom_toast, (ViewGroup) getActivity().findViewById(R.id.custom_toast_layout));
        text_toast = (TextView) layout_toast.findViewById(R.id.textToShow);

        ViewGroup layout = (ViewGroup) v.findViewById(R.id.mailLayout);
        layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getActivity(), SuggestActivity.class);
                startActivity(intent);
            }
        });

        ViewGroup layout2 = (ViewGroup) v.findViewById(R.id.leaveLayout);
        layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(getActivity());
                alert_confirm.setMessage("탈퇴시 그 동안 작성한 지도와 리뷰정보가 모두 소멸됩니다.\n정말 탈퇴하시겠습니까?\n").setCancelable(false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 'YES' target_id,
                                RequestQueue queue = MyVolley.getInstance(getActivity()).getRequestQueue();

                                MapzipRequestBuilder builder = null;
                                try {
                                    builder = new MapzipRequestBuilder();
                                    builder.setCustomAttribute(NetworkUtil.TARGET_ID, user.getUserID());
                                    builder.showInside();
                                } catch (JSONException e) {
                                    Log.e(TAG, "제이손 에러");
                                }

                                JsonObjectRequest myReq = new JsonObjectRequest(Request.Method.POST,
                                        SystemMain.SERVER_DELETEUSER_URL,
                                        builder.build(),
                                        createMyReqSuccessListener(),
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
        });

        ViewGroup layout3 = (ViewGroup) v.findViewById(R.id.fetchLayout);
        layout3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                getNotice();
            }
        });

        Button testBtn = (Button) v.findViewById(R.id.testBtn);
        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue queue = MyVolley.getInstance(getActivity()).getRequestQueue();
                MapzipRequestBuilder builder = null;
                try {
                    builder = new MapzipRequestBuilder();
                    builder.setCustomAttribute("custom_key1", "string_value");
                    builder.setCustomAttribute("custom_key2", 5);
                    builder.setCustomAttribute("custom_key3", true);
                    builder.showInside();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest myReq = new JsonObjectRequest(Request.Method.POST,
                        "http://ljs93kr.cafe24.com/mapzip/test/MapzipResponseTest.php",
                        builder.build(),
                        MRSuccessListener(),
                        createMyReqErrorListener()) {
                };
                queue.add(myReq);
            }
        });

        return v;
    }

    private Response.Listener<JSONObject> MRSuccessListener() {
        return new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    MapzipResponse mResponse = new MapzipResponse(response);
                    mResponse.showAllContents();
                    mResponse.showHeaders();
                    mResponse.showDebugs();
                } catch (JSONException e) {
                    e.printStackTrace();
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
                    if (mapzipResponse.getState(ResponseUtil.PROCESS_SETTING_LEAVE)) {
                        // toast
                        text_toast.setText("정상적으로 탈퇴가 완료되었습니다.");
                        Toast toast = new Toast(getActivity());
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(layout_toast);
                        toast.show();

                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    } else {
                        // toast
                        text_toast.setText("다시 시도해주세요.");
                        Toast toast = new Toast(getActivity());
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(layout_toast);
                        toast.show();
                    }
                } catch (JSONException ex) {
                    Log.e(TAG, "제이손 에러");
                }
            }
        };
    }

    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    // toast
                    text_toast.setText("인터넷 연결이 필요합니다.");
                    Toast toast = new Toast(getActivity());
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

    // notice get method
    private void getNotice() {
        RequestQueue queue = MyVolley.getInstance(getActivity()).getRequestQueue();

        MapzipRequestBuilder builder = null;
        try {
            builder = new MapzipRequestBuilder();
            builder.setCustomAttribute(NetworkUtil.STATE, 1);
            builder.showInside();
        } catch (JSONException e) {
            Log.e(TAG,"제이손 에러");
        }

        JsonObjectRequest myReq = new JsonObjectRequest(Request.Method.POST,
                SystemMain.SERVER_NOTICE_URL,
                builder.build(),
                createNoticeReqSuccessListener(),
                createNoticeReqErrorListener()) {
        };
        queue.add(myReq);
    }

    private Response.Listener<JSONObject> createNoticeReqSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    MapzipResponse mapzipResponse = new MapzipResponse(response);
                    mapzipResponse.showAllContents();
                    if (mapzipResponse.getState(ResponseUtil.PROCESS_SETTING_NOTICE)) {

                        noticeString = "버전: " + mapzipResponse.getFieldsMember(MapzipResponse.TYPE_STRING,NetworkUtil.NOTICE_VERSION)+"\n\n";

                        noticeString += mapzipResponse.getFieldsMember(MapzipResponse.TYPE_STRING,NetworkUtil.CONTENTS) + "\n\n";
                        noticeString += "@이 창은 공지사항탭에서 다시 확인할 수 있습니다.";

                        AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
                        ab.setTitle("새로운 MapZip의 패치소식 ^0^/");
                        ab.setMessage(noticeString);
                        ab.setPositiveButton("확인", null);

                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("notice_version", (String)mapzipResponse.getFieldsMember(MapzipResponse.TYPE_STRING, NetworkUtil.NOTICE_VERSION));
                        editor.commit();

                        ab.show();
                    }else{
                        // toast
                        text_toast.setText("다시 시도해주세요.");
                        Toast toast = new Toast(getActivity());
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

    private Response.ErrorListener createNoticeReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    // toast
                    text_toast.setText("인터넷 연결이 필요합니다.");
                    Toast toast = new Toast(getActivity());
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
