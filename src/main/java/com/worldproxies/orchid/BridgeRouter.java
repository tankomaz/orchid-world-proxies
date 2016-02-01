package com.worldproxies.orchid;

import com.worldproxies.orchid.data.HexDigest;

public interface BridgeRouter extends Router {
	void setIdentity(HexDigest identity);
	void setDescriptor(RouterDescriptor descriptor);
}
