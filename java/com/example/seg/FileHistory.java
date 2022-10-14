package com.example.seg;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

public class FileHistory {
    // all file history items
    private List<FileHistoryItem> fileHistoryItems = new ArrayList<>();
    private int maxCapacity;
    private final String savedFilename = "fileHistory.txt";

    public FileHistory(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public List<FileHistoryItem> getFileHistoryItems() {
        return fileHistoryItems;
    }

    public void addHistoryItem(FileHistoryItem item) {
        // check if there are same items
        List<FileHistoryItem> removeList = new ArrayList<>();
        for (FileHistoryItem item1 : fileHistoryItems) {
            if (item1.getFilePath().equals(item.getFilePath())) {
                // add to remove list
                removeList.add(item1);
            }
        }
        // remove them all
        fileHistoryItems.removeAll(removeList);
        // add to first
        fileHistoryItems.add(0, item);
        // if the fileHistoryItems exceed the max capacity
        if (fileHistoryItems.size() > maxCapacity) {
            // remove the rest
            fileHistoryItems = fileHistoryItems.subList(0, maxCapacity);
        }
    }

    public void load() {
        File txtfile = new File(savedFilename);
        fileHistoryItems.clear();
        // check the file exists
        if (txtfile.exists()) {
            try {
                Scanner scanner = new Scanner(new File(savedFilename));
                // read record count
                int count = scanner.nextInt();
                HashSet<String> pathSet = new HashSet<>();
                scanner.nextLine();
                for (int i = 0; i < count; i++) {
                    // read filepath
                    String filePath = scanner.nextLine();
                    if (!pathSet.contains(filePath)) {
                        pathSet.add(filePath);
                        // create file history item
                        FileHistoryItem item = new FileHistoryItem(filePath);
                        // add to fileHistoryItems
                        fileHistoryItems.add(item);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void save() {
        try {
            // create print writer
            PrintWriter printWriter = new PrintWriter(savedFilename);
            // write record count
            printWriter.println(fileHistoryItems.size());
            for (FileHistoryItem item : fileHistoryItems) {
                // write each path
                printWriter.println(item.getFilePath());
            }
            // close
            printWriter.flush();
            printWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
