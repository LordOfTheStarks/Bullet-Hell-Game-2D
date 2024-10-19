module com.lordstark.java2d {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.lordstark.java2d to javafx.fxml;
    exports com.lordstark.java2d;
}