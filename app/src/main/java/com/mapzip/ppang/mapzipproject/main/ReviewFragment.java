package com.mapzip.ppang.mapzipproject.main;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mapzip.ppang.mapzipproject.R;
import com.mapzip.ppang.mapzipproject.activity.ReviewRegisterActivity;
import com.mapzip.ppang.mapzipproject.map.MapActivity;
import com.mapzip.ppang.mapzipproject.map.RestaurantResult;
import com.mapzip.ppang.mapzipproject.map.RestaurantSearcher;
import com.mapzip.ppang.mapzipproject.map.SearchInLocationActivity;
import com.mapzip.ppang.mapzipproject.model.UserData;

import java.util.ArrayList;

public class ReviewFragment extends Fragment implements AbsListView.OnScrollListener {

    private final String TAG = "ReviewFragment";

    private UserData user;

    // toast
    private View layout_toast;
    private TextView text_toast;

    private Button review_regi;
    private Button review_search;
    private Button review_regi_self;

    private EditText searchedName;
    private EditText searchedLocation;

    private String storeToSerch;
    private String storeName;
    private String storeLocation;

    private RestaurantResult restaurants;
    private Context context;
    private String query;
    private RestaurantSearcher restaurantSearcher;

    // list
    private ArrayList<MyItem> marItem;
    private MyListAdapter mMyAdapte;
    private ListView mListView;
    private MyItem items;
    private View footer;
    private int arrsize;

    // 스크롤 로딩
    private LayoutInflater mInflater;

    private boolean mLockListView;
    private boolean mLockBtn;

    private Handler handler;

    // 선택 이벤트
    private int selectNum;

    public ReviewFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        user = UserData.getInstance();
        arrsize = 0;
        selectNum = -1;
        //getActivity().getActionBar().setTitle("리뷰");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View v = inflater.inflate(R.layout.fragment_review, container, false);

        layout_toast = inflater.inflate(R.layout.my_custom_toast, (ViewGroup) getActivity().findViewById(R.id.custom_toast_layout));
        text_toast = (TextView) layout_toast.findViewById(R.id.textToShow);

        mListView = (ListView) v.findViewById(R.id.searchList_review);
        mListView.setOnItemClickListener(new ListViewItemClickListener());
        //mListView.setSelector(R.drawable.store_list_bg_selector);

        // 스크롤 리스너 등록
        mListView.setOnScrollListener(this);

        marItem = new ArrayList<MyItem>();

        user.setReviewListlock(false);
        mLockListView = false;
        mLockBtn = true;

        // 푸터를 등록. setAdapter 이전에 해야함.
        mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footer = mInflater.inflate(R.layout.listview_footer, null);


        //2016.01.10송지원이 고침
        //searchedit = (EditText) v.findViewById(R.id.searchText_review);//검색창이 하나였던것을 가게와 지역으로 나누어 검색하게 함
        searchedName = (EditText) v.findViewById(R.id.searchNameText_review);
        searchedLocation = (EditText) v.findViewById(R.id.searchLocationText_review);

        review_search = (Button) v.findViewById(R.id.searchBtn_review);
        review_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                storeName = null;//검색창에 입력한 가게이름 받아오는 String
                storeLocation = null;//검색창에 입력한 가게위치 받아오는 String
                storeName = searchedName.getText().toString().trim();
                storeLocation = searchedLocation.getText().toString().trim();


                storeToSerch = null;//입력된 가게이름과 가게위치를 합쳐서 하나의 검색어로 만들어 저징하는 String

                if(storeName.length() != 0){//가게이름이 있을경우

                    if(storeLocation.length() != 0){//장소 있을경우

                        storeToSerch = storeName+" "+storeLocation;
                    }
                    else{//장소 없을경우
                        //이부분은 검색시 가게이름만 작성하고 위치는 작성하지않았을때 사용자의 자주가는 지역 중심으로 검색 결과를 보여주는 옵션을 추가할때 사용할 부분.
                        //지금은 그냥 지역정보 없이 가게명만 보냄. 옵션 추가하기전까진 중구부터 나올것이다.
                        Log.d("dSJW", "2");
                        storeToSerch = storeName;
                    }
                }
                else{//가게이름 없을경우

                    //이부분은 추가하거나 말거나 이야기 해봅시다!!!
                    //토스트를 띄울건지 아니면 아무반응 없게 할것인지
                    text_toast.setText("검색어를 입력해주세요.");
                    Toast toast = new Toast(getActivity());
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout_toast);
                    toast.show();

                    return;
                }


                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(review_search.getWindowToken(), 0);

                //query = searchedit.getText().toString();
                query = storeToSerch;
                startSearching();

                marItem.clear();
                arrsize = 0;
                selectNum = -1;
                mMyAdapte = new MyListAdapter(getActivity(),R.layout.v_list_f_review, marItem);
                mListView.addFooterView(footer);
                mListView.setAdapter(mMyAdapte);
                mMyAdapte.notifyDataSetChanged();

                mLockBtn = false;

                reviewBtnChange(false);
            }
        });

        review_regi_self = (Button) v.findViewById(R.id.registerOwnBtn_review);
        review_regi_self.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(searchedName.getWindowToken(), 0);
                searchedName.clearFocus();
                searchedName.setSelectAllOnFocus(false);

                inputMethodManager.hideSoftInputFromWindow(searchedLocation.getWindowToken(), 0);
                searchedLocation.clearFocus();
                searchedLocation.setSelectAllOnFocus(false);

                Intent intent = new Intent(getActivity(), SearchInLocationActivity.class);
                startActivity(intent);
            }
        });

//        review_regi_self.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if(event.getAction() == MotionEvent.ACTION_DOWN)
//                    review_regi_self.setBackgroundResource(R.drawable.background_btn);
//
//                if(event.getAction() == MotionEvent.ACTION_UP)
//                    review_regi_self.setBackgroundResource(R.drawable.noclick);
//                return false;
//            }
//        });

        review_regi_self.setClickable(true);

        // 리뷰작성버튼 로직
        review_regi = (Button) v.findViewById(R.id.registerBtn_review);
        review_regi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectNum == -1) {
                    // toast
                    text_toast.setText("가게를 선택해주세요.");
                    Toast toast = new Toast(getActivity());
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout_toast);
                    toast.show();
                    return;
                }

                if (restaurants.get(selectNum).getLngX() == 0 || restaurants.get(selectNum).getLatY() == 0) {
                    // toast
                    text_toast.setText("다시 검색해주세요.");
                    Toast toast = new Toast(getActivity());
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout_toast);
                    toast.show();
                    return;
                }

                Intent intent = new Intent(getActivity(), ReviewRegisterActivity.class);
                intent.putExtra("store_name", restaurants.get(selectNum).getTitle());
                intent.putExtra("store_address", restaurants.get(selectNum).getAdress());
                intent.putExtra("store_contact", restaurants.get(selectNum).getTelephone());
                intent.putExtra("store_x", restaurants.get(selectNum).getLngX());
                intent.putExtra("store_y", restaurants.get(selectNum).getLatY());
                intent.putExtra("store_cx", restaurants.get(selectNum).getKatecX());
                intent.putExtra("store_cy", restaurants.get(selectNum).getKatecY());
                intent.putExtra("state","enroll");
                startActivity(intent);
            }
        });

//        review_regi.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if(event.getAction() == MotionEvent.ACTION_DOWN)
//                    review_regi.setBackgroundResource(R.drawable.background_btn);
//
//                if(event.getAction() == MotionEvent.ACTION_UP)
//                    review_regi.setBackgroundResource(R.drawable.noclick2);
//                return false;
//            }
//        });

        review_regi.setClickable(true);

        return v;
    }

    /**
     * 리뷰작성버튼이 활성화/비활성화 되도록 하는 함수입니다.
     */
    private void reviewBtnChange(boolean valid){
        if(valid == true) {
            review_regi.setBackgroundResource(R.drawable.rec_btn_pink_round);
            review_regi.setTextColor(getResources().getColor(R.color.white));
        }else{
            review_regi.setBackgroundResource(R.drawable.rec_btn_white);
            review_regi.setTextColor(getResources().getColor(R.color.hotpink));
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
    }

    public void startSearching(){
        context = getActivity();
        restaurants = new RestaurantResult();
        restaurantSearcher = new RestaurantSearcher(restaurants, query, context);
        restaurantSearcher.UrlRequest();
    }

    // 리스트뷰 출력 항목
    class MyItem {
        MyItem(String _coustId, String name, String address) {
            sCustId = _coustId;
            name_s = name;
            address_s = address;
        }

        String sCustId;
        String name_s;
        String address_s;

    }

    // 어댑터 클래스
    class MyListAdapter extends BaseAdapter {
        Context cContext;
        LayoutInflater lInflater;
        ArrayList<MyItem> alSrc;
        int layout;
        private Animation animation_none;
        private Animation animation_active;
        private int lastPosition;

        public MyListAdapter(Context _context, int _layout, ArrayList<MyItem> _arrayList) {
            cContext = _context;
            lInflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            alSrc = _arrayList;
            layout = _layout;

            animation_none = AnimationUtils.loadAnimation(getActivity(),R.anim.store_list_anim_nomove);
            animation_active = AnimationUtils.loadAnimation(getActivity(),R.anim.store_list_anim_new);

            lastPosition = -1;
        }

        @Override
        public int getCount() {
            return alSrc.size();
        }

        @Override
        public Object getItem(int position) {
            return alSrc.get(position).sCustId;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public String getName(int position) {
            return alSrc.get(position).name_s;
        }

        public String getAddress(int position) { return alSrc.get(position).address_s; }

        // 각 뷰의 항목 생성
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int pos = position;

            TextView nameText = null;
            TextView addressText = null;
            Button mapviewBtn = null;
            StoreListHolder holder = null;

            if (convertView == null) {
                convertView = lInflater.inflate(layout, parent, false);

                nameText = (TextView) convertView.findViewById(R.id.tv_name_f_review);
                addressText = (TextView) convertView.findViewById(R.id.tv_finfo_f_review);
                mapviewBtn = (Button)convertView.findViewById(R.id.btn_mapview_f_review);
                mapviewBtn.setVisibility(View.VISIBLE);

                holder = new StoreListHolder();
                holder.m_storeName = nameText;
                holder.m_storeLocation = addressText;
                holder.m_mapviewBtn = mapviewBtn;

                convertView.setTag(holder);

            }else{
                holder = (StoreListHolder)convertView.getTag();
                nameText = holder.m_storeName;
                addressText = holder.m_storeLocation;
                mapviewBtn = holder.m_mapviewBtn;

            }

            nameText.setText(getName(pos));
            addressText.setText(getAddress(pos));

            mapviewBtn.setFocusable(false);
            mapviewBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), MapActivity.class);
                    intent.putExtra("LNG", restaurants.get(pos).getLngX());
                    intent.putExtra("LAT", restaurants.get(pos).getLatY());
                    intent.putExtra("fragment_id", "review");
                    intent.putExtra("store_name", restaurants.get(pos).getTitle());
                    intent.putExtra("store_x", restaurants.get(pos).getLngX());
                    intent.putExtra("store_y", restaurants.get(pos).getLatY());
                    startActivity(intent);
                }
            });
            if(position >= this.lastPosition){
                convertView.startAnimation(animation_active);
                this.lastPosition  = position;
            }else{
                convertView.startAnimation(animation_none);
            }



            return convertView;
        }

        private class StoreListHolder{
            public TextView m_storeName;
            public TextView m_storeLocation;
            public Button m_mapviewBtn;
        }
    }

    // 더미 아이템 추가
    private void addItems(final int size) {
        // 아이템을 추가하는 동안 중복 요청을 방지하기 위해 락을 걸어둡니다.
        mLockListView = true;
        Runnable run = new Runnable() {
            @Override
            public void run() {
                Log.v("아이템추가","진입");
                if (mLockBtn == false) {
                    try {
                        Log.v("아이템추가","런");
                        int asize = arrsize;
                        for (int i=asize; i < asize+size; i++) {
                            items = new MyItem(String.valueOf(i), restaurants.getRestaurants().get(i).getTitle(), restaurants.getRestaurants().get(i).getAdress());
                            marItem.add(items);
                            arrsize++;

                            if((arrsize == restaurants.getRestaurants().size()) && (restaurants.getRestaurants().size() != 0)) {
                                mLockBtn = true;
                                mListView.removeFooterView(footer);
                            }
                        }
                    } catch (Exception e) {

                    }
                    // 모든 데이터를 로드하여 적용하였다면 어댑터에 알리고
                    // 리스트뷰의 락을 해제합니다.
                    mMyAdapte.notifyDataSetChanged();
                }
            }
        };

        // 속도의 딜레이를 구현하기 위한 꼼수

        handler = new Handler();
        handler.postDelayed(run, 1000);

        mLockListView = false;

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        int count = totalItemCount - visibleItemCount;
        Log.v("리뷰 리스트뷰락",String.valueOf(user.getReviewListlock()));

        if (firstVisibleItem >= count && totalItemCount != 0 && (mLockBtn == false) && (user.getReviewListlock() == false) && (mLockListView == false)) {
            Log.i("list", "Loading next items");
            addItems(5);
        }

    }

    private class ListViewItemClickListener implements AdapterView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            selectNum = position;

            // 리뷰버튼 활성화
            reviewBtnChange(true);
            Log.v("리스트뷰 셀렉트", String.valueOf(selectNum));
        }
    }

}


