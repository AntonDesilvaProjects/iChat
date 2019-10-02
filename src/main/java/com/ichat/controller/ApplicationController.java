package com.ichat.controller;

import com.ichat.component.MessageBroker;
import com.ichat.component.exception.ServerConnectionException;
import com.ichat.service.ApplicationService;
import com.ichat.service.MessageBrokerEventListener;
import com.ichat.service.MessageListenerService;
import com.ichat.service.Service;
import com.ichat.ui.ChatClientApplication;
import com.ichat.ui.ChatClientView;
import com.ichat.ui.StatusBar;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class ApplicationController extends Controller implements MessageBrokerEventListener {

    private Stage applicationStage;
    private Stage chatViewStage;
    private final double WIDTH = 600;
    private final double HEIGHT = 500;

    public ApplicationController(Stage applicationStage) {
        this.applicationStage = applicationStage;
    }

    public void handleOnClick() {
        TextField txtUserName = (TextField) findChildNode("#txtUsername");
        String value = txtUserName.getText();
        if (!StringUtils.isEmpty(value)) {
            //set the USERNAME
            ApplicationService.USERNAME = value;
            try {
                ApplicationService.MESSAGE_BROKER = new MessageBroker((MessageListenerService) ChatClientApplication.SERVICES.get(Service.Services.MESSAGE_LISTENER));
                ApplicationService.MESSAGE_BROKER.addEventListener(this);
                //create the main chat client view
                chatViewStage = new Stage();
                chatViewStage.setTitle("iChat");
                chatViewStage.setWidth(WIDTH);
                chatViewStage.setHeight(HEIGHT);
                chatViewStage.setResizable(false);
                chatViewStage.setScene(new Scene(new ChatClientView(WIDTH, HEIGHT)));
                chatViewStage.show();

                applicationStage.hide();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ServerConnectionException s) {
                Alert alert = new Alert(Alert.AlertType.NONE, "Unable to connect to server. Please try again later.", ButtonType.OK);
                alert.showAndWait();
            }
        }
    }

    @Override
    public Node findChildNode(String idString) {
        return this.getApplicationStage().getScene().lookup(idString);
    }

    public Stage getApplicationStage() {
        return applicationStage;
    }

    public ApplicationController setApplicationStage(Stage applicationStage) {
        this.applicationStage = applicationStage;
        return this;
    }

    public boolean isFocused() {
        return chatViewStage.isFocused();
    }

    public Stage getChatViewStage() {
        return chatViewStage;
    }

    @Override
    public void onConnected() {
        updateViewBasedOnConnectionStatus(true);
    }

    @Override
    public void onConnectionLost() {
        updateViewBasedOnConnectionStatus(false);
    }

    private void updateViewBasedOnConnectionStatus(boolean isConnected) {
        Scene chatViewScene = getChatViewStage().getScene();
        StatusBar statusBar = (StatusBar) chatViewScene.lookup("#statusBar");
        Button btnMessage = (Button) chatViewScene.lookup("#btnMessage");
        Button btnPlus = (Button) chatViewScene.lookup("#btnPlus");

        Platform.runLater(() -> {
            String statusText = "Connected";
            boolean isDisabled = !isConnected;
            if (!isConnected) {
              statusText = "Connection lost. Attempting to reconnect...";
            }
            statusBar.updateStatusText(statusText);
            btnMessage.setDisable(isDisabled);
            btnPlus.setDisable(isDisabled);
        });
    }
}
