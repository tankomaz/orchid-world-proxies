package com.worldproxies.orchid.directory.consensus;

import com.worldproxies.orchid.directory.parsing.NameIntegerParameter;
import com.worldproxies.orchid.crypto.TorMessageDigest;
import com.worldproxies.orchid.crypto.TorSignature;
import com.worldproxies.orchid.data.HexDigest;
import com.worldproxies.orchid.directory.parsing.DocumentFieldParser;

public class FooterSectionParser extends ConsensusDocumentSectionParser {

	private boolean seenFirstSignature = false;
	
	FooterSectionParser(DocumentFieldParser parser, ConsensusDocumentImpl document) {
		super(parser, document);
	}

	@Override
	String getNextStateKeyword() {
		return null;
	}

	@Override ConsensusDocumentParser.DocumentSection getSection() {
		return ConsensusDocumentParser.DocumentSection.FOOTER;
	}
	
	ConsensusDocumentParser.DocumentSection nextSection() {
		return ConsensusDocumentParser.DocumentSection.NO_SECTION;
	}

	@Override
	void parseLine(DocumentKeyword keyword) {
		switch(keyword) {
		case BANDWIDTH_WEIGHTS:
			processBandwidthWeights();
			break;
			
		case DIRECTORY_SIGNATURE:
			processSignature();
			break;

		default:
			break;
		}
	}

	private void doFirstSignature() {
		seenFirstSignature = true;
		fieldParser.endSignedEntity();
		final TorMessageDigest messageDigest = fieldParser.getSignatureMessageDigest();
		messageDigest.update("directory-signature ");
		document.setSigningHash(messageDigest.getHexDigest());
		
		TorMessageDigest messageDigest256 = fieldParser.getSignatureMessageDigest256();
		messageDigest256.update("directory-signature ");
		document.setSigningHash256(messageDigest256.getHexDigest());
	}
	
	private void processSignature() {
		if(!seenFirstSignature) {
			doFirstSignature();
		}
		final String s = fieldParser.parseString();
		final HexDigest identity;
		boolean useSha256 = false;
		if(s.length() < TorMessageDigest.TOR_DIGEST_SIZE) {
			useSha256 = ("sha256".equals(s));
			identity = fieldParser.parseHexDigest();
		} else {
			identity = HexDigest.createFromString(s);
		}
		HexDigest signingKey = fieldParser.parseHexDigest();
		TorSignature signature = fieldParser.parseSignature();
		document.addSignature(new DirectorySignature(identity, signingKey, signature, useSha256));
	}
	
	private void processBandwidthWeights() {
		final int remaining = fieldParser.argumentsRemaining();
		for(int i = 0; i < remaining; i++) {
			NameIntegerParameter p = fieldParser.parseParameter();
			document.addBandwidthWeight(p.getName(), p.getValue());
		}
	}
}
