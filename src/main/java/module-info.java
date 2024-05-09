module com.alphafit.alphafit {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires itextpdf;
    requires java.mail;


    opens com.alphafit.alphafit to javafx.fxml;
    exports com.alphafit.alphafit;
    exports Main;
    opens Main to javafx.fxml;
    exports Util;
    opens Util to javafx.fxml;
    exports GUI;
    opens GUI to javafx.fxml;
}