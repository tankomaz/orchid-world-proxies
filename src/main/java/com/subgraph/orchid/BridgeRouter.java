package com.subgraph.orchid;

import com.subgraph.orchid.data.HexDigest;

public interface BridgeRouter extends com.subgraph.orchid.Router {
	void setIdentity(HexDigest identity);
	void setDescriptor(com.subgraph.orchid.RouterDescriptor descriptor);
}
