package com.ichat.controller;

import com.ichat.common.Constants;
import com.ichat.common.Headers;
import com.ichat.common.Utils;
import com.ichat.service.*;
import com.ichat.ui.ChatClientApplication;
import com.ichat.ui.StatusBar;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.io.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class MessageViewController extends Controller implements MessageListener {

    private MessageListenerService messageListenerService;
    private StringBuilder messageHtmlStr;
    private ApplicationService applicationService;
    private Stage chatViewStage;

    public MessageViewController(MessageListenerService messageListenerService) {
        this.messageListenerService = messageListenerService;
        this.messageListenerService.addListener(this);
        messageHtmlStr = new StringBuilder();
        this.applicationService = (ApplicationService) ChatClientApplication.SERVICES.get(Service.Services.APPLICATION_SERVICE);
        this.chatViewStage = applicationService.getApplicationController().getChatViewStage();
    }

    @Override
    public void onMessage(Message message) {
        if (message == null || !message.getHeaders().containsKey(Headers.CONTENT_TYPE)) {
            System.out.println("Invalid message received. Skipping...");
            return;
        }
        processMessage(message);
    }

    private void processMessage(Message message) {
        String contentTypeHeader = (String) message.getHeaders().get(Headers.CONTENT_TYPE);
        boolean updateMessageView = false;
        if (Headers.ContentType.FILE.equals(contentTypeHeader)) {
            //file received
            String msg = "<i>Downloading file....";
            ByteArrayFile file = (ByteArrayFile) message.getBody();
            try {
                saveFile(file);
                msg += "done</i>";
            } catch (Exception e) {
                e.printStackTrace();
                msg += "unexpected error occurred!</i>";
            }
            messageHtmlStr.append(messageHtmlStr.length() == 0 ? "" : "<br>");
            messageHtmlStr.append(msg);
            updateMessageView = true;
        } else if (Headers.ContentType.TEXT.equals(contentTypeHeader)) {
            String user = (String) message.getHeaders().get("user");
            if (message.getHeaders().containsKey(Headers.KEYSTROKE)) {
                //update the status text
                if (!user.equals(ApplicationService.USERNAME)) {
                    StatusBar statusBar = (StatusBar) chatViewStage.getScene().lookup("#statusBar");
                    statusBar.updateCurrentInfo(user + " is typing");
                }
                return;
            }
            String rawMessage = (String) message.getBody();
            if (StringUtils.isEmpty(rawMessage)) {
                return;
            }
            String processedMessage = processEmojis(rawMessage);
            processedMessage = processUrls(processedMessage);
            //apend the user
            processedMessage = user + ": " + processedMessage;
            //colorize the message
            //only system messages will not have any colors so just italicisize it
            String color = Utils.getNonNull((String)message.getHeaders().get(Headers.USER_COLOR), "transparent; font-style: italic;");
            processedMessage = String.format("<span class=\"user-message\" style=\"background: %s;\">%s</span>", color, processedMessage);
            //after processing is complete, append the new message to the message string
            messageHtmlStr.append(messageHtmlStr.length() == 0 ? "" : "<br><br>");
            messageHtmlStr.append(processedMessage);
            updateMessageView = true;
        }

        if (updateMessageView) {
            updateMessageView( messageHtmlStr.toString());
        }
    }

    private String processEmojis(String rawStr) {
        Pattern emojiPattern = Pattern.compile("(:[a-z_]+[\\s?!.,])|(:[a-z_]+$)");
        Matcher emojiMatcher = emojiPattern.matcher(rawStr);
        StringBuffer buffer = new StringBuffer();
        String match, replacementEmoji, lastChar;
        boolean putbackLastChar;
        while (emojiMatcher.find()) {
            putbackLastChar = false;
            match = emojiMatcher.group();
            lastChar = match.substring(match.length()-1);
            if ("!?., ".contains(lastChar)) {
                match = match.substring(0, match.length() - 1);
                putbackLastChar = true;
            }
            replacementEmoji = Constants.EMOJI_MAP.get(match);
            //can't find matching emoji - put back the original string
            if (StringUtils.isEmpty(replacementEmoji)) {
                replacementEmoji = match;
                putbackLastChar = false; //entire string will be put back so we don't need this anymore
            }
            emojiMatcher.appendReplacement(buffer,replacementEmoji + (putbackLastChar ? lastChar : ""));
        }
        emojiMatcher.appendTail(buffer);
        return buffer.toString();
    }

    private String processUrls(String rawStr) {
        //convert any weblinks that images to image tags so they can be
        List<String> urls = Utils.extractUrlsFrom(rawStr);
        if (!CollectionUtils.isEmpty(urls)) {
            //convert URLs to anchor tags
            String urlTemplate = "<a href=\"%s\">%s</a>";
            for (String url : urls) {
                rawStr = rawStr.replaceAll(Pattern.quote(url), String.format(urlTemplate, url, url));
            }
            //fetch a preview of any URLS pointing to images
            String imageTemplate = "<br><br><div style=\"border: 1px solid black; padding: 5px;\"><span>%s</span><br><a href=\"%s\"><img src=\"%s\" style=\"max-width: 150px;max-height: 150px;\"></a></div>";
            for (String url : urls) {
                if (StringUtils.endsWithAny(url.toLowerCase(), Constants.IMAGE_FORMATS.toArray(new String[0]))) {
                    //grab the image name
                    String imageName = url.substring(url.lastIndexOf('/') + 1);
                    rawStr += String.format(imageTemplate, imageName, url, url);
                }
            }
        }
        return rawStr;
    }

    private void updateMessageView(String htmlString)  {
        WebView webView = (WebView) getBoundNode();
        webView.getEngine().loadContent(htmlString);
        notifyUser();
    }

    private void saveFile(ByteArrayFile byteArrayFile) {
        String downloadsDirectoryPath = System.getProperty("user.home") + File.separator + "downloads" + File.separator + "ichat" + File.separator;
        File downloadDirectory = new File(downloadsDirectoryPath);
        if (!downloadDirectory.exists()) {
            downloadDirectory.mkdir();
        }

        String filePath = downloadsDirectoryPath + byteArrayFile.getName();
        File fileToSave = new File(filePath);
        int fileCt = 0;
        while (fileToSave.exists()) {
            filePath = downloadsDirectoryPath + getOrdinalFileName(byteArrayFile.getName(), ++fileCt);
            fileToSave = new File(filePath);
        }
        BufferedOutputStream bufferedOutputStream;
        try {
            bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(filePath));
            bufferedOutputStream.write(byteArrayFile.getFileBytes());
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
            Desktop.getDesktop().open(downloadDirectory);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getOrdinalFileName(String fileName, int ordinal) {
        int extensionPrefixIdx = fileName.lastIndexOf('.');
        String fileExtension = fileName.substring(extensionPrefixIdx);
        return fileName.substring(0, extensionPrefixIdx) + "(" + ordinal + ")" + fileExtension;
    }

    private void notifyUser() {
        boolean isVisible = applicationService.getApplicationController().isFocused();
        if (!isVisible) {
            Media sound = Constants.MESSAGE_ALERT;
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.play();
        }
    }
}
