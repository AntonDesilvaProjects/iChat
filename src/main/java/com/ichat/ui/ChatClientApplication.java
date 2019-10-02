package com.ichat.ui;

import com.ichat.controller.ApplicationController;
import com.ichat.service.ApplicationService;
import com.ichat.service.MessageListenerService;
import com.ichat.service.MessagePublisherService;
import com.ichat.service.Service;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ChatClientApplication extends Application {

    public static Map<Service.Services, Service> SERVICES;

    public static void main(String[] args) {
        initServices();
        launch(args);
    }
    public void start(Stage primaryStage) throws Exception {
        ApplicationController applicationController = new ApplicationController(primaryStage);
        primaryStage.setTitle("iChat");
        primaryStage.setHeight(125);
        primaryStage.setWidth(300);
        primaryStage.setScene(new Scene(new UsernameView(applicationController)));
        primaryStage.setResizable(Boolean.FALSE);
        primaryStage.show();

        ApplicationService applicationService = (ApplicationService) SERVICES.get(Service.Services.APPLICATION_SERVICE);
        applicationService.setApplicationController(applicationController);
    }
    /**
     *  All the services are singletons so initiate them here
     * */
    private static void initServices() {
        Map<Service.Services, Service> temp = new HashMap<>();
        MessagePublisherService publisherService = new MessagePublisherService();
        MessageListenerService listenerService = new MessageListenerService();
        ApplicationService applicationService = new ApplicationService();

        temp.put(publisherService.key(), publisherService);
        temp.put(listenerService.key(), listenerService);
        temp.put(applicationService.key(), applicationService);

        SERVICES = Collections.unmodifiableMap(temp);
    }
}
