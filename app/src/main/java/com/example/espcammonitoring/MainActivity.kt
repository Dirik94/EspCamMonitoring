package com.example.espcammonitoring

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // WebViewClient allows you to handle
        // onPageFinished and override Url loading.
        val myWebView: WebView = findViewById(R.id.webView)

        // this will load the url of the website
        myWebView.loadUrl("http://192.168.1.5:81/stream")

        // this will enable the javascript settings, it can also allow xss vulnerabilities
        myWebView.settings.javaScriptEnabled = true

        // if you want to enable zoom feature
        myWebView.settings.setSupportZoom(true)

        if (!Environment.isExternalStorageManager()) {
            val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
            startActivity(intent)
        }

        val buttonGallery: Button = findViewById(R.id.gallery)
        buttonGallery.setOnClickListener {
            val myIntent = Intent(this@MainActivity, GalleryActivity::class.java)
            this@MainActivity.startActivity(myIntent)
        }

        val buttonPhoto: Button = findViewById(R.id.take_photo)
        buttonPhoto.setOnClickListener {
            // pass layout view to method to perform capture screenshot and save it.
            takeScreenshot(window.decorView.rootView)
        }
    }

    private fun takeScreenshot(view: View): File? {
        val date = Date()
        val format = "yyyy-MM-dd-HH.mm.SS"
        val dateFormat = SimpleDateFormat(format)
        val formattedDate = dateFormat.format(date)

        try {
            // File name : keeping file name unique using data time.
            val path = Environment.getExternalStorageDirectory().toString() + File.separator + "espCam" + "/${formattedDate}.jpg"
            view.isDrawingCacheEnabled = true
            val bitmap = Bitmap.createBitmap(view.drawingCache)
            view.isDrawingCacheEnabled = false
            val imageurl = File(path)
            val outputStream = FileOutputStream(imageurl)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
            outputStream.flush()
            outputStream.close()
            Log.d(TAG, "takeScreenshot Path: $imageurl")
            return imageurl
        } catch (io: FileNotFoundException) {
            Toast.makeText(this, "Failed to create file", Toast.LENGTH_SHORT).show()
            io.printStackTrace()
        } catch (e: IOException) {
            Toast.makeText(this, "Failed to create file", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
        return null
    }
}