package com.ichat.ui;

import com.ichat.common.Constants;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.CompletableFuture;

public class StatusBar extends HBox {

    private Label lblCurrentStatus;
    private Label lblCurrentInfo;

    public StatusBar() {
        super();
        initView();
    }
    public void initView() {
        //label to display current status
        lblCurrentStatus = new Label("Connected");

        //label and progress bar to display any transient status information
        lblCurrentInfo = new Label();
        lblCurrentInfo.setVisible(Boolean.TRUE);

        //empty space
        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);

        //container configurations
        this.setSpacing(5);
        this.getChildren().addAll(lblCurrentStatus, region, lblCurrentInfo);
    }

    public void updateStatusText(String status) {
        if (StringUtils.isNotEmpty(status)) {
            lblCurrentStatus.setText(status);
        }
    }

    public void updateCurrentInfo(String info) {
        if (StringUtils.isNotEmpty(info)) {
            lblCurrentInfo.setText(info);
            //this is bit of hacky/flawed way to simulate a label component whose
            //value gets auto-cleared some fixed time after its last value update
            //for this particular scenario, this is OK
            //problem here is that if the thread is already asleep, then when the next
            //input is received, then label will get cleared anyways - there are ways to
            //handle this but will require more effort
            CompletableFuture.runAsync(() -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(()->lblCurrentInfo.setText(""));
            }, Constants.THREAD_POOL);
        }
    }
}
