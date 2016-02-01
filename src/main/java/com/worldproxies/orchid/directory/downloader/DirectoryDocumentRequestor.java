package com.worldproxies.orchid.directory.downloader;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import com.worldproxies.orchid.CircuitManager;
import com.worldproxies.orchid.DirectoryCircuit;
import com.worldproxies.orchid.KeyCertificate;
import com.worldproxies.orchid.Router;
import com.worldproxies.orchid.RouterDescriptor;
import com.worldproxies.orchid.RouterMicrodescriptor;
import com.worldproxies.orchid.Stream;
import com.worldproxies.orchid.Tor;
import com.worldproxies.orchid.data.HexDigest;
import com.worldproxies.orchid.ConsensusDocument;
import com.worldproxies.orchid.StreamConnectFailedException;
import com.worldproxies.orchid.circuits.TorInitializationTracker;

/**
 * Synchronously downloads directory documents.
 */
public class DirectoryDocumentRequestor {
	private final static int OPEN_DIRECTORY_STREAM_TIMEOUT = 10 * 1000;
	
	private final DirectoryCircuit circuit;
	private final TorInitializationTracker initializationTracker;
	
	
	public DirectoryDocumentRequestor(DirectoryCircuit circuit) {
		this(circuit, null);
	}

	public DirectoryDocumentRequestor(DirectoryCircuit circuit, TorInitializationTracker initializationTracker) {
		this.circuit = circuit;
		this.initializationTracker = initializationTracker;
	}

	public RouterDescriptor downloadBridgeDescriptor(Router bridge) throws DirectoryRequestFailedException {
		return fetchSingleDocument(new BridgeDescriptorFetcher());
	}

	public ConsensusDocument downloadCurrentConsensus(boolean useMicrodescriptors) throws DirectoryRequestFailedException {
		return fetchSingleDocument(new ConsensusFetcher(useMicrodescriptors), CircuitManager.DIRECTORY_PURPOSE_CONSENSUS);
	}

	public List<KeyCertificate> downloadKeyCertificates(Set<ConsensusDocument.RequiredCertificate> required) throws DirectoryRequestFailedException {
		return fetchDocuments(new CertificateFetcher(required), CircuitManager.DIRECTORY_PURPOSE_CERTIFICATES);
	}

	public List<RouterDescriptor> downloadRouterDescriptors(Set<HexDigest> fingerprints) throws DirectoryRequestFailedException {
		return fetchDocuments(new RouterDescriptorFetcher(fingerprints), CircuitManager.DIRECTORY_PURPOSE_DESCRIPTORS);
	}

	public List<RouterMicrodescriptor> downloadRouterMicrodescriptors(Set<HexDigest> fingerprints) throws DirectoryRequestFailedException  {
		return fetchDocuments(new MicrodescriptorFetcher(fingerprints), CircuitManager.DIRECTORY_PURPOSE_DESCRIPTORS);
	}
	
	private <T> T fetchSingleDocument(DocumentFetcher<T> fetcher) throws DirectoryRequestFailedException {
		return fetchSingleDocument(fetcher, 0);
	}

	private <T> T fetchSingleDocument(DocumentFetcher<T> fetcher, int purpose) throws DirectoryRequestFailedException {
		final List<T> result = fetchDocuments(fetcher, purpose);
		if(result.size() == 1) {
			return result.get(0);
		}
		return null;
	}
	
	private <T> List<T> fetchDocuments(DocumentFetcher<T> fetcher, int purpose) throws DirectoryRequestFailedException {
		try {
			final HttpConnection http = createHttpConnection(purpose);
			try {
				return fetcher.requestDocuments(http);
			} finally {
				http.close();
			}
		} catch (TimeoutException e) {
			throw new DirectoryRequestFailedException("Directory request timed out");
		} catch (StreamConnectFailedException e) {
			throw new DirectoryRequestFailedException("Failed to open directory stream", e);
		} catch (IOException e) {
			throw new DirectoryRequestFailedException("I/O exception processing directory request", e);
		} catch (InterruptedException e) {
			throw new DirectoryRequestFailedException("Directory request interrupted");
		} 
	}
	
	private HttpConnection createHttpConnection(int purpose) throws InterruptedException, TimeoutException, StreamConnectFailedException {
		return new HttpConnection(openDirectoryStream(purpose));
	}

	private Stream openDirectoryStream(int purpose) throws InterruptedException, TimeoutException, StreamConnectFailedException {
		final int requestEventCode = purposeToEventCode(purpose, false);
		final int loadingEventCode = purposeToEventCode(purpose, true);
		
		notifyInitialization(requestEventCode);

		final Stream stream = circuit.openDirectoryStream(OPEN_DIRECTORY_STREAM_TIMEOUT, true);
		notifyInitialization(loadingEventCode);
		return stream;
	}
	
	private int purposeToEventCode(int purpose, boolean getLoadingEvent) {
		switch(purpose) {
		case CircuitManager.DIRECTORY_PURPOSE_CONSENSUS:
			return getLoadingEvent ? Tor.BOOTSTRAP_STATUS_LOADING_STATUS : Tor.BOOTSTRAP_STATUS_REQUESTING_STATUS;
		case CircuitManager.DIRECTORY_PURPOSE_CERTIFICATES:
			 return getLoadingEvent ? Tor.BOOTSTRAP_STATUS_LOADING_KEYS : Tor.BOOTSTRAP_STATUS_REQUESTING_KEYS;
		case CircuitManager.DIRECTORY_PURPOSE_DESCRIPTORS:
			return getLoadingEvent ? Tor.BOOTSTRAP_STATUS_LOADING_DESCRIPTORS : Tor.BOOTSTRAP_STATUS_REQUESTING_DESCRIPTORS;
		default:
			return 0;
		}
	}
	
	private void notifyInitialization(int code) {
		if(code > 0 && initializationTracker != null) {
			initializationTracker.notifyEvent(code);
		}
	}
}
