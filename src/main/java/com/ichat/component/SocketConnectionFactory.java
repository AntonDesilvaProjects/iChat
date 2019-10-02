package com.ichat.component;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketConnectionFactory {
    private static Socket defaultConnectionSocket;

    public enum ConnectionType {
        DEFAULT_CONNECTION_SOCKET("192.168.10.120", 3021);

        private String host;
        private int port;

        ConnectionType(String host, int port) {
            this.host = host;
            this.port = port;
        }

        public String getHost() {
            return host;
        }

        public ConnectionType setHost(String host) {
            this.host = host;
            return this;
        }

        public int getPort() {
            return port;
        }

        public ConnectionType setPort(int port) {
            this.port = port;
            return this;
        }
    }

    private SocketConnectionFactory() {
    }

    public static Socket getSocketInstance(ConnectionType connectionType) {
        Socket _socket = null;
        if (connectionType == ConnectionType.DEFAULT_CONNECTION_SOCKET) {
            if (defaultConnectionSocket == null || defaultConnectionSocket.isClosed()) {
                try {
                    defaultConnectionSocket = new Socket(connectionType.getHost(), connectionType.getPort());
                } catch (UnknownHostException u) {
                    System.out.println("Host/port unknown!");
                } catch (IOException e) {
                    System.out.println("Unable to connect to ther server...");
                }
            }
            _socket = defaultConnectionSocket;
        }
        return _socket;
    }
}
