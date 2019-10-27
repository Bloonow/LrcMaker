package com.excelbloonow.android.lrcmaker.MakeWidget;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.excelbloonow.android.lrcmaker.AudioWidget.AudioControlFragment;
import com.excelbloonow.android.lrcmaker.FileWidget.FileUtil;
import com.excelbloonow.android.lrcmaker.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MakeFragment extends Fragment {

    private TextView mPrevText;
    private TextView mCurText;
    private TextView mNextText;

    private AudioControlFragment mAudioControlFragment;

    private Button mPrevButton;
    private Button mNextButton;
    private Button mFinishButton;
    private Button mTempSaveButton;

    private String mMusicFilepath;
    private String mLyricFilepath;

    private String[] mLyric;
    private int mCurrentLine = -1;

    private List<String> mResultLyric;

    public static MakeFragment newInstance(String musicFilename, String lyricFilename) {
        Bundle args = new Bundle();
        args.putString("key_send_music_filename", musicFilename);
        args.putString("key_send_lyric_filename", lyricFilename);
        MakeFragment fragment = new MakeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static MakeFragment newInstance() {
        return new MakeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get the music and lyric filename
        mMusicFilepath = getArguments().getString("key_send_music_filename");
        mLyricFilepath = getArguments().getString("key_send_lyric_filename");

        // get lyric string and convert it to string[]
        String lyrics = FileUtil.fileInputWithTrimmed(mLyricFilepath);
        if (lyrics != null) {
            mLyric = lyrics.split("\n");
        }

        mResultLyric = new ArrayList<>();

        checkIsTempSaved();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.make_fragment, container, false);

        // init the text view of three display lines of lyric, previous, current, next
        mPrevText = view.findViewById(R.id.id_prev_text);
        mCurText = view.findViewById(R.id.id_cur_text);
        mNextText = view.findViewById(R.id.id_next_text);
        if (mLyric != null) {
            mNextText.setText(mLyric[mCurrentLine + 1]);
        } else {
            mNextText.setText(R.string.text_nothing);
        }

        // instance the AudioControlFragment
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.id_audio_frame_layout);
        if (fragment == null) {
            fragment = new AudioControlFragment(mMusicFilepath);
            getChildFragmentManager().beginTransaction()
                    .add(R.id.id_audio_frame_layout, fragment)
                    .commit();
        }
        if (fragment instanceof AudioControlFragment) {
            mAudioControlFragment = (AudioControlFragment)fragment;
        }

        // init the buttons of control
        mPrevButton = view.findViewById(R.id.id_previous_line_button);
        mPrevButton.setOnClickListener(prevButton -> {
            toPrevLine();
            if (mResultLyric.size() > 0) {
                mResultLyric.remove(mResultLyric.size() - 1);
            }
        });
        mNextButton = view.findViewById(R.id.id_next_line_button);
        mNextButton.setOnClickListener(nextButton -> {
            toNextLine();
            String curResultLine = mAudioControlFragment.getTime();
            if (0 <= mCurrentLine && mCurrentLine < mLyric.length) {
                curResultLine += mLyric[mCurrentLine];
                mResultLyric.add(curResultLine);
            }
        });

        // finish button to save the completed lyric to a file
        mFinishButton = view.findViewById(R.id.id_finish_button);
        mFinishButton.setOnClickListener(finishView -> finishSave());

        mTempSaveButton = view.findViewById(R.id.id_temp_save_button);
        mTempSaveButton.setOnClickListener(saveView -> tempSave());

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        tempSave();
        mAudioControlFragment.pauseMedia();
    }

    // the finish lyric file will be save at the same directory to mLyricFilepath
    // and the lyric filename is ends with .lrc
    private void finishSave() {
        String saveFilename = mLyricFilepath.substring(0, mLyricFilepath.length() - 4) + ".lrc";
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : mResultLyric) {
            stringBuilder.append(str).append("\n");
        }
        String resultContent = stringBuilder.toString();
        if (FileUtil.fileOutput(saveFilename, resultContent)) {
            Toast.makeText(getActivity(),"Finish Save Ok",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Finish Save Error", Toast.LENGTH_SHORT).show();
        }
    }

    // call when the temp save button is clicked or onPause()
    private void tempSave() {
        // get the application cache directory, and create or get a cache file under it
        File cacheDir = getActivity().getApplicationContext().getCacheDir();
        // the cache file's name is same to lyric name, but location path is not
        String lyricFilename = mLyricFilepath.substring(mLyricFilepath.lastIndexOf(File.separator));
        String cacheFilename = cacheDir.getAbsolutePath()
                + File.separator
                + lyricFilename;
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : mResultLyric) {
            stringBuilder.append(str).append("\n");
        }
        String tempSave = stringBuilder.toString();
        if (FileUtil.fileOutput(cacheFilename, tempSave)) {
            Toast.makeText(getActivity(),"Temp Save Ok",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Temp Save Error", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkIsTempSaved() {
        File cacheDir = getActivity().getApplicationContext().getCacheDir();
        String[] cacheFilenames = cacheDir.list();
        String lyricFilename = mLyricFilepath.substring(mLyricFilepath.lastIndexOf(File.separator) + 1);
        for (String filename : cacheFilenames) {
            // there is a cache file's name is same to current lyric filename
            // which show that the lyric has been saved temporarily
            // and then, ask user if reload the cache file
            if (filename.equals(lyricFilename)) {
                new AlertDialog.Builder(getActivity())
                        .setMessage("检测到该歌词的暂存文件，是否载入？")
                        .setPositiveButton("是", (dialog, which) -> {
                            String tempSaveFilename = cacheDir + File.separator + filename;
                            String tempSaveLyricString = FileUtil.fileInputWithTrimmed(tempSaveFilename);
                            if (tempSaveLyricString != null && tempSaveLyricString.length() > 0) {
                                String[] tempSaveLyrics = tempSaveLyricString.split("\n");
                                mCurrentLine = tempSaveLyrics.length - 1;
                                updateTextView();
                                mResultLyric = new ArrayList<>(Arrays.asList(tempSaveLyrics));
                                String time = tempSaveLyrics[mCurrentLine].substring(0, 10);
                                mAudioControlFragment.seekToTime(time);
                            }
                        })
                        .setNegativeButton("否", null)
                        .create()
                        .show();

            }
        }
    }

    // the help function to next line
    private void toNextLine() {
        if (mLyric != null) {
            if (mCurrentLine < mLyric.length) {
                ++mCurrentLine;
                updateTextView();
            }
        }
    }

    // the help function to previous line
    private void toPrevLine() {
        if (mLyric != null) {
            if (mCurrentLine > -1) {
                --mCurrentLine;
                updateTextView();
            }
        }
    }

    // the help function in toNextLine() and toPrevLine
    private void updateTextView() {
        int pre = mCurrentLine - 1;
        int next = mCurrentLine + 1;
        int lyricLength = mLyric.length;
        if (pre >= 0) {
            mPrevText.setText(mLyric[pre]);
        } else {
            mPrevText.setText("");
        }
        if (0 <= mCurrentLine && mCurrentLine <= lyricLength - 1) {
            mCurText.setText(mLyric[mCurrentLine]);
        } else {
            mCurText.setText("");
        }
        if (next <= lyricLength - 1) {
            mNextText.setText(mLyric[next]);
        } else {
            mNextText.setText("");
        }
    }

}
