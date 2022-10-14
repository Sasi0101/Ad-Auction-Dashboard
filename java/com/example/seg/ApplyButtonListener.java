package com.example.seg;

import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.text.ParseException;

public interface ApplyButtonListener {
    void applyButton(int label, String filterName) throws ParseException, IOException, CsvException, InterruptedException;
}