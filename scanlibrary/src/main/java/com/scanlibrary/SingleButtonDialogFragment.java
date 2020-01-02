package com.scanlibrary;

import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

@SuppressLint("ValidFragment")
public class SingleButtonDialogFragment extends DialogFragment {
    protected boolean isCancelable;
    protected String message;
    protected int positiveButtonTitle;
    protected String title;

    public SingleButtonDialogFragment(int positiveButtonTitle2, String message2, String title2, boolean isCancelable2) {
        this.positiveButtonTitle = positiveButtonTitle2;
        this.message = message2;
        this.title = title2;
        this.isCancelable = isCancelable2;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Builder(getActivity()).setTitle(this.title).setCancelable(this.isCancelable).setMessage(this.message).setPositiveButton(this.positiveButtonTitle, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        }).create();
    }
}
