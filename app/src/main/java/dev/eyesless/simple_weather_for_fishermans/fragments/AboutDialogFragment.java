package dev.eyesless.simple_weather_for_fishermans.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
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
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_alert, null); //todo Warning:(29, 61) Avoid passing `null` as the view root (needed to resolve layout parameters on the inflated layout's root element)
        if (view != null) {
            TextView alertTitle = (TextView)view.findViewById(R.id.alert_title);
            alertTitle.setText(getActivity().getResources().getString(R.string.app_name));
            TextView alertText = (TextView)view.findViewById(R.id.alert_text);
            alertText.setText(getActivity().getResources().getString(R.string.termsofusage));
            builder.setView(view);
        } else {
            builder.setTitle(getActivity().getResources().getString(R.string.app_name));
            builder.setMessage(getActivity().getResources().getString(R.string.pleasereadabout));
        }
        builder.setPositiveButton(R.string.gotit, null);
        return builder.create();
    }

}
