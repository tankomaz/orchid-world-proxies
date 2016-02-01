package com.worldproxies.orchid;

import com.worldproxies.orchid.crypto.TorPublicKey;
import com.worldproxies.orchid.data.HexDigest;
import com.worldproxies.orchid.data.IPv4Address;

import java.util.Set;

public interface Router {

	String getNickname();
	String getCountryCode();
	IPv4Address getAddress();
	int getOnionPort();
	int getDirectoryPort();
	TorPublicKey getIdentityKey();
	HexDigest getIdentityHash();
	boolean isDescriptorDownloadable();

	String getVersion();
	Descriptor getCurrentDescriptor();
	HexDigest getDescriptorDigest();
	HexDigest getMicrodescriptorDigest();

	TorPublicKey getOnionKey();
	byte[] getNTorOnionKey();
	
	boolean hasBandwidth();
	int getEstimatedBandwidth();
	int getMeasuredBandwidth();

	Set<String> getFamilyMembers();
	int getAverageBandwidth();
	int getBurstBandwidth();
	int getObservedBandwidth();
	boolean isHibernating();
	boolean isRunning();
	boolean isValid();
	boolean isBadExit();
	boolean isPossibleGuard();
	boolean isExit();
	boolean isFast();
	boolean isStable();
	boolean isHSDirectory();
	boolean exitPolicyAccepts(IPv4Address address, int port);
	boolean exitPolicyAccepts(int port);
}
