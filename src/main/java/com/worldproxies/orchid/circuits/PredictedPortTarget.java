package com.worldproxies.orchid.circuits;

import com.worldproxies.orchid.data.IPv4Address;
import com.worldproxies.orchid.data.exitpolicy.ExitTarget;

public class PredictedPortTarget implements ExitTarget {
	
	final int port;

	public PredictedPortTarget(int port) {
		this.port = port;
	}

	public boolean isAddressTarget() {
		return false;
	}

	public IPv4Address getAddress() {
		return new IPv4Address(0);
	}

	public String getHostname() {
		return "";
	}

	public int getPort() {
		return port;
	}
}
