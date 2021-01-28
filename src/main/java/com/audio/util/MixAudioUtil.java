package com.audio.util;


import android.util.Log;

import com.audio.util.callback.DecodeOperateInterface;
import com.audio.util.util.Constant;
import com.audio.util.util.FileUtils;

import java.io.File;

public class MixAudioUtil {
    /**
     * 音频合成回调接口
     */
    public interface MixAudioCallback{
        public void onFinish(String msg);
        public void onError(String msg);
    }

    /**
     * 音频合成功能
     * @param path1 音频文件输入路径
     * @param path2 音频文件输入路径
     * @param outPath 合成后的结果文件路径
     * @param progress1 音量比值
     * @param progress2 音量比值
     */
    public static void mixAudio(String path1, String path2, String outPath,float progress1, float progress2){
        mixAudio(path1,path2,outPath,progress1,progress2,null);
    }

    /**
     * 音频合成功能
     * @param path1 音频文件输入路径
     * @param path2 音频文件输入路径
     * @param outPath 合成后的结果文件路径
     * @param progress1 音量比值
     * @param progress2 音量比值
     * @param callback   音频合成功能回调
     */
    public static void mixAudio(String path1, String path2, String outPath, float progress1, float progress2, MixAudioCallback callback){
        String fileName1 = new File(path1).getName();
        fileName1 = fileName1.substring(0, fileName1.lastIndexOf('.')) + Constant.SUFFIX_WAV;
        String fileName2 = new File(path2).getName();
        fileName2 = fileName2.substring(0, fileName2.lastIndexOf('.')) + Constant.SUFFIX_WAV;

        String destPath1 = FileUtils.getAudioEditStorageDirectory() + File.separator + fileName1;
        String destPath2 = FileUtils.getAudioEditStorageDirectory() + File.separator + fileName2;

        decodeAudio(path1, destPath1);
        decodeAudio(path2, destPath2);

        if(!FileUtils.checkFileExist(destPath1)){
            String msg = "解码失败" + destPath1;
            //ToastUtil.showToast("解码失败" + destPath1);
            if(callback != null){
                callback.onError(msg);
            }
            Log.d("ddebug",msg);
            return;
        }
        if(!FileUtils.checkFileExist(destPath2)){
            String msg = "解码失败" + destPath2;
            //ToastUtil.showToast("解码失败" + destPath2);
            if(callback != null){
                callback.onError(msg);
            }
            Log.d("ddebug",msg);
            return;
        }

        Audio audio1 = getAudioFromPath(destPath1);
        Audio audio2 = getAudioFromPath(destPath2);
        Audio outAudio = new Audio();

        //String outPath = new File(new File(destPath1).getParentFile(), "out.wav").getAbsolutePath();
        outAudio.setPath(outPath);
        Log.d("ddebug","outPath = " + outPath);

        if(audio1 != null && audio2 != null){
            AudioEditUtil.mixAudioWithSameBaseSrcAudio(audio1, audio2, outAudio, 0, progress1, progress2);
        }

        String msg = "合成完成";
        if(callback != null){
            callback.onFinish(msg);
        }
        //EventBus.getDefault().post(new AudioMsg(AudioTaskCreator.ACTION_AUDIO_MIX, outAudio.getPath(), msg));
        Log.d("ddebug",msg);
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
