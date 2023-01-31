package com.example.espcammonitoring

import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class GalleryActivity : AppCompatActivity() {
    var recyclerView: RecyclerView? = null
    var arrayList = ArrayList<Image>()
    private val activityResultLauncher = registerForActivityResult<String, Boolean>(
        ActivityResultContracts.RequestPermission()
    ) { result: Boolean ->
        if (result) {
            images
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        recyclerView = findViewById(R.id.image_recycler)
        recyclerView?.setLayoutManager(LinearLayoutManager(this@GalleryActivity))
        recyclerView?.setHasFixedSize(true)
        if (ActivityCompat.checkSelfPermission(
                this@GalleryActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            activityResultLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        } else if (ActivityCompat.checkSelfPermission(
                this@GalleryActivity,
                READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            activityResultLauncher.launch(READ_EXTERNAL_STORAGE)
        } else {
            images
        }
    }

    override fun onResume() {
        super.onResume()
        images
    }
    private val images: Unit
        get() {
            arrayList.clear()
            val file = File(Environment.getExternalStorageDirectory(),"espCam")
            val files = file.listFiles()
            if (files != null) {
                for (file1 in files) {
                    if (file1.path.endsWith(".png") || file1.path.endsWith(".jpg")) {
                        arrayList.add(Image(file1.name, file1.path, file1.length()))
                    }
                }
            }
            val adapter = ImageAdapter(this@GalleryActivity, arrayList)
            recyclerView!!.adapter = adapter
            adapter.setOnItemClickListener { view, path ->
                startActivity(
                    Intent(
                        this@GalleryActivity, ImageViewerActivity::class.java
                    ).putExtra("image", path)
                )
            }
        }
}