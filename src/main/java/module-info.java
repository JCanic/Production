module com.example.canic7 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.slf4j;
    requires java.sql;


    opens com.example.canic8 to javafx.fxml;
    exports com.example.canic8;
}