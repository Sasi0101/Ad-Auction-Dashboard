package com.example.seg;

import com.opencsv.exceptions.CsvException;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class MenuScreen {
    private ClickLog clickLog;
    private ImpressionLog impressionLog;
    private ServerLog serverLog;
    private LineChartController lcc;
    private LineChartViewer lcViewer;
    private LineChartController lcc1;
    private Boolean isFirstDate = true;
    private final Stage stage;
    private final Scene scene;
    private final TabPane mainTabPane = new TabPane();
    private Slider currentSlider;
    private ApplyButtonListener applyButtonListener;
    private ZoomingListener zoomingListener;

    // Line 1 of metrics
    protected int bouncePageVisits = 1;
    protected int bounceTimeSpent = 60;
    MetricButton noImpressions;
    MetricButton noClicks;
    MetricButton noUniques;
    MetricButton noBounces;
    MetricButton noConversions;
    MetricButton totalCost;
    MetricButton ctr;
    MetricButton cpa;
    MetricButton cpm;
    MetricButton bounceRate;

    // Line 2 of metrics
    protected int bouncePageVisits2 = 1;
    protected int bounceTimeSpent2 = 60;
    MetricButton noImpressions2;
    MetricButton noClicks2;
    MetricButton noUniques2;
    MetricButton noBounces2;
    MetricButton noConversions2;
    MetricButton totalCost2;
    MetricButton ctr2;
    MetricButton cpa2;
    MetricButton cpm2;
    MetricButton bounceRate2;
    static String[] finalImpression;
    static String[] finalClicklog;
    static String[] finalServerlog;

    /**
     * The menu screen showing key metrics, histogram buttons and graph buttons
     *
     * @param clickLog      file handler
     * @param impressionLog file handler
     * @param serverLog     file handler
     */
    public MenuScreen(ClickLog clickLog, ImpressionLog impressionLog, ServerLog serverLog, String folderName) throws ParseException {
        // Window settings, setup and styles

        mainTabPane.getTabs().add(createNewTab(clickLog, impressionLog, serverLog, folderName));
        Font.loadFont(getClass().getResourceAsStream("Fredoka.ttf"), 32);
        scene = new Scene(mainTabPane, 1500, 900);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage = new Stage();
        stage.setTitle("Ad Auction Dashboard");
        stage.setScene(scene);
        stage.show();
        LineChartController.clickLogWithImpressionLog(impressionLog,clickLog,serverLog);
        LineChartController.startDate   = new SimpleDateFormat("yyyy-MM-dd").parse(impressionLog.getCell(0,1).split(" ")[0]);
        LineChartController.endDate     = new SimpleDateFormat("yyyy-MM-dd").parse(impressionLog.getCell(0,impressionLog.length-1).split(" ")[0]);
        LineChartController.startDate1  = new SimpleDateFormat("yyyy-MM-dd").parse(impressionLog.getCell(0,1).split(" ")[0]);
        LineChartController.endDate1    = new SimpleDateFormat("yyyy-MM-dd").parse(impressionLog.getCell(0,impressionLog.length-1).split(" ")[0]);
        LineChartController.startDate2  = new SimpleDateFormat("yyyy-MM-dd").parse(impressionLog.getCell(0,1).split(" ")[0]);
        LineChartController.endDate2    = new SimpleDateFormat("yyyy-MM-dd").parse(impressionLog.getCell(0,impressionLog.length-1).split(" ")[0]);
    }


    // show csv charts
    private void showCharts(Stage stage, Scene scene) {
        ClickCostHistory histogram = new ClickCostHistory();
        try {
            Button saveButton = new Button("Save as PNG");
            BarChart<String, Number> barChart = histogram.getBarChart(this.clickLog);
            VBox histogramBox = new VBox(barChart, saveButton);
            histogramBox.setAlignment(Pos.CENTER);
            Scene scene1 = new Scene(histogramBox, 1300, 750);
            scene1.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
            saveButton.getStyleClass().add("smallButton");
            stage.setScene(scene1);
            stage.show();

            saveButton.setOnMouseClicked(b -> {
                //Choose the filepath where you want to save it
                FileChooser save = new FileChooser();
                save.setInitialFileName("histogram-of-click-cost.png");
                save.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG files", ".png"));
                File file = save.showSaveDialog(stage);
                try {
                    SaveAsPNG.saveBarChart(barChart, file.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            stage.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
                if (KeyCode.ESCAPE == event.getCode()) {
                    stage.setScene(scene);
                    stage.show();
                }
                if (KeyCode.S == event.getCode()) {
                    //Choose the filepath where you want to save it
                    FileChooser save = new FileChooser();
                    save.setInitialFileName("histogram-of-click-cost.png");
                    save.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG files", ".png"));
                    File file = save.showSaveDialog(stage);

                    try {
                        SaveAsPNG.saveBarChart(barChart, file.getAbsolutePath());
                    } catch (IOException ioException) {
                        System.out.println("Failed to save to PNG");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (CsvException ioException) {
            ioException.printStackTrace();
        }
    }


    private ScrollPane filters() {
        VBox vBox = new VBox();
        vBox.getStyleClass().add("box");

        //**Config Name **//
        TextField configName = new TextField("config name");

        //** DateButton **//
        //AtomicBoolean isDataLabelOn = new AtomicBoolean(false);
        AtomicBoolean isFirstClickDateButton = new AtomicBoolean(true);
        LocalDate[] dates = new LocalDate[2];
        Button dateButton = new Button("Date range");
        dateButton.getStyleClass().add("menuItem");
        Label dataLabel = new Label("");
        DatePicker datePicker = new DatePicker();
        datePicker.getStyleClass().add("dateField");
        VBox dateRange = new VBox();
        dateRange.getChildren().add(0, dateButton);


        //Handle what happens when the date button is clicked
        dateButton.setOnMouseClicked(event1 -> {
            if (isFirstClickDateButton.get()) {
                dateRange.getChildren().addAll(dataLabel, datePicker);
                EventHandler<ActionEvent> event = e -> {
                    if (isFirstDate) {
                        dates[0] = datePicker.getValue();
                        if (dates[1] == null) {
                            dataLabel.setText(dates[0] + " - ");
                        } else {
                            dataLabel.setText(dates[0] + " - " + dates[1]);
                        }
                        isFirstDate = false;
                    } else {
                        dates[1] = datePicker.getValue();
                        dataLabel.setText(dates[0] + " - " + dates[1]);
                        isFirstDate = true;
                        dateRange.getChildren().remove(datePicker);
                    }

                };

                datePicker.setOnAction(event);
                isFirstClickDateButton.set(false);
            } else {
                dateRange.getChildren().removeAll(dataLabel, datePicker);
                isFirstClickDateButton.set(true);
            }
        });
        //Tooltip.install(dateRange, new Tooltip("Click to select start and end date"));

        //**  GenderButton **//
        AtomicBoolean isGenderButtonOn = new AtomicBoolean(false);
        AtomicBoolean isFirstClickGenderButton = new AtomicBoolean(true);
        VBox genderBox = new VBox();

        //Handle what happens when the gender button is clicked
        Button genderButton = new Button("Gender");
        genderButton.getStyleClass().add("menuItem");
        genderBox.getChildren().add(genderButton);
        CheckBox femaleBox = new CheckBox("Female");
        CheckBox maleBox = new CheckBox("Male");

        genderButton.setOnMouseClicked(e -> {
            if (isFirstClickGenderButton.get()) {
                if (!isGenderButtonOn.get()) {
                    genderBox.getChildren().addAll(maleBox, femaleBox);
                    isGenderButtonOn.set(true);
                }
                isFirstClickGenderButton.set(false);
            } else {
                genderBox.getChildren().removeAll(maleBox, femaleBox);
                isGenderButtonOn.set(false);
                isFirstClickGenderButton.set(true);
            }

        });


        //** AgeButton **//
        AtomicBoolean isAgeButtonOn = new AtomicBoolean(false);
        AtomicBoolean isFirstClickAgeButton = new AtomicBoolean(true);
        VBox ageBox = new VBox();
        Button ageButton = new Button("Age");
        ageButton.getStyleClass().add("menuItem");
        ageBox.getChildren().add(ageButton);
        CheckBox age1 = new CheckBox("<25");
        CheckBox age2 = new CheckBox("25-34");
        CheckBox age3 = new CheckBox("35-44");
        CheckBox age4 = new CheckBox("45-54");
        CheckBox age5 = new CheckBox(">54");

        ageButton.setOnMouseClicked(e -> {
            if (isFirstClickAgeButton.get()) {
                if (!isAgeButtonOn.get()) {
                    ageBox.getChildren().addAll(age1, age2, age3, age4, age5);
                    isAgeButtonOn.set(true);
                }
                isFirstClickAgeButton.set(false);
            } else {
                ageBox.getChildren().removeAll(age1, age2, age3, age4, age5);
                isAgeButtonOn.set(false);
                isFirstClickAgeButton.set(true);
            }
        });


        //** IncomeButton  **//
        AtomicBoolean isIncomeButtonOn = new AtomicBoolean(false);
        AtomicBoolean isFirstClickIncomeButton = new AtomicBoolean(true);
        VBox incomeBox = new VBox();
        Button incomeButton = new Button("Income");
        incomeButton.getStyleClass().add("menuItem");
        incomeBox.getChildren().add(incomeButton);
        CheckBox low = new CheckBox("Low");
        CheckBox medium = new CheckBox("Medium");
        CheckBox high = new CheckBox("High");

        incomeButton.setOnMouseClicked(e -> {
            if (isFirstClickIncomeButton.get()) {
                if (!isIncomeButtonOn.get()) {
                    incomeBox.getChildren().addAll(low, medium, high);
                    isIncomeButtonOn.set(true);
                }
                isFirstClickIncomeButton.set(false);
            } else {
                incomeBox.getChildren().removeAll(low, medium, high);
                isIncomeButtonOn.set(false);
                isFirstClickIncomeButton.set(true);
            }
        });


        //** ContextButton **//
        AtomicBoolean isContextButtonOn = new AtomicBoolean(false);
        AtomicBoolean isFirstClickContextButton = new AtomicBoolean(true);
        VBox contextBox = new VBox();
        Button contextButton = new Button("Context");
        contextButton.getStyleClass().add("menuItem");
        contextBox.getChildren().add(contextButton);
        CheckBox news = new CheckBox("News");
        CheckBox shopping = new CheckBox("Shopping");
        CheckBox socialMedia = new CheckBox("Social Media");
        CheckBox blog = new CheckBox("Blog");
        CheckBox hobbies = new CheckBox("Hobbies");
        CheckBox travel = new CheckBox("Travel");

        contextButton.setOnMouseClicked(e -> {
            if (isFirstClickContextButton.get()) {
                if (!isContextButtonOn.get()) {
                    contextBox.getChildren().addAll(news, shopping, socialMedia, blog, hobbies, travel);
                    isContextButtonOn.set(true);
                }
                isFirstClickContextButton.set(false);
            } else {
                contextBox.getChildren().removeAll(news, shopping, socialMedia, blog, hobbies, travel);
                isContextButtonOn.set(false);
                isFirstClickContextButton.set(true);
            }
        });

        //** Save/Load filters Button **//
        VBox saveLoadHolder = new VBox();
        Button saveButton = new Button("Save Configuration");
        saveButton.getStyleClass().add("smallButton");
        Button loadButton = new Button("Load Configuration");
        loadButton.getStyleClass().add("smallButton");
        Button clear = new Button("Clear");
        clear.getStyleClass().add("smallButton");
        clear.setOnMouseClicked(e -> {
            dataLabel.setText("");
            femaleBox.setSelected(false);
            maleBox.setSelected(false);
            age1.setSelected(false);
            age2.setSelected(false);
            age3.setSelected(false);
            age4.setSelected(false);
            age5.setSelected(false);
            low.setSelected(false);
            medium.setSelected(false);
            high.setSelected(false);
            news.setSelected(false);
            shopping.setSelected(false);
            socialMedia.setSelected(false);
            blog.setSelected(false);
            hobbies.setSelected(false);
            travel.setSelected(false);
        });
        Button tickAll = new Button("Tick all");
        tickAll.getStyleClass().add("smallButton");
        tickAll.setOnMouseClicked(e -> {
            dataLabel.setText("");
            femaleBox.setSelected(true);
            maleBox.setSelected(true);
            age1.setSelected(true);
            age2.setSelected(true);
            age3.setSelected(true);
            age4.setSelected(true);
            age5.setSelected(true);
            low.setSelected(true);
            medium.setSelected(true);
            high.setSelected(true);
            news.setSelected(true);
            shopping.setSelected(true);
            socialMedia.setSelected(true);
            blog.setSelected(true);
            hobbies.setSelected(true);
            travel.setSelected(true);
        });

        saveButton.setOnMouseClicked(e -> {
            //Choose the filepath where you want to save it
            FileChooser save = new FileChooser();
            save.setInitialFileName(configName.getText() + ".txt");
            save.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("TEXT files", ".txt"));
            File file = save.showSaveDialog(stage);
            try (BufferedWriter writer = new BufferedWriter(new PrintWriter(file))) {
                writer.write(configName.getText());
                writer.newLine();
                writer.write(dataLabel.getText());
                writer.newLine();
                writer.write(String.valueOf(femaleBox.isSelected()));
                writer.newLine();
                writer.write(String.valueOf(maleBox.isSelected()));
                writer.newLine();
                writer.write(String.valueOf(age1.isSelected()));
                writer.newLine();
                writer.write(String.valueOf(age2.isSelected()));
                writer.newLine();
                writer.write(String.valueOf(age3.isSelected()));
                writer.newLine();
                writer.write(String.valueOf(age4.isSelected()));
                writer.newLine();
                writer.write(String.valueOf(age5.isSelected()));
                writer.newLine();
                writer.write(String.valueOf(low.isSelected()));
                writer.newLine();
                writer.write(String.valueOf(medium.isSelected()));
                writer.newLine();
                writer.write(String.valueOf(news.isSelected()));
                writer.newLine();
                writer.write(String.valueOf(shopping.isSelected()));
                writer.newLine();
                writer.write(String.valueOf(socialMedia.isSelected()));
                writer.newLine();
                writer.write(String.valueOf(blog.isSelected()));
                writer.newLine();
                writer.write(String.valueOf(hobbies.isSelected()));
                writer.newLine();
                writer.write(String.valueOf(travel.isSelected()));
            } catch (Exception ioException) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("Save failed.");
                errorAlert.setContentText("Save failed. Please try again.");
                errorAlert.showAndWait();
            }
        });
        // SAVE CONFIG

        loadButton.setOnMouseClicked(e -> {
            FileChooser fileChooser = new FileChooser();
            File inputFile = fileChooser.showOpenDialog(stage);
            if (inputFile != null) {
                String folderPath = inputFile.getAbsolutePath();
                try (BufferedReader reader = new BufferedReader(new FileReader(folderPath))) {
                    configName.setText(reader.readLine());
                    dataLabel.setText(reader.readLine());
                    femaleBox.setSelected(Boolean.parseBoolean(reader.readLine()));
                    maleBox.setSelected(Boolean.parseBoolean(reader.readLine()));
                    age1.setSelected(Boolean.parseBoolean(reader.readLine()));
                    age2.setSelected(Boolean.parseBoolean(reader.readLine()));
                    age3.setSelected(Boolean.parseBoolean(reader.readLine()));
                    age4.setSelected(Boolean.parseBoolean(reader.readLine()));
                    age5.setSelected(Boolean.parseBoolean(reader.readLine()));
                    low.setSelected(Boolean.parseBoolean(reader.readLine()));
                    medium.setSelected(Boolean.parseBoolean(reader.readLine()));
                    news.setSelected(Boolean.parseBoolean(reader.readLine()));
                    shopping.setSelected(Boolean.parseBoolean(reader.readLine()));
                    socialMedia.setSelected(Boolean.parseBoolean(reader.readLine()));
                    blog.setSelected(Boolean.parseBoolean(reader.readLine()));
                    hobbies.setSelected(Boolean.parseBoolean(reader.readLine()));
                    travel.setSelected(Boolean.parseBoolean(reader.readLine()));
                } catch (Exception ioException) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setHeaderText("File does not exist");
                    errorAlert.setContentText("Save a configuration first.");
                    errorAlert.showAndWait();
                }
            }
        });
        saveLoadHolder.getChildren().addAll(tickAll, clear, saveButton, loadButton);


        //** Apply Button **//
        Button applyButton = new Button("Apply List 1");
        applyButton.getStyleClass().add("menuItem");
        applyButton.setOnMouseClicked(e -> {
            boolean[] filters = new boolean[16];
            filters[0] = maleBox.isSelected();
            filters[1] = femaleBox.isSelected();
            filters[2] = age1.isSelected();
            filters[3] = age2.isSelected();
            filters[4] = age3.isSelected();
            filters[5] = age4.isSelected();
            filters[6] = age5.isSelected();
            filters[7] = low.isSelected();
            filters[8] = medium.isSelected();
            filters[9] = high.isSelected();
            filters[10] = news.isSelected();
            filters[11] = shopping.isSelected();
            filters[12] = socialMedia.isSelected();
            filters[13] = blog.isSelected();
            filters[14] = hobbies.isSelected();
            filters[15] = travel.isSelected();


            String startDate;
            //LineChartController.startDate = startDate;
            String endDate;
            if (!dataLabel.getText().equals("")) {
                String receivedDateRange = dataLabel.getText();
                String[] parts = receivedDateRange.split(" - ");
                startDate = parts[0];
                endDate = parts[1];
                try {
                    LineChartController.startDate1 = new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
                    LineChartController.endDate1 = new SimpleDateFormat("yyyy-MM-dd").parse(endDate);
                } catch (ParseException parseException) {
                    parseException.printStackTrace();
                }
            } else {
                // Get date range from csv file if the user does not specify
                startDate = impressionLog.getCell(0, 0).split(" ")[0];
                endDate = impressionLog.getCell(0, impressionLog.getLength() - 1).split(" ")[0];
            }

            noImpressions.changeMetric(KeyMetrics.getNumberOfImpressionsFiltered(impressionLog, filters, startDate, endDate));
            noClicks.changeMetric(KeyMetrics.getNumberOfClicksFiltered(clickLog, impressionLog, filters, startDate, endDate));
            noUniques.changeMetric(KeyMetrics.getNumberOfUniquesFiltered(clickLog, impressionLog, filters, startDate, endDate));
            noBounces.changeMetric(KeyMetrics.getNumberOfBouncesFiltered(serverLog, bouncePageVisits, bounceTimeSpent, impressionLog, filters, startDate, endDate));
            noConversions.changeMetric(KeyMetrics.getNumberOfConversionsFiltered(serverLog, impressionLog, filters, startDate, endDate));
            totalCost.changeMetric(KeyMetrics.getTotalCostFiltered(impressionLog, clickLog, filters, startDate, endDate));
            ctr.changeMetric(KeyMetrics.getCTRFiltered(impressionLog, clickLog, filters, startDate, endDate));
            cpa.changeMetric(KeyMetrics.getCPAFiltered(impressionLog, clickLog, serverLog, filters, startDate, endDate));
            cpm.changeMetric(KeyMetrics.getCPMFiltered(impressionLog, clickLog, filters, startDate, endDate));
            bounceRate.changeMetric(KeyMetrics.getBounceRateFiltered(clickLog, serverLog, impressionLog, bounceTimeSpent, bouncePageVisits, filters, startDate, endDate));
            LineChartController.male = maleBox.isSelected();
            LineChartController.female = femaleBox.isSelected();
            LineChartController.age25 = age1.isSelected();
            LineChartController.age34 = age2.isSelected();
            LineChartController.age44 = age3.isSelected();
            LineChartController.age54 = age4.isSelected();
            LineChartController.age100 = age5.isSelected();
            LineChartController.low = low.isSelected();
            LineChartController.medium = medium.isSelected();
            LineChartController.high = high.isSelected();
            LineChartController.news = news.isSelected();
            LineChartController.shopping = shopping.isSelected();
            LineChartController.socialMedia = socialMedia.isSelected();
            LineChartController.blog = blog.isSelected();
            LineChartController.hobbies = hobbies.isSelected();
            LineChartController.travel = travel.isSelected();

            try {
                applyButtonListener.applyButton(1, configName.getText());
            } catch (ParseException | IOException | CsvException | InterruptedException parseException) {
                parseException.printStackTrace();
            }


        });

        Button secondApplyButton = new Button("Apply List 2");
        secondApplyButton.getStyleClass().add("menuItem");
        secondApplyButton.setOnMouseClicked(e -> {
            boolean[] filters = new boolean[16];
            filters[0] = maleBox.isSelected();
            filters[1] = femaleBox.isSelected();
            filters[2] = age1.isSelected();
            filters[3] = age2.isSelected();
            filters[4] = age3.isSelected();
            filters[5] = age4.isSelected();
            filters[6] = age5.isSelected();
            filters[7] = low.isSelected();
            filters[8] = medium.isSelected();
            filters[9] = high.isSelected();
            filters[10] = news.isSelected();
            filters[11] = shopping.isSelected();
            filters[12] = socialMedia.isSelected();
            filters[13] = blog.isSelected();
            filters[14] = hobbies.isSelected();
            filters[15] = travel.isSelected();


            String startDate;
            //LineChartController.startDate = startDate;
            String endDate;
            if (!dataLabel.getText().equals("")) {
                String receivedDateRange = dataLabel.getText();
                String[] parts = receivedDateRange.split(" - ");
                startDate = parts[0];
                endDate = parts[1];
                try {
                    LineChartController.startDate2 = new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
                    LineChartController.endDate2 = new SimpleDateFormat("yyyy-MM-dd").parse(endDate);
                } catch (ParseException parseException) {
                    parseException.printStackTrace();
                }
            } else {
                // Get date range from csv file if the user does not specify
                startDate = impressionLog.getCell(0,0).split(" ")[0];
                endDate = impressionLog.getCell(0,impressionLog.getLength()-1).split(" ")[0];
            }


            noImpressions2.changeMetric(KeyMetrics.getNumberOfImpressionsFiltered(impressionLog, filters, startDate, endDate));
            noClicks2.changeMetric(KeyMetrics.getNumberOfClicksFiltered(clickLog, impressionLog, filters, startDate, endDate));
            noUniques2.changeMetric(KeyMetrics.getNumberOfUniquesFiltered(clickLog, impressionLog, filters, startDate, endDate));
            noBounces2.changeMetric(KeyMetrics.getNumberOfBouncesFiltered(serverLog, bouncePageVisits2, bounceTimeSpent2, impressionLog, filters, startDate, endDate));
            noConversions2.changeMetric(KeyMetrics.getNumberOfConversionsFiltered(serverLog, impressionLog, filters, startDate, endDate));
            totalCost2.changeMetric(KeyMetrics.getTotalCostFiltered(impressionLog, clickLog, filters, startDate, endDate));
            ctr2.changeMetric(KeyMetrics.getCTRFiltered(impressionLog, clickLog, filters, startDate, endDate));
            cpa2.changeMetric(KeyMetrics.getCPAFiltered(impressionLog, clickLog, serverLog, filters, startDate, endDate));
            cpm2.changeMetric(KeyMetrics.getCPMFiltered(impressionLog, clickLog, filters, startDate, endDate));
            bounceRate2.changeMetric(KeyMetrics.getBounceRateFiltered(clickLog, serverLog, impressionLog, bounceTimeSpent2, bouncePageVisits2, filters, startDate, endDate));
            LineChartController.male1 = maleBox.isSelected();
            LineChartController.female1 = femaleBox.isSelected();
            LineChartController.age251 = age1.isSelected();
            LineChartController.age341 = age2.isSelected();
            LineChartController.age441 = age3.isSelected();
            LineChartController.age541 = age4.isSelected();
            LineChartController.age1001 = age5.isSelected();
            LineChartController.low1 = low.isSelected();
            LineChartController.medium1 = medium.isSelected();
            LineChartController.high1 = high.isSelected();
            LineChartController.news1 = news.isSelected();
            LineChartController.shopping1 = shopping.isSelected();
            LineChartController.socialMedia1 = socialMedia.isSelected();
            LineChartController.blog1 = blog.isSelected();
            LineChartController.hobbies1 = hobbies.isSelected();
            LineChartController.travel1 = travel.isSelected();
            try {
                applyButtonListener.applyButton(2, configName.getText());
            } catch (ParseException | IOException | CsvException | InterruptedException parseException) {
                parseException.printStackTrace();
            }
        });

        // TOOLTIPS
        Tooltip.install(dateRange, new Tooltip("Click to open the date range options"));
        Tooltip.install(genderButton, new Tooltip("Click to open the gender options"));
        Tooltip.install(incomeButton, new Tooltip(" Click to open the income options"));
        Tooltip.install(contextButton, new Tooltip("Click to open the context options"));
        Tooltip.install(tickAll, new Tooltip("Select all"));
        Tooltip.install(clear, new Tooltip("Clear all"));
        Tooltip.install(saveButton, new Tooltip("Save configuration"));
        Tooltip.install(ageButton, new Tooltip("Click to open the age options"));
        Tooltip.install(loadButton, new Tooltip("Load configuration"));



        //** Label of filters **//
        Label title = new Label("Filters");
        title.setPadding(new Insets(10));
        title.getStyleClass().add("number");


        Button zoom1 = new Button("Left");
        Tooltip.install(zoom1, new Tooltip("Click to see only the left graph"));
        zoom1.getStyleClass().add("smallButton");
        zoom1.setOnMouseClicked(e -> zoomingListener.zooming(1));

        Button zoom2 = new Button("Right");
        Tooltip.install(zoom2, new Tooltip("Click to see only the right graph"));
        zoom2.getStyleClass().add("smallButton");
        zoom2.setOnMouseClicked(e -> zoomingListener.zooming(2));

        Button zoom3 = new Button("Both");
        Tooltip.install(zoom3, new Tooltip("Click to see both of the graphs"));
        zoom3.getStyleClass().add("smallButton");
        zoom3.setOnMouseClicked(e -> zoomingListener.zooming(3));

        HBox zoomButtons = new HBox();
        zoomButtons.getChildren().addAll(zoom1, zoom2, zoom3);
        vBox.getChildren().addAll(title, configName, dateRange, genderBox, ageBox, incomeBox, contextBox, applyButton, secondApplyButton, zoomButtons, saveLoadHolder);
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.getStyleClass().add("scrollBox");
        scrollPane.setPrefViewportWidth(190);
        scrollPane.setContent(vBox);

        // All filters on by default
        femaleBox.setSelected(true);
        maleBox.setSelected(true);
        age1.setSelected(true);
        age2.setSelected(true);
        age3.setSelected(true);
        age4.setSelected(true);
        age5.setSelected(true);
        low.setSelected(true);
        medium.setSelected(true);
        high.setSelected(true);
        news.setSelected(true);
        shopping.setSelected(true);
        socialMedia.setSelected(true);
        blog.setSelected(true);
        hobbies.setSelected(true);
        travel.setSelected(true);


        return scrollPane;
    }


    /**
     * method to create a new tab
     *
     * @param clickLog      - ClickLog object
     * @param impressionLog - ImpressionLog object
     * @param serverLog     - ServerLog object
     * @param folderName    - name of the folder
     * @return the new tab
     */
    private Tab createNewTab(ClickLog clickLog, ImpressionLog impressionLog, ServerLog serverLog, String folderName) {
        SimpleDoubleProperty timeGranProperty = new SimpleDoubleProperty();
        Tab newTab = new Tab(folderName);
        Font.loadFont(getClass().getResourceAsStream("Fredoka.ttf"), 32);
        newTab.getStyleClass().add("tab");
        BorderPane pane = new BorderPane();
        Label metricListLabel = new Label("List 1");
        Label metricListLabel2 = new Label("List 2");

        applyButtonListener = (label, filterName) -> {
                if (label == 1) {
                    System.out.println("Got to firstApplyButton listener");
                    metricListLabel.setText(filterName);
                    lcc.reload();
                    lcViewer = lcc.getLcviewer();
                    pane.setCenter(lcc.getLcviewer());

                }
                if (label == 2) {
                    System.out.println("Got to secondApplyButton listener");
                    metricListLabel2.setText(filterName);
                    lcc1.reload();
                    lcViewer = lcc1.getLcviewer();
                    pane.setRight(lcc1.getLcviewer());
                }
            };


        zoomingListener = whichOne -> {
            System.out.println("Zooming listener was called");
            if(whichOne == 1){
                pane.rightProperty().set(null);
                pane.centerProperty().set(null);
                lcViewer = lcc.getLcviewer();
                pane.setCenter(lcViewer);
            }
            if(whichOne == 2){
                pane.rightProperty().set(null);
                pane.centerProperty().set(null);
                lcViewer = lcc1.getLcviewer();
                pane.setCenter(lcViewer);
            }
            if(whichOne == 3){
                pane.rightProperty().set(null);
                pane.centerProperty().set(null);
                pane.setCenter(lcc.getLcviewer());
                pane.setRight(lcc1.getLcviewer());
            }
        };

        this.clickLog = clickLog;
        this.impressionLog = impressionLog;
        this.serverLog = serverLog;

        timeGranProperty.addListener((observableValue, number, t1) -> {
            System.out.println(observableValue.getValue().intValue());
            lcc.setGroupingInterval(observableValue.getValue().intValue());
            lcc1.setGroupingInterval(observableValue.getValue().intValue());
            if(lcc != null) {
                lcc.reload();
                lcViewer = lcc.getLcviewer();
                pane.setCenter(lcViewer);
            }
            if(lcc1 != null) {
                lcc1.reload();
                lcViewer = lcc1.getLcviewer();
                pane.setRight(lcViewer);
            }
        });

        lcc = new LineChartController(2, clickLog, impressionLog, serverLog);
        lcc1 = new LineChartController(2, clickLog, impressionLog, serverLog);

        //placeholder for graphs
        var tempPanel = new Pane();
        var tempTxt = new Text("Click on a metric to display its progression over time!");
        tempTxt.getStyleClass().add("text-item");
        var tempTf = new TextFlow(tempTxt);
        //tempPanel.getChildren().add(tempTf);
        tempTf.setLayoutX(10);
        tempTf.setLayoutY(7);
        tempTf.setStyle("-fx-font-size: 24");
        pane.setCenter(tempPanel);
        //pane.setRight(new Label("Test"));

        // ####     Other UI components     ####

        //Button to allow another folder to be imported
        Button additionalFile = new Button("Load Additional Folder");
        additionalFile.getStyleClass().add("menuItem");

        additionalFile.setOnAction(e -> {
            //create a new FileScreen Instance
            FileScreen newScreen = new FileScreen();
            try {
                // add a listener for when newScreen has extracted the data from the chosen folder
                newScreen.setOnAdditionalCampaign((clickLog1, impressionLog1, serverLog1, folderName1) -> mainTabPane.getTabs().add(createNewTab(clickLog1, impressionLog1, serverLog1, folderName1)));
                // start the new FileScreen
                newScreen.start(new Stage());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        //Button for the histogram and the action in case it is pressed
        Button histogramButton = new Button("Open Histogram of click cost");
        histogramButton.getStyleClass().add("menuItem");
        histogramButton.setOnAction(e -> {
            //if the clockLog data is NOT empty then show histogram
            if (this.clickLog != null) {
                showCharts(stage, scene);
            }
        });


        // ####     Owens Buttons for Graphs     ####
        Button saveGraphButton = new Button("Save Graph to PNG");
        saveGraphButton.getStyleClass().add("menuItem");
        saveGraphButton.setOnAction(e -> {

            FileChooser save = new FileChooser();
            save.setInitialFileName("histogram-of-click-cost.png");
            save.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG files", ".png"));
            File file = save.showSaveDialog(stage);
            try {
                SaveAsPNG.saveLineChart(lcViewer.getLineChart(), file.getAbsolutePath());
            } catch (IOException a) {
                a.printStackTrace();
            }

            System.out.println("Graph save button clicked");
        });

        // Make metric buttons
        noImpressions = new MetricButton("Number of impressions", KeyMetrics.getNumberOfImpressions(impressionLog));
        noImpressions.setOnMouseClicked(mouseEvent -> Platform.runLater(() -> {
            LineChartController.isFirst = true;
            LineChartController.isSecond = false;
            lcc.loadGraph(1);

            pane.centerProperty().set(null);
            lcViewer = lcc.getLcviewer();
            pane.setCenter(lcViewer);
            currentSlider = makeSlider();
            timeGranProperty.bind(currentSlider.valueProperty());
            Label hourLabel = new Label("Hourly");
            hourLabel.setPadding(new Insets(4));
            Label dayLabel = new Label("Daily");
            dayLabel.setPadding(new Insets(4));
            HBox layout = new HBox();
            layout.getChildren().addAll(hourLabel, currentSlider, dayLabel);
            pane.setBottom(layout);
        }));
        noClicks = new MetricButton("Number of Clicks", KeyMetrics.getNumberOfClicks(clickLog));
        noClicks.setOnMouseClicked(mouseEvent -> Platform.runLater(() -> {
            LineChartController.isFirst = true;
            LineChartController.isSecond = false;
            lcc.loadGraph(2);

            //pane.centerProperty().set(null);
            lcViewer = lcc.getLcviewer();
            pane.setCenter(lcViewer);
            currentSlider = makeSlider();
            timeGranProperty.bind(currentSlider.valueProperty());
            Label hourLabel = new Label("Hourly");
            hourLabel.setPadding(new Insets(4));
            Label dayLabel = new Label("Daily");
            dayLabel.setPadding(new Insets(4));
            HBox layout = new HBox();
            layout.getChildren().addAll(hourLabel, currentSlider, dayLabel);
            pane.setBottom(layout);
        }));
        noUniques = new MetricButton("Number of Uniques", KeyMetrics.getNumberOfUniques(clickLog));
        noUniques.setOnMouseClicked(mouseEvent -> Platform.runLater(() -> {
            LineChartController.isFirst = true;
            LineChartController.isSecond = false;
            lcc.loadGraph(3);
            pane.centerProperty().set(null);
            lcViewer = lcc.getLcviewer();
            pane.setCenter(lcViewer);
            currentSlider = makeSlider();
            timeGranProperty.bind(currentSlider.valueProperty());
            Label hourLabel = new Label("Hourly");
            hourLabel.setPadding(new Insets(4));
            Label dayLabel = new Label("Daily");
            dayLabel.setPadding(new Insets(4));
            HBox layout = new HBox();
            layout.getChildren().addAll(hourLabel, currentSlider, dayLabel);
            pane.setBottom(layout);
        }));
        noBounces = new MetricButton("Number of Bounces", KeyMetrics.getNumberOfBounces(serverLog, bouncePageVisits, bounceTimeSpent));
        noBounces.setOnMouseClicked(mouseEvent -> Platform.runLater(() -> {
            LineChartController.isFirst = true;
            LineChartController.isSecond = false;
            lcc.loadGraph(4);
            pane.centerProperty().set(null);
            lcViewer = lcc.getLcviewer();
            pane.setCenter(lcViewer);
            currentSlider = makeSlider();
            timeGranProperty.bind(currentSlider.valueProperty());
            Label hourLabel = new Label("Hourly");
            hourLabel.setPadding(new Insets(4));
            Label dayLabel = new Label("Daily");
            dayLabel.setPadding(new Insets(4));
            HBox layout = new HBox();
            layout.getChildren().addAll(hourLabel, currentSlider, dayLabel);
            pane.setBottom(layout);
        }));
        noConversions = new MetricButton("Number of Conversions", KeyMetrics.getNumberOfConversions(serverLog));
        noConversions.setOnMouseClicked(mouseEvent -> Platform.runLater(() -> {
            LineChartController.isFirst = true;
            LineChartController.isSecond = false;
            lcc.loadGraph(5);

            pane.centerProperty().set(null);
            lcViewer = lcc.getLcviewer();
            pane.setCenter(lcViewer);
            currentSlider = makeSlider();
            timeGranProperty.bind(currentSlider.valueProperty());
            Label hourLabel = new Label("Hourly");
            hourLabel.setPadding(new Insets(4));
            Label dayLabel = new Label("Daily");
            dayLabel.setPadding(new Insets(4));
            HBox layout = new HBox();
            layout.getChildren().addAll(hourLabel, currentSlider, dayLabel);
            pane.setBottom(layout);
        }));
        totalCost = new MetricButton("Total Cost (£)", KeyMetrics.getTotalCost(impressionLog, clickLog));
        totalCost.setOnMouseClicked(mouseEvent -> Platform.runLater(() -> {
            LineChartController.isFirst = true;
            LineChartController.isSecond = false;
            lcc.loadGraph(6);

            pane.centerProperty().set(null);
            lcViewer = lcc.getLcviewer();
            pane.setCenter(lcViewer);
            currentSlider = makeSlider();
            timeGranProperty.bind(currentSlider.valueProperty());
            Label hourLabel = new Label("Hourly");
            hourLabel.setPadding(new Insets(4));
            Label dayLabel = new Label("Daily");
            dayLabel.setPadding(new Insets(4));
            HBox layout = new HBox();
            layout.getChildren().addAll(hourLabel, currentSlider, dayLabel);
            pane.setBottom(layout);
        }));
        ctr = new MetricButton("CTR", KeyMetrics.getCTR(impressionLog, clickLog));
        cpa = new MetricButton("CPA", KeyMetrics.getCPA(impressionLog, clickLog, serverLog));
        cpm = new MetricButton("CPM", KeyMetrics.getCPM(impressionLog, clickLog));
        bounceRate = new MetricButton("Bounce Rate", KeyMetrics.getBounceRate(clickLog, serverLog, bouncePageVisits, bounceTimeSpent));
        bounceRate.setOnMouseClicked(e -> {
            ChangeBounceDefinition newDef = new ChangeBounceDefinition(bouncePageVisits, bounceTimeSpent);
            newDef.setChangeBounceDefListener((pageVisit, bounceTime) -> {
                bounceRate.changeMetric(KeyMetrics.getBounceRate(clickLog, serverLog, pageVisit, bounceTime));
                noBounces.changeMetric(KeyMetrics.getNumberOfBounces(serverLog, pageVisit, bounceTime));
                LineChartController.minTimeSpentt = bounceTime;
                LineChartController.minVisit = pageVisit;
                bouncePageVisits = pageVisit;
                bounceTimeSpent = bounceTime;
            });
            System.out.println("Bounce Rate button clicked");
        });
        metricListLabel.setPadding(new Insets(30));
        metricListLabel.getStyleClass().add("number");

        // Make metric buttons 2
        noImpressions2 = new MetricButton("Number of impressions", KeyMetrics.getNumberOfImpressions(impressionLog));
        noImpressions2.setOnMouseClicked(mouseEvent -> Platform.runLater(() -> {
            LineChartController.isFirst = false;
            LineChartController.isSecond = true;
            lcc1.loadGraph(1);
            pane.rightProperty().set(null);
            lcViewer = lcc1.getLcviewer();
            pane.setRight(lcViewer);
            currentSlider = makeSlider();
            timeGranProperty.bind(currentSlider.valueProperty());
            Label hourLabel = new Label("Hourly");
            hourLabel.setPadding(new Insets(4));
            Label dayLabel = new Label("Daily");
            dayLabel.setPadding(new Insets(4));
            HBox layout = new HBox();
            layout.getChildren().addAll(hourLabel, currentSlider, dayLabel);
            pane.setBottom(layout);
        }));
        noClicks2 = new MetricButton("Number of Clicks", KeyMetrics.getNumberOfClicks(clickLog));
        noClicks2.setOnMouseClicked(mouseEvent -> Platform.runLater(() -> {
            LineChartController.isFirst = false;
            LineChartController.isSecond = true;
            lcc1.loadGraph(2);

            pane.rightProperty().set(null);
            lcViewer = lcc1.getLcviewer();
            pane.setRight(lcViewer);
            currentSlider = makeSlider();
            timeGranProperty.bind(currentSlider.valueProperty());
            Label hourLabel = new Label("Hourly");
            hourLabel.setPadding(new Insets(4));
            Label dayLabel = new Label("Daily");
            dayLabel.setPadding(new Insets(4));
            HBox layout = new HBox();
            layout.getChildren().addAll(hourLabel, currentSlider, dayLabel);
            pane.setBottom(layout);
        }));
        noUniques2 = new MetricButton("Number of Uniques", KeyMetrics.getNumberOfUniques(clickLog));
        noUniques2.setOnMouseClicked(mouseEvent -> Platform.runLater(() -> {
            LineChartController.isFirst = false;
            LineChartController.isSecond = true;
            lcc1.loadGraph(3);

            pane.rightProperty().set(null);
            lcViewer = lcc1.getLcviewer();
            pane.setRight(lcViewer);
            currentSlider = makeSlider();
            timeGranProperty.bind(currentSlider.valueProperty());
            Label hourLabel = new Label("Hourly");
            hourLabel.setPadding(new Insets(4));
            Label dayLabel = new Label("Daily");
            dayLabel.setPadding(new Insets(4));
            HBox layout = new HBox();
            layout.getChildren().addAll(hourLabel, currentSlider, dayLabel);
            pane.setBottom(layout);
        }));
        noBounces2 = new MetricButton("Number of Bounces", KeyMetrics.getNumberOfBounces(serverLog, bouncePageVisits2, bounceTimeSpent2));
        noBounces2.setOnMouseClicked(mouseEvent -> Platform.runLater(() -> {
            LineChartController.isFirst = false;
            LineChartController.isSecond = true;
            lcc1.loadGraph(4);

            pane.rightProperty().set(null);
            lcViewer = lcc1.getLcviewer();
            pane.setRight(lcViewer);
            currentSlider = makeSlider();
            timeGranProperty.bind(currentSlider.valueProperty());
            Label hourLabel = new Label("Hourly");
            hourLabel.setPadding(new Insets(4));
            Label dayLabel = new Label("Daily");
            dayLabel.setPadding(new Insets(4));
            HBox layout = new HBox();
            layout.getChildren().addAll(hourLabel, currentSlider, dayLabel);
            pane.setBottom(layout);
        }));
        noConversions2 = new MetricButton("Number of Conversions", KeyMetrics.getNumberOfConversions(serverLog));
        noConversions2.setOnMouseClicked(mouseEvent -> Platform.runLater(() -> {
            LineChartController.isFirst = false;
            LineChartController.isSecond = true;
            lcc1.loadGraph(5);

            pane.rightProperty().set(null);
            lcViewer = lcc1.getLcviewer();
            pane.setRight(lcViewer);
            currentSlider = makeSlider();
            timeGranProperty.bind(currentSlider.valueProperty());
            Label hourLabel = new Label("Hourly");
            hourLabel.setPadding(new Insets(4));
            Label dayLabel = new Label("Daily");
            dayLabel.setPadding(new Insets(4));
            HBox layout = new HBox();
            layout.getChildren().addAll(hourLabel, currentSlider, dayLabel);
            pane.setBottom(layout);
        }));
        totalCost2 = new MetricButton("Total Cost (£)", KeyMetrics.getTotalCost(impressionLog, clickLog));
        totalCost2.setOnMouseClicked(mouseEvent -> Platform.runLater(() -> {
            LineChartController.isFirst = false;
            LineChartController.isSecond = true;
            lcc1.loadGraph(6);

            pane.rightProperty().set(null);
            lcViewer = lcc1.getLcviewer();
            pane.setRight(lcViewer);
            currentSlider = makeSlider();
            timeGranProperty.bind(currentSlider.valueProperty());
            Label hourLabel = new Label("Hourly");
            hourLabel.setPadding(new Insets(4));
            Label dayLabel = new Label("Daily");
            dayLabel.setPadding(new Insets(4));
            HBox layout = new HBox();
            layout.getChildren().addAll(hourLabel, currentSlider, dayLabel);
            pane.setBottom(layout);
        }));
        ctr2 = new MetricButton("CTR", KeyMetrics.getCTR(impressionLog, clickLog));
        cpa2 = new MetricButton("CPA", KeyMetrics.getCPA(impressionLog, clickLog, serverLog));
        cpm2 = new MetricButton("CPM", KeyMetrics.getCPM(impressionLog, clickLog));
        bounceRate2 = new MetricButton("Bounce Rate", KeyMetrics.getBounceRate(clickLog, serverLog, bouncePageVisits2, bounceTimeSpent2));
        bounceRate2.setOnMouseClicked(e -> {
            ChangeBounceDefinition newDef = new ChangeBounceDefinition(bouncePageVisits2, bounceTimeSpent2);
            newDef.setChangeBounceDefListener((pageVisit, bounceTime) -> {
                bounceRate2.changeMetric(KeyMetrics.getBounceRate(clickLog, serverLog, pageVisit, bounceTime));
                noBounces2.changeMetric(KeyMetrics.getNumberOfBounces(serverLog, pageVisit, bounceTime));
                LineChartController.minTimeSpentt = bounceTime;
                LineChartController.minVisit = pageVisit;
                bouncePageVisits2 = pageVisit;
                bounceTimeSpent2 = bounceTime;
            });
            System.out.println("Bounce Rate 2 button clicked");
        });
        metricListLabel2.setPadding(new Insets(30));
        metricListLabel2.getStyleClass().add("number");

        // Tooltips for hovering over metric
        Tooltip bounceRateTip = new Tooltip("Click to redefine bounce definition");
        Tooltip.install(bounceRate, bounceRateTip);
        Tooltip CTRtip = new Tooltip("Click Through Rate");
        Tooltip CPAtip = new Tooltip("Cost Per Acquisition");
        Tooltip CPMtip = new Tooltip("Cost Per Thousand Impressions");
        Tooltip.install(ctr, CTRtip);
        Tooltip.install(cpa, CPAtip);
        Tooltip.install(cpm, CPMtip);
        Tooltip.install(bounceRate2, bounceRateTip);
        Tooltip.install(ctr2, CTRtip);
        Tooltip.install(cpa2, CPAtip);
        Tooltip.install(cpm2, CPMtip);
        Tooltip.install(noImpressions, new Tooltip("Number of impressions, Click to open the chart"));
        Tooltip.install(noClicks, new Tooltip("Number of Clicks, Click to open the chart"));
        Tooltip.install(noUniques, new Tooltip("Uniques, Click to open the chart"));
        Tooltip.install(noBounces, new Tooltip("Number of Bounces, Click to open the chart"));
        Tooltip.install(noConversions, new Tooltip("Number of Conversions, Click to open the chart"));
        Tooltip.install(totalCost, new Tooltip("Total cost, Click to open the chart"));
        Tooltip.install(noImpressions2, new Tooltip("Number of impressions, Click to open the chart"));
        Tooltip.install(noClicks2, new Tooltip("Number of Clicks, Click to open the chart"));
        Tooltip.install(noUniques2, new Tooltip("Uniques, Click to open the chart"));
        Tooltip.install(noBounces2, new Tooltip("Number of Bounces, Click to open the chart"));
        Tooltip.install(noConversions2, new Tooltip("Number of Conversions, Click to open the chart"));
        Tooltip.install(totalCost2, new Tooltip("Total cost, Click to open the chart"));

        //Regions to improve spacing
        Region metRegionL = new Region();
        HBox.setHgrow(metRegionL, Priority.ALWAYS);
        Region metRegionR = new Region();
        HBox.setHgrow(metRegionR, Priority.ALWAYS);
        //Region metRegionL2 = new Region();
        HBox.setHgrow(metRegionL, Priority.ALWAYS);
        Region metRegionR2 = new Region();
        HBox.setHgrow(metRegionR, Priority.ALWAYS);

        // Buttons go into Hbox
        HBox metricButtons = new HBox();
        metricButtons.getChildren().addAll(metricListLabel, noImpressions, noClicks, noUniques, noBounces, noConversions, totalCost, ctr, cpa, cpm, bounceRate, metRegionR);
        HBox metricButtons2 = new HBox();
        metricButtons2.getChildren().addAll(metricListLabel2, noImpressions2, noClicks2, noUniques2, noBounces2, noConversions2, totalCost2, ctr2, cpa2, cpm2, bounceRate2, metRegionR2);

        //VBox to hold the 2 buttons + spacing regions
        Region regionL = new Region();
        HBox.setHgrow(regionL, Priority.ALWAYS);
        Region regionM1 = new Region();
        HBox.setHgrow(regionM1, Priority.ALWAYS);
        Region regionM2 = new Region();
        HBox.setHgrow(regionM2, Priority.ALWAYS);
        Region regionR = new Region();
        HBox.setHgrow(regionR, Priority.ALWAYS);
        Region regionR2 = new Region();
        HBox.setHgrow(regionR2, Priority.ALWAYS);

        Region regionREnd = new Region();
        HBox.setHgrow(regionREnd, Priority.ALWAYS);

        // create a button to show csv data
        Button showCSVDataButton = new Button("Show CSV Data");
        showCSVDataButton.getStyleClass().add("menuItem");
        showCSVDataButton.setOnAction(actionEvent -> {
            // create a reader to read clicklog
            CSVDataReader reader1 = new CSVDataReader();
            //reader1.readCsv(clickLog.getFilename());
            reader1.readCsv(clickLog);
            TableView<Row> tableView1 = JavaFxUtils.buildTableViewFromCsv(reader1.getColumns(), reader1.getRows());

            // create a reader to read impression log
            CSVDataReader reader2 = new CSVDataReader();
            //reader2.readCsv(impressionLog.getFilename());
            reader2.readCsv(impressionLog);
            TableView<Row> tableView2 = JavaFxUtils.buildTableViewFromCsv(reader2.getColumns(), reader2.getRows());

            // create a reader to read server log
            CSVDataReader reader3 = new CSVDataReader();
            //reader3.readCsv(serverLog.getFilename());
            reader3.readCsv(serverLog);
            TableView<Row> tableView3 = JavaFxUtils.buildTableViewFromCsv(reader3.getColumns(), reader3.getRows());

            // create a tab pane
            TabPane tabPane = new TabPane();
            tabPane.setPrefHeight(800);
            tabPane.setPrefWidth(600);

            // create three tabs to show tableviews
            Tab tb1 = new Tab("click_log");
            tb1.setContent(tableView1);

            Tab tb2 = new Tab("impression_log");
            tb2.setContent(tableView2);

            Tab tb3 = new Tab("server_log");
            tb3.setContent(tableView3);

            tabPane.getTabs().addAll(tb1, tb2, tb3);
            tabPane.setSide(Side.BOTTOM);

            // create a scene
            Scene excelScene = new Scene(tabPane, 800, 600);
            // create a window
            Stage stage1 = new Stage();
            stage1.setScene(excelScene);
            stage1.setWidth(800);
            stage1.setHeight(600);
            stage1.show();
        });

        Button statsCalcs = new Button("Statistics Calculators");
        statsCalcs.getStyleClass().add("menuItem");
        statsCalcs.setOnAction(e -> {
            StatisticsCalculatorsMenu statsCalcScreen = new StatisticsCalculatorsMenu();
            System.out.println("Statistics Calculators button clicked");
        });

        // Colour theme mode button
        Button themeButton = new Button("Theme");
        themeButton.getStyleClass().add("menuItem");
        AtomicInteger cycle = new AtomicInteger(0);
        themeButton.setOnAction(actionEvent -> {

            try {
                Path stylePath = Paths.get(getClass().getResource("style.css").toURI());
                Path style2Path = Paths.get(getClass().getResource("style2.css").toURI());
                Path style3Path = Paths.get(getClass().getResource("style3.css").toURI());

                Files.move(stylePath, stylePath.resolveSibling("styleOne.css"));
                Path tempOnePath = Paths.get(getClass().getResource("styleOne.css").toURI());
                Files.move(style2Path, style2Path.resolveSibling("style.css"));
                Files.move(style3Path, style3Path.resolveSibling("styleThree.css"));
                Path tempThreePath = Paths.get(getClass().getResource("styleThree.css").toURI());
                //Path tempThreePath = Paths.get("src/main/resources/com/example/seg/styleThree.css");
                Files.move(tempOnePath, tempOnePath.resolveSibling("style3.css"));
                Files.move(tempThreePath, tempThreePath.resolveSibling("style2.css"));

                scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

                scene.getStylesheets().remove(0);

                //switch (cycle.get()) {
                //    case 0 -> {
                //        scene.getStylesheets().add(getClass().getResource("style2.css").toExternalForm());
                //        Files.move(style2Path, style2Path.resolveSibling("styletemp.css"));
                //        Files.move(stylePath, stylePath.resolveSibling("styl2.css"));
                //        Files.move(tempPath, tempPath.resolveSibling("style3.css"));
                //    }
                //    case 1 -> {
                //        scene.getStylesheets().add(getClass().getResource("style3.css").toExternalForm());
                //        Files.move(style3Path, stylePath.resolveSibling("style.css"));
                //        Files.move(stylePath, stylePath.resolveSibling("style3.css"));
                //    }
                //    case 2 -> {
                //        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
                //        cycle.set(-1);
                //    }
                //}
                cycle.set(cycle.get() + 1);
            } catch (Exception exception) {
                exception.printStackTrace();
            }

        });

        //TOLTIPS
        Tooltip.install(showCSVDataButton, new Tooltip("Show csv data in spreadsheet"));
        Tooltip.install(histogramButton, new Tooltip("Open histogram of click cost"));
        Tooltip.install(themeButton, new Tooltip("Change colour theme"));
        Tooltip.install(additionalFile, new Tooltip("Open a new file a new tab"));
        Tooltip.install(saveGraphButton, new Tooltip("Save graph to a PNG file"));
        Tooltip.install(statsCalcs, new Tooltip("Open window of statistics"));

        // Buttons for other UI elements
        HBox uiButtons = new HBox();
        uiButtons.setPadding(new Insets(4, 4, 4, 4));
        uiButtons.getChildren().addAll(additionalFile, regionM1, saveGraphButton, regionM2, histogramButton, regionR, showCSVDataButton, regionR2, themeButton, regionREnd, statsCalcs);

        //vbox to hold the top buttons
        VBox buttonBox = new VBox();
        buttonBox.getStyleClass().add("box");
        buttonBox.getChildren().addAll(uiButtons, metricButtons, metricButtons2);
        buttonBox.setAlignment(Pos.CENTER);

        //add Hbox to the Borderpane
        pane.setTop(buttonBox);
        pane.setLeft(filters());


        newTab.setContent(pane);


        System.out.println("New tab created: " + folderName);

        return newTab;

    }

    private Slider makeSlider() {
        Slider slider = new Slider(1, 2, 2);
        slider.getStyleClass().add("text-item");
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(false);
        slider.setMinorTickCount(0);
        slider.setMajorTickUnit(1);
        slider.setSnapToTicks(true);
        slider.setMaxWidth(125);
        return slider;
    }
}
