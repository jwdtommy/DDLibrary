package com.hyena.framework.samples.music;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.SeekBar;

import com.hyena.framework.annotation.AttachViewId;
import com.hyena.framework.annotation.SystemService;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.app.fragment.BaseUIFragmentHelper;
import com.hyena.framework.audio.bean.Song;
import com.hyena.framework.samples.R;
import com.hyena.framework.servcie.audio.PlayerBusService;
import com.hyena.framework.servcie.audio.listener.ProgressChangeListener;
import com.hyena.framework.utils.UiThreadHandler;

/**
 * Created by yangzc on 16/9/1.
 */
public class MusicPlayFragment extends BaseUIFragment<BaseUIFragmentHelper> {

    @AttachViewId(R.id.btn_music_player_start)
    private View mBtnStart;
    @AttachViewId(R.id.btn_music_player_pause)
    private View mBtnPause;
    @AttachViewId(R.id.btn_music_player_resume)
    private View mBtnResume;

    @AttachViewId(R.id.seek_music_player_progress)
    private SeekBar mSeekBar;

    @SystemService(PlayerBusService.BUS_SERVICE_NAME)
    private PlayerBusService mPlayBusService;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_music_player, null);
    }

    private boolean mIsSeeking = false;

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        mBtnStart.setOnClickListener(mClickListener);
        mBtnPause.setOnClickListener(mClickListener);
        mBtnResume.setOnClickListener(mClickListener);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mIsSeeking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mIsSeeking = false;
                try {
                    mPlayBusService.seekTo(seekBar.getProgress());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mPlayBusService.getPlayerBusServiceObserver().addProgressChangeListener(new ProgressChangeListener() {

            @Override
            public void onPlayProgressChange(final long progress, final long duration) {
                UiThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mIsSeeking)
                            return;

                        mSeekBar.setMax((int) duration);
                        mSeekBar.setProgress((int) progress);
                    }
                });
            }

            @Override
            public void onDownloadProgressChange(int percent, long duration) {

            }
        });
        startRefresh();
    }

    private View.OnClickListener mClickListener = new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_music_player_start:
                {
                    String url = "http://7xohdn.com2.z0.glb.qiniucdn.com/tingli/15594833.mp3";
                    Song song = new Song(true, url, null);
                    try {
                        mPlayBusService.play(song);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case R.id.btn_music_player_pause:
                {
                    try {
                        mPlayBusService.pause();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case R.id.btn_music_player_resume:
                {
                    try {
                        mPlayBusService.resume();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    };

    private void startRefresh(){
        Message msg = mHandler.obtainMessage(MSG_REFRESH);
        mHandler.sendMessage(msg);
    }

    private void stopRefresh(){
        mHandler.removeMessages(MSG_REFRESH);
    }

    private static final int MSG_REFRESH = 1;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_REFRESH:
                {
                    try {
                        mPlayBusService.getPosition();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Message next = mHandler.obtainMessage(MSG_REFRESH);
                    sendMessageDelayed(next, 1000);
                }
            }
        }
    };
}
