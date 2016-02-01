package com.subgraph.orchid.circuits.hs;

import com.subgraph.orchid.Directory;
import com.subgraph.orchid.HiddenServiceCircuit;
import com.subgraph.orchid.OpenFailedException;
import com.subgraph.orchid.Stream;
import com.subgraph.orchid.StreamConnectFailedException;
import com.subgraph.orchid.TorConfig;
import com.subgraph.orchid.TorException;
import com.subgraph.orchid.circuits.CircuitManagerImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

public class HiddenServiceManager {
	private final static int RENDEZVOUS_RETRY_COUNT = 5;
	private final static int HS_STREAM_TIMEOUT = 20000;
	
	private final static Logger logger = Logger.getLogger(HiddenServiceManager.class.getName());
	
	private final Map<String, HiddenService> hiddenServices;
	private final TorConfig config;
	private final Directory directory;
	private final com.subgraph.orchid.circuits.hs.HSDirectories hsDirectories;
	private final CircuitManagerImpl circuitManager;
	
	public HiddenServiceManager(TorConfig config, Directory directory, CircuitManagerImpl circuitManager) {
		this.config = config;
		this.directory = directory;
		this.hiddenServices = new HashMap<String, HiddenService>();
		this.hsDirectories = new com.subgraph.orchid.circuits.hs.HSDirectories(directory);
		this.circuitManager = circuitManager;
	}
	
	public Stream getStreamTo(String onion, int port) throws OpenFailedException, InterruptedException, TimeoutException {
		final HiddenService hs = getHiddenServiceForOnion(onion);
		final HiddenServiceCircuit circuit = getCircuitTo(hs);
		
		try {
			return circuit.openStream(port, HS_STREAM_TIMEOUT);
		} catch (StreamConnectFailedException e) {
			throw new OpenFailedException("Failed to open stream to hidden service "+ hs.getOnionAddressForLogging() + " reason "+ e.getReason());
		}
	}
	
	private synchronized HiddenServiceCircuit getCircuitTo(HiddenService hs) throws OpenFailedException {
		if(hs.getCircuit() == null) {
			final HiddenServiceCircuit c = openCircuitTo(hs);
			if(c == null) {
				throw new OpenFailedException("Failed to open circuit to "+ hs.getOnionAddressForLogging());
			}
			hs.setCircuit(c);
		}
		return hs.getCircuit();
	}
	
	private HiddenServiceCircuit openCircuitTo(HiddenService hs) throws OpenFailedException {
		com.subgraph.orchid.circuits.hs.HSDescriptor descriptor = getDescriptorFor(hs);
		
		for(int i = 0; i < RENDEZVOUS_RETRY_COUNT; i++) {
			final HiddenServiceCircuit c = openRendezvousCircuit(hs, descriptor);
			if(c != null) {
				return c;
			}
		}
		throw new OpenFailedException("Failed to open circuit to "+ hs.getOnionAddressForLogging());
	}
	
	com.subgraph.orchid.circuits.hs.HSDescriptor getDescriptorFor(HiddenService hs) throws OpenFailedException {
		if(hs.hasCurrentDescriptor()) {
			return hs.getDescriptor();
		}
		final com.subgraph.orchid.circuits.hs.HSDescriptor descriptor = downloadDescriptorFor(hs);
		if(descriptor == null) {
			final String msg = "Failed to download HS descriptor for "+ hs.getOnionAddressForLogging(); 
			logger.info(msg);
			throw new OpenFailedException(msg);
		}
		hs.setDescriptor(descriptor);
		return descriptor;
	}
	
	private com.subgraph.orchid.circuits.hs.HSDescriptor downloadDescriptorFor(HiddenService hs) {
		logger.fine("Downloading HS descriptor for "+ hs.getOnionAddressForLogging());
		final List<com.subgraph.orchid.circuits.hs.HSDescriptorDirectory> dirs = hsDirectories.getDirectoriesForHiddenService(hs);
		final com.subgraph.orchid.circuits.hs.HSDescriptorDownloader downloader = new com.subgraph.orchid.circuits.hs.HSDescriptorDownloader(hs, circuitManager, dirs);
		return downloader.downloadDescriptor();
	}

	HiddenService getHiddenServiceForOnion(String onion) throws OpenFailedException {
		final String key = onion.endsWith(".onion") ? onion.substring(0, onion.length() - 6) : onion;
		synchronized(hiddenServices) {
			if(!hiddenServices.containsKey(key)) {
				hiddenServices.put(key, createHiddenServiceFor(key));
			}
			return hiddenServices.get(key);
		}
	}	
	
	private HiddenService createHiddenServiceFor(String key) throws OpenFailedException {
		try {
			byte[] decoded = HiddenService.decodeOnion(key);
			return new HiddenService(config, decoded);
		} catch (TorException e) {
			final String target = config.getSafeLogging() ? "[scrubbed]" : (key + ".onion");
			throw new OpenFailedException("Failed to decode onion address "+ target + " : "+ e.getMessage());
		}
	}

	private HiddenServiceCircuit openRendezvousCircuit(HiddenService hs, com.subgraph.orchid.circuits.hs.HSDescriptor descriptor) {
		final com.subgraph.orchid.circuits.hs.RendezvousCircuitBuilder builder = new com.subgraph.orchid.circuits.hs.RendezvousCircuitBuilder(directory, circuitManager, hs, descriptor);
		try {
			return builder.call();
		} catch (Exception e) {
			return null;
		}
	}
}