package com.ichat.ui;

import com.ichat.controller.Controller;
import com.ichat.controller.MessageBarController;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

public class MessageBar extends HBox {

    private final static double NODE_HEIGHT = 45;
    private MessageBarController controller;

    public MessageBar(double width, double height) {
        super();
        initView(width, height);
    }

    private void initView(double width, double height) {
        //message box
        TextArea txtMessage = new TextArea();
        txtMessage.setId("txtMessage");
        //this is a hack to force the height/weight to be fixed while increasing
        //font size - otherwise, textarea will keep growing along with font size
        txtMessage.setMaxHeight(35);
        txtMessage.setMaxWidth(500);
        txtMessage.setMinHeight(35);
        txtMessage.setMinWidth(500);
        txtMessage.setPrefWidth(USE_COMPUTED_SIZE);
        txtMessage.setPrefHeight(USE_COMPUTED_SIZE);
        txtMessage.setFont(new Font(14));
        //txtMessage.textFormatterProperty()
        txtMessage.setWrapText(Boolean.TRUE);
        txtMessage.setOnKeyPressed(e -> controller.txtMessageOnReturn(e));

        //message button
        Button btnMessage = new Button("Send");
        btnMessage.setId("btnMessage");
        btnMessage.setPrefHeight(NODE_HEIGHT);
        btnMessage.setOnMouseClicked(e -> controller.btnSendMsgOnClick(e));

        //additional options button
        Button btnPlus = new Button("+");
        btnPlus.setId("btnPlus");
        btnPlus.setPrefHeight(NODE_HEIGHT);

        btnPlus.setOnMouseClicked(e -> controller.btnPlusOnClick(e));

        //container configurations
        this.setSpacing(5);
        this.setWidth(width);
        this.setHeight(height);
        this.getChildren().addAll(txtMessage, btnMessage, btnPlus);
    }

    public void setController(Controller controller) {
        if (!(controller instanceof MessageBarController)) {
            throw new IllegalArgumentException("Invalid controller argument!");
        }
        this.controller = (MessageBarController) controller;
        this.controller.setBoundNode(this);
    }
}
