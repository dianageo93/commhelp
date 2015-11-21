package commhelp.com.communityhelp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LoginDialogFragment.LoginDialogListener {

    private static final String TAG = "MainActivity";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final long LOCATION_REFRESH_TIME = 10;
    private static final float LOCATION_REFRESH_DISTANCE = (float) 0.1;
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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
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

        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_REFRESH_TIME,
                LOCATION_REFRESH_DISTANCE, mLocationListener);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                LOCATION_REFRESH_DISTANCE, mLocationListener);


        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                mToken = sharedPreferences
                        .getString(QuickstartPreferences.TOKEN, "");
                Log.i(TAG, "I have registered" + mToken);
                showLoginDialog();

                check_and_send();
            }
        };

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            try {
                throw new Exception("NASOL BOSULIQUE");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            // Send new location to the server
            Log.i(TAG, "M-am mutat la tara");
            JSONObject jo = new JSONObject();
            try {
                jo.put("uid", mToken);
                jo.put("lat", Double.toString(location.getLatitude()));
                jo.put("lon", Double.toString(location.getLongitude()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //String ret = executePost("http://commhelpapp.appspot.com/updateuser", jo.toString());
            //Log.i(TAG, "RASPUNS"+ret);
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
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
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
        /*
        HttpURLConnection connection = null;
        try {
            //Create connection
            URL url = new URL(targetURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty( "Content-type", "application/json");

            conn.setDoOutput(true);

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

            writer.write(urlParameters);
            writer.flush();
            String line;
            BufferedReader reader = new BufferedReader(new
                    InputStreamReader(conn.getInputStream()));
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            writer.close();
            reader.close();
            return "pup";
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        */
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

        check_and_send();
    }
    
    public void check_and_send() {
        if (mToken == "") {
            // Registration not finished yet
            return;
        }
        if (mName == "") { // TODO: name field in form should be required
            // Form not completed yet
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            try {
                throw new Exception("NASOL BOSULIQUE");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
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
        Log.i(TAG, "JASON "+jo.toString());
        String ret = executePost("http://commhelpapp.appspot.com/registeruser", jo.toString());
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        RadioGroup gender = (RadioGroup) dialog.getDialog().findViewById(R.id.radio_gender);
        EditText birthYear = (EditText) dialog.getDialog().findViewById(R.id.birth_year);
        // Check which radio button was clicked
        switch(view.getId()) {
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
}
