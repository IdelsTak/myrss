module com.github.idelstak.myrss {
    requires javafx.controls;
    requires javafx.fxml;

    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.boxicons;
    requires org.apache.commons.text;
    requires com.rometools.rome;
    requires rome.fetcher;
    requires org.apache.commons.lang3;

    exports com.github.idelstak.myrss;
    exports com.github.idelstak.myrss.channels;
    opens com.github.idelstak.myrss;
    opens com.github.idelstak.myrss.channels;
}