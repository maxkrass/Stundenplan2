package com.maxkrass.stundenplan.objects;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.maxkrass.stundenplan.R;

public enum Color {

    RED(R.color.material_red),
    PINK(R.color.material_pink),
    PURPLE(R.color.material_purple),
    DEEP_PURPLE(R.color.material_deep_purple),
    INDIGO(R.color.material_indigo),
    BLUE(R.color.material_blue),
    LIGHT_BLUE(R.color.material_light_blue),
    CYAN(R.color.material_cyan),
    TEAL(R.color.material_teal),
    GREEN(R.color.material_green),
    LIGHT_GREEN(R.color.material_light_green),
    LIME(R.color.material_lime),
    YELLOW(R.color.material_yellow),
    AMBER(R.color.material_amber),
    ORANGE(R.color.material_orange),
    DEEP_ORANGE(R.color.material_deep_orange),
    BROWN(R.color.material_brown),
    GREY(R.color.material_grey),
    BLUE_GREY(R.color.material_blue_grey),
    BLACK(R.color.material_black),
    WHITE(R.color.material_white);

    public int getColor(Context context) {
        return ContextCompat.getColor(context, color);
    }

    private int color;

    Color(int color) {
        this.color = color;
    }

    @Override
    public String toString() {
        String[] words = super.toString().split("_");
        StringBuilder sb = new StringBuilder();
        if (words[0].length() > 0) {
            sb.append(Character.toUpperCase(words[0].charAt(0))).append(words[0].subSequence(1, words[0].length()).toString().toLowerCase());
            for (int i = 1; i < words.length; i++) {
                sb.append(" ");
                sb.append(Character.toUpperCase(words[i].charAt(0))).append(words[i].subSequence(1, words[i].length()).toString().toLowerCase());
            }
        }
        return sb.toString();
    }
}
