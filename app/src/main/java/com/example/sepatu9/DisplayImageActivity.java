package com.example.sepatu9;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

public class DisplayImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);

        // Inisialisasi ImageView
        ImageView imageViewDisplay = findViewById(R.id.imageview_display);

        // Ambil gambar yang telah dipotret dari intent
        Bitmap photo = getIntent().getParcelableExtra("photo");

        // Tampilkan gambar di ImageView
        if (photo != null) {
            imageViewDisplay.setImageBitmap(photo);
        }
    }
}
