package com.excelbloonow.android.lrcmaker.AudioWidget;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.excelbloonow.android.lrcmaker.R;

import java.io.File;

public class AudioControlFragment extends Fragment {

    private SeekBar mSeekBar;
    private TextView mTimeProportionText;
    private ImageButton mForwardButton;
    private ImageButton mPlayPauseButton;
    private ImageButton mBackwardButton;

    private String mMusicFilename;
    private MediaPlayer mMediaPlayer;
    private Handler mHandler;
    private Runnable mUpdateUI;

    private int mTotalMillisecond;
    private String mTotalTime;

    public AudioControlFragment(String filename) {
        mMusicFilename = filename;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        File file = new File(mMusicFilename);

        if (file.exists() && !file.isDirectory()) {
            Uri uri = Uri.fromFile(file);
            mMediaPlayer = MediaPlayer.create(getActivity(), uri);
            mTotalMillisecond = mMediaPlayer.getDuration();
            int totalSecond = Math.round(mTotalMillisecond / 1000);
            int min = totalSecond / 60;
            int sec = totalSecond - min * 60;
            mTotalTime = min + " : " + sec;
        }

        mHandler = new Handler();
        mUpdateUI = () -> {
            mSeekBar.setProgress(mMediaPlayer.getCurrentPosition());
            mHandler.postDelayed(mUpdateUI, 100);
            mTimeProportionText.setText(timeProportion());
        };

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.audio_fragment_control, container, false);

        mSeekBar = view.findViewById(R.id.id_audio_seek_bar);
        mSeekBar.setMax(mTotalMillisecond);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int position, boolean fromUser) {
                if (fromUser) {
                    mMediaPlayer.seekTo(position);
                    mTimeProportionText.setText(timeProportion());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mTimeProportionText = view.findViewById(R.id.id_time_proportion_text);
        mTimeProportionText.setText("0 : 0 / 0 : 0");

        mBackwardButton = view.findViewById(R.id.id_backward_button);
        mPlayPauseButton = view.findViewById(R.id.id_play_pause_button);
        mForwardButton = view.findViewById(R.id.id_forward_button);

        if (mMediaPlayer != null) {
            mBackwardButton.setOnClickListener(backwardButton -> {
                backward(3);
                mUpdateUI.run();
            });

            mPlayPauseButton.setOnClickListener(playPause -> {
                if (mMediaPlayer.isPlaying()) {
                    mPlayPauseButton.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_play));
                    mMediaPlayer.pause();
                    mHandler.removeCallbacks(mUpdateUI);
                    // has be paused
                } else {
                    mPlayPauseButton.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_pause));
                    mMediaPlayer.start();
                    mHandler.post(mUpdateUI);
                    // has be played
                }
            });

            mForwardButton.setOnClickListener(forwardButton -> {
                forward(3);
                mUpdateUI.run();
            });
        }

        return view;
    }

    public void pauseMedia() {
        mPlayPauseButton.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_play));
        mMediaPlayer.pause();
        mHandler.removeCallbacks(mUpdateUI);
    }

    private void forward(int seconds) {
        int deltaMsec = seconds * 1000;
        int resultMsec = deltaMsec + mMediaPlayer.getCurrentPosition();
        mMediaPlayer.seekTo(resultMsec < mTotalMillisecond ? resultMsec : mTotalMillisecond);
    }

    private void backward(int seconds) {
        int deltaMsec = seconds * 1000;
        int resultMsec = mMediaPlayer.getCurrentPosition() - deltaMsec;
        mMediaPlayer.seekTo(resultMsec > 0 ? resultMsec : 0);
    }

    private String timeProportion() {
        int currentSec = mMediaPlayer.getCurrentPosition() / 1000;
        int min = currentSec / 60;
        int sec = currentSec % 60;
        return min + " : " + sec + " / " + mTotalTime;
    }

    // get the format time, like [01:21.56] [min:sec.msec]
    public String getTime() {
        if (mMediaPlayer != null) {
            int curMsec = mMediaPlayer.getCurrentPosition();
            int min = curMsec / 1000 / 60;
            int sec = (curMsec / 1000) % 60;
            int msec = (curMsec % 1000) / 10;
            return String.format("[%02d:%02d.%02d]", min, sec, msec);
        }
        return null;
    }

    // call mediaPlayer's seekTo from external by format time, like [01:21.56]
    public void seekToTime(String time) {
        int min = Integer.valueOf(time.substring(1, 3));
        int sec = Integer.valueOf(time.substring(4, 6));
        int msec = Integer.valueOf(time.substring(7, 9));
        int currentMsec = (min * 60 + sec) * 1000 + msec * 10;
        mMediaPlayer.seekTo(currentMsec);
        mUpdateUI.run();
    }

}
