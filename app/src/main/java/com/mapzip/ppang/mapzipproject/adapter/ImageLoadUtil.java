package com.mapzip.ppang.mapzipproject.adapter;

import android.util.Log;

import com.mapzip.ppang.mapzipproject.model.SystemMain;

/**
 * Created by ppang on 16. 5. 15..
 */
public class ImageLoadUtil {

    public static String[] getImageURLArr(String userID, String storeID, int imageCount) {
        String[] imageUrlArr = new String[imageCount];
        for(int imageID=0; imageID<imageCount; imageID++){
            String imageUrl;
            imageUrl = SystemMain.SERVER_ROOT_URL
                    + "/client_data/client_" + userID
                    + "/store_" + storeID
                    + "/image" + imageID + ".jpg";

            imageUrlArr[imageID] = imageUrl;
        }

        return imageUrlArr;
    }
}
