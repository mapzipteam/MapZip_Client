package com.mapzip.ppang.mapzipproject.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mapzip.ppang.mapzipproject.R;
import com.mapzip.ppang.mapzipproject.adapter.ImageAdapter;
import com.mapzip.ppang.mapzipproject.model.FriendData;
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


/**
 * Created by Song  Ji won on 2015-07-31.
 */
public class ReviewActivity extends Activity {
    private final String TAG = "ReviewActivity";

    // toast
    private View layout_toast;
    private TextView text_toast;

    // user Data
    private UserData user;
    private FriendData fuser;
    private boolean userlock = false; // true = friend's, false = mine

    // review Data
    private ReviewData reviewData;
    private TextView hashtag;
    private ImageView review_emotion;
    private TextView store_name;
    private TextView good_text;
    private TextView bad_text;
    private TextView review_text;
    private TextView store_address;
    private TextView store_contact;

    // image
    private ImageAdapter imageadapter;
    private ViewPager viewPager;

    // Btn
    private Button modifyBtn;
    private Button deleteBtn;

    // Loading
   // private LoadingTask loading;
   // public ProgressDialog asyncDialog;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        getActionBar().hide();

        // toast
        LayoutInflater inflater = this.getLayoutInflater();
        layout_toast = inflater.inflate(R.layout.my_custom_toast, (ViewGroup) findViewById(R.id.custom_toast_layout));
        text_toast = (TextView) layout_toast.findViewById(R.id.textToShow);

        // user Data & friend check
        user = UserData.getInstance();
        fuser = FriendData.getInstance();

        if (getIntent().getStringExtra("fragment_id").equals("friend_home"))
            userlock = true;

        // hide Btn
        modifyBtn = (Button) findViewById(R.id.modifyBtn_review);
        deleteBtn = (Button) findViewById(R.id.deleteBtn_review);

        if(userlock == true){
            modifyBtn.setVisibility(View.GONE);
            deleteBtn.setVisibility(View.GONE);
        }else{
            modifyBtn.setVisibility(View.VISIBLE);
            deleteBtn.setVisibility(View.VISIBLE);
        }

        // review Data
        review_emotion = (ImageView) findViewById(R.id.emotion_review);
        hashtag = (TextView) findViewById(R.id.hashtag_review);
        store_name = (TextView) findViewById(R.id.name_review);
        review_text = (TextView) findViewById(R.id.text_review);
        good_text = (TextView) findViewById(R.id.goodtext_review);
        bad_text = (TextView) findViewById(R.id.badtext_review);
        store_address = (TextView) findViewById(R.id.address_text_review);
        store_contact = (TextView) findViewById(R.id.contact_text_review);

        // get review Data from user Data
        if (userlock == false) { // mine
            reviewData = user.getReviewData();
        } else { // friend's
            reviewData = fuser.getReviewData();
        }

        store_name.setText(reviewData.getStore_name());
        if(reviewData.getGood_text().equals("null"))
            good_text.setText("");
        else
            good_text.setText(reviewData.getGood_text());

        if(reviewData.getBad_text().equals("null"))
            bad_text.setText("");
        else
            bad_text.setText(reviewData.getBad_text());

        review_text.setText(reviewData.getReview_text());
        store_address.setText(reviewData.getStore_address());
        store_contact.setText(reviewData.getStore_contact());

        good_text.setSelected(true);
        bad_text.setSelected(true);

        // get hashtag from userData.mapmetaArray
        try {
            if (userlock == false) { // mine
                hashtag.setText(user.getMapmetaArray().getJSONObject(Integer.parseInt(reviewData.getMapid())-1).getString(NetworkUtil.MAP_HASH_TAG));
            } else { // friend's
                hashtag.setText(fuser.getMapmetaArray().getJSONObject(Integer.parseInt(reviewData.getMapid())-1).getString(NetworkUtil.MAP_HASH_TAG));
            }
        }catch (JSONException ex){
            Log.e(TAG,"JSONEX get hash in reviewActivity");
        }

        // set Emotion Image
        if (reviewData.getReview_emotion() < 20)
            review_emotion.setImageResource(R.drawable.emotion1);
        else if ((20 <= reviewData.getReview_emotion()) && (reviewData.getReview_emotion() < 40))
            review_emotion.setImageResource(R.drawable.emotion2);
        else if ((40 <= reviewData.getReview_emotion()) && (reviewData.getReview_emotion() < 60))
            review_emotion.setImageResource(R.drawable.emotion3);
        else if ((60 <= reviewData.getReview_emotion()) && (reviewData.getReview_emotion() < 80))
            review_emotion.setImageResource(R.drawable.emotion4);
        else
            review_emotion.setImageResource(R.drawable.emotion5);

        /*
         *  get & set Image
         */
        viewPager = (ViewPager) findViewById(R.id.pager_review);

        // set Imageadapter
        if(userlock == false)
            imageadapter = new ImageAdapter(getApplicationContext(), SystemMain.TYPE_USER);
        else
            imageadapter = new ImageAdapter(getApplicationContext(), SystemMain.TYPE_FRIEND);

        viewPager.setAdapter(imageadapter);
        imageadapter.notifyDataSetChanged();
    }

    // modify Btn
    public void modifyOnclick(View v) {
        Intent intent = new Intent(this, ReviewRegisterActivity.class);
        intent.putExtra("store_name", reviewData.getStore_name());
        intent.putExtra("store_address", reviewData.getStore_address());
        intent.putExtra("store_contact", reviewData.getStore_contact());
        intent.putExtra("store_x", reviewData.getStore_x());
        intent.putExtra("store_y", reviewData.getStore_y());
        intent.putExtra("flag_type",reviewData.getFlag_type());
        intent.putExtra("store_cx", 0);
        intent.putExtra("store_cy", 0);
        intent.putExtra("store_id", reviewData.getStore_id());
        intent.putExtra("state", "modify");
        startActivity(intent);

        finish();
    }

    // delete Btn
    public void deleteOnclick(View v){
        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(ReviewActivity.this);
        alert_confirm.setMessage("리뷰를 정말 삭제하시겠습니까?\n").setCancelable(false).setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RequestQueue queue = MyVolley.getInstance(ReviewActivity.this).getRequestQueue();

                        MapzipRequestBuilder builder = null;
                        try {
                            builder= new MapzipRequestBuilder();
                            builder.setCustomAttribute(NetworkUtil.USER_ID, user.getUserID());
                            builder.setCustomAttribute(NetworkUtil.MAP_ID, reviewData.getMapid());
                            builder.setCustomAttribute(NetworkUtil.STORE_ID, reviewData.getStore_id());
                            builder.showInside();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        JsonObjectRequest myReq = new JsonObjectRequest(Request.Method.POST,
                                SystemMain.SERVER_REVIEWDELETE_URL,
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

    // cancel Btn
    public void closeOnClick(View v){
        finish();
    }

    // pin을 삭제하는 리스너 , ReviewRegisterAcitivty 의 createMyReqSuccessListen_makedir 와는 다름.
    private Response.Listener<JSONObject> createMyReqSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    MapzipResponse mapzipResponse = new MapzipResponse(response);
                    mapzipResponse.showAllContents();
                    if (mapzipResponse.getState(ResponseUtil.PROCESS_REVIEW_DELETE)) { // 604
                        // toast
                        text_toast.setText("리뷰가 삭제되었습니다.");
                        Toast toast = new Toast(getApplicationContext());
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(layout_toast);
                        toast.show();

                        // for home fragment(map Image) refresh - @로딩 필요
                        Resources res;
                        res = getResources();
                        int mapid = Integer.parseInt(reviewData.getMapid());
                        int pingcount = user.getPingCount(mapid, reviewData.getGu_num());
                        user.setReviewCount(mapid, reviewData.getGu_num(), pingcount - 1);

                        if((pingcount-1) == 0){ // no reivew check
                            int checknonzero = 0;
                            for(int c=1; c<=SystemMain.SeoulGuCount; c++){
                                if(user.getPingCount(mapid,c) != 0){
                                    checknonzero = 1;
                                    break;
                                }
                            }
                            if(checknonzero == 0)
                                user.setMapforpinNum(mapid,2);
                        }

                        // map Image reload
                        user.setMapImage(mapid, res);
                        user.setMapmetaNum(1);

                        // for map activity(pin) refresh
                        JSONArray narray = user.getMapforpinArray(mapid);
                        for(int i=0; i<narray.length(); i++){
                            if(narray.getJSONObject(i).getString(NetworkUtil.STORE_ID).equals(reviewData.getStore_id()) == true){
                                narray = removeJsonObjectAtJsonArrayIndex(narray,i);
                            }
                        }
                        user.setMapforpinArray(narray, mapid);
                        user.setMapRefreshLock(false);

                        finish();
                    }else if(mapzipResponse.getState(ResponseUtil.PROCESS_REVIEW_UPDATE)){


                        // for home fragment(map Image) refresh - @로딩 필요
                        Resources res;
                        res = getResources();
                        int mapid = Integer.parseInt(reviewData.getMapid());
                        int pingcount = user.getPingCount(mapid, reviewData.getGu_num());
                        user.setReviewCount(mapid, reviewData.getGu_num(), pingcount - 1);

                        if((pingcount-1) == 0){ // no reivew check
                            int checknonzero = 0;
                            for(int c=1; c<=SystemMain.SeoulGuCount; c++){
                                if(user.getPingCount(mapid,c) != 0){
                                    checknonzero = 1;
                                    break;
                                }
                            }
                            if(checknonzero == 0)
                                user.setMapforpinNum(mapid,2);
                        }


                        // for map activity(pin) refresh
                        JSONArray narray = user.getMapforpinArray(mapid);
                        for(int i=0; i<narray.length(); i++){
                            if(narray.getJSONObject(i).getString(NetworkUtil.STORE_ID).equals(reviewData.getStore_id()) == true){
                                narray = removeJsonObjectAtJsonArrayIndex(narray,i);
                            }
                        }
                        user.setMapforpinArray(narray, mapid);
                        user.setMapRefreshLock(true);

                        finish();

                    }
                    else {
                        // toast
                        text_toast.setText("다시 시도해주세요.");
                        Toast toast = new Toast(getApplicationContext());
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
                    Toast toast = new Toast(getApplicationContext());
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout_toast);
                    toast.show();

                    Log.e(TAG, error.getMessage());
                }catch (NullPointerException ex) {
                    Log.e(TAG, "nullpointexception");
                }
            }
        };
    }

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
}
