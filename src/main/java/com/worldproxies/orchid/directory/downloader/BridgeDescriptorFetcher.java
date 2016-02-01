package com.worldproxies.orchid.directory.downloader;

import java.nio.ByteBuffer;

import com.worldproxies.orchid.RouterDescriptor;
import com.worldproxies.orchid.directory.parsing.DocumentParser;

public class BridgeDescriptorFetcher extends DocumentFetcher<RouterDescriptor>{

	@Override
	String getRequestPath() {
		return "/tor/server/authority";
	}

	@Override
	DocumentParser<RouterDescriptor> createParser(ByteBuffer response) {
		return PARSER_FACTORY.createRouterDescriptorParser(response, true);
	}
}
