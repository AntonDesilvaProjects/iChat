package com.ichat.service;

import java.util.ArrayList;
import java.util.List;

public class MessageListenerService extends Service {

    private List<MessageListener> messageListeners;

    public MessageListenerService() {
        this.messageListeners = new ArrayList<>();
    }

    @Override
    public Services key() {
        return Services.MESSAGE_LISTENER;
    }

    public void onMessageReceived(Message<String> message) {
        notifyListeners(message);
    }

    public void addListener(MessageListener messageListener) {
        messageListeners.add(messageListener);
    }

    public void removeListener(MessageListener messageListener) {
        messageListeners.remove(messageListener);
    }

    public void notifyListeners(Message message) {
        messageListeners.forEach(c -> c.onMessage(message));
    }
}
