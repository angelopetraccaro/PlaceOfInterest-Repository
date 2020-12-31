package com.gmail.petraccaro.angelo.placesofinterest.Controllers;

public interface Contract {
    void OnSuccess(Object obj);
    void OnError(String message);
}
