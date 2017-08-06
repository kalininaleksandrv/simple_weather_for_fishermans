package dev.eyesless.simple_weather_for_fishermans.fragments;

class CentralFragmentPresenter {

    private CentralFragmentInterface cfinterface;

    CentralFragmentPresenter(CentralFragmentInterface cfi) {

        this.cfinterface = cfi;

    }


    protected void isBtnPressed() {

        String s = cfinterface.getPlace();
        cfinterface.setCoords(s);

    }

}
