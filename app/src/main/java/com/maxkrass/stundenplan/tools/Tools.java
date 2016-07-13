package com.maxkrass.stundenplan.tools;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;

import com.maxkrass.stundenplan.objects.Teacher;
import com.orm.SugarRecord;

public class Tools {

    public static float getPixels(int densityIndepandantPixels, Context context) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, densityIndepandantPixels, context.getResources().getDisplayMetrics());
    }

    public static int getTeacherPosition(Teacher teacher) {
        return SugarRecord.listAll(Teacher.class, "name").indexOf(teacher);
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

    public static int fetchPrimaryColor(Context mContext) {
        TypedValue typedValue = new TypedValue();

        mContext.getTheme().resolveAttribute(android.R.attr.colorPrimary, typedValue, true);

        return typedValue.data;
    }

}
