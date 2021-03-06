package commhelp.com.communityhelp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.Rating;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;

import org.json.JSONException;
import org.json.JSONObject;

public class RatingDialogFragment extends DialogFragment {
    private MainActivity main;

    public void setMainActivity(MainActivity _main) {
        this.main = _main;
    }

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface RatingDialogListener {
        public void onSubmitDetails(DialogFragment dialog);
        public void onDiscardDetails(View view);
    }

    // Use this instance of the interface to deliver action events
    RatingDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the LoginDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the LoginDialogListener so we can send events to the host
            mListener = (RatingDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement RatingDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.rating_dialog, null);
        builder.setView(dialogView)
                .setNegativeButton("Submit", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        Dialog dialog = builder.create();

        //Button submit = (Button) getActivity().findViewById()
        JSONObject jo = new JSONObject();
        RatingBar ratingBar = (RatingBar) dialogView.findViewById(R.id.ratingBar);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(main.PREFS_NAME, 0);
        try {
            jo.put("source_uid", main.getToken());
            jo.put("volunteer_uid", sharedPreferences.getString("volunteer_uid", ""));
            jo.put("rank", ratingBar.getRating());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("TAG", "AM RATAT "+ratingBar.getRating());
        String ret = main.executePost("http://commhelpapp.appspot.com/givereview", jo.toString());

        return dialog;
    }

}