package search;

import grabber.novel.NovelMetadata;

import java.util.ArrayList;
import java.util.List;

public class SearchResult extends NovelMetadata {
    private List<String[]> additionalInfo = new ArrayList<>();

    public SearchResult() {

    }

    public void addAdditionalInfo(String[] info) {
        additionalInfo.add(info);
    }
    public List<String[]> getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(List<String[]> additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
}
