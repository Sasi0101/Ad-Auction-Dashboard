package com.example.seg;

import java.io.File;

public class FileHistoryItem {
    // file path of the history item
    private String filePath;

    public FileHistoryItem(String filePath) {
        this.filePath = filePath;
    }

    // get the file path
    public String getFilePath() {
        return filePath;
    }

    public String getDisplayName() {
        File file = new File(filePath);
        return file.getName();
    }
}
