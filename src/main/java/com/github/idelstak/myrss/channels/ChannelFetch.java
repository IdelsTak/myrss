package com.github.idelstak.myrss.channels;

import com.rometools.fetcher.*;
import com.rometools.fetcher.impl.*;
import com.rometools.rome.feed.rss.*;
import com.rometools.rome.feed.synd.*;
import com.rometools.rome.io.*;

import java.io.*;
import java.net.*;

public class ChannelFetch {

    private final URL url;

    public ChannelFetch(URL url) {this.url = url;}

    public Channel channel() throws FetcherException, FeedException, IOException {
        FeedFetcherCache cache = HashMapFeedInfoCache.getInstance();
        SyndFeed feed = new HttpURLFeedFetcher(cache).retrieveFeed(url);
        return (Channel) feed.createWireFeed();
    }

}