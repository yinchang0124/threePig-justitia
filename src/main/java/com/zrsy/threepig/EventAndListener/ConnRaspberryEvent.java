package com.zrsy.threepig.EventAndListener;

import org.springframework.context.ApplicationEvent;

public class ConnRaspberryEvent extends ApplicationEvent {
    private static final long serialVersionUID = 1L;

    public ConnRaspberryEvent(Object source) {
        super(source);
    }
}
