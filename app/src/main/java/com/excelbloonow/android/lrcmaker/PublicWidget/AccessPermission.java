package com.excelbloonow.android.lrcmaker.PublicWidget;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class AccessPermission {

    public static final int REQUEST_STORAGE_CODE = 0;

    public static void requestStoragePermission(Context applicationContext, Activity activity) {
        int hasWriteStoragePermission = ContextCompat.checkSelfPermission(applicationContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_CODE);
        }
    }

}
