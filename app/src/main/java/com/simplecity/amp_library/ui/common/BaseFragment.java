package com.simplecity.amp_library.ui.common;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.simplecity.amp_library.ShuttleApplication;
import com.simplecity.amp_library.playback.MediaManager;
import com.simplecity.amp_library.ui.screens.drawer.NavigationEventRelay;
import com.simplecity.amp_library.ui.views.multisheet.MultiSheetEventRelay;
import com.simplecity.amp_library.utils.AnalyticsManager;
import com.squareup.leakcanary.RefWatcher;
import dagger.android.support.AndroidSupportInjection;
import java.lang.reflect.Field;
import javax.inject.Inject;
import test.com.androidnavigation.fragment.BaseController;

public abstract class BaseFragment extends BaseController {

    private static final String TAG = "BaseFragment";

    // Arbitrary value; set it to some reasonable default
    private static final int DEFAULT_CHILD_ANIMATION_DURATION = 250;

    @Inject
    MultiSheetEventRelay multiSheetEventRelay;

    @Inject
    protected MediaManager mediaManager;

    @Inject
    protected NavigationEventRelay navigationEventRelay;

    @Inject
    public AnalyticsManager analyticsManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidSupportInjection.inject(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        analyticsManager.logScreenName(getActivity(), screenName());
    }

    private static Map<String, Integer> fragmentAnimations = new HashMap<>();

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        final Fragment parent = getParentFragment();
        if (!enter && parent != null && parent.isRemoving()) {
            Animation doNothingAnim = new AlphaAnimation(1, 1);
            doNothingAnim.setDuration(DEFAULT_CHILD_ANIMATION_DURATION);
            return doNothingAnim;
        } else {
            if (nextAnim != 0) {
                Animation anim = AnimationUtils.loadAnimation(getActivity(), nextAnim);
                return anim != null ? anim : super.onCreateAnimation(transit, enter, nextAnim);
            }
            return super.onCreateAnimation(transit, enter, nextAnim);
        }
    }
    

    public static void setNextAnimation(String tag, int animationResource) {
        fragmentAnimations.put(tag, animationResource);
    }

    public static int getNextAnimationResource(String tag) {
        return fragmentAnimations.getOrDefault(tag, 0);
    }

    /**
     * Retrieves the animation duration for a specific fragment tag.
     * @param tag The tag of the fragment whose animation duration is needed.
     * @param defValue Default value to return if no animation is set.
     * @return The duration of the animation, or the default value if none is set.
     */
    private static long getNextAnimationDuration(String tag, long defValue) {
        int animResource = getNextAnimationResource(tag);
        if (animResource != 0) {
            try {
                Animation animation = AnimationUtils.loadAnimation(ShuttleApplication.getAppContext(), animResource);
                return animation.getDuration();
            } catch (Resources.NotFoundException e) {
                // Log or handle the error as needed
                return defValue;
            }
        }
        return defValue;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        RefWatcher refWatcher = ((ShuttleApplication) getContext().getApplicationContext()).getRefWatcher();
        refWatcher.watch(this);
    }

    public MediaManager getMediaManager() {
        return mediaManager;
    }

    public NavigationEventRelay getNavigationEventRelay() {
        return navigationEventRelay;
    }

    protected abstract String screenName();
}
