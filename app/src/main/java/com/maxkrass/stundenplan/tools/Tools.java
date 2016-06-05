package com.maxkrass.stundenplan.tools;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.TypedValue;

import com.maxkrass.stundenplan.R;
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

    public static int fetchPrimaryColor(Context mContext) {
        TypedValue typedValue = new TypedValue();

        mContext.getTheme().resolveAttribute(android.R.attr.colorPrimary, typedValue, true);

        return typedValue.data;
    }

}
