package com.maxkrass.stundenplan;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.util.AttributeSet;
import android.util.Property;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.TextView;

public class TextTransition extends Transition {

    private static final String PROPNAME_TEXT_SIZE = "maxkrass:transition:textsize";
    //private static final String PROPNAME_TEXT_COLOR = "maxkrass:transition:textcolor";
    private static final String[] TRANSITION_PROPERTIES = { PROPNAME_TEXT_SIZE/*, PROPNAME_TEXT_COLOR*/};

    private static final Property<TextView, Float> TEXT_SIZE_PROPERTY =
            new Property<TextView, Float>(Float.class, "textSize") {
                @Override
                public Float get(TextView object) {
                    return object.getTextSize();
                }

                @Override
                public void set(TextView object, Float value) {
                    object.setTextSize(TypedValue.COMPLEX_UNIT_PX, value);
                }
            };

    private static final Property<TextView, Integer> TEXT_COLOR_PROPERTY =
            new Property<TextView, Integer>(Integer.class, "currentTextColor") {
                @Override
                public Integer get(TextView object) {
                    return object.getCurrentTextColor();
                }

                @Override
                public void set(TextView object, Integer value) {
                    object.setTextColor(value);
                }
            };

    public TextTransition() {

    }

    public TextTransition(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    public String[] getTransitionProperties() {
        return TRANSITION_PROPERTIES;
    }

    @Override
    public void captureStartValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override
    public void captureEndValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    private void captureValues(TransitionValues transitionValues) {
        if (transitionValues.view instanceof TextView) {
            TextView textView = (TextView) transitionValues.view;
            transitionValues.values.put(PROPNAME_TEXT_SIZE, textView.getTextSize());
            //transitionValues.values.put(PROPNAME_TEXT_COLOR, textView.getCurrentTextColor());
        }
    }

    @Override
    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
        if (startValues == null || endValues == null)
            return null;

        Float startSize = (Float) startValues.values.get(PROPNAME_TEXT_SIZE);
        Float endSize = (Float) endValues.values.get(PROPNAME_TEXT_SIZE);
        //Integer startColor = (Integer) startValues.values.get(PROPNAME_TEXT_COLOR);
        //Integer endColor = (Integer) endValues.values.get(PROPNAME_TEXT_COLOR);

        if(startSize == null || endSize == null || startSize.floatValue() == endSize.floatValue())
            return null;

        TextView view = (TextView) endValues.view;
        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, startSize);

        return ObjectAnimator.ofFloat(view, TEXT_SIZE_PROPERTY, startSize, endSize)/*.ofInt(view, TEXT_COLOR_PROPERTY, startColor, endColor)*/;
    }
}
