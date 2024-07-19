package com.github.idelstak.myrss.items;

import com.github.idelstak.myrss.components.*;
import com.rometools.rome.feed.rss.*;
import javafx.beans.property.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.web.*;

import static javafx.application.Platform.*;

public class ItemContentController extends FxmlController {

    private final ObjectProperty<Item> item;
    @FXML
    private Label titleLabel;
    @FXML
    private WebView contentView;

    public ItemContentController() {item = new SimpleObjectProperty<>();}

    public void setItem(ObjectProperty<Item> item) {
        if (this.item.isBound()) {
            this.item.unbind();
        }
        this.item.bind(item);
    }

    @Override
    protected void initialize() {
        titleLabel.textProperty().bind(item.map(Item::getTitle));
        item.addListener((_, _, item) -> {
            if (item == null) {
                return;
            }

            runLater(() -> contentView.getEngine().loadContent(item.getContent().getValue()));
        });
    }
}