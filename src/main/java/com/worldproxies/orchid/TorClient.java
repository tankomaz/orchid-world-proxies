package com.worldproxies.orchid;

import com.worldproxies.orchid.circuits.TorInitializationTracker;
import com.worldproxies.orchid.directory.downloader.DirectoryDownloaderImpl;
import com.worldproxies.orchid.sockets.OrchidSocketFactory;

import javax.net.SocketFactory;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is the main entry-point for running a Tor proxy
 * or client.
 */
public class TorClient {
    private final static Logger logger = Logger.getLogger(TorClient.class.getName());

    private final SocksPortListener socksListener;
    //	private final Dashboard dashboard;

    private final TorInitializationTracker initializationTracker;
    private final ConnectionCache connectionCache;
    private final DirectoryDownloaderImpl directoryDownloader;
    private final CircuitManager circuitManager;

    private boolean isStarted = false;
    private boolean isStopped = false;

    private final CountDownLatch readyLatch = new CountDownLatch(1);

    public TorClient(TorServer torServer, TorClientConfig clientConfig) {
        initializationTracker = Tor.createInitalizationTracker();
        initializationTracker.addListener(createReadyFlagInitializationListener());
        connectionCache = Tor.createConnectionCache(torServer.getConfig(), clientConfig, initializationTracker);
        directoryDownloader = Tor.createDirectoryDownloader(torServer.getConfig(), initializationTracker);
        circuitManager = Tor.createCircuitManager(torServer.getConfig(), clientConfig, directoryDownloader,
                torServer.getDirectory(), connectionCache, initializationTracker);
        socksListener = Tor.createSocksPortListener(torServer.getConfig(), circuitManager);
        //		dashboard = new Dashboard();
        //		dashboard.addRenderables(circuitManager, directoryDownloader, socksListener);
    }

    public SocketFactory getSocketFactory() {
        return new OrchidSocketFactory(this);
    }

    /**
     * Start running the Tor client service.
     */
    public synchronized void start() {
        if (isStarted) {
            return;
        }
        if (isStopped) {
            throw new IllegalStateException("Cannot restart a TorClient instance.  Create a new instance instead.");
        }
        logger.info("Starting Orchid (version: " + Tor.getFullVersion() + ")");
        circuitManager.startBuildingCircuits();
        //        if(dashboard.isEnabledByProperty()) {
        //			dashboard.startListening();
        //		}
        isStarted = true;
    }

    public synchronized void stop() {
        if (!isStarted || isStopped) {
            return;
        }
        try {
            socksListener.stop();
            //			if(dashboard.isListening()) {
            //				dashboard.stopListening();
            //			}
            directoryDownloader.stop();
            connectionCache.close();
            circuitManager.stopBuildingCircuits(true);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Unexpected exception while shutting down TorClient instance: " + e, e);
        } finally {
            isStopped = true;
        }
    }

    //	public CircuitManager getCircuitManager() {
    //		return circuitManager;
    //	}

    public Stream openExitStreamTo(String hostname, int port)
            throws InterruptedException, TimeoutException, OpenFailedException {
        ensureStarted();
        return circuitManager.openExitStreamTo(hostname, port);
    }

    public void waitUntilReady() throws InterruptedException {
        readyLatch.await();
    }

    public void waitUntilReady(long timeout) throws InterruptedException, TimeoutException {
        if (!readyLatch.await(timeout, TimeUnit.MILLISECONDS)) {
            throw new TimeoutException();
        }
    }

    //	public Stream openExitStreamTo(String hostname, int port) throws InterruptedException, TimeoutException,
    //            OpenFailedException {
    //		ensureStarted();
    //		return circuitManager.openExitStreamTo(hostname, port);
    //	}

    private synchronized void ensureStarted() {
        if (!isStarted) {
            throw new IllegalStateException("Must call start() first");
        }
    }

    private TorInitializationListener createReadyFlagInitializationListener() {
        return new TorInitializationListener() {
            public void initializationProgress(String message, int percent) {
            }

            public void initializationCompleted() {
                readyLatch.countDown();
            }
        };
    }

    public void enableSocksListener(int port) {
        socksListener.addListeningPort(port);
    }

    public void enableSocksListener() {
        enableSocksListener(9150);
    }

    //	public void enableDashboard() {
    //		if(!dashboard.isListening()) {
    //			dashboard.startListening();
    //		}
    //	}
    //
    //	public void enableDashboard(int port) {
    //		dashboard.setListeningPort(port);
    //		enableDashboard();
    //	}
    //
    //	public void disableDashboard() {
    //		if(dashboard.isListening()) {
    //			dashboard.stopListening();
    //		}
    //	}

    public static void main(String[] args) {
        //        TorConfig config = Tor.createConfig();
        //        config.setExitNodes(new ArrayList<String>() {{add("us");}});
        //		final TorClient client = new TorClient(null);
        //		client.addInitializationListener(createInitalizationListner());
        //		client.start();
        //        client.enableSocksListener();
    }

}
