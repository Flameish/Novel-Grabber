package search;

public class SearchProgress {
    private String sourceName;
    private SearchResult[] searchResults;

    public SearchProgress(String sourceName, SearchResult[] searchResults) {
        this.sourceName = sourceName;
        this.searchResults = searchResults;
    }

    public String getSourceName() {
        return sourceName;
    }

    public SearchResult[] getSearchResults() {
        return searchResults;
    }
}
