package com.audio.util;

import android.content.Context;

import com.audio.util.cut.AudioCutUtil;
import com.audio.util.record.AudioRecordUtil;
import com.audio.util.record.RecordCallback;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class AudioUtilHelper {
    /**
     * 测试可用性方法
     * @return
     */
    public static String helloStr(){
        return "AudioUtilHelper 一个简单的测试字符串";
    }


    /**
     * 开始录音
     * wavOutFilePath wav录音文件输出路径
     */
//    public static void startRecord(Context context, String wavOutFilePath){
//        AudioRecordUtil.startRecord(context,wavOutFilePath);
//    }
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
     * 开始录音
     * sampleRateInHz 采样率
     * channelConfig  声道数
     * audioFormat  采样位数
     * wavOutFilePath wav录音文件输出路径
     */
//    public static void startRecord(Context context, int sampleRateInHz, int channelConfig, int audioFormat,String wavOutFilePath) {
//        AudioRecordUtil.startRecord(context,sampleRateInHz, channelConfig, audioFormat,wavOutFilePath,null);
//    }
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
    public static void finishRecord(){
        AudioRecordUtil.finishRecord();
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
