package com.ichat.ui;

import com.ichat.controller.MessageBarController;
import com.ichat.controller.MessageViewController;
import com.ichat.service.MessageListenerService;
import com.ichat.service.MessagePublisherService;
import com.ichat.service.Service;
import javafx.geometry.Insets;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class ChatClientView extends BorderPane {

    private MessageBarController messageBarController;

    public ChatClientView(double width, double height) {
        super();
        initView(width, height);
    }

    private void initView(double width, double height) {
        //set up the center region
        VBox centerVBox = new VBox();
        centerVBox.setPrefWidth(this.getWidth());
        centerVBox.setPrefHeight(this.getHeight());
        centerVBox.setPadding(new Insets(5));
        centerVBox.setSpacing(10);

        MessageView messageView = new MessageView(400,400);
        messageView.setController(new MessageViewController((MessageListenerService) ChatClientApplication.SERVICES.get(Service.Services.MESSAGE_LISTENER)));

        MessageBar messageBar = new MessageBar(50, 100);
        messageBar.setController(new MessageBarController((MessagePublisherService) ChatClientApplication.SERVICES.get(Service.Services.MESSAGE_PUBLISHER)));

        centerVBox.getChildren().addAll(messageView.getWebview(), messageBar);

        this.setCenter(centerVBox);

        //set up the bottom region
        StatusBar statusBar = new StatusBar();
        statusBar.setId("statusBar");
        VBox bottomVBox = new VBox();
        bottomVBox.setPadding(new Insets(5));
        Separator separator = new Separator();
        bottomVBox.getChildren().addAll(separator, statusBar);

        this.setBottom(bottomVBox);

        //container configuration
        this.setWidth(width);
        this.setHeight(height);
    }
}
