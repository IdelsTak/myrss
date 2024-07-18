module com.github.idelstak.myrss {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;

    opens com.github.idelstak.myrss to javafx.fxml;
    exports com.github.idelstak.myrss;
}