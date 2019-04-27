package com.zrsy.threepig.EventAndListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

public class ConnNodeMCUListener implements ApplicationListener<ConnNodeMCUEvent> {
    protected static final Logger logger = LoggerFactory.getLogger(ConnRaspberryListener.class);


    @Override
    public void onApplicationEvent(ConnNodeMCUEvent connNodeMCUEvent) {

    }
}
