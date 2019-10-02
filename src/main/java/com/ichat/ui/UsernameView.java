package com.ichat.ui;

import com.ichat.controller.ApplicationController;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class UsernameView extends HBox {

    private ApplicationController applicationController;

    public UsernameView(ApplicationController applicationController) {
        super();
        this.applicationController = applicationController;
        initView();
    }

    public void initView() {
        //message box
        TextField txtMessage = new TextField();
        txtMessage.setId("txtUsername");
        txtMessage.setPromptText("Enter USERNAME");
        txtMessage.setPrefWidth(175);
        txtMessage.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");

        //message button
        Button btnSubmit = new Button("OK");
        btnSubmit.setOnMouseClicked(e -> applicationController.handleOnClick());
        btnSubmit.setOnKeyPressed(k -> applicationController.handleOnClick());

        this.setPrefWidth(300);
        this.setPrefHeight(35);
        this.setSpacing(10);
        this.setPadding(new Insets(35));
        this.getChildren().addAll(txtMessage, btnSubmit);
    }
}
