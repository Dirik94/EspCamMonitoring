package com.example.espcammonitoring;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImageViewerActivity extends AppCompatActivity {

    void requestDeletePermission(List<Uri> uriList) {
        PendingIntent pendingIntent = MediaStore.createDeleteRequest(this.getContentResolver(), uriList);
        try {
            Activity activity = (Activity) this;
            activity.startIntentSenderForResult(pendingIntent.getIntentSender(), 10, null, 0, 0,
                    0, null);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        String path = null;
        ImageView imageView = findViewById(R.id.imageView);
        Intent intent = getIntent();
        if (intent != null) {
            Glide.with(ImageViewerActivity.this).load(intent.getStringExtra("image")).placeholder(R.drawable.ic_baseline_broken_image_24).into(imageView);
            path = intent.getStringExtra("image");
        }

        ImageButton share = findViewById(R.id.shareImage);
        String finalPath = path;
        share.setOnClickListener(v -> new ShareCompat.IntentBuilder(ImageViewerActivity.this).setStream(Uri.parse(finalPath)).setType("image/*").setChooserTitle("Share Image").startChooser());

        ImageButton delete = findViewById(R.id.deleteImage);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] projection = new String[]{MediaStore.Images.Media._ID};
                String selection = MediaStore.Images.Media.DATA + " = ?";
                String[] selectionArgs = new String[]{new File(finalPath).getAbsolutePath()};
                Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver contentResolver = getContentResolver();
                Cursor cursor = contentResolver.query(queryUri, projection, selection, selectionArgs, null);
                if (cursor != null && cursor.moveToFirst()) {
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                    Uri deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                    try {
                        List<Uri> uriList = new ArrayList<>();
                        Collections.addAll(uriList, deleteUri);
                        requestDeletePermission(uriList);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(ImageViewerActivity.this, "Error deleting file", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ImageViewerActivity.this, "File not Found", Toast.LENGTH_SHORT).show();
                }
                cursor.close();
            }
        });
    }
}
