package com.ichat.service;

public interface MessageBrokerEventListener {
    void onConnected();
    void onConnectionLost();
}
