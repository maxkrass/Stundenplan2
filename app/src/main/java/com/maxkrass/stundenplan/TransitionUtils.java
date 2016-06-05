package com.maxkrass.stundenplan;

import android.content.Context;
import android.transition.ChangeBounds;
import android.transition.ChangeClipBounds;
import android.transition.ChangeTransform;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionSet;

public final class TransitionUtils {

    /**
     * Returns a modified enter transition that excludes the navigation bar and status
     * bar as targets during the animation. This ensures that the navigation bar and
     * status bar won't appear to "blink" as they fade in/out during the transition.
     */
    public static Transition makeEnterTransition() {
        Transition fade = new Fade();
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        return fade;
    }

    /**
     * Returns a transition that will (1) move the shared element to its correct size
     * and location on screen, (2) gradually increase/decrease the shared element's
     * text size, and (3) gradually alters the shared element's text color through out
     * the transition.
     */
    public static Transition makeSharedElementEnterTransition() {
        TransitionSet set = new TransitionSet();
        set.setOrdering(TransitionSet.ORDERING_TOGETHER);

        Transition recolor = new Recolor();
        recolor.addTarget(R.id.teacher_name);
        //recolor.addTarget(R.id.view_teacher_name);
        recolor.addTarget("teacher_name");
        set.addTransition(recolor);

        Transition changeBounds = new ChangeBounds();
        //changeBounds.addTarget(R.id.teacher_name);
        //changeBounds.addTarget("teacher_name");
        //changeBounds.addTarget("teacher_phone_icon");
        set.addTransition(changeBounds);

        Transition changeTransform = new ChangeTransform();
        changeTransform.addTarget(R.id.teacher_name);
        changeTransform.addTarget("teacher_name");
        //set.addTransition(changeTransform);

        Transition changeClipBounds = new ChangeClipBounds();
        changeClipBounds.addTarget(R.id.teacher_name);
        changeClipBounds.addTarget("teacher_name");
        //set.addTransition(changeClipBounds);

        set.addTransition(new TextSizeTransition().addTarget("teacher_name").addTarget(R.id.teacher_name));

        //set.setDuration(5000);

        return set;
    }

    private TransitionUtils() {
    }
}
