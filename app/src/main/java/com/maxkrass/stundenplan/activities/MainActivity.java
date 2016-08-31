package com.maxkrass.stundenplan.activities;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.maxkrass.stundenplan.R;
import com.maxkrass.stundenplan.databinding.ActivityMainBinding;
import com.maxkrass.stundenplan.fragments.MainActivityFragment;
import com.maxkrass.stundenplan.fragments.ManageSubjectsFragment;
import com.maxkrass.stundenplan.fragments.ManageTeachersFragment;
import com.maxkrass.stundenplan.fragments.SettingsFragment;
import com.maxkrass.stundenplan.fragments.SubstitutionPlanFragment;
import com.maxkrass.stundenplan.tools.Tools;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

public class MainActivity extends BaseActivity implements Drawer.OnDrawerItemClickListener, SubstitutionPlanFragment.OnFragmentInteractionListener {

	private static final String TAG                   = "MainActivity";
	private final        String MAIN_FRAGMENT_TAG     = "main_fragment";
	private final        String SUBSTITUTION_PLAN_TAG = "substitution_plan_fragment";
	private final        String MANAGE_TEACHERS_TAG   = "manage_teachers_fragment";
	private final        String SETTINGS_FRAGMENT_TAG = "settings_fragment";
	private final        String MANAGE_SUBJECTS_TAG   = "manage_subjects_fragment";
	public  TabLayout                      tabLayout;
	int width;
	private String                         lastFragmentTag;
	private Toolbar                        toolbar;
	private Drawer                         result;
	private FirebaseAuth                   mFirebaseAuth;
	private FirebaseAuth.AuthStateListener mAuthListener;
	private FirebaseUser                   user;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (width <= 480) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		mFirebaseAuth.addAuthStateListener(mAuthListener);
	}

	@Override
	protected void onStop() {
		super.onStop();
		mFirebaseAuth.removeAuthStateListener(mAuthListener);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("selectedItem", result.getCurrentSelectedPosition());
		outState.putString("lastFragment", lastFragmentTag);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Display display = getWindowManager().getDefaultDisplay();
		width = display.getWidth();
		if (width <= 480) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		ActivityMainBinding activityMainBinding = DataBindingUtil.setContentView(MainActivity.this, R.layout.activity_main);
		activityMainBinding.mainAppBarLayout.setPadding(0, Tools.getStatusBarHeight(MainActivity.this), 0, 0);
		toolbar = activityMainBinding.mainToolbar;
		tabLayout = activityMainBinding.mainTabLayout;
		//setSupportActionBar(toolbar);

		result = new DrawerBuilder(this)
				.withToolbar(toolbar)
				.inflateMenu(R.menu.drawer_menu)
				.withOnDrawerItemClickListener(this)
				.withDrawerLayout(com.mikepenz.materialdrawer.R.layout.material_drawer_fits_not)
				.withTranslucentStatusBar(false)
				.withSavedInstance(savedInstanceState)
				.build();

		mFirebaseAuth = FirebaseAuth.getInstance();
		mAuthListener = new FirebaseAuth.AuthStateListener() {
			@Override
			public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
				user = firebaseAuth.getCurrentUser();
				if (user != null) {
					Log.d(TAG, "onAuthStateChanged:signed_in: " + user.getUid());
					new AccountHeaderBuilder()
							.withActivity(MainActivity.this)
							.withHeaderBackground(R.color.material_grey)
							.withDrawer(result)
							.addProfiles(
									new ProfileDrawerItem()
											.withName(user.getDisplayName())
											.withEmail(user.getEmail())
											.withIcon(user.getPhotoUrl())
							).build();
					/*SharedPreferences settings =
							getPreferences(Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = settings.edit();
					editor.putString(PREF_ACCOUNT_NAME, user.getEmail());
					editor.apply();
					mCredential = GoogleAccountCredential.usingOAuth2(getApplicationContext(), Arrays.asList(SCOPES)).setBackOff(new ExponentialBackOff());
					mCredential.setSelectedAccountName(user.getEmail());
					//getResultsFromApi();
					final Calendar mService = new com.google.api.services.calendar.Calendar.Builder(
							AndroidHttp.newCompatibleTransport(), JacksonFactory.getDefaultInstance(), mCredential)
							.setApplicationName("Google Calendar API Android Quickstart")
							.build();
					final DateTime now = new DateTime(System.currentTimeMillis());
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								Events events;
								events = mService.events().list("primary")
										.setMaxResults(10)
										.setTimeMin(now)
										.setOrderBy("startTime")
										.setSingleEvents(true)
										.execute();
								List<Event> items = events.getItems();
								Log.d(TAG, "onAuthStateChanged: " + items);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}).start();*/
				}

			}
		};
		if (savedInstanceState != null) {
			lastFragmentTag = savedInstanceState.getString("lastFragment");
		}
		if (getSupportFragmentManager().findFragmentByTag(lastFragmentTag) == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new MainActivityFragment(), MAIN_FRAGMENT_TAG).commit();
			lastFragmentTag = MAIN_FRAGMENT_TAG;
			Log.d(TAG, "onCreate: adding MainActivityFragment");
		}
		restoreUI();
		Log.d(TAG, "onCreate: finished onCreate");
	}

	private void restoreUI() {
		switch (lastFragmentTag) {
			case MAIN_FRAGMENT_TAG:
				toolbar.setTitle(R.string.app_name);
				tabLayout.setVisibility(View.GONE);
				result.setSelectionAtPosition(0, false);
				break;
			case SUBSTITUTION_PLAN_TAG:
				toolbar.setTitle("Vertretungsplan");
				tabLayout.setVisibility(View.VISIBLE);
				result.setSelectionAtPosition(1, false);
				break;
			case MANAGE_TEACHERS_TAG:
				toolbar.setTitle(getString(R.string.action_teachers));
				tabLayout.setVisibility(View.GONE);
				result.setSelectionAtPosition(2, false);
				break;
			case MANAGE_SUBJECTS_TAG:
				toolbar.setTitle(getString(R.string.action_subjects));
				tabLayout.setVisibility(View.GONE);
				result.setSelectionAtPosition(3, false);
				break;
			case SETTINGS_FRAGMENT_TAG:
				toolbar.setTitle(getString(R.string.action_settings));
				tabLayout.setVisibility(View.GONE);
				result.setSelectionAtPosition(4, false);
				break;
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState != null) {
			//result.setSelectionAtPosition(savedInstanceState.getInt("selectedItem"), false);
		}
	}

	@Override
	public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
		switch ((int) drawerItem.getIdentifier()) {
			default:
				return false;
			case R.id.drawer_main_item:
				if (getSupportFragmentManager().findFragmentByTag(MAIN_FRAGMENT_TAG) == null) {
					getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MainActivityFragment(), MAIN_FRAGMENT_TAG).commit();
					toolbar.setTitle(R.string.app_name);
					tabLayout.setVisibility(View.GONE);
					lastFragmentTag = MAIN_FRAGMENT_TAG;
				}
				break;
			case R.id.drawer_substitution_item:
				if (getSupportFragmentManager().findFragmentByTag(SUBSTITUTION_PLAN_TAG) == null) {
					getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, SubstitutionPlanFragment.newInstance(getUid()), SUBSTITUTION_PLAN_TAG).commit();
					toolbar.setTitle("Vertretungsplan");
					tabLayout.setVisibility(View.VISIBLE);
					lastFragmentTag = SUBSTITUTION_PLAN_TAG;
				}
				break;
			case R.id.drawer_teachers_item:
				if (getSupportFragmentManager().findFragmentByTag(MANAGE_TEACHERS_TAG) == null) {
					getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ManageTeachersFragment(), MANAGE_TEACHERS_TAG).commit();
					toolbar.setTitle("Lehrer");
					tabLayout.setVisibility(View.GONE);
					lastFragmentTag = MANAGE_TEACHERS_TAG;
				}
				break;
			case R.id.drawer_subjects_item:
				if (getSupportFragmentManager().findFragmentByTag(MANAGE_SUBJECTS_TAG) == null) {
					getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ManageSubjectsFragment(), MANAGE_SUBJECTS_TAG).commit();
					toolbar.setTitle("Fächer");
					tabLayout.setVisibility(View.GONE);
					lastFragmentTag = MANAGE_SUBJECTS_TAG;
				}
				break;
			case R.id.drawer_settings_item:
				if (getSupportFragmentManager().findFragmentByTag(SETTINGS_FRAGMENT_TAG) == null) {
					getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment(), SETTINGS_FRAGMENT_TAG).commit();
					toolbar.setTitle("Einstellungen");
					tabLayout.setVisibility(View.GONE);
					lastFragmentTag = SETTINGS_FRAGMENT_TAG;
				}
				break;
		}
		result.closeDrawer();
		return true;
	}

//	GoogleAccountCredential mCredential;
//	private TextView mOutputText;
//	private Button mCallApiButton;
//	ProgressDialog mProgress;
//
//	static final int REQUEST_ACCOUNT_PICKER = 1000;
//	static final int REQUEST_AUTHORIZATION = 1001;
//	static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
//	static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
//
//	private static final String BUTTON_TEXT = "Call Google Calendar API";
//	private static final String PREF_ACCOUNT_NAME = "accountName";
//	private static final String[] SCOPES = {CalendarScopes.CALENDAR_READONLY};
//
//	/**
//	 * Create the main activity.
//	 *
//	 * @param savedInstanceState previously saved instance data.
//	 */
//	void onCreate2(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		LinearLayout activityLayout = new LinearLayout(this);
//		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//				LinearLayout.LayoutParams.MATCH_PARENT,
//				LinearLayout.LayoutParams.MATCH_PARENT);
//		activityLayout.setLayoutParams(lp);
//		activityLayout.setOrientation(LinearLayout.VERTICAL);
//		activityLayout.setPadding(16, 16, 16, 16);
//
//		ViewGroup.LayoutParams tlp = new ViewGroup.LayoutParams(
//				ViewGroup.LayoutParams.WRAP_CONTENT,
//				ViewGroup.LayoutParams.WRAP_CONTENT);
//
//		mCallApiButton = new Button(this);
//		mCallApiButton.setText(BUTTON_TEXT);
//		mCallApiButton.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				mCallApiButton.setEnabled(false);
//				mOutputText.setText("");
//				getResultsFromApi();
//				mCallApiButton.setEnabled(true);
//			}
//		});
//		activityLayout.addView(mCallApiButton);
//
//		mOutputText = new TextView(this);
//		mOutputText.setLayoutParams(tlp);
//		mOutputText.setPadding(16, 16, 16, 16);
//		mOutputText.setVerticalScrollBarEnabled(true);
//		mOutputText.setMovementMethod(new ScrollingMovementMethod());
//		mOutputText.setText(
//				"Click the \'" + BUTTON_TEXT + "\' button to test the API.");
//		activityLayout.addView(mOutputText);
//
//		mProgress = new ProgressDialog(this);
//		mProgress.setMessage("Calling Google Calendar API ...");
//
//		setContentView(activityLayout);
//
//		// Initialize credentials and service object.
//		mCredential = GoogleAccountCredential.usingOAuth2(
//				getApplicationContext(), Arrays.asList(SCOPES))
//				.setBackOff(new ExponentialBackOff());
//	}
//
//
//	/**
//	 * Attempt to call the API, after verifying that all the preconditions are
//	 * satisfied. The preconditions are: Google Play Services installed, an
//	 * account was selected and the device currently has online access. If any
//	 * of the preconditions are not satisfied, the app will prompt the user as
//	 * appropriate.
//	 */
//	private void getResultsFromApi() {
//		if (!isGooglePlayServicesAvailable()) {
//			acquireGooglePlayServices();
//		} else if (mCredential.getSelectedAccountName() == null) {
//			chooseAccount();
//		} else if (!isDeviceOnline()) {
//			mOutputText.setText("No network connection available.");
//		} else {
//			new MakeRequestTask(mCredential).execute();
//		}
//	}
//
//	/**
//	 * Attempts to set the account used with the API credentials. If an account
//	 * name was previously saved it will use that one; otherwise an account
//	 * picker dialog will be shown to the user. Note that the setting the
//	 * account to use with the credentials object requires the app to have the
//	 * GET_ACCOUNTS permission, which is requested here if it is not already
//	 * present. The AfterPermissionGranted annotation indicates that this
//	 * function will be rerun automatically whenever the GET_ACCOUNTS permission
//	 * is granted.
//	 */
//	@AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
//	private void chooseAccount() {
//		if (EasyPermissions.hasPermissions(
//				this, Manifest.permission.GET_ACCOUNTS)) {
//			String accountName = getPreferences(Context.MODE_PRIVATE)
//					.getString(PREF_ACCOUNT_NAME, null);
//			if (accountName != null) {
//				mCredential.setSelectedAccountName(accountName);
//				getResultsFromApi();
//			} else {
//				// Start a dialog from which the user can choose an account
//				startActivityForResult(
//						mCredential.newChooseAccountIntent(),
//						REQUEST_ACCOUNT_PICKER);
//			}
//		} else {
//			// Request the GET_ACCOUNTS permission via a user dialog
//			EasyPermissions.requestPermissions(
//					this,
//					"This app needs to access your Google account (via Contacts).",
//					REQUEST_PERMISSION_GET_ACCOUNTS,
//					Manifest.permission.GET_ACCOUNTS);
//		}
//	}
//
//	/**
//	 * Called when an activity launched here (specifically, AccountPicker
//	 * and authorization) exits, giving you the requestCode you started it with,
//	 * the resultCode it returned, and any additional data from it.
//	 *
//	 * @param requestCode code indicating which activity result is incoming.
//	 * @param resultCode  code indicating the result of the incoming
//	 *                    activity result.
//	 * @param data        Intent (containing result data) returned by incoming
//	 *                    activity result.
//	 */
//	@Override
//	protected void onActivityResult(
//			int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		switch (requestCode) {
//			case REQUEST_GOOGLE_PLAY_SERVICES:
//				if (resultCode != RESULT_OK) {
//					mOutputText.setText(
//							"This app requires Google Play Services. Please install " +
//									"Google Play Services on your device and relaunch this app.");
//				} else {
//					getResultsFromApi();
//				}
//				break;
//			case REQUEST_ACCOUNT_PICKER:
//				if (resultCode == RESULT_OK && data != null &&
//						data.getExtras() != null) {
//					String accountName =
//							data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
//					if (accountName != null) {
//						SharedPreferences settings =
//								getPreferences(Context.MODE_PRIVATE);
//						SharedPreferences.Editor editor = settings.edit();
//						editor.putString(PREF_ACCOUNT_NAME, accountName);
//						editor.apply();
//						mCredential.setSelectedAccountName(accountName);
//						getResultsFromApi();
//					}
//				}
//				break;
//			case REQUEST_AUTHORIZATION:
//				if (resultCode == RESULT_OK) {
//					getResultsFromApi();
//				}
//				break;
//		}
//	}
//
//	/**
//	 * Respond to requests for permissions at runtime for API 23 and above.
//	 *
//	 * @param requestCode  The request code passed in
//	 *                     requestPermissions(android.app.Activity, String, int, String[])
//	 * @param permissions  The requested permissions. Never null.
//	 * @param grantResults The grant results for the corresponding permissions
//	 *                     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
//	 */
//	@Override
//	public void onRequestPermissionsResult(int requestCode,
//	                                       @NonNull String[] permissions,
//	                                       @NonNull int[] grantResults) {
//		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//		EasyPermissions.onRequestPermissionsResult(
//				requestCode, permissions, grantResults, this);
//	}
//
//	/**
//	 * Callback for when a permission is granted using the EasyPermissions
//	 * library.
//	 *
//	 * @param requestCode The request code associated with the requested
//	 *                    permission
//	 * @param list        The requested permission list. Never null.
//	 */
//	@Override
//	public void onPermissionsGranted(int requestCode, List<String> list) {
//		// Do nothing.
//	}
//
//	/**
//	 * Callback for when a permission is denied using the EasyPermissions
//	 * library.
//	 *
//	 * @param requestCode The request code associated with the requested
//	 *                    permission
//	 * @param list        The requested permission list. Never null.
//	 */
//	@Override
//	public void onPermissionsDenied(int requestCode, List<String> list) {
//		// Do nothing.
//	}
//
//	/**
//	 * Checks whether the device currently has a network connection.
//	 *
//	 * @return true if the device has a network connection, false otherwise.
//	 */
//	private boolean isDeviceOnline() {
//		ConnectivityManager connMgr =
//				(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
//		return (networkInfo != null && networkInfo.isConnected());
//	}
//
//	/**
//	 * Check that Google Play services APK is installed and up to date.
//	 *
//	 * @return true if Google Play Services is available and up to
//	 * date on this device; false otherwise.
//	 */
//	private boolean isGooglePlayServicesAvailable() {
//		GoogleApiAvailability apiAvailability =
//				GoogleApiAvailability.getInstance();
//		final int connectionStatusCode =
//				apiAvailability.isGooglePlayServicesAvailable(this);
//		return connectionStatusCode == ConnectionResult.SUCCESS;
//	}
//
//	/**
//	 * Attempt to resolve a missing, out-of-date, invalid or disabled Google
//	 * Play Services installation via a user dialog, if possible.
//	 */
//	private void acquireGooglePlayServices() {
//		GoogleApiAvailability apiAvailability =
//				GoogleApiAvailability.getInstance();
//		final int connectionStatusCode =
//				apiAvailability.isGooglePlayServicesAvailable(this);
//		if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
//			showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
//		}
//	}
//
//
//	/**
//	 * Display an error dialog showing that Google Play Services is missing
//	 * or out of date.
//	 *
//	 * @param connectionStatusCode code describing the presence (or lack of)
//	 *                             Google Play Services on this device.
//	 */
//	void showGooglePlayServicesAvailabilityErrorDialog(
//			final int connectionStatusCode) {
//		GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
//		Dialog dialog = apiAvailability.getErrorDialog(
//				MainActivity.this,
//				connectionStatusCode,
//				REQUEST_GOOGLE_PLAY_SERVICES);
//		dialog.show();
//	}
//
//	/**
//	 * An asynchronous task that handles the Google Calendar API call.
//	 * Placing the API calls in their own task ensures the UI stays responsive.
//	 */
//	private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
//		private com.google.api.services.calendar.Calendar mService = null;
//		private Exception mLastError = null;
//
//		public MakeRequestTask(GoogleAccountCredential credential) {
//			HttpTransport transport = AndroidHttp.newCompatibleTransport();
//			JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
//			mService = new com.google.api.services.calendar.Calendar.Builder(
//					transport, jsonFactory, credential)
//					.setApplicationName("Google Calendar API Android Quickstart")
//					.build();
//		}
//
//		/**
//		 * Background task to call Google Calendar API.
//		 *
//		 * @param params no parameters needed for this task.
//		 */
//		@Override
//		protected List<String> doInBackground(Void... params) {
//			try {
//				return getDataFromApi();
//			} catch (Exception e) {
//				mLastError = e;
//				cancel(true);
//				return null;
//			}
//		}
//
//		/**
//		 * Fetch a list of the next 10 events from the primary calendar.
//		 *
//		 * @return List of Strings describing returned events.
//		 * @throws IOException
//		 */
//		private List<String> getDataFromApi() throws IOException {
//			// List the next 10 events from the primary calendar.
//			DateTime now = new DateTime(System.currentTimeMillis());
//			List<String> eventStrings = new ArrayList<>();
//			Events events = mService.events().list("primary")
//					.setMaxResults(10)
//					.setTimeMin(now)
//					.setOrderBy("startTime")
//					.setSingleEvents(true)
//					.execute();
//			List<Event> items = events.getItems();
//
//			for (Event event : items) {
//				DateTime start = event.getStart().getDateTime();
//				if (start == null) {
//					// All-day events don't have start times, so just use
//					// the start date.
//					start = event.getStart().getDate();
//				}
//				eventStrings.add(
//						String.format("%s (%s)", event.getSummary(), start));
//			}
//			return eventStrings;
//		}
//
//
//		@Override
//		protected void onPreExecute() {
//			mOutputText.setText("");
//			mProgress.show();
//		}
//
//		@Override
//		protected void onPostExecute(List<String> output) {
//			mProgress.hide();
//			if (output == null || output.size() == 0) {
//				mOutputText.setText("No results returned.");
//			} else {
//				output.add(0, "Data retrieved using the Google Calendar API:");
//				mOutputText.setText(TextUtils.join("\n", output));
//			}
//		}
//
//		@Override
//		protected void onCancelled() {
//			mProgress.hide();
//			if (mLastError != null) {
//				if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
//					showGooglePlayServicesAvailabilityErrorDialog(
//							((GooglePlayServicesAvailabilityIOException) mLastError)
//									.getConnectionStatusCode());
//				} else if (mLastError instanceof UserRecoverableAuthIOException) {
//					startActivityForResult(
//							((UserRecoverableAuthIOException) mLastError).getIntent(),
//							MainActivity.REQUEST_AUTHORIZATION);
//				} else {
//					mOutputText.setText("The following error occurred:\n"
//							+ mLastError.getMessage());
//				}
//			} else {
//				mOutputText.setText("Request cancelled.");
//			}
//		}
//	}
}
