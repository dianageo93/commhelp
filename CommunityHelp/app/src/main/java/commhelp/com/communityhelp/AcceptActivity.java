package commhelp.com.communityhelp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

public class AcceptActivity extends Activity {
    public static final String TAG = "AcceptActivity";
    private static final String PREFS_NAME = "MyPrefsFile";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "accept activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accept_save_request);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_accept);
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, 0);
        final String mToken = sharedPreferences.getString(QuickstartPreferences.TOKEN, "");
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject jo = new JSONObject();
                if (mToken.equals("")) {
                    return;
                }
                try {
                    jo.put("uid", mToken);
                    jo.put("victim_uid", getIntent().getStringExtra("victim_uid"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String ret = MainActivity.executePost(
                        "http://commhelpapp.appspot.com/givehelp", jo.toString());
                Snackbar.make(view, getIntent().getStringExtra("victim_name") +
                        " has been notified and is waiting !", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}
