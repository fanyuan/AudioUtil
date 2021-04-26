# AudioUtil
AudioUtil  module


模块简介：
================================================

主要实现了音频的录制，获取媒体文件时长（可以是本地文件也可以是远端网络文件），mp3格式转换pcm，pcm转换为wav（至于wav转换成mp3请参考另一模块(https://github.com/fanyuan/MyMp3Convert.git)）；

使用场景:
================================================

1、从录制到编辑合并一条龙：
   用录制功能录好pcm后，采用转码功能把pcm转换成wav可播放格式，可以把wav文件进行剪切、拼接、混音合成等操作后，生成最终的wav文件
2、各个功能拆开单独使用

使用指导：为了实现对于调用者友好使用，本模块使用用AudioUtilHelper进行了各功能模块的封装，AudioUtilHelper作为统一入口；以使使用者不用关心实现细节，迷失在代码丛林；

功能介绍：
================================================
1、录音功能：
           支持断点续录，调用startRecord开始录制，比如录制过程中有事中断去做其他事情，可以选择暂停(pause)或停止(finishRecord)，然后想接着录时就可以接着录，只要目标文件路径一样就好继续录制；
           比如录唱歌曲时，中间有事打断，可以在处理好事情后，在接着录制
           其中暂停(pause)方法不会释放资源，停止(finishRecord)方法会释放资源
           如果想要删除掉已录制的文件可以调用giveUpRecord方法放弃当前的录制，并删除相关文件
           startRecord方法是重录功能，把之前的录制文件删除重新开始录制

2、获取相关音频文件时长：
           如果想知道一个音频文件的播放时长可以通过getAudioDuration方法获取，支持本地文件和网络远程文件；
           有用过exoplayer和mediaplayer都知道这两个播放器对于音频文件的时长是不精确的，有误差，可以用此方法解决

3、把mp3文件解码转换成mav格式：
           mp3ToWav方法可以把mp3文件转换成wav格式；
           使用场景：如果想自己录制一首歌曲后，想加入一个喜欢的背景乐，就可以从其他地方得到伴奏的mp3然后转成袋鼠，用本模块的合并功能进行混音合成（合成方法mixAudio下面会讲）

4、音频混音合并功能：
           用来进行混音合成，调用mixAudio方法来进行；可以把两个wav文件全成为一个，并且通过权重可以调节音量比例，比如想要录制的声音70%，背景音30%
        
5、音频文件拼接：
           用来拼接多个音频文件，调用mergeWav方法来完成多个音频文件的拼接；
           比如想把多首自己喜欢的歌曲拼接到一起，形成一大的个文件进行各个歌曲的连续播放
        
6、音频文件的剪辑：
           用来提取相关音频文件的片段，调用cutAudio方法通过传入开始时间和结束时间来提取时间区间的音频部分，比如想提取某首歌曲的某几句歌

至于把wav文件转换成mp3可以参考另一模块(https://github.com/fanyuan/MyMp3Convert.git)


使用示例：
================================================

录制功能       
================================================

   java
   ================================================    
        String path = AudioUtilHelper.getRecordLocalTempFilePath(this, "一个小测试");
        AudioUtilHelper.startRecord(this, path, object : RecordCallback {
        override fun onRecordError(errorMsg: String) {
        ThreadHelper.runOnUiThreadDelay({
        DialogAlterHelper.showSingleButtonDialog(this@RecordActivity, errorMsg, { toReRecord() })
        }, 300)


        Apputils.log(applicationContext, "RecordActivity  onRecordError $errorMsg")
        }

        override fun onRecordStatusChanged(isInRecording: Boolean) {
        Apputils.log(applicationContext, "RecordActivity  onRecordStatusChanged $isInRecording")
        }

        })

  kotlin
  ================================================
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
        AudioUtilHelper.finishRecord(null)
        }

-----------------------------
       mp3转wav
================================================
        val mp3Path = "/sdcard/test.mp3"
        val wavPath = "/sdcard/target.wav"

        AudioUtilHelper.mp3ToWav(mp3Path, wavPath, object : DecodeUtil.DecodeOperateInterface {
        override fun onLoadedAudioInfo(audio: String?) {
        Apputils.log(applicationContext, "onLoadedAudioInfo -- audio = $audio")
        }

        override fun onLoadedError(audio: String?) {
        Apputils.log(applicationContext, "onLoadedError -- audio = $audio")
        hasError = true
        if (audio != null) {
        errorMs = audio
        }
        }
        })

--------------------------------

  音频合成功能
================================================
        Thread {
        var path1 = Environment.getExternalStorageDirectory().absolutePath + File.separator + "temp/姑娘我爱你convert1610210798289.wav"
        var path2 = Environment.getExternalStorageDirectory().absolutePath + File.separator + "temp/遇上你是我的缘convert1610201261595.wav"
        var pathOut = Environment.getExternalStorageDirectory().absolutePath + File.separator + "temp/遇上你是我的缘_姑娘我爱你_mix.wav"
        //MixAudioUtil.mixAudio(path1, path2, pathOut, 0.3f, 0.5f);
        AudioUtilHelper.mixAudio(path1, path2, pathOut, 0.3f, 0.5f)
        Log.d("ddebug","---run---")
        }.start()

--------------------------------

   音频拼接功能
================================================
        var path = Environment.getExternalStorageDirectory().absolutePath + File.separator + "temp/春晓.wav"//"temp/遇上你是我的缘convert1610201261595.wav"
        var path1 = Environment.getExternalStorageDirectory().absolutePath + File.separator + "temp/姑娘我爱你convert1610210798289.wav"
        var pathOut = Environment.getExternalStorageDirectory().absolutePath + File.separator + "temp/春晓merge.wav"//"temp/遇上你是我的缘merge.wav"
        val fileIn = File(path)
        val fileOut = File(pathOut);
        val list = listOf<File>(fileIn, fileIn)//File(path1)

        Thread(){
            AudioUtilHelper.mergeWav(list,fileOut)
        }.start();

--------------------------------

  音频提取功能
================================================
        Thread(){
        log("---cutAudio---")
        var path = Environment.getExternalStorageDirectory().absolutePath + File.separator + "temp/春晓.wav"//"temp/姑娘我爱你convert1610210798289.wav"
        var path2 = Environment.getExternalStorageDirectory().absolutePath + File.separator + "temp/春晓cut1.wav"//"temp/姑娘我爱你cut1.wav"
        //AudioCutUtil.cutAudio(path,path2,20f,30f)
        AudioUtilHelper.cutAudio(path,path2,5f,15f);
        }.start();



