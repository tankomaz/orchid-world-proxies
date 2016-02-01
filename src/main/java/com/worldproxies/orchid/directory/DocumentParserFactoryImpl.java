package com.worldproxies.orchid.directory;

import java.nio.ByteBuffer;

import com.worldproxies.orchid.ConsensusDocument;
import com.worldproxies.orchid.RouterDescriptor;
import com.worldproxies.orchid.RouterMicrodescriptor;
import com.worldproxies.orchid.directory.certificate.KeyCertificateParser;
import com.worldproxies.orchid.directory.consensus.ConsensusDocumentParser;
import com.worldproxies.orchid.directory.parsing.DocumentFieldParser;
import com.worldproxies.orchid.directory.parsing.DocumentParser;
import com.worldproxies.orchid.directory.parsing.DocumentParserFactory;
import com.worldproxies.orchid.directory.router.RouterDescriptorParser;
import com.worldproxies.orchid.directory.router.RouterMicrodescriptorParser;
import com.worldproxies.orchid.KeyCertificate;

public class DocumentParserFactoryImpl implements DocumentParserFactory {
	
	public DocumentParser<KeyCertificate> createKeyCertificateParser(ByteBuffer buffer) {
		return new KeyCertificateParser(new DocumentFieldParserImpl(buffer));
	}

	public DocumentParser<RouterDescriptor> createRouterDescriptorParser(ByteBuffer buffer, boolean verifySignatures) {
		return new RouterDescriptorParser(new DocumentFieldParserImpl(buffer), verifySignatures);
	}

	public DocumentParser<RouterMicrodescriptor> createRouterMicrodescriptorParser(ByteBuffer buffer) {
		buffer.rewind();
		DocumentFieldParser dfp = new DocumentFieldParserImpl(buffer);
		return new RouterMicrodescriptorParser(dfp);
	}

	public DocumentParser<ConsensusDocument> createConsensusDocumentParser(ByteBuffer buffer) {
		return new ConsensusDocumentParser(new DocumentFieldParserImpl(buffer));
	}
}
