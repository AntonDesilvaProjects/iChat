package com.ichat.service;

public abstract class Service {
    public enum Services {
        MESSAGE_PUBLISHER,
        MESSAGE_LISTENER,
        APPLICATION_SERVICE
    }

    public abstract Services key();
}
