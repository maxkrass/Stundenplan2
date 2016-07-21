package com.maxkrass.stundenplan.tools;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;

public class Tools {

    public static float getPixels(int densityIndependentPixels, Context context) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, densityIndependentPixels, context.getResources().getDisplayMetrics());
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

}
