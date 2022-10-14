module com.example.seg {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.opencsv;
    requires org.junit.jupiter.api;
    requires javafx.graphics;
    requires java.desktop;
    requires javafx.swing;

    opens com.example.seg to javafx.fxml;
    exports com.example.seg;

}