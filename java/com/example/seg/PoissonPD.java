package com.example.seg;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class PoissonPD {
    protected String meanSuccesses;
    protected String numSuccesses;
    protected BigDecimal Answer;
    protected Functions functions = new Functions();

    /**
     * Constructor + implementing functionality for Poisson PD
     */
    public PoissonPD(){
        //creating the scene
        Font.loadFont(getClass().getResourceAsStream("Fredoka.ttf"), 32);
        BorderPane pane = new BorderPane();
        Scene scene = new Scene(pane,450,250);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        Stage stage = new Stage();
        stage.setTitle("Poisson Probability Distribution Calculator");
        stage.setScene(scene);
        stage.show();
        stage.setResizable(false);

        //creating the UI elements
        Button calculate = new Button("Calculate");
        calculate.getStyleClass().add("smallButton");
        Label successLab = new Label("Enter number of successes:");
        Label meanLab = new Label("Enter mean number of success:");
        successLab.getStyleClass().add("statsInstruction");
        meanLab.getStyleClass().add("statsInstruction");
        TextField textNumSuccess = new TextField();
        TextField textMeanSuccess = new TextField();
        Label result = new Label("Answer");
        result.getStyleClass().add("statsResult");

        //adding functionality
        calculate.setOnAction(e -> {
            numSuccesses = textNumSuccess.getText();
            meanSuccesses = textMeanSuccess.getText();
            if (checkInt()) {
                int x = Integer.parseInt(numSuccesses);
                BigDecimal l = new BigDecimal(meanSuccesses);

                //adding alerts for error cases
                if (x > 5000) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Error");
                    alert.setHeaderText("Number of trials too large. Size limit: 5000");
                    alert.showAndWait().ifPresent(rs -> {
                        if (rs == ButtonType.OK) {
                            System.out.println("Pressed OK.");
                        }
                    });
                } else {
                    //getting the answer to the calculation
                    Answer = functions.poissonPdCalc(x,l).round(new MathContext(7, RoundingMode.HALF_UP));
                    System.out.println(Answer);
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setHeaderText("Invalid input: Successes not a whole number or Mean not a decimal");
                alert.showAndWait().ifPresent(rs -> {
                    if (rs == ButtonType.OK) {
                        System.out.println("Pressed OK.");
                    }
                });
            }
            result.setText(Answer + " (7 s.f)");
        });

        //creating VBox to house the elements
        VBox centreBox = new VBox();
        centreBox.getChildren().addAll(successLab,textNumSuccess,meanLab,textMeanSuccess,calculate,result);
        centreBox.setPadding(new Insets(4,4,4,4));

        //adding the elements to the scene
        pane.setCenter(centreBox);
    }

    /**
     * method to validate the input
     * @return boolean result
     */
    public boolean checkInt() {
        return numSuccesses.matches("\\d+\\d*") && meanSuccesses.matches("\\d+\\.\\d*"); //&& probability.matches("[1-9]+\\d*.[1-9]+\\d*");
    }
}

