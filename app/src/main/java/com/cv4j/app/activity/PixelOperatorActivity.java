/*
 * Copyright (c) 2017 - present, CV4J Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cv4j.app.activity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.widget.ImageView;

import com.cv4j.app.R;
import com.cv4j.app.app.BaseActivity;
import com.cv4j.core.datamodel.CV4JImage;
import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.core.datamodel.Rect;
import com.cv4j.core.pixels.Operator;
import com.cv4j.core.pixels.OperatorFunction;
import com.cv4j.exception.CV4JException;

import com.safframework.injectview.annotations.InjectExtra;
import com.safframework.injectview.annotations.InjectView;
import com.safframework.injectview.annotations.OnClick;

import java.util.concurrent.Callable;

/**
 * Created by tony on 2017/11/5.
 */

public class PixelOperatorActivity extends BaseActivity {

    @InjectView(R.id.image1)
    ImageView image1;

    @InjectView(R.id.image2)
    ImageView image2;

    @InjectView(R.id.result_image)
    ImageView result;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectExtra(key = "Title")
    String title;

    @InjectExtra(key = "Type")
    int type;

    SparseArray<OperatorFunction> operationsTwoImages;

    /**
     * Add operator.
     */
    public static final int ADD = 1;

    /**
     * Subtract operator.
     */
    public static final int SUBTRACT = 2;

    /**
     * Multiply operator.
     */
    public static final int MULTIPLE = 3;

    /**
     * Division operator.
     */
    public static final int DIVISION = 4;

    /**
     * Bitwise and operator.
     */
    public static final int BITWISE_AND = 5;

    /**
     * Bitwise or operator.
     */
    public static final int BITWISE_OR = 6;

    /**
     * Bitwise not operator.
     */
    public static final int BITWISE_NOT = 7;

    /**
     * Bitwise xor operator.
     */
    public static final int BITWISE_XOR = 8;

    /**
     * Add wright operator.
     */
    public static final int ADD_WEIGHT = 9;

    /**
     * Sub image operator.
     */
    public static final int SUB_IMAGE = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pixel_operator);

        initData();
    }

    private void initData() {
        toolbar.setTitle("< " + title);
        Resources res = getResources();

        final Bitmap bitmap1 = BitmapFactory.decodeResource(res, R.drawable.pixel_test_1);
        image1.setImageBitmap(bitmap1);

        final Bitmap bitmap2 = BitmapFactory.decodeResource(res, R.drawable.pixel_test_2);
        image2.setImageBitmap(bitmap2);

        CV4JImage cv4jImage1 = new CV4JImage(bitmap1);
        ImageProcessor imageProcessor1 = cv4jImage1.getProcessor();

        CV4JImage cv4jImage2 = new CV4JImage(bitmap2);
        ImageProcessor imageProcessor2 = cv4jImage2.getProcessor();

        operationsTwoImages = new SparseArray<>(8);
        operationsTwoImages.append(ADD, Operator::add);
        operationsTwoImages.append(SUBTRACT, Operator::subtract);
        operationsTwoImages.append(MULTIPLE, Operator::multiple);
        operationsTwoImages.append(DIVISION, Operator::division);
        operationsTwoImages.append(BITWISE_AND, Operator::bitwise_and);
        operationsTwoImages.append(BITWISE_OR, Operator::bitwise_or);

        OperatorFunction operator = operationsTwoImages.get(type);
        ImageProcessor imageProcessor = null;
        if (operator != null) {
            operator.call(imageProcessor1, imageProcessor2);
        } else {
            switch (type) {
                case BITWISE_NOT:
                    imageProcessor = Operator.bitwise_not(imageProcessor1);
                    break;

                case ADD_WEIGHT:
                    float weight1 = 2.0f;
                    float weight2 = 1.0f;
                    int gamma = 4;
                    imageProcessor = Operator.addWeight(imageProcessor1,weight1,imageProcessor2,weight2,gamma);
                    break;

                case SUB_IMAGE:
                    Rect rect = new Rect();
                    rect.x = 0;
                    rect.y = 0;
                    rect.width = 300;
                    rect.height = 300;

                    try {
                        imageProcessor = Operator.subImage(imageProcessor1, rect);
                    } catch (CV4JException e) {
                        System.out.println("CV4J error on sub image operator.");
                    }
                    break;

                default:
                    imageProcessor = Operator.add(imageProcessor1,imageProcessor2);
                    break;
            }
        }

        if (imageProcessor != null) {
            CV4JImage resultCV4JImage = new CV4JImage(imageProcessor.getWidth(), imageProcessor.getHeight(), imageProcessor.getPixels());
            result.setImageBitmap(resultCV4JImage.getProcessor().getImage().toBitmap());
        }
    }

    @OnClick(id= R.id.toolbar)
    void clickToolbar() {
        finish();
    }
}
