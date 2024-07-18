package com.github.idelstak.myrss;

import com.rometools.rome.feed.rss.*;
import org.junit.jupiter.api.*;

public class ChannelTest {

    @Test
    public void creates() throws Exception {
        String text = "https://www.upwork.com/ab/feed/jobs/rss?api_params=1&amp;" +
                      "orgUid=760234771696193537&amp;" +
                      "paging=NaN-undefined&amp;q=java&amp;" +
                      "securityToken" +
                      "=bdd0d2494d627766508a4c0db58536ee295d5754726667de4c8ef27f41c04cfe8c3a697755a92d9fe93754b946c5296cfd70451f6bd5f4ca68b2950c7d7236de&amp;sort=recency&amp;userUid=760234771675222016";
        CleanLink link = new CleanLink(text);
        ChannelFetch fetch = new ChannelFetch(link.url());
        Channel channel = fetch.channel();

        System.out.println("channel = " + channel);
    }
}