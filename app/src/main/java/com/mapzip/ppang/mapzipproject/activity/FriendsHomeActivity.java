package com.mapzip.ppang.mapzipproject.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mapzip.ppang.mapzipproject.R;
import com.mapzip.ppang.mapzipproject.ScalableLayout.ScalableLayout;
import com.mapzip.ppang.mapzipproject.map.MapActivity;
import com.mapzip.ppang.mapzipproject.map.Location;
import com.mapzip.ppang.mapzipproject.model.FriendData;
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

import java.lang.reflect.Method;
import java.util.ArrayList;

public class FriendsHomeActivity extends Activity implements View.OnClickListener {
    private final String TAG = "FriendsHomeActivity";

    private FriendData fuser;
    private UserData user;

    // toast
    private View layout_toast;
    private TextView text_toast;

    private double loc_LNG = 0;
    private double loc_LAT = 0;

    private ImageView imageview; // map image
    private String hashString;
    private TextView hashstate1; // hashtag
    private TextView hashstate2; // hashtag
    private TextView hashstate3; // hashtag
    private TextView hashstate4; // hashtag
    private TextView hashstate5; // hashtag
    private String mapid; // 현재 지도 pid값

    private ArrayList<String> sppinerList; // map name
    private Spinner spinner;
    private ArrayAdapter adapter;
    private int mapnum; // map num

    private Button mapsetting;
    
    public int realWidth; //ScreenSize width
    public int realHeight;//ScreenSize height

    // Seoul Btn
    private Button DoBong;
    private Button NoWon;
    private Button GangBuk;
    private Button SungBuk;
    private Button ZongRang;
    private Button EunPhung;
    private Button ZongRo;
    private Button DongDaeMon;
    private Button SuDaeMon;
    private Button Zhong;
    private Button SungDong;
    private Button GangZin;
    private Button GangDong;
    private Button MaPho;
    private Button YongSan;
    private Button GangSue;
    private Button YangChen;
    private Button GuRo;
    private Button YongDengPo;
    private Button DongJack;
    private Button GemChun;
    private Button GanAk;
    private Button SeoCho;
    private Button GangNam;
    private Button SongPa;


    //2016.01.08 송지원이 고침
    private ScalableLayout scalableLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView((R.layout.fragment_home));

        fuser = FriendData.getInstance();
        user = UserData.getInstance();
        getActionBar().setTitle("   "+fuser.getUserName() + "의 지도");
        getActionBar().setDisplayShowHomeEnabled(false);

        mapnum = fuser.getMapmetaArray().length();
        sppinerList = new ArrayList<String>();
        try {
            for (int i = 0; i < mapnum; i++) {
                sppinerList.add(fuser.getMapmetaArray().getJSONObject(i).getString(NetworkUtil.MAP_TITLE));
            }
        } catch (JSONException ex) {

        }

        LayoutInflater inflater = this.getLayoutInflater();
        layout_toast = inflater.inflate(R.layout.my_custom_toast, (ViewGroup) findViewById(R.id.custom_toast_layout));
        text_toast = (TextView) layout_toast.findViewById(R.id.textToShow);

        //  topstate = (TextView) findViewById(R.id.topstate);
        //  topstate.setText(fuser.getfuserName());
       /*topstate.append(" (");
        topstate.append(fuser.getfuserID());
        topstate.append(")");*/
        //  topstate.append("의 지도");

        //2016.01.08 송지원이 고침
        scalableLayout = (ScalableLayout)findViewById(R.id.scalablelayout);

        imageview = (ImageView) findViewById(R.id.mapimage);
        hashstate1 = (TextView) findViewById(R.id.tagText);
        hashstate2 = (TextView) findViewById(R.id.tagText2);
        hashstate3 = (TextView) findViewById(R.id.tagText3);
        hashstate4 = (TextView) findViewById(R.id.tagText4);
        hashstate5 = (TextView) findViewById(R.id.tagText5);
        mapsetting = (Button) findViewById(R.id.mapsetting);

        if(user.getUserID().equals(fuser.getUserID()))
            mapsetting.setVisibility(View.GONE);
        else
            addFriend_search();

        mapsetting.setBackgroundResource(R.drawable.addfriend2);
        mapsetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriend_enroll(v);
            }
        });

        // Seoul Btn init
        DoBong = (Button) findViewById(R.id.DoBong);
        NoWon = (Button) findViewById(R.id.NoWon);
        GangBuk = (Button) findViewById(R.id.GangBuk);
        SungBuk = (Button) findViewById(R.id.SungBuk);
        ZongRang = (Button) findViewById(R.id.ZongRang);
        EunPhung = (Button) findViewById(R.id.EunPhung);
        ZongRo = (Button) findViewById(R.id.ZongRo);
        DongDaeMon = (Button) findViewById(R.id.DongDaeMon);
        SuDaeMon = (Button) findViewById(R.id.SuDaeMon);
        Zhong = (Button) findViewById(R.id.Zhong);
        SungDong = (Button) findViewById(R.id.SungDong);
        GangZin = (Button) findViewById(R.id.GangZin);
        GangDong = (Button) findViewById(R.id.GangDong);
        MaPho = (Button) findViewById(R.id.MaPho);
        YongSan = (Button) findViewById(R.id.YongSan);
        GangSue = (Button) findViewById(R.id.GangSue);
        YangChen = (Button) findViewById(R.id.YangChen);
        GuRo = (Button) findViewById(R.id.GuRo);
        YongDengPo = (Button) findViewById(R.id.YongDengPo);
        DongJack = (Button) findViewById(R.id.DongJack);
        GemChun = (Button) findViewById(R.id.GemChun);
        GanAk = (Button) findViewById(R.id.GanAk);
        SeoCho = (Button) findViewById(R.id.SeoCho);
        GangNam = (Button) findViewById(R.id.GangNam);
        SongPa = (Button) findViewById(R.id.SongPa);

        // map name
        spinner = (Spinner) findViewById(R.id.spinner);
        adapter = new ArrayAdapter(this,R.layout.spinner_centerhorizontal, sppinerList);
        adapter.setDropDownViewResource(R.layout.spinner_centerhorizontal);
        spinner.setAdapter(adapter);

        // map select
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    RelativeLayout.LayoutParams imageLayout = new RelativeLayout.LayoutParams(realWidth, realHeight / 5 * 3); // width, height
                    imageLayout.setMargins(0, realHeight / 10, 0, 0);
                    imageview.setLayoutParams(imageLayout);
                    RelativeLayout.LayoutParams tagLayout = new RelativeLayout.LayoutParams(realWidth, realHeight / 18);// width, height
                    tagLayout.setMargins(10, (int) realHeight / 24 * 17, 10, 0);
                    //hashtaglayout.setLayoutParams(tagLayout);

                    JSONObject mapmeta = fuser.getMapmetaArray().getJSONObject(position);
                    mapid = mapmeta.get(NetworkUtil.MAP_ID).toString();

                    Bitmap result = fuser.getResult(Integer.parseInt(mapid));
//2016.01.08        imageview.setImageBitmap(result);
                    imageview.setEnabled(false);
                    Drawable drawable = new BitmapDrawable(result);
                    scalableLayout.setBackground(drawable);

                    hashString =  mapmeta.get(NetworkUtil.MAP_HASH_TAG).toString();
                    String[] hasharr = hashString.split("#");

                    for (int i = 1; i < hasharr.length; i++) {
                        switch (i) {
                            case 1:
                                hashstate1.setText(hasharr[i]);
                                break;
                            case 2:
                                hashstate2.setText(hasharr[i]);
                                break;
                            case 3:
                                hashstate3.setText(hasharr[i]);
                                break;
                            case 4:
                                hashstate4.setText(hasharr[i]);
                                break;
                            case 5:
                                hashstate5.setText(hasharr[i]);
                                break;
                        }
                    }

                    // category select (SEOUL)
                    if (Integer.parseInt(mapmeta.get(NetworkUtil.MAP_CATEGORY).toString()) == SystemMain.SEOUL_MAP_NUM) {
                        seoulBtnVisibility("visible", mapid);
                    }
                } catch (JSONException ex) {
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Seoul Btn onClick
        DoBong.setOnClickListener(this);
        NoWon.setOnClickListener(this);
        GangBuk.setOnClickListener(this);
        SungBuk.setOnClickListener(this);
        ZongRang.setOnClickListener(this);
        EunPhung.setOnClickListener(this);
        ZongRo.setOnClickListener(this);
        DongDaeMon.setOnClickListener(this);
        SuDaeMon.setOnClickListener(this);
        Zhong.setOnClickListener(this);
        SungDong.setOnClickListener(this);
        GangZin.setOnClickListener(this);
        GangDong.setOnClickListener(this);
        MaPho.setOnClickListener(this);
        YongSan.setOnClickListener(this);
        GangSue.setOnClickListener(this);
        YangChen.setOnClickListener(this);
        GuRo.setOnClickListener(this);
        YongDengPo.setOnClickListener(this);
        DongJack.setOnClickListener(this);
        GemChun.setOnClickListener(this);
        GanAk.setOnClickListener(this);
        SeoCho.setOnClickListener(this);
        GangNam.setOnClickListener(this);
        SongPa.setOnClickListener(this);

        imageview.post(new Runnable() {

            @Override
            public void run() {

                int[] location = new int[2];
                imageview.getLocationOnScreen(location);

                ScreenSize();

                //은평
                ScalableLayout.LayoutParams layoutParams1 = new ScalableLayout.LayoutParams(1600, 1300, 700f, 500f);
                EunPhung.setLayoutParams(layoutParams1);//scalableLayout.addView(EunPhung, layoutParams1);
                scalableLayout.setScale_TextSize(EunPhung, 150);

                //서대문
                ScalableLayout.LayoutParams layoutParams2 = new ScalableLayout.LayoutParams(1650, 1900, 700f, 500f);
                SuDaeMon.setLayoutParams(layoutParams2);//scalableLayout.addView(SuDaeMon, layoutParams2);
                scalableLayout.setScale_TextSize(SuDaeMon, 150);

                //종로
                ScalableLayout.LayoutParams layoutParams3 = new ScalableLayout.LayoutParams(2150, 1700, 700f, 500f);
                ZongRo.setLayoutParams(layoutParams3);//scalableLayout.addView(ZongRo, layoutParams3);
                scalableLayout.setScale_TextSize(ZongRo, 150);

                //성북
                ScalableLayout.LayoutParams layoutParams4 = new ScalableLayout.LayoutParams(2600, 1600, 700f, 500f);
                SungBuk.setLayoutParams(layoutParams4);//scalableLayout.addView(SungBuk, layoutParams4);
                scalableLayout.setScale_TextSize(SungBuk, 150);

                //강북
                ScalableLayout.LayoutParams layoutParams5 = new ScalableLayout.LayoutParams(2400, 900, 700f, 500f);
                GangBuk.setLayoutParams(layoutParams5);//scalableLayout.addView(GangBuk, layoutParams5);
                scalableLayout.setScale_TextSize(GangBuk, 150);




                //도봉
                ScalableLayout.LayoutParams layoutParams6 = new ScalableLayout.LayoutParams(2800, 600, 700f, 500f);
                DoBong.setLayoutParams(layoutParams6);//scalableLayout.addView(DoBong, layoutParams6);
                scalableLayout.setScale_TextSize(DoBong, 150);

                //노원
                ScalableLayout.LayoutParams layoutParams7 = new ScalableLayout.LayoutParams(3350, 1000, 700f, 500f);
                NoWon.setLayoutParams(layoutParams7);//scalableLayout.addView(NoWon, layoutParams7);
                scalableLayout.setScale_TextSize(NoWon, 150);

                //중랑
                ScalableLayout.LayoutParams layoutParams8 = new ScalableLayout.LayoutParams(3500, 1600, 700f, 500f);
                ZongRang.setLayoutParams(layoutParams8);//scalableLayout.addView(ZongRang, layoutParams8);
                scalableLayout.setScale_TextSize(ZongRang, 150);

                //동대문
                ScalableLayout.LayoutParams layoutParams9 = new ScalableLayout.LayoutParams(3100, 1900, 700f, 500f);
                DongDaeMon.setLayoutParams(layoutParams9);//scalableLayout.addView(DongDaeMon, layoutParams9);
                scalableLayout.setScale_TextSize(DongDaeMon, 150);

                //강서
                ScalableLayout.LayoutParams layoutParams10 = new ScalableLayout.LayoutParams(390, 2050, 700f, 500f);
                GangSue.setLayoutParams(layoutParams10);//scalableLayout.addView(GangSue, layoutParams10);
                scalableLayout.setScale_TextSize(GangSue, 150);




                ///////////////////////
                //양천
                ScalableLayout.LayoutParams layoutParams11 = new ScalableLayout.LayoutParams(800, 2700, 700f, 500f);
                YangChen.setLayoutParams(layoutParams11);//scalableLayout.addView(YangChen, layoutParams11);
                scalableLayout.setScale_TextSize(YangChen, 150);

                //구로
                ScalableLayout.LayoutParams layoutParams12 = new ScalableLayout.LayoutParams(600, 3100, 700f, 500f);
                GuRo.setLayoutParams(layoutParams12);//scalableLayout.addView(GuRo, layoutParams12);
                scalableLayout.setScale_TextSize(GuRo, 150);

                //금천
                ScalableLayout.LayoutParams layoutParams13 = new ScalableLayout.LayoutParams(1300, 3600, 700f, 500f);
                GemChun.setLayoutParams(layoutParams13);//scalableLayout.addView(GemChun, layoutParams13);
                scalableLayout.setScale_TextSize(GemChun, 150);

                //마포
                ScalableLayout.LayoutParams layoutParams14 = new ScalableLayout.LayoutParams(1400, 2200, 700f, 500f);
                MaPho.setLayoutParams(layoutParams14);//scalableLayout.addView(MaPho, layoutParams14);
                scalableLayout.setScale_TextSize(MaPho, 150);

                //영등포
                ScalableLayout.LayoutParams layoutParams15 = new ScalableLayout.LayoutParams(1350, 2700, 700f, 500f);
                YongDengPo.setLayoutParams(layoutParams15);//scalableLayout.addView(YongDengPo, layoutParams15);
                scalableLayout.setScale_TextSize(YongDengPo, 150);




                /////////////////////////
                //관악
                ScalableLayout.LayoutParams layoutParams16 = new ScalableLayout.LayoutParams(1750, 3500, 700f, 500f);
                GanAk.setLayoutParams(layoutParams16);//scalableLayout.addView(GanAk, layoutParams16);
                scalableLayout.setScale_TextSize(GanAk, 150);

                //동작
                ScalableLayout.LayoutParams layoutParams17 = new ScalableLayout.LayoutParams(1800, 2900, 700f, 500f);
                DongJack.setLayoutParams(layoutParams17);//scalableLayout.addView(DongJack, layoutParams17);
                scalableLayout.setScale_TextSize(DongJack, 150);

                //용산
                ScalableLayout.LayoutParams layoutParams18 = new ScalableLayout.LayoutParams(2150, 2500, 700f, 500f);
                YongSan.setLayoutParams(layoutParams18);//scalableLayout.addView(YongSan, layoutParams18);
                scalableLayout.setScale_TextSize(YongSan, 150);

                //중구
                ScalableLayout.LayoutParams layoutParams19 = new ScalableLayout.LayoutParams(2400, 2200, 700f, 500f);
                Zhong.setLayoutParams(layoutParams19);//scalableLayout.addView(Zhong, layoutParams19);
                scalableLayout.setScale_TextSize(Zhong, 150);

                //성동
                ScalableLayout.LayoutParams layoutParams20 = new ScalableLayout.LayoutParams(2900, 2250, 700f, 500f);
                SungDong.setLayoutParams(layoutParams20);//scalableLayout.addView(SungDong, layoutParams20);
                scalableLayout.setScale_TextSize(SungDong, 150);




                //////////////////////////////////
                //서초
                ScalableLayout.LayoutParams layoutParams21 = new ScalableLayout.LayoutParams(2450, 3200, 700f, 500f);
                SeoCho.setLayoutParams(layoutParams21);//scalableLayout.addView(SeoCho, layoutParams21);
                scalableLayout.setScale_TextSize(SeoCho, 150);

                //강남
                ScalableLayout.LayoutParams layoutParams22 = new ScalableLayout.LayoutParams(2950, 3000, 700f, 500f);
                GangNam.setLayoutParams(layoutParams22);//scalableLayout.addView(GangNam, layoutParams22);
                scalableLayout.setScale_TextSize(GangNam, 150);

                //광진
                ScalableLayout.LayoutParams layoutParams23 = new ScalableLayout.LayoutParams(3500, 2400, 700f, 500f);
                GangZin.setLayoutParams(layoutParams23);//scalableLayout.addView(GangZin, layoutParams23);
                scalableLayout.setScale_TextSize(GangZin, 150);

                //송파
                ScalableLayout.LayoutParams layoutParams24 = new ScalableLayout.LayoutParams(3700, 2900, 700f, 500f);
                SongPa.setLayoutParams(layoutParams24);//scalableLayout.addView(SongPa, layoutParams24);
                scalableLayout.setScale_TextSize(SongPa, 150);

                //강동
                ScalableLayout.LayoutParams layoutParams25 = new ScalableLayout.LayoutParams(4000, 2300, 700f, 500f);
                GangDong.setLayoutParams(layoutParams25);//scalableLayout.addView(GangDong, layoutParams25);
                scalableLayout.setScale_TextSize(GangDong, 150);

            }
        });

    }
    
    // 서울 지도 버튼 Visibility
    public void seoulBtnVisibility(String visible, String mapid) {
        if (visible.equals("visible")) {
            // Seoul Btn can view
            DoBong.setVisibility(View.VISIBLE);
            NoWon.setVisibility(View.VISIBLE);
            GangBuk.setVisibility(View.VISIBLE);
            SungBuk.setVisibility(View.VISIBLE);
            ZongRang.setVisibility(View.VISIBLE);
            EunPhung.setVisibility(View.VISIBLE);
            ZongRo.setVisibility(View.VISIBLE);
            DongDaeMon.setVisibility(View.VISIBLE);
            SuDaeMon.setVisibility(View.VISIBLE);
            Zhong.setVisibility(View.VISIBLE);
            SungDong.setVisibility(View.VISIBLE);
            GangZin.setVisibility(View.VISIBLE);
            GangDong.setVisibility(View.VISIBLE);
            MaPho.setVisibility(View.VISIBLE);
            YongSan.setVisibility(View.VISIBLE);
            GangSue.setVisibility(View.VISIBLE);
            YangChen.setVisibility(View.VISIBLE);
            GuRo.setVisibility(View.VISIBLE);
            YongDengPo.setVisibility(View.VISIBLE);
            DongJack.setVisibility(View.VISIBLE);
            GemChun.setVisibility(View.VISIBLE);
            GanAk.setVisibility(View.VISIBLE);
            SeoCho.setVisibility(View.VISIBLE);
            GangNam.setVisibility(View.VISIBLE);
            SongPa.setVisibility(View.VISIBLE);

            DoBong.setText("도봉구\n(");
            DoBong.append(String.valueOf(fuser.getPingCount(Integer.parseInt(mapid), SystemMain.DoBong)));
            DoBong.append(")");
            NoWon.setText("노원구\n(");
            NoWon.append(String.valueOf(fuser.getPingCount(Integer.parseInt(mapid), SystemMain.NoWon)));
            NoWon.append(")");
            GangBuk.setText("강북구\n(");
            GangBuk.append(String.valueOf(fuser.getPingCount(Integer.parseInt(mapid), SystemMain.GangBuk)));
            GangBuk.append(")");
            SungBuk.setText("성북구\n(");
            SungBuk.append(String.valueOf(fuser.getPingCount(Integer.parseInt(mapid), SystemMain.SungBuk)));
            SungBuk.append(")");
            ZongRang.setText("중랑구\n(");
            ZongRang.append(String.valueOf(fuser.getPingCount(Integer.parseInt(mapid), SystemMain.ZongRang)));
            ZongRang.append(")");
            EunPhung.setText("은평구\n(");
            EunPhung.append(String.valueOf(fuser.getPingCount(Integer.parseInt(mapid), SystemMain.EunPhung)));
            EunPhung.append(")");
            ZongRo.setText("종로구\n(");
            ZongRo.append(String.valueOf(fuser.getPingCount(Integer.parseInt(mapid), SystemMain.ZongRo)));
            ZongRo.append(")");
            DongDaeMon.setText("동대문구\n(");
            DongDaeMon.append(String.valueOf(fuser.getPingCount(Integer.parseInt(mapid), SystemMain.DongDaeMon)));
            DongDaeMon.append(")");
            SuDaeMon.setText("서대문구\n(");
            SuDaeMon.append(String.valueOf(fuser.getPingCount(Integer.parseInt(mapid), SystemMain.SuDaeMon)));
            SuDaeMon.append(")");
            Zhong.setText("중구\n(");
            Zhong.append(String.valueOf(fuser.getPingCount(Integer.parseInt(mapid), SystemMain.Zhong)));
            Zhong.append(")");
            SungDong.setText("성동구\n(");
            SungDong.append(String.valueOf(fuser.getPingCount(Integer.parseInt(mapid), SystemMain.SungDong)));
            SungDong.append(")");
            GangZin.setText("광진구\n(");
            GangZin.append(String.valueOf(fuser.getPingCount(Integer.parseInt(mapid), SystemMain.GangZin)));
            GangZin.append(")");
            GangDong.setText("강동구\n(");
            GangDong.append(String.valueOf(fuser.getPingCount(Integer.parseInt(mapid), SystemMain.GangDong)));
            GangDong.append(")");
            MaPho.setText("마포구\n(");
            MaPho.append(String.valueOf(fuser.getPingCount(Integer.parseInt(mapid), SystemMain.MaPho)));
            MaPho.append(")");
            YongSan.setText("용산구\n(");
            YongSan.append(String.valueOf(fuser.getPingCount(Integer.parseInt(mapid), SystemMain.YongSan)));
            YongSan.append(")");
            GangSue.setText("강서구\n(");
            GangSue.append(String.valueOf(fuser.getPingCount(Integer.parseInt(mapid), SystemMain.GangSue)));
            GangSue.append(")");
            YangChen.setText("양천구\n(");
            YangChen.append(String.valueOf(fuser.getPingCount(Integer.parseInt(mapid), SystemMain.YangChen)));
            YangChen.append(")");
            GuRo.setText("구로구\n(");
            GuRo.append(String.valueOf(fuser.getPingCount(Integer.parseInt(mapid), SystemMain.GuRo)));
            GuRo.append(")");
            YongDengPo.setText("영등포구\n(");
            YongDengPo.append(String.valueOf(fuser.getPingCount(Integer.parseInt(mapid), SystemMain.YongDengPo)));
            YongDengPo.append(")");
            DongJack.setText("동작구\n(");
            DongJack.append(String.valueOf(fuser.getPingCount(Integer.parseInt(mapid), SystemMain.DongJack)));
            DongJack.append(")");
            GemChun.setText("금천구\n(");
            GemChun.append(String.valueOf(fuser.getPingCount(Integer.parseInt(mapid), SystemMain.GemChun)));
            GemChun.append(")");
            GanAk.setText("관악구\n(");
            GanAk.append(String.valueOf(fuser.getPingCount(Integer.parseInt(mapid), SystemMain.GanAk)));
            GanAk.append(")");
            SeoCho.setText("서초구\n(");
            SeoCho.append(String.valueOf(fuser.getPingCount(Integer.parseInt(mapid), SystemMain.SeoCho)));
            SeoCho.append(")");
            GangNam.setText("강남구\n(");
            GangNam.append(String.valueOf(fuser.getPingCount(Integer.parseInt(mapid), SystemMain.GangNam)));
            GangNam.append(")");
            SongPa.setText("송파구\n(");
            SongPa.append(String.valueOf(fuser.getPingCount(Integer.parseInt(mapid), SystemMain.SongPa)));
            SongPa.append(")");

        } else if (visible.equals("gone")) {
            // Seoul Btn can not view
            DoBong.setVisibility(View.GONE);
            NoWon.setVisibility(View.GONE);
            GangBuk.setVisibility(View.GONE);
            SungBuk.setVisibility(View.GONE);
            ZongRang.setVisibility(View.GONE);
            EunPhung.setVisibility(View.GONE);
            ZongRo.setVisibility(View.GONE);
            DongDaeMon.setVisibility(View.GONE);
            SuDaeMon.setVisibility(View.GONE);
            Zhong.setVisibility(View.GONE);
            SungDong.setVisibility(View.GONE);
            GangZin.setVisibility(View.GONE);
            GangDong.setVisibility(View.GONE);
            MaPho.setVisibility(View.GONE);
            YongSan.setVisibility(View.GONE);
            GangSue.setVisibility(View.GONE);
            YangChen.setVisibility(View.GONE);
            GuRo.setVisibility(View.GONE);
            YongDengPo.setVisibility(View.GONE);
            DongJack.setVisibility(View.GONE);
            GemChun.setVisibility(View.GONE);
            GanAk.setVisibility(View.GONE);
            SeoCho.setVisibility(View.GONE);
            GangNam.setVisibility(View.GONE);
            SongPa.setVisibility(View.GONE);
        }
    }

    // 버튼 클릭 리스너
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // select location (SEOUL)
            case R.id.DoBong:
                loc_LNG = Location.DOBONGGU_LNG;
                loc_LAT = Location.DOBONGGU_LAT;
                break;
            case R.id.NoWon:
                loc_LNG = Location.NOWONGU_LNG;
                loc_LAT = Location.NOWONGU_LAT;
                break;
            case R.id.GangBuk:
                loc_LNG = Location.GANGBOOKGU_LNG;
                loc_LAT = Location.GANGBOOKGU_LAT;
                break;
            case R.id.SungBuk:
                loc_LNG = Location.SEONGBUKGU_LNG;
                loc_LAT = Location.SEONGBUKGU_LAT;
                break;
            case R.id.ZongRang:
                loc_LNG = Location.JUNGNAMGGU_LNG;
                loc_LAT = Location.JUNGNAMGGU_LAT;
                break;
            case R.id.EunPhung:
                loc_LNG = Location.EUNPYEONGGU_LNG;
                loc_LAT = Location.EUNPYEONGGU_LAT;
                break;
            case R.id.ZongRo:
                loc_LNG = Location.JONGNOGU_LNG;
                loc_LAT = Location.JONGNOGU_LAT;
                break;
            case R.id.DongDaeMon:
                loc_LNG = Location.DONGDAEMUNGU_LNG;
                loc_LAT = Location.DONGDAEMUNGU_LAT;
                break;
            case R.id.SuDaeMon:
                loc_LNG = Location.SEODAEMUNGU_LNG;
                loc_LAT = Location.SEODAEMUNGU_LAT;
                break;
            case R.id.Zhong:
                loc_LNG = Location.JUNGGU_LNG;
                loc_LAT = Location.JUNGGU_LAT;
                break;
            case R.id.SungDong:
                loc_LNG = Location.SEONGDONGGU_LNG;
                loc_LAT = Location.SEONGDONGGU_LAT;
                break;
            case R.id.GangZin:
                loc_LNG = Location.GWANGJINGU_LNG;
                loc_LAT = Location.GWANGJINGU_LAT;
                break;
            case R.id.GangDong:
                loc_LNG = Location.GANGDONGGU_LNG;
                loc_LAT = Location.GANGDONGGU_LAT;
                break;
            case R.id.MaPho:
                loc_LNG = Location.MAPOGU_LNG;
                loc_LAT = Location.MAPOGU_LAT;
                break;
            case R.id.YongSan:
                loc_LNG = Location.YONGSANGU_LNG;
                loc_LAT = Location.YONGSANGU_LAT;
                break;
            case R.id.GangSue:
                loc_LNG = Location.GANGSEOGU_LNG;
                loc_LAT = Location.GANGSEOGU_LAT;
                break;
            case R.id.YangChen:
                loc_LNG = Location.YANGCHEONGU_LNG;
                loc_LAT = Location.YANGCHEONGU_LAT;
                break;
            case R.id.GuRo:
                loc_LNG = Location.GUROGU_LNG;
                loc_LAT = Location.GUROGU_LAT;
                break;
            case R.id.YongDengPo:
                loc_LNG = Location.YEONGDEUNGPOGU_LNG;
                loc_LAT = Location.YEONGDEUNGPOGU_LAT;
                break;
            case R.id.DongJack:
                loc_LNG = Location.DONGJAKGU_LNG;
                loc_LAT = Location.DONGJAKGU_LAT;
                break;
            case R.id.GemChun:
                loc_LNG = Location.GEUMCHEONGU_LNG;
                loc_LAT = Location.GEUMCHEONGU_LAT;
                break;
            case R.id.GanAk:
                loc_LNG = Location.GWANAKGU_LNG;
                loc_LAT = Location.GWANAKGU_LAT;
                break;
            case R.id.SeoCho:
                loc_LNG = Location.SEOCHOGU_LNG;
                loc_LAT = Location.SEOCHOGU_LAT;
                break;
            case R.id.GangNam:
                loc_LNG = Location.GANGNAMGU_LNG;
                loc_LAT = Location.GANGNAMGU_LAT;
                break;
            case R.id.SongPa:
                loc_LNG = Location.SONGPAGU_LNG;
                loc_LAT = Location.SONGPAGU_LAT;
                break;
        }

        if (fuser.getMapforpinNum(Integer.parseInt(mapid)) == 0) {
            GetStorearrary(v);
        } else if(fuser.getMapforpinNum(Integer.parseInt(mapid)) == 2){
            // toast
            text_toast.setText("등록 된 리뷰가 없습니다.");
            Toast toast = new Toast(getApplicationContext());
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout_toast);
            toast.show();
        }
        else {
            Log.v("홈", "맵인텐트");
            Intent intent = new Intent(getApplicationContext(), MapActivity.class);
            intent.putExtra("fragment_id","friend_home");
            intent.putExtra("LNG", loc_LNG);
            intent.putExtra("LAT", loc_LAT);
            intent.putExtra("mapid", mapid);
            startActivity(intent);

        }
    }

    public void ScreenSize() {
        Display display = getWindowManager().getDefaultDisplay();

        if (Build.VERSION.SDK_INT >= 17) {
            //new pleasant way to get real metrics
            DisplayMetrics realMetrics = new DisplayMetrics();
            display.getRealMetrics(realMetrics);
            realWidth = realMetrics.widthPixels;
            realHeight = realMetrics.heightPixels;

        } else if (Build.VERSION.SDK_INT >= 14) {
            //reflection for this weird in-between time
            try {
                Method mGetRawH = Display.class.getMethod("getRawHeight");
                Method mGetRawW = Display.class.getMethod("getRawWidth");
                realWidth = (Integer) mGetRawW.invoke(display);
                realHeight = (Integer) mGetRawH.invoke(display);
            } catch (Exception e) {
                //this may not be 100% accurate, but it's all we've got
                realWidth = display.getWidth();
                realHeight = display.getHeight();
                Log.e("Display Info", "Couldn't use reflection to get the real display metrics.");
            }

        } else {
            //This should be close, as lower API devices should not have window navigation bars
            realWidth = display.getWidth();
            realHeight = display.getHeight();

        }
    }

    public void GetStorearrary(View v) {
        RequestQueue queue = MyVolley.getInstance(getApplicationContext()).getRequestQueue();
        MapzipRequestBuilder builder = null;
        try {
            builder= new MapzipRequestBuilder();
            builder.setCustomAttribute(NetworkUtil.USER_ID, fuser.getUserID());
            builder.setCustomAttribute(NetworkUtil.MAP_ID, mapid);
            builder.showInside();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest myReq = new JsonObjectRequest(Request.Method.POST,
                SystemMain.SERVER_HOMETOMAP_URL,
                builder.build(),
                createMyReqSuccessListener(),
                createMyReqErrorListener()) {
        };
        queue.add(myReq);

    }


    private Response.Listener<JSONObject> createMyReqSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    MapzipResponse mapzipResponse = new MapzipResponse(response);
                    mapzipResponse.showAllContents();
                    if (mapzipResponse.getState(ResponseUtil.PROCESS_HOME_GET_REVIEW_META)) { // 701
                        fuser.setMapforpinNum(Integer.parseInt(mapid), 1);
                        JSONArray reviewMeta = (JSONArray)mapzipResponse.getFieldsMember(mapzipResponse.TYPE_JSON_ARRAY,NetworkUtil.REVIEW_META);
                        fuser.setMapforpinArray(reviewMeta, reviewMeta.getJSONObject(0).getInt(NetworkUtil.MAP_ID));

                        Log.v(TAG, "맵인텐트");
                        Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                        intent.putExtra("fragment_id","friend_home");
                        intent.putExtra("LNG", loc_LNG);
                        intent.putExtra("LAT", loc_LAT);
                        intent.putExtra("mapid", mapid);
                        startActivity(intent);
                    } else { // 711
                        fuser.setMapforpinNum(Integer.parseInt(mapid), 2);
                        // toast
                        text_toast.setText("등록 된 리뷰가 없습니다.");
                        Toast toast = new Toast(getApplicationContext());
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(layout_toast);
                        toast.show();
                    }
                } catch (JSONException ex) {
                    Log.v(TAG, "제이손 에러");
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

    public void addFriend_enroll(View v) {
        RequestQueue queue = MyVolley.getInstance(this).getRequestQueue();

       MapzipRequestBuilder builder = null;
        try {
            builder= new MapzipRequestBuilder();
            builder.setCustomAttribute(NetworkUtil.USER_ID, user.getUserID());
            builder.setCustomAttribute(NetworkUtil.FRIEND_ID, fuser.getUserID());
            builder.setCustomAttribute(NetworkUtil.USER_NAME, user.getUserName());
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

    private Response.Listener<JSONObject> createMyReqSuccessListener_enroll() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    MapzipResponse mapzipResponse = new MapzipResponse(response);
                    mapzipResponse.showAllContents();
                    if (mapzipResponse.getState(ResponseUtil.PROCESS_FRIEND_ADD)) {

                        mapsetting.setVisibility(View.INVISIBLE);
                        mapsetting.setEnabled(false);
                        mapsetting.setBackgroundResource(R.drawable.addfriend);
                        mapsetting.setVisibility(View.VISIBLE);

                        // toast
                        text_toast.setText(fuser.getUserID()+"님을 맵갈피에 추가하였습니다.");
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


    public void addFriend_search() {
        RequestQueue queue = MyVolley.getInstance(this).getRequestQueue();
        MapzipRequestBuilder builder = null;
        try {
            builder= new MapzipRequestBuilder();
            builder.setCustomAttribute(NetworkUtil.USER_ID, user.getUserID());
            builder.setCustomAttribute(NetworkUtil.FRIEND_ID, fuser.getUserID());
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

    private Response.Listener<JSONObject> createMyReqSuccessListener_search() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    MapzipResponse mapzipResponse = new MapzipResponse(response);
                    mapzipResponse.showAllContents();
                    if (mapzipResponse.getState(ResponseUtil.PROCESS_FRIEND_SEARCH_BY_NAME)) {
                        if (mapzipResponse.getFieldsMember(MapzipResponse.TYPE_STRING,NetworkUtil.IS_FRIEND).equals("true")) {
                            mapsetting.setVisibility(View.INVISIBLE);
                            mapsetting.setEnabled(false);
                            mapsetting.setBackgroundResource(R.drawable.addfriend);
                            mapsetting.setVisibility(View.VISIBLE);
                        }
                    }else{
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

}
