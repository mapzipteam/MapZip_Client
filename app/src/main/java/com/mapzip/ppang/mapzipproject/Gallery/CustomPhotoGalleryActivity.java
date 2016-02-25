package com.mapzip.ppang.mapzipproject.Gallery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.mapzip.ppang.mapzipproject.R;

import java.util.ArrayList;

/**
 * Created by Song  Ji won on 2016-02-26.
 */
public class CustomPhotoGalleryActivity extends Activity implements ListView.OnScrollListener {




    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        switch(scrollState){

            case ListView.OnScrollListener.SCROLL_STATE_IDLE:
                mBusy = false;
                mGalleryImageAdapter.notifyDataSetChanged();
                break;

            case ListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                mBusy = true;
                break;

            case ListView.OnScrollListener.SCROLL_STATE_FLING:
                mBusy = true;
                break;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }






    private String LOG_TAG = "CustomPhotoGalleryActivity";

    private GridView mGridImages;
    private GalleryImageAdapter mGalleryImageAdapter;
    private ArrayList<GalleryImageInfo> mGalleryImageInfoArrayList;

    private Button mSelectButton;


//    private String[] arrPath;
//    private String[] arrOrientation;
//    private boolean[] thumbnailsselection;
//    private int ids[];

//    private int count;


    private boolean mBusy = false;

    //    public String getPathFromUri(Uri uri) {
//        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
//        cursor.moveToNext();
//        String path = cursor.getString(cursor.getColumnIndex("_data"));
//        cursor.close();
//
//        return path;
//    }
//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.custom_gallery);

        mGridImages = (GridView)findViewById(R.id.grid_images);
        mGridImages.setOnScrollListener(this);

        mGalleryImageInfoArrayList = new ArrayList<GalleryImageInfo>();

        mSelectButton = (Button)findViewById(R.id.button_select);


        new DoFindImageList().execute();

//        mGalleryImageAdapter = new mGalleryImageAdapter();
//        mGridImages.setAdapter(mGalleryImageAdapter);



/*


//        final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID, MediaStore.Images.Media.ORIENTATION};
        final String[] columns = {MediaStore.Images.Media.DEFAULT_SORT_ORDER,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.MINI_THUMB_MAGIC,
                MediaStore.Images.Media.ORIENTATION,
                MediaStore.MediaColumns.DATE_MODIFIED,
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.MediaColumns.TITLE};

        final String ordetBy = MediaStore.Images.Media._ID;

        Cursor imageCursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, ordetBy);
        int image_column_index = imageCursor.getColumnIndex(MediaStore.Images.Media._ID);
        int dataColumnIndex = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);
        int uriColumnIndex = imageCursor.getColumnIndex(MediaStore.Images.Media.ORIENTATION);

        int index_1 = imageCursor.getColumnIndex(MediaStore.Images.Media.DEFAULT_SORT_ORDER);
        int index_2 = imageCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        int index_3 = imageCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
        int index_4 = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);
        int index_5 = imageCursor.getColumnIndex(MediaStore.Images.Media.MINI_THUMB_MAGIC);
//        int index_6 = imageCursor.getColumnIndex(MediaStore.Images.Media.ORIENTATION);
        int index_7 = imageCursor.getColumnIndex(MediaStore.MediaColumns.DATE_MODIFIED);
        int index_8 = imageCursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);
        int index_9 = imageCursor.getColumnIndex(MediaStore.MediaColumns.TITLE);






        this.count = imageCursor.getCount();
//        Log.e(LOG_TAG,"imageCursor.getCount() : "+this.count);


        this.arrPath = new String[this.count];
//        this.arrUri = new Uri[this.count];
        this.arrOrientation = new String[this.count];

        ids = new int[this.count];
        this.thumbnailsselection = new boolean[this.count];


        for (int i = 0; i<this.count; i++){

            imageCursor.moveToPosition(i);
            ids[i] = imageCursor.getInt(image_column_index);
            arrPath[i] = imageCursor.getString(dataColumnIndex);
            arrOrientation[i] = Integer.toString(imageCursor.getInt(uriColumnIndex));

            String str_1 = imageCursor.getString(index_1);
            String str_2 = imageCursor.getString(index_2);
            String str_3 = imageCursor.getString(index_3);
            String str_4 = imageCursor.getString(index_4);
            String str_5 = imageCursor.getString(index_5);
//            String str_6 = imageCursor.getString(index_6);
            String str_7 = imageCursor.getString(index_7);
            String str_8 = imageCursor.getString(index_8);
            String str_9 = imageCursor.getString(index_9);




            Log.d(LOG_TAG + "imageCusor", "~~~~~~~" + ids[i]);
            Log.e(LOG_TAG +"imageCusor", "1) MediaStore.Images.Media.DEFAULT_SORT_ORDER : " + str_1);
            Log.e(LOG_TAG + "imageCusor", "2) MediaStore.Images.Media.BUCKET_DISPLAY_NAME : " + str_2);
            Log.e(LOG_TAG+"imageCusor", "3) MediaStore.Images.Media.BUCKET_ID : " + str_3);
            Log.e(LOG_TAG+"imageCusor", "4) MediaStore.Images.Media.DATA : " + str_4);
            Log.e(LOG_TAG+"imageCusor", "5) MediaStore.Images.Media.MINI_THUMB_MAGIC : "+str_5);
//            Log.e(LOG_TAG+"imageCusor", str_6);
            Log.e(LOG_TAG+"imageCusor", "7) MediaStore.MediaColumns.DATE_MODIFIED : " + str_7);
            Log.e(LOG_TAG + "imageCusor", "8) MediaStore.MediaColumns.DISPLAY_NAME : " + str_8);
            Log.e(LOG_TAG + "imageCusor", "9) MediaStore.MediaColumns.TITLE : " + str_9);


        }

        mGalleryImageAdapter = new mGalleryImageAdapter();
        mGridImages.setAdapter(mGalleryImageAdapter);

        imageCursor.close();
*/



        mSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final int len = mGalleryImageInfoArrayList.size();
//                final int len = thumbnailsselection.length;

                int cnt = 0;
                String selectImagesPath = "";
                String selectImagesOrientation = "";
                String selectImageAlbumName = "";

                for(int i=0; i<len; i++){

//                    if(thumbnailsselection[i]){
//                        cnt++;
//                        selectImagesPath = selectImagesPath + arrPath[i] + "|";
//                        selectImagesOrientation = selectImagesOrientation +arrOrientation[i] + "|";
//                    }

                    if(mGalleryImageInfoArrayList.get(i).ismCheckedState()){

                        cnt++;
                        selectImagesPath = selectImagesPath +mGalleryImageInfoArrayList.get(i).getmPath() + "|";
                        selectImagesOrientation = selectImagesOrientation + mGalleryImageInfoArrayList.get(i).getmOrientation() + "|";
                        selectImageAlbumName = selectImageAlbumName + mGalleryImageInfoArrayList.get(i).getmAlbumName() + "|";
                    }
                }

                if(cnt == 0){
                    Toast.makeText(getApplicationContext(), "Please select at least one image", Toast.LENGTH_SHORT).show();
                }else{

                    Log.d("selectImagesPath", selectImagesPath);
                    Intent intent = new Intent();
                    intent.putExtra("path", selectImagesPath);
                    intent.putExtra("orientation", selectImagesOrientation);
                    intent.putExtra("albumname", selectImageAlbumName);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        });
    }


    private long findGalleryImages() {

        long numOfImages = 0;

        final String[] columns = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.Images.Media.ORIENTATION,
                MediaStore.MediaColumns.DATE_MODIFIED,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
        };

        final String orderBy = MediaStore.Images.Media.DATE_ADDED;

        Cursor imageCursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);

//        numOfImages = imageCursor.getCount();
//
//
//        this.arrPath = new String[this.count];
//        this.arrOrientation = new String[this.count];
//
//        ids = new int[this.count];
//        this.thumbnailsselection = new boolean[this.count];
//


        if(imageCursor != null && imageCursor.getCount() >0){

            int imageIdColumn = imageCursor.getColumnIndex(MediaStore.Images.Media._ID);
            int imageDataColumn = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);
            int imageDisplayNameColumn = imageCursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);
            int imageOrientationColumn = imageCursor.getColumnIndex(MediaStore.Images.Media.ORIENTATION);
            int imageDateModifiedColumn = imageCursor.getColumnIndex(MediaStore.MediaColumns.DATE_MODIFIED);
            int imageBuckecDisplayNameColumn = imageCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

            imageCursor.moveToFirst();

            while(imageCursor.moveToNext()) {

                GalleryImageInfo galleryImageInfo = new GalleryImageInfo();

//            ids[i] = imageCursor.getInt(image_column_index);
//            arrPath[i] = imageCursor.getString(dataColumnIndex);
//            arrOrientation[i] = Integer.toString(imageCursor.getInt(uriColumnIndex));


                galleryImageInfo.setmId(imageCursor.getString(imageIdColumn));
                galleryImageInfo.setmPath(imageCursor.getString(imageDataColumn));
                galleryImageInfo.setmFileName(imageCursor.getString(imageDisplayNameColumn));
                galleryImageInfo.setmOrientation(imageCursor.getInt(imageOrientationColumn));
                galleryImageInfo.setmModifiedTime(imageCursor.getInt(imageDateModifiedColumn));
                galleryImageInfo.setmAlbumName(imageCursor.getString(imageBuckecDisplayNameColumn));
                galleryImageInfo.setmCheckedState(false);


                mGalleryImageInfoArrayList.add(galleryImageInfo);

                numOfImages++;
            }
        }

        imageCursor.close();
        return numOfImages;
    }


    private void updateUI(){

        mGalleryImageAdapter = new GalleryImageAdapter(this, R.layout.custom_gallery_item, mGalleryImageInfoArrayList);
        mGridImages.setAdapter(mGalleryImageAdapter);
    }



    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }

    private void setBitmap(final ImageView imageView, final int id){

        new AsyncTask<Void, Void, Bitmap>(){
            @Override
            protected Bitmap doInBackground(Void... params) {
                return MediaStore.Images.Thumbnails.getThumbnail(getApplicationContext().getContentResolver(), id, /*MediaStore.Images.Thumbnails.MICRO_KIND*/MediaStore.Images.Thumbnails.MINI_KIND, null);
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                imageView.setImageBitmap(bitmap);
            }
        }.execute();
    }






    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // ViewHolder, Adapter Class
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    class ViewHolder{
        LinearLayout background;
        ImageView image;
        int id;
        boolean selected;
    }


    public class GalleryImageAdapter extends BaseAdapter {

        private Context mContext;
        private int mCustomGalleryItem;
        private LayoutInflater mLayoutInflater;
        private ArrayList<GalleryImageInfo> mGalleryImageInfoArrayList;

        public GalleryImageAdapter(Context context, int galleryItem, ArrayList<GalleryImageInfo> galleryImageInfos){

            mContext = context;
            mCustomGalleryItem = galleryItem;
            mGalleryImageInfoArrayList = galleryImageInfos;

            mLayoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }


        @Override
        public int getCount() {

            return mGalleryImageInfoArrayList.size();
        }

        @Override
        public Object getItem(int position) {

            return mGalleryImageInfoArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {

            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null){

                convertView = mLayoutInflater.inflate(mCustomGalleryItem, parent, false);

                ViewHolder holder = new ViewHolder();
                holder.image = (ImageView) convertView.findViewById(R.id.image);
                holder.background = (LinearLayout)convertView.findViewById(R.id.gallery_item_background);

                convertView.setTag(holder);

            }

            final ViewHolder holder = (ViewHolder)convertView.getTag();

            if(((GalleryImageInfo)mGalleryImageInfoArrayList.get(position)).ismCheckedState()){

                holder.background.setBackgroundColor(getResources().getColor(R.color.image_active));
            }else{

                holder.background.setBackgroundColor(getResources().getColor(R.color.image_unactive));
            }





            holder.image.setId(position);

            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    int id = holder.image.getId();
//                    if (thumbnailsselection[id]) {
//                        holder.background.setBackgroundColor(getResources().getColor(R.color.image_unactive));
//                        thumbnailsselection[id] = false;
//
//                    } else {
//                        holder.background.setBackgroundColor(getResources().getColor(R.color.image_active));
//                        thumbnailsselection[id] = true;
//                    }
                    int id = holder.image.getId();

                    if (mGalleryImageInfoArrayList.get(id).ismCheckedState()) {
                        holder.background.setBackgroundColor(getResources().getColor(R.color.image_unactive));
                        mGalleryImageInfoArrayList.get(id).setmCheckedState(false);
                    } else {
                        holder.background.setBackgroundColor(getResources().getColor(R.color.image_active));
                        mGalleryImageInfoArrayList.get(id).setmCheckedState(true);
                    }
                }
            });


            if(!mBusy) {

                try {
//                    setBitmap(holder.image, ids[position]);
                    setBitmap(holder.image, Integer.parseInt(mGalleryImageInfoArrayList.get(position).getmId()));

                    holder.image.setVisibility(ImageView.VISIBLE);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }else{

                holder.image.setVisibility(ImageView.GONE);
            }

            holder.id = position;

            return convertView;
        }
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // ViewHolder, Adapter Class End
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~





    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // AsyncTask Class
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private class DoFindImageList extends AsyncTask<String, Integer, Long>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Long doInBackground(String... params) {
            long returnValue = 0;

            returnValue = findGalleryImages();

            return returnValue;
        }

        @Override
        protected void onPostExecute(Long aLong) {

            updateUI();
//            super.onPostExecute(aLong);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}
