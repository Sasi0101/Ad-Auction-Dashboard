package com.example.seg;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to handle inputting a folder containing the input files
 */
public class LoadMultipleFiles {

    private final List<File> fileList = new ArrayList<>();

    /**
     * creates a FileFilter which returns true if input file ends in .csv
     */

    final FileFilter csvFilter = new FileFilter() {
        @Override
        public boolean accept(File file) {
            return file.getName().endsWith(".csv");
        }
    };

    /**
     * Takes in a folder and gets all the csv files from it
     * @param inFolder containing the csv files
     */
    public LoadMultipleFiles(File inFolder) {
        try {
            for (final File file : inFolder.listFiles()) {
                if (csvFilter.accept(file)) {
                    fileList.add(file);
                    System.out.println("File found: " + file.getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("LoadMultipleCampaigns instance constructed");
    }

    /**
     * Getter method for the list of files
     * @return fileList
     */
    public List<File> getFileList() {
        return fileList;
    }

}
