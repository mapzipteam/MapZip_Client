package com.mapzip.ppang.mapzipproject.model;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.mapzip.ppang.mapzipproject.R;

/**
 * Created by ppangg on 2016-03-24.
 */
public class MapZipDialog extends Dialog {
    private final String TAG = "MapZipDialog";
    private int DialogNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v_dialog);

        switch (DialogNum){
            case SystemMain.DialogSet.NOMAL:
                return;
            case SystemMain.DialogSet.GOOD_CHECK_REVIEW_REGI:
                goodCheckCreate();
                return;
            case SystemMain.DialogSet.BAD_CHECK_REVIEW_REGI:
                return;
        }
    }

    public MapZipDialog(Context context, int DialogNum) {
        super(context);
        this.DialogNum = DialogNum;
    }

    public MapZipDialog(Context context) {
        super(context);
    }

    private void goodCheckCreate(){
        String[] goodReviewText = getContext().getResources().getStringArray(R.array.goodtext_review_regi);

        LinearLayout layout = (LinearLayout) findViewById(R.id.dialog_layout);

        for(int i=0; i<goodReviewText.length; i++){
            CheckBox checkBox = new CheckBox(getContext());
            checkBox.setText(goodReviewText[i]);
            layout.addView(checkBox);
        }

        setTitle("칭찬해주세요");
    }
}

