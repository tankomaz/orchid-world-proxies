package com.subgraph.orchid;

import java.util.List;

public class TorClientConfig {

    public static TorClientConfig getConfig() {
        return new TorClientConfig(null, null, null, null);
    }

    private List<String> exitNodes;
    private List<String> excludeNodes;
    private List<String> excludeExitNodes;
    private List<String> entryNodes;

    public TorClientConfig(List<String> exitNodes, List<String> excludeNodes, List<String> excludeExitNodes,
            List<String> entryNodes) {
        this.exitNodes = exitNodes;
        this.excludeNodes = excludeNodes;
        this.excludeExitNodes = excludeExitNodes;
        this.entryNodes = entryNodes;
    }

    public List<String> getExitNodes() {
        return exitNodes;
    }

    public void setExitNodes(List<String> exitNodes) {
        this.exitNodes = exitNodes;
    }

    public List<String> getExcludeNodes() {
        return excludeNodes;
    }

    public void setExcludeNodes(List<String> excludeNodes) {
        this.excludeNodes = excludeNodes;
    }

    public List<String> getExcludeExitNodes() {
        return excludeExitNodes;
    }

    public void setExcludeExitNodes(List<String> excludeExitNodes) {
        this.excludeExitNodes = excludeExitNodes;
    }

    public List<String> getEntryNodes() {
        return entryNodes;
    }

    public void setEntryNodes(List<String> entryNodes) {
        this.entryNodes = entryNodes;
    }

//    @Override public String toString() {
//        return "TorClientConfig{" +
//                "exitNodes=" + exitNodes +
//                ", excludeNodes=" + excludeNodes +
//                ", excludeExitNodes=" + excludeExitNodes +
//                ", entryNodes=" + entryNodes +
//                '}';
//    }
}
