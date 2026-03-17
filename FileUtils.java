package com.dmd.framework.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileUtils {
    private static final Logger logger = LogManager.getLogger(FileUtils.class);

    public static void createDirectoryIfNotExists(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                logger.info("Directory created: " + path);
            } else {
                logger.error("Failed to create directory: " + path);
            }
        }
    }

    public static void writeToFile(String filePath, String content) {
        try {
            File file = new File(filePath);
            createDirectoryIfNotExists(file.getParent());
            
            try (FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.write(content);
                fileWriter.flush();
                logger.info("Content written successfully to: " + filePath);
            }
        } catch (IOException e) {
            logger.error("Failed to write to file: " + filePath, e);
            throw new RuntimeException("Failed to write to file: " + filePath, e);
        }
    }

    public static void appendToFile(String filePath, String content) {
        try {
            File file = new File(filePath);
            createDirectoryIfNotExists(file.getParent());
            
            try (FileWriter fileWriter = new FileWriter(file, true)) {
                fileWriter.append(content);
                fileWriter.append("\n");
                fileWriter.flush();
                logger.info("Content appended successfully to: " + filePath);
            }
        } catch (IOException e) {
            logger.error("Failed to append to file: " + filePath, e);
            throw new RuntimeException("Failed to append to file: " + filePath, e);
        }
    }

    public static String getTimestampedFileName(String baseFileName) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String timestamp = LocalDateTime.now().format(formatter);
        return baseFileName.replace(".txt", "_" + timestamp + ".txt")
                          .replace(".json", "_" + timestamp + ".json")
                          .replace(".html", "_" + timestamp + ".html");
    }
}
