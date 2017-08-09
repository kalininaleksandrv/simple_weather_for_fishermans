package dev.eyesless.simple_weather_for_fishermans;

class AMainPresenter {

    private AMainIntwerface aMainIntwerface;

    private int MenuId;

    AMainPresenter(AMainIntwerface aMainIntwerface) {
        this.aMainIntwerface = aMainIntwerface;
    }

     void setmenuid(int itemId) {

        this.MenuId = itemId;
        aMainIntwerface.toastmaker(String.valueOf(itemId));
    }
}
