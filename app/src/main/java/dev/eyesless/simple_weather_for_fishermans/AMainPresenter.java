package dev.eyesless.simple_weather_for_fishermans;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import dev.eyesless.simple_weather_for_fishermans.fragments.CentralFragmentPresenter;

class AMainPresenter {

    private final AMainIntwerface aMainIntwerface;
    private SharedPreferences sharedpref;
    private SharedPreferences.Editor editor;
    private final static String FIRSTLOUNCHPREF = "firstlounchpref";
    private final static String SAVEDBOOLEAN = "savedbooleanr";
    private Context context;


    AMainPresenter(AMainIntwerface aMainIntwerface) {
        this.aMainIntwerface = aMainIntwerface;
    }

     void setmenuid(int itemId) {

         aMainIntwerface.toastmaker(String.valueOf(itemId));
    }

    void isFirstLounch() {

        if (!getFirstLounch()) {
            //first call method to show alert "about"
            aMainIntwerface.showDialogAbout();
            //if getFirsLounch returns true means its first lounch
            //second add value in shared prefs
            sharedpref = context.getSharedPreferences(FIRSTLOUNCHPREF, Context.MODE_PRIVATE);
            editor = sharedpref.edit();
            editor.putBoolean(SAVEDBOOLEAN, true);
            editor.apply();

        }
    }

    private boolean getFirstLounch(){
        sharedpref = context.getSharedPreferences(FIRSTLOUNCHPREF, Context.MODE_PRIVATE);
        return sharedpref.getBoolean(SAVEDBOOLEAN, false);
    }

    void setActivity(AMainActivity activity){
        this.context = activity.getApplication().getApplicationContext();
    }

    void setNullToActivity(){
        this.context = null;
    }
}
