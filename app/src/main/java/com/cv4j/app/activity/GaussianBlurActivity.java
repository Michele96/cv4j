package com.cv4j.app.activity;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.cv4j.app.R;
import com.cv4j.app.app.BaseActivity;
import com.cv4j.core.filters.GaussianBlurFilter;
import com.cv4j.rxjava.RxImageData;
import com.safframework.injectview.annotations.InjectExtra;
import com.safframework.injectview.annotations.InjectView;
import com.safframework.injectview.annotations.OnClick;


/**
 * Created by Tony Shen on 2017/3/21.
 */

public class GaussianBlurActivity extends BaseActivity {

    @InjectView(R.id.image1)
    ImageView image1;

    @InjectView(R.id.image2)
    ImageView image2;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectExtra(key = "Title")
    String title;

    private Resources res;
    private Bitmap bitmap;

    private ProgressDialog progDailog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gaussian_blur);

        initData();
        useRenderScript();
        // 由于blur()中将bitmap回收啦，所以要重新赋值
        bitmap = BitmapFactory.decodeResource(res, R.drawable.test_filters);
        useCV4j();
    }

    private void initData() {
        toolbar.setTitle("< "+title);
        res = getResources();
        bitmap = BitmapFactory.decodeResource(res, R.drawable.test_filters);

        progDailog = ProgressDialog.show(this, "Loading", "Please wait...", true);
        progDailog.setCancelable(false);
    }

    private void useRenderScript() {
        image1.setImageBitmap(blur(bitmap));
    }

    private void useCV4j() {
        GaussianBlurFilter filter = new GaussianBlurFilter();
        int sigma = 10;
        filter.setSigma(sigma);

        RxImageData.bitmap(bitmap)
                .dialog(progDailog)
                .addFilter(filter)
                .into(image2);
    }

    /**
     * 使用RenderScript实现高斯模糊的算法
     * @param image
     * @return
     */
    public Bitmap blur(Bitmap image){
        //Let's create an empty bitmap with the same size of the bitmap we want to blur
        Bitmap outBitmap = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);
        //Instantiate a new Renderscript
        RenderScript rs = RenderScript.create(getApplicationContext());
        //Create an Intrinsic Blur Script using the Renderscript
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        //Create the Allocations (in/out) with the Renderscript and the in/out bitmaps
        Allocation allIn = Allocation.createFromBitmap(rs, image);
        Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);
        //Set the radius of the blur: 0 < radius <= 25
        int radius = 20.0f;
        blurScript.setRadius(radius);
        //Perform the Renderscript
        blurScript.setInput(allIn);
        blurScript.forEach(allOut);
        //Copy the final bitmap created by the out Allocation to the outBitmap
        allOut.copyTo(outBitmap);
        //recycle the original bitmap
        image.recycle();
        //After finishing everything, we destroy the Renderscript.
        rs.destroy();

        return outBitmap;

    }

    @OnClick(id= R.id.toolbar)
    void clickToolbar() {

        finish();
    }
}
