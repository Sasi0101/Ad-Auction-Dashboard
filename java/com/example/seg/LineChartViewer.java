package com.example.seg;

import javafx.scene.chart.*;
import javafx.scene.layout.BorderPane;

import java.awt.geom.Line2D;

public class LineChartViewer extends BorderPane {

    XYChart linechart;
    CategoryAxis dateaxis = new CategoryAxis();
    Axis yaxis;
    XYChart.Series currentSer;

    public LineChartViewer(XYChart.Series series1, String yaxisName){
        currentSer = series1;
        dateaxis.setLabel("Date/Time");
        yaxis = new NumberAxis();
        yaxis.setLabel(yaxisName);
        linechart = new LineChart(dateaxis, yaxis);
        linechart.getData().add(currentSer);
        this.setCenter(linechart);

        /*        //test
        XYChart.Series series1 = new XYChart.Series();
        series1.getData().add(new XYChart.Data<>(1, 200));
        series1.getData().add(new XYChart.Data<>(2, 400));
        series1.getData().add(new XYChart.Data<>(3, 500));
        LineChart test = new LineChart(dateaxis, new NumberAxis());
        test.getData().add(series1);
        this.setCenter(test);
        //test end*/
    }

    public void setSeriesName(String name){
        currentSer.setName(name);
    }

    public XYChart getLineChart() { //for saving
        return this.linechart;
    }
}
