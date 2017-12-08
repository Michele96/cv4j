package com.cv4j.app.activity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.cv4j.app.R;
import com.cv4j.app.app.BaseActivity;
import com.cv4j.core.datamodel.CV4JImage;
import com.cv4j.core.filters.CommonFilter;
import com.safframework.aop.annotation.Trace;
import com.safframework.injectview.annotations.InjectExtra;
import com.safframework.injectview.annotations.InjectView;
import com.safframework.injectview.annotations.OnClick;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tony Shen on 2017/3/11.
 */

public class SelectFilterActivity extends BaseActivity {

    @InjectView(R.id.image)
    ImageView image;

    @InjectView(R.id.spinner)
    Spinner spinner;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectExtra(key = "Title")
    String title;

    private Bitmap bitmap;

    private List<String> list = new ArrayList<>();

    private ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_filter);

        initViews();
        initData();
    }

    private void initViews() {
        toolbar.setTitle("< "+title);
    }

    private void initData() {
        Resources res = getResources();

        String[] filterNames = res.getStringArray(R.array.filterNames);
        bitmap = BitmapFactory.decodeResource(res, R.drawable.test_filters);
        image.setImageBitmap(bitmap);

        for (String filter:filterNames) {
            list.add(filter);
        }

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                if (position == 0) {
                    image.setImageBitmap(bitmap);
                    return;
                }

                // 清除滤镜
                image.clearColorFilter();

                String filterName = (String) adapter.getItem(position);
                changeFilter(filterName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private Object getFilter(String filterName) {

        Object object = null;
        String className = filterName + "Filter";
        
        try {
            object = Class.forName("com.cv4j.core.filters." + className).newInstance();
        } catch (ClassNotFoundException e) {
            System.out.println("Class " + filter + " not found");
        } catch (InstantiationException e) {
            System.out.println("Instantiation error for class " + className);
        } catch (IllegalAccessException e) {
            System.out.println("Illegal acces error for class " + className);
        }

        return object;
    }

    @Trace
    public void changeFilter(String filterName) {
        CV4JImage colorImage = new CV4JImage(bitmap);
        CommonFilter filter = (CommonFilter)getFilter(filterName);
        if (filter!=null) {

            image.setImageBitmap(filter.filter(colorImage.getProcessor()).getImage().toBitmap());
        }
    }

    @OnClick(id= R.id.toolbar)
    void clickToolbar() {

        finish();
    }
}
