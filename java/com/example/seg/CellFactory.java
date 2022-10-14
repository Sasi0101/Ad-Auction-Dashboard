package com.example.seg;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 * Cell factory class, used to retrieve value from each row
 */
public class CellFactory implements Callback<TableColumn.CellDataFeatures<Row, String>, ObservableValue<String>> {
    @Override
    public ObservableValue<String> call(TableColumn.CellDataFeatures<Row, String> rowStringCellDataFeatures) {
        // get the index of current column
        int index = rowStringCellDataFeatures.getTableView().getColumns().indexOf(rowStringCellDataFeatures.getTableColumn());
        // get current cell value of the column
        String value = rowStringCellDataFeatures.getValue().getCellValue(index);
        // convert to observable string
        return new SimpleStringProperty(value);
    }
}
