package dev.eyesless.simple_weather_for_fishermans.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;

import dev.eyesless.simple_weather_for_fishermans.AMainActivity;
import dev.eyesless.simple_weather_for_fishermans.R;
import dev.eyesless.simple_weather_for_fishermans.weather_response_classes.Image;

public class CentralFragmentImpl extends Fragment implements CentralFragmentInterface {

    private static final int INFLATED_VIEW = R.layout.fragment_central;
    private View parentview;
    private Button cf_button_find;
    private TextView cf_coordoutput;
    private TextView cf_defoultloc;
    private ImageButton cf_imagebutton_find;
    CentralFragmentPresenter cfpresenter;
    private AMainActivity mActivity;

    public CentralFragmentImpl() {


        cfpresenter = new CentralFragmentPresenter(this);

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

        cf_button_find.setOnClickListener(new cfOnClickListner());

        cf_imagebutton_find.setOnClickListener(new cfIBtnOnClickListner());

    }

    private void inititems() {

        cf_defoultloc = (TextView) parentview.findViewById(R.id.txt_defaults);
        cf_button_find = (Button) parentview.findViewById(R.id.btn_find_coords);
        cf_coordoutput = (TextView) parentview.findViewById(R.id.txt_coordinates);
        cf_imagebutton_find = (ImageButton) parentview.findViewById(R.id.btn_img_find_coords);

    }



    public void setDefoultLoc() {

       cf_defoultloc.setText(cfpresenter.getDefoultLoc());
    }

    @Override
    public void setCoords(String s) {

        cf_coordoutput.setText(s);
    }

    public void isBtnPressed() {

        cfpresenter.isBtnPressed();

    }

    public void isImgBtnPressed() {

        cfpresenter.isImgBtnPressed();

    }

    public void activitysetter (AMainActivity aMainActivity){

        cfpresenter.setActivity (aMainActivity);

    }

    private class cfOnClickListner implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            isBtnPressed();
        }
    }

    private class cfIBtnOnClickListner implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            isImgBtnPressed();
        }
    }
}
