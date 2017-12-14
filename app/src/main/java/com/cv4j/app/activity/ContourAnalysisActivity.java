package com.cv4j.app.activity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.SparseIntArray;
import android.widget.ImageView;

import com.cv4j.app.R;
import com.cv4j.app.app.BaseActivity;
import com.cv4j.core.binary.ConnectedAreaLabel;
import com.cv4j.core.binary.ContourAnalysis;
import com.cv4j.core.binary.Threshold;
import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.CV4JImage;
import com.cv4j.core.datamodel.MeasureData;
import com.safframework.injectview.annotations.InjectExtra;
import com.safframework.injectview.annotations.InjectView;
import com.safframework.injectview.annotations.OnClick;
import com.safframework.log.L;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Tony Shen on 2017/5/1.
 */

public class ContourAnalysisActivity extends BaseActivity {

    @InjectView(R.id.image0)
    ImageView image0;

    @InjectView(R.id.image1)
    ImageView image1;

    @InjectView(R.id.image2)
    ImageView image2;

    @InjectView(R.id.image3)
    ImageView image3;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectExtra(key = "Title")
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contour_analysis);

        initData();
    }

    private void initData() {
        final int MAX_RGB = 255;

        toolbar.setTitle("< "+title);
        Resources res = getResources();
        final Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.test_ca);
        image0.setImageBitmap(bitmap);

        CV4JImage cv4JImage = new CV4JImage(bitmap);
        Threshold threshold = new Threshold();
        threshold.process((ByteProcessor)(cv4JImage.convert2Gray().getProcessor()),Threshold.THRESH_OTSU,Threshold.METHOD_THRESH_BINARY, MAX_RGB);
        image1.setImageBitmap(cv4JImage.getProcessor().getImage().toBitmap());

        ConnectedAreaLabel connectedAreaLabel = new ConnectedAreaLabel();
        connectedAreaLabel.setFilterNoise(true);
        int[] mask = new int[cv4JImage.getProcessor().getWidth() * cv4JImage.getProcessor().getHeight()];
        connectedAreaLabel.process((ByteProcessor)cv4JImage.getProcessor(),mask,null,false);

        ActivityUtility util = new ActivityUtility();
        util.subInitData(cv4JImage, mask);

        int height = cv4JImage.getProcessor().getHeight();
        int width = cv4JImage.getProcessor().getWidth();
        int size = height * width;
        for (int i = 0;i<size;i++) {
            int c = mask[i];
            if (c>=0) {
                colors.put(c, Color.argb(MAX_RGB, random.nextInt(MAX_RGB),random.nextInt(MAX_RGB),random.nextInt(MAX_RGB)));
            }
        }

        cv4JImage.resetBitmap();
        Bitmap newBitmap = cv4JImage.getProcessor().getImage().toBitmap();

        for(int row=0; row<height; row++) {
            for (int col = 0; col < width; col++) {

                int c = mask[row*width+col];
                if (c>=0) {
                    newBitmap.setPixel(col,row,colors.get(c));
                }
            }
        }

        image2.setImageBitmap(newBitmap);

        // 轮廓分析
        Bitmap thirdBitmap = Bitmap.createBitmap(newBitmap);
        ContourAnalysis ca = new ContourAnalysis();
        List<MeasureData> measureDatas = new ArrayList<>();
        ca.process((ByteProcessor)(cv4JImage.convert2Gray().getProcessor()),mask,measureDatas);

        Canvas canvas = new Canvas(thirdBitmap);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        for (MeasureData data:measureDatas) {
            canvas.drawText(data.toString(),data.getCp().x,data.getCp().y,paint);
            L.i(data.toString());
        }
        image3.setImageBitmap(thirdBitmap);
    }

    @OnClick(id= R.id.toolbar)
    void clickToolbar() {

        finish();
    }
}
