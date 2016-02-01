package com.worldproxies.orchid.directory.downloader;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.worldproxies.orchid.RouterDescriptor;
import com.worldproxies.orchid.data.HexDigest;
import com.worldproxies.orchid.directory.parsing.DocumentParser;

public class RouterDescriptorFetcher extends DocumentFetcher<RouterDescriptor>{

	private final List<HexDigest> fingerprints;
	
	public RouterDescriptorFetcher(Collection<HexDigest> fingerprints) {
		this.fingerprints = new ArrayList<HexDigest>(fingerprints);
	}

	@Override
	String getRequestPath() {
		return "/tor/server/d/"+ fingerprintsToRequestString();
	}

	private String fingerprintsToRequestString() {
		final StringBuilder sb = new StringBuilder();
		for(HexDigest fp: fingerprints) {
			appendFingerprint(sb, fp);
		}
		return sb.toString();
	}
	private void appendFingerprint(StringBuilder sb, HexDigest fp) {
		if(sb.length() > 0) {
			sb.append("+");
		}
		sb.append(fp.toString());
	}
	
	@Override
	DocumentParser<RouterDescriptor> createParser(ByteBuffer response) {
		return PARSER_FACTORY.createRouterDescriptorParser(response, true);
	}
}
