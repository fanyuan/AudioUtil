package com.audio.util.cut;

import android.util.Log;

import com.audio.util.AudioEditUtil;
import com.audio.util.DecodeEngine;
import com.audio.util.Audio;
import com.audio.util.callback.DecodeOperateInterface;
import com.audio.util.util.FileUtils;

import java.io.File;

public class AudioCutUtil {
    public interface AudioCutCallback{
        public void onFinish(String pathFinished,String msg);
        public void onCutError(String msg);
    }
    /**
     * 裁剪音频
     * @param srcPath 源音频路径
     * @param destPath 目标音频路径
     * @param startTime 裁剪开始时间
     * @param endTime 裁剪结束时间
     */
    public static void cutAudio(String srcPath,String destPath, float startTime, float endTime){
        cutAudio(srcPath,destPath,startTime,endTime,null);
    }
    /**
     * 裁剪音频
     * @param srcPath 源音频路径
     * @param destPath 目标音频路径
     * @param startTime 裁剪开始时间
     * @param endTime 裁剪结束时间
     */
    public static void cutAudio(String srcPath, String destPath,float startTime, float endTime,AudioCutCallback callback){
        String fileName = new File(srcPath).getName();
        //String nameNoSuffix = fileName.substring(0, fileName.lastIndexOf('.'));
        //fileName = nameNoSuffix + Constant.SUFFIX_WAV;
        //String outName = nameNoSuffix + "_cut.wav";

        //裁剪后音频的路径
        //String destPath = FileUtils.getAudioEditStorageDirectory() + File.separator + outName;
        Log.d("ddebug","cut destPath = " + destPath);

        //解码源音频，得到解码后的文件
        decodeAudio(srcPath, destPath);

        if(!FileUtils.checkFileExist(destPath)){
            if(callback != null){
                callback.onCutError("解码失败" + destPath);
            }
            Log.d("ddebug","解码失败" + destPath);
            //ToastUtil.showToast("解码失败" + destPath);
            return;
        }

        Audio audio = getAudioFromPath(destPath);

        if(audio != null){
            AudioEditUtil.cutAudio(audio, startTime, endTime);
        }

        String msg = "裁剪完成";
        Log.d("ddebug",msg + "cut destPath = " + destPath);
        if(callback != null){
            callback.onFinish(destPath,msg);
        }
        //EventBus.getDefault().post(new AudioMsg(AudioTaskCreator.ACTION_AUDIO_CUT, destPath, msg));
    }
    private static void decodeAudio(String path, String destPath){
        final File file = new File(path);

        if(FileUtils.checkFileExist(destPath)){
            FileUtils.deleteFile(new File(destPath));
        }

        FileUtils.confirmFolderExist(new File(destPath).getParent());

        DecodeEngine.getInstance().convertMusicFileToWaveFile(path, destPath, new DecodeOperateInterface() {
            @Override public void updateDecodeProgress(int decodeProgress) {
                String msg = String.format("解码文件：%s，进度：%d", file.getName(), decodeProgress) + "%";
                //EventBus.getDefault().post(new AudioMsg(AudioTaskCreator.ACTION_AUDIO_MIX, msg));
            }

            @Override public void decodeSuccess() {

            }

            @Override public void decodeFail() {

            }
        });
    }
    /**
     * 获取根据解码后的文件得到audio数据
     * @param path
     * @return
     */
    private static Audio getAudioFromPath(String path){
        if(!FileUtils.checkFileExist(path)){
            return null;
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            try {
                Audio audio = Audio.createAudioFromFile(new File(path));
                return audio;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
