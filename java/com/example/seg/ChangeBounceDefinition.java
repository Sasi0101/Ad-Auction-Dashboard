package com.example.seg;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;


public class ChangeBounceDefinition {

    // variables for UI and regex checking
    private final Stage stage;
    protected int pageVisitNumber = 1;
    private boolean isPageVisitValid;
    protected int bounceTimeNumber = 1;
    private boolean isBounceTimeValid;

    // listener for new page visit and bounce time values
    protected ChangeBounceDefListener changeBounceDefListener;

    /**
     * method for creating the UI for the bounce redefinition
     */
    public ChangeBounceDefinition(int bouncePageVisits, int bounceTimeSpent) {
        //Setting up the scene and the stage
        Font.loadFont(getClass().getResourceAsStream("Fredoka.ttf"), 32);
        BorderPane pane = new BorderPane();
        Scene scene = new Scene(pane,300,150);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage = new Stage();
        stage.setTitle("Redefine Bounce");
        stage.setScene(scene);
        stage.show();

        //creating the input boxes and labels + their positioning       ---- will tidy in next increment
        Label pageVisitLabel = new Label("Enter page visit number:");
        Label bounceTimeLabel = new Label("Enter time (seconds):   ");
        TextField pageVisits = new TextField(Integer.toString(bouncePageVisits));
        TextField bounceTime = new TextField(Integer.toString(bounceTimeSpent));
        pageVisits.setPrefWidth(50);
        bounceTime.setPrefWidth(50);


        //positioning for the Labels and TextFields
        HBox pageVisitBox = new HBox();
        HBox bounceTimeBox = new HBox();
        pageVisitBox.setPadding(new Insets(4,4,4,4));
        bounceTimeBox.setPadding(new Insets(4,4,4,4));

        Region pageVisitL = new Region();
        Region pageVisitR = new Region();
        Region pageVisitM = new Region();
        HBox.setHgrow(pageVisitL,Priority.ALWAYS);
        HBox.setHgrow(pageVisitR,Priority.ALWAYS);
        HBox.setHgrow(pageVisitM,Priority.ALWAYS);
        Region bounceTimeL = new Region();
        Region bounceTimeM = new Region();
        Region bounceTimeR = new Region();
        HBox.setHgrow(bounceTimeL,Priority.ALWAYS);
        HBox.setHgrow(bounceTimeM,Priority.ALWAYS);
        HBox.setHgrow(bounceTimeR,Priority.ALWAYS);

        pageVisitBox.getChildren().addAll(pageVisitL,pageVisitLabel,pageVisitM,pageVisits,pageVisitR);
        bounceTimeBox.getChildren().addAll(bounceTimeL,bounceTimeLabel,bounceTimeM,bounceTime,bounceTimeR);

        VBox centerVbox = new VBox();
        centerVbox.setPadding(new Insets(4,4,4,4));
        centerVbox.getChildren().addAll(pageVisitBox,bounceTimeBox);


        //creating + basic functionality for the Apply button
        Button applyNewBounceDefinition = new Button("Apply");
        applyNewBounceDefinition.getStyleClass().add("menuItem");
        applyNewBounceDefinition.setOnAction(e -> {
            var pText = pageVisits.getText();
            var bText = bounceTime.getText();
            //System.out.println(pText);
            //System.out.println(bText);
            checkInt(pText, bText);
            if (isPageVisitValid && isBounceTimeValid) {
                System.out.println("Valid page visit number and bounce time entered: " + pText);
                stage.close();
                changeBounceDef(Integer.parseInt(pText),Integer.parseInt(bText));
            } else {
                System.out.println("Invalid page visit number or bounce time entered");
            }
            System.out.println("Apply clicked");

        });

        // positioning for Apply button
        Region regionBottomL = new Region();
        Region regionBottomR = new Region();
        HBox.setHgrow(regionBottomL,Priority.ALWAYS);
        HBox.setHgrow(regionBottomR,Priority.ALWAYS);
        HBox bottomHBox = new HBox();
        bottomHBox.setPadding(new Insets(4,4,4,4));
        bottomHBox.getChildren().addAll(regionBottomL,applyNewBounceDefinition,regionBottomR);

        // adding input elements and Apply button to the pane
        pane.setTop(centerVbox);
        pane.setBottom(bottomHBox);

    }

    /**
     * method to check user entered an integer into the text boxes
     * @param pageVisits the entered page visits as string
     * @param bounceTime the entered bounce time as string
     */
    private void checkInt(String pageVisits, String bounceTime) {
        if (pageVisits.matches("[1-9]+[0-9]*") && bounceTime.matches("[1-9]+[0-9]*")) {
            pageVisitNumber = Integer.parseInt(pageVisits);
            isPageVisitValid = true;
            bounceTimeNumber = Integer.parseInt(bounceTime);
            isBounceTimeValid = true;
        } else if (pageVisits.matches("[1-9]+[0-9]*") && !(bounceTime.matches("[1-9]+[0-9]*"))) {
            pageVisitNumber = Integer.parseInt(pageVisits);
            isPageVisitValid = true;
            isBounceTimeValid = false;
        } else if (!(pageVisits.matches("[1-9]+[0-9]*")) && bounceTime.matches("[1-9]+[0-9]*")) {
            bounceTimeNumber = Integer.parseInt(bounceTime);
            isBounceTimeValid = true;
            isPageVisitValid = false;
        } else {
            isPageVisitValid = false;
            isBounceTimeValid = false;
        }
    }


    /**
     * setter method for the ChangeBounceDefListener
     * @param newChangeBounceDefListener the new listener
     */
    public void setChangeBounceDefListener(ChangeBounceDefListener newChangeBounceDefListener) {
        changeBounceDefListener = newChangeBounceDefListener;
    }

    public void changeBounceDef(int pageVisit, int bounceTime) {
        changeBounceDefListener.changeBounceDef(pageVisit,bounceTime);
    }

}
