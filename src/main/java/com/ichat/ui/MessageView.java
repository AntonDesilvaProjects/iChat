package com.ichat.ui;

import com.ichat.controller.Controller;
import com.ichat.controller.MessageViewController;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.web.WebView;

public class MessageView  {

    private WebView webview;
    private MessageViewController messageViewController;

    public MessageView(double width, double height) {
        webview = new WebView();
        webview.setPrefHeight(height);
        webview.getEngine().getLoadWorker().stateProperty().addListener(new HyperlinkRedirectTrap(webview));
        webview.getEngine().getLoadWorker().stateProperty().addListener((ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue)-> {
            if (Worker.State.SUCCEEDED.equals(newValue)) {
                //automatically scroll to the bottom of the chat view
                webview.getEngine().executeScript("window.scrollTo(0, document.body.scrollHeight)");
            }
        });
        webview.getEngine().setUserStyleSheetLocation(getClass().getClassLoader().getResource("style.css").toString());
    }

    public void addNewMessage(String message) {
        throw new UnsupportedOperationException("Not Yet implemented!");
    }

    public WebView getWebview() {
        return webview;
    }

    public void setController(Controller controller) {
        messageViewController = (MessageViewController) controller;
        messageViewController.setBoundNode(getWebview());
    }

}
