package com.excelbloonow.android.lrcmaker.FileWidget;

import android.os.Environment;

import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class Directory {

    private static final String ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();

    private String mCurrentAbsoluteDirectory;
    private List<String> mSubFilenames;

    public Directory() {
        this(ROOT);
    }

    public Directory(String currentAbsoluteDirectory) {
        mCurrentAbsoluteDirectory = currentAbsoluteDirectory;
        File file = new File(currentAbsoluteDirectory);
        mSubFilenames = new ArrayList<>(Arrays.asList(Objects.requireNonNull(file.list())));
    }

    public String getCurrentAbsoluteDirectory() {
        return mCurrentAbsoluteDirectory;
    }

    public List<String> getSubFilenames() {
        return mSubFilenames;
    }

    /**
     * @return the sorted subFilenames by dictionary located at china
     */
    public List<String> getSortedSubFilenames() {
        List<String> folders = new ArrayList<>();
        List<String> files = new ArrayList<>();
        for (String subFilename : mSubFilenames) {
            String subFilepath = mCurrentAbsoluteDirectory + File.separator + subFilename;
            if (new File(subFilepath).isDirectory()) {
                folders.add(subFilename);
            } else {
                files.add(subFilename);
            }
        }
        Comparator<Object> compare = Collator.getInstance(java.util.Locale.CHINA);
        Collections.sort(folders, compare);
        Collections.sort(files, compare);
        List<String> sortedFilenames = new ArrayList<>(folders);
        sortedFilenames.addAll(files);
        return sortedFilenames;
    }

    /**
     * @param subFilename the subFilename you want to go into
     * @return return false if subFilename is not a directory, else true
     */
    public boolean goSubDirectory(String subFilename) {
        if (!mSubFilenames.contains(subFilename)) {
            return false;
        }
        String absoluteFilename = mCurrentAbsoluteDirectory + File.separator + subFilename;
        File file = new File(absoluteFilename);
        if (!file.isDirectory()) {
            return false;
        }
        mCurrentAbsoluteDirectory = absoluteFilename;
        mSubFilenames = Arrays.asList(Objects.requireNonNull(file.list()));
        return true;
    }

    /**
     * @return return false if parent is not exists, else true
     */
    public boolean goParentDirectory() {
        if (mCurrentAbsoluteDirectory.equals(ROOT)) {
            return false;
        }
        int lastSeparator = mCurrentAbsoluteDirectory.lastIndexOf(File.separator);
        mCurrentAbsoluteDirectory = mCurrentAbsoluteDirectory.substring(0, lastSeparator);
        File file = new File(mCurrentAbsoluteDirectory);
        mSubFilenames = Arrays.asList(Objects.requireNonNull(file.list()));
        return true;
    }

}
