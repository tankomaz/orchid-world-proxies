package com.worldproxies.orchid;

import java.nio.ByteBuffer;

public interface Document {
	ByteBuffer getRawDocumentBytes();
	String getRawDocumentData();
	boolean isValidDocument();
}
