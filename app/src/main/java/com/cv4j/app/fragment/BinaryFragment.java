package com.cv4j.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cv4j.app.R;
import com.cv4j.app.activity.Example.CoinsActivity;
import com.cv4j.app.activity.ContourAnalysisActivity;
import com.cv4j.app.activity.Detect.DetectQRActivity;
import com.cv4j.app.activity.Detect.LineDetectionActivity;
import com.cv4j.app.activity.MorphologyActivity;
import com.cv4j.app.app.BaseFragment;
import com.safframework.injectview.Injector;
import com.safframework.injectview.annotations.InjectView;
import com.safframework.injectview.annotations.OnClick;

/**
 * Created by Tony Shen on 2017/4/16.
 */

public class BinaryFragment extends BaseFragment {

    @InjectView(R.id.text1)
    TextView text1;

    @InjectView(R.id.text2)
    TextView text2;

    @InjectView(R.id.text3)
    TextView text3;

    @InjectView(R.id.text4)
    TextView text4;

    @InjectView(R.id.text5)
    TextView text5;

    String title = "Title";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_binary, container, false);
        Injector.injectInto(this, v);

        return v;
    }

    @OnClick(id=R.id.text1)
    void clickText1() {

        Intent i = new Intent(mContext,MorphologyActivity.class);
        i.putExtra(title,text1.getText().toString());
        startActivity(i);
    }

    @OnClick(id=R.id.text2)
    void clickText2() {

        Intent i = new Intent(mContext,CoinsActivity.class);
        i.putExtra(title,text2.getText().toString());
        startActivity(i);
    }

    @OnClick(id=R.id.text3)
    void clickText3() {

        Intent i = new Intent(mContext,ContourAnalysisActivity.class);
        i.putExtra(title,text3.getText().toString());
        startActivity(i);
    }

    @OnClick(id=R.id.text4)
    void clickText4() {

        Intent i = new Intent(mContext,LineDetectionActivity.class);
        i.putExtra(title,text4.getText().toString());
        startActivity(i);
    }

    @OnClick(id=R.id.text5)
    void clickText5() {

        Intent i = new Intent(mContext,DetectQRActivity.class);
        i.putExtra(title,text5.getText().toString());
        startActivity(i);
    }
}
