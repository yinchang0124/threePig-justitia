package com.zrsy.threepig.initializer;

import com.bigchaindb.builders.BigchainDbConfigBuilder;
import net.i2p.crypto.eddsa.EdDSASecurityProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.security.Security;

/**
 * springboot启动，这个类也会启动，并且去连接BigchainDB节点
 */
@Component
public class BigchaindbSetupRunner implements CommandLineRunner {
    @Value("${BigchainDB_url}")
    private String baseUrl;

    @Override
    public void run(String... args) throws Exception {
        BigchainDbConfigBuilder
                .baseUrl(baseUrl)
                .setup();
    }
}
