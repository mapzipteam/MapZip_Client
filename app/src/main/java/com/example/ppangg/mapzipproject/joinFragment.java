package com.example.ppangg.mapzipproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.ppangg.mapzipproject.network.MyVolley;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ppangg on 2015-07-31.
 */
public class joinFragment extends Fragment {

    private TextView state;

    private EditText inputID;
    private EditText inputName;
    private EditText inputPW;

    private int mPageNumber;
    private Context cont;

    private Button JoinBtn;


    public static joinFragment create(int pageNumber) {
        joinFragment fragment = new joinFragment();
        Bundle args = new Bundle();
        args.putInt("page", pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt("page");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.join_layout, container, false);

        state = (TextView) rootView.findViewById(R.id.TextState2);
        inputID = (EditText) rootView.findViewById(R.id.InputID2);
        inputName = (EditText) rootView.findViewById(R.id.InputName2);
        inputPW = (EditText) rootView.findViewById(R.id.InputPW2);
        JoinBtn = (Button) rootView.findViewById(R.id.btnJoin);

        JoinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoJoin(v);
            }
        });

        return rootView;
    }
/*
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }
*/
    public void DoJoin(View v) {
        RequestQueue queue = MyVolley.getInstance(cont).getRequestQueue();
        String url = SystemMain.SERVER_JOIN_URL;
        final String userid = inputID.getText().toString();
        final String userpw = inputPW.getText().toString();
        final String username = inputName.getText().toString();
        if (userid != null && !userid.equals("") && userpw != null && !userpw.equals("") && username != null && !username.equals("")) {
            StringRequest myReq = new StringRequest(Request.Method.POST,
                    url,
                    NetSuccessListener(),
                    NetErrorListener()) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("userid", userid);
                    params.put("userpw", userpw);
                    params.put("username", username);
                    return params;
                }
            };
            queue.add(myReq);
        }
    }

    private Response.Listener<String> NetSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.v("내용", response);
                Log.v("길이", String.valueOf(response.length()));

                if(response.substring(3).equals("1")) {
                    state.setText("회원가입에 성공하였습니다.");
                    Log.v("회원가입", "성공");
                }
                else {
                    state.setText("이미 존재하는 계정입니다.");
                    Log.v("회원가입", "실패");
                }
            }
        };
    }

    private Response.ErrorListener NetErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("회원가입",error.getMessage());
                state.setText("인터넷 연결이 필요합니다.");
            }
        };
    }
}