package com.github.idelstak.myrss.components;

import javafx.fxml.*;
import javafx.scene.*;

import java.io.*;

public enum Fxml {
    CHANNELS_TREE("/com/github/idelstak/myrss/channels-tree.fxml"),
    ITEMS_TABLE("/com/github/idelstak/myrss/items-table.fxml"),
    ITEMS_CONTENT("/com/github/idelstak/myrss/item-content.fxml"),
    MAIN_VIEW("/com/github/idelstak/myrss/main-view.fxml"),
    NEW_CHANNEL_DIALOG("/com/github/idelstak/myrss/new-channel-dialog.fxml");

    private final String path;
    private Parent root;
    private Object controller;

    Fxml(String path) {this.path = path;}

    public Parent root() throws IOException {
        load();
        return root;
    }

    private void load() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
        root = loader.load();
        controller = loader.getController();
    }

    public Object controller() throws IOException {
        if (root == null) {
            load();
        }
        return controller;
    }
}