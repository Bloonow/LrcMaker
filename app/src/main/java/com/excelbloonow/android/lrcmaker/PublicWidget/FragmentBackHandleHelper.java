package com.excelbloonow.android.lrcmaker.PublicWidget;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.List;

public class FragmentBackHandleHelper {
    private static boolean toHandle(FragmentManager fragmentManager) {
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments == null || fragments.size() == 0) {
            return false;
        }
        for (int i = fragments.size() - 1; i >= 0; --i) {
            if (handled(fragments.get(i))) {
                return true;
            }
        }

        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
            return true;
        }

        return false;
    }

    private static boolean handled(Fragment fragment) {
        return fragment != null
                && fragment.isVisible()
                && fragment instanceof FragmentBackHandler
                && ((FragmentBackHandler)fragment).onBackKeyDownHandled();
    }

    public static boolean handleBackFromFragment(Fragment fragment) {
        return toHandle(fragment.getChildFragmentManager());
    }

    public static boolean handleBackFromActivity(AppCompatActivity activity) {
        return toHandle(activity.getSupportFragmentManager());
    }

}