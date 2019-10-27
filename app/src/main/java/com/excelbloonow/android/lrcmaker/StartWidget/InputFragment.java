package com.excelbloonow.android.lrcmaker.StartWidget;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.excelbloonow.android.lrcmaker.FileWidget.FileUtil;
import com.excelbloonow.android.lrcmaker.PublicWidget.FragmentBackHandler;
import com.excelbloonow.android.lrcmaker.R;

import java.io.File;
import java.util.List;

public class InputFragment extends Fragment implements FragmentBackHandler {

    public static final String RESULT_IS_INPUT = "input_fragment_is_input";

    private EditText mInputEditText;
    private Button mOKButton;

    public static InputFragment newInstance() {
        return new InputFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.start_fragment_input, container, false);

        mInputEditText = view.findViewById(R.id.id_edit_text_input_manual);

        mOKButton = view.findViewById(R.id.id_input_ok_button);
        mOKButton.setOnClickListener(okView -> {
            String cacheFilepath = getActivity().getApplicationContext().getCacheDir().getAbsolutePath()
                    + File.separator
                    + "excel_bloonow_input_cache.cache";
            String inputContent = mInputEditText.getText().toString();
            if (inputContent.length() == 0) {
                Toast.makeText(getActivity(), "录入内容为空", Toast.LENGTH_SHORT).show();
            } else if (FileUtil.fileOutput(cacheFilepath, inputContent)) {
                Toast.makeText(getActivity(), "录入完成", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra(RESULT_IS_INPUT, true);
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
            } else {
                Toast.makeText(getActivity(), "录入失败", Toast.LENGTH_SHORT).show();
            }
            onBackKeyDownHandled();
        });

        return view;
    }

    @Override
    public boolean onBackKeyDownHandled() {
        FragmentManager supportFragmentManger = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManger.beginTransaction();
        List<Fragment> fragments = supportFragmentManger.getFragments();
        boolean startFragmentShowed = false;
        for (Fragment fragment : fragments) {
            if (fragment instanceof StartFragment) {
                fragmentTransaction.show(fragment);
                startFragmentShowed = true;
            } else {
                fragmentTransaction.hide(fragment);
            }
        }
        if (!startFragmentShowed) {
            fragmentTransaction.add(R.id.id_single_fragment, StartFragment.newInstance());
        }
        fragmentTransaction.commit();

        return true;
    }
}
