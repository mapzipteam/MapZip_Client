package com.mapzip.ppang.mapzipproject.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mapzip.ppang.mapzipproject.R;

/**
 * Created by ppangg on 2015-08-23.
 */

public class ImageLoadAdapter extends PagerAdapter {
    private final String TAG = "ImageLoadAdapter";
    private Context mContext;
    private int mTotalCount = 0;
    private String[] mImageUrlArr;

    private int mHeight = 0;
    private int mWidth = 0;

    // width와 height 입력시 지정한 크기만큼만 이미지 로딩.
    public ImageLoadAdapter(Context context, String[] urlArr, int width, int height){
        this(context, urlArr);
        this.mWidth = width;
        this.mHeight = height;
    }

    public ImageLoadAdapter(Context context, String[] urlArr){
        this.mContext = context;
        this.mImageUrlArr = urlArr;
        this.mTotalCount = urlArr.length;
    }

    public ImageLoadAdapter(Context context){ // no image test
        this.mContext = context;
        this.mTotalCount = 0;
    }

    // getCount()의 return 값에 따라 page 갯수가 결정됨.
    @Override
    public int getCount() {
        if(mTotalCount == 0) // no image
            return 1;
        else
            return mTotalCount;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((ImageView) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //Log.v(TAG+"position",String.valueOf(position));
        ImageView imageView = new ImageView(mContext);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setBackgroundResource(0);

        if(mTotalCount == 0) { // no image
            Glide.with(mContext).load(R.drawable.noimage)
                    .into(imageView);
        }else {
            if (mWidth == 0 || mHeight == 0)
                Glide.with(mContext).load(mImageUrlArr[position])
                        .placeholder(R.drawable.load)
                        .thumbnail(0.1f)
                        .error(R.drawable.loadfail)
                        .into(imageView);
            else
                Glide.with(mContext).load(mImageUrlArr[position])
                        .override(mWidth, mHeight) // image 사이즈 조절
                        .placeholder(R.drawable.load)
                        .thumbnail(0.1f)
                        .error(R.drawable.loadfail)
                        .into(imageView);
        }

        ((ViewPager) container).addView(imageView, 0);

        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((ImageView) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }
}
