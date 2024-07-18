module com.github.idelstak.myrss {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.apache.commons.text;
    requires com.rometools.rome;
    requires rome.fetcher;
    requires org.apache.commons.lang3;

    opens com.github.idelstak.myrss to javafx.fxml;
    exports com.github.idelstak.myrss;
}