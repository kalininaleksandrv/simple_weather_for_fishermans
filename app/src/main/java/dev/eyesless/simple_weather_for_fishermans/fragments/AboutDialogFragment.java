package dev.eyesless.simple_weather_for_fishermans.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import dev.eyesless.simple_weather_for_fishermans.R;


public class AboutDialogFragment extends DialogFragment {


    public AboutDialogFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(messageGetter());
        builder.setPositiveButton(R.string.gotit, null);
        return builder.create();
    }

   private String messageGetter(){
        return getActivity().getResources().getString(R.string.app_name);
    }
}
