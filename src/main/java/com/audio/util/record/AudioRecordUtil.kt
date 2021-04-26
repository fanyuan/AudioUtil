package com.audio.util.record

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Handler
import android.os.HandlerThread
import android.text.TextUtils
import android.util.Log
import androidx.core.content.ContextCompat
import com.audio.util.ConvertUtil
import kotlinx.android.synthetic.main.activity_record.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


/**
 * 音频录制回调接口
 */
interface RecordCallback{
    fun onRecordError(errorMsg:String)
    fun onRecordStatusChanged(isInRecording:Boolean)
    //fun onRecordFinish(path:String)
}
public interface FinishCallback{
    fun onFinish()
}
class AudioRecordUtil {

    companion object{

        private val TAG = "AudioRecordUtil"
        /**
         * 采样率，现在能够保证在所有设备上使用的采样率是44100Hz, 但是其他的采样率（22050, 16000, 11025）在一些设备上也可以使用。
         */
        private val SAMPLE_RATE_INHZ = 44100

        /**
         * 声道数。CHANNEL_IN_MONO and CHANNEL_IN_STEREO.
         */
        private val CHANNEL_CONFIG: Int = AudioFormat.CHANNEL_IN_STEREO

        /**
         * 返回的音频数据的格式。 ENCODING_PCM_8BIT, ENCODING_PCM_16BIT, and ENCODING_PCM_FLOAT.
         */
        private val AUDIO_FORMAT: Int = AudioFormat.ENCODING_PCM_16BIT


        /**
         * 音频录制类
         */
        private var audioRecord: AudioRecord? = null;

        /**
         * 工作线程
         */
        private lateinit var handlerThread: HandlerThread

        /**
         * 工作调度类
         */
        private lateinit var workHandler: Handler

        /**
         * 是否正在录制中
         */
        private  var isRecording = false

        /**
         * 当前正在录制的输出音频文件路径
         */
        private  var currentOutAudioPath:String? = null




        /**
         *初始化录制控件
         * sampleRateInHz 采样率
         * channelConfig  声道数
         * audioFormat  采样位数
         */
        private fun init(sampleRateInHz:Int, channelConfig:Int, audioFormat:Int) {
            val minBufferSize =
                    AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat)
            //MediaRecorder.AudioSource.MIC
            audioRecord = AudioRecord(
                    MediaRecorder.AudioSource.VOICE_COMMUNICATION, sampleRateInHz,
                    channelConfig, audioFormat, minBufferSize
            )

            handlerThread = HandlerThread("Record");
            handlerThread.start();
            workHandler = Handler(handlerThread.getLooper());
        }

        /**
         * 重新开始音频录制
         */
        @JvmStatic
        fun reStartRecord(context: Context, wavOutFilePath: String, callback: RecordCallback) {
            giveUp()
            startRecord(context,wavOutFilePath,callback)
        }

        /**
         * 开始录音
         * wavOutFilePath wav录音文件输出路径
         */
        @JvmStatic
        public fun startRecord(context: Context, wavOutFilePath:String){
            startRecord(context,wavOutFilePath,null)
        }
        /**
         * 开始录音
         * wavOutFilePath wav录音文件输出路径
         */
        @JvmStatic
        public fun startRecord(context: Context, wavOutFilePath:String, callback: RecordCallback?){
            startRecord(context,SAMPLE_RATE_INHZ, CHANNEL_CONFIG, AUDIO_FORMAT,wavOutFilePath,callback)
        }
        /**
         * 开始录音
         * sampleRateInHz 采样率
         * channelConfig  声道数
         * audioFormat  采样位数
         * wavOutFilePath wav录音文件输出路径
         */
        @JvmStatic
        public fun startRecord(context: Context, sampleRateInHz:Int, channelConfig:Int, audioFormat:Int, wavOutFilePath:String) {
            startRecord(context,sampleRateInHz, channelConfig, audioFormat,wavOutFilePath,null)
        }
        /**
         * 开始录音
         * sampleRateInHz 采样率
         * channelConfig  声道数
         * audioFormat  采样位数
         * callback  录制回调
         * wavOutFilePath wav录音文件输出路径
         */
        @JvmStatic
        public fun startRecord(context: Context, sampleRateInHz:Int, channelConfig:Int, audioFormat:Int, wavOutFilePath:String, callback: RecordCallback?) {
            //情况判断  权限  录制状态

            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
                callback?.onRecordError( "${Manifest.permission.RECORD_AUDIO} 权限未授予")
                return
            }
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                callback?.onRecordError( "${Manifest.permission.WRITE_EXTERNAL_STORAGE} 权限未授权")
                return
            }
            if(isRecording){
                callback?.onRecordError("已有任务正在执行，同一时间只能执行一个录制任务，不能重复执行录制")
                return
            }
            if(!TextUtils.isEmpty(currentOutAudioPath) && !TextUtils.equals(wavOutFilePath, currentOutAudioPath)){
                callback?.onRecordError("$currentOutAudioPath 任务正在执行，同一时间只能有一个录制任务")
                return
            }

            init(sampleRateInHz,channelConfig,audioFormat)

            // 获取最小录音缓存大小，
            val minBufferSize =
                    AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat)

            // 初始化缓存
            val data = ByteArray(minBufferSize)

            val audioPcmPath = getPcmPath(wavOutFilePath)
            val file = File(audioPcmPath)
            Log.i(TAG, "path:" + file.getAbsolutePath())
            //tv.append("\n"+"文件位置：" + file.getAbsolutePath());//tvRecordFilePosition.setText("文件位置：" + file.getAbsolutePath())
            if(!file.exists()){
                file.parentFile.mkdirs()

                if (!file.createNewFile()) {
                    val msg = "文件未能创建成功：" + file.getAbsolutePath()

                    callback?.onRecordError(msg)

                    Log.d(TAG, msg)
                    return
                }
            }

            // 开始录音
            audioRecord?.startRecording()

            isRecording = true
            currentOutAudioPath = wavOutFilePath

            callback?.onRecordStatusChanged(isRecording)

            // 创建数据流，将缓存导入数据流
            workHandler.post(Runnable {
                var fos: FileOutputStream? = null
                try {
                    fos = FileOutputStream(file,true)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()

                    if(callback != null){
                        e.message?.let { callback.onRecordError(it) }
                    }

                    val msg = "文件未找到"
                    Log.e(TAG,msg)
                    //tv.append("\n" + msg)
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
                }finally {
                    callback?.onRecordStatusChanged(isRecording)
                }
            })
        }
        /**
         * 暂停录音
         */
        @JvmStatic
        public fun pause() {
            isRecording = false
            audioRecord?.let {
                it.stop()
                it.release()
                audioRecord = null
            }
        }

        /**
         * 完成录制；
         * 建议在字线程调用处理
         */
        @JvmStatic
        fun finishRecord(callback: FinishCallback?){
            if(isRecording){
                pause();
            }
            Log.d("ddebug","finishRecord currentOutAudioPath = $currentOutAudioPath")
            val audioPcmPath = currentOutAudioPath?.let { getPcmPath(it) }
            ConvertUtil.convertPcm2WavBitNum16(audioPcmPath, currentOutAudioPath)
            audioPcmPath?.let { File(audioPcmPath).delete() }
            currentOutAudioPath = null
            callback?.let {
                it.onFinish()
            }
        }
        /**
         * 根据wav文件输出路径得到出pcm文件临时目录
         */
        private fun getPcmPath(wavPath:String):String{
            val buffer:StringBuffer = StringBuffer(wavPath)
            buffer.append(".tmp.pcm")
            return buffer.toString()
        }

        /**
         * 丢弃已录制的音频
         */
        @JvmStatic
        fun giveUp() {
            if(isRecording){
                pause()
            }
            Log.d("ddebug","放弃录制项：currentOutAudioPath   =  $currentOutAudioPath")
            currentOutAudioPath?.let {
                val audioPcmPath = getPcmPath(it)
                var file1 = File(audioPcmPath)
                var file2 = File(currentOutAudioPath)
                Log.d("ddebug", "放弃录制项：   exists1 = ${file1.exists()}  exists2 = ${file2.exists()}")
                var delete1 = file1.delete()
                var delete2 = file2.delete()
                Log.d("ddebug","放弃录制项：   $delete1   $delete2")
                Log.d("ddebug","放弃录制项：$audioPcmPath     $currentOutAudioPath")
            }
            currentOutAudioPath = null
        }


    }
