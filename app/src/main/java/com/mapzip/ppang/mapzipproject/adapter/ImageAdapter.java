package com.mapzip.ppang.mapzipproject.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mapzip.ppang.mapzipproject.R;
import com.mapzip.ppang.mapzipproject.model.FriendData;
import com.mapzip.ppang.mapzipproject.model.SystemMain;
import com.mapzip.ppang.mapzipproject.model.UserData;

/**
 * Created by ppangg on 2015-08-23.
 */

public class ImageAdapter extends PagerAdapter {
    Context context;
    private int nowwho;
    private UserData user;
    private FriendData fuser;

    public ImageAdapter(Context context, int i){
        this.context=context;
        nowwho = i;

        if(i == SystemMain.justuser)
            user = UserData.getInstance();
        else if(i == SystemMain.justfuser)
            fuser = FriendData.getInstance();
    }
    @Override
    public int getCount() {
        if(nowwho == SystemMain.justuser)
            return user.getGalImages().length;
        else
            return fuser.getGalImages().length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((ImageView) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        ImageView imageView = new ImageView(context);
//        int padding = context.getResources().getDimensionPixelSize(R.dimen.padding_medium);

//        imageView.setPadding(padding, padding, padding, padding);
//        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//        2016.02.22송지원이 수정
//        이미지가 모두 가로세로 늘리는게 싫어서 원본 지율 맞춰서 줄어들게 만들었어요
//        R.color.image_adapter_background도 일부러 거지같이 만들었으니깐 이쁜 색으로 골라주세용ㅎㅎ^^
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setBackgroundColor(context.getResources().getColor(R.color.image_adapter_background));


        Log.v("이미지 어댑터 포지션",String.valueOf(position));
        if(nowwho == SystemMain.justuser)
            imageView.setImageBitmap(user.getGalImages()[position]);
        else
            imageView.setImageBitmap(fuser.getGalImages()[position]);

        ((ViewPager) container).addView(imageView, 0);

        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((ImageView) object);
    }

    @Override
    public int getItemPosition(Object object) {
        Log.v("이미지어댑터","겟포지션");
        return super.getItemPosition(object);
    }
}
