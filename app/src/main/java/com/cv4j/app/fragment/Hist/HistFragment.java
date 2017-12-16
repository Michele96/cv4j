package com.cv4j.app.fragment.Hist;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cv4j.app.R;
import com.cv4j.app.activity.Hist.CompareHistActivity;
import com.cv4j.app.activity.Histogram.HistogramDemoActivity;
import com.cv4j.app.activity.Histogram.HistogramEqualizationActivity;
import com.cv4j.app.activity.Hist.ProjectHistActivity;
import com.cv4j.app.app.BaseFragment;
import com.safframework.injectview.Injector;
import com.safframework.injectview.annotations.InjectView;
import com.safframework.injectview.annotations.OnClick;

/**
 * Created by Tony Shen on 2017/5/14.
 */

public class HistFragment extends BaseFragment {

    @InjectView(R.id.text1)
    TextView text1;

    @InjectView(R.id.text2)
    TextView text2;

    @InjectView(R.id.text3)
    TextView text3;

    @InjectView(R.id.text4)
    TextView text4;

    String title = "Title";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_hist, container, false);
        Injector.injectInto(this, v);

        return v;
    }

    @OnClick(id=R.id.text1)
    void clickText1() {

        Intent i = new Intent(mContext,HistogramEqualizationActivity.class);
        i.putExtra(title,text1.getText().toString());
        startActivity(i);
    }

    @OnClick(id=R.id.text2)
    void clickText2() {

        Intent i = new Intent(mContext, HistogramDemoActivity.class);
        i.putExtra(title,text2.getText().toString());
        startActivity(i);
    }

    @OnClick(id=R.id.text3)
    void clickText3() {

        Intent i = new Intent(mContext, CompareHistActivity.class);
        i.putExtra(title,text3.getText().toString());
        startActivity(i);
    }

    @OnClick(id=R.id.text4)
    void clickText4() {

        Intent i = new Intent(mContext, ProjectHistActivity.class);
        i.putExtra(title,text4.getText().toString());
        startActivity(i);
    }
}
