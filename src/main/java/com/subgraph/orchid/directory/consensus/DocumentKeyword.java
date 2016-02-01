package com.subgraph.orchid.directory.consensus;

enum DocumentKeyword {
	/*
	 * See dirspec.txt section 3.2
	 */
	NETWORK_STATUS_VERSION("network-status-version", ConsensusDocumentParser.DocumentSection.PREAMBLE, 1),
	VOTE_STATUS("vote-status", ConsensusDocumentParser.DocumentSection.PREAMBLE, 1),
	CONSENSUS_METHODS("consensus-methods", ConsensusDocumentParser.DocumentSection.PREAMBLE, 1, true),
	CONSENSUS_METHOD("consensus-method", ConsensusDocumentParser.DocumentSection.PREAMBLE, 1, false, true),
	PUBLISHED("published", ConsensusDocumentParser.DocumentSection.PREAMBLE, 2, true),
	VALID_AFTER("valid-after",  ConsensusDocumentParser.DocumentSection.PREAMBLE,2),
	FRESH_UNTIL("fresh-until",  ConsensusDocumentParser.DocumentSection.PREAMBLE,2),
	VALID_UNTIL("valid-until",  ConsensusDocumentParser.DocumentSection.PREAMBLE,2),
	VOTING_DELAY("voting-delay",  ConsensusDocumentParser.DocumentSection.PREAMBLE,2),
	CLIENT_VERSIONS("client-versions",  ConsensusDocumentParser.DocumentSection.PREAMBLE,1),
	SERVER_VERSIONS("server-versions",  ConsensusDocumentParser.DocumentSection.PREAMBLE,1),
	KNOWN_FLAGS("known-flags",  ConsensusDocumentParser.DocumentSection.PREAMBLE),
	PARAMS("params",  ConsensusDocumentParser.DocumentSection.PREAMBLE),
	
	DIR_SOURCE("dir-source", ConsensusDocumentParser.DocumentSection.AUTHORITY, 6),
	CONTACT("contact", ConsensusDocumentParser.DocumentSection.AUTHORITY),
	VOTE_DIGEST("vote-digest", ConsensusDocumentParser.DocumentSection.AUTHORITY, 1, false, true),
	
	R("r", ConsensusDocumentParser.DocumentSection.ROUTER_STATUS, 8),
	S("s", ConsensusDocumentParser.DocumentSection.ROUTER_STATUS),
	V("v", ConsensusDocumentParser.DocumentSection.ROUTER_STATUS),
	W("w", ConsensusDocumentParser.DocumentSection.ROUTER_STATUS, 1),
	P("p", ConsensusDocumentParser.DocumentSection.ROUTER_STATUS, 2),
	M("m", ConsensusDocumentParser.DocumentSection.ROUTER_STATUS, 1),
	
	DIRECTORY_FOOTER("directory-footer", ConsensusDocumentParser.DocumentSection.FOOTER),
	BANDWIDTH_WEIGHTS("bandwidth-weights", ConsensusDocumentParser.DocumentSection.FOOTER, 19),
	DIRECTORY_SIGNATURE("directory-signature", ConsensusDocumentParser.DocumentSection.FOOTER, 2),
	
	UNKNOWN_KEYWORD("KEYWORD NOT FOUND");
	
	
	public final static int VARIABLE_ARGUMENT_COUNT = -1;

	private final String keyword;
	private final ConsensusDocumentParser.DocumentSection section;
	private final int argumentCount;
	private final boolean voteOnly;
	private final boolean consensusOnly;
	
	
	DocumentKeyword(String keyword) {
		this(keyword, ConsensusDocumentParser.DocumentSection.NO_SECTION);
	}
	
	DocumentKeyword(String keyword, ConsensusDocumentParser.DocumentSection section) {
		this(keyword, section, VARIABLE_ARGUMENT_COUNT);
	}
	DocumentKeyword(String keyword, ConsensusDocumentParser.DocumentSection section, int argumentCount) {
		this(keyword, section, argumentCount, false);
	}
	
	DocumentKeyword(String keyword, ConsensusDocumentParser.DocumentSection section, int argumentCount, boolean voteOnly) {
		this(keyword, section, argumentCount, voteOnly, false);
	}
	
	
	DocumentKeyword(String keyword, ConsensusDocumentParser.DocumentSection section, int argumentCount, boolean voteOnly, boolean consensusOnly) {
		this.keyword = keyword;
		this.section = section;
		this.argumentCount = argumentCount;
		this.voteOnly = voteOnly;
		this.consensusOnly = consensusOnly;
	}

	static DocumentKeyword findKeyword(String keyword, ConsensusDocumentParser.DocumentSection section) {
		for(DocumentKeyword k : values()) {
			if(k.getKeyword().equals(keyword) && k.getSection().equals(section))
				return k;
		}
		return UNKNOWN_KEYWORD;
	}
	
	public String getKeyword() {
		return keyword;
	}
	
	public ConsensusDocumentParser.DocumentSection getSection() {
		return section;
	}

	public int getArgumentCount() {
		return argumentCount;
	}
	
	public boolean isConsensusOnly() {
		return consensusOnly;
	}
	
	public boolean isVoteOnly() {
		return voteOnly;
	}
	
	
}
