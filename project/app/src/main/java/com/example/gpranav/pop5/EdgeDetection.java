package com.example.gpranav.pop5;

/**
 * Created by gpranav on 6/10/17.
 */

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class EdgeDetection extends AppCompatActivity {
    final static int KERNAL_WIDTH = 3;
    final static int KERNAL_HEIGHT = 3;
    int[][] kernal ={{0, -1, 0},{-1, 4, -1},{0, -1, 0}};
    ImageView imageAfter;
    Bitmap bitmap_Source;
    ProgressBar progressBar;

    private Handler handler;
    Bitmap afterProcess;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edge_detect);

        imageAfter = (ImageView)findViewById(R.id.imageAfter);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        MainActivity.imageView.buildDrawingCache();
        bitmap_Source = MainActivity.imageView.getDrawingCache();

        handler = new Handler();
        StratBackgroundProcess();
        }

        private void StratBackgroundProcess(){

        Runnable runnable = new Runnable(){

        @Override
        public void run() {
            afterProcess = processingBitmap(bitmap_Source, kernal);

            handler.post(new Runnable(){

            @Override
            public void run() {
        progressBar.setVisibility(View.GONE);
        imageAfter.setImageBitmap(afterProcess);
        }

        });
        }
        };
        new Thread(runnable).start();
        }

        private Bitmap processingBitmap(Bitmap src, int[][] knl){
        Bitmap dest = Bitmap.createBitmap(
        src.getWidth(), src.getHeight(), src.getConfig());

        int bmWidth = src.getWidth();
        int bmHeight = src.getHeight();
        int bmWidth_MINUS_2 = bmWidth - 2;
        int bmHeight_MINUS_2 = bmHeight - 2;

        for(int i = 1; i <= bmWidth_MINUS_2; i++){
        for(int j = 1; j <= bmHeight_MINUS_2; j++){

        int[][] subSrc = new int[KERNAL_WIDTH][KERNAL_HEIGHT];
        for(int k = 0; k < KERNAL_WIDTH; k++){
            for(int l = 0; l < KERNAL_HEIGHT; l++){
                subSrc[k][l] = src.getPixel(i-1+k, j-1+l);
            }
        }

        int subSumA = 0;
        int subSumR = 0;
        int subSumG = 0;
        int subSumB = 0;

        for(int k = 0; k < KERNAL_WIDTH; k++){
            for(int l = 0; l < KERNAL_HEIGHT; l++){
            subSumR += Color.red(subSrc[k][l]) * knl[k][l];
            subSumG += Color.green(subSrc[k][l]) * knl[k][l];
            subSumB += Color.blue(subSrc[k][l]) * knl[k][l];
            }
        }

        subSumA = Color.alpha(src.getPixel(i, j));

        if(subSumR <0){
            subSumR = 0;
        }else if(subSumR > 255){
            subSumR = 255;
        }

        if(subSumG <0){
            subSumG = 0;
        }else if(subSumG > 255){
            subSumG = 255;
        }

        if(subSumB <0){
            subSumB = 0;
        }else if(subSumB > 255){
            subSumB = 255;
        }

            dest.setPixel(i, j, Color.argb(
            subSumA,
            subSumR,
            subSumG,
            subSumB));
        }
        }
            return dest;
        }
}