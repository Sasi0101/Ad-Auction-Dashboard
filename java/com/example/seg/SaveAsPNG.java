package com.example.seg;

import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.chart.XYChart;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SaveAsPNG {

    //BarChart
    static void saveBarChart(BarChart<String, Number> barChart, String filePath) throws IOException {

        BufferedImage test = SwingFXUtils.fromFXImage(barChart.snapshot(new SnapshotParameters(),null), null);
        ImageIO.write(test,"PNG", new File(filePath));
        System.out.println("PNG Saved");

    }

    //XYChart
    static void saveLineChart(XYChart lineChart, String filePath) throws IOException {
        BufferedImage image = SwingFXUtils.fromFXImage(lineChart.snapshot(new SnapshotParameters(), null), null);
        ImageIO.write(image, "PNG", new File(filePath));
        System.out.print("LineChart was saved");
    }

}
