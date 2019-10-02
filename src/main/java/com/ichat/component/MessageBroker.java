package com.ichat.component;

import com.ichat.common.Constants;
import com.ichat.common.Headers;
import com.ichat.component.exception.HandshakeFailedException;
import com.ichat.component.exception.ServerConnectionException;
import com.ichat.service.*;
import javafx.application.Platform;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static com.ichat.component.SocketConnectionFactory.*;

public class MessageBroker {

    private BlockingQueue<Message> messageQueue;
    private Socket defaultConnectionSocket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    private final int DEFAULT_MESSAGE_QUEUE_SIZE = 1024;

    private MessageListenerService messageListenerService;

    List<MessageBrokerEventListener> eventListenerList;

    public MessageBroker(MessageListenerService messageListenerService) throws IOException {
        this.messageListenerService = messageListenerService;
        this.eventListenerList = new ArrayList<>();
        initializeComponents();
    }

    private void initializeComponents() throws IOException {
        messageQueue = new ArrayBlockingQueue<Message>(DEFAULT_MESSAGE_QUEUE_SIZE);
        refreshSocketAndDataStreams();
        //initialize the message listener
        CompletableFuture.runAsync(() -> {
           while (true) {
               try {
                   Message<String> message = (Message) inputStream.readObject();
                   Platform.runLater(() -> messageListenerService.onMessageReceived(message));
               } catch (Exception e) {
                   System.out.println("Lost connection to server. Retrying...");
                   retryConnection();
               }
           }
        });
        //initialize the message publisher
        CompletableFuture.runAsync(() -> {
            while (true) {
                try {
                    Message<String> message = (Message<String>) messageQueue.take();
                    outputStream.writeObject(message);
                    outputStream.flush();
                } catch (Exception e) {
                    System.out.println("Lost connection to server. Retrying...");
                    retryConnection();
                }
            }
        });
    }

    private void refreshSocketAndDataStreams() throws IOException {
        if (defaultConnectionSocket != null) {
            defaultConnectionSocket.close();
        }
        defaultConnectionSocket = SocketConnectionFactory.getSocketInstance(ConnectionType.DEFAULT_CONNECTION_SOCKET);
        if (defaultConnectionSocket == null || defaultConnectionSocket.isClosed()) {
            throw new ServerConnectionException("Unable to establish connection to the server!");
        }
        outputStream = new ObjectOutputStream(new BufferedOutputStream(defaultConnectionSocket.getOutputStream()));
        outputStream.flush(); //this is required before attempting to create input stream
        inputStream = new ObjectInputStream(new BufferedInputStream(defaultConnectionSocket.getInputStream()));

        //send a handshake message
        Message<String> message = new Message<>();
        Map<String, String> headers = new HashMap<>();
        headers.put(Headers.USER, ApplicationService.USERNAME);
        headers.put(Headers.CONTENT_TYPE, Headers.ContentType.TEXT);
        headers.put(Headers.HANDSHAKE, "true");
        message.setHeaders(headers);
        try {
            sendMessage(message);
        } catch (InterruptedException e) {
            throw new HandshakeFailedException(e.getMessage());
        }
    }

    private void retryConnection() {
        int secondsUntilNextRetry = 2000;
        boolean notifiedConnectionLostEvent = false;
        while (true) {
            try {
                Thread.sleep(secondsUntilNextRetry);
                refreshSocketAndDataStreams();
                eventListenerList.forEach(MessageBrokerEventListener::onConnected);
                break;
            } catch (ServerConnectionException s) {
                //disable linear backoff to retry connection
                //and try at fixed interval
                //secondsUntilNextRetry *= 2;
                System.out.println("Trying again in " + secondsUntilNextRetry + " ms!");
                if (!notifiedConnectionLostEvent) {
                    eventListenerList.forEach(MessageBrokerEventListener::onConnectionLost);
                    //for now, there is no need to keep pushing
                    //the same event every time
                    notifiedConnectionLostEvent = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(Message m) throws InterruptedException {
        messageQueue.put(m);
    }

    public BlockingQueue<Message> getMessageQueue() {
        return messageQueue;
    }

    public Socket getDefaultConnectionSocket() {
        return defaultConnectionSocket;
    }

    public MessageBroker setDefaultConnectionSocket(Socket defaultConnectionSocket) {
        this.defaultConnectionSocket = defaultConnectionSocket;
        return this;
    }

    public MessageListenerService getMessageListenerService() {
        return messageListenerService;
    }

    public MessageBroker setMessageListenerService(MessageListenerService messageListenerService) {
        this.messageListenerService = messageListenerService;
        return this;
    }

    public void addEventListener(MessageBrokerEventListener listener) {
        this.eventListenerList.add(listener);
    }
}
