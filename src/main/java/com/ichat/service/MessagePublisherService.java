package com.ichat.service;

import com.ichat.common.Headers;

import java.net.Socket;
import java.util.List;

public class MessagePublisherService extends Service {

    private Socket defaultConnectionSocket;

    public void sendMessage(String strMsg) {
        Message<String> message = new Message<>(strMsg);
        message.getHeaders().put(Headers.USER, ApplicationService.USERNAME);
        message.getHeaders().put(Headers.CONTENT_TYPE, Headers.ContentType.TEXT);
        try {
            ApplicationService.MESSAGE_BROKER.sendMessage(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(List<ByteArrayFile> files) {
        Message<List<ByteArrayFile>> message = new Message<>(files);
        message.getHeaders().put(Headers.USER, ApplicationService.USERNAME);
        message.getHeaders().put(Headers.CONTENT_TYPE, Headers.ContentType.FILE);
        try {
            ApplicationService.MESSAGE_BROKER.sendMessage(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Message message) {
        try {
            ApplicationService.MESSAGE_BROKER.sendMessage(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Socket getDefaultConnectionSocket() {
        return defaultConnectionSocket;
    }

    public MessagePublisherService setDefaultConnectionSocket(Socket defaultConnectionSocket) {
        this.defaultConnectionSocket = defaultConnectionSocket;
        return this;
    }

    @Override
    public Services key() {
        return Services.MESSAGE_PUBLISHER;
    }
}
