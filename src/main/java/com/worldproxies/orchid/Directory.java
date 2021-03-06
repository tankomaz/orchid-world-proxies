package com.worldproxies.orchid;

import com.worldproxies.orchid.data.HexDigest;
import com.worldproxies.orchid.events.EventHandler;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * 
 * Main interface for accessing directory information and interacting
 * with directory authorities and caches.
 *
 */
public interface Directory {
	boolean haveMinimumRouterInfo();
	void loadFromStore();
	void close();
	void waitUntilLoaded();
	void storeCertificates();
	
	Collection<DirectoryServer> getDirectoryAuthorities();
	DirectoryServer getRandomDirectoryAuthority();
	void addCertificate(KeyCertificate certificate);
	Set<ConsensusDocument.RequiredCertificate> getRequiredCertificates();
	void addRouterMicrodescriptors(List<RouterMicrodescriptor> microdescriptors);
	void addRouterDescriptors(List<RouterDescriptor> descriptors);
	void addConsensusDocument(ConsensusDocument consensus, boolean fromCache);
	ConsensusDocument getCurrentConsensusDocument();
	boolean hasPendingConsensus();
	void registerConsensusChangedHandler(EventHandler handler);
	void unregisterConsensusChangedHandler(EventHandler handler);
	Router getRouterByName(String name);
	Router getRouterByIdentity(HexDigest identity);
	List<Router> getRouterListByNames(List<String> names);
	List<Router> getRoutersWithDownloadableDescriptors();
	List<Router> getAllRouters();
	
	RouterMicrodescriptor getMicrodescriptorFromCache(HexDigest descriptorDigest);
	RouterDescriptor getBasicDescriptorFromCache(HexDigest descriptorDigest);
	
	GuardEntry createGuardEntryFor(Router router);
	List<GuardEntry> getGuardEntries();
	void removeGuardEntry(GuardEntry entry);
	void addGuardEntry(GuardEntry entry);
}
