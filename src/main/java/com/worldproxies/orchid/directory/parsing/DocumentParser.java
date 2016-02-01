package com.worldproxies.orchid.directory.parsing;


public interface DocumentParser<T> {
	boolean parse(DocumentParsingResultHandler<T> resultHandler);
	DocumentParsingResult<T> parse();
}
