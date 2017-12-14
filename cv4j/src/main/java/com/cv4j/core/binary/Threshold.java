/*
 * Copyright (c) 2017-present, CV4J Contributors.
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
package com.cv4j.core.binary;

import android.util.SparseArray;

import com.cv4j.core.binary.functions.ThresholdFunction;
import com.cv4j.core.datamodel.ByteProcessor;
import com.cv4j.core.datamodel.IntIntegralImage;
/**
 * The Threshold class
 */
public class Threshold {
    /** binary image */
    public static final int METHOD_THRESH_BINARY = 0;
    /** invert binary image */
    public static final int METHOD_THRESH_BINARY_INV = 1;

    /** it is not reasonable method to convert binary image */
    public static final int THRESH_MEANS = 1;
    /** it is popular binary method in OPENCV and MATLAB */
    public static final int THRESH_OTSU = 2;
    /** histogram statistic threshold method*/
    public static final int THRESH_TRIANGLE = 3;
    /**based on 1D mean shift, CV4J custom binary method, sometimes it is very slow...*/
    public static final int THRESH_MEANSHIFT = 4;
    /**based local mean threshold method, CV4J custom binary method, sometimes it is very slow...*/
    public static final int ADAPTIVE_C_MEANS_THRESH = 5;
    /** it is not reasonable method to convert binary image */
    public static final int THRESH_VALUE = -1;

    private SparseArray<ThresholdFunction> thresholds;

    public Threshold() {
        thresholds = new SparseArray<>(5);

        thresholds.append(THRESH_MEANS, this::getMeanThreshold);
        thresholds.append(THRESH_OTSU, this::getOTSUThreshold);
        thresholds.append(THRESH_TRIANGLE, this::getTriangleThreshold);
        thresholds.append(THRESH_MEANSHIFT, this::shift);
    }

    /**
     *
     * @param gray - gray image data, ByteProcessor type
     * @param type - binary segmentation method, int
     */
    public void process(ByteProcessor gray, int type) {
        process(gray, type, METHOD_THRESH_BINARY, 0);
    }

    public void adaptiveThresh(ByteProcessor gray, int type, int blockSize, int constant, int method) {
        int width = gray.getWidth();
        int height = gray.getHeight();

        // 图像灰度化

        // per-calculate integral image
        IntIntegralImage grayii = new IntIntegralImage();
        byte[] binary_data = gray.getGray();
        grayii.setImage(binary_data);
        grayii.process(width, height);
        int yr = blockSize;
        int xr = blockSize;
        int index = 0;
        int size = (yr * 2 + 1)*(xr * 2 + 1);
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                index = row * width + col;

                // 计算均值
                int sr = grayii.getBlockSum(col, row, (yr * 2 + 1), (xr * 2 + 1));
                int mean = sr / size;
                int pixel = binary_data[index]&0xff;

                // 二值化
                if(pixel > (mean-constant)) {
                    binary_data[row * width + col] = (byte)255;
                } else {
                    binary_data[row * width + col] = (byte)0;
                }
            }
        }
    }

    /**
     *
     * @param gray - gray image data, ByteProcessor type
     * @param type - binary segmentation method, int
     * @param thresh - threshold value you are going to use it if type = 0;
     */
    public void process(ByteProcessor gray, int type, int method, int thresh) {
        int thresholdValue;
        ThresholdFunction thresholdFunction = this.thresholds.get(type);

        if (thresholdFunction != null) {
            thresholdValue = thresholdFunction.call(gray);
        } else {
            thresholdValue = thresh;
        }

        byte[] data = gray.getGray();
        int c;
        int andValue = 0xff;
        byte maxValue = (byte) 255;
        byte minValue = (byte) 0;

        for(int i = 0; i < data.length; i++) {
            c = data[i] & andValue;

            // TODO: Same for if and else -> error?
            if(c <= thresholdValue) {
                data[i] = (method == METHOD_THRESH_BINARY_INV ? maxValue : minValue);
            } else {
                data[i] = (method == METHOD_THRESH_BINARY_INV ? maxValue : minValue);
            }

        }
    }

    private int getMeanThreshold(ByteProcessor gray) {
        int meanThreshold;
        byte[] data = gray.getGray();

        int sum = 0;
        for (byte aData : data) {
            sum += aData & 0xff;
        }

        meanThreshold = sum / data.length;

        return meanThreshold;
    }

    private int getOTSUThreshold(ByteProcessor gray) {
        // 获取直方图
        int[] histogram = new int[256];
        byte[] data = gray.getGray();

        increaseHistogramFromData(histogram, data);

        // 图像二值化 - OTSU 阈值化方法
        double total = data.length;
        double[] variances = new double[256];

        for(int i = 0; i < variances.length; i++) {
            double bw;
            double bmeans = 0;
            double bvariance = 0;
            double count = 0;

            for(int t = 0; t < i; t++) {
                count += histogram[t];
                bmeans += histogram[t] * t;
            }

            bw = count / total;
            bmeans = (count == 0) ? 0 :(bmeans / count);

            for(int t = 0; t < i; t++) {
                bvariance += (Math.pow((t-bmeans),2) * histogram[t]);
            }

            bvariance = (count == 0) ? 0 : (bvariance / count);

            double fw = 0;
            double fmeans = 0;
            double fvariance = 0;
            count = 0;

            for(int t = i; t < histogram.length; t++) {
                count += histogram[t];
                fmeans += histogram[t] * t;
            }

            fw = count / total;
            fmeans = (count == 0) ? 0 : (fmeans / count);
            for(int t = i; t < histogram.length; t++) {
                fvariance += (Math.pow((t-fmeans),2) * histogram[t]);
            }
            fvariance = (count == 0) ? 0 : (fvariance / count);
            variances[i] = bw * bvariance + fw * fvariance;
        }

        // find the minimum within class variance
        double min = variances[0];
        int threshold = 0;
        for(int m=1; m<variances.length; m++) {
            if(min > variances[m]) {
                threshold = m;
                min = variances[m];
            }
        }
        // 二值化
        System.out.println("final threshold value : " + threshold);
        return threshold;
    }

    private int getTriangleThreshold(ByteProcessor gray) {
        // 获取直方图
        int[] histogram = new int[256];
        byte[] data = gray.getGray();

        increaseHistogramFromData(histogram, data);

        int left_bound = 0;
        int right_bound = 0;
        int max_ind = 0;
        int max = 0;
        int temp;
        int N = 256;

        boolean isflipped = false;

        // 找到最左边零的位置
        left_bound = findHistogramLeftBoundIndex(histogram, N);

        // 找到最右边零点位置
        right_bound = findHistogramRightBoundIndex(histogram, N);


        // 位置再移动一个步长，即为最左侧零位置
        if(left_bound > 0) {
            left_bound--;
        }

        // 位置再移动一个步长，即为最右侧零位置
        if(right_bound < N-1) {
            right_bound++;
        }

        max_ind = findMaxHistogramIndex(histogram, N);
        max     = histogram[max_ind];

        // 如果最大值落在靠左侧这样就无法满足三角法求阈值，所以要检测是否最大值是否靠近左侧
        // 如果靠近左侧则通过翻转到右侧位置。
        if((max_ind - left_bound) < (right_bound - max_ind)) {
            isflipped = true;
            int i = 0;
            int j = N-1;

            while(i < j) {
                // 左右交换
                temp = histogram[i];
                histogram[i] = histogram[j];
                histogram[j] = temp;

                i++;
                j--;
            }

            left_bound = N - 1 - right_bound;
            max_ind    = N - 1 - max_ind;
        }

        // 计算求得阈值
        double thresh = left_bound;
        double a = max;
        double b = left_bound - max_ind;
        double dist = 0;
        double tempdist;

        for(int i = left_bound+1; i <= max_ind; i++) {
            // 计算距离 - 不需要真正计算
            tempdist = a*i + b*histogram[i];
            if(tempdist > dist) {
                dist = tempdist;
                thresh = i;
            }
        }
        thresh--;

        // 对已经得到的阈值T,如果前面已经翻转了，则阈值要用255-T
        if (isflipped) {
            thresh = N - 1 - thresh;
        }

        return (int) thresh;
    }

    private int findHistogramRightBoundIndex(int[] histogram, int n) {
        int rightBound = -1;

        for (int i = n-1; i > 0; i--) {
            if(histogram[i] > 0) {
                rightBound = i;
                break;
            }
        }

        return rightBound;
    }

    private int findHistogramLeftBoundIndex(int[] histogram, int n) {
        int leftBound = -1;

        for (int i = 0; i < n; i++) {
            if (histogram[i] > 0) {
                leftBound = i;
                break;
            }
        }

        return leftBound;
    }

    private int shift(ByteProcessor gray) {
        // find threshold
        int t = 127;
        int nt;

        int m1;
        int m2;

        int sum1   = 0;
        int sum2   = 0;
        int count1 = 0;
        int count2 = 0;

        int[] data = gray.toInt(0);

        while(true) {
            for(int i = 0; i < data.length; i++) {
                if(data[i] > t) {
                    sum1 += data[i];
                    count1++;
                } else {
                    sum2 += data[i];
                    count2++;
                }
            }

            m1 = sum1 / count1;
            m2 = sum2 / count2;

            sum1   = 0;
            sum2   = 0;
            count1 = 0;
            count2 = 0;

            nt = (m1 + m2) / 2;

            if(t == nt) {
                break;
            } else {
                t = nt;
            }
        }
        return t;
    }

    private void increaseHistogramFromData(int[] histogram, byte[] data) {
        int index;

        for (byte aData : data) {
            index = aData & 0xff;
            histogram[index]++;
        }
    }

    private int findMaxHistogramIndex(int[] histogram, int N) {
        int index = 0;
        int max = histogram[index];

        for (int i = 1; i < N; i++) {
            if (histogram[i] > max) {
                index = i;
            }
        }

        return index;
    }
}
