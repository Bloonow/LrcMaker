package com.excelbloonow.android.lrcmaker.StartWidget;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.excelbloonow.android.lrcmaker.FileWidget.FileBrowserActivity;
import com.excelbloonow.android.lrcmaker.FileWidget.FileBrowserFragment;
import com.excelbloonow.android.lrcmaker.MakeWidget.MakeActivity;
import com.excelbloonow.android.lrcmaker.R;

import java.io.File;

public class StartFragment extends Fragment {

    private static final int REQUEST_FILENAME_CODE_MUSIC = 0;
    private static final int REQUEST_FILENAME_CODE_LYRIC = 1;
    private static final int REQUEST_IS_INPUT_MANUAL_CODE = 2;

    private Button mChooseMusic;
    private Button mInputButton;
    private Button mChooseLyric;
    private Button mStart;
    private Button mGuidance;

    private View.OnClickListener mInputButtonClickListenerNoInput = v -> {
        Fragment fragment = InputFragment.newInstance();
        fragment.setTargetFragment(StartFragment.this, REQUEST_IS_INPUT_MANUAL_CODE);
        getActivity().getSupportFragmentManager().beginTransaction()
                .hide(StartFragment.this)
                .add(R.id.id_single_fragment, fragment)
                .commit();
    };


    public static StartFragment newInstance() {
        return new StartFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.start_fragment, container, false);

        mChooseMusic = view.findViewById(R.id.id_choose_music);
        mChooseMusic.setOnClickListener(musicView -> {
            Intent intent = FileBrowserActivity.newIntent(getActivity());
            intent.putExtra("key_request_filename", "value_request_filename");
            startActivityForResult(intent, REQUEST_FILENAME_CODE_MUSIC);
        });

        mInputButton = view.findViewById(R.id.id_input_button);
        mInputButton.setOnClickListener(mInputButtonClickListenerNoInput);

        mChooseLyric = view.findViewById(R.id.id_choose_lyric);
        mChooseLyric.setOnClickListener(lyricView -> {
            Intent intent = FileBrowserActivity.newIntent(getActivity());
            intent.putExtra("key_request_filename", "value_request_filename");
            startActivityForResult(intent, REQUEST_FILENAME_CODE_LYRIC);
        });

        mStart = view.findViewById(R.id.id_start);
        mStart.setOnClickListener(makeView -> {
            String musicFilename = (String) mChooseMusic.getText();
            String lyricFilename;
            if (mChooseLyric.isEnabled()) {
                lyricFilename = (String) mChooseLyric.getText();
            } else {
                lyricFilename = getActivity().getApplicationContext().getCacheDir().getAbsolutePath()
                        + File.separator
                        + "excel_bloonow_input_cache.cache";
            }

            if (musicFilename != null && lyricFilename != null) {
                Intent intent = MakeActivity.newIntent(getActivity());
                intent.putExtra("key_send_music_filename", musicFilename);
                intent.putExtra("key_send_lyric_filename", lyricFilename);
                startActivity(intent);
            }
        });

        mGuidance = view.findViewById(R.id.id_guidance_button);
        mGuidance.setOnClickListener(guideView -> {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .hide(this)
                    .add(R.id.id_single_fragment, new GuidanceFragment())
                    .commit();
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK || data == null) {
            return;
        }
        if (requestCode == REQUEST_FILENAME_CODE_MUSIC) {
            String filename = data.getStringExtra(FileBrowserFragment.RESULT_KEY_FILENAME);
            mChooseMusic.setText(filename);
        } else if (requestCode == REQUEST_FILENAME_CODE_LYRIC) {
            String filename = data.getStringExtra(FileBrowserFragment.RESULT_KEY_FILENAME);
            mChooseLyric.setText(filename);
        } else if (requestCode == REQUEST_IS_INPUT_MANUAL_CODE) {
            boolean isInput = data.getBooleanExtra(InputFragment.RESULT_IS_INPUT, false);
            if (isInput) {
                mChooseLyric.setEnabled(false);
                mInputButton.setText("再次点击取消录入");
                mInputButton.setOnClickListener(view -> {
                    mChooseLyric.setEnabled(true);
                    mInputButton.setText("手动录入歌词");
                    mInputButton.setOnClickListener(mInputButtonClickListenerNoInput);
                });
            }
        }
    }
}
