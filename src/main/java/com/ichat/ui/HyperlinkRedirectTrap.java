package com.ichat.ui;

import com.ichat.common.Constants;
import com.ichat.common.Headers;
import com.ichat.service.Message;
import com.ichat.service.MessagePublisherService;
import com.ichat.service.Service;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.web.WebView;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLAnchorElement;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Handles anchor tags redirect event in the WebView component
 * */
public class HyperlinkRedirectTrap implements ChangeListener<Worker.State>, EventListener {

    private final WebView webView;
    private final String ANCHOR = "a";
    private final String CLICK_EVENT = "click";

    private MessagePublisherService messagePublisherService;

    public HyperlinkRedirectTrap(final WebView webView) {
        this.webView = webView;
        this.messagePublisherService = (MessagePublisherService) ChatClientApplication.SERVICES.get(Service.Services.MESSAGE_PUBLISHER);
    }

    @Override
    public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {
        if (Worker.State.SUCCEEDED.equals(newValue)) {
            Document document = webView.getEngine().getDocument();
            NodeList anchorTags = document.getElementsByTagName(ANCHOR);
            Node anchorTag;
            EventTarget target;
            for(int i=0; i < anchorTags.getLength(); i++) {
                anchorTag = anchorTags.item(i);
                target = (EventTarget) anchorTag;
                target.addEventListener(CLICK_EVENT, this, false);
            }
        }
    }

    @Override
    public void handleEvent(Event event) {
        if (event.getCurrentTarget() instanceof HTMLAnchorElement) {
            HTMLAnchorElement anchorElement = (HTMLAnchorElement) event.getCurrentTarget();
            String href = anchorElement.getHref();
            String className  = anchorElement.getClassName();
            if (Constants.FILE_DOWNLOAD_CLASSNAME.equals(className)) {
                //send message to server to download the specified file
                //the href will contain the unique file name
                //the text of the anchor tag will be the real file name
                String nonUniqueFileName = anchorElement.getTextContent();
                Message<String> fileDownloadRequest = new Message<>();
                fileDownloadRequest.getHeaders().put(Headers.CONTENT_TYPE, Headers.ContentType.TEXT);
                fileDownloadRequest.getHeaders().put(Headers.FILE_DOWNLOAD, href);
                fileDownloadRequest.setBody(nonUniqueFileName); //we will put the real file name as the body
                messagePublisherService.sendMessage(fileDownloadRequest);
            } else {
                if (Desktop.isDesktopSupported()) {
                    openUrlInSystemBrowser(href);
                } else {
                    System.out.println("Web browsers are not supported on this Operating System!");
                }
            }
            event.preventDefault();
        }
    }

    private void openUrlInSystemBrowser(String url) {
        try {
            URI uri = new URI(url);
            Desktop.getDesktop().browse(uri);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
