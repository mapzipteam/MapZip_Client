package com.mapzip.ppang.mapzipproject.model;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
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
        LinearLayout layout = (LinearLayout) findViewById(R.id.dialog_layout);

        Button gg = new Button(getContext());
        gg.setText("testBtn");

        layout.addView(gg);

        setTitle("test");
    }

}

