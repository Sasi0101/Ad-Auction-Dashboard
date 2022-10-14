package com.example.seg;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads a file from the given directory.
 * Access data using methods.
 * data is in from List<String[]>, a list of rows
 */
public class DataFile {
    protected List<String[]> data;
    protected final int width;
    protected int length;
    protected final String name;
    protected final String filename;

    /**
     * Takes a filename to initialise data.
     * @param filename filename or filepath
     * @throws IOException if file not found
     * @throws CsvException if not separated properly
     */
    public DataFile(String filename) throws IOException, CsvException {
        // Read csv using filename and init length and width
        this.filename = filename;
        Reader reader = new FileReader(filename);
        int pathNum = filename.split("\\\\").length;
        String[] pathStrings = filename.split("\\\\");
        this.name = pathStrings[pathNum-1];
        this.data = read_csv(reader);
        this.data.remove(0);
        this.width = data.get(0).length;
        this.length = data.size();
    }

    public DataFile (DataFile toCopy){
        this.name = "copy_" + toCopy.name;
        this.data = toCopy.data;
        this.width = toCopy.width;
        this.length = toCopy.length;
        this.filename = toCopy.filename;
    }

    /**
     * Get a row given an index
     * @param i index
     * @return row as string array
     */
    public String[] getRow(int i){
        return data.get(i);
    }

    /**
     * Get a column given an index
     * @param i index
     * @return column as string array
     */
    public String[] getColumn(int i){
        // Init a new list, append i-th element in each row to list and return list as array.
        String[] col = new String[this.length];
        for (int j = 0; j < this.length; j++) {
            col[j] = data.get(j)[i];
        }
        return col;
    }

    /**
     * Get cell at x,y position (column, row)
     * @param x column
     * @param y row
     * @return cell as String
     */
    public String getCell(int x, int y){
        return data.get(y)[x];
    }

    /**
     * Get data list - list of rows each containing a string array
     * @return List<String[]>
     */
    public List<String[]> getData() {
        return data;
    }

    /**
     * Takes a reader and returns the list of string arrays containing the file data
     * @param reader the reader
     * @return data
     * @throws IOException if file not found
     * @throws CsvException if not separated properly
     */
    private List<String[]> read_csv(Reader reader) throws IOException, CsvException {
        CSVReader csv_reader = new CSVReader(reader);
        List<String[]> list = csv_reader.readAll();
        csv_reader.close();
        reader.close();
        return new ArrayList<>(list);
    }

    public int getLength() { return data.size(); }
    //public int getWidth() { return width; }
    public String getName() { return name; }
    //public String getFilename() {return filename;}

}
