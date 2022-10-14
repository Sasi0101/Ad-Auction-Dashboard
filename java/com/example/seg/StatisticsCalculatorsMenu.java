package com.example.seg;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class StatisticsCalculatorsMenu {

    /**
     * Menu Screen to choose a statistics calculator
     */
    public StatisticsCalculatorsMenu() {
        //creating the scene and stage
        Font.loadFont(getClass().getResourceAsStream("Fredoka.ttf"), 32);
        BorderPane pane = new BorderPane();
        Scene scene = new Scene(pane,800,200);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        Stage stage = new Stage();
        stage.setTitle("Statistics Calculators");
        stage.setScene(scene);
        stage.show();
        stage.setResizable(false);

        //creating the elements
        Label statsLabel = new Label("Statistics Menu");
        Button BinomialPD = new Button("Binomial Probability Distribution");
        Button BinomialCD = new Button("Binomial Cumulative Distribution");
        Button PoissonPD = new Button("Poisson Probability Distribution");
        Button PoissonCD = new Button("Poisson Cumulative Distribution");

        //adding styling
        statsLabel.getStyleClass().add("statsLabel");
        BinomialPD.getStyleClass().add("menuItem");
        BinomialCD.getStyleClass().add("menuItem");
        PoissonPD.getStyleClass().add("menuItem");
        PoissonCD.getStyleClass().add("menuItem");

        //creating regions to help with spacing
        Region statsLabelL = new Region();
        Region statsLabelR = new Region();
        HBox.setHgrow(statsLabelL,Priority.ALWAYS);
        HBox.setHgrow(statsLabelR,Priority.ALWAYS);
        Region binomialL = new Region();
        Region binomialM = new Region();
        Region binomialR = new Region();
        HBox.setHgrow(binomialL, Priority.ALWAYS);
        HBox.setHgrow(binomialM,Priority.ALWAYS);
        HBox.setHgrow(binomialR,Priority.ALWAYS);
        Region poissonL = new Region();
        Region poissonM = new Region();
        Region poissonR = new Region();
        HBox.setHgrow(poissonL,Priority.ALWAYS);
        HBox.setHgrow(poissonM,Priority.ALWAYS);
        HBox.setHgrow(poissonR,Priority.ALWAYS);

        //adding functionality to the buttons
        BinomialPD.setOnAction(e -> {
            com.example.seg.BinomialPD binomialPDScreen = new BinomialPD();
            System.out.println("Binomial Probability Distribution clicked");
        });
        BinomialCD.setOnAction(e -> {
            com.example.seg.BinomialCD binomialCDScreen = new BinomialCD();
            System.out.println("Binomial Cumulative Distribution clicked");
        });
        PoissonPD.setOnAction(e -> {
            com.example.seg.PoissonPD poissonPDScreen = new PoissonPD();
            System.out.println("Poisson Probability Distribution clicked");
        });
        PoissonCD.setOnAction(e -> {
            com.example.seg.PoissonCD poissonCDScreen = new PoissonCD();
            System.out.println("Poisson Cumulative Distribution clicked");
        });

        //creating HBoxes to house the elements
        HBox labelBox = new HBox();
        HBox binomialBox = new HBox();
        HBox poissonBox = new HBox();
        HBox normalBox = new HBox();
        labelBox.setPadding(new Insets(4,4,4,4));
        binomialBox.setPadding(new Insets(4,4,4,4));
        poissonBox.setPadding(new Insets(4,4,4,4));
        normalBox.setPadding(new Insets(4,4,4,4));

        //adding the elements to th boxes
        labelBox.getChildren().addAll(statsLabelL,statsLabel,statsLabelR);
        binomialBox.getChildren().addAll(binomialL,BinomialPD,binomialM,BinomialCD,binomialR);
        poissonBox.getChildren().addAll(poissonL,PoissonPD,poissonM,PoissonCD,poissonR);

        //creating a VBox to house the HBoxes
        VBox centerVbox = new VBox();
        centerVbox.setPadding(new Insets(4,4,4,4));
        Region vBoxTop = new Region();
        Region vBoxMidT = new Region();
        Region vBoxBottom = new Region();
        centerVbox.getChildren().addAll(vBoxTop,binomialBox,vBoxMidT,poissonBox,vBoxBottom);

        //adding the UI elements to the scene
        pane.setTop(labelBox);
        pane.setCenter(centerVbox);

    }
}
