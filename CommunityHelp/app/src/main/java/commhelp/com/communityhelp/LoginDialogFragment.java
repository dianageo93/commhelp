package commhelp.com.communityhelp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class LoginDialogFragment extends DialogFragment {
    
    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface LoginDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onRadioButtonClicked(View view);
    }
    
    // Use this instance of the interface to deliver action events
    LoginDialogListener mListener;
    
    // Override the Fragment.onAttach() method to instantiate the LoginDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the LoginDialogListener so we can send events to the host
            mListener = (LoginDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement LoginDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.login_dialog, null))
                .setPositiveButton(R.string.dialog_register, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the positive button event back to the host activity
                        mListener.onDialogPositiveClick(LoginDialogFragment.this);
                    }
                });
        return builder.create();
    }

}