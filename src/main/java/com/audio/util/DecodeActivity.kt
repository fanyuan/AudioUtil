package com.audio.util

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import com.audio.util.DecodeUtil.DecodeOperateInterface
import com.audio.util.cut.AudioCutUtil
import kotlinx.android.synthetic.main.activity_decode.*
import java.io.File

class DecodeActivity : AppCompatActivity() {
    val callback = object : DecodeOperateInterface {
        override fun onLoadedAudioInfo(audio: String?) {
            tv_info.append(audio)
        }

        override fun onLoadedError(audio: String?) {
            tv_info.append(audio)
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_decode)

    }
    fun start(v: View){
//        var path = Environment.getExternalStorageDirectory().absolutePath + File.separator + "temp/提现成功.m4a"
//        var pathTaret = Environment.getExternalStorageDirectory().absolutePath + File.separator + "temp/提现成功${System.currentTimeMillis()}.m4a"
//        DecodeUtil.decodeAudio(path,pathTaret,0,10,callback)

        var path = Environment.getExternalStorageDirectory().absolutePath + File.separator + "temp/姑娘我爱你.mp3"
        var pathTaret = Environment.getExternalStorageDirectory().absolutePath + File.separator + "temp/姑娘我爱你${System.currentTimeMillis()}.pcm"
        DecodeUtil.decodeAudio(path, pathTaret, 0, -10, callback)

//        path = Environment.getExternalStorageDirectory().absolutePath + File.separator + "temp/遇上你是我的缘.mp3"
//        pathTaret = Environment.getExternalStorageDirectory().absolutePath + File.separator + "temp/遇上你是我的缘${System.currentTimeMillis()}.mp3"
//        DecodeUtil.decodeAudio(path,pathTaret,0,-10,callback)
    }
    fun convert(v:View){
//        var path = Environment.getExternalStorageDirectory().absolutePath + File.separator + "temp/遇上你是我的缘1610188365903.pcm"
//        var pathTaret = Environment.getExternalStorageDirectory().absolutePath + File.separator + "temp/遇上你是我的缘convert${System.currentTimeMillis()}.wav"
//        ConvertUtil.convertPcm2Wav2(path,pathTaret)

        var path = Environment.getExternalStorageDirectory().absolutePath + File.separator + "temp/姑娘我爱你1610210733753.pcm"
        var pathTaret = Environment.getExternalStorageDirectory().absolutePath + File.separator + "temp/姑娘我爱你convert${System.currentTimeMillis()}.wav"
        ConvertUtil.convertPcm2Wav2(path, pathTaret)

    }
    fun mixAudio(v:View){


        object : Thread(){
            override fun run() {
                super.run()
                var path1 = Environment.getExternalStorageDirectory().absolutePath + File.separator + "temp/姑娘我爱你convert1610210798289.wav"
                var path2 = Environment.getExternalStorageDirectory().absolutePath + File.separator + "temp/遇上你是我的缘convert1610201261595.wav"
                var pathOut = Environment.getExternalStorageDirectory().absolutePath + File.separator + "temp/遇上你是我的缘_姑娘我爱你_mix.wav"
                //MixAudioUtil.mixAudio(path1, path2, pathOut, 0.3f, 0.5f);
                AudioUtilHelper.mixAudio(path1, path2, pathOut, 0.3f, 0.5f)
                Log.d("ddebug","---run---")
            }
        }.start();

    }

    fun mergeAudio(v:View){
        log("---mergeAudio---")
        var path = Environment.getExternalStorageDirectory().absolutePath + File.separator + "temp/遇上你是我的缘convert1610201261595.wav"
        var path1 = Environment.getExternalStorageDirectory().absolutePath + File.separator + "temp/姑娘我爱你convert1610210798289.wav"
        var pathOut = Environment.getExternalStorageDirectory().absolutePath + File.separator + "temp/遇上你是我的缘merge.wav"
        val fileIn = File(path)
        val fileOut = File(pathOut);
        val list = listOf<File>(fileIn, File(path1))

        Thread(){
            log("---mergeAudio---")
            //WavMergeUtil.mergeWav(list, fileOut);
            AudioUtilHelper.mergeWav(list,fileOut)
        }.start();

    }

    fun cutAudio(v:View){
        Thread(){
            log("---cutAudio---")
            var path = Environment.getExternalStorageDirectory().absolutePath + File.separator + "temp/姑娘我爱你convert1610210798289.wav"
            var path2 = Environment.getExternalStorageDirectory().absolutePath + File.separator + "temp/姑娘我爱你cut1.wav"
            //AudioCutUtil.cutAudio(path,path2,20f,30f)
            AudioUtilHelper.cutAudio(path,path2,20f,30f);
        }.start();
    }
    fun cutAudio2(v:View){
        Thread(){
            log("---cutAudio---")
            var path = Environment.getExternalStorageDirectory().absolutePath + File.separator + "temp/姑娘我爱你convert1610210798289.wav"
            var pathOut = Environment.getExternalStorageDirectory().absolutePath + File.separator + "temp/姑娘我爱你cut2.wav"
//            AudioCutUtil.cutAudio(path,pathOut,60f,75f,object: AudioCutUtil.AudioCutCallback{
//                override fun onFinish(pathFinish: String?, msg: String?) {
//                    Log.d("ddebug","act onFinish $pathFinish   $msg")
//                }
//
//                override fun onCutError(msg: String?) {
//                    Log.d("ddebug","act onCutError   $msg")
//                }
//
//            })
            AudioUtilHelper.cutAudio(path,pathOut,60f,75f,object: AudioCutUtil.AudioCutCallback{
                override fun onFinish(pathFinish: String?, msg: String?) {
                    Log.d("ddebug","act onFinish $pathFinish   $msg")
                }

                override fun onCutError(msg: String?) {
                    Log.d("ddebug","act onCutError   $msg")
                }

            })
        }.start();
    }

    fun log(msg:String){
        Log.d("ddebug",msg)
    }
}