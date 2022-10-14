package com.example.seg;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Read csv data from file
 */
public class CSVDataReader {
    private List<String> columns = new ArrayList<>();
    private List<Row> rows = new ArrayList<>();

    public CSVDataReader() {
    }

    public void readCsv(DataFile dataFile) {
        try {
            List<String> values = new ArrayList<>();
            // create a scanner
            Scanner scanner = new Scanner(new File(dataFile.filename));
            // read head line
            String line = scanner.nextLine();
            String[] parts = line.split(",");
            // read columns
            columns.addAll(Arrays.asList(parts));
            scanner.close();

            for (int i = 0; i < dataFile.getLength() && i < 1000000; i++) {
                values.clear();
                parts = dataFile.getRow(i);
                Collections.addAll(values, parts);
                // add to rows
                rows.add(new Row(values));
            }
        }catch (FileNotFoundException e) {
        }
    }
    /**
     * Read csv data from file
     * @param filename filename of the csv file
     */
    public void readCsv(String filename) {
        try {
            List<String> values = new ArrayList<>();
            // create a scanner
            Scanner scanner = new Scanner(new File(filename));
            // read head line
            String line = scanner.nextLine();
            String[] parts = line.split(",");
            // read columns
            columns.addAll(Arrays.asList(parts));
            while (scanner.hasNextLine()) {
                values.clear();
                // read data row
                line = scanner.nextLine();
                parts = line.split(",");
                if (parts.length > 0) {
                    // read cell values
                    Collections.addAll(values, parts);
                    // add to rows
                    rows.add(new Row(values));
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
        }
    }

    public List<String> getColumns() {
        return columns;
    }

    public List<Row> getRows() {
        return rows;
    }
}
