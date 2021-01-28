package com.audio.util

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.audio.util.record.AudioRecordUtil
import com.audio.util.record.RecordCallback
import java.io.File

class Record2Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record2)
    }
    fun start(v: View){
        val path = externalCacheDir?.absolutePath.toString() + File.separator + "hello/test3.wav"
        AudioUtilHelper.startRecord(this,path,object :RecordCallback{
            override fun onRecordError(errorMsg: String) {
                Log.d("ddebug","Record2Activity  onRecordError $errorMsg")
            }

            override fun onRecordStatusChanged(isInRecording: Boolean) {
                Log.d("ddebug","Record2Activity  onRecordStatusChanged $isInRecording")
            }

        })
    }
    fun pause(v: View){
        AudioUtilHelper.pause()
    }
    fun finish(v: View){
        AudioUtilHelper.finishRecord()
    }
}