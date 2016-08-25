package com.maxkrass.stundenplan.activities;

import android.content.Intent;
import android.os.Bundle;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.maxkrass.stundenplan.R;

/**
 * Max made this for Stundenplan2 on 03.08.2016.
 */
public class SplashActivity extends BaseActivity {

	private static final String GOOGLE_TOS_URL =
			"https://www.google.com/policies/terms/";
	private static final int    RC_SIGN_IN     = 7001;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FirebaseAuth auth = FirebaseAuth.getInstance();
		if (auth.getCurrentUser() != null) {
			startActivity(new Intent(this, MainActivity.class));
			finish();
		} else {
			startActivityForResult(
					AuthUI.getInstance().createSignInIntentBuilder()
							.setTheme(R.style.AppTheme)
							.setLogo(R.mipmap.ic_launcher)
							.setProviders(AuthUI.GOOGLE_PROVIDER)
							.setTosUrl(GOOGLE_TOS_URL)
							.build(),
					RC_SIGN_IN);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RC_SIGN_IN) {
			if (resultCode == RESULT_OK) {
				startActivity(new Intent(this, MainActivity.class));
				finish();
				return;
			}

			if (resultCode == RESULT_CANCELED) {
				startActivityForResult(
						AuthUI.getInstance().createSignInIntentBuilder()
								.setTheme(R.style.AppTheme)
								.setLogo(R.mipmap.ic_launcher)
								.setProviders(AuthUI.GOOGLE_PROVIDER)
								.setTosUrl(GOOGLE_TOS_URL)
								.build(),
						RC_SIGN_IN);
			}
		}
	}
}
