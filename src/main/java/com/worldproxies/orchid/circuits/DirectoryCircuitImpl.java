package com.worldproxies.orchid.circuits;

import java.util.List;
import java.util.concurrent.TimeoutException;

import com.worldproxies.orchid.DirectoryCircuit;
import com.worldproxies.orchid.Router;
import com.worldproxies.orchid.Stream;
import com.worldproxies.orchid.circuits.path.CircuitPathChooser;
import com.worldproxies.orchid.StreamConnectFailedException;
import com.worldproxies.orchid.circuits.path.PathSelectionFailedException;

public class DirectoryCircuitImpl extends CircuitImpl implements DirectoryCircuit {
	
	protected DirectoryCircuitImpl(CircuitManagerImpl circuitManager, List<Router> prechosenPath) {
		super(circuitManager, prechosenPath);
	}
	
	public Stream openDirectoryStream(long timeout, boolean autoclose) throws InterruptedException, TimeoutException,
            StreamConnectFailedException {
		final StreamImpl stream = createNewStream(autoclose);
		try {
			stream.openDirectory(timeout);
			return stream;
		} catch (Exception e) {
			removeStream(stream);
			return processStreamOpenException(e);
		}
	}

	@Override
	protected List<Router> choosePathForCircuit(CircuitPathChooser pathChooser) throws InterruptedException,
            PathSelectionFailedException {
		if(prechosenPath != null) {
			return prechosenPath;
		}
		return pathChooser.chooseDirectoryPath();
	}

	@Override
	protected String getCircuitTypeLabel() {
		return "Directory";
	}
}
