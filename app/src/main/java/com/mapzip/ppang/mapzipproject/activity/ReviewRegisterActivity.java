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
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import com.mapzip.ppang.mapzipproject.network.MapzipRequestBuilder;
import com.mapzip.ppang.mapzipproject.network.MapzipResponse;
import com.mapzip.ppang.mapzipproject.network.MyVolley;
import com.mapzip.ppang.mapzipproject.network.NetworkUtil;
import com.mapzip.ppang.mapzipproject.network.ResponseUtil;

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
    private final String TAG = "ReviewRegisterActivity";

    private final int GOODTEXT = 1;
    private final int BADTEXT = 2;


    private static final int PICK_FROM_CAMERA = 0;  // onActivityResult()에서 사용할 리퀘스트 코드
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;

    // state
    private int state = 0; // 0: default review enroll, 1: modify review
    private String primap_id;

    int flag;
    private boolean DEBUG;

    // toast
    private View layout_toast;
    private TextView text_toast;

    // user Data
    private UserData user;

    // UI
    private ImageView flag_image;
    private TextView titleText;
    private TextView addressText;
    private TextView contactText;
    private Button enrollBtn;
    private Button modifyBtn;
    //

    Intent flagIntent;      // 깃발정보를 보내줄 flag

    private Uri mImageCaptureUri;
    private ImageView mPhotoImageView;  // 이미지 경로랑 뷰


    // Image View
    private ViewPager viewPager;
    private List<Bitmap> oPerlishArray; // Image List
    private Bitmap[] bitarr; // Image array, oPerlishArray.toArray(bitarr)
    private ImageAdapter imageadapter;
    private Bitmap noimage;

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
    private ArrayList<String> flagspinnerList;
    private Spinner mapspinner;
    private Spinner flagspinner;
    private ArrayAdapter mapadapter;
    private ArrayAdapter flagadapter;

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
        if (getIntent().getStringExtra("state").equals("modify") == true) {
            state = 1;
            try {
                reviewData = user.getReviewData().clone();
            } catch (Exception ex) {
                Log.e(TAG, "clone ex");
            }

            primap_id = reviewData.getMapid();
        } else
            state = 0;

        if (state == 0) {
            enrollBtn.setVisibility(View.VISIBLE);
            modifyBtn.setVisibility(View.GONE);
        } else {
            enrollBtn.setVisibility(View.GONE);
            modifyBtn.setVisibility(View.VISIBLE);
        }
        Log.v("state", String.valueOf(state));

        // setting reviewData to send
        reviewData.setStore_x(getIntent().getDoubleExtra("store_x", 0));
        reviewData.setStore_y(getIntent().getDoubleExtra("store_y", 0));
        reviewData.setStore_name(getIntent().getStringExtra("store_name"));
        reviewData.setFlag_type(getIntent().getIntExtra("flag_type",0));
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

        if (state == 0) { // in enroll
            // no Image
            //noimage = drawableToBitmap(getResources().getDrawable(R.drawable.noimage));
            noimage = BitmapFactory.decodeResource(getResources(), R.drawable.noimage);
            //여기서 아웃오브 메모리 한번났어용

            oPerlishArray.add(noimage);

            bitarr = new Bitmap[oPerlishArray.size()];
            oPerlishArray.toArray(bitarr); // fill the array
            user.inputGalImages(bitarr);
        }
        imageadapter = new ImageAdapter(this, SystemMain.TYPE_USER);
        viewPager.setAdapter(imageadapter);

        /*
         *  map spinner
         */

        // flag spinner

        flagspinnerList = new ArrayList<String>();
        try{

            /*    for(int i=0;i<2; i++ ){         // 나중에 지도와 함께 flag 종류 함께 수정할 수 있도록
                      flagspinnerList.add()                         //
                }*/
            flagspinnerList.add("flag1");
            flagspinnerList.add("flag2");
            flagspinnerList.add("flag3");
        } catch( Exception ex){
            Log.v(TAG, "JSON ex review_regi_mapspinner");
        }

        flagspinner = (Spinner) findViewById(R.id.spinner_review2);
        flagadapter = new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,flagspinnerList);
        flagadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        flagspinner.setAdapter(flagadapter);

        flagspinner.setSelection(user.getReviewData().getFlag_type());

        flagspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position){

                    case 0:{

                        flag_image.setImageResource(R.drawable.ic_pin_01);
                        break;

                    }
                    case 1:{


                        flag_image.setImageResource(R.drawable.ic_pin_02);
                        break;
                    }
                    case 2:{

                        flag_image.setImageResource(R.drawable.ic_pin_03);
                        break;
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        flag_image= (ImageView) findViewById(R.id.flag_image);

        switch (user.getReviewData().getFlag_type()){

            case 0:{

                flag_image.setImageResource(R.drawable.ic_pin_01);
                break;

            }
            case 1:{


                flag_image.setImageResource(R.drawable.ic_pin_02);
                break;
            }
            case 2:{

                flag_image.setImageResource(R.drawable.ic_pin_03);
                break;
            }
        }

        //

        // get map name
        mapsppinerList = new ArrayList<String>();
        try {
            for (int i = 0; i < user.getMapmetaArray().length(); i++) {
                mapsppinerList.add(user.getMapmetaArray().getJSONObject(i).getString(NetworkUtil.MAP_TITLE));
            }
        } catch (JSONException ex) {
            Log.v(TAG, "JSON ex review_regi_mapspinner");
        }

        // set map spinner
        mapspinner = (Spinner) findViewById(R.id.spinner_review);
        mapadapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, mapsppinerList);
        mapadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mapspinner.setAdapter(mapadapter);
        if (state == 1) // in modify
            mapspinner.setSelection(Integer.parseInt(reviewData.getMapid()) - 1);

        // map select
        mapspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    // get map id to send
                    JSONObject mapmeta = null;
                    mapmeta = user.getMapmetaArray().getJSONObject(position);
                    reviewData.setMapid(mapmeta.get(NetworkUtil.MAP_ID).toString());
                } catch (JSONException ex) {
                    Log.v(TAG, "JSON ex review_regi_mapspinner2");
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
        if (state == 1)  // in modify
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
        if (state == 1) {
            // 리뷰: 좋은말, 나쁜말.
            if (reviewData.getGood_text().equals("null"))
                good_text.setText("");
            else
                good_text.setText(reviewData.getGood_text());

            if (reviewData.getBad_text().equals("null"))
                bad_text.setText("");
            else
                bad_text.setText(reviewData.getBad_text());

            // 직접입력 있을 때
            if (reviewData.getReview_text().length() != 0) {
                direct_text.setText(reviewData.getReview_text());
                direct_text.setVisibility(View.VISIBLE);
                direct_text_logo.setVisibility(View.VISIBLE);
                textReviewBtn.setBackgroundResource(R.drawable.btn_remove_circle);
            }
        }
    }

    private void doTakePhoto(){

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));

        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);

        startActivityForResult(intent,PICK_FROM_CAMERA);

    }

    private void doTakeAlbumAction(){
        Intent intent =new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent,PICK_FROM_ALBUM);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    //  onResult - findImageonClick
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if(resultCode != RESULT_OK)
            return;

        switch (requestCode) {


            case PICK_FROM_ALBUM:
            {
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

                    if (cursor != null && cursor.moveToFirst()) {
                        orientationDegree = cursor.getInt(cursor.getColumnIndex(orientationColumn[0]));
                    }

                    Bitmap rotated_resized_image_bitmap = rotateBitmapImage(resized_image_bitmap, orientationDegree);

                    oPerlishArray.add(rotated_resized_image_bitmap);
                    bitarr = new Bitmap[oPerlishArray.size()];
                    oPerlishArray.toArray(bitarr);

                    // save Image in user Data
                    if (state == 1) // in modify
                    {
                        if ((reviewData.getImage_num() + afterimagenum) == 0)
                            user.inputGalImages(bitarr);
                        else
                            user.addGalImages(bitarr);

                        modifyedcheck = true;
                    } else {
                        user.inputGalImages(bitarr);
                    }

                    afterimagenum++;
                    serverchoice = 2;
                    imageadapter = new ImageAdapter(this, SystemMain.TYPE_USER);
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




                break;
            }
            case PICK_FROM_CAMERA: {

                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");

                intent.putExtra("outputX", 90);
                intent.putExtra("outputY", 90);
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, CROP_FROM_CAMERA);

                break;
            }
            case CROP_FROM_CAMERA :
            {
                // 크롭이 된 이후의 이미지를 넘겨 받습니다.
                // 이미지뷰에 이미지를 보여준다거나 부가적인 작업 이후에
                // 임시 파일을 삭제합니다.
                final Bundle extras = data.getExtras();

                if (extras != null) {
                    Bitmap photo = extras.getParcelable("data");
                    mPhotoImageView.setImageBitmap(photo);
                }

                // 임시 파일 삭제
                File f = new File(mImageCaptureUri.getPath());
                if (f.exists()) {
                    f.delete();
                }

                break;
            }
        }

    }

    // in enroll Btn
    public void DoReviewset(View v) {

        reviewData.setFlag_type(flagspinner.getSelectedItemPosition());
        if(DEBUG){
            Log.i("flagspinner : ",""+flagspinner.getSelectedItemPosition());
        }
        Toast.makeText(getApplicationContext(),"flag :"+flagspinner.getSelectedItemPosition(),Toast.LENGTH_SHORT).show();


        if (direct_text.getVisibility() == View.INVISIBLE) {
            reviewData.setReview_text("");
        } else {
            reviewData.setReview_text(direct_text.getText().toString());
        }
        reviewData.setGood_text(good_text.getText().toString());
        reviewData.setBad_text(bad_text.getText().toString());

        if (serverchoice == 2)
            reviewData.setImage_num(user.getGalImages().length);

        RequestQueue queue = MyVolley.getInstance(this).getRequestQueue();
        MapzipRequestBuilder builder = null;
        try {

            builder = new MapzipRequestBuilder();
            builder.setCustomAttribute(NetworkUtil.USER_ID, user.getUserID());
            builder.setCustomAttribute(NetworkUtil.MAP_ID, reviewData.getMapid());
            builder.setCustomAttribute(NetworkUtil.USER_NAME, user.getUserName());
            builder.setCustomAttribute(NetworkUtil.REVIEW_DATA_STORE_X, reviewData.getStore_x());
            builder.setCustomAttribute(NetworkUtil.REVIEW_DATA_STORE_Y, reviewData.getStore_y());
            builder.setCustomAttribute(NetworkUtil.REVIEW_DATA_STORE_NAME, reviewData.getStore_name());
            builder.setCustomAttribute(NetworkUtil.REVIEW_DATA_STORE_ADDRESS, reviewData.getStore_address());
            builder.setCustomAttribute(NetworkUtil.REVIEW_DATA_STORE_CONTACT, reviewData.getStore_contact());
            builder.setCustomAttribute(NetworkUtil.REVIEW_DATA_EMOTION, reviewData.getReview_emotion());
            builder.setCustomAttribute(NetworkUtil.REVIEW_DATA_TEXT, reviewData.getReview_text());
            builder.setCustomAttribute(NetworkUtil.REVIEW_DATA_IMAGE_NUM, reviewData.getImage_num());
            builder.setCustomAttribute(NetworkUtil.REVIEW_DATA_GU_NUM, reviewData.getGu_num());
            builder.setCustomAttribute(NetworkUtil.REVIEW_DATA_POSITIVE_TEXT, reviewData.getGood_text());
            builder.setCustomAttribute(NetworkUtil.REVIEW_DATA_NEGATIVE_TEXT, reviewData.getBad_text());
            builder.setCustomAttribute(NetworkUtil.REVIEW_DATA_FLAG_TYPE, reviewData.getFlag_type());
            builder.showInside();

        } catch (JSONException e) {
            Log.e(TAG, "제이손 에러");
        }

        JsonObjectRequest myReq = new JsonObjectRequest(Request.Method.POST,
                SystemMain.SERVER_REVIEWENROLL_URL,
                builder.build(),
                createMyReqSuccessListener(),
                createMyReqErrorListener()) {
        };
        queue.add(myReq);
    }

    // in enroll Btn -> response, 이미지 있을때
    public void MakeImageDir() {
        RequestQueue queue = MyVolley.getInstance(this).getRequestQueue();
        MapzipRequestBuilder builder = null;
        try {
            builder = new MapzipRequestBuilder();
            builder.setCustomAttribute(NetworkUtil.USER_ID, user.getUserID());
            builder.setCustomAttribute(NetworkUtil.MAP_ID, reviewData.getMapid());
            builder.setCustomAttribute(NetworkUtil.STORE_ID, reviewData.getStore_id());
            builder.showInside();

        } catch (JSONException e) {
            Log.e(TAG, "제이손 에러");
        }

        JsonObjectRequest myReq = new JsonObjectRequest(Request.Method.POST,
                SystemMain.SERVER_REVIEWENROLL2_URL,
                builder.build(),
                createMyReqSuccessListener_makeDir(),
                createMyReqErrorListener()) {
        };
        queue.add(myReq);
    }

    // in modify Btn
    public void DoModifyset(View v) {
        if(DEBUG){
            Log.i("flagspinner : ",""+flagspinner.getSelectedItemPosition());
        }

        if (direct_text.getVisibility() == View.INVISIBLE) {
            reviewData.setReview_text("");
        } else {
            reviewData.setReview_text(direct_text.getText().toString());
        }
        reviewData.setGood_text(good_text.getText().toString());
        reviewData.setBad_text(bad_text.getText().toString());

        reviewData.setImage_num(reviewData.getImage_num() + afterimagenum);
        reviewData.setFlag_type(flagspinner.getSelectedItemPosition());

        RequestQueue queue = MyVolley.getInstance(this).getRequestQueue();
        MapzipRequestBuilder builder = null;
        try {
            builder = new MapzipRequestBuilder();
            builder.setCustomAttribute(NetworkUtil.USER_ID, user.getUserID());
            builder.setCustomAttribute(NetworkUtil.MAP_ID, reviewData.getMapid());
            builder.setCustomAttribute(NetworkUtil.REVIEW_DATA_EMOTION, reviewData.getReview_emotion());
            builder.setCustomAttribute(NetworkUtil.REVIEW_DATA_TEXT, reviewData.getReview_text());
            builder.setCustomAttribute(NetworkUtil.REVIEW_DATA_POSITIVE_TEXT, reviewData.getGood_text());
            builder.setCustomAttribute(NetworkUtil.REVIEW_DATA_NEGATIVE_TEXT, reviewData.getBad_text());
            builder.setCustomAttribute(NetworkUtil.REVIEW_DATA_IMAGE_NUM, reviewData.getImage_num());
            builder.setCustomAttribute(NetworkUtil.REVIEW_DATA_FLAG_TYPE, reviewData.getFlag_type());

            builder.setCustomAttribute(NetworkUtil.STORE_ID, getIntent().getStringExtra("store_id"));
            builder.showInside();

        } catch (JSONException e) {
            Log.e(TAG, "제이손 에러");
        }



        JsonObjectRequest myReq = new JsonObjectRequest(Request.Method.POST,
                SystemMain.SERVER_REVIEWMODIFY_URL,
                builder.build(),
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
                try {
                    MapzipResponse mapzipResponse = new MapzipResponse(response);
                    mapzipResponse.showAllContents();
                    if (mapzipResponse.getState(ResponseUtil.PROCESS_REVIEW_UPDATE)) { // 607





                        // if Map Id modified (지도 변경시)
                        if (primap_id.equals(reviewData.getMapid()) == false) {
                            int pmap_id = Integer.parseInt(primap_id); // 수정 전
                            int nmap_id = Integer.parseInt(reviewData.getMapid()); // 수정 후 map_id
                            int gu_num = reviewData.getGu_num();
                            int noReviewCheck = 0; // 0: 리뷰있음 1: no review

                            if (user.getPingCount(nmap_id, gu_num) == 0) { // no review check in now map
                                int checknonzero = 0;
                                for (int c = 1; c <= SystemMain.SeoulGuCount; c++) {
                                    if (user.getPingCount(nmap_id, c) != 0) {
                                        checknonzero = 1;
                                        break;
                                    }
                                }
                                if (checknonzero == 0)
                                    noReviewCheck = 1;
                            }

                            // mapforpinNum, PingCount modify, if review count is 0
                            user.setReviewCount(pmap_id, gu_num, user.getPingCount(pmap_id, gu_num) - 1);
                            user.setReviewCount(nmap_id, gu_num, user.getPingCount(nmap_id, gu_num) + 1);

                            if (user.getPingCount(pmap_id, gu_num) == 0) { // no review check in primap
                                int checknonzero = 0;
                                for (int c = 1; c <= SystemMain.SeoulGuCount; c++) {
                                    if (user.getPingCount(pmap_id, c) != 0) {
                                        checknonzero = 1;
                                        break;
                                    }
                                }
                                if (checknonzero == 0)
                                    user.setMapforpinNum(pmap_id, 2);
                            }

                            // map Image reload
                            user.setMapImage(pmap_id, res);
                            user.setMapImage(nmap_id, res);
                            user.setMapmetaNum(1);

                            // mapforpinArray modify
                            JSONArray farray = user.getMapforpinArray(pmap_id); // 이전 지도에서 리뷰디테일 삭제
                            JSONObject moveobj = new JSONObject();              //
                            for (int i = 0; i < farray.length(); i++) {
                                if (farray.getJSONObject(i).getString("store_id").equals(reviewData.getStore_id()) == true) {
                                    moveobj = farray.getJSONObject(i);
                                    farray = removeJsonObjectAtJsonArrayIndex(farray, i);
                                }
                            }
                            user.setMapforpinArray(farray, pmap_id);

                            if (noReviewCheck == 1) { // 옮길 지도에 리뷰 정보가 없었을때
                                JSONArray sarray = new JSONArray();
                                sarray.put(sarray.length(), moveobj);
                                user.setMapforpinArray(sarray, nmap_id);
                                user.setMapforpinNum(nmap_id, 1);
                            } else { // 리뷰 정보가 있었을때
                                if (user.getMapforpinNum(nmap_id) != 0) {
                                    JSONArray sarray = user.getMapforpinArray(nmap_id);
                                    sarray.put(sarray.length(), moveobj);
                                    user.setMapforpinArray(sarray, nmap_id);
                                }
                            }
                            user.setMapRefreshLock(true);
                        }

                        if (modifyedcheck == true) {
                            Log.v("리뷰수정", "OK");
                            if ((reviewData.getImage_num() - afterimagenum) == 0)
                                MakeImageDir();
                            else {
                                serverchoice = 2;
                                loading.execute();
                                // 2번째통신 이미지갯수만큼 반복
                            }
                        } else {
                            // toast

                            text_toast.setText("리뷰가 수정되었습니다. ");

                            Toast toast = new Toast(getApplicationContext());
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(layout_toast);
                            toast.show();



                            Resources res;
                            res = getResources();
                            int mapid = Integer.parseInt(reviewData.getMapid());

                            JSONArray narray = user.getMapforpinArray(mapid);
                            for(int i=0; i<narray.length(); i++){
                                if(narray.getJSONObject(i).getString(NetworkUtil.STORE_ID).equals(reviewData.getStore_id()) == true){
                                    narray.getJSONObject(i).put(NetworkUtil.REVIEW_DATA_FLAG_TYPE,reviewData.getFlag_type());
                                }
                            }



                            user.setMapforpinArray(narray, mapid);
                            user.setMapRefreshLock(false);




                            //

                            //

                            finish();





                        }
                    }
                } catch (JSONException e) {
                    Log.e(TAG,"제이손 에러");
                }
            }
        };
    }

    // for enroll response
    private Response.Listener<JSONObject> createMyReqSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    MapzipResponse mapzipResponse = new MapzipResponse(response);
                    mapzipResponse.showAllContents();
                    if (mapzipResponse.getState(ResponseUtil.PROCESS_REVIEW_ENROLL)) { // 601
                        // 1번째 통신 성공
                        Log.v("리뷰저장", "OK");
                        reviewData.setStore_id(mapzipResponse.getFieldsString(NetworkUtil.STORE_ID));
                        if (reviewData.getImage_num() != 0)
                            MakeImageDir(); // 이미지 있으면 2번째 통신 시작
                        else {
                            serverchoice = 1; // no image
                            loading.execute();
                        }
                    } else if (!mapzipResponse.getState(ResponseUtil.PROCESS_REVIEW_ENROLL)) { // 612
                        //1번째 통신에서 중복가게 걸러내기
                        // toast
                        text_toast.setText("이미 등록 된 가게입니다.");
                        Toast toast = new Toast(getApplicationContext());
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(layout_toast);
                        toast.show();
                    } else if (mapzipResponse.getState(ResponseUtil.PROCESS_REVIEW_MAKE_IMG_DIR)) { // 602 || 621
                        // 2번째통신 성공
                        Log.v("리뷰저장2", "OK");
                        serverchoice = 2;
                        loading.execute();
                        // 3번째통신 이미지갯수만큼 반복
                    } else{
                        // toast
                        text_toast.setText("다시 시도해주세요.");
                        Toast toast = new Toast(getApplicationContext());
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(layout_toast);
                        toast.show();
                    }
                } catch (JSONException ex) {
                    Log.e(TAG, "제이손 에러");
                }
            }
        };
    }


    // for enroll response
    private Response.Listener<JSONObject> createMyReqSuccessListener_makeDir() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    MapzipResponse mapzipResponse = new MapzipResponse(response);
                    mapzipResponse.showAllContents();
                    if (mapzipResponse.getState(ResponseUtil.PROCESS_REVIEW_MAKE_IMG_DIR)) { // 602 || 621
                        // 2번째통신 성공
                        Log.v("리뷰저장2", "OK");
                        serverchoice = 2;
                        loading.execute();
                        // 3번째통신 이미지갯수만큼 반복
                    } else{
                        // toast
                        text_toast.setText("다시 시도해주세요.");
                        Toast toast = new Toast(getApplicationContext());
                        toast.setDuration(Toast.LENGTH_SHORT);
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
        String image = getStringImage(user.getGalImages()[i]);

        RequestQueue queue = MyVolley.getInstance(this).getRequestQueue();
        MapzipRequestBuilder builder = null;
        try {
            builder = new MapzipRequestBuilder();
            builder.setCustomAttribute(NetworkUtil.USER_ID, user.getUserID());
            builder.setCustomAttribute(NetworkUtil.MAP_ID, reviewData.getMapid());
            builder.setCustomAttribute(NetworkUtil.STORE_ID, reviewData.getStore_id());
            builder.setCustomAttribute(NetworkUtil.REVIEW_DATA_IMAGE_STRING, image);
            builder.setCustomAttribute(NetworkUtil.REVIEW_DATA_IMAGE_NAME,"image" + String.valueOf(imagenum));
            builder.showInside();
            imagenum++;
        } catch (JSONException e) {
            Log.e(TAG, "제이손 에러");
        }

        JsonObjectRequest myReq = new JsonObjectRequest(Request.Method.POST,
                SystemMain.SERVER_REVIEWENROLL3_URL,
                builder.build(),
                createMyReqSuccessListener_image(),
                createMyReqErrorListener_image()) {
        };
        queue.add(myReq);
    }

    private Response.Listener<JSONObject> createMyReqSuccessListener_image() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    MapzipResponse mapzipResponse = new MapzipResponse(response);
                    mapzipResponse.showAllContents();
                    if (mapzipResponse.getState(ResponseUtil.PROCESS_REVIEW_IMAGE_SEND)) {
                    } else{
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

    private Response.ErrorListener createMyReqErrorListener_image() {
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
                } catch (NullPointerException ex) {
                    // toast
                    Log.e(TAG, "nullpointexception");
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
    public Bitmap resizeBitmapImage(Uri image_uri, int maxWidth, int maxHeight) {

        String imagePath = getPathFromUri(image_uri);

        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inSampleSize = 2;
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
    public Bitmap rotateBitmapImage(Bitmap srcBmp, int degree) {

        int width = srcBmp.getWidth();
        int height = srcBmp.getHeight();

        Matrix matrix = new Matrix();
        matrix.postRotate(degree);

        Bitmap rotatedBmp = Bitmap.createBitmap(srcBmp, 0, 0, width, height, matrix, true);

        return rotatedBmp;
    }

    // get Image encoding
    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // Must compress the Image to reduce image size to make upload easy
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byte_arr = stream.toByteArray();
        // Encode Image to String
        String encodedImage = Base64.encodeToString(byte_arr, 0);

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
        } else if (good_text.getText().toString().trim().isEmpty() && bad_text.getText().toString().trim().isEmpty() && (direct_text.getText().toString().trim().isEmpty() || (direct_text.getVisibility() == View.INVISIBLE))) { // review text not selected
            // toast
            text_toast.setText("리뷰를 작성해주세요.");
            Toast toast = new Toast(getApplicationContext());
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout_toast);
            toast.show();

            return -1;
        } else {
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

            if (state == 0) {
                int tmp = user.getPingCount(Integer.parseInt(reviewData.getMapid()), reviewData.getGu_num());
                user.setReviewCount(Integer.parseInt(reviewData.getMapid()), reviewData.getGu_num(), tmp + 1);
                user.setMapImage(Integer.parseInt(reviewData.getMapid()), res);
            }

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Log.v(TAG, "loading success");
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (asyncDialog != null) {
                asyncDialog.dismiss();
            }
            Log.v(TAG, "loading finish");

            if (state == 0) {
                // toast
                text_toast.setText("리뷰가 등록되었습니다.");
                Toast toast = new Toast(getApplicationContext());
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(layout_toast);
                toast.show();
            } else {
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
        if (user.getGalImages().length >= SystemMain.MAXIMAGENUM) {
            // toast
            text_toast.setText("더이상 이미지를 추가할 수 없습니다.");
            Toast toast = new Toast(getApplicationContext());
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout_toast);
            toast.show();

            return;
        }

        DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener()
        {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                doTakePhoto();
            }
        };
        DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener()
        {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                doTakeAlbumAction();
            }
        };
        DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener()
        {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        };

        //
        new AlertDialog.Builder(this)
                .setTitle("업로드할 이미지 선택")
                .setPositiveButton("사진촬영",cameraListener)
                .setNeutralButton("앨범선택",albumListener)
                .setNegativeButton("취소",cancelListener)
                .show();

    }

    // 사진제거 버튼
    public void deleteImageonClick(View v) {
        if ((reviewData.getImage_num() + afterimagenum) == 0) { // 이미지가 없는 상태
            Log.v("imagenum", String.valueOf(reviewData.getImage_num()));
            Log.v("afterimagenum", String.valueOf(afterimagenum));
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

        if (fordelbitarr.length == 1) {
            // no Image
            noimage = drawableToBitmap(getResources().getDrawable(R.drawable.noimage));
            oPerlishArray.add(noimage);
        } else {
            for (int i = 0; i < fordelbitarr.length; i++)
                oPerlishArray.add(fordelbitarr[i]);
            oPerlishArray.remove(delposition);
        }
        bitarr = new Bitmap[oPerlishArray.size()];
        oPerlishArray.toArray(bitarr);
        user.inputGalImages(bitarr);

        //set imageadapter
        imageadapter = new ImageAdapter(this, SystemMain.TYPE_USER);
        viewPager.setAdapter(imageadapter);
        imageadapter.notifyDataSetChanged();

        afterimagenum--;

        if (afterimagenum == 0)
            oncreatelock = false;
        Log.v("afterimagenum_d", String.valueOf(afterimagenum));
    }

    // 리뷰등록 버튼
    public void enrollonClick_review_regi(View v) {
        if (reviewtextset() == 1) {



            DoReviewset(v); // 서버 통신




            user.setMapforpinNum(Integer.parseInt(reviewData.getMapid()), 0); // to review loading
        }
    }

    // 취소 버튼
    public void cancelonClick_review_regi(View v) {
        finish();
    }

    // 리뷰수정 버튼
    public void modifyonClick_review_regi(View v) {
        if (reviewtextset() == 1) {
            DoModifyset(v);
        }
    }

    // 좋은말 리뷰 더하기 버튼
    public void goodtextClick_review_regi(View v) {
        if (mGoodTextD_Created == false) {
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
    public void badtextClick_review_regi(View v) {
        if (mBadTextD_Created == false) {
            mBadTextDialog = createDialog(BADTEXT);
            mBadTextD_Created = true;
            mBadTextDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(final DialogInterface dialog) {
                    Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                    positiveButton.setOnClickListener(
                            new View.OnClickListener() {
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
    private Dialog createDialog(int tag) {
        View innerView = null;
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        String[] reviewtxt = null;

        if (tag == GOODTEXT) {
            innerView = getLayoutInflater().inflate(R.layout.v_textcheckbox_a_reviewregi, null);
            reviewtxt = getResources().getStringArray(R.array.goodtext_review_regi);
            mGoodCheckBoxs = new CheckBox[]{(CheckBox) innerView.findViewById(R.id.checkbox1), (CheckBox) innerView.findViewById(R.id.checkbox2), (CheckBox) innerView.findViewById(R.id.checkbox3), (CheckBox) innerView.findViewById(R.id.checkbox4), (CheckBox) innerView.findViewById(R.id.checkbox5)
                    , (CheckBox) innerView.findViewById(R.id.checkbox6), (CheckBox) innerView.findViewById(R.id.checkbox7), (CheckBox) innerView.findViewById(R.id.checkbox8), (CheckBox) innerView.findViewById(R.id.checkbox9), (CheckBox) innerView.findViewById(R.id.checkbox10),
                    (CheckBox) innerView.findViewById(R.id.checkbox11), (CheckBox) innerView.findViewById(R.id.checkbox12), (CheckBox) innerView.findViewById(R.id.checkbox13), (CheckBox) innerView.findViewById(R.id.checkbox14), (CheckBox) innerView.findViewById(R.id.checkbox15),
                    (CheckBox) innerView.findViewById(R.id.checkbox16), (CheckBox) innerView.findViewById(R.id.checkbox17), (CheckBox) innerView.findViewById(R.id.checkbox18)};

            // checkbox 내용 입력
            for (int i = 0; i < mGoodCheckBoxs.length; i++) {
                mGoodCheckBoxs[i].setText(reviewtxt[i]);
            }

            ab.setTitle("칭찬해주세요.");
        } else if (tag == BADTEXT) {
            innerView = getLayoutInflater().inflate(R.layout.v_textcheckbox_a_reviewregi, null);
            reviewtxt = getResources().getStringArray(R.array.badtext_review_regi);
            mBadCheckBoxs = new CheckBox[]{(CheckBox) innerView.findViewById(R.id.checkbox1), (CheckBox) innerView.findViewById(R.id.checkbox2), (CheckBox) innerView.findViewById(R.id.checkbox3), (CheckBox) innerView.findViewById(R.id.checkbox4), (CheckBox) innerView.findViewById(R.id.checkbox5)
                    , (CheckBox) innerView.findViewById(R.id.checkbox6), (CheckBox) innerView.findViewById(R.id.checkbox7), (CheckBox) innerView.findViewById(R.id.checkbox8), (CheckBox) innerView.findViewById(R.id.checkbox9), (CheckBox) innerView.findViewById(R.id.checkbox10),
                    (CheckBox) innerView.findViewById(R.id.checkbox11), (CheckBox) innerView.findViewById(R.id.checkbox12), (CheckBox) innerView.findViewById(R.id.checkbox13), (CheckBox) innerView.findViewById(R.id.checkbox14), (CheckBox) innerView.findViewById(R.id.checkbox15),
                    (CheckBox) innerView.findViewById(R.id.checkbox16), (CheckBox) innerView.findViewById(R.id.checkbox17), (CheckBox) innerView.findViewById(R.id.checkbox18)};

            // checkbox 내용 입력
            for (int i = 0; i < mBadCheckBoxs.length; i++) {
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
    public void textClick_review_regi(View v) {
        if (direct_text.getVisibility() == View.INVISIBLE) {
            textReviewBtn.setBackgroundResource(R.drawable.btn_remove_circle);
            direct_text_logo.setVisibility(View.VISIBLE);
            direct_text.setVisibility(View.VISIBLE);
        } else {
            textReviewBtn.setBackgroundResource(R.drawable.btn_add_circle);
            direct_text_logo.setVisibility(View.INVISIBLE);
            direct_text.setVisibility(View.INVISIBLE);
        }
    }
}
