package com.maxkrass.stundenplan.activities;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.maxkrass.stundenplan.R;
import com.maxkrass.stundenplan.customViews.CheckBoxWidget;

public class BaseActivity extends AppCompatActivity implements View.OnClickListener {

	//final int RESULT_CODE_GOOD = 0x0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Window window = getWindow();

		window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

		window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		window.getDecorView().setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE
						| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
		window.setStatusBarColor(ContextCompat.getColor(this, R.color.status_bar_color));
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

	@SuppressWarnings("ConstantConditions")
	String getUid() {
		return FirebaseAuth.getInstance().getCurrentUser().getUid();
	}
}
