package com.github.idelstak.myrss.launch;

import com.github.idelstak.myrss.channels.*;
import com.github.idelstak.myrss.components.*;
import com.github.idelstak.myrss.items.*;
import com.rometools.rome.feed.rss.*;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.layout.*;

import static com.github.idelstak.myrss.components.Fxml.*;
import static javafx.scene.layout.AnchorPane.*;

public class MainViewController extends FxmlController {

    private final ObservableList<Channel> channels;
    @FXML
    private AnchorPane itemsPane;
    @FXML
    private AnchorPane itemContentPane;
    @FXML
    private AnchorPane channelsPane;
    @FXML
    private HBox statusBox;

    public MainViewController() {channels = FXCollections.observableArrayList();}

    public void setChannels(ObservableList<Channel> channels) {
        Bindings.bindContentBidirectional(this.channels, channels);
    }

    @Override
    protected void initialize() throws Exception {
        Node channelsTree = CHANNELS_TREE.root();
        ChannelsTreeController controller = (ChannelsTreeController) CHANNELS_TREE.controller();
        controller.setChannels(channels);
        ObjectProperty<Channel> selectedChannel = controller.selectedChannel();
        channelsPane.getChildren().add(channelsTree);
        anchor(channelsTree);

        Node itemsTable = ITEMS_TABLE.root();
        ItemsTableController itemsController = (ItemsTableController) ITEMS_TABLE.controller();
        itemsController.setChannel(selectedChannel);
        ObjectProperty<Item> selectedItem = itemsController.selectedItem();
        itemsPane.getChildren().add(itemsTable);
        anchor(itemsTable);

        Node itemContentView = ITEMS_CONTENT.root();
        ItemContentController contentController = (ItemContentController) ITEMS_CONTENT.controller();
        contentController.setItem(selectedItem);
        itemContentPane.getChildren().add(itemContentView);
        anchor(itemContentView);
    }

    private static void anchor(Node node) {
        setTopAnchor(node, 0.0D);
        setRightAnchor(node, 0.0D);
        setBottomAnchor(node, 0.0D);
        setLeftAnchor(node, 0.0D);
    }
}