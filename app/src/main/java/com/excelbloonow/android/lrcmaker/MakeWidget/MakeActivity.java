package com.excelbloonow.android.lrcmaker.MakeWidget;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.excelbloonow.android.lrcmaker.PublicWidget.SingleFragmentActivity;

public class MakeActivity extends SingleFragmentActivity {

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, MakeActivity.class);
    }

    @Override
    protected Fragment createFragment() {
        String musicFilename = getIntent().getStringExtra("key_send_music_filename");
        String lyricFilename = getIntent().getStringExtra("key_send_lyric_filename");
        return MakeFragment.newInstance(musicFilename, lyricFilename);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
