package com.worldproxies.orchid;

import java.util.List;
import java.util.Set;

import com.worldproxies.orchid.data.HexDigest;
import com.worldproxies.orchid.directory.downloader.DirectoryRequestFailedException;

public interface DirectoryDownloader {
	void start(Directory directory);
	void stop();
	
	RouterDescriptor downloadBridgeDescriptor(Router bridge) throws DirectoryRequestFailedException;
	
	ConsensusDocument downloadCurrentConsensus(boolean useMicrodescriptors) throws DirectoryRequestFailedException;
	ConsensusDocument downloadCurrentConsensus(boolean useMicrodescriptors, DirectoryCircuit circuit) throws DirectoryRequestFailedException;
	
	List<KeyCertificate> downloadKeyCertificates(Set<ConsensusDocument.RequiredCertificate> required) throws DirectoryRequestFailedException;
	List<KeyCertificate> downloadKeyCertificates(Set<ConsensusDocument.RequiredCertificate> required, DirectoryCircuit circuit) throws DirectoryRequestFailedException;
	
	List<RouterDescriptor> downloadRouterDescriptors(Set<HexDigest> fingerprints) throws DirectoryRequestFailedException;
	List<RouterDescriptor> downloadRouterDescriptors(Set<HexDigest> fingerprints, DirectoryCircuit circuit) throws DirectoryRequestFailedException;
	
	List<RouterMicrodescriptor> downloadRouterMicrodescriptors(Set<HexDigest> fingerprints) throws DirectoryRequestFailedException;
	List<RouterMicrodescriptor> downloadRouterMicrodescriptors(Set<HexDigest> fingerprints, DirectoryCircuit circuit) throws DirectoryRequestFailedException;
}
