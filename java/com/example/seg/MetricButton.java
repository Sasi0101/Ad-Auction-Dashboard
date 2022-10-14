package com.example.seg;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

import java.awt.event.ActionEvent;

public class MetricButton extends VBox {

    private final Label metric;

    public MetricButton(String title, float number){
        setAlignment(Pos.CENTER);
        getStyleClass().add("metricListItem");
        Label name = new Label(title);
        name.getStyleClass().add("text-item");
        String text = String.valueOf(number);
        if(text.endsWith(".0")) {
            text = text.replace(".0", "");
        }
        metric = new Label(text);
        metric.getStyleClass().add("number");
        getChildren().add(name);
        getChildren().add(metric);
        setAction(e -> {
            System.out.println("Button Pressed");
            // Graph code here
        });
    }
    public void setAction(EventHandler<? super MouseEvent> var1){
        setOnMouseClicked(var1);
    }

    /**
     * method to change the value of a Metric button
     * @param number new number to be displayed
     */
    public void changeMetric(float number){
        String text = String.valueOf(number);
        if(text.endsWith(".0")) {
            text = text.replace(".0", "");
        }
        metric.setText(text);
    }
}
