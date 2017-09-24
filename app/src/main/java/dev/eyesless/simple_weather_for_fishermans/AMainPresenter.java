package dev.eyesless.simple_weather_for_fishermans;

class AMainPresenter {

    private final AMainIntwerface aMainIntwerface;

    AMainPresenter(AMainIntwerface aMainIntwerface) {
        this.aMainIntwerface = aMainIntwerface;
    }

     void setmenuid(int itemId) {

         aMainIntwerface.toastmaker(String.valueOf(itemId));
    }
}
