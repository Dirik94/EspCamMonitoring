package com.example.espcammonitoring

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.View
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var webView = findViewById<WebView>(R.id.webView)
        webView.webViewClient = WebViewClientMy()

        // this will load the url of the website
        webView.loadUrl("http://192.168.1.5:81/stream")

        // this will enable the javascript settings, it can also allow xss vulnerabilities
        webView.settings.javaScriptEnabled = true

        // if you want to enable zoom feature
        webView.settings.setSupportZoom(true)

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
            takeScreenshot(webView)
        }
    }

    private fun takeScreenshot(view: View): File? {
        val date = Date()
        val format = "yyyy-MM-dd-HH.mm.SS"
        val dateFormat = SimpleDateFormat(format)
        val formattedDate = dateFormat.format(date)

        try {
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

class WebViewClientMy : WebViewClient() {
    @Override
    override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler, error: SslError?) {
        handler.proceed() // Ignore SSL certificate errors
    }
}