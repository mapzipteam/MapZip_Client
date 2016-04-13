package com.mapzip.ppang.mapzipproject.model;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.mapzip.ppang.mapzipproject.R;

/**
 * Created by ppangg on 2016-03-24.
 */
public class MapZipDialog extends Dialog {
    private final String TAG = "MapZipDialog";
    private int DialogNum = 0;

    private Button mPositiveBtn;
    private Button mNegativeBtn;

    private CheckBox[] mGoodCheckBoxes;
    private CheckBox[] mBadCheckBoxes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v_dialog);

        mPositiveBtn = (Button) findViewById(R.id.positive_btn_dialog);
        mNegativeBtn = (Button) findViewById(R.id.negative_btn_dialog);

        mNegativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick_NegativeBtn();
            }
        });
        mPositiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick_PositiveBtn();
            }
        });

        // layout setting
        switch (DialogNum){
            case SystemMain.DialogSet.NOMAL:
                return;
            case SystemMain.DialogSet.GOOD_CHECK_REVIEW_REGI:
                goodCheckCreate();
                return;
            case SystemMain.DialogSet.BAD_CHECK_REVIEW_REGI:
                badCheckCreate();
                return;
        }
    }

    public MapZipDialog(Context context, int DialogNum) {
        super(context);
        this.DialogNum = DialogNum;

        // setting
        switch (DialogNum){
            case SystemMain.DialogSet.NOMAL:
                return;
            case SystemMain.DialogSet.GOOD_CHECK_REVIEW_REGI:
                String[] goodReviewText = getContext().getResources().getStringArray(R.array.goodtext_review_regi);
                mGoodCheckBoxes = new CheckBox[goodReviewText.length];
                for(int i=0; i<goodReviewText.length; i++){
                    CheckBox checkBox = new CheckBox(getContext());
                    mGoodCheckBoxes[i] = checkBox;
                    mGoodCheckBoxes[i].setText(goodReviewText[i]);
                }
                return;
            case SystemMain.DialogSet.BAD_CHECK_REVIEW_REGI:
                String[] badReviewText = getContext().getResources().getStringArray(R.array.badtext_review_regi);
                mBadCheckBoxes = new CheckBox[badReviewText.length];
                for(int i=0; i<badReviewText.length; i++){
                    CheckBox checkBox = new CheckBox(getContext());
                    mBadCheckBoxes[i] = checkBox;
                    mBadCheckBoxes[i].setText(badReviewText[i]);
                }
                return;
        }
    }

    public MapZipDialog(Context context) {
        super(context);
    }

    private void goodCheckCreate(){
        LinearLayout layout = (LinearLayout) findViewById(R.id.dialog_layout);
        for(int i=0; i<mGoodCheckBoxes.length; i++){
            layout.addView(mGoodCheckBoxes[i]);
        }

        setTitle("칭찬해주세요 (0~5개 선택)");
    }

    private void badCheckCreate(){
        LinearLayout layout = (LinearLayout) findViewById(R.id.dialog_layout);
        for(int i=0; i<mBadCheckBoxes.length; i++){
            layout.addView(mBadCheckBoxes[i]);
        }

        setTitle("비판해주세요 (0~5개 선택)");
    }

    public void onClick_NegativeBtn(){
        dismiss();
    }

    public void onClick_PositiveBtn(){
        dismiss();
    }

    public CheckBox[] getmGoodCheckBoxes() {
        return mGoodCheckBoxes;
    }

    public CheckBox[] getmBadCheckBoxes() {
        return mBadCheckBoxes;
    }

    public Button getmPositiveBtn() {
        return mPositiveBtn;
    }
}

