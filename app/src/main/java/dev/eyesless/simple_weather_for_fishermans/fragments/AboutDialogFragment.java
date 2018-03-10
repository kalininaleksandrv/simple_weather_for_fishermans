package dev.eyesless.simple_weather_for_fishermans.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import dev.eyesless.simple_weather_for_fishermans.AMainIntwerface;
import dev.eyesless.simple_weather_for_fishermans.R;


public class AboutDialogFragment extends DialogFragment {

    private AMainIntwerface mActivityCallback;

    public AboutDialogFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_alert, null);

        //add clicklistnet to hold "got it" button pressen and show onboarding page
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mActivityCallback.showOnboarding();

            }
        };

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
        builder.setPositiveButton(R.string.gotit, dialogClickListener);
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AMainIntwerface) {
            mActivityCallback = (AMainIntwerface) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement AMainIntwerface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivityCallback = null;
    }


}
