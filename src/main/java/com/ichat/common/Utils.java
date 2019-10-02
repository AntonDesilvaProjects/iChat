package com.ichat.common;

import org.apache.commons.lang3.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Utils {
    public static List<String> extractUrlsFrom(String text) {
        if(StringUtils.isEmpty(text)) {
            return null;
        }
        List<String> extractedUrls = new ArrayList<>();
        //we could use a regex approach to match urls but we will
        //take a simpler approach based on tokenization
        StringTokenizer tokenizer = new StringTokenizer(text, " ");
        String token;
        while (tokenizer.hasMoreTokens()) {
            token = tokenizer.nextToken();
            if (StringUtils.startsWithAny(token, "http://", "https://")) {
                try {
                    //java will attempt to parse the URL - if it succeeds, then we will
                    //add to the list of valid URLs; otherwise exception will be thrown
                    new URL(token);
                    extractedUrls.add(token);
                } catch(MalformedURLException m) {
                    //bad URL - ignore and continue processing
                }
            }
        }
        return extractedUrls;
    }

    public static <T> T getNonNull(T value, T other) {
        return value == null ? other : value;
    }
}
