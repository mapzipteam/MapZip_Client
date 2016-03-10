package com.mapzip.ppang.mapzipproject.main;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
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
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.SignUpEvent;
import com.mapzip.ppang.mapzipproject.R;
import com.mapzip.ppang.mapzipproject.fabric.FabricPreferences;
import com.mapzip.ppang.mapzipproject.fabric.JoinFabric;
import com.mapzip.ppang.mapzipproject.model.SystemMain;
import com.mapzip.ppang.mapzipproject.network.MapzipRequestBuilder;
import com.mapzip.ppang.mapzipproject.network.MapzipResponse;
import com.mapzip.ppang.mapzipproject.network.MyVolley;
import com.mapzip.ppang.mapzipproject.network.ResponseUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

import static com.mapzip.ppang.mapzipproject.fabric.FabricPreferences.*;

/**
 * Created by ppangg on 2015-07-31.
 */
public class JoinFragment extends Fragment {

    private EditText inputID;
    private EditText inputName;
    private EditText inputPW;
    private EditText inputPW2;

    private int mPageNumber;

    private Button JoinBtn;

    // TextWatcher
    private TextWatcher textWatcher;
    private int textcount=0;

    // head
    private ImageView joinhead;
    private ImageView joinhead2;
    private ImageView joinhead3;
    private ImageView joinhead4;

    // toast
    private View layout_toast;
    private TextView text_toast;

    // Join Fabric Preferences
    private JoinFabric joinFabric;

    // joinCheck boolean
    private boolean idok = false;
    private boolean nameok = false;
    private boolean pwok = false;
    private boolean pw2ok = false;
    private boolean join_total_ok = false;

    public static JoinFragment create(int pageNumber) {
        JoinFragment fragment = new JoinFragment();
        Bundle args = new Bundle();
        args.putInt("page", pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt("page");
        joinFabric = new JoinFabric();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        layout_toast = inflater.inflate(R.layout.my_custom_toast, (ViewGroup) getActivity().findViewById(R.id.custom_toast_layout));
        text_toast = (TextView) layout_toast.findViewById(R.id.textToShow);

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.join_layout, container, false);

        inputID = (EditText) rootView.findViewById(R.id.InputID);
        inputName = (EditText) rootView.findViewById(R.id.InputName);
        inputPW = (EditText) rootView.findViewById(R.id.InputPW);
        inputPW2 = (EditText) rootView.findViewById(R.id.InputPW2);
        JoinBtn = (Button) rootView.findViewById(R.id.btnJoin);
        joinhead = (ImageView) rootView.findViewById(R.id.joinhead);
        joinhead2 = (ImageView) rootView.findViewById(R.id.joinhead2);
        joinhead3 = (ImageView) rootView.findViewById(R.id.joinhead3);
        joinhead4 = (ImageView) rootView.findViewById(R.id.joinhead4);



        inputID.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    joinhead.setBackgroundResource(R.drawable.joinedithead);
                else
                    joinhead.setBackgroundResource(R.color.transparent);
            }
        });

        inputName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    joinhead2.setBackgroundResource(R.drawable.joinedithead);
                else
                    joinhead2.setBackgroundResource(R.color.transparent);
            }
        });

        inputPW.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    joinhead3.setBackgroundResource(R.drawable.joinedithead);
                else
                    joinhead3.setBackgroundResource(R.color.transparent);
            }
        });

        inputPW2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    joinhead4.setBackgroundResource(R.drawable.joinedithead);
                else
                    joinhead4.setBackgroundResource(R.color.transparent);
            }
        });

        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                joinCheck();
            }
        };

        inputID.addTextChangedListener(textWatcher);
        inputName.addTextChangedListener(textWatcher);
        inputPW.addTextChangedListener(textWatcher);
        inputPW2.addTextChangedListener(textWatcher);

        JoinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(inputID.getWindowToken(), 0);
                InputMethodManager imm2 = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm2.hideSoftInputFromWindow(inputPW.getWindowToken(), 0);
                InputMethodManager imm3 = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm3.hideSoftInputFromWindow(inputName.getWindowToken(), 0);
                DoJoin(v);


            }
        });

        return rootView;
    }

    /**
     *
     * @param flag 커다란 카테고리 id, pw, interent, success
     * @param detail 카테고리에 대한 자세한 사항
     */
    private void sendJoinActionToAnswers(int flag, int detail) {
//        Answers.getInstance().logContentView(new ContentViewEvent()
//        .putContentName("Join Action")
//        .putContentType("Account")
//        .putContentId("0")
//        .putCustomAttribute("Example1", 1)
//        .putCustomAttribute("Example2", "1"));
        SignUpEvent signUpEvent = new SignUpEvent();

        if(flag == joinFabric.FABRIC_JOIN_ERROR_ID){
            signUpEvent.putSuccess(false)
                    .putCustomAttribute(joinFabric.KEY_JOIN_FAIL_CATEGORY,
                            joinFabric.VALUE_FAIL_CATEGORY_ID);
            if(detail == joinFabric.FABRIC_JOIN_ERROR_ID_SHORT) {
                // when id length too short
                signUpEvent.putCustomAttribute(joinFabric.KEY_JOIN_FAIL_REASON,
                        joinFabric.VALUE_FAIL_ID_LENGTH_SHORT);
            }else if(detail == joinFabric.FABRIC_JOIN_ERROR_ID_PATTERN){
                // when id pattern miss match
                signUpEvent.putCustomAttribute(joinFabric.KEY_JOIN_FAIL_REASON,
                        joinFabric.VALUE_FAIL_ID_PATTERN_MISS);
            }else if(detail == joinFabric.FABRIC_JOIN_ERROR_DUPID){
                // when id duplicate in server
                signUpEvent.putCustomAttribute(joinFabric.KEY_JOIN_FAIL_REASON,
                        joinFabric.VALUE_FAIL_ID_DUP);
            }else if(detail == joinFabric.FABRIC_JOIN_ERROR_ID_LONG){
                // when id length over standard..
                signUpEvent.putCustomAttribute(joinFabric.KEY_JOIN_FAIL_REASON,
                        joinFabric.VALUE_FAIL_ID_LENGTH_LONG);
            }
        }else if(flag == joinFabric.FABRIC_JOIN_ERROR_PW){
            signUpEvent.putSuccess(false)
                    .putCustomAttribute(joinFabric.KEY_JOIN_FAIL_CATEGORY,
                            joinFabric.VALUE_FAIL_CATEGORY_PW);
            if(detail == joinFabric.FABRIC_JOIN_ERROR_PW_SHORT){
                signUpEvent.putCustomAttribute(joinFabric.KEY_JOIN_FAIL_REASON,
                        joinFabric.VALUE_FAIL_PW_LENGTH_SHORT);
            }else if(detail == joinFabric.FABRIC_JOIN_ERROR_PW_CONFIRM){
                signUpEvent.putCustomAttribute(joinFabric.KEY_JOIN_FAIL_REASON,
                        joinFabric.VALUE_FAIL_PW_CONFIRM);
            }
        }else if(flag == joinFabric.FABRIC_JOIN_SUCCESS){
            // join success
            signUpEvent.putSuccess(true);
        }else if(flag == FabricPreferences.FAIL_BY_INTERNET){
            // no interent
            signUpEvent.putSuccess(false)
                    .putCustomAttribute(joinFabric.KEY_JOIN_FAIL_CATEGORY,
                            FabricPreferences.VALUE_NO_INTERNET);
        }

        Answers.getInstance().logSignUp(signUpEvent);
    }

    private void joinCheck(){

        if(inputID.getText().toString().length() >= 5 && inputID.getText().toString().length() <=15){
            idok = true;
        }else{
            idok = false;
        }

        if(inputName.getText().toString().length() >= 1){
            nameok = true;
        }else{
            nameok = false;
        }

        if (inputPW.getText().toString().length() >= 8 && inputPW.getText().toString().length() <= 15){
            pwok = true;
        }else{
            pwok = false;
        }

        if(pwok && inputPW.getText().toString().equals(inputPW2.getText().toString())){
            pw2ok = true;
        }else{
            pw2ok = false;
        }

        if(idok && nameok && pwok && pw2ok){
            join_total_ok = true;
            JoinBtn.setBackgroundResource(R.drawable.joinbtn2);
        }else{
            join_total_ok = false;
            JoinBtn.setBackgroundResource(R.drawable.joinbtn);
        }
    }

    public void DoJoin(View v) {
        if(!join_total_ok){
            joinFailResponse();
        }else{

            RequestQueue queue = MyVolley.getInstance(getActivity()).getRequestQueue();
            final String userid = inputID.getText().toString();
            final String userpw = inputPW.getText().toString();
            final String username = inputName.getText().toString();

            if(idok){
                Pattern ps = Pattern.compile("^[a-zA-Z0-9]+$");//영문, 숫자, 한글만 허용
                if(!ps.matcher(userid).matches()){
                    // toast
                    sendJoinActionToAnswers(joinFabric.FABRIC_JOIN_ERROR_ID, joinFabric.FABRIC_JOIN_ERROR_ID_PATTERN);

                    text_toast.setText("아이디는 영문과 숫자의 조합으로 생성해주세요.");
                    Toast toast = new Toast(getActivity());
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout_toast);
                    toast.show();

                    return;
                }
            }

            if (userid != null && !userid.equals("") && userpw != null && !userpw.equals("") && username != null && !username.equals("")) {
                MapzipRequestBuilder builder = null;
                try {
                    builder= new MapzipRequestBuilder();
                    builder.setCustomAttribute("user_id", userid);
                    builder.setCustomAttribute("user_pw", userpw);
                    builder.setCustomAttribute("user_name", username);
                    builder.showInside();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest myReq = new JsonObjectRequest(Request.Method.POST,
                        SystemMain.SERVER_JOIN_URL,
                        builder.build(),
                        createMyReqSuccessListener(),
                        createMyReqErrorListener()) {
                /*
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("userid", userid);
                    params.put("userpw", userpw);
                    params.put("username", username);
                    return params;
                }*/
                };
                queue.add(myReq);
            }
        }

    }

    private Response.Listener<JSONObject> createMyReqSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    MapzipResponse mapzipResponse = new MapzipResponse(response);
                    mapzipResponse.showAllContents();
                    if (mapzipResponse.getState(ResponseUtil.PROCESS_JOIN)) {
                        // toast
                        text_toast.setText("회원가입에 성공하였습니다.");
                        Toast toast = new Toast(getActivity());
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(layout_toast);
                        toast.show();

                        sendJoinActionToAnswers(joinFabric.FABRIC_JOIN_SUCCESS, 0);


                    } else {
                        // toast
                        text_toast.setText("이미 존재하는 계정정보입니다.");
                        Toast toast = new Toast(getActivity());
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(layout_toast);
                        toast.show();

                        sendJoinActionToAnswers(joinFabric.FABRIC_JOIN_ERROR_ID, joinFabric.FABRIC_JOIN_ERROR_DUPID);

                    }
                }catch (JSONException e){
                    Log.v("에러", "제이손");
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

                    sendJoinActionToAnswers(FabricPreferences.FAIL_BY_INTERNET, 0);

                    Log.e("searchmap", error.getMessage());
                }catch (NullPointerException ex){
                    // toast
                    Log.e("searchmap", "nullpointexception");
                }
            }
        };
    }

    private void joinFailResponse(){
        if(!idok){
            if(inputID.getText().toString().length()<5){
                sendJoinActionToAnswers(joinFabric.FABRIC_JOIN_ERROR_ID, joinFabric.FABRIC_JOIN_ERROR_PW_SHORT);
            }else if(inputID.getText().toString().length() >15){
                sendJoinActionToAnswers(joinFabric.FABRIC_JOIN_ERROR_ID, joinFabric.FABRIC_JOIN_ERROR_ID_LONG);
            }

            text_toast.setText("ID는 5자이상, 15자 이하로 작성해주세요");
            Toast toast = new Toast(getActivity());
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout_toast);
            toast.show();
            return;
        }
        if(!pwok){
            sendJoinActionToAnswers(joinFabric.FABRIC_JOIN_ERROR_PW, joinFabric.FABRIC_JOIN_ERROR_PW_SHORT);

            text_toast.setText("Password는 8자이상, 15자 이하로 작성해주세요");
            Toast toast = new Toast(getActivity());
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout_toast);
            toast.show();

            return;
        }
        if(!pw2ok) {
            sendJoinActionToAnswers(joinFabric.FABRIC_JOIN_ERROR_PW, joinFabric.FABRIC_JOIN_ERROR_PW_CONFIRM);

            text_toast.setText("비밀번호확인을 틀리셨습니다.");
            Toast toast = new Toast(getActivity());
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout_toast);
            toast.show();

            return;
        }
    }
}