module com.lordstark.java2d {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.lordstark.java2d to javafx.fxml;
    exports com.lordstark.java2d;
    exports com.lordstark.java2d.Menu;
    opens com.lordstark.java2d.Menu to javafx.fxml;
    exports com.lordstark.java2d.Game;
    opens com.lordstark.java2d.Game to javafx.fxml;
}