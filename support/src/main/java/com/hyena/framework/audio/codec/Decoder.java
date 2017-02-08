package com.hyena.framework.audio.codec;

/**
 * 解码器接口
 * @author yangzc
 *
 */
public interface Decoder
{
    /**
     * 解码并读取16-bit PCM采样数据，返回读取到的实际样本数量
     * 
     * @param samples The number of read samples.
     */
    int readSamples(float[] samples);
    
    /** 
     * 解码并读取16-bit PCM采样数据，返回读取到的实际样本数量
     * 
     * @param samples The number of read samples.
     */
    int readSamples(short[] samples);
    
    /**
     * 销毁解码器并释放本地相关资源
     */
    void release(); 
    
    /**
     * 返回解码器是否释放
     * @return
     */
    boolean isReleased();
    
    /**
     * 读取媒体文件并申请相关解码资源
     * @param file 待解码的媒体文件路径
     * @return 返回操作结果，0表示成功，负数表示失败
     */
    int load(String file);
    
    /**
     * 定位到特定时间段，开始解码
     * @param msec 指定的时间，单位毫秒
     */
    void seekTo(int msec);
    
    /**
     * 获取解码器通道数量，不一定与原媒体包含的通道相同
     * @return 返回通道数量
     */
    int getChannelNum();
    
    /**
     * 获取媒体资源的码率，单位Kbps,如128Kbps
     * @return 返回资源的码率
     */
    int getBitrate();
    
    /**
     * 获取媒体资源的采样率，单位HZ, 如44100HZ
     * @return 返回资源的采样率
     */
    int getSamplerate();
    
    /**
     * 获取媒体资源的播放时长，单位秒
     * @return 返回资源的播放时长
     */
    int getDuration();
    
    /**
     * 获取媒体资源当前的解码索引位置，单位毫秒
     * @return 返回资源当前的解码索引位置
     */
    int getCurrentPosition();
    
    /**
     * 获取媒体资源每帧的采样数
     * @return 返回资源每帧的采样数
     */
    int getSamplePerFrame();
    
    /**
     * 判断是否解码结束
     * @return
     */
    boolean isFinished() ;
    
    /**
     * 获取该解码器所支持的媒体格式，通常指文件后缀，如"mp3"等
     * @return 返回解码器所支持的媒体格式
     */
    String[] getFormats();
}
