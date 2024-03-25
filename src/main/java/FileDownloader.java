import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileDownloader {
    public static void main(String[] args) {
        String url = "https://cdn.cryptiiiic.com/bin/Windows/x86_64/futurerestore/"; // Replace with the actual URL
        List<FileInfo> files = extractFilesFromWebpage(url);
        if (!files.isEmpty()) {
            FileInfo newestFile = getNewestFile(files);
            if (newestFile != null) {
                System.out.println("Newest file: " + newestFile.getName());
                System.out.println("Timestamp: " + newestFile.getTimestamp());
                System.out.println("File size: " + newestFile.getSize() + " bytes");
                System.out.println("Download URL: " + url + newestFile.getUrl());
            } else {
                System.out.println("No files found.");
            }
        } else {
            System.out.println("No files found on the webpage.");
        }
    }

    private static List<FileInfo> extractFilesFromWebpage(String urlString) {
        List<FileInfo> files = new ArrayList<>();
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                // Extract file names and URLs using regex
                String input = line;

                String regex = "<a href=\"(.*?)\">(.*?)<\\/a>\\s+(\\d{2}-\\w{3}-\\d{4}\\s+\\d{2}:\\d{2})\\s+(\\d+)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(input);

                if (matcher.matches()) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm");
                    
                    String fileUrl = matcher.group(1);
                    String fileName = matcher.group(2);
                    String timestamp = matcher.group(3);
                    String fileSize = matcher.group(4);

                    System.out.println("URL: " + fileUrl);
                    System.out.println("Filename: " + fileName);
                    System.out.println("Timestamp: " + timestamp);
                    System.out.println("File Size: " + fileSize);
                    System.out.println("");

                    if (!fileName.equals("../")) 
                    {
                        long fileSizeNumber = Long.parseLong(fileSize);
                        LocalDateTime dateTime1 = LocalDateTime.parse(timestamp, formatter);
                        files.add(new FileInfo(fileName, fileUrl, dateTime1, fileSizeNumber));
                    }
                }
            }
            reader.close();
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return files;
    }

    private static FileInfo getNewestFile(List<FileInfo> files) {
        FileInfo newestFile = null;
        for (FileInfo file : files) {
            LocalDateTime dateTime1 = file.getTimestamp();
            if (newestFile == null) {
                System.out.println("Newest time set to " + dateTime1 + ", newest file set as " + file.getName());
                newestFile = file;
            }
            else if (newestFile != null)
            {
                LocalDateTime dateTime2 = newestFile.getTimestamp();
                if (dateTime1.isBefore(dateTime2)) {
                    System.out.println(dateTime1 + " is before " + dateTime2 + ", discard " + file.getName() + ", new value is unchanged.");
                } 
                else if (dateTime1.isAfter(dateTime2)) 
                {
                    System.out.println(dateTime1 + " is after " + dateTime2 + ", discard " + newestFile.getName() + ", new value is overwritten.");
                    newestFile = file;
                } 
                else {
                    System.out.println(dateTime1 + " is equal to " + dateTime2 + ", discard " + file.getName() + ", new value is unchanged.");
                }
            }
        }
        System.out.println("");
        return newestFile;
    }

    private static class FileInfo {
        private final String name;
        private final String url;
        private final LocalDateTime timestamp;
        private final long size;

        public FileInfo(String name, String url, LocalDateTime timestamp, long size) {
            this.name = name;
            this.url = url;
            this.timestamp = timestamp;
            this.size = size;
        }

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public long getSize() {
            return size;
        }
    }
}
