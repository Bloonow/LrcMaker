package com.excelbloonow.android.lrcmaker.FileWidget;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.excelbloonow.android.lrcmaker.PublicWidget.FragmentBackHandleHelper;
import com.excelbloonow.android.lrcmaker.PublicWidget.SingleFragmentActivity;

public class FileBrowserActivity extends SingleFragmentActivity {

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, FileBrowserActivity.class);
    }

    @Override
    protected Fragment createFragment() {
        String requestContent = getIntent().getStringExtra("key_request_filename");
        return FileBrowserFragment.newInstance(requestContent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        if (!FragmentBackHandleHelper.handleBackFromActivity(this)) {
            super.onBackPressed();
        }
    }

}
