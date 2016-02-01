package com.subgraph.orchid.directory.consensus;

import com.subgraph.orchid.directory.parsing.DocumentFieldParser;

public class AuthoritySectionParser extends ConsensusDocumentSectionParser {

	private VoteAuthorityEntryImpl currentEntry = null;
	
	AuthoritySectionParser(DocumentFieldParser parser , ConsensusDocumentImpl document) {
		super(parser, document);
		startEntry();
	}
	
	@Override
	void parseLine(DocumentKeyword keyword) {
		switch(keyword) {
		case DIR_SOURCE:
			parseDirSource();
			break;
		case CONTACT:
			currentEntry.setContact(fieldParser.parseConcatenatedString());
			break;
		case VOTE_DIGEST:
			currentEntry.setVoteDigest(fieldParser.parseHexDigest());
			addCurrentEntry();
			break;
		default:
			break;
		}
	}
	
	private void startEntry() {
		currentEntry = new VoteAuthorityEntryImpl();
	}
	
	private void addCurrentEntry() {
		document.addVoteAuthorityEntry(currentEntry);
		startEntry();
	}

	private void parseDirSource() {
		currentEntry.setNickname(fieldParser.parseNickname());
		currentEntry.setIdentity(fieldParser.parseHexDigest());
		currentEntry.setHostname(fieldParser.parseString());
		currentEntry.setAddress(fieldParser.parseAddress());
		currentEntry.setDirectoryPort(fieldParser.parsePort());
		currentEntry.setRouterPort(fieldParser.parsePort());
	}

	@Override
	String getNextStateKeyword() {
		return "r";
	}

	@Override ConsensusDocumentParser.DocumentSection getSection() {
		return ConsensusDocumentParser.DocumentSection.AUTHORITY;
	}
	
	ConsensusDocumentParser.DocumentSection nextSection() {
		return ConsensusDocumentParser.DocumentSection.ROUTER_STATUS;
	}
}
