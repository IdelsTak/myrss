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
    requires java.desktop;
    requires javafx.web;

    exports com.github.idelstak.myrss.channels;
    opens com.github.idelstak.myrss.channels;
    exports com.github.idelstak.myrss.components;
    opens com.github.idelstak.myrss.components;
    exports com.github.idelstak.myrss.items;
    opens com.github.idelstak.myrss.items;
    exports com.github.idelstak.myrss.launch;
    opens com.github.idelstak.myrss.launch;
}