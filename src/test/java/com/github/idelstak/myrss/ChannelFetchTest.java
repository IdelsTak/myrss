package com.github.idelstak.myrss;

import com.github.idelstak.myrss.channels.*;
import com.rometools.rome.feed.rss.*;
import com.rometools.rome.feed.synd.*;
import com.rometools.rome.io.*;
import org.junit.jupiter.api.*;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

public class ChannelFetchTest {

    @Test
    @Disabled("No need to run an online test")
    public void shouldFetchGivenLink() throws Exception {
        String text = "https://www.upwork.com/ab/feed/jobs/rss?api_params=1&amp;" +
                      "orgUid=760234771696193537&amp;" +
                      "paging=NaN-undefined&amp;q=java&amp;" +
                      "securityToken" +
                      "=bdd0d2494d627766508a4c0db58536ee295d5754726667de4c8ef27f41c04cfe8c3a697755a92d9fe93754b946c5296cfd70451f6bd5f4ca68b2950c7d7236de&amp;sort=recency&amp;userUid=760234771675222016";
        CleanLink link = new CleanLink(text);
        ChannelFetch fetch = new ChannelFetch(link.url());
        Channel channel = fetch.channel();

        assertEquals("All jobs | upwork.com", channel.getTitle());
    }

    @Test
    public void shouldReadRssFiles() throws Exception {
        URL filesDir = getClass().getResource("/sample/files");
        assert filesDir != null;
        List<File> files;
        try (Stream<Path> walked = Files.walk(Path.of(filesDir.toURI()))) {
            files = walked.map(Path::toFile).filter(File::isFile).toList();
            for (File file : files) {
                SyndFeed feed = new SyndFeedInput().build(file);
                String title = feed.getTitle();
                switch (file.getName()) {
                    case "java-us.rss" -> assertThat(title).isEqualTo("All Java AND United States jobs | upwork.com");
                    case "javafx.rss" -> assertThat(title).isEqualTo("All JavaFX jobs | upwork.com");
                    case "java.rss" -> assertThat(title).isEqualTo("All Java - Payment verified jobs | upwork.com");
                    case "swing.rss" -> assertThat(title).isEqualTo("All Swing jobs | upwork.com");
                }
            }
        }
    }
}