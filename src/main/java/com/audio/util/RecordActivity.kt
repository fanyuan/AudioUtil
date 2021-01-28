package com.audio.util

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.*
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.audio.util.record.AudioRecordUtil
import com.audio.util.record.RecordCallback
import kotlinx.android.synthetic.main.activity_record.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class RecordActivity : AppCompatActivity() {
    val MY_PERMISSIONS_REQUEST = 132
    val TAG = "record"
    /**
     * 需要申请的运行时权限
      */
    private val permissions = arrayOf<String>(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    var audioRecord:AudioRecord? = null;
    lateinit var handlerThread:HandlerThread
    lateinit var workHandler:Handler

    lateinit var audioPcmPath:String //= getExternalFilesDir(Environment.DIRECTORY_MUSIC)?.absolutePath.toString() + File.separator + "hello/test.pcm"
    lateinit var audioWavPath:String //= getExternalFilesDir(Environment.DIRECTORY_MUSIC)?.absolutePath.toString() + File.separator + "hello/test.wav"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)
        init()
        checkPermissions()
    }

    private fun init() {
        val minBufferSize =
            AudioRecord.getMinBufferSize(SAMPLE_RATE_INHZ, CHANNEL_CONFIG, AUDIO_FORMAT)
        //MediaRecorder.AudioSource.VOICE_COMMUNICATION
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC, SAMPLE_RATE_INHZ,
            CHANNEL_CONFIG, AUDIO_FORMAT, minBufferSize
        )

        handlerThread = HandlerThread("Record");
        handlerThread.start();
        workHandler = Handler(handlerThread.getLooper());

        audioPcmPath = externalCacheDir?.absolutePath.toString() + File.separator + "hello/test.pcm"//getExternalFilesDir(Environment.DIRECTORY_MUSIC)?.absolutePath.toString() + File.separator + "hello/test.pcm"
        audioWavPath = externalCacheDir?.absolutePath.toString() + File.separator + "hello/test.wav"
    }

    val mPermissionList = arrayListOf<String>()
    /**
     * 检查权限
      */
    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (i in 0 until permissions.size) {
                if (ContextCompat.checkSelfPermission(this, permissions[i]) !=
                    PackageManager.PERMISSION_GRANTED
                ) {

                    mPermissionList.add(permissions[i])
                }
            }
            if (!mPermissionList.isEmpty()) {
                val permissions: Array<String> =
                    mPermissionList.toArray(arrayOfNulls<String>(mPermissionList.size))
                ActivityCompat.requestPermissions(this, permissions, MY_PERMISSIONS_REQUEST)
            }
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode === MY_PERMISSIONS_REQUEST) {
            for (i in 0 until grantResults.size) {
                if (grantResults[i] !== PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, permissions[i] + " 权限被用户禁止！")
                }
            }
        }
    }

    fun start(v: View){
        startRecord()
    }
    fun stop(v:View){
        stopRecord();
    }

    fun stop2(v:View){
        stopRecord2();
    }

    fun start2(v: View){
        val path = externalCacheDir?.absolutePath.toString() + File.separator + "hello/test2.wav"
        AudioRecordUtil.startRecord(this,path,object : RecordCallback {
            override fun onRecordError(errorMsg: String) {
                Log.d("ddebug","onRecordError = " + errorMsg)
            }

            override fun onRecordStatusChanged(isInRecording: Boolean) {
                Log.d("ddebug","onRecordStatusChanged isInRecording = $isInRecording")
            }

        });
    }

    fun pause(v: View){
        AudioRecordUtil.pause()
    }

    fun finishRecord2(v: View){
        AudioRecordUtil.finishRecord()
    }
    /**
     * 采样率，现在能够保证在所有设备上使用的采样率是44100Hz, 但是其他的采样率（22050, 16000, 11025）在一些设备上也可以使用。
      */
    val SAMPLE_RATE_INHZ = 44100

    /**
     * 声道数。CHANNEL_IN_MONO and CHANNEL_IN_STEREO.
     */
    val CHANNEL_CONFIG: Int = AudioFormat.CHANNEL_IN_STEREO

    /**
     * 返回的音频数据的格式。 ENCODING_PCM_8BIT, ENCODING_PCM_16BIT, and ENCODING_PCM_FLOAT.
      */
    val AUDIO_FORMAT: Int = AudioFormat.ENCODING_PCM_16BIT

    var isRecording = false

    /**
     * 开始录音
     */
    private fun startRecord() {
        // 获取最小录音缓存大小，
        val minBufferSize =
            AudioRecord.getMinBufferSize(SAMPLE_RATE_INHZ, CHANNEL_CONFIG, AUDIO_FORMAT)

        if(audioRecord  == null){
            //MediaRecorder.AudioSource.VOICE_COMMUNICATION
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC, SAMPLE_RATE_INHZ,
                CHANNEL_CONFIG, AUDIO_FORMAT, minBufferSize
            )
        }

        // 初始化缓存
        val data = ByteArray(minBufferSize)
        val file = File(audioPcmPath)
        Log.i(TAG, "path:" + file.getAbsolutePath())
        tv.append("\n"+"文件位置：" + file.getAbsolutePath());//tvRecordFilePosition.setText("文件位置：" + file.getAbsolutePath())
        if(!file.exists()){
            file.parentFile.mkdirs()

            if (!file.createNewFile()) {
                val msg = "文件未能创建成功：" + file.getAbsolutePath()
                Log.d(TAG, msg)
                tv.append("\n" + msg)
                return
            }
        }

        // 开始录音
        audioRecord?.startRecording()
        isRecording = true
        // 创建数据流，将缓存导入数据流
        workHandler.post(Runnable {
            var fos: FileOutputStream? = null
            try {
                fos = FileOutputStream(file,true)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()

                val msg = "文件未找到"
                Log.e(TAG,msg)
                tv.append("\n" + msg)
            }
            if (fos == null) return@Runnable
            while (isRecording) {
                val length: Int = audioRecord!!.read(data, 0, minBufferSize)
                if (AudioRecord.ERROR_INVALID_OPERATION != length) {
                    try {
                        fos.write(data, 0, length)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            try {
                // 关闭数据流
                fos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        })
    }

    /**
     * 停止录音
     */
    private fun stopRecord() {
        isRecording = false
        audioRecord?.let {
            it.stop()
            it.release()
            audioRecord = null
            //ConvertUtil.convertPcm2Wav(audioPcmPath, audioWavPath)
            //File(audioPcmPath).delete()
        }
    }

    /**
     * 完成录制
     */
    fun finishRecord(v:View){
        ConvertUtil.convertPcm2Wav(audioPcmPath, audioWavPath)
        File(audioPcmPath).delete()
    }

    /**
     * 停止录音
     */
    private fun stopRecord2() {
        isRecording = false
        audioRecord?.let {
            it.stop()
            it.release()
            audioRecord = null
            ConvertUtil.convertPcm2Wav2(audioPcmPath, audioWavPath)
            File(audioPcmPath).delete()
        }
    }
    fun convert(v:View){
        convert2Wav()
    }
    private fun convert2Wav(){
        //var path = getExternalFilesDir(Environment.DIRECTORY_MUSIC).absolutePath.toString() + File.separator + "hello/test.pcm"
        //var pathTaret = Environment.getExternalStorageDirectory().absolutePath + File.separator + "temp/姑娘我爱你convert${System.currentTimeMillis()}.wav"
        ConvertUtil.convertPcm2Wav(audioPcmPath, audioWavPath)
    }
}