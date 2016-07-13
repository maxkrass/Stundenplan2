package com.maxkrass.stundenplan.listener;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.view.MenuItem;

import com.maxkrass.stundenplan.R;
import com.maxkrass.stundenplan.activities.MainActivity;

/**
 * Max made this for Stundenplan2 on 07.07.2016.
 */
public class MainDrawerItemSelcetedListener implements NavigationView.OnNavigationItemSelectedListener {

	Context context;

	public MainDrawerItemSelcetedListener(Context context) {
		this.context = context;
	}

	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			default:
				return false;
			case R.id.drawer_main_item:
				context.startActivity(new Intent(context, MainActivity.class));
				break;
			case R.id.drawer_subjects_item:
				//context.startActivity(new Intent(context, ManageSubjectsActivity.class));
				break;
			case R.id.drawer_teachers_item:
				//context.startActivity(new Intent(context, ManageTeachersActivity.class));
				break;
			case R.id.drawer_settings_item:
				//context.startActivity(new Intent(context, SettingsActivity.class));
				break;
		}
		return true;
	}
}
