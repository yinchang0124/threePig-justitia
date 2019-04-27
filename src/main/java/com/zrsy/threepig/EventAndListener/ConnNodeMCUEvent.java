package com.zrsy.threepig.EventAndListener;

import org.springframework.context.ApplicationEvent;


/**
 * 连接nodemcu的事件
 */
public class ConnNodeMCUEvent extends ApplicationEvent {
    private static final long serialVersionUID = 1L;

    public ConnNodeMCUEvent(Object source) {
        super(source);
    }
}
