package com.example.seg;

import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.util.*;

public class ImpressionLog extends DataFile {
    private final Map<String, Integer> headers = new HashMap<String, Integer>();

    /**
     * Takes a filename to initialise data.
     *
     * @param filename filename or filepath
     * @throws IOException  if file not found
     * @throws CsvException if not separated properly
     */
    public ImpressionLog(String filename) throws IOException, CsvException {
        super(filename);
        headers.put("date",0);
        headers.put("id",1);
        headers.put("gender",2);
        headers.put("age",3);
        headers.put("income",4);
        headers.put("context",5);
    }
    public ImpressionLog (ImpressionLog toCopy){
        super(toCopy);
    }

    /**
     * Get column as string array using its name.
     * @param name String: date, id, gender, age, income, context
     * @return String array of column
     */
    public String[] getColumn(String name){
        //Init a new list, append i-th element in each row to list and return list as array.
        List<String> column = new ArrayList<>();
        for(String[] row : data){
            column.add(row[headers.get(name.toLowerCase())]);
        }
        return (String[]) column.toArray();
    }

    //Owen made this cos he's lazy
    public static ImpressionLog splitFile(int startRow, int endRow, ImpressionLog toCopy) throws IOException, CsvException {
        ImpressionLog newFile = new ImpressionLog(toCopy);
        newFile.data = newFile.data.subList(startRow, endRow);
        newFile.length = newFile.data.size();
        return newFile;
    }

}
