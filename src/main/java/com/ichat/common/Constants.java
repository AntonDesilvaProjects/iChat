package com.ichat.common;

import javafx.scene.media.Media;

import javax.imageio.ImageIO;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Constants {
    public static Map<String, String> EMOJI_MAP;
    static {
        Map<String,String> temp = new HashMap<>();
        temp.put(":smile", "&#128512;");
        temp.put(":joy", "&#128514;");
        temp.put(":wink", "&#128521;");
        temp.put(":neutral", "&#128528;");
        temp.put(":confused", "&#128533;");
        temp.put(":kiss", "&#128536;");
        temp.put(":disappointed", "&#128542;");
        temp.put(":worried", "&#128543;");
        temp.put(":angry", "&#128544;");
        temp.put(":cry", "&#128546;");
        temp.put(":sleepy", "&#128564;");
        temp.put(":shock", "&#128562;");
        temp.put(":sick", "&#129319;");
        temp.put(":vomit", "&#129326;");
        EMOJI_MAP = Collections.unmodifiableMap(temp);
    }
    public static final List<String> IMAGE_FORMATS = Collections.unmodifiableList(Arrays.asList(".tif", ".tiff", ".bmp", ".jpg", ".jpeg", ".gif", ".png", ".eps"));
    public static final String FILE_DOWNLOAD_CLASSNAME = "file-download";

    public final static Executor THREAD_POOL = Executors.newCachedThreadPool();
    public static Media MESSAGE_ALERT;

    static {
        try {
            MESSAGE_ALERT = new Media(Constants.class.getClassLoader().getResource("sms-alert-2-daniel_simon.mp3").toURI().toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
