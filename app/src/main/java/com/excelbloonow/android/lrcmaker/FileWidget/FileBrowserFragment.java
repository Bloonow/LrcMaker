package com.excelbloonow.android.lrcmaker.FileWidget;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.excelbloonow.android.lrcmaker.PublicWidget.FragmentBackHandleHelper;
import com.excelbloonow.android.lrcmaker.PublicWidget.FragmentBackHandler;
import com.excelbloonow.android.lrcmaker.R;

import java.io.File;
import java.util.List;

public class FileBrowserFragment extends Fragment implements FragmentBackHandler {

    public static final String RESULT_KEY_FILENAME = "extra_filename";

    private Directory mDirectory;
    private RecyclerView mFileRecycler;
    private LinearLayoutManager mLinearLayoutManager;
    private ConstraintLayout mBottomMenu;
    private Button mDoneButton;

    private String selectedFilename;

    // remember the position of the recycler view list
    private int mLastPosition = 0;
    private int mLastOffset = 0;

    // represent the bottom menu is visible, at first it is invisible
    private boolean mIsSelectedShow = false;

    @Override
    public boolean onBackKeyDownHandled() {
        if (mIsSelectedShow) {
            mIsSelectedShow = false;
            mBottomMenu.setVisibility(View.GONE);
            updateRecycler();
            return true;
        }
        if (mDirectory.goParentDirectory()) {
            updateRecycler();
            return true;
        }
        return FragmentBackHandleHelper.handleBackFromFragment(this);
    }

    // launch the fragment with a request string
    public static FileBrowserFragment newInstance(String request) {
        if (request == null) {
            return new FileBrowserFragment();
        }
        Bundle args = new Bundle();
        args.putString("key_request_filename", request);
        FileBrowserFragment fragment = new FileBrowserFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static FileBrowserFragment newInstance() {
        return new FileBrowserFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDirectory = new Directory();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.public_recycler_view_list, container, false);

        mFileRecycler = view.findViewById(R.id.id_recycler_view_list);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mFileRecycler.setLayoutManager(mLinearLayoutManager);
        mFileRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                View topItemView = mLinearLayoutManager.getChildAt(0);
                mLastOffset = topItemView.getTop();
                mLastPosition = mLinearLayoutManager.getPosition(topItemView);
            }
        });

        updateRecycler();

        // init the bottom menu and its subWidgets
        mBottomMenu = view.findViewById(R.id.id_bottom_menu);

        // enable the getButton if start fragment request a filename
        if ("value_request_filename".equals(getArguments().getString("key_request_filename"))) {
            mDoneButton = mBottomMenu.findViewById(R.id.id_get_filename_button);
            mDoneButton.setOnClickListener(buttonView -> {
                String filepath = mDirectory.getCurrentAbsoluteDirectory()
                        + File.separator
                        + selectedFilename;
                Intent data = new Intent();
                data.putExtra(RESULT_KEY_FILENAME, filepath);
                getActivity().setResult(Activity.RESULT_OK, data);
                getActivity().finish();
            });
        }

        // at first, the bottom menu is invisible
        mBottomMenu.setVisibility(View.GONE);

        return view;
    }

    private void updateRecycler() {
        mFileRecycler.setAdapter(new FileAdapter());
        mLinearLayoutManager.scrollToPositionWithOffset(mLastPosition, mLastOffset);
    }



    private class FileHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private ImageView mFileIcon;
        private TextView mFilenameInfo;
        private CheckBox mCheckBox;

        private String mFilename;

        public FileHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.file_recycler_view_item, parent, false));
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            mFileIcon = itemView.findViewById(R.id.id_item_file_icon);
            mFilenameInfo = itemView.findViewById(R.id.id_item_file_info);
            mCheckBox = itemView.findViewById(R.id.id_item_check);
            mCheckBox.setVisibility(View.GONE);
        }

        public void bind(String filename) {
            mFilename = filename;
            mFilenameInfo.setText(filename);
            String filepath = mDirectory.getCurrentAbsoluteDirectory()
                    + File.separator
                    + filename;
            setIcon(filepath);
        }

        // set the file icon
        private void setIcon(String filepath) {
            File file = new File(filepath);
            if (file.isDirectory()) {
                mFileIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_folder));
            } else {
                // the file is not a directory, and then set the icon by its suffix name
                String filename = mFilename.toLowerCase();
                if (filename.endsWith(".txt")) {
                    mFileIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_suffix_txt));
                } else if (filename.endsWith(".mp3")) {
                    mFileIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_suffix_mp3));
                } else if (filename.endsWith(".flac")) {
                    mFileIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_suffix_flac));
                } else if (filename.endsWith(".wav")) {
                    mFileIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_suffix_wav));
                } else {
                    mFileIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_unknow_file));
                }
            }
        }

        @Override
        public void onClick(View view) {
            if (mIsSelectedShow) {
                mCheckBox.setChecked(!mCheckBox.isChecked());
                if (!mCheckBox.isChecked()) {
                    selectedFilename = null;
                } else {
                    selectedFilename = mFilename;
                }
                 return;
            }
            if (mDirectory.goSubDirectory(mFilename)) {
                updateRecycler();
            }
        }

        @Override
        public boolean onLongClick(View view) {
            mCheckBox.setChecked(true);
            mCheckBox.setVisibility(View.VISIBLE);
            selectedFilename = mFilename;
            mIsSelectedShow = true;
            mBottomMenu.setVisibility(View.VISIBLE);
            return true;
        }
    }

    private class FileAdapter extends RecyclerView.Adapter {

        private List<String> mFilenames;

        public FileAdapter() {
            mFilenames = mDirectory.getSortedSubFilenames();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new FileHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof FileHolder) {
                ((FileHolder)holder).bind(mFilenames.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return mFilenames.size();
        }
    }
}
