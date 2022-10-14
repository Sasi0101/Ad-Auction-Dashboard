package com.example.seg;

import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

public class JavaFxUtils {
    /**
     * Build a table view from column names and rows data
     * @param columnNames column names
     * @param rows rows data
     * @return the tableview contains the data
     */
    public static TableView<Row> buildTableViewFromCsv(List<String> columnNames, List<Row> rows) {
        // create a table view
        TableView<Row> tableView = new TableView<>();
        for (String colName : columnNames) {
            // create each column
            TableColumn<Row, String> column = new TableColumn<>(colName);
            // set width
            column.setMinWidth(140);
            // set factory
            column.setCellValueFactory(new CellFactory());
            // add to tab view
            tableView.getColumns().add(column);
        }
        // set rows data
        tableView.setItems(FXCollections.observableList(rows));
        return tableView;
    }

}