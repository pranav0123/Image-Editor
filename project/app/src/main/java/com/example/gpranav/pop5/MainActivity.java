package com.example.gpranav.pop5;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;
import static org.opencv.core.CvType.CV_8UC1;

public class MainActivity extends AppCompatActivity {

    private int RESULT_LOAD_IMAGE = 1;
    public float contrast = 0;
    public static ImageView imageView;
    public float brightness = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonLoadImage = (Button) findViewById(R.id.button_id);
        Button buttonRotateImage = (Button) findViewById(R.id.button);
        Button buttonNoiseImage = (Button) findViewById(R.id.noise);
        Button buttonCapture = (Button) findViewById(R.id.btnCapture);
        Button buttonEdge = (Button) findViewById(R.id.edge);
        Button buttonDecrease = (Button) findViewById(R.id.decrease);
        TextView textBrightness = (TextView) findViewById(R.id.bright);
        Button buttonIncrease = (Button) findViewById(R.id.increase);

        buttonDecrease.setVisibility(View.GONE);
        buttonIncrease.setVisibility(View.GONE);
        textBrightness.setVisibility(View.GONE);
        buttonRotateImage.setVisibility(View.GONE);
        buttonNoiseImage.setVisibility(View.GONE);
        buttonEdge.setVisibility(View.GONE);

        buttonLoadImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);

            }

        });


        buttonCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent var = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(var);
            }
        });
        buttonEdge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                imageView.buildDrawingCache();
                Bitmap bmap = imageView.getDrawingCache();
                detectEdges(bmap);
            }
        });


        buttonRotateImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                imageView.setRotation(imageView.getRotation() + 90);
                String mCurrentPhotoPath = Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DCIM + "/" + "Camera/";
                System.out.println(mCurrentPhotoPath);

            }
        });

        buttonNoiseImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                imageView.buildDrawingCache();
                Bitmap bmap = imageView.getDrawingCache();
                removeNoise(bmap);

            }
        });


        buttonIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                imageView.buildDrawingCache();
                Bitmap mBitmap = imageView.getDrawingCache();
                contrast += 0.25;
                brightness += 0.5;
                ColorMatrix cm = new ColorMatrix(new float[]
                        {
                                contrast, 0, 0, 0, brightness,
                                0, contrast, 0, 0, brightness,
                                0, 0, contrast, 0, brightness,
                                0, 0, 0, 1, 0
                        });
                Bitmap mEnhancedBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), mBitmap
                        .getConfig());
                Canvas canvas = new Canvas(mEnhancedBitmap);
                Paint paint = new Paint();
                paint.setColorFilter(new ColorMatrixColorFilter(cm));
                canvas.drawBitmap(mBitmap, 0, 0, paint);
                imageView.setImageBitmap(mEnhancedBitmap);
            }
        });

        buttonDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                imageView.buildDrawingCache();
                Bitmap mBitmap = imageView.getDrawingCache();
                contrast -= 0.25;
                brightness -= 0.5;
                ColorMatrix cm = new ColorMatrix(new float[]
                        {
                                contrast, 0, 0, 0, brightness,
                                0, contrast, 0, 0, brightness,
                                0, 0, contrast, 0, brightness,
                                0, 0, 0, 1, 0
                        });
                Bitmap mEnhancedBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), mBitmap
                        .getConfig());
                Canvas canvas = new Canvas(mEnhancedBitmap);
                Paint paint = new Paint();
                paint.setColorFilter(new ColorMatrixColorFilter(cm));
                canvas.drawBitmap(mBitmap, 0, 0, paint);
                imageView.setImageBitmap(mEnhancedBitmap);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            imageView = (ImageView) findViewById(R.id.ImageViewId);
            imageView.setImageURI(imageUri);
            imageView.setImageURI(imageUri);
            Button buttonLoadImage = (Button) findViewById(R.id.button_id);
            Button buttonRotateImage = (Button) findViewById(R.id.button);
            Button buttonNoiseImage = (Button) findViewById(R.id.noise);
            Button buttonCapture = (Button) findViewById(R.id.btnCapture);
            Button buttonEdge = (Button) findViewById(R.id.edge);
            Button buttonDecrease = (Button) findViewById(R.id.decrease);
            TextView textBrightness = (TextView) findViewById(R.id.bright);
            Button buttonIncrease = (Button) findViewById(R.id.increase);
            buttonDecrease.setVisibility(View.VISIBLE);
            buttonIncrease.setVisibility(View.VISIBLE);
            textBrightness.setVisibility(View.VISIBLE);
            buttonLoadImage.setVisibility(View.GONE);
            buttonCapture.setVisibility(View.GONE);
            buttonRotateImage.setVisibility(View.VISIBLE);
            buttonNoiseImage.setVisibility(View.VISIBLE);
            buttonEdge.setVisibility(View.VISIBLE);

        }

    }

    Mat imageMat;
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i("OpenCV", "OpenCV loaded successfully");
                    imageMat = new Mat();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    public Bitmap RemoveNoise(Bitmap bmap) {
        for (int x = 0; x < bmap.getWidth(); x++) {
            for (int y = 0; y < bmap.getHeight(); y++) {
                int pixel = bmap.getPixel(x, y);
                int R = Color.red(pixel);
                int G = Color.green(pixel);
                int B = Color.blue(pixel);
                if (R < 162 && G < 162 && B < 162)
                    bmap.setPixel(x, y, Color.BLACK);
            }
        }
        for (int x = 0; x < bmap.getWidth(); x++) {
            for (int y = 0; y < bmap.getHeight(); y++) {
                int pixel = bmap.getPixel(x, y);
                int R = Color.red(pixel);
                int G = Color.green(pixel);
                int B = Color.blue(pixel);
                if (R > 162 && G > 162 && B > 162)
                    bmap.setPixel(x, y, Color.WHITE);
            }
        }
        return bmap;
    }

    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void removeNoise(Bitmap bitmap) {
        Mat matrixImage = new Mat();
        Utils.bitmapToMat(bitmap, matrixImage);
        Photo.fastNlMeansDenoisingColored(matrixImage, matrixImage, 10, 10, 7, 21);

        Bitmap resultBitmap = Bitmap.createBitmap(matrixImage.cols(), matrixImage.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(matrixImage, resultBitmap);
        imageView.setImageBitmap(resultBitmap);

    }

    public void detectEdges(Bitmap bitmap) {
        Mat rgba = new Mat();
        Utils.bitmapToMat(bitmap, rgba);

        Mat edges = new Mat(rgba.size(), CV_8UC1);
        Imgproc.cvtColor(rgba, edges, Imgproc.COLOR_RGB2GRAY, 4);
        Imgproc.Canny(edges, edges, 80, 100);

        Bitmap resultBitmap = Bitmap.createBitmap(edges.cols(), edges.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(edges, resultBitmap);
        imageView.setImageBitmap(resultBitmap);
    }
}
