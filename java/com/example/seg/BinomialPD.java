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

public class BinomialPD {
    protected String probability;
    protected String numTrials;
    protected String numSuccesses;
    protected BigDecimal Answer;
    protected Functions functions = new Functions();

    /**
     * Constructor + implementing functionality for Binomial PD
     */
    public BinomialPD (){
        Font.loadFont(getClass().getResourceAsStream("Fredoka.ttf"), 32);
        BorderPane pane = new BorderPane();
        Scene scene = new Scene(pane,450,250);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        Stage stage = new Stage();
        stage.setTitle("Binomial Probability Distribution Calculator");
        stage.setScene(scene);
        stage.show();
        stage.setResizable(false);

        //creating the UI elements
        Button calculate = new Button("Calculate");
        calculate.getStyleClass().add("smallButton");
        Label successLab = new Label("Enter number of successes:");
        Label trialsLab = new Label("Enter number of trials:");
        Label probabilityLab = new Label("Enter probability of success:");
        successLab.getStyleClass().add("statsInstruction");
        trialsLab.getStyleClass().add("statsInstruction");
        probabilityLab.getStyleClass().add("statsInstruction");
        TextField textNumSuccess = new TextField();
        TextField textNumTrials = new TextField();
        TextField textProbability = new TextField();
        Label result = new Label("Answer");
        result.getStyleClass().add("statsResult");

        //adding functionality
        calculate.setOnAction(e -> {
            numSuccesses = textNumSuccess.getText();
            numTrials = textNumTrials.getText();
            probability = textProbability.getText();

            if (checkInt()) {
                BigDecimal p = new BigDecimal(probability);
                int n = Integer.parseInt(numTrials);
                int x = Integer.parseInt(numSuccesses);

                //adding alerts for error cases
                if (n > 5000) {
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
                    Answer = functions.binomialPdCalc(x, n, p).round(new MathContext(11, RoundingMode.HALF_UP));
                    System.out.println(Answer);
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setHeaderText("Invalid input: Successes or Trials not a number, or Probability not a decimal < 1");
                alert.showAndWait().ifPresent(rs -> {
                    if (rs == ButtonType.OK) {
                        System.out.println("Pressed OK.");
                    }
                });
            }
            result.setText(String.valueOf(Answer));
        });

        //creating VBox to house the elements
        VBox centreBox = new VBox();
        centreBox.getChildren().addAll(successLab,textNumSuccess,trialsLab,textNumTrials,probabilityLab,textProbability,calculate,result);
        centreBox.setPadding(new Insets(4,4,4,4));

        //adding the elements to the scene
        pane.setCenter(centreBox);
    }

    /**
     * method to validate the input
     * @return boolean result
     */
    public boolean checkInt() {
        return numSuccesses.matches("\\d+\\d*") && numTrials.matches("[1-9]+\\d*") && probability.matches("0\\.\\d+"); //&& probability.matches("[1-9]+\\d*.[1-9]+\\d*");
    }
}
