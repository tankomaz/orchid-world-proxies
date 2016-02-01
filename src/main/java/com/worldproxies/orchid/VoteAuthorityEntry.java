package com.worldproxies.orchid;

import java.util.List;

import com.worldproxies.orchid.data.HexDigest;
import com.worldproxies.orchid.data.IPv4Address;
import com.worldproxies.orchid.directory.consensus.DirectorySignature;

public interface VoteAuthorityEntry {
	String getNickname();
	HexDigest getIdentity();
	String getHostname();
	IPv4Address getAddress();
	int getDirectoryPort();
	int getRouterPort();
	String getContact();
	HexDigest getVoteDigest();
	List<DirectorySignature> getSignatures();
}
