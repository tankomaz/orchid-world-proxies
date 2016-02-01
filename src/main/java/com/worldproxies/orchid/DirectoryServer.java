package com.worldproxies.orchid;

import java.util.List;

import com.worldproxies.orchid.data.HexDigest;

/**
 * Represents a directory authority server or a directory cache.
 */
public interface DirectoryServer extends Router {
	int getDirectoryPort();
	boolean isV2Authority();
	boolean isV3Authority();
	HexDigest getV3Identity();
	boolean isHiddenServiceAuthority();
	boolean isBridgeAuthority();
	boolean isExtraInfoCache();
	
	KeyCertificate getCertificateByFingerprint(HexDigest fingerprint);
	List<KeyCertificate> getCertificates();
	void addCertificate(KeyCertificate certificate);
}
