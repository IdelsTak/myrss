package com.github.idelstak.myrss.components;

import org.apache.commons.text.*;

import java.net.*;
import java.nio.charset.*;

public class CleanLink {

    private final String text;

    public CleanLink(String text) {this.text = text;}

    public URL url() throws URISyntaxException, MalformedURLException {
        String decoded = URLDecoder.decode(text, StandardCharsets.UTF_8);
        String unescaped = StringEscapeUtils.unescapeHtml4(decoded);
        return new URI(unescaped).toURL();
    }
}