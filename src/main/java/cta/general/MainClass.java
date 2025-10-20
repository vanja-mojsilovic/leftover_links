package cta.general;

import java.io.*;
import java.util.*;

public class MainClass {
    public static void main(String[] args) throws Exception {
        String basePath = "src/main/resources/";
        String entryCsv = basePath + "website_urls.csv";
        String ctaCsv = basePath + "cta_links.csv";
        String resultCsv = basePath + "result.csv";

        List<EntryRecord> entries = MethodsClass.loadEntryData(entryCsv);
        Map<String, List<String>> spotIdToCtaLinks = MethodsClass.loadCtaLinks(ctaCsv);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(resultCsv))) {
            writer.write("\"issue_key\",\"spot_id\",\"comment\"\n");

            for (EntryRecord entry : entries) {
                String issueKey = entry.issueKey;
                String spotId = entry.spotId;
                String homepageUrl = entry.websiteUrl;

                if (spotId == null || spotId.isBlank() || homepageUrl == null || homepageUrl.isBlank()) {
                    System.out.println("Skipping: " + issueKey + ", spotId=" + spotId + ", homepageUrl=" + homepageUrl);
                    writer.write(issueKey + "," + (spotId == null ? "unknown" : spotId) + ",\"skipped due to missing spot ID or website URL\"\n");
                    continue;
                }

                String html = MethodsClass.fetchHtml(homepageUrl);
                if (html.isBlank()) {
                    System.out.println("Failed to fetch HTML for " + issueKey + " (" + spotId + ")");
                    writer.write(issueKey + "," + spotId + ",\"failed to fetch homepage HTML\"\n");
                    continue;
                }

                List<String> ctaLinks = spotIdToCtaLinks.getOrDefault(spotId, Collections.emptyList());
                List<String> missingLinks = new ArrayList<>();

                for (String ctaUrl : ctaLinks) {
                    if (!html.contains(ctaUrl)) {
                        missingLinks.add(ctaUrl);
                        System.out.println("Missing CTA for " + issueKey + ": " + ctaUrl);
                    } else {
                        System.out.println("Found CTA for " + issueKey + ": " + ctaUrl);
                    }
                }

                String comment = missingLinks.isEmpty()
                        ? "no issues found"
                        : "issue found " + String.join(" ", missingLinks);
                writer.write(issueKey + "," + spotId + ",\"" + comment + "\"\n");
            }
        }

        System.out.println("Results written to " + resultCsv);
    }
}
