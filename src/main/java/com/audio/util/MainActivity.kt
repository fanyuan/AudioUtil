package com.audio.util

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.audio.util.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        binding.act = this

        checkPermission()
        val downloadCache = Environment.getDownloadCacheDirectory().path + "===" + Environment.getDownloadCacheDirectory().absolutePath//App.getInstance().getCacheDir().getPath();

        //Log.d("ddebug","downloadCache = ${downloadCache} --- externalCacheDir.path =" + externalCacheDir?.path + "---" + externalCacheDir?.absolutePath )
    }

    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 10001)
        }
    }

    fun decode(){
        startActivity(Intent(this, DecodeActivity::class.java))
    }
    fun record(){
        startActivity(Intent(this, RecordActivity::class.java))
    }
    fun record2(){
        startActivity(Intent(this, Record2Activity::class.java))
    }
}