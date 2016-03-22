package com.mapzip.ppang.mapzipproject.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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
 * Created by ppangg on 2015-08-30.
 */
public class AddFriendsActivity extends Activity {
    private final String TAG = "AddFriendsActivity";

    private UserData user;

    // toast
    private View layout_toast;
    private TextView text_toast;

    private EditText searchText;
    private TextView friendinfo;
    private Button friendadd;
    private String friendID;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().hide();
        setContentView(R.layout.activity_addfriend);
        user = UserData.getInstance();

        LayoutInflater inflater = this.getLayoutInflater();
        layout_toast = inflater.inflate(R.layout.my_custom_toast, (ViewGroup) findViewById(R.id.custom_toast_layout));
        text_toast = (TextView) layout_toast.findViewById(R.id.textToShow);
        searchText = (EditText) findViewById(R.id.searchText_addfriend);
        friendinfo = (TextView) findViewById(R.id.addfriendText);
        friendadd = (Button) findViewById(R.id.addfriendBtn);
    }

    public void addFriend_search(View v) {

        if(user.getUserID().equals(searchText.getText().toString())){
            // toast
            text_toast.setText("자기자신은 검색할 수 없습니다.");
            Toast toast = new Toast(getApplicationContext());
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout_toast);
            toast.show();

            return;
        } else if(searchText.getText().toString().trim().isEmpty()){
            // toast
            text_toast.setText("검색어를 입력해주세요.");
            Toast toast = new Toast(getApplicationContext());
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout_toast);
            toast.show();
           // Log.v("error","error");

            return;
        }


        // keyboard hide
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);


        RequestQueue queue = MyVolley.getInstance(this).getRequestQueue();
        MapzipRequestBuilder builder = null;
        try {
            builder= new MapzipRequestBuilder();
            builder.setCustomAttribute(NetworkUtil.USER_ID, user.getUserID());
            builder.setCustomAttribute(NetworkUtil.FRIEND_ID, searchText.getText().toString());
            builder.showInside();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest myReq = new JsonObjectRequest(Request.Method.POST,
                SystemMain.SERVER_ADDFRIENDSEARCH_URL,
                builder.build(),
                createMyReqSuccessListener_search(),
                createMyReqErrorListener()) {
        };
        queue.add(myReq);
    }

    public void addFriend_enroll(View v) {
        user.setfriendlock(false);

        // keyboard hide
        InputMethodManager imm2 = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm2.hideSoftInputFromWindow(friendinfo.getWindowToken(), 0);

        RequestQueue queue = MyVolley.getInstance(this).getRequestQueue();
        MapzipRequestBuilder builder = null;
        try {
            builder= new MapzipRequestBuilder();
            builder.setCustomAttribute(NetworkUtil.USER_ID, user.getUserID());
            builder.setCustomAttribute(NetworkUtil.FRIEND_ID,friendID);
            builder.setCustomAttribute(NetworkUtil.USER_NAME,user.getUserName());
            builder.showInside();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest myReq = new JsonObjectRequest(Request.Method.POST,
                SystemMain.SERVER_ADDFRIENDENROLL_URL,
                builder.build(),
                createMyReqSuccessListener_enroll(),
                createMyReqErrorListener()) {
        };
        queue.add(myReq);
    }

    private Response.Listener<JSONObject> createMyReqSuccessListener_search() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                friendID = searchText.getText().toString();

                try {
                    MapzipResponse mapzipResponse = new MapzipResponse(response);
                    mapzipResponse.showAllContents();
                    if (mapzipResponse.getState(ResponseUtil.PROCESS_FRIEND_SEARCH_BY_NAME)) {
                        if(mapzipResponse.getFieldsString(NetworkUtil.TOTAL_REVIEW).equals("null")){
                            // toast
                            text_toast.setText("존재하지 않는 사용자입니다.");
                            Toast toast = new Toast(getApplicationContext());
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout_toast);
                            toast.show();

                            return;
                        }

                        friendinfo.setVisibility(View.GONE);
                        friendadd.setVisibility(View.GONE);

                        friendinfo.setText(mapzipResponse.getFieldsString(NetworkUtil.FRIEND_NAME) + " (" + friendID + ")\n" +
                                "리뷰수: " + mapzipResponse.getFieldsString(NetworkUtil.TOTAL_REVIEW));

                        if (mapzipResponse.getFieldsBoolean(NetworkUtil.IS_FRIEND) == true) {
                            friendadd.setBackgroundResource(R.drawable.friend_add2);
                            friendadd.setEnabled(false);
                        } else {
                            friendadd.setBackgroundResource(R.drawable.friend_add);
                            friendadd.setEnabled(true);
                        }

                        friendinfo.setVisibility(View.VISIBLE);
                        friendadd.setVisibility(View.VISIBLE);
                    } else{
                        // toast
                        text_toast.setText("다시 시도해주세요.");
                        Toast toast = new Toast(getApplicationContext());
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(layout_toast);
                        toast.show();
                    }

                }catch (JSONException ex){
                    Log.e(TAG,"제이손 에러");
                }

            }
        };
    }

    private Response.Listener<JSONObject> createMyReqSuccessListener_enroll() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    MapzipResponse mapzipResponse = new MapzipResponse(response);
                    mapzipResponse.showAllContents();
                    if (mapzipResponse.getState(ResponseUtil.PROCESS_FRIEND_ADD)) {

                        friendadd.setVisibility(View.INVISIBLE);
                        friendadd.setBackgroundResource(R.drawable.friend_add2);
                        friendadd.setEnabled(false);
                        friendadd.setVisibility(View.VISIBLE);

                        // toast
                        text_toast.setText(friendID + "님을 맵갈피에 추가하였습니다.");
                        Toast toast = new Toast(getApplicationContext());
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(layout_toast);
                        toast.show();
                    } else {
                        // toast
                        text_toast.setText("다시 시도해주세요.");
                        Toast toast = new Toast(getApplicationContext());
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(layout_toast);
                        toast.show();
                    }
                }catch (JSONException e){
                    Log.e(TAG,"제이손 에러");
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
                    Toast toast = new Toast(getApplicationContext());
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout_toast);
                    toast.show();

                    Log.e(TAG, error.getMessage());
                }catch (NullPointerException ex){
                    // toast
                    Log.e(TAG, "nullpointexception");
                }
            }
        };
    }
}
