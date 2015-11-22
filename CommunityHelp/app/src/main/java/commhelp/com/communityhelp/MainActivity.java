package commhelp.com.communityhelp;

import android.*;
import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LoginDialogFragment.LoginDialogListener {

    private static final String TAG = "MainActivity";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final long LOCATION_REFRESH_TIME = 10;
    private static final float LOCATION_REFRESH_DISTANCE = (float) 0.1;
    private static final String PREFS_NAME = "MyPrefsFile";
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private LocationManager mLocationManager = null;
    private String mToken = "";
    private String mName = "";
    private String mSex = "";
    private String mPhoneNumber = "";
    private String mRole = "";
    private String mYearOfBirth = "";
    private String mGender = "";
    private DialogFragment dialog;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(view.getContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "NO KNOWN LOCATION");
                    return;
                }
                Location location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                JSONObject jo = new JSONObject();
                try {
                    jo.put("uid", mToken);
                    jo.put("name", mName);
                    jo.put("lat", Double.toString(location.getLatitude()));
                    jo.put("lon", Double.toString(location.getLongitude()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                executePost("http://commhelpapp.appspot.com/gethelp", jo.toString());
                Snackbar.make(view, "Hold on! Help is on the way!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, 0);
        if (sharedPreferences.getString(QuickstartPreferences.NAME, "").equals("")) {
            mRegistrationBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    SharedPreferences sharedPreferences =
                            PreferenceManager.getDefaultSharedPreferences(context);
                    mToken = sharedPreferences
                            .getString(QuickstartPreferences.TOKEN, "");
                    Log.i(TAG, "I have registered" + mToken);
                    checkAndSend();
                }
            };
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
            showLoginDialog();
        } else {
            mName = sharedPreferences.getString(QuickstartPreferences.NAME, "");
            mRole = sharedPreferences.getString(QuickstartPreferences.ROLE, "");
            mPhoneNumber = sharedPreferences.getString(QuickstartPreferences.PHONENUMBER, "");
            mYearOfBirth = sharedPreferences.getString(QuickstartPreferences.YEAROFBIRTH, "");
            mGender = sharedPreferences.getString(QuickstartPreferences.GENDER, "");
            mToken = sharedPreferences.getString(QuickstartPreferences.TOKEN, "");

            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_REFRESH_TIME,
                    LOCATION_REFRESH_DISTANCE, mLocationListener);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                    LOCATION_REFRESH_DISTANCE, mLocationListener);
        }

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            try {
                throw new Exception("NASOL BOSULIQUE");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            JSONObject jo = new JSONObject();
            if (mToken.equals("")) {
                return;
            }
            try {
                jo.put("uid", mToken);
                jo.put("lat", Double.toString(location.getLatitude()));
                jo.put("lon", Double.toString(location.getLongitude()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String ret = executePost("http://commhelpapp.appspot.com/updateuser", jo.toString());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://commhelp.com.communityhelp/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);

        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        // Commit the edits!
        editor.commit();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static String executePost(String targetURL, String urlParameters) {
        new HttpGetAsyncTask().execute(targetURL, urlParameters);
        return "puta";
    }

    public void showLoginDialog() {
        // Create an instance of the dialog fragment and show it
        dialog = new LoginDialogFragment();
        dialog.show(getSupportFragmentManager(), "LoginDialogFragment");
    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the LoginDialogFragment.LoginDialogListener interface
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // User touched the dialog's positive button
        mName = ((EditText) dialog.getDialog().findViewById(R.id.username))
                .getText().toString();
        if (((RadioButton) dialog.getDialog().findViewById(R.id.radio_user)).isChecked()) {
            mRole = "default";
        } else {
            mRole = "helper";
        }
        mPhoneNumber = ((EditText) dialog.getDialog().findViewById(R.id.phone_number))
                .getText().toString();
        Log.i(TAG, "name: " + mName + " role: " + mRole + " phone: " + mPhoneNumber);
        if (mRole.equals("default")) {
            mYearOfBirth = ((EditText) dialog.getDialog().findViewById(R.id.birth_year))
                    .getText().toString();
            if (((RadioButton) dialog.getDialog().findViewById(R.id.radio_m)).isChecked()) {
                mGender = "male";
            } else {
                mGender = "female";
            }
            Log.i(TAG, "role: " + mRole + " yearOfBirth: " + mYearOfBirth + " is male: " + mGender);
        }

        checkAndSend();
    }

    public void checkAndSend() {
        if (mToken.equals("")) {
            // Registration not finished yet
            return;
        }
        if (mName.equals("")) { // TODO: name field in form should be required
            // Form not completed yet
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            try {
                throw new Exception("NASOL BOSULIQUE");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, 0).edit();
        editor.putString(QuickstartPreferences.TOKEN, mToken);
        editor.putString(QuickstartPreferences.NAME, mName);
        editor.putString(QuickstartPreferences.ROLE, mRole);
        editor.putString(QuickstartPreferences.PHONENUMBER, mPhoneNumber);
        editor.putString(QuickstartPreferences.YEAROFBIRTH, mYearOfBirth);
        editor.putString(QuickstartPreferences.GENDER, mGender);
        editor.commit();
        Location lastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        JSONObject jo = new JSONObject();
        try {
            jo.put("uid", mToken);
            jo.put("lat", Double.toString(lastKnownLocation.getLatitude()));
            jo.put("lon", Double.toString(lastKnownLocation.getLongitude()));
            jo.put("name", mName);
            jo.put("gender", mGender);
            jo.put("role", mRole);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "JASON " + jo.toString());
        String ret = executePost("http://commhelpapp.appspot.com/registeruser", jo.toString());
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_REFRESH_TIME,
                LOCATION_REFRESH_DISTANCE, mLocationListener);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                LOCATION_REFRESH_DISTANCE, mLocationListener);
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        RadioGroup gender = (RadioGroup) dialog.getDialog().findViewById(R.id.radio_gender);
        EditText birthYear = (EditText) dialog.getDialog().findViewById(R.id.birth_year);
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radio_user:
                if (checked) {
                    gender.setVisibility(View.VISIBLE);
                    birthYear.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.radio_volunteer:
                if (checked) {
                    gender.setVisibility(View.INVISIBLE);
                    birthYear.setVisibility(View.INVISIBLE);
                }
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://commhelp.com.communityhelp/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }
}
