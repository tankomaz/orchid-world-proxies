package com.worldproxies.orchid.data.exitpolicy;

import com.worldproxies.orchid.data.IPv4Address;

public interface ExitTarget {
	boolean isAddressTarget();
	IPv4Address getAddress();
	String getHostname();
	int getPort();
}
