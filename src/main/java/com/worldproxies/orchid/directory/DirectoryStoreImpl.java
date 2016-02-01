package com.worldproxies.orchid.directory;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.worldproxies.orchid.DirectoryStore;
import com.worldproxies.orchid.Document;
import com.worldproxies.orchid.TorConfig;

public class DirectoryStoreImpl implements DirectoryStore {
	
	private final TorConfig config;
	private Map<CacheFile, DirectoryStoreFile> fileMap;

	DirectoryStoreImpl(TorConfig config) {
		this.config = config;
		this.fileMap = new HashMap<CacheFile, DirectoryStoreFile>();
	}

	public synchronized ByteBuffer loadCacheFile(CacheFile cacheFile) {
		return getStoreFile(cacheFile).loadContents();
	}
	
	public synchronized void writeData(CacheFile cacheFile, ByteBuffer data) {
		getStoreFile(cacheFile).writeData(data);
	}
	
	public synchronized void writeDocument(CacheFile cacheFile, Document document) {
		writeDocumentList(cacheFile, Arrays.asList(document));
	}
	
	public synchronized void writeDocumentList(CacheFile cacheFile, List<? extends Document> documents) {
		getStoreFile(cacheFile).writeDocuments(documents);
	}

	public synchronized void appendDocumentList(CacheFile cacheFile, List<? extends Document> documents) {
		getStoreFile(cacheFile).appendDocuments(documents);
	}
	
	public synchronized void removeCacheFile(CacheFile cacheFile) {
		getStoreFile(cacheFile).remove();
	}
	
	public synchronized void removeAllCacheFiles() {
		for(CacheFile cf: CacheFile.values()) {
			getStoreFile(cf).remove();
		}
	}
	
	private DirectoryStoreFile getStoreFile(CacheFile cacheFile) {
		if(!fileMap.containsKey(cacheFile)) {
			fileMap.put(cacheFile, new DirectoryStoreFile(config, cacheFile.getFilename()));
		}
		return fileMap.get(cacheFile);
	}	
}
