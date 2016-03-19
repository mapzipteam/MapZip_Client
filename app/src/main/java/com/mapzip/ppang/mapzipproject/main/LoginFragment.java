package com.mapzip.ppang.mapzipproject.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.LoginEvent;
import com.mapzip.ppang.mapzipproject.R;
import com.mapzip.ppang.mapzipproject.model.SystemMain;
import com.mapzip.ppang.mapzipproject.model.UserData;
import com.mapzip.ppang.mapzipproject.network.MapzipRequestBuilder;
import com.mapzip.ppang.mapzipproject.network.MapzipResponse;
import com.mapzip.ppang.mapzipproject.network.MyVolley;
import com.mapzip.ppang.mapzipproject.network.NetworkUtil;
import com.mapzip.ppang.mapzipproject.network.ResponseUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ppangg on 2015-07-31.
 */
public class LoginFragment extends Fragment {

    private final String TAG = "LoginFragment";

    private boolean lockBtn=false;
    private LoadingTask Loading;
    private Resources res;
    private EditText inputID;
    private EditText inputPW;
    private Button LoginBtn;
    public UserData user;
    public int mapCount = 0;
    public ProgressDialog  asyncDialog;

    // head
    private ImageView idicon;
    private ImageView pwicon;

    // toast
    private View layout_toast;
    private TextView text_toast;

    //auto_login
    private int isAuto;
    private String auto_id;
    private String auto_pw;
    private CheckBox check_auto;


    public static LoginFragment create(int pageNumber,int isAuto,String auto_id, String auto_pw) {

        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putInt("page", pageNumber);
        args.putInt("isAuto",isAuto);
        args.putString("auto_id",auto_id);
        args.putString("auto_pw",auto_pw);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isAuto = getArguments().getInt("isAuto");
        if(isAuto == 1){
            auto_id = getArguments().getString("auto_id","");
            auto_pw = getArguments().getString("auto_pw","");
        }
        res = getResources();
        asyncDialog = new ProgressDialog(this.getActivity());
        user = UserData.getInstance();

        Loading = new LoadingTask();
        lockBtn=false;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        layout_toast = inflater.inflate(R.layout.my_custom_toast, (ViewGroup) getActivity().findViewById(R.id.custom_toast_layout));
        text_toast = (TextView) layout_toast.findViewById(R.id.textToShow);

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.login_layout, container, false);

        inputID = (EditText) rootView.findViewById(R.id.InputID);
        inputPW = (EditText) rootView.findViewById(R.id.InputPW);
        LoginBtn = (Button) rootView.findViewById(R.id.btnLogin);
        idicon = (ImageView) rootView.findViewById(R.id.idicon);
        pwicon = (ImageView) rootView.findViewById(R.id.pwicon);

        idicon.setBackgroundResource(R.drawable.idicongray);
        pwicon.setBackgroundResource(R.drawable.pwicongray);

        // Auto_Login CheckBox
        check_auto = (CheckBox)rootView.findViewById(R.id.check_auto);
        check_auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check_auto.isChecked()) {
                    user.setIsAuto(1);
                } else {
                    user.setIsAuto(0);

                }
            }
        });

        inputID.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    idicon.setBackgroundResource(R.drawable.idiconyellow);
                else
                    idicon.setBackgroundResource(R.drawable.idicongray);
            }
        });
        inputPW.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    pwicon.setBackgroundResource(R.drawable.pwiconyellow);
                else
                    pwicon.setBackgroundResource(R.drawable.pwicongray);
            }
        });

        if(user.getIsAuto() == 1){
            check_auto.setChecked(true);
            inputID.setText(auto_id);
            inputPW.setText(auto_pw);
        }

        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             if(lockBtn==false) {
                 lockBtn = true;
                 DoLogin(v);
             }
            }
        });

        return rootView;
    }

    public void DoLogin(View v) {
        RequestQueue queue = MyVolley.getInstance(getActivity()).getRequestQueue();

        final String userid = inputID.getText().toString();
        final String userpw = inputPW.getText().toString();
        final String gcm_key = user.getGcm_token();


        if (userid != null && !userid.equals("") && userpw != null && !userpw.equals("")) {

            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(inputPW.getWindowToken(), 0);
            InputMethodManager imm2 = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(inputID.getWindowToken(), 0);

            MapzipRequestBuilder builder = null;
            try {
                builder= new MapzipRequestBuilder();
                builder.setCustomAttribute(NetworkUtil.USER_ID, userid);
                builder.setCustomAttribute(NetworkUtil.USER_PW, userpw);
                builder.setCustomAttribute(NetworkUtil.GCM_KEY, gcm_key);
                builder.showInside();
            } catch (JSONException e) {
                Log.v("제이손", "에러");
            }

            JsonObjectRequest myReq = new JsonObjectRequest(Request.Method.POST,
                    SystemMain.SERVER_LOGIN_URL,
                    builder.build(),
                    createMyReqSuccessListener(),
                    createMyReqErrorListener());

            queue.add(myReq);
        }
    }

    private Response.Listener<JSONObject> createMyReqSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    lockBtn =false;
                    MapzipResponse mapzipResponse = new MapzipResponse(response);
                    mapzipResponse.showAllContents();

                    if (mapzipResponse.getState(ResponseUtil.PROCESS_LOGIN)) {

                        user.LoginOK();
                        user.inputID(inputID.getText().toString());
                        user.inputPW(inputPW.getText().toString());
                        user.inputName(mapzipResponse.getFieldsString(NetworkUtil.USER_NAME));

                        mapCount = mapzipResponse.getFieldsJSONArray(NetworkUtil.MAP_META_INFO).length();

                        // 지도 순서 맞추기
                        user.setMapmetaArray(mapzipResponse.setMapMetaOrder());

                        // 구별 리뷰 갯수 저장하기
                        mapzipResponse.setMapReviewCount(SystemMain.TYPE_USER);

                        Loading.execute();

                        // toast
                        text_toast.setText("환영합니다!");
                        Toast toast = new Toast(getActivity());
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(layout_toast);
                        toast.show();

                        // fabric-Login
                        sendLoginSuccessToAnswers();

                    } else{
                        // toast
                        text_toast.setText("존재하지 않는 계정정보입니다.");
                        Toast toast = new Toast(getActivity());
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(layout_toast);
                        toast.show();
                    }
                } catch (JSONException e) {
                    Log.v(TAG, "JSON ERROR");
                }
            }
        };
    }

    private void sendLoginSuccessToAnswers() {
        Answers.getInstance().logLogin(new LoginEvent()
        .putSuccess(true)
        .putCustomAttribute("Version Code", user.getBuild_version()));
    }

    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                lockBtn =false;

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
    protected class LoadingTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("로딩중입니다..");
            asyncDialog.setCanceledOnTouchOutside(false);
            // show dialog
            asyncDialog.show();
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            for (int mapNum = 1; mapNum <= mapCount; mapNum++)
                user.setMapImage(mapNum, res);
         return null;
        }


        @Override
        protected void onPostExecute(Void result) {

            if (asyncDialog != null) {
                asyncDialog.dismiss();
                Intent intent = new Intent(getActivity(), SlidingTapActivity.class);
                startActivity(intent);
                getActivity().finish();
            }

            super.onPostExecute(result);
        }



    }
}