package com.zrsy.threepig.EventAndListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

public class ConnRaspberryListener implements ApplicationListener<ConnRaspberryEvent> {
    protected static final Logger logger = LoggerFactory.getLogger(ConnRaspberryListener.class);



    public void onApplicationEvent(ConnRaspberryEvent connEvent) {
        logger.info(connEvent.getSource().toString());
    }
}
