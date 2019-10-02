package com.ichat.controller;

import javafx.scene.Node;

public abstract class Controller {
    private Node boundNode;

    public Node getBoundNode() {
        return boundNode;
    }

    public Controller setBoundNode(Node boundNode) {
        this.boundNode = boundNode;
        return this;
    }

    public Node findChildNode(String idString) {
        return this.getBoundNode().lookup(idString);
    }
}
