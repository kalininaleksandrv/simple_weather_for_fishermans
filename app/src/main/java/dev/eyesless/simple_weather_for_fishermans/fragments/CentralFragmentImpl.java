package dev.eyesless.simple_weather_for_fishermans.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.tasks.Task;

import java.util.List;

import dev.eyesless.simple_weather_for_fishermans.AMainActivity;
import dev.eyesless.simple_weather_for_fishermans.R;
import dev.eyesless.simple_weather_for_fishermans.weather_response_classes.Datum;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class CentralFragmentImpl extends Fragment implements CentralFragmentInterface, SwipeRefreshLayout.OnRefreshListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int INFLATED_VIEW = R.layout.fragment_central;
    private View parentview;
    private TextView cf_coordoutput;
    private TextView cf_defoultloc;
    private TextView cf_txttochange;
    private TextView cf_trytoload;
    private ImageButton cf_imagebutton_find;
    private ProgressBar cf_progress;
    private SwipeRefreshLayout cf_swipe;
    private final CentralFragmentPresenter cfpresenter;
    private AMainActivity mActivity;
    private RecyclerView cf_recycler;
    private RVadapter adapter;
    private LoaderManager mLoader;
    private final static String CURRENT_LOC = "currentloc";
    private String currentcocation;
    private FloatingActionButton cf_floatingbutton;


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
        cf_progress.setVisibility(View.VISIBLE);
        setDefoultLoc();
        startSearch();
        cf_defoultloc.setOnClickListener(new cfIBtnOnClickListner());
        cf_imagebutton_find.setOnClickListener(new cfIBtnOnClickListner());
        cf_floatingbutton.setOnClickListener(new cfIBtnOnClickListner());
        recyclerparamsinit();
        cf_swipe.setOnRefreshListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    //set extended params to recycler view and swipe refresher
    private void recyclerparamsinit() {



        cf_recycler.setHasFixedSize(true);
        cf_recycler.setBackgroundColor(0xFF673AB7);
        cf_recycler.setLayoutManager(new LinearLayoutManager(this.getContext()));
        adapter = new RVadapter(cfpresenter.getTempAdapterList(), getContext());
        cf_recycler.setAdapter(adapter);

        //separate cards on recycle view
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(cf_recycler.getContext(), DividerItemDecoration.VERTICAL);
        try {
            mDividerItemDecoration.setDrawable(getContext().getDrawable(R.drawable.divider_gradiented));
        } catch (Exception e) {
            e.printStackTrace();
        }
        cf_recycler.addItemDecoration(mDividerItemDecoration);

        //set color scheme to swipe animation
        cf_swipe.setColorSchemeResources(R.color.colorPrimary);
    }

    private void inititems() {
        cf_defoultloc = (TextView) parentview.findViewById(R.id.txt_defaults);
        cf_coordoutput = (TextView) parentview.findViewById(R.id.txt_coordinates);
        cf_txttochange = (TextView) parentview.findViewById(R.id.txt_to_change);
        cf_trytoload = (TextView) parentview.findViewById(R.id.try_to_load_data);
        cf_imagebutton_find = (ImageButton) parentview.findViewById(R.id.btn_img_find_coords);
        cf_recycler = (RecyclerView) parentview.findViewById(R.id.recycler_view_cf);
        cf_progress = (ProgressBar) parentview.findViewById(R.id.progressBar_cf);
        cf_swipe = (SwipeRefreshLayout) parentview.findViewById(R.id.swipe);
        cf_floatingbutton = (FloatingActionButton) parentview.findViewById(R.id.floatingActionButton_cf);
    }

    @Override
    public void setDefoultLoc() {

        if (cfpresenter.getAutocompleeted() == null){
            if (getCurrentcocation() == null){setCurrentcocation(CentralFragmentPresenter.DEFOULT_LOC);}
        } else {setCurrentcocation (cfpresenter.getAutocompleeted());}
            cf_defoultloc.setText(getCurrentcocation());
    }

    private void startSearch() {
        cfpresenter.startSearch(false);
    }

    private void activitysetter(AMainActivity aMainActivity, LoaderManager loadmmngr){
        cfpresenter.setActivity (aMainActivity);
        cfpresenter.setLoadManager (loadmmngr);
    }

    @Override
    public void setCoords(String s) {
        cf_coordoutput.setText(s);
    }

    //when swipe refresh used
    @Override
    public void onRefresh() {
        startSearch();//restart loading info
    }

    private class cfIBtnOnClickListner implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.txt_defaults:
                    startActivityFromPresenter();
                    break;
                case R.id.btn_img_find_coords:
                    cfpresenter.getGpsPermission(CentralFragmentImpl.this);
                    break;
                case R.id.floatingActionButton_cf:
                    startActivityFromPresenter();
                    break;
            }
        }
    }

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
                cfpresenter.startSearch(true);
                cf_progress.setVisibility(View.VISIBLE);
                cf_trytoload.setVisibility(View.VISIBLE);

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(mActivity, data);
                mActivity.toastmaker(getResources().getString(R.string.autocompleeterror));

            } else if (resultCode == RESULT_CANCELED) {
            }
        }

        if (requestCode == CentralFragmentPresenter.GPS_ENABLER_REQUEST_CODE){

            if (resultCode == RESULT_OK){
                cfpresenter.getGpsPermission(CentralFragmentImpl.this);
            } else if (resultCode == RESULT_CANCELED) {
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
    public void adapterrefresh(List<Datum> mylist) {

        if (mylist != null) {
            adapter = new RVadapter(mylist, getContext());
            cf_recycler.setAdapter(adapter);
            cf_recycler.setVisibility(View.VISIBLE);
        }

        else {
            mActivity.toastmaker(mActivity.getString(R.string.nonewdata));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (cf_defoultloc != null) {
            outState.putString(CURRENT_LOC, getCurrentcocation());
        }
    }

    //getters and setters
    private String getCurrentcocation() {
        return currentcocation;
    }

    private void setCurrentcocation(String currentcocation) {
        this.currentcocation = currentcocation;
    }

    @Override
    public void stoprefreashing (){
        cf_progress.setVisibility(View.INVISIBLE);
        cf_trytoload.setVisibility(View.INVISIBLE);
        cf_swipe.setRefreshing(false);
    }

    @Override
    public void getGpsPermission() {
        ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    //here the result of permission request in method getCoordinatesFromGps
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED){cfpresenter.getCoordinatesFromGps();}

        if(grantResults[0] == PackageManager.PERMISSION_DENIED){cfpresenter.informUserAboutGpsUnavaliable();}
    }
}
