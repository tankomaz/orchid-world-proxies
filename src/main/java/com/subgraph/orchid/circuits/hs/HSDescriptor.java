package com.subgraph.orchid.circuits.hs;

import com.subgraph.orchid.crypto.TorPublicKey;
import com.subgraph.orchid.data.HexDigest;
import com.subgraph.orchid.crypto.TorRandom;
import com.subgraph.orchid.data.Timestamp;

import java.util.ArrayList;
import java.util.List;

public class HSDescriptor {
	private final static long MS_24_HOURS = (24 * 60 * 60 * 1000);
	private final HiddenService hiddenService;
	private HexDigest descriptorId;
	private Timestamp publicationTime;
	private HexDigest secretIdPart;
	private TorPublicKey permanentKey;
	private int[] protocolVersions;
	private List<com.subgraph.orchid.circuits.hs.IntroductionPoint> introductionPoints;
	
	public HSDescriptor(HiddenService hiddenService) {
		this.hiddenService = hiddenService;
		introductionPoints = new ArrayList<com.subgraph.orchid.circuits.hs.IntroductionPoint>();
	}

	HiddenService getHiddenService() {
		return hiddenService;
	}

	void setPublicationTime(Timestamp ts) {
		this.publicationTime = ts;
	}
	
	void setSecretIdPart(HexDigest secretIdPart) {
		this.secretIdPart = secretIdPart;
	}
	
	void setDescriptorId(HexDigest descriptorId) {
		this.descriptorId = descriptorId;
	}
	
	void setPermanentKey(TorPublicKey permanentKey) {
		this.permanentKey = permanentKey;
	}
	
	void setProtocolVersions(int[] protocolVersions) {
		this.protocolVersions = protocolVersions;
	}

	void addIntroductionPoint(com.subgraph.orchid.circuits.hs.IntroductionPoint ip) {
		introductionPoints.add(ip);
	}

	HexDigest getDescriptorId() {
		return descriptorId;
	}
	
	int getVersion() {
		return 2;
	}
	
	TorPublicKey getPermanentKey() {
		return permanentKey;
	}
	
	HexDigest getSecretIdPart() {
		return secretIdPart;
	}
	
	Timestamp getPublicationTime() {
		return publicationTime;
	}
	
	int[] getProtocolVersions() {
		return protocolVersions;
	}
	
	boolean isExpired() {
		final long now = System.currentTimeMillis();
		final long then = publicationTime.getTime();
		return (now - then) > MS_24_HOURS;
	}

	List<com.subgraph.orchid.circuits.hs.IntroductionPoint> getIntroductionPoints() {
		return new ArrayList<com.subgraph.orchid.circuits.hs.IntroductionPoint>(introductionPoints);
	}
	
	List<com.subgraph.orchid.circuits.hs.IntroductionPoint> getShuffledIntroductionPoints() {
		return shuffle(getIntroductionPoints());
	}
	
	private List<com.subgraph.orchid.circuits.hs.IntroductionPoint> shuffle(List<com.subgraph.orchid.circuits.hs.IntroductionPoint> list) {
		final TorRandom r = new TorRandom();
		final int sz = list.size();
		for(int i = 0; i < sz; i++) {
			swap(list, i, r.nextInt(sz));
		}
		return list;
	}
	
	private void swap(List<com.subgraph.orchid.circuits.hs.IntroductionPoint> list, int a, int b) {
		if(a == b) {
			return;
		}
		final com.subgraph.orchid.circuits.hs.IntroductionPoint tmp = list.get(a);
		list.set(a, list.get(b));
		list.set(b, tmp);
	}
}
