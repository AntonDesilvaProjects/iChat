package com.ichat.service;

import com.ichat.component.MessageBroker;
import com.ichat.controller.ApplicationController;

public class ApplicationService extends Service {

    public static String USERNAME;
    public static MessageBroker MESSAGE_BROKER;
    private ApplicationController applicationController;

    @Override
    public Services key() {
        return Services.APPLICATION_SERVICE;
    }

    public ApplicationController getApplicationController() {
        return applicationController;
    }

    public ApplicationService setApplicationController(ApplicationController applicationController) {
        this.applicationController = applicationController;
        return this;
    }
}
