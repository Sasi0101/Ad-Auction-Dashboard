package com.example.seg;

import com.opencsv.exceptions.CsvException;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class ClickCostHistory {

    public BarChart<String, Number> getBarChart (ClickLog csvFile) throws  CsvException {

        //Check if our csv file is not null
        if (csvFile == null) throw new CsvException("The file is empty");

        int lengthOfData = csvFile.getLength();
        int max = 0;
        double[] frequency = new double[lengthOfData];
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String, Number> barChart = new BarChart<>(xAxis,yAxis);
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        //Filling up the frequency array with the number of clicks which costs between i and i+1
        for(int i=0; i< lengthOfData; i++){
            if ((int)Math.floor(Double.parseDouble(csvFile.getCell(2,i))) > max)
                max = (int)Math.floor(Double.parseDouble(csvFile.getCell(2,i)));
            frequency[(int)Math.floor(Double.parseDouble(csvFile.getCell(2,i)))] ++;
        }

        //Filling up the series with data
        for (int i=0; i<max; i++){
            series.getData().add(new XYChart.Data<>("£" + i + "-" + (i + 1), frequency[i]));
        }

        //Making the barchart and adding the data to it
        barChart.setTitle("Histogram of click cost");
        barChart.setCategoryGap(0);
        barChart.setBarGap(3);
        series.setName("Accumulated Data");
        xAxis.setLabel("Cost");
        yAxis.setLabel("Frequency");
        barChart.getData().add(series);
        barChart.getStyleClass().add("text-item");
        return barChart;
    }
}