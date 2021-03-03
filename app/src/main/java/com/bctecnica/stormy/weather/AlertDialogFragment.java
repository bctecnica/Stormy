package com.bctecnica.stormy.weather;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

// Pops up a dialog box if there is a error
public class AlertDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Oh dear")
                .setMessage("an error has occurred")
                .setPositiveButton("ok", null);

        return builder.create();
    }
}
