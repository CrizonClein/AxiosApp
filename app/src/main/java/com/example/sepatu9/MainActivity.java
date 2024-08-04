package com.example.sepatu9;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;
import android.provider.MediaStore;
import android.view.View;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.CvType;
import org.opencv.android.Utils;
import org.opencv.core.MatOfFloat;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.HOGDescriptor;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST_CODE = 2;
    private ImageView imageView;
    private TextView resultTextView;
    private Bitmap selectedImage;
    private SVMClassifier svmClassifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        resultTextView = findViewById(R.id.resultTextView);
        Button selectImageButton = findViewById(R.id.selectImageButton);
        Button classifyButton = findViewById(R.id.classifyButton);
        Button captureImageButton = findViewById(R.id.captureImageButton);

        // Load OpenCV library
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }

        // Initialize SVM classifier
        svmClassifier = new SVMClassifier();

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        captureImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });

        classifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                classifyImage();
            }
        });

        // Display the last classification result if available
        resultTextView.setText(getLastClassificationResult());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                imageView.setImageBitmap(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            selectedImage = (Bitmap) extras.get("data");
            imageView.setImageBitmap(selectedImage);
        }
    }

    private void classifyImage() {
        if (selectedImage != null) {
            new ClassificationTask().execute(selectedImage);
        }
    }

    private class ClassificationTask extends AsyncTask<Bitmap, Void, String> {

        @Override
        protected String doInBackground(Bitmap... bitmaps) {
            Bitmap bitmap = bitmaps[0];
            Mat matImage = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
            Utils.bitmapToMat(bitmap, matImage);

            // Convert image to grayscale
            Imgproc.cvtColor(matImage, matImage, Imgproc.COLOR_RGBA2GRAY);

            // Compute HOG descriptor
            HOGDescriptor hog = new HOGDescriptor();
            MatOfFloat descriptors = new MatOfFloat();
            hog.compute(matImage, descriptors);

            // Classify using SVM
            float[] featureArray = descriptors.toArray();
            return svmClassifier.classify(featureArray);
        }

        @Override
        protected void onPostExecute(String result) {
            resultTextView.setText(result);
            saveClassificationResult(result);
            Toast.makeText(MainActivity.this, "Klasifikasi selesai", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveClassificationResult(String result) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("last_classification", result);
        editor.apply();
    }

    private String getLastClassificationResult() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getString("last_classification", "No result available");
    }
}



