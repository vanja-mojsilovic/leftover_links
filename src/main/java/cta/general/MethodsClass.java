package cta.general;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.*;
import java.util.*;
import cta.general.EntryRecord;


public class MethodsClass {

    public static Map<String, String> loadWebsiteUrls(String path) throws IOException {
        Map<String, String> map = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 2);
                if (parts.length == 2) {
                    String spotId = parts[0].replace("\"", "");
                    String url = parts[1].replace("\"", "");
                    map.put(spotId, url);
                }
            }
        }
        return map;
    }

    public static Map<String, List<String>> loadCtaLinks(String path) throws IOException {
        Map<String, List<String>> map = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 3);
                if (parts.length >= 2) {
                    String spotId = parts[0].replace("\"", "");
                    String url = parts[1].replace("\"", "");
                    map.computeIfAbsent(spotId, k -> new ArrayList<>()).add(url);
                }
            }
        }
        return map;
    }

    public static String fetchHtml(String url) {
        try {
            Document doc = Jsoup.connect(url).timeout(10000).get();
            return doc.html();
        } catch (Exception e) {
            System.out.println("Jsoup failed for " + url + ": " + e.getMessage());
            return "";
        }
    }

    public static List<EntryRecord> loadEntryData(String path) throws IOException {
        List<EntryRecord> entries = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 3);
                String issueKey = parts.length > 0 ? parts[0].trim() : "";
                String spotId = parts.length > 1 ? parts[1].trim() : "";
                String websiteUrl = parts.length > 2 ? parts[2].trim() : "";
                entries.add(new EntryRecord(issueKey, spotId, websiteUrl));
            }
        }
        return entries;
    }

}
