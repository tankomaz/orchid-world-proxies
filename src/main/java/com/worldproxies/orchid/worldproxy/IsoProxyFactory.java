package com.worldproxies.orchid.worldproxy;

import com.worldproxies.orchid.Tor;
import com.worldproxies.orchid.TorConfig;
import com.worldproxies.orchid.TorInitializationListener;
import com.worldproxies.orchid.TorServer;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class IsoProxyFactory {
    private final static Logger logger = Logger.getLogger(IsoProxyFactory.class.getName());

    private final String[] COUNTRY_CODES = { "AP", "EU", "AD", "AE", "AF", "AG", "AI", "AL", "AM", "CW", "AO", "AQ",
            "AR", "AS", "AT", "AU", "AW", "AZ", "BA", "BB", "BD", "BE", "BF", "BG", "BH", "BI", "BJ", "BM", "BN", "BO",
            "BR", "BS", "BT", "BV", "BW", "BY", "BZ", "CA", "CC", "CD", "CF", "CG", "CH", "CI", "CK", "CL", "CM", "CN",
            "CO", "CR", "CU", "CV", "CX", "CY", "CZ", "DE", "DJ", "DK", "DM", "DO", "DZ", "EC", "EE", "EG", "EH", "ER",
            "ES", "ET", "FI", "FJ", "FK", "FM", "FO", "FR", "SX", "GA", "GB", "GD", "GE", "GF", "GH", "GI", "GL", "GM",
            "GN", "GP", "GQ", "GR", "GS", "GT", "GU", "GW", "GY", "HK", "HM", "HN", "HR", "HT", "HU", "ID", "IE", "IL",
            "IN", "IO", "IQ", "IR", "IS", "IT", "JM", "JO", "JP", "KE", "KG", "KH", "KI", "KM", "KN", "KP", "KR", "KW",
            "KY", "KZ", "LA", "LB", "LC", "LI", "LK", "LR", "LS", "LT", "LU", "LV", "LY", "MA", "MC", "MD", "MG", "MH",
            "MK", "ML", "MM", "MN", "MO", "MP", "MQ", "MR", "MS", "MT", "MU", "MV", "MW", "MX", "MY", "MZ", "NA", "NC",
            "NE", "NF", "NG", "NI", "NL", "NO", "NP", "NR", "NU", "NZ", "OM", "PA", "PE", "PF", "PG", "PH", "PK", "PL",
            "PM", "PN", "PR", "PS", "PT", "PW", "PY", "QA", "RE", "RO", "RU", "RW", "SA", "SB", "SC", "SD", "SE", "SG",
            "SH", "SI", "SJ", "SK", "SL", "SM", "SN", "SO", "SR", "ST", "SV", "SY", "SZ", "TC", "TD", "TF", "TG", "TH",
            "TJ", "TK", "TM", "TN", "TO", "TL", "TR", "TT", "TV", "TW", "TZ", "UA", "UG", "UM", "US", "UY", "UZ", "VA",
            "VC", "VE", "VG", "VI", "VN", "VU", "WF", "WS", "YE", "YT", "RS", "ZA", "ZM", "ME", "ZW", "AX", "GG", "IM",
            "JE", "BL", "MF", "BQ", "SS" };

    private final int startPort = 10000;
    private AtomicInteger currentPort = new AtomicInteger(startPort);
    private Map<String, Integer> isoProxyPortMap = new ConcurrentHashMap<>();
    private ExecutorService service;

    private IsoProxyFactory() {
        this.service = Executors.newCachedThreadPool();
        init();
    }

    private void init() {
        try {
            TorConfig config = Tor.createConfig();
            config.setWarnUnsafeSocks(false);
            TorServer torServer = new TorServer(config);
            torServer.addInitializationListener(createInitializationListner());
            torServer.start();
            torServer.waitUntilReady();

            List<Future<InitTorStatus>> statuses = new ArrayList<>();
            for (String iso : COUNTRY_CODES) {
                Future<InitTorStatus> torStatusFuture = service
                        .submit(new TorRunner(torServer, iso, currentPort.getAndIncrement()));
                statuses.add(torStatusFuture);
                Thread.sleep(200);
            }
            for (Future<InitTorStatus> status : statuses) {
                try {
                    InitTorStatus initTorStatus = status.get();
                    if (initTorStatus == null) {
                        continue;
                    }
                    if (!isoProxyPortMap.containsKey(initTorStatus.getIso())) {
                        isoProxyPortMap.putIfAbsent(initTorStatus.getIso(), initTorStatus.getPort());
                    }
                } catch (Throwable t) {
                    logger.warning("Cannot init thread:" + t.getMessage());
                }
            }

        } catch (Throwable t) {
            logger.severe("Cannot init server with proxy clients:" + t.getMessage());
        }
        logger.info("All sockets init successfully, total loaded countries:" + isoProxyPortMap.size());
    }

    private static TorInitializationListener createInitializationListner() {
        return new TorInitializationListener() {

            public void initializationProgress(String message, int percent) {
                logger.info(">>> [ " + percent + "% ]: " + message);
            }

            public void initializationCompleted() {
                logger.info("Tor is ready to go!");
            }
        };
    }

    public static Map<String, Integer> getIsoPorts() {
        return getInstance().isoProxyPortMap;
    }

    public static Proxy getProxy(String iso) {
        Integer port = getInstance().isoProxyPortMap.get(iso);
        if (port != null) {
            return new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(port));
        } else {
            return null;
        }
    }

    private static IsoProxyFactory getInstance() {
        return InstanceHolder.proxyFactory;
    }

    private static class InstanceHolder {
        private static final IsoProxyFactory proxyFactory = new IsoProxyFactory();
    }

}
