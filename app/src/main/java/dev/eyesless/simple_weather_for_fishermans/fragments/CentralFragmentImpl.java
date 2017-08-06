package dev.eyesless.simple_weather_for_fishermans.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import dev.eyesless.simple_weather_for_fishermans.R;

public class CentralFragmentImpl extends Fragment implements CentralFragmentInterface {

    private static final int INFLATED_VIEW = R.layout.fragment_central;
    private View parentview;
    private EditText cf_edittext;
    private Button cf_button_find;
    private TextView cf_coordoutput;
    CentralFragmentPresenter cfpresenter;

    public CentralFragmentImpl() {

        cfpresenter = new CentralFragmentPresenter(this);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(INFLATED_VIEW, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        this.parentview = getView();

        inititems ();

        cf_button_find.setOnClickListener(new cfOnClickListner());

    }

    private void inititems() {

        cf_edittext = (EditText) parentview.findViewById(R.id.edtxt_founded_city);
        cf_button_find = (Button) parentview.findViewById(R.id.btn_find_coords);
        cf_coordoutput = (TextView) parentview.findViewById(R.id.txt_coordinates);

    }

    @Override
    public String getPlace() {

        return cf_edittext.getText().toString();
    }

    @Override
    public void setCoords(String s) {

        cf_coordoutput.setText(s);
    }

    @Override
    public void isBtnPressed() {

        cfpresenter.isBtnPressed();

    }

    private class cfOnClickListner implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            isBtnPressed();
        }
    }
}
