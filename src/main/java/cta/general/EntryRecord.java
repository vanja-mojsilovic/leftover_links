package cta.general;

public class EntryRecord {
    public String issueKey;
    public String spotId;
    public String websiteUrl;

    public EntryRecord(String issueKey, String spotId, String websiteUrl) {
        this.issueKey = issueKey;
        this.spotId = spotId;
        this.websiteUrl = websiteUrl;
    }
}
