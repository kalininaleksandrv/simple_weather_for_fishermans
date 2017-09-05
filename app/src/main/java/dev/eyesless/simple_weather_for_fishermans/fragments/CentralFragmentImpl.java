package dev.eyesless.simple_weather_for_fishermans.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.util.List;

import dev.eyesless.simple_weather_for_fishermans.AMainActivity;
import dev.eyesless.simple_weather_for_fishermans.R;
import dev.eyesless.simple_weather_for_fishermans.weather_response_classes.Datum;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class CentralFragmentImpl extends Fragment implements CentralFragmentInterface {

    private static final int INFLATED_VIEW = R.layout.fragment_central;
    private View parentview;
    private TextView cf_coordoutput;
    private TextView cf_defoultloc;
    private TextView cf_txttochange;
    private TextView cf_trytoload;
    private ImageButton cf_imagebutton_find;
    CentralFragmentPresenter cfpresenter;
    private AMainActivity mActivity;
    private RecyclerView cf_recycler;
    private RVadapter adapter;
    private LoaderManager mLoader;
    final static String CURRENT_LOC = "currentloc";
    private String currentcocation;


    public CentralFragmentImpl() {

            cfpresenter = new CentralFragmentPresenter(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (AMainActivity)getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            setCurrentcocation(savedInstanceState.getString(CURRENT_LOC));
            Log.e("MY_TAG", "restoring defoult loc " + savedInstanceState.getString(CURRENT_LOC));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(INFLATED_VIEW, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLoader = getLoaderManager();
    }

    @Override
    public void onStart() {
        super.onStart();
        activitysetter(mActivity, mLoader);
        this.parentview = getView();
        inititems ();
        cf_recycler.setVisibility(View.INVISIBLE);
        setDefoultLoc();
        startSearch();
        cf_imagebutton_find.setOnClickListener(new cfIBtnOnClickListner());
        recyclerparamsinit();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    //set extended params to recycler view
    private void recyclerparamsinit() {
        cf_recycler.setHasFixedSize(true);
        cf_recycler.setLayoutManager(new LinearLayoutManager(this.getContext()));
        adapter = new RVadapter(cfpresenter.getTempAdapterList());
        cf_recycler.setAdapter(adapter);
    }

    private void inititems() {
        cf_defoultloc = (TextView) parentview.findViewById(R.id.txt_defaults);
        cf_coordoutput = (TextView) parentview.findViewById(R.id.txt_coordinates);
        cf_txttochange = (TextView) parentview.findViewById(R.id.txt_to_change);
        cf_trytoload = (TextView) parentview.findViewById(R.id.try_to_load_data);
        cf_imagebutton_find = (ImageButton) parentview.findViewById(R.id.btn_img_find_coords);
        cf_recycler = (RecyclerView) parentview.findViewById(R.id.recycler_view_cf);
    }

    public void setDefoultLoc() {

        if (cfpresenter.getAutocompleeted() == null){
            if (getCurrentcocation() == null){setCurrentcocation(CentralFragmentPresenter.DEFOULT_LOC);}
        } else {setCurrentcocation (cfpresenter.getAutocompleeted());}
            cf_defoultloc.setText(getCurrentcocation());
    }

    private void startSearch() {
        cfpresenter.startSearch(false);
    }

    public void activitysetter (AMainActivity aMainActivity, LoaderManager loadmmngr){
        cfpresenter.setActivity (aMainActivity);
        cfpresenter.setLoadManager (loadmmngr);
    }

    @Override
    public void setCoords(String s) {
        cf_coordoutput.setText(s);
    }

    private class cfIBtnOnClickListner implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            startActivityFromPresenter();
        }
    }

    @Override
    public void startActivityFromPresenter() {
        cfpresenter.startActivity(this);
    }

    //result of autocompleet transfering to Central Fragment
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CentralFragmentPresenter.PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                Place place = PlaceAutocomplete.getPlace(mActivity, data);

                cfpresenter.setAutocompleted(place.getAddress().toString());
                Log.e("MY_TAG", place.getAddress().toString());
                cfpresenter.startSearch(true);
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

    @Override
    public void setLocUnavaliable () {
        String unavaliable = getResources().getString(R.string.locunavaliable);
        String noinet = getResources().getString(R.string.nonetworcconnection);
        cf_txttochange.setText(unavaliable);
        mActivity.toastmaker(unavaliable+noinet);
    }

    @Override
    public void adapterrefresh(List<Datum> mylist, boolean isdatanew) {
        Log.e("MY_TAG", "refreshing adapter on view " + mylist.get(0).getSummary());
        adapter = new RVadapter(mylist);
        cf_recycler.setAdapter(adapter);
        cf_recycler.setVisibility(View.VISIBLE);
        cf_trytoload.setVisibility(View.INVISIBLE);
        if (!isdatanew){mActivity.toastmaker(getString(R.string.nonewdata));}
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (cf_defoultloc != null) {
            outState.putString(CURRENT_LOC, getCurrentcocation());
        }
    }

    //getters and setters
    public String getCurrentcocation() {
        return currentcocation;
    }

    public void setCurrentcocation(String currentcocation) {
        this.currentcocation = currentcocation;
    }
}
