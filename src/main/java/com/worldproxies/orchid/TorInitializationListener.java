package com.worldproxies.orchid;

public interface TorInitializationListener {
	void initializationProgress(String message, int percent);
	void initializationCompleted();
}
