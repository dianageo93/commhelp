package commhelp.com.communityhelp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.media.Rating;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;

public class StatsDialogFragment extends DialogFragment {

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface StatsDialogListener {
        public void onSubmitDetails(DialogFragment dialog);
        public void onDiscardDetails(View view);
    }

    // Use this instance of the interface to deliver action events
    StatsDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the LoginDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the LoginDialogListener so we can send events to the host
            mListener = (StatsDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement StatsDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.stats_dialog, null))
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        /*
        ImageView badge = (ImageView) getActivity().findViewById(R.id.badge);
        int badge_src = 0;
        String mLevel = "3";
        if (mLevel.equals("1")) {
            badge_src = R.drawable.rookie1;
        }
        if (mLevel.equals("2")) {
            badge_src = R.drawable.guardianangle2;
        }
        if (mLevel.equals("3")) {
            badge_src = R.drawable.saviour3;
        }
        if (mLevel.equals("4")) {
            badge_src = R.drawable.superhero4;
        }
        badge.setImageResource(badge_src);
        RatingBar stars = (RatingBar) getActivity().findViewById(R.id.ratingBar);
        stars.setRating(4);
        */
        return builder.create();
    }

}