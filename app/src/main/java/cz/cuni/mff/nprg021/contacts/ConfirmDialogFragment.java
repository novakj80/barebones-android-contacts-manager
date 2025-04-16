package cz.cuni.mff.nprg021.contacts;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

public class ConfirmDialogFragment extends DialogFragment {

    public interface ConfirmDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    ConfirmDialogListener listener;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.confirm_dialog_message)
                .setPositiveButton(R.string.confirm_dialog_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onDialogPositiveClick(ConfirmDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.confirm_dialog_negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onDialogNegativeClick(ConfirmDialogFragment.this);
                    }
                })
                .setNeutralButton(R.string.confirm_dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (ConfirmDialogListener) context;
    }

    // Must be implemented for compatibility on API 22 and lower
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (ConfirmDialogListener) activity;
    }
}
