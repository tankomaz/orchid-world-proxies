package com.worldproxies.orchid.circuits;

import com.worldproxies.orchid.OpenFailedException;
import com.worldproxies.orchid.Stream;
import com.worldproxies.orchid.StreamConnectFailedException;
import com.worldproxies.orchid.TorConfig;
import com.worldproxies.orchid.data.IPv4Address;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;

public class PendingExitStreams {
	
	private final Set<StreamExitRequest> pendingRequests;
	private final Object lock = new Object();
	private final TorConfig config;

	PendingExitStreams(TorConfig config) {
		this.config = config;
		pendingRequests = new HashSet<StreamExitRequest>();
	}
	
	Stream openExitStream(IPv4Address address, int port) throws InterruptedException, OpenFailedException {
		final StreamExitRequest request = new StreamExitRequest(lock, address, port);
		return openExitStreamByRequest(request);
	}
	
	Stream openExitStream(String hostname, int port) throws InterruptedException, OpenFailedException {
		final StreamExitRequest request =  new StreamExitRequest(lock, hostname, port);
		return openExitStreamByRequest(request);
	}
	
	private Stream openExitStreamByRequest(StreamExitRequest request) throws InterruptedException, OpenFailedException {
		if(config.getCircuitStreamTimeout() != 0) {
			request.setStreamTimeout(config.getCircuitStreamTimeout());
		}
		
		synchronized(lock) {
			pendingRequests.add(request);
			try {
				return handleRequest(request);
			} finally {
				pendingRequests.remove(request);
			}
		}
	}
	
	private Stream handleRequest(StreamExitRequest request) throws InterruptedException, OpenFailedException {
		while(true) {
			while(!request.isCompleted()) {
				lock.wait();
			}
			try {
				return request.getStream();
			} catch (TimeoutException | StreamConnectFailedException e) {
				request.resetForRetry();
			}
        }
	}
	
	List<StreamExitRequest> getUnreservedPendingRequests() {
		final List<StreamExitRequest> result = new ArrayList<StreamExitRequest>();
		synchronized (lock) {
			for(StreamExitRequest request: pendingRequests) {
				if(!request.isReserved()) {
					result.add(request);
				}
			}
		}
		return result;
	}
}
