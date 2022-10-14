package com.example.seg;

import com.example.seg.DataFile;
import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerLog extends DataFile {
    private final Map<String, Integer> headers = new HashMap<String, Integer>();

    /**
     * Takes a filename to initialise data.
     *
     * @param filename filename or filepath
     * @throws IOException  if file not found
     * @throws CsvException if not separated properly
     */
    public ServerLog(String filename) throws IOException, CsvException {
        super(filename);
        headers.put("entry date",0);
        headers.put("id",1);
        headers.put("exit date",2);
        headers.put("pages viewed",3);
        headers.put("conversion",4);
    }

    public ServerLog (ServerLog toCopy){
        super(toCopy);
    }

    /**
     * Get column as string array using its name.
     * @param name String: entry date, id, exit date, pages viewed, conversion
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
    public static ServerLog splitFile(int startRow, int endRow, ServerLog toCopy) throws IOException, CsvException {
        ServerLog newFile = new ServerLog(toCopy);
        newFile.data = newFile.data.subList(startRow, endRow);
        newFile.length = newFile.data.size();
        return newFile;
    }

    //JMA made this
    public void deleteRow(int index) {
        this.data.remove(index);
    }

    public void clean_data(ImpressionLog impressionLog) {
        String[] impressionLogIDs = impressionLog.getColumn(1);
        String[] theseIDs = this.getColumn(1);
        int deletedAmount = 0;
        int currentIndex = 0;
        for (int pointer = 0; pointer < theseIDs.length; pointer ++) {
            int counter = 0;
            boolean found = false;
            while(counter != impressionLogIDs.length) {
                if(impressionLogIDs[currentIndex].equals(theseIDs[pointer])) {
                    found = true;
                    break;
                }
                currentIndex ++;
                if(currentIndex == impressionLogIDs.length) {
                    currentIndex = 0;
                }
                counter ++;
            }
            if(!found){
                this.deleteRow(pointer - deletedAmount);
                deletedAmount ++;
            }
        }
    }

}
