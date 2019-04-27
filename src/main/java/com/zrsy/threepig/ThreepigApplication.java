package com.zrsy.threepig;

import com.zrsy.threepig.EventAndListener.ConnNodeMCUListener;
import com.zrsy.threepig.EventAndListener.ConnRaspberryListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ThreepigApplication {

    public static void main(String[] args) {
        SpringApplication app=new SpringApplication(ThreepigApplication.class);
        app.addListeners(new ConnRaspberryListener());
        app.addListeners(new ConnNodeMCUListener());
        app.run(args);
    }

}
