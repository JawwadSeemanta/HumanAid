package com.humanaid.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.humanaid.R;
import com.humanaid.usefulClasses.ConnectionCheck;
import com.squareup.picasso.Picasso;

public class ImageViewerActivity extends AppCompatActivity {

    private String url;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        imageView = (ImageView) findViewById(R.id.image_viewer);

        url = getIntent().getStringExtra("url");
        if (ConnectionCheck.isOnline(this)) {
            Picasso.get()
                    .load(url)
                    .fit()
                    .into(imageView);
        } else {
            Toast.makeText(this, "Please Connect to Internet and Try again!!", Toast.LENGTH_SHORT).show();
            finish();
        }

    }
}