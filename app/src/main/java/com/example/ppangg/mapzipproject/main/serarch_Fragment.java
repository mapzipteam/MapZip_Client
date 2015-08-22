package com.example.ppangg.mapzipproject.main;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.ppangg.mapzipproject.R;
import com.example.ppangg.mapzipproject.SystemMain;
import com.example.ppangg.mapzipproject.network.MyVolley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class serarch_Fragment extends Fragment implements AbsListView.OnScrollListener{


    private View v;

    // search
    private EditText searchhash;
    private Button searchBtn;
    private int seq;

    // toast
    private View layout;
    private TextView text;

    // list
    private ArrayList<MyItem> marItem;
    private MyListAdapter 	  mMyAdapte;
    private ListView mListView;
    private MyItem items;

    // ��ũ�� �ε�
    private LayoutInflater mInflater;
    private boolean mLockListView;

    private boolean mLockBtn;

    private JSONArray getArray;

    public serarch_Fragment(){
        seq = 0;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

       v = inflater.inflate(R.layout.fragment_search, container, false);

        searchhash = (EditText)v.findViewById(R.id.searchText);
        searchBtn = (Button)v.findViewById(R.id.searchBtn);

        mListView = (ListView)v.findViewById(R.id.searchList);
        marItem = new ArrayList<MyItem>();

        mLockListView = true;
        mLockBtn = true;

        // Ǫ�͸� ���. setAdapter ������ �ؾ���.
        mInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mListView.addFooterView(mInflater.inflate(R.layout.listview_footer, null));

        // ��ũ�� ������ ���
        mListView.setOnScrollListener(this);
/*
        mMyAdapte = new MyListAdapter(getActivity(), R.layout.custom_listview, marItem);
        mListView.setAdapter(mMyAdapte);
*/
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                marItem.clear();
                mMyAdapte = new MyListAdapter(getActivity(), R.layout.custom_listview, marItem);
                mListView.setAdapter(mMyAdapte);
                mMyAdapte.notifyDataSetChanged();
                seq = 0;

                DoSearch(v);

                // �ӽ� ������ ���
                addItems(3);

                mLockBtn = false;
            }
        });
         
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
    }
    // ����Ʈ�� ��� �׸�
    class MyItem
    {
        MyItem(String _coustId, String name, String category, String hashtag)
        {
            sCustId = _coustId;
            name_s = name;
            category_s = category;
            hashtag_s = hashtag;
        }
        String sCustId;
        String name_s;
        String category_s;
        String hashtag_s;

    }

    // ����� Ŭ����
    class MyListAdapter extends BaseAdapter
    {
        Context cContext;
        LayoutInflater lInflater;
        ArrayList<MyItem> alSrc;
        int layout;

        public MyListAdapter(Context _context, int _layout, ArrayList<MyItem> _arrayList)
        {
            cContext  = _context;
            lInflater = (LayoutInflater)_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            alSrc	  = _arrayList;
            layout    = _layout;
        }

        @Override
        public int getCount()
        {
            return alSrc.size();
        }

        @Override
        public Object getItem(int position)
        {
            return alSrc.get(position).sCustId;
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        public String getName(int position)
        {
            return alSrc.get(position).name_s;
        }

        public String getCategory(int position)
        {
            return alSrc.get(position).category_s;
        }

        public String getHash(int position)
        {
            return alSrc.get(position).hashtag_s;
        }


        // �� ���� �׸� ����
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            final int pos = position;
            if(convertView == null)
            {
                convertView = lInflater.inflate(layout, parent, false);
            }

            final String getCustId = alSrc.get(pos).sCustId;

            TextView nameText_search = (TextView)convertView.findViewById(R.id.nameText_search);
            nameText_search.setText(getName(pos)+getCategory(pos));
            TextView hashText_search = (TextView)convertView.findViewById(R.id.hashText_search);
            hashText_search.setText(getHash(pos));

            return convertView;
        }
    }

    // ���� ������ �߰�
    private void addItems(final int size)
    {
        // �������� �߰��ϴ� ���� �ߺ� ��û�� �����ϱ� ���� ���� �ɾ�Ӵϴ�.
        mLockListView = true;
        Runnable run = new Runnable()
        {
            @Override
            public void run()
            {
                if(mLockBtn == false) {
                    try {
                        for (int i = 0; i < size; i++) {
                            items = new MyItem(String.valueOf(i), getArray.getJSONObject(i).getString("user_name"), getArray.getJSONObject(i).getString("category"), getArray.getJSONObject(i).getString("hash_tag"));
                            marItem.add(items);
                        }
                    } catch (JSONException e) {

                    }
                    // ��� �����͸� �ε��Ͽ� �����Ͽ��ٸ� ����Ϳ� �˸���
                    // ����Ʈ���� ���� �����մϴ�.
                    mMyAdapte.notifyDataSetChanged();
                    mLockListView = false;
                }
            }
        };
        // �ӵ��� �����̸� �����ϱ� ���� �ļ�

        Handler handler = new Handler();
        handler.postDelayed(run, 1000);

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        // ���� ���� ó���� ���̴� ����ȣ�� �������� ����ȣ�� ���Ѱ���
        // ��ü�� ���ڿ� ���������� ���� �Ʒ��� ��ũ�� �Ǿ��ٰ� �����մϴ�.
        int count = totalItemCount - visibleItemCount;

        if(firstVisibleItem >= count && totalItemCount != 0 && mLockListView == false && mLockBtn == false)
        {
            Log.i("list", "Loading next items");
            DoSearch(v);

            addItems(3);
        }

    }

    public void DoSearch(View v) {
        RequestQueue queue = MyVolley.getInstance(getActivity()).getRequestQueue();

        JSONObject obj = new JSONObject();
        try {
            obj.put("target", searchhash.getText().toString());
            obj.put("more",seq);
            Log.v("searchmap ������", obj.toString());
        } catch (JSONException e) {
            Log.v("���̼�", "����");
        }

        JsonObjectRequest myReq = new JsonObjectRequest(Request.Method.POST,
                SystemMain.SERVER_MAPSEARCH_URL,
                obj,
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
                    int state = response.getInt("state");
                    if( state == 501) {
                        getArray = response.getJSONArray("map_search");
                        seq++;

                        Log.v("searchmap �ޱ�", response.toString());

                    }else if(state == 502){
                        mLockBtn = true;
                    }

                }catch (JSONException e){

                }
            }
        };
    }

    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // toast
                text.setText("���ͳ� ������ �ʿ��մϴ�.");
                Toast toast = new Toast(getActivity());
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();

                Log.e("searchmap", error.getMessage());
            }
        };
    }

}
