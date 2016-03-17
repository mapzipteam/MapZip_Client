package com.mapzip.ppang.mapzipproject.wannabemap;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;

import com.mapzip.ppang.mapzipproject.R;

/**
 * Created by acekim on 16. 3. 17.
 */
public class DetailReviewDialog extends BottomSheetDialog {
    public DetailReviewDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_detailreview);
    }
}
