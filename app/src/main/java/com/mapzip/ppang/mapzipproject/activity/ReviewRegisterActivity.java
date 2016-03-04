package com.mapzip.ppang.mapzipproject.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mapzip.ppang.mapzipproject.R;
import com.mapzip.ppang.mapzipproject.adapter.ImageAdapter;
import com.mapzip.ppang.mapzipproject.model.ReviewData;
import com.mapzip.ppang.mapzipproject.model.SystemMain;
import com.mapzip.ppang.mapzipproject.model.UserData;
import com.mapzip.ppang.mapzipproject.network.MyVolley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ppangg on 2015-08-22.
 */
public class ReviewRegisterActivity extends Activity {

    private final int GOODTEXT = 1;
    private final int BADTEXT = 2;

    // state
    private int state = 0; // 0: default review enroll, 1: modify review
    private String primap_id;

    // toast
    private View layout_toast;
    private TextView text_toast;

    // user Data
    private UserData user;

    // UI
    private TextView titleText;
    private TextView addressText;
    private TextView contactText;
    private Button enrollBtn;
    private Button modifyBtn;

    // Image View
    private ViewPager viewPager;
    private List<Bitmap> oPerlishArray; // Image List
    private Bitmap[] bitarr; // Image array, oPerlishArray.toArray(bitarr)
    private ImageAdapter imageadapter;
    private Bitmap noimage;
    private Bitmap[] backupbitarr;

    // Image Select
    final int REQ_CODE_SELECT_IMAGE = 100;
    private boolean oncreatelock = false; // image array clear -> false: oPerlishArray.clear() , true: x

    // Image Send
    private int serverchoice = 0; // Image send check -> 0: default, 1: only text, 2: image send in Loding Background
    private int imagenum = 0; // to identify Image -> increase in doUpload

    // modify image
    private boolean modifyedcheck = false; // image modify check -> false: no modify, true: modified
    private int afterimagenum = 0;

    // Loading
    private LoadingTask loading;
    public ProgressDialog asyncDialog;

    // review text
    private Button textReviewBtn;
    private TextView good_text;
    private TextView bad_text;
    private TextView direct_text_logo;
    private EditText direct_text;

    // review Dialog
    private boolean mGoodTextD_Created = false;
    private boolean mBadTextD_Created = false;
    private Dialog mGoodTextDialog;
    private Dialog mBadTextDialog;
    private CheckBox[] mGoodCheckBoxs;
    private CheckBox[] mBadCheckBoxs;

    // review emotion
    private SeekBar seekbar;
    private ImageView emotion;

    // map spinner
    private ArrayList<String> mapsppinerList; // map name
    private Spinner mapspinner;
    private ArrayAdapter mapadapter;

    // to send Review Data
    private ReviewData reviewData = new ReviewData();

    // set Map Image
    private Resources res;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_review_regi);

        // toast
        LayoutInflater inflater = this.getLayoutInflater();
        layout_toast = inflater.inflate(R.layout.my_custom_toast, (ViewGroup) findViewById(R.id.custom_toast_layout));
        text_toast = (TextView) layout_toast.findViewById(R.id.textToShow);

        // user Data
        user = UserData.getInstance();

        /*
         *  Init var & Setting view
         */
        res = getResources();
        loading = new LoadingTask();
        serverchoice = 0;
        imagenum = 0;

        textReviewBtn = (Button) findViewById(R.id.textBtn_review_regi);
        enrollBtn = (Button) findViewById(R.id.enrollBtn_review_regi);
        modifyBtn = (Button) findViewById(R.id.modifyBtn_review_regi);

        // state
        if(getIntent().getStringExtra("state").equals("modify") == true) {
            state = 1;
            try{
            reviewData = user.getReviewData().clone();
            }catch (Exception ex){
                Log.v("ReviewData","clone ex");
            }

            backupbitarr = user.getGalImages().clone();

            primap_id = reviewData.getMapid();
            Log.v("mapid", reviewData.getMapid());
        }
        else
            state=0;

        if(state == 0){
            enrollBtn.setVisibility(View.VISIBLE);
            modifyBtn.setVisibility(View.GONE);
        }
        else{
            enrollBtn.setVisibility(View.GONE);
            modifyBtn.setVisibility(View.VISIBLE);
        }
        Log.v("state", String.valueOf(state));

        // setting reviewData to send
        reviewData.setStore_x(getIntent().getDoubleExtra("store_x", 0));
        reviewData.setStore_y(getIntent().getDoubleExtra("store_y", 0));
        reviewData.setStore_name(getIntent().getStringExtra("store_name"));
        reviewData.setStore_address(getIntent().getStringExtra("store_address"));
        reviewData.setStore_contact(getIntent().getStringExtra("store_contact"));
        reviewData.setGu_num(getGunum());

        // setting View
        titleText = (TextView) findViewById(R.id.name_review_regi);
        addressText = (TextView) findViewById(R.id.address_txt_review_regi);
        contactText = (TextView) findViewById(R.id.contact_txt_review_regi);
        titleText.setText(reviewData.getStore_name());
        addressText.setText(reviewData.getStore_address());
        contactText.setText(reviewData.getStore_contact());

        /*
         *  setting for Image
         */
        viewPager = (ViewPager) findViewById(R.id.pager_review_regi);

        oPerlishArray = new ArrayList<Bitmap>();

        if(state == 0){ // in enroll
            // no Image
            //noimage = drawableToBitmap(getResources().getDrawable(R.drawable.noimage));
            noimage = BitmapFactory.decodeResource(getResources(), R.drawable.noimage);
            //여기서 아웃오브 메모리 한번났어용

            oPerlishArray.add(noimage);

            bitarr = new Bitmap[oPerlishArray.size()];
            oPerlishArray.toArray(bitarr); // fill the array
            user.inputGalImages(bitarr);
        }
        imageadapter = new ImageAdapter(this, SystemMain.justuser);
        viewPager.setAdapter(imageadapter);

        /*
         *  map spinner
         */
        // get map name
        mapsppinerList = new ArrayList<String>();
        try {
            for (int i = 0; i < user.getMapmetaArray().length(); i++) {
                mapsppinerList.add(user.getMapmetaArray().getJSONObject(i).getString("title"));
            }
        } catch (JSONException ex) {
            Log.v("제이손 에러","review_regi_mapspinner");
        }

        // set map spinner
        mapspinner = (Spinner) findViewById(R.id.spinner_review);
        mapadapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, mapsppinerList);
        mapadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mapspinner.setAdapter(mapadapter);
        if(state == 1) // in modify
                mapspinner.setSelection(Integer.parseInt(reviewData.getMapid())-1);

        // map select
        mapspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    // get map id to send
                    JSONObject mapmeta = null;
                    mapmeta = user.getMapmetaArray().getJSONObject(position);
                    reviewData.setMapid(mapmeta.get("map_id").toString());
                    Log.v("mappid", reviewData.getMapid());
                } catch (JSONException ex) {
                    Log.v("제이손 에러", "review_regi_mapspinner2");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // emotion
        emotion = (ImageView) findViewById(R.id.emotion_review_regi);
        emotion.setImageResource(R.drawable.sample_emotion0); // default emotion image
        seekbar = (SeekBar) findViewById(R.id.emotionBar_review_regi);
        if(state == 1)  // in modify
        {
            int pro = reviewData.getReview_emotion();
            seekbar.setProgress(pro);
            if (pro < 20)
                emotion.setImageResource(R.drawable.emotion1);
            else if ((20 <= pro) && (pro < 40))
                emotion.setImageResource(R.drawable.emotion2);
            else if ((40 <= pro) && (pro < 60))
                emotion.setImageResource(R.drawable.emotion3);
            else if ((60 <= pro) && (pro < 80))
                emotion.setImageResource(R.drawable.emotion4);
            else
                emotion.setImageResource(R.drawable.emotion5);
        }
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                reviewData.setReview_emotion(progress); // to send emotion

                if (progress < 20)
                    emotion.setImageResource(R.drawable.emotion1);
                else if ((20 <= progress) && (progress < 40))
                    emotion.setImageResource(R.drawable.emotion2);
                else if ((40 <= progress) && (progress < 60))
                    emotion.setImageResource(R.drawable.emotion3);
                else if ((60 <= progress) && (progress < 80))
                    emotion.setImageResource(R.drawable.emotion4);
                else
                    emotion.setImageResource(R.drawable.emotion5);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        /*
         *  review text
         */
        direct_text = (EditText) findViewById(R.id.text_review_regi);
        direct_text_logo = (TextView) findViewById(R.id.textLogo_review_regi);
        good_text = (TextView) findViewById(R.id.goodtext_review_regi);
        good_text.setSelected(true);
        bad_text = (TextView) findViewById(R.id.badtext_review_regi);
        bad_text.setSelected(true);

        // in modify
        if(state == 1){
            // 리뷰: 좋은말, 나쁜말.

            // 직접입력 있을 때.
            direct_text.setText(reviewData.getReview_text());
            direct_text.setVisibility(View.VISIBLE);
            direct_text_logo.setVisibility(View.VISIBLE);
        }
    }

    //  onResult - findImageonClick
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v("resultCode", String.valueOf(resultCode));

        if (requestCode == REQ_CODE_SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    //Uri에서 이미지 이름을 얻어온다.
                    //String name_Str = getImageNameToUri(data.getData());

                    //이미지 데이터를 리사이징된 비트맵으로 받아온다.
                    Uri image_uri = data.getData();

                    if (oncreatelock == false || state == 1) { // 사진 여러장 일 때 or modify
                        oPerlishArray.clear();
                        oncreatelock = true;
                    }

                    //2016.01.11송지원이 바꿈
                    int maxWidth = viewPager.getWidth();
                    int maxHeight = viewPager.getHeight();

                    Bitmap resized_image_bitmap = resizeBitmapImage(image_uri, maxWidth, maxHeight);

                    //2016.01.26 송지원이 추가. 이미지 자동회전 오류 잡는 코드.
                    String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
                    Cursor cursor = managedQuery(image_uri, orientationColumn, null, null, null);
                    int orientationDegree = -1;

                    if(cursor != null && cursor.moveToFirst()){
                        orientationDegree = cursor.getInt(cursor.getColumnIndex(orientationColumn[0]));
                    }

                    Bitmap rotated_resized_image_bitmap = rotateBitmapImage(resized_image_bitmap,  orientationDegree);

                    oPerlishArray.add(rotated_resized_image_bitmap);
                    bitarr = new Bitmap[oPerlishArray.size()];
                    oPerlishArray.toArray(bitarr);

                    // save Image in user Data
                    if (state == 1) // in modify
                    {
                        Log.v("image modify", "ok");
                        Log.v("image_length1",String.valueOf(user.getGalImages().length));
                        if((reviewData.getImage_num()+afterimagenum) == 0)
                            user.inputGalImages(bitarr);
                        else
                            user.addGalImages(bitarr);

                        modifyedcheck = true;
                        Log.v("image_length2",String.valueOf(user.getGalImages().length));
                    }
                    else{
                        user.inputGalImages(bitarr);
                    }


                    afterimagenum++;
                    serverchoice = 2;
                    imageadapter = new ImageAdapter(this,SystemMain.justuser);
                    viewPager.setAdapter(imageadapter);
                    imageadapter.notifyDataSetChanged();
//                } catch (FileNotFoundException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // in enroll Btn
    public void DoReviewset(View v) {
        RequestQueue queue = MyVolley.getInstance(this).getRequestQueue();

        JSONObject obj = new JSONObject();
        try {
            reviewData.setReview_text(direct_text.getText().toString());

            if(serverchoice == 2)
                reviewData.setImage_num(user.getGalImages().length);

            obj.put("userid", user.getUserID());
            obj.put("map_id", reviewData.getMapid());
            obj.put("store_x", reviewData.getStore_x());
            obj.put("store_y", reviewData.getStore_y());
            obj.put("store_name", reviewData.getStore_name());
            obj.put("store_address", reviewData.getStore_address());
            obj.put("store_contact", reviewData.getStore_contact());
            obj.put("review_emotion", reviewData.getReview_emotion());
            obj.put("review_text", reviewData.getReview_text());
            obj.put("image_num", reviewData.getImage_num());
            obj.put("gu_num", reviewData.getGu_num());
            obj.put("user_name",user.getUserName());

            Log.v("review 등록 보내기", obj.toString());
        } catch (JSONException e) {
            Log.v("제이손", "에러");
        }

        JsonObjectRequest myReq = new JsonObjectRequest(Request.Method.POST,
                SystemMain.SERVER_REVIEWENROLL_URL,
                obj,
                createMyReqSuccessListener(),
                createMyReqErrorListener()) {
        };
        queue.add(myReq);
    }

    // in enroll Btn -> response, 이미지 있을때
    public void DoReviewset2() {
        RequestQueue queue = MyVolley.getInstance(this).getRequestQueue();

        JSONObject obj = new JSONObject();
        try {
            obj.put("userid", user.getUserID());
            obj.put("map_id", reviewData.getMapid());
            obj.put("store_id", reviewData.getStore_id());

            Log.v("review 등록2 보내기", obj.toString());
        } catch (JSONException e) {
            Log.v("제이손", "에러");
        }

        JsonObjectRequest myReq = new JsonObjectRequest(Request.Method.POST,
                SystemMain.SERVER_REVIEWENROLL2_URL,
                obj,
                createMyReqSuccessListener(),
                createMyReqErrorListener()) {
        };
        queue.add(myReq);
    }

    // in modify Btn
    public void DoModifyset(View v) {
        RequestQueue queue = MyVolley.getInstance(this).getRequestQueue();

        JSONObject obj = new JSONObject();
        try {
            reviewData.setReview_text(direct_text.getText().toString());

            reviewData.setImage_num(reviewData.getImage_num()+afterimagenum);
            obj.put("user_id", user.getUserID());
            obj.put("map_id", reviewData.getMapid());
            obj.put("review_emotion", reviewData.getReview_emotion());
            obj.put("review_text", reviewData.getReview_text());
            obj.put("store_id", getIntent().getStringExtra("store_id"));
            obj.put("image_num", reviewData.getImage_num());
            Log.v("image_num", String.valueOf(reviewData.getImage_num()));

            Log.v("review 수정 보내기", obj.toString());
        } catch (JSONException e) {
            Log.v("제이손", "에러");
        }

        JsonObjectRequest myReq = new JsonObjectRequest(Request.Method.POST,
                SystemMain.SERVER_REVIEWMODIFY_URL,
                obj,
                createMyReqSuccessListener_modify(),
                createMyReqErrorListener()) {
        };
        queue.add(myReq);
    }

    // in modify Btn -> response, 이미지 있을때 (in noimage)
    public void DoModifyset2() {
        RequestQueue queue = MyVolley.getInstance(this).getRequestQueue();

        JSONObject obj = new JSONObject();
        try {
            obj.put("userid", user.getUserID());
            obj.put("map_id", reviewData.getMapid());
            obj.put("store_id", reviewData.getStore_id());

            Log.v("review 등록2 보내기", obj.toString());
        } catch (JSONException e) {
            Log.v("제이손", "에러");
        }

        JsonObjectRequest myReq = new JsonObjectRequest(Request.Method.POST,
                SystemMain.SERVER_REVIEWENROLL2_URL,
                obj,
                createMyReqSuccessListener_modify(),
                createMyReqErrorListener()) {
        };
        queue.add(myReq);
    }

    // for modify response
    private Response.Listener<JSONObject> createMyReqSuccessListener_modify() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("review_modify 받기", response.toString());
                try {
                    if(response.getInt("state") == SystemMain.CLIENT_REVIEW_DATA_UPDATE_SUCCESS) { // 607
                        // if Map Id modified (지도 변경시)
                        if(primap_id.equals(reviewData.getMapid()) == false){
                            int pmap_id = Integer.parseInt(primap_id); // 수정 전
                            int nmap_id = Integer.parseInt(reviewData.getMapid()); // 수정 후 map_id
                            int gu_num = reviewData.getGu_num();
                            int nmapnocheck = 0; // 0: 리뷰있음 1: no review

                            if(user.getPingCount(nmap_id,gu_num) == 0){ // no review check in now map
                                int checknonzero = 0;
                                for(int c=1; c<=SystemMain.SeoulGuCount; c++){
                                    if(user.getPingCount(nmap_id,c) != 0){
                                        checknonzero = 1;
                                        break;
                                    }
                                }
                                if(checknonzero == 0)
                                    nmapnocheck = 1;
                            }

                            // mapforpinNum, PingCount modify, if review count is 0
                            user.setReviewCount(pmap_id, gu_num,user.getPingCount(pmap_id,gu_num)-1);
                            user.setReviewCount(nmap_id, gu_num, user.getPingCount(nmap_id,gu_num)+1);

                            if(user.getPingCount(pmap_id,gu_num) == 0){ // no review check in primap
                                int checknonzero = 0;
                                for(int c=1; c<=SystemMain.SeoulGuCount; c++){
                                    if(user.getPingCount(pmap_id,c) != 0){
                                        checknonzero = 1;
                                        break;
                                    }
                                }
                                if(checknonzero == 0)
                                    user.setMapforpinNum(pmap_id,2);
                            }

                            // map Image reload
                            user.setMapImage(pmap_id, res);
                            user.setMapImage(nmap_id, res);
                            user.setMapmetaNum(1);

                            // mapforpinArray modify
                            JSONArray farray = user.getMapforpinArray(pmap_id); // 이전 지도에서 리뷰디테일 삭제
                            JSONObject moveobj = new JSONObject();
                            for(int i=0; i<farray.length(); i++){
                                if(farray.getJSONObject(i).getString("store_id").equals(reviewData.getStore_id()) == true){
                                    moveobj = farray.getJSONObject(i);
                                    farray = removeJsonObjectAtJsonArrayIndex(farray,i);
                                }
                            }
                            user.setMapforpinArray(farray, pmap_id);
                            Log.v("moveobj", moveobj.toString());

                            if(nmapnocheck == 1) { // 옮길 지도에 리뷰 정보가 없었을때
                                JSONArray sarray = new JSONArray();
                                sarray.put(sarray.length(),moveobj);
                                user.setMapforpinArray(sarray, nmap_id);
                                user.setMapforpinNum(nmap_id, 1);
                                Log.v("1 sarray", sarray.toString());
                            }
                            else{ // 리뷰 정보가 있었을때
                                if(user.getMapforpinNum(nmap_id) != 0) {
                                    JSONArray sarray = user.getMapforpinArray(nmap_id);
                                    Log.v("0_1 sarray", sarray.toString());
                                    sarray.put(sarray.length(), moveobj);
                                    user.setMapforpinArray(sarray, nmap_id);
                                    Log.v("0 sarray", sarray.toString());
                                }
                            }
                            user.setMapRefreshLock(false);
                        }

                        if(modifyedcheck == true){
                            Log.v("리뷰수정", "OK");
                            if((reviewData.getImage_num()-afterimagenum) == 0)
                                DoModifyset2();
                            else{
                                serverchoice = 2;
                                loading.execute();
                                // 2번째통신 이미지갯수만큼 반복
                            }
                        }else{
                            // toast
                            text_toast.setText("리뷰가 수정되었습니다.");
                            Toast toast = new Toast(getApplicationContext());
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(layout_toast);
                            toast.show();
                            
                            finish();
                        }
                    }
                    else if ((response.getInt("state") == SystemMain.CLIENT_REVIEW_IMAGE_MKDIR_SUCCESS) || (response.getInt("state") == SystemMain.CLIENT_REVIEW_IMAGE_MKDIR_EXIST)){ // 602 || 621
                        serverchoice = 2;
                        loading.execute();
                        // 2번째통신 이미지갯수만큼 반복
                    }
                }catch (JSONException e){
                    Log.e("제이손","에러");
                }
            }
        };
    }

    // for enroll response
    private Response.Listener<JSONObject> createMyReqSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("review_regi 받기", response.toString());

                try {
                    if (response.getInt("state") == SystemMain.CLIENT_REVIEW_DATA_ENROLL_SUCCESS) { // 601
                        // 1번째 통신 성공
                        Log.v("리뷰저장", "OK");
                        reviewData.setStore_id(response.getString("store_id"));
                        if (reviewData.getImage_num() != 0)
                            DoReviewset2(); // 이미지 있으면 2번째 통신 시작
                        else {
                            serverchoice = 1; // no image
                            loading.execute();
                        }
                    } else if ((response.getInt("state") == SystemMain.CLIENT_REVIEW_IMAGE_MKDIR_SUCCESS) || (response.getInt("state") == SystemMain.CLIENT_REVIEW_IMAGE_MKDIR_EXIST)) { // 602 || 621
                        // 2번째통신 성공
                        Log.v("리뷰저장2", "OK");
                        serverchoice = 2;
                        loading.execute();
                        // 3번째통신 이미지갯수만큼 반복
                    }
                    if (response.getInt("state") == SystemMain.CLIENT_REVIEW_DATA_ENROLL_EXIST) { // 612
                        //1번째 통신에서 중복가게 걸러내기
                        // toast
                        text_toast.setText("이미 등록 된 가게입니다.");
                        Toast toast = new Toast(getApplicationContext());
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(layout_toast);
                        toast.show();
                    }
                } catch (JSONException ex) {
                    Log.e("제이손","에러");
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

                    Log.e("review_register", error.getMessage());
                } catch (NullPointerException ex) {
                    // toast
                    Log.e("review_register", "nullpointexception");
                }
            }
        };
    }

    /*
     * for Image
     */
    // get image uri, use in DoUpload
    private File getImageFile(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        if (uri == null) {
            return null;
        }
        Cursor mCursor = getContentResolver().query(uri, projection, null, null, MediaStore.Images.Media.DATE_MODIFIED + " desc");
        if (mCursor == null || mCursor.getCount() < 1) {
            return null;
        }
        int column_index = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        mCursor.moveToFirst();

        String path = mCursor.getString(column_index);
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }
        return new File(path);
    }

    // image upload
    public void DoUpload(final int i) {
        Log.v("에러치크","2");

        RequestQueue queue = MyVolley.getInstance(this).getRequestQueue();

        JSONObject obj = new JSONObject();
        try {
            Log.v("길이길이",String.valueOf(user.getGalImages().length));
            String image = getStringImage(user.getGalImages()[i]);
            Log.v("image string",image);
            Log.v("image 길이", String.valueOf(image.length()));

            obj.put("image_string",image);
            obj.put("userid", user.getUserID());
            obj.put("map_id", reviewData.getMapid());
            obj.put("store_id", reviewData.getStore_id());
            obj.put("image_name", "image" + String.valueOf(imagenum));
            imagenum++;

            Log.v("param",obj.toString());

            Log.v("에러치크","1");

        } catch (JSONException e) {
            Log.v("제이손", "에러");
        }

        JsonObjectRequest myReq = new JsonObjectRequest(Request.Method.POST,
                SystemMain.SERVER_REVIEWENROLL3_URL,
                obj,
                createMyReqSuccessListener_image(),
                createMyReqErrorListener_image()) {
        };
        queue.add(myReq);

   /*
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SystemMain.SERVER_REVIEWENROLL3_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Showing toast message of the response
                        Log.v("이미지 업로드",s);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Showing toast
                        Log.v("이미지 업로드 에러", String.valueOf(volleyError));
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                Log.v("길이길이",String.valueOf(user.getGalImages().length));
                String image = getStringImage(user.getGalImages()[i]);
                Log.v("image string",image);
                Log.v("image 길이", String.valueOf(image.length()));

                Map<String, String> params = new Hashtable<String, String>();
                params.put("image_string",image);
                params.put("userid", user.getUserID());
                params.put("map_id", reviewData.getMapid());
                params.put("store_id", reviewData.getStore_id());
                params.put("image_name", "image" + String.valueOf(imagenum));
                imagenum++;

                Log.v("param",params.toString());

                Log.v("에러치크","1");

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
        */

        /*
        mfile = getImageFile(uriarray[i]);
        if (mfile == null) {
            Toast.makeText(getApplicationContext(), "이미지가 선택되지 않았습니다", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> params = new HashMap<String, String>();
        params.put("userid", user.getUserID());
        params.put("map_id", reviewData.getMapid());
        params.put("store_id", reviewData.getStore_id());
        params.put("image_name", "image" + String.valueOf(imagenum));
        imagenum++;

        RequestQueue queue = MyVolley.getInstance(getApplicationContext()).getRequestQueue();
        MultipartRequest mRequest = new MultipartRequest(SystemMain.SERVER_REVIEWENROLL3_URL,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // toast
                        text_toast.setText("인터넷 연결이 필요합니다.");
                        Toast toast = new Toast(getApplicationContext());
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(layout_toast);
                        toast.show();
                    }
                }, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("volley", response);
            }
        }, mfile, params);
        Log.v("사진 보내기", mRequest.toString());
        queue.add(mRequest);
        */
    }

    private Response.Listener<JSONObject> createMyReqSuccessListener_image() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("이미지 업로드", response.toString());
            }
        };
    }

    private Response.ErrorListener createMyReqErrorListener_image() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    Log.e("이미지 업로드", error.getMessage());
                } catch (NullPointerException ex) {
                    // toast
                    Log.e("review_register", "nullpointexception");
                }
            }
        };
    }

    /*
     * util
     */
    // for array item remove
    public static JSONArray removeJsonObjectAtJsonArrayIndex(JSONArray source, int index) throws JSONException {
        if (index < 0 || index > source.length() - 1) {
            throw new IndexOutOfBoundsException();
        }

        final JSONArray copy = new JSONArray();
        for (int i = 0, count = source.length(); i < count; i++) {
            if (i != index) copy.put(source.get(i));
        }
        return copy;
    }

    // resized bitmap
    public Bitmap resizeBitmapImage(Uri image_uri, int maxWidth, int maxHeight){

        String imagePath = getPathFromUri(image_uri);

        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inSampleSize = 4;
        options.outWidth = maxWidth;
        options.outHeight = maxHeight;


        Bitmap bitmap_resized = BitmapFactory.decodeFile(imagePath, options);


        return bitmap_resized;

    }

    public String getPathFromUri(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToNext();
        String path = cursor.getString(cursor.getColumnIndex("_data"));
        cursor.close();

        return path;
    }

    //이미지 degree만큼 회전 해서 return하는 함수
    public Bitmap rotateBitmapImage(Bitmap srcBmp, int degree){

        int width = srcBmp.getWidth();
        int height = srcBmp.getHeight();

        Log.d("dSJW", "아여기보시게" + degree);
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);

        Bitmap rotatedBmp = Bitmap.createBitmap(srcBmp, 0, 0, width, height, matrix, true);

        return rotatedBmp;
    }

    // get Image encoding
    public String getStringImage(Bitmap bmp){/*
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
        */

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // Must compress the Image to reduce image size to make upload easy
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byte_arr = stream.toByteArray();
        // Encode Image to String
        String encodedImage = Base64.encodeToString(byte_arr, 0);

        /*
        String encodedImage="";
        try {

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            // Must compress the Image to reduce image size to make upload easy
            bmp.compress(Bitmap.CompressFormat.JPEG, 50, stream);
            byte[] byte_arr = stream.toByteArray();
            // Encode Image to String
            encodedImage = new String(byte_arr,"UTF-8");

        }catch (Exception ex){
            Log.v("에러","utf-8");
        }
        */
        return encodedImage;
    }

    // for no Image
    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    // to send Gunum
    public int getGunum() {
        int gunum = -1;
        if (reviewData.getStore_address().contains("서울특별시 도봉구"))
            gunum = SystemMain.DoBong;
        else if (reviewData.getStore_address().contains("서울특별시 노원구"))
            gunum = SystemMain.NoWon;
        else if (reviewData.getStore_address().contains("서울특별시 강북구"))
            gunum = SystemMain.GangBuk;
        else if (reviewData.getStore_address().contains("서울특별시 성북구"))
            gunum = SystemMain.SungBuk;
        else if (reviewData.getStore_address().contains("서울특별시 중랑구"))
            gunum = SystemMain.ZongRang;
        else if (reviewData.getStore_address().contains("서울특별시 은평구"))
            gunum = SystemMain.EunPhung;
        else if (reviewData.getStore_address().contains("서울특별시 종로구"))
            gunum = SystemMain.ZongRo;
        else if (reviewData.getStore_address().contains("서울특별시 동대문구"))
            gunum = SystemMain.DongDaeMon;
        else if (reviewData.getStore_address().contains("서울특별시 서대문구"))
            gunum = SystemMain.SuDaeMon;
        else if (reviewData.getStore_address().contains("서울특별시 중구"))
            gunum = SystemMain.Zhong;
        else if (reviewData.getStore_address().contains("서울특별시 성동구"))
            gunum = SystemMain.SungDong;
        else if (reviewData.getStore_address().contains("서울특별시 광진구"))
            gunum = SystemMain.GangZin;
        else if (reviewData.getStore_address().contains("서울특별시 강동구"))
            gunum = SystemMain.GangDong;
        else if (reviewData.getStore_address().contains("서울특별시 마포구"))
            gunum = SystemMain.MaPho;
        else if (reviewData.getStore_address().contains("서울특별시 용산구"))
            gunum = SystemMain.YongSan;
        else if (reviewData.getStore_address().contains("서울특별시 강서구"))
            gunum = SystemMain.GangSue;
        else if (reviewData.getStore_address().contains("서울특별시 양천구"))
            gunum = SystemMain.YangChen;
        else if (reviewData.getStore_address().contains("서울특별시 구로구"))
            gunum = SystemMain.GuRo;
        else if (reviewData.getStore_address().contains("서울특별시 영등포구"))
            gunum = SystemMain.YongDengPo;
        else if (reviewData.getStore_address().contains("서울특별시 동작구"))
            gunum = SystemMain.DongJack;
        else if (reviewData.getStore_address().contains("서울특별시 금천구"))
            gunum = SystemMain.GemChun;
        else if (reviewData.getStore_address().contains("서울특별시 관악구"))
            gunum = SystemMain.GanAk;
        else if (reviewData.getStore_address().contains("서울특별시 서초구"))
            gunum = SystemMain.SeoCho;
        else if (reviewData.getStore_address().contains("서울특별시 강남구"))
            gunum = SystemMain.GangNam;
        else if (reviewData.getStore_address().contains("서울특별시 송파구"))
            gunum = SystemMain.SongPa;

        return gunum;
    }

    // setting review text & emotion
    public int reviewtextset() {
        if (reviewData.getReview_emotion() == 0) { // emotion not selected
            // toast
            text_toast.setText("이모티콘을 선택해주세요.");
            Toast toast = new Toast(getApplicationContext());
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout_toast);
            toast.show();

            return -1;
        } else if (good_text.getText().toString().trim().isEmpty() && bad_text.getText().toString().trim().isEmpty() && direct_text.getText().toString().trim().isEmpty()) { // review text not selected
            // toast
            text_toast.setText("리뷰를 작성해주세요.");
            Toast toast = new Toast(getApplicationContext());
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout_toast);
            toast.show();

            return -1;
        } else{
            return 1;
        }
    }

    /*
     *  Loading
     */
    protected class LoadingTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            asyncDialog = new ProgressDialog(ReviewRegisterActivity.this);
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("로딩중입니다..");
            asyncDialog.setCanceledOnTouchOutside(false);
            asyncDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            if (serverchoice == 1) {
            } else if (serverchoice == 2) {
/*
                if(state==1) // in modify
                    imagenum=(reviewData.getImage_num()-afterimagenum);
*/
                for (int i = 0; i < reviewData.getImage_num(); i++)
                    DoUpload(i);

                user.setAfterModify(true);
            }

            if(state == 0) {
                int tmp = user.getPingCount(Integer.parseInt(reviewData.getMapid()), reviewData.getGu_num());
                user.setReviewCount(Integer.parseInt(reviewData.getMapid()), reviewData.getGu_num(), tmp + 1);
                user.setMapImage(Integer.parseInt(reviewData.getMapid()), res);
            }

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Log.v("loading", "success");
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (asyncDialog != null) {
                asyncDialog.dismiss();
            }
            Log.d("loading", "finish");

            if(state == 0) {
                // toast
                text_toast.setText("리뷰가 등록되었습니다.");
                Toast toast = new Toast(getApplicationContext());
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(layout_toast);
                toast.show();
            }
            else{
                // toast
                text_toast.setText("리뷰가 수정되었습니다.");
                Toast toast = new Toast(getApplicationContext());
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(layout_toast);
                toast.show();
            }
            finish();
            
            super.onPostExecute(result);
        }
    }

    /*
     *  onClick Btn
     */
    // 사진찾기 버튼
    public void findImageonClick(View v) {
        if(user.getGalImages().length >= SystemMain.MAXIMAGENUM){
            // toast
            text_toast.setText("더이상 이미지를 추가할 수 없습니다.");
            Toast toast = new Toast(getApplicationContext());
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout_toast);
            toast.show();

            return;
        }

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
    }

    // 사진제거 버튼
    public void deleteImageonClick(View v){
        if((reviewData.getImage_num()+afterimagenum) == 0){ // 이미지가 없는 상태
            Log.v("imagenum",String.valueOf(reviewData.getImage_num()));
            Log.v("afterimagenum",String.valueOf(afterimagenum));
            // toast
            text_toast.setText("제거할 이미지가 없습니다.");
            Toast toast = new Toast(getApplicationContext());
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout_toast);
            toast.show();

            return;
        }
        modifyedcheck = true;

        Log.v("이미지카운트", String.valueOf(viewPager.getCurrentItem()));
        int delposition = viewPager.getCurrentItem();
        Bitmap[] fordelbitarr = user.getGalImages();

        //add userGal. removed image arr
        oPerlishArray.clear();

        if(fordelbitarr.length == 1) {
            // no Image
            noimage = drawableToBitmap(getResources().getDrawable(R.drawable.noimage));
            oPerlishArray.add(noimage);
        }
        else{
            for (int i = 0; i < fordelbitarr.length; i++)
                oPerlishArray.add(fordelbitarr[i]);
            oPerlishArray.remove(delposition);
        }
        bitarr = new Bitmap[oPerlishArray.size()];
        oPerlishArray.toArray(bitarr);
        user.inputGalImages(bitarr);

        //set imageadapter
        imageadapter = new ImageAdapter(this,SystemMain.justuser);
        viewPager.setAdapter(imageadapter);
        imageadapter.notifyDataSetChanged();

        afterimagenum--;

        if(afterimagenum==0)
            oncreatelock=false;
        Log.v("afterimagenum_d",String.valueOf(afterimagenum));
    }

    // 리뷰등록 버튼
    public void enrollonClick_review_regi(View v) {
            if(reviewtextset() == 1) {
                DoReviewset(v); // 서버 통신
                user.setMapforpinNum(Integer.parseInt(reviewData.getMapid()), 0); // to review loading
            }
    }

    // 취소 버튼
    public void cancelonClick_review_regi(View v) {
        finish();
    }

    // 리뷰수정 버튼
    public void modifyonClick_review_regi(View v){
        if(reviewtextset()==1){
            DoModifyset(v);
        }
    }

    // 좋은말 리뷰 더하기 버튼
    public void goodtextClick_review_regi(View v){
        if(mGoodTextD_Created == false) {
            mGoodTextDialog = createDialog(GOODTEXT);
            mGoodTextD_Created = true;
            mGoodTextDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(final DialogInterface dialog) {
                    Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                    positiveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        String goodtext_complete = "";
                        int checkedNum = 0;

                        for (int i = 0; i < mGoodCheckBoxs.length; i++) {
                            if (mGoodCheckBoxs[i].isChecked()) {
                                checkedNum++;
                                goodtext_complete += (" " + mGoodCheckBoxs[i].getText());
                            }
                        }

                        if (checkedNum > 5) {
                            // toast
                            text_toast.setText("5개까지 선택할 수 있습니다.");
                            Toast toast = new Toast(getApplicationContext());
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(layout_toast);
                            toast.show();
                            return;
                        }

                        good_text.setText(goodtext_complete);
                        dialog.dismiss();
                        }
                    });
                }
            });
        }

        mGoodTextDialog.show();
    }

    // 나쁜말 리뷰 더하기 버튼
    public void badtextClick_review_regi(View v){
        if(mBadTextD_Created == false) {
            mBadTextDialog = createDialog(BADTEXT);
            mBadTextD_Created = true;
            mBadTextDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(final DialogInterface dialog) {
                    Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                    positiveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String badtext_complete = "";
                            int checkedNum = 0;

                            for (int i = 0; i < mBadCheckBoxs.length; i++) {
                                if (mBadCheckBoxs[i].isChecked()) {
                                    checkedNum++;
                                    badtext_complete += (" " + mBadCheckBoxs[i].getText());
                                }
                            }

                            if (checkedNum > 5) {
                                // toast
                                text_toast.setText("5개까지 선택할 수 있습니다.");
                                Toast toast = new Toast(getApplicationContext());
                                toast.setDuration(Toast.LENGTH_SHORT);
                                toast.setView(layout_toast);
                                toast.show();
                                return;
                            }

                            bad_text.setText(badtext_complete);
                            dialog.dismiss();
                        }
                    });
                }
            });
        }

        mBadTextDialog.show();
    }

    // Dialog Create
    private Dialog createDialog(int tag){
        View innerView=null;
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        String[] reviewtxt=null;

        if(tag == GOODTEXT){
            innerView = getLayoutInflater().inflate(R.layout.v_goodcheckbox_a_reviewregi,null);
            reviewtxt = getResources().getStringArray(R.array.goodtext_review_regi);
            mGoodCheckBoxs = new CheckBox[]{(CheckBox)innerView.findViewById(R.id.checkbox1),(CheckBox)innerView.findViewById(R.id.checkbox2),(CheckBox)innerView.findViewById(R.id.checkbox3),(CheckBox)innerView.findViewById(R.id.checkbox4),(CheckBox)innerView.findViewById(R.id.checkbox5)
                    ,(CheckBox)innerView.findViewById(R.id.checkbox6),(CheckBox)innerView.findViewById(R.id.checkbox7),(CheckBox)innerView.findViewById(R.id.checkbox8),(CheckBox)innerView.findViewById(R.id.checkbox9),(CheckBox)innerView.findViewById(R.id.checkbox10),
                    (CheckBox)innerView.findViewById(R.id.checkbox11),(CheckBox)innerView.findViewById(R.id.checkbox12),(CheckBox)innerView.findViewById(R.id.checkbox13),(CheckBox)innerView.findViewById(R.id.checkbox14),(CheckBox)innerView.findViewById(R.id.checkbox15)};

            // checkbox 내용 입력
            for(int i=0; i< mGoodCheckBoxs.length; i++){
                mGoodCheckBoxs[i].setText(reviewtxt[i]);
            }

            ab.setTitle("칭찬해주세요.");
        }else if(tag == BADTEXT){
            innerView = getLayoutInflater().inflate(R.layout.v_goodcheckbox_a_reviewregi,null);
            reviewtxt = getResources().getStringArray(R.array.badtext_review_regi);
            mBadCheckBoxs = new CheckBox[]{(CheckBox)innerView.findViewById(R.id.checkbox1),(CheckBox)innerView.findViewById(R.id.checkbox2),(CheckBox)innerView.findViewById(R.id.checkbox3),(CheckBox)innerView.findViewById(R.id.checkbox4),(CheckBox)innerView.findViewById(R.id.checkbox5)
                    ,(CheckBox)innerView.findViewById(R.id.checkbox6),(CheckBox)innerView.findViewById(R.id.checkbox7),(CheckBox)innerView.findViewById(R.id.checkbox8),(CheckBox)innerView.findViewById(R.id.checkbox9),(CheckBox)innerView.findViewById(R.id.checkbox10),
                    (CheckBox)innerView.findViewById(R.id.checkbox11),(CheckBox)innerView.findViewById(R.id.checkbox12),(CheckBox)innerView.findViewById(R.id.checkbox13),(CheckBox)innerView.findViewById(R.id.checkbox14),(CheckBox)innerView.findViewById(R.id.checkbox15)};

            // checkbox 내용 입력
            for(int i=0; i< mBadCheckBoxs.length; i++){
                mBadCheckBoxs[i].setText(reviewtxt[i]);
            }

            ab.setTitle("비판해주세요.");
        }

        ab.setView(innerView);

        ab.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        ab.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return ab.create();
    }

    // 직접입력 더하기 버튼
    public void textClick_review_regi(View v){
        if(direct_text.getVisibility() == View.INVISIBLE) {
            textReviewBtn.setBackgroundResource(R.drawable.btn_remove_cricle);
            direct_text_logo.setVisibility(View.VISIBLE);
            direct_text.setVisibility(View.VISIBLE);
        }
        else{
            textReviewBtn.setBackgroundResource(R.drawable.btn_add_circle);
            direct_text_logo.setVisibility(View.INVISIBLE);
            direct_text.setVisibility(View.INVISIBLE);
        }
    }
}
