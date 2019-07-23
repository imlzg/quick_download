package org.gaozou.roy.quick.download;

import java.net.URL;
import java.net.MalformedURLException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

/**
 * Author: george
 * Powered by GaoZou group.
 */
public class URLDetect {
    private static String v = "http://www.youtube.com/get_video?video_id={0}&t={1}";
    private static String p = "http://www.youtube.com/watch?v=";
    private static String watchRegex = "youtube\\.com\\/watch\\?v=([^&]+)";
    private static String embedRegex = "youtube\\.com\\/v\\/([^&]+)";

    public static URL detect(String url) {
        URL durl = getURL(url);

        String host = durl.getHost();
        if (host.indexOf("youtube.com") != -1) {

            String vid = collect(url, embedRegex, 1);
            if (! isEmpty(vid)) {
                url = p + vid;
            } else {
                vid = collect(url, watchRegex, 1);
            }
    
            String c = getContent(url);
            String t = collect(c, "\\\"t\\\": \\\"([^\\\"]*)\\\"", 1);

            durl = getURL(format(v, vid, t));

        }
        return durl;
    }

    private static String getHost(String url) {
        try {
            return new URL(url).getHost();
        } catch (MalformedURLException e) {
            System.out.println("problem get host from: "+ url);
            return null;
        }
    }

    private static String collect(String str, String pattern, Integer i) {
        Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(str);
        if (m.find()) {
            return m.group(i).trim();
        }
        return null;
    }

    private static URL getURL(String link) {
        if (isEmpty(link)) return null;
        if (! link.toLowerCase().startsWith("http://")) return null;
        try {
            return new URL(link);
        } catch (MalformedURLException e) {
            System.out.println("problem get URL from: "+ link);
            return null;
        }
    }
    private static String getContent(String link) {
        return getContent(getURL(link));
    }
    private static String getContent(URL url) {
        if (null == url) return null;
        StringBuffer buffer = new StringBuffer();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
        } catch (IOException e) {
            System.out.println("problem read "+ url);
        }
        if (buffer.length() == 0) return null;
        String content = buffer.toString();
        String title = collect(content, "<title>(.*)</title>", 1);
        title = null == title ? "" : title;
        if (title.indexOf("404") != -1 || title.indexOf("Error") != -1 || title.indexOf("Not Found") != -1) {
            System.out.println("404 problem: "+ url);
            return null;
        }
        return content;
    }

    private static boolean isEmpty(String str) {
        return (null == str || "".equals(str.trim()));
    }

    private static String format(String pattern, String... params) {
        for (int i = 0; i < params.length; i++) {
            pattern = pattern.replaceAll("\\{" + i + "\\}", params[i]);
        }
        return pattern;
    }
}
