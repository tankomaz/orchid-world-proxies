package com.subgraph.orchid.worldproxy;

import com.subgraph.orchid.TorClient;
import com.subgraph.orchid.TorClientConfig;
import com.subgraph.orchid.TorServer;

import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

public class TorRunner implements Callable<InitTorStatus> {

    private final static Logger logger = Logger.getLogger(TorRunner.class.getName());

    private TorServer torServer;
    private String iso;
    private int port;

    public TorRunner(TorServer torServer, String iso, int port) {
        this.torServer = torServer;
        this.iso = iso;
        this.port = port;
    }

    @Override
    public InitTorStatus call() throws Exception {
        final TorClientConfig clientConfig = new TorClientConfig(Collections.singletonList("{"+iso+"}"), null, null, null);
        final TorClient client = new TorClient(torServer, clientConfig);
        client.enableSocksListener(port);
        client.start();
        try {
            client.waitUntilReady(5000);
            logger.info("Proxy Ready to start..." + iso);
            return new InitTorStatus(port, iso);
        } catch (Throwable e) {
            client.stop();
            logger.info(String.format("ExitNode for [%s] will not load. %s", iso, e));
            return null;
        }
    }

}
