package com.example.seg;

import com.opencsv.exceptions.CsvException;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public class FileScreen extends Application {
    ClickLog clickLog;
    ImpressionLog impressionLog;
    ServerLog serverLog;

    // shared variables
    FileHistory fileHistory;
    boolean recentButtonClicked = false;
    VBox historyButtonsPane;
    Stage stage;
    Scene scene;
    //String newFolderName;

    // variables that assist with tabbing in MenuScreen
    protected static boolean firstInstance = true;
    protected AdditionalCampaignListener additionalCampaignListener;


    /**
     * method that handles opening an inputted Folder and getting the csv data out
     * @param file is the folder to open
     */
    private void openFolder(File file) {
        LoadMultipleFiles fileLoader = new LoadMultipleFiles(file);
        List<File> fileList = fileLoader.getFileList();
        boolean server = false;
        boolean click = false;
        boolean impression = false;
        try {
            for (File csvFile : fileList) {
                    BufferedReader reader = new BufferedReader(new FileReader(csvFile));
                    int width = reader.readLine().split(",").length;
                    // SAVE THE FILENAME TO A .TXT FILE
                    if (width == 5) {
                        this.serverLog = new ServerLog(csvFile.getAbsolutePath());
                        System.out.println("new serverLog file added: " + csvFile.getName());
                        server = true;
                    } else if (width == 3) {
                        this.clickLog = new ClickLog(csvFile.getAbsolutePath());
                        System.out.println("new clickLog file added: " + csvFile.getName());
                        click = true;
                    } else if (width == 7) {
                        this.impressionLog = new ImpressionLog(csvFile.getAbsolutePath());
                        System.out.println("new impressionLog file added: " + csvFile.getName());
                        impression = true;
                    } else {
                        throw new Exception("file is not in the correct format");
                    }
            }
        }
        catch (Exception e1) {
            server = false;
            //Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            //errorAlert.setHeaderText("Input not valid");
            //errorAlert.setContentText("Invalid Data");
            //errorAlert.showAndWait();
        }

        if (!(server && click && impression)){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Input not valid");
            errorAlert.setContentText("Please make sure click, impression and server log files are correctly formatted and in the folder.");
            errorAlert.showAndWait();
        }
        //Prints the name of the selected files
        System.out.println("Name of chosen clickLog file is " + clickLog.getName());
        System.out.println("Name of chosen impressionLog file is " + impressionLog.getName());
        System.out.println("Name of chosen serverLog file is " + serverLog.getName());
    }

    /**
     * method to show the Recent files List
     * @param historyButtonsPane the pane for the recent files UI element
     */
    private void showRecentList(Pane historyButtonsPane) {
        // clear file history list
        historyButtonsPane.getChildren().clear();
        int index = 0;
        for (FileHistoryItem item : fileHistory.getFileHistoryItems()) {
            // create a flow pane to
            FlowPane itemPane = new FlowPane();
            itemPane.setAlignment(Pos.CENTER);
            itemPane.setHgap(8);

            // create a label
            Label label = new Label(item.getFilePath());
            label.getStyleClass().add("text-item");
            historyButtonsPane.getChildren().add(itemPane);
            if (index < 3) {
                // for the first three item, add a label and a button
                Button openItemButton = new Button("Open");
                openItemButton.getStyleClass().add("smallButton");
                itemPane.getChildren().add(label);
                itemPane.getChildren().add(openItemButton);

                openItemButton.setOnAction(ee -> {
                    String filePath = item.getFilePath();
                    File targetFile = new File(filePath);
                    openFolder(targetFile);
                    try {
                        String[] folderNameSplit = targetFile.getName().split("\\\\");
                        String folderName = folderNameSplit[folderNameSplit.length-1];
                        openMenuScreen(folderName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (CsvException | ParseException e) {
                        e.printStackTrace();
                    }
                    // showCharts(stage, scene);
                });

            } else {
                // for the other items, add a label only
                itemPane.getChildren().add(label);
            }
            index++;
        }
    }


    /**
     * method to open the menu screen / load an additional campaign into the existing menu screen
     * @param folderName is the name of the new folder
     * @throws IOException for IO issues
     * @throws CsvException for issues surrounding the csv file specifically
     */
    private  void openMenuScreen(String folderName) throws IOException, CsvException, ParseException {
        this.stage.close();
        if (firstInstance) {
            MenuScreen menuScreen = new MenuScreen(clickLog, impressionLog, serverLog, folderName);
            firstInstance = false;
        } else {
            additionalCampaign(clickLog,impressionLog,serverLog,folderName);
        }
    }


    /**
     * method to start the FileScreen instance UI
     * @param stage of the new FileScreen instance
     * @throws IOException for any issues with file handling
     */
    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;
        ProgressBar pb = new ProgressBar();
        pb.setProgress(1.0);
        //FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        //Scene scene = new Scene(fxmlLoader.load(), 520, 300);
        // Above is old fxml code for javafx and idk how to use that so ima do it the old way

        // create a file history
        fileHistory = new FileHistory(10);
        // load file history
        fileHistory.load();

        // Setting scene
        BorderPane pane = new BorderPane();
        scene = new Scene(pane, 800, 600);

        // Adding scene style sheet from style.css in /resources/.../style.css
        //Font.loadFont(getClass().getResourceAsStream("abeatbyKaiRegular.ttf"), 32);
        Font.loadFont(getClass().getResourceAsStream("Fredoka.ttf"), 32);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        pane.getStyleClass().add("menu");

        // Adding scene to stage
        stage.setTitle("Open file");
        stage.setScene(scene);
        stage.show();

        // Title
        ImageView titleImage = new ImageView();
        Image adLogo = new Image(String.valueOf(this.getClass().getResource("adlogo.png")));
        titleImage.setImage(adLogo);
        titleImage.setPreserveRatio(true);
        titleImage.setFitWidth(600);
        BorderPane.setAlignment(titleImage, Pos.CENTER);

        // File chooser button
        DirectoryChooser fileChooser = new DirectoryChooser();
        Button openButton = new Button("Select folder");

        openButton.setOnAction(e -> {
            File inputFile = fileChooser.showDialog(stage);
            if (inputFile != null) {
                String folderPath = inputFile.getAbsolutePath();
                openFolder(inputFile);

                FileHistoryItem fileHistoryItem = new FileHistoryItem(folderPath);
                fileHistory.addHistoryItem(fileHistoryItem);
                fileHistory.save();
                if (recentButtonClicked) {
                    showRecentList(historyButtonsPane);
                }

                try {
                    String[] folderNameSplit = inputFile.getName().split("\\\\");
                    String folderName = folderNameSplit[folderNameSplit.length-1];
                    openMenuScreen(folderName);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                } catch (CsvException | ParseException csvException) {
                    csvException.printStackTrace();
                }
            }
        });
        Button recentButton = new Button("Recent");
        recentButton.getStyleClass().add("menuItem");

        // Button stylesheet
        openButton.getStyleClass().add("menuItem");

        // create a vbox to display recent files
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(50);
        vBox.getChildren().add(titleImage);
        vBox.getChildren().add(openButton);
        vBox.getChildren().add(recentButton);
        historyButtonsPane = new VBox();
        vBox.getChildren().add(historyButtonsPane);
        vBox.getStyleClass().add("box");

        recentButton.setOnAction(e -> {
            // when recent button clicked show recent list
            recentButtonClicked = true;
            showRecentList(historyButtonsPane);
        });

        pane.setCenter(vBox);

    }

    /**
     * method which launches the application
     */
    public void launchFileScreen() {
        launch();
    }


    /**
     * setter method for the AdditionalCampaignsListener
     * @param listener inputted
     */
    public void setOnAdditionalCampaign(AdditionalCampaignListener listener) {
        additionalCampaignListener = listener;
    }


    /**
     * method which links a FileScreen instance with the MenuScreen via a listener interface
     * @param clickLog the ClickLog object for the current campaign
     * @param impressionLog the ImpressionLog object
     * @param serverLog the ServerLog object
     * @param folderName the name of the folder
     */
    public void additionalCampaign(ClickLog clickLog, ImpressionLog impressionLog, ServerLog serverLog, String folderName) {
        additionalCampaignListener.additionalCampaign(clickLog,impressionLog,serverLog,folderName);
    }
}