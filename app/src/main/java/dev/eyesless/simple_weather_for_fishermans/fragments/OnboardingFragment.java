package dev.eyesless.simple_weather_for_fishermans.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dev.eyesless.simple_weather_for_fishermans.AMainIntwerface;
import dev.eyesless.simple_weather_for_fishermans.R;


public class OnboardingFragment extends Fragment {

    private AMainIntwerface mActivityCallback;
    private View parentview;


    public OnboardingFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_onboarding, container, false);
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
    public void onStart() {
        super.onStart();
        this.parentview = getView();




        //hide onboarding if user click GOT IT

        ConstraintLayout constraintLayout;

        if (parentview != null) {
            constraintLayout = (ConstraintLayout) parentview.findViewById(R.id.constraint_onboarding);
            constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //remoove current fragment from backstack to correct work backpressure app exit (additional bask-click needed if not)
                    FragmentManager mymanager = getFragmentManager();
                    mymanager.popBackStack();
                    mActivityCallback.hideonboarding();
                }
            });
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mActivityCallback = null;
    }
}
