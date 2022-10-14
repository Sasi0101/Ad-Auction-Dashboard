package com.example.seg;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.util.ArrayList;
import javafx.application.Platform;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SearchBox extends BorderPane{

    public ArrayList<String> totalItemsList;
    public ListView<String> lv;
    public SearchBox(ArrayList<String> items, MenuScreen m, Stage currStage){
        TextField searchField = new TextField();
        searchField.setPromptText("*config name*");
        searchField.getStyleClass().add("text-field");
        searchField.setOnKeyTyped(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                Platform.runLater(() -> {
                    lv.getItems().clear();
                    for (String s : totalItemsList) {
                        if (s.contains(searchField.getText())) {
                            lv.getItems().add(s);
                        }
                    }
                });
            }
        });
        this.setTop(searchField);

        Button applyButton = new Button("Select");
        applyButton.getStyleClass().add("menuItem");
        /*applyButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Platform.runLater(() -> {
                    //m.loadingConfName = lv.getSelectionModel().getSelectedItem();
                    currStage.close();
                });
            }
        });*/
        this.setBottom(applyButton);


        lv = new ListView();
        totalItemsList = new ArrayList<>();
        for (String s : items){
            totalItemsList.add(s);
            lv.getItems().add(s);
        }
        this.setCenter(new ScrollPane(lv));
    }

}
