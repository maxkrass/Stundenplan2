package com.maxkrass.stundenplan.objects;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;

import com.maxkrass.stundenplan.R;

import java.io.Serializable;

public enum Color implements Serializable {

	LIGHT_GREEN(R.color.material_light_green);

	private final int color;

	Color(@ColorRes int color) {
		this.color = color;
	}

	public int getColor(Context context) {
		return ContextCompat.getColor(context, color);
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
