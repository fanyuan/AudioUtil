package com.audio.util;

import android.content.Context;

import com.audio.util.cut.AudioCutUtil;
import com.audio.util.record.AudioRecordUtil;
import com.audio.util.record.FinishCallback;
import com.audio.util.record.RecordCallback;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class AudioUtilHelper {
    public static final String SUFFIX_WAV = ".wav";
    public static final String TEMP = "_temp";
    /**
     * 测试可用性方法
     * @return
     */
    public static String helloStr(){
        return "AudioUtilHelper 一个简单的测试字符串";
    }
    /**
     * 获取录制音频下载本地临时存放文件路径
     */
    public static String getRecordLocalTempFilePath(Context context,String name){
        return getRecordDirectoryPath(context) + File.separator + name + TEMP + SUFFIX_WAV;
    }
    /**
     * 获取录制音频下载本地存放文件路径
     */
    public static String getRecordLocalFilePath(Context context,String name){
        return getRecordDirectoryPath(context) + File.separator + name + SUFFIX_WAV;
    }
    /**
     * 获取录制音频存放目录
     * @param context
     * @return
     */
    public static String getRecordDirectoryPath(Context context){
        return context.getExternalCacheDir().getAbsolutePath()+File.separator+"record";
    }
    /**
     * 开始录音
     * wavOutFilePath wav录音文件输出路径
     */
    public static void startRecord(Context context, String wavOutFilePath){
        AudioRecordUtil.startRecord(context,wavOutFilePath);
    }
    /**
     * 开始音频录制
     * @param context
     * @param wavOutFilePath  wavt音频文件输入路径
     * @param callback
     */
    public static void startRecord(Context context, String wavOutFilePath, RecordCallback callback){
        AudioRecordUtil.startRecord(context,wavOutFilePath,callback);
    }
    /**
     * 重新开始音频录制
     * @param context
     * @param wavOutFilePath  wavt音频文件输入路径
     * @param callback
     */
    public static void reStartRecord(Context context, String wavOutFilePath, RecordCallback callback){
        AudioRecordUtil.reStartRecord(context,wavOutFilePath,callback);
    }
    /**
     * 开始录音
     * sampleRateInHz 采样率
     * channelConfig  声道数
     * audioFormat  采样位数
     * wavOutFilePath wav录音文件输出路径
     */
    public static void startRecord(Context context, int sampleRateInHz, int channelConfig, int audioFormat,String wavOutFilePath) {
        AudioRecordUtil.startRecord(context,sampleRateInHz, channelConfig, audioFormat,wavOutFilePath,null);
    }
    /**
     * 开始录音
     * sampleRateInHz 采样率
     * channelConfig  声道数
     * audioFormat  采样位数
     * callback  录制回调
     * wavOutFilePath wav录音文件输出路径
     */
    public static void startRecord(Context context,int sampleRateInHz,int channelConfig,int audioFormat,String wavOutFilePath,RecordCallback callback) {
        AudioRecordUtil.startRecord(context,sampleRateInHz, channelConfig, audioFormat,wavOutFilePath,callback);
    }

    /**
     * 暂停录音
     */
    public static void pause(){
        AudioRecordUtil.pause();
    }

    /**
     * 完成录制；
     * 建议在子线程调用处理
     */
    public static void finishRecord(FinishCallback callback){
        AudioRecordUtil.finishRecord(callback);
    }
    /**
     * 完成录制；
     * 建议在子线程调用处理
     */
    public static void finishRecord(){
        AudioRecordUtil.finishRecord(null);
    }
    /**
     * 丢弃当前录制的音频
     */
    public static void giveUpRecord(){
        AudioRecordUtil.giveUp();
    }
    /**
     * 获取媒体音频文件时长
     * @param url  音频文件存放url可以是本地文件也可以是网络文件
     * @return
     */
    public static long getAudioDuration(String url){
        return DecodeUtil.getAudioDuration(url);
    }
    /**
     * 把mp3文件转换为wav文件
     * @param inMp3Path
     * @param outWavPath
     * @param callback
     */
    public static void mp3ToWav(String inMp3Path, String outWavPath, DecodeUtil.DecodeOperateInterface callback){

        String pcmPath = inMp3Path + ".temp.pcm";
        DecodeUtil.decodeAudio(inMp3Path, pcmPath, 0, -10, callback);

        ConvertUtil.convertPcm2WavBitNum16(pcmPath, outWavPath);
        new File(pcmPath).delete();
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
        MixAudioUtil.mixAudio(path1,path2,outPath,progress1,progress2);
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
    public static void mixAudio(String path1, String path2, String outPath, float progress1, float progress2, MixAudioUtil.MixAudioCallback callback){
        MixAudioUtil.mixAudio(path1,path2,outPath,progress1,progress2,callback);
    }
    /**
     * 拼接wav音频的文件
     * @param inputs
     * @param output
     * @throws IOException
     */
    public static void mergeWav(List<File> inputs, File output) throws IOException {
        WavMergeUtil.mergeWav(inputs,output);
    }
    /**
     * 裁剪音频功能
     * @param srcPath 源音频路径
     * @param destPath 目标音频路径
     * @param startTime 裁剪开始时间
     * @param endTime 裁剪结束时间
     */
    public static void cutAudio(String srcPath,String destPath, float startTime, float endTime){
        AudioCutUtil.cutAudio(srcPath,destPath,startTime,endTime);
    }

    /**
     *  裁剪音频功能
     * @param srcPath 源音频路径
     *@param destPath 目标音频路径
     *@param startTime 裁剪开始时间
     * @param endTime 裁剪结束时间
     * @param callback 音频裁剪回调
     */
    public static void cutAudio(String srcPath, String destPath, float startTime, float endTime, AudioCutUtil.AudioCutCallback callback){
        AudioCutUtil.cutAudio(srcPath,destPath,startTime,endTime,callback);
    }
}
