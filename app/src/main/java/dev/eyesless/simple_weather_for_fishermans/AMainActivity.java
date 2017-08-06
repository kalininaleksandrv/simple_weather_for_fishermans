package dev.eyesless.simple_weather_for_fishermans;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AMainActivity extends AppCompatActivity{

    private static final int LAYOUT = R.layout.activity_amain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
    }
}
