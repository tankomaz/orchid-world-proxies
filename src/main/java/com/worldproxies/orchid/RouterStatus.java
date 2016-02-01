package com.worldproxies.orchid;

import com.worldproxies.orchid.data.HexDigest;
import com.worldproxies.orchid.data.IPv4Address;
import com.worldproxies.orchid.data.exitpolicy.ExitPorts;
import com.worldproxies.orchid.data.Timestamp;

public interface RouterStatus {
	String getNickname();
	HexDigest getIdentity();
	HexDigest getDescriptorDigest();
	HexDigest getMicrodescriptorDigest();
	Timestamp getPublicationTime();
	IPv4Address getAddress();
	int getRouterPort();
	boolean isDirectory();
	int getDirectoryPort();
	boolean hasFlag(String flag);
	String getVersion();
	boolean hasBandwidth();
	int getEstimatedBandwidth();
	int getMeasuredBandwidth();
	ExitPorts getExitPorts();
}
