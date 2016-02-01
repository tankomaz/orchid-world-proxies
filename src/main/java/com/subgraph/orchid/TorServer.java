package com.subgraph.orchid;

import com.subgraph.orchid.circuits.TorInitializationTracker;
import com.subgraph.orchid.crypto.PRNGFixes;
import com.subgraph.orchid.directory.downloader.DirectoryDownloaderImpl;

import javax.crypto.Cipher;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TorServer {

    private final static Logger logger = Logger.getLogger(TorClient.class.getName());

    private final TorConfig config;
    private final Directory directory;
    private final TorInitializationTracker initializationTracker;
    private final ConnectionCache connectionCache;
    private final DirectoryDownloaderImpl directoryDownloader;
    private final CircuitManager circuitManager;
    //	private final Dashboard dashboard;


    private boolean isStarted = false;
    private boolean isStopped = false;

    private final CountDownLatch readyLatch = new CountDownLatch(1);

    public TorServer(TorConfig config) {
        if(Tor.isAndroidRuntime()) {
            PRNGFixes.apply();
        }
        this.config = config;
        TorClientConfig clientConfig = new TorClientConfig(null, null, null, null);
        directory = Tor.createDirectory(config, null);
        initializationTracker = Tor.createInitalizationTracker();
        initializationTracker.addListener(createReadyFlagInitializationListener());
        connectionCache = Tor.createConnectionCache(config, clientConfig, initializationTracker);
        directoryDownloader = Tor.createDirectoryDownloader(config, initializationTracker);
        circuitManager = Tor.createCircuitManager(config, clientConfig, directoryDownloader, directory, connectionCache,
                initializationTracker);

        //		dashboard = new Dashboard();
        //		dashboard.addRenderables(circuitManager, directoryDownloader, socksListener);
    }

    public TorConfig getConfig() {
        return config;
    }

    /**
     * Start running the Tor client service.
     */
    public synchronized void start() {
        if(isStarted) {
            return;
        }
        if(isStopped) {
            throw new IllegalStateException("Cannot restart a TorClient instance.  Create a new instance instead.");
        }
        logger.info("Starting Orchid (version: "+ Tor.getFullVersion() +")");
        verifyUnlimitedStrengthPolicyInstalled();
        circuitManager.startBuildingCircuits();
        directoryDownloader.start(directory);
        //        if(dashboard.isEnabledByProperty()) {
        //			dashboard.startListening();
        //		}
        isStarted = true;
    }

    public synchronized void stop() {
        if(!isStarted || isStopped) {
            return;
        }
        try {
            //			if(dashboard.isListening()) {
            //				dashboard.stopListening();
            //			}
            directoryDownloader.stop();
            directory.close();
            connectionCache.close();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Unexpected exception while shutting down TorClient instance: "+ e, e);
        } finally {
            isStopped = true;
        }
    }

    public Directory getDirectory() {
        return directory;
    }

    public void waitUntilReady() throws InterruptedException {
        readyLatch.await();
    }

    public void waitUntilReady(long timeout) throws InterruptedException, TimeoutException {
        if(!readyLatch.await(timeout, TimeUnit.MILLISECONDS)) {
            throw new TimeoutException();
        }
    }

    private synchronized void ensureStarted() {
        if(!isStarted) {
            throw new IllegalStateException("Must call start() first");
        }
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

    public void addInitializationListener(TorInitializationListener listener) {
        initializationTracker.addListener(listener);
    }

    public void removeInitializationListener(TorInitializationListener listener) {
        initializationTracker.removeListener(listener);
    }

    private TorInitializationListener createReadyFlagInitializationListener() {
        return new TorInitializationListener() {
            public void initializationProgress(String message, int percent) {}
            public void initializationCompleted() {
                readyLatch.countDown();
            }
        };
    }

    private static TorInitializationListener createInitalizationListner() {
        return new TorInitializationListener() {

            public void initializationProgress(String message, int percent) {
                logger.info(">>> [ "+ percent + "% ]: "+ message);
            }

            public void initializationCompleted() {
                logger.info("Tor is ready to go!");
            }
        };
    }

    private void verifyUnlimitedStrengthPolicyInstalled() {
        try {
            if(Cipher.getMaxAllowedKeyLength("AES") < 256) {
                final String message = "Unlimited Strength Jurisdiction Policy Files are required but not installed.";
                logger.severe(message);
                throw new TorException(message);
            }
        } catch (NoSuchAlgorithmException e) {
            logger.log(Level.SEVERE, "No AES provider found");
            throw new TorException(e);
        }  catch (NoSuchMethodError e) {
            logger.info("Skipped check for Unlimited Strength Jurisdiction Policy Files");
        }
    }


}
