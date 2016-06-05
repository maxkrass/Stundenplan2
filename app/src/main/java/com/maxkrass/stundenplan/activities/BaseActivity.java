package com.maxkrass.stundenplan.activities;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.maxkrass.stundenplan.views.CheckBoxWidget;

public class BaseActivity extends AppCompatActivity implements View.OnClickListener {

	final int REQUEST_CODE_NEW_LESSON = 0x10;
	//final int RESULT_CODE_GOOD = 0x0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().getDecorView().setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE
						| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
	}

	public void onClick(View view) {
		if (view instanceof CheckBoxWidget) {
			((CheckBoxWidget) view).toggle();
		}
	}

	@NonNull
	@Override
	public ActionBar getActionBar() {
		assert super.getActionBar() != null;
		return super.getActionBar();
	}
}
