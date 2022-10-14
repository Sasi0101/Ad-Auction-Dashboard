package com.example.seg;

import java.util.ArrayList;
import java.util.List;

public class Row {
    List<String> cellValues;

    public Row(List<String> cellValues) {
        this.cellValues = new ArrayList<>(cellValues);
    }

    public String getCellValue(int index) {
        return cellValues.get(index);
    }
}

