package com.example.seg;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;


public class HelloController {
    private Boolean visible = Boolean.FALSE;

    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        if (!visible) {
            welcomeText.setText("Welcome to Group 18 SEG Project!");
            welcomeText.setTextFill(Color.GOLD);
            welcomeText.setFont(new Font("Times New Roman",30));
            visible = Boolean.TRUE;
        } else {
            welcomeText.setText("");
            visible = Boolean.FALSE;
        }
    }
}