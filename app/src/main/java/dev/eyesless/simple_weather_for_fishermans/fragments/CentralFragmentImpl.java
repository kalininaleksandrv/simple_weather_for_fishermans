package dev.eyesless.simple_weather_for_fishermans.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import dev.eyesless.simple_weather_for_fishermans.AMainActivity;
import dev.eyesless.simple_weather_for_fishermans.R;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class CentralFragmentImpl extends Fragment implements CentralFragmentInterface {

    private static final int INFLATED_VIEW = R.layout.fragment_central;
    private View parentview;
    private TextView cf_coordoutput;
    private TextView cf_defoultloc;
    private ImageButton cf_imagebutton_find;
    CentralFragmentPresenter cfpresenter;
    private AMainActivity mActivity;

        public CentralFragmentImpl() {

        cfpresenter = new CentralFragmentPresenter(this, getContext());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(INFLATED_VIEW, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (AMainActivity)getActivity();
        activitysetter(mActivity);
    }

    @Override
    public void onStart() {
        super.onStart();
        this.parentview = getView();

        inititems ();

        setDefoultLoc();

        cfpresenter.startSearch();

        cf_imagebutton_find.setOnClickListener(new cfIBtnOnClickListner());
    }

    @Override
    public void setCoords(String s) {
        cf_coordoutput.setText(s);
    }

    @Override
    public void startActivityFromPresenter() {
        cfpresenter.startActivity(this);
    }


    private class cfIBtnOnClickListner implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            isImgBtnPressed();
        }
    }

    //result of autocompleet transfering to Central Fragment
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CentralFragmentPresenter.PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                Place place = PlaceAutocomplete.getPlace(mActivity, data);

                cfpresenter.setAutocompleted(place.getAddress().toString());

                Log.e("MY_TAG", place.getAddress().toString());

                cfpresenter.startSearch();
                setDefoultLoc();

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(mActivity, data);
                mActivity.toastmaker(getResources().getString(R.string.autocompleeterror));
                Log.e("MY_TAG", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                Log.e("MY_TAG", "operation canceled by user");
            }
        }
    }

    private void inititems() {
        cf_defoultloc = (TextView) parentview.findViewById(R.id.txt_defaults);
        cf_coordoutput = (TextView) parentview.findViewById(R.id.txt_coordinates);
        cf_imagebutton_find = (ImageButton) parentview.findViewById(R.id.btn_img_find_coords);
    }

    public void setDefoultLoc() {
       cf_defoultloc.setText(cfpresenter.getDefoultLoc());
    }

    public void isImgBtnPressed() {
        startActivityFromPresenter();

    }

    public void activitysetter (AMainActivity aMainActivity){
        cfpresenter.setActivity (aMainActivity);
    }


}
