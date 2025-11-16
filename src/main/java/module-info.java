module org.example.plateformereservation {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.sql;
    requires javafx.graphics;
    requires javafx.base;

    opens org.example.controller to javafx.fxml;
    opens org.example to javafx.fxml;
    exports org.example;
}