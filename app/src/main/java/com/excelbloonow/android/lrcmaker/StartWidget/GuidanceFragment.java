package com.excelbloonow.android.lrcmaker.StartWidget;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.excelbloonow.android.lrcmaker.PublicWidget.FragmentBackHandler;
import com.excelbloonow.android.lrcmaker.R;

import java.util.List;

public class GuidanceFragment extends Fragment implements FragmentBackHandler {

    public static GuidanceFragment newInstance() {
        return new GuidanceFragment();
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.start_fragment_guidance, container, false);

        TextView guidanceTextView = view.findViewById(R.id.id_guidance_text_view);
        guidanceTextView.setText("Here is a guidance.");

        return view;
    }

    @Override
    public boolean onBackKeyDownHandled() {
        // return with destroy
        FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        List<Fragment> fragments = supportFragmentManager.getFragments();
        boolean startFragmentShowed = false;
        for (Fragment fragment : fragments) {
            if (fragment instanceof StartFragment) {
                fragmentTransaction.show(fragment);
                startFragmentShowed = true;
            } else if (fragment instanceof GuidanceFragment) {
                fragmentTransaction.remove(fragment);
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
