package dev.eyesless.simple_weather_for_fishermans.fragments;

class CentralFragmentPresenter {

    private CentralFragmentInterface cfinterface;

    CentralFragmentPresenter(CentralFragmentInterface cfi) {

        this.cfinterface = cfi;

    }


     void isBtnPressed() {

        String s = cfinterface.getPlace();
        cfinterface.setCoords(s);

    }

}
