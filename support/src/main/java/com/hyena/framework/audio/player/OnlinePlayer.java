/**
 * Copyright (C) 2015 The AndroidPhoneTeacher Project
 */
package com.hyena.framework.audio.player;

import com.hyena.framework.audio.StatusCode;
import com.hyena.framework.audio.bean.Song;
import com.hyena.framework.audio.codec.Decoder;
import com.hyena.framework.audio.codec.NativeMP3Decoder;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Looper;
import android.os.Message;

public class OnlinePlayer extends BasePlayer {

	private static final int MIN_BUFFER_LEN = 8192;

	//循环消息
	public static final int MSG_LOOPER = 1;
	private AudioTrack mAudioTrack;
	private Decoder mDecoder;
	private short[] buffer;
	private DecodingThread mDecodingThread;

	private final Object mAudioLock = new Object();
	private final Object mDecoderLock = new Object();

	public OnlinePlayer(Looper looper) {
		super(looper);
		mDecoder = new NativeMP3Decoder();
		getLooperHandle().sendEmptyMessage(MSG_LOOPER);
	}
	
	@Override
	public void setDataSource(Song song) {
		reset();
		super.setDataSource(song);
		int loadRet = mDecoder.load(getDataSource().getLocalFile()
				.getAbsolutePath());
		if (loadRet < 0) {
			setState(StatusCode.STATUS_ERROR);
		}else{
			int channelNum = mDecoder.getChannelNum();
			int samplerate = mDecoder.getSamplerate();
			int channelConfig = (channelNum <= 1) ? AudioFormat.CHANNEL_OUT_MONO
					: AudioFormat.CHANNEL_OUT_STEREO;
			int bufferSize = AudioTrack.getMinBufferSize(samplerate,
					channelConfig, AudioFormat.ENCODING_PCM_16BIT) << 1;
			mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, samplerate,
					channelConfig, AudioFormat.ENCODING_PCM_16BIT, bufferSize,
					AudioTrack.MODE_STREAM);
			bufferSize >>= 1;
			bufferSize = (bufferSize + MIN_BUFFER_LEN - 1)
					& (~(MIN_BUFFER_LEN - 1));
			if (buffer == null || buffer.length != bufferSize) {
				buffer = null;
				buffer = new short[bufferSize];
			}
			setState(StatusCode.STATUS_PREPARED);
			mDecodingThread = new DecodingThread();
			mDecodingThread.start();
		}
	}

	@Override
	public void play() {
		try {
			mAudioTrack.play();
			setState(StatusCode.STATUS_PLAYING);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Override
	public void pause() {
		synchronized (mDecoderLock) {
			mAudioTrack.pause();
			setState(StatusCode.STATUS_PAUSE);
		}
	}

	@Override
	public void stop() {
		synchronized (mDecoderLock) {
			mAudioTrack.stop();
			setState(StatusCode.STATUS_STOP);
		}
	}

	@Override
	public void release() {
		synchronized (mDecoderLock) {
			mAudioTrack.release();
			setState(StatusCode.STATUS_RELEASE);
		}
	}

	@Override
	public int getDuration() {
		synchronized (mDecoderLock) {
			return mDecoder.getDuration();
		}
	}

	@Override
	public int getCurrentPosition() {
		synchronized (mDecoderLock) {
			return mDecoder.getCurrentPosition();
		}
	}

	@Override
	public void seekTo(int position) {
		synchronized (mDecoderLock) {
			mDecoder.seekTo(position);
		}
	}

	@Override
	public void reset() {
		super.reset();
		if(mDecoder != null) {
			mDecoder.release();
		}
		
		if(mAudioTrack != null) {
			mAudioTrack.pause();
			mAudioTrack.flush();
			mAudioTrack.stop();
			mAudioTrack.release();
		}
		mAudioTrack = null;
		setState(StatusCode.STATUS_INITED);
	}

	private class DecodingThread extends Thread {

		@Override
		public void run() {
			while (isPaused()) {
				try {
					Thread.sleep(100);
				} catch (Exception e) {
					break;
				}
			}
			
			while (true) {
				int decodeLen = decodingSamples();
				if (decodeLen == -1) {
					// FINISH
					setState(StatusCode.STATUS_COMPLETED);
					break;
				} else if (decodeLen > 0) {
					writePCMAudioData(decodeLen);
				}

			}
		}

		private int decodingSamples() {
			synchronized (mDecoderLock) {
				if (mDecoder.isFinished()) {
					return -1;
				}
				int decodeLen = mDecoder.readSamples(buffer);
				decodeLen = (decodeLen >> 1) << 1;
				if (decodeLen == 0 && mDecoder.isFinished()) {
					return -1;
				}
				return decodeLen;
			}
		}

		private void writePCMAudioData(int decodeLen) {
			int number = 0;
			synchronized (mAudioLock) {
				number = mAudioTrack.write(buffer, 0, decodeLen);
			}
			if (number == AudioTrack.ERROR_INVALID_OPERATION
					|| number == AudioTrack.ERROR_BAD_VALUE) {
				// error
				setState(StatusCode.STATUS_ERROR);
				mAudioTrack.stop();
			}
		}
	}
	
	@Override
	public void handleMessageImpl(Message msg) {
		super.handleMessageImpl(msg);
		if(msg.what == MSG_LOOPER){
			if(getDataSource() != null && (isPlaying() || isPaused())) {
				onPlayPositionChange(getCurrentPosition(), getDuration() * 1000);
			}
			getLooperHandle().sendEmptyMessageDelayed(MSG_LOOPER, 1000);
		}
	}
}
