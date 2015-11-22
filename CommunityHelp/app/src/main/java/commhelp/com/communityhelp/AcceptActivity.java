package commhelp.com.communityhelp;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class AcceptActivity extends Activity {
    public static final String TAG = "AcceptActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "accept activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accept_save_request);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_accept);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "click pe buton");
            }
        });
    }
}
