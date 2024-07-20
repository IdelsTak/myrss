package com.github.idelstak.myrss.launch;

import com.rometools.rome.feed.rss.*;
import javafx.application.*;
import javafx.collections.*;
import javafx.scene.*;
import javafx.stage.*;

import static com.github.idelstak.myrss.components.Fxml.*;

public class MyRssFx extends Application {

    private final ObservableList<Channel> channels;

    public MyRssFx() {channels = FXCollections.observableArrayList();}

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = MAIN_VIEW.root();
        MainViewController controller = (MainViewController) MAIN_VIEW.controller();
        controller.setChannels(channels);

        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("My RSS");
        primaryStage.show();
    }
}