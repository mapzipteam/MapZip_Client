package com.example.ppangg.mapzipproject;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class home_Fragment extends Fragment {

    private View v;
    private TextView topstate;
    private TextView hashstate;
    private UserData user;
    private ArrayList<String> sppinerList;
    private View imageview;

    private int mapnum;

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

    public home_Fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        user = UserData.getInstance();

        mapnum = user.getMapmetaArray().length();

        sppinerList = new ArrayList<String>();
        try {
            for (int i = 0; i < mapnum; i++) {
                sppinerList.add(user.getMapmetaArray().getJSONObject(i).getString("title"));
            }
        }catch (JSONException ex){

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_home, container, false);
        imageview = (View) v.findViewById(R.id.mapimage);

        topstate = (TextView) v.findViewById(R.id.topstate);
        topstate.setText(user.getUserName());
        topstate.append("(");
        topstate.append(user.getUserID());
        topstate.append(")");

        hashstate = (TextView) v.findViewById(R.id.tagText);

        DoBong = (Button) v.findViewById(R.id.DoBong);
        NoWon = (Button) v.findViewById(R.id.NoWon);
        GangBuk = (Button) v.findViewById(R.id.GangBuk);
        SungBuk = (Button) v.findViewById(R.id.SungBuk);
        ZongRang = (Button) v.findViewById(R.id.ZongRang);
        EunPhung = (Button) v.findViewById(R.id.EunPhung);
        ZongRo = (Button) v.findViewById(R.id.ZongRo);
        DongDaeMon = (Button) v.findViewById(R.id.DongDaeMon);
        SuDaeMon = (Button) v.findViewById(R.id.SuDaeMon);
        Zhong = (Button) v.findViewById(R.id.Zhong);
        SungDong = (Button) v.findViewById(R.id.SungDong);
        GangZin = (Button) v.findViewById(R.id.GangZin);
        GangDong = (Button) v.findViewById(R.id.GangDong);
        MaPho = (Button) v.findViewById(R.id.MaPho);
        YongSan = (Button) v.findViewById(R.id.YongSan);
        GangSue = (Button) v.findViewById(R.id.GangSue);
        YangChen = (Button) v.findViewById(R.id.YangChen);
        GuRo = (Button) v.findViewById(R.id.GuRo);
        YongDengPo = (Button) v.findViewById(R.id.YongDengPo);
        DongJack = (Button) v.findViewById(R.id.DongJack);
        GemChun = (Button) v.findViewById(R.id.GemChun);
        GanAk = (Button) v.findViewById(R.id.GanAk);
        SeoCho = (Button) v.findViewById(R.id.SeoCho);
        GangNam = (Button) v.findViewById(R.id.GangNam);
        SongPa = (Button) v.findViewById(R.id.SongPa);

        Spinner spinner = (Spinner) v.findViewById(R.id.spinner);
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, sppinerList);
        //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
        //        getActivity(), R.array.spinner_number, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if (position == 0) {

                        JSONObject mapmeta = user.getMapmetaArray().getJSONObject(position);
                        if (Integer.parseInt(mapmeta.get("category").toString()) == SystemMain.SEOUL_MAP_NUM) {
                            imageview.setBackgroundResource(R.drawable.seoul);
                            seoulBtnVisibility("visible");
                            hashstate.setText(mapmeta.get("hash_tag").toString());
                        }

                    } else if (position == 1) {

                        JSONObject mapmeta = user.getMapmetaArray().getJSONObject(position);
                        if (Integer.parseInt(mapmeta.get("category").toString()) == SystemMain.SEOUL_MAP_NUM) {
                            imageview.setBackgroundResource(R.drawable.seoul);
                            seoulBtnVisibility("visible");
                            hashstate.setText(mapmeta.get("hash_tag").toString());
                        }
                    }
                } catch (JSONException ex) {
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        GangNam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MapActivity.class);

                //준수형이 해주신거 이런식으로 바꿔야해intent.putExtra("location", 1);
                /////ㄴㄴ이게 젤좋은데 안되는것같다intent.putExtra("location", (Parcelable)Location.setLocation(1));

                intent.putExtra("LNG", Location.GANGNAMGU_LNG);
                intent.putExtra("LAT", Location.GANGNAMGU_LAT);

                startActivity(intent);
            }
        });

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
    }

    public void seoulBtnVisibility(String visible) {

        if (visible.equals("visible")) {
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
        } else if (visible.equals("gone")) {
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
    /*
    public void onButton1Clicked(View v)//"1.강남구"버튼
    {
        Intent intent = new Intent(getActivity(),MapActivity.class);

        //준수형이 해주신거 이런식으로 바꿔야해intent.putExtra("location", 1);
        /////ㄴㄴ이게 젤좋은데 안되는것같다intent.putExtra("location", (Parcelable)Location.setLocation(1));

        intent.putExtra("LNG", Location.GANGNAMGU_LNG);
        intent.putExtra("LAT", Location.GANGNAMGU_LAT);

        startActivity(intent);
    }*/

}
