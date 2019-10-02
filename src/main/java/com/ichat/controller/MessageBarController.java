package com.ichat.controller;

import com.ichat.common.Headers;
import com.ichat.service.ApplicationService;
import com.ichat.service.ByteArrayFile;
import com.ichat.service.Message;
import com.ichat.service.MessagePublisherService;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MessageBarController extends Controller {

    private MessagePublisherService messagePublisherService;

    public MessageBarController(MessagePublisherService messagePublisherService) {
        this.messagePublisherService = messagePublisherService;
    }
    //event handlers
    public void btnSendMsgOnClick(MouseEvent mouseEvent) {
        TextArea txtMessageArea = (TextArea) findChildNode("#txtMessage");
        sendMessage(txtMessageArea.getText());
        txtMessageArea.clear();
    }

    public void btnPlusOnClick(MouseEvent mouseEvent) {
        Button btnPlus = (Button) mouseEvent.getSource();
        Stage stage = (Stage) btnPlus.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Files");
        List<File> files = fileChooser.showOpenMultipleDialog(stage);
        sendMessage(files);
    }

    public void txtMessageOnReturn(KeyEvent keyEvent) {
        if (keyEvent.getCode() != KeyCode.ENTER) {
            //any key other than the 'enter' key is pressed
            //send a keystroke message to server
            Message<String> keyStrokeMessage = new Message<>();
            keyStrokeMessage.getHeaders().put(Headers.KEYSTROKE, keyEvent.getCode().toString());
            keyStrokeMessage.getHeaders().put(Headers.CONTENT_TYPE, Headers.ContentType.TEXT);
            keyStrokeMessage.getHeaders().put(Headers.USER, ApplicationService.USERNAME);
            keyStrokeMessage.setBody(keyEvent.getCode().toString());
            messagePublisherService.sendMessage(keyStrokeMessage);
            return;
        }
        keyEvent.consume(); //prevents further propagation of the event
        TextArea txtMessage = (TextArea) keyEvent.getSource();
        sendMessage(txtMessage.getText());
        txtMessage.clear();
    }

    private void sendMessage(Object messagePayload) {
        if (messagePayload instanceof String) {
            String message = messagePayload.toString();
            if (StringUtils.isEmpty(message)) {
                return;
            }
            try {
                messagePublisherService.sendMessage(message);
            } catch (Exception e) {

            }
        } else if (messagePayload instanceof List) {
            List<File> files = (List<File>) messagePayload;
            if (CollectionUtils.isEmpty(files)) {
                return;
            }
            List<ByteArrayFile> fileList = new ArrayList<>();
            for (File f: files) {
                try {
                    byte[] fileBytes = new byte[(int) f.length()];
                    BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(f));
                    inputStream.read(fileBytes, 0, fileBytes.length);
                    ByteArrayFile file = new ByteArrayFile(f.getName(), fileBytes);
                    String s = f.getName();
                    fileList.add(file);
                    inputStream.close();
                } catch (FileNotFoundException e) {
                    System.out.println("File not found!");
                } catch (IOException e) {
                    System.out.println("An error occurred while preparing file for transfer!");
                }
            }
            try {
                messagePublisherService.sendMessage(fileList);
            } catch (Exception e) {

            }
        }
    }
}
