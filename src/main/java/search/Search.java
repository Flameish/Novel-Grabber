package search;

import grabber.sources.SourceException;
import grabber.sources.Sources;
import system.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Search {
    private final String searchTerm;
    private final Map<String, SearchResult[]> searchResultsBySourceName = new ConcurrentHashMap<>();
    private final List<SearchListener> searchListeners = new ArrayList<>();

    public Search(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public Map<String, SearchResult[]> execute() {
        Logger.verbose("Starting global search: " + searchTerm);
        searchResultsBySourceName.clear();
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        try {
            Sources.getSearchableSources().forEach((className, source) -> executor.execute(() -> {
                try {
                    String sourceName =  source.getName();
                    Logger.verbose(String.format("Searching %s...", sourceName));
                    SearchResult[] searchResults = source.search(searchTerm);
                    Logger.verbose(String.format("%s: %d results", sourceName, searchResults.length));
                    if (searchResults.length > 0) {
                        searchResultsBySourceName.put(sourceName, searchResults);
                        for (SearchListener listener : searchListeners) {
                            listener.update(new SearchProgress(sourceName, searchResults));
                        }
                    }
                } catch (SourceException sourceException) {
                    Logger.error(sourceException.getMessage());
                    sourceException.printStackTrace();
                }
            }));
        } catch(Exception err) {
            err.printStackTrace();
        }
        awaitTerminationAfterShutdown(executor);
        Logger.verbose(String.format("Search complete. %d total results.", searchResultsBySourceName.size()));
        return searchResultsBySourceName;
    }

    public void awaitTerminationAfterShutdown(ExecutorService threadPool) {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException ex) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public Map<String, SearchResult[]> getResults() {
        return searchResultsBySourceName;
    }


    public SearchResult[] getResultsForSource(String name) {
        return searchResultsBySourceName.get(name);
    }


    public void addSearchListener(SearchListener searchListener) {
        searchListeners.add(searchListener);
    }

    public String getSearchTerm() {
        return searchTerm;
    }
}
