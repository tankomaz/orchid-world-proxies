package com.worldproxies.orchid.directory.parsing;

import java.nio.ByteBuffer;

import com.worldproxies.orchid.KeyCertificate;
import com.worldproxies.orchid.ConsensusDocument;
import com.worldproxies.orchid.RouterDescriptor;
import com.worldproxies.orchid.RouterMicrodescriptor;

public interface DocumentParserFactory {
	DocumentParser<RouterDescriptor> createRouterDescriptorParser(ByteBuffer buffer, boolean verifySignatures);
	
	DocumentParser<RouterMicrodescriptor> createRouterMicrodescriptorParser(ByteBuffer buffer);

	DocumentParser<KeyCertificate> createKeyCertificateParser(ByteBuffer buffer);

	DocumentParser<ConsensusDocument> createConsensusDocumentParser(ByteBuffer buffer);
}
