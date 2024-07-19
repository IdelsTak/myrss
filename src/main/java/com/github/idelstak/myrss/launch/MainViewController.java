package com.github.idelstak.myrss.launch;

import com.github.idelstak.myrss.channels.*;
import com.github.idelstak.myrss.components.*;
import com.github.idelstak.myrss.items.*;
import com.rometools.rome.feed.rss.*;
import com.rometools.rome.feed.synd.*;
import com.rometools.rome.io.*;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;

import java.io.*;

import static com.github.idelstak.myrss.components.Fxml.*;
import static javafx.application.Platform.*;
import static javafx.scene.layout.AnchorPane.*;

public class MainViewController extends FxmlController {

    private final ObservableList<Channel> channels;
    private final ObjectProperty<File> selectedRssFile;
    @FXML
    private AnchorPane itemsPane;
    @FXML
    private AnchorPane itemContentPane;
    @FXML
    private AnchorPane channelsPane;
    @FXML
    private HBox statusBox;
    @FXML
    private MenuItem addSubscriptionMenuItem;
    @FXML
    private SplitMenuButton addSubscriptionButton;

    public MainViewController() {
        channels = FXCollections.observableArrayList();
        selectedRssFile = new SimpleObjectProperty<>();
    }

    public void setChannels(ObservableList<Channel> channels) {
        Bindings.bindContentBidirectional(this.channels, channels);
    }

    public void setSelectedRssFile(ObjectProperty<File> selectedRssFile) {
        this.selectedRssFile.bindBidirectional(selectedRssFile);
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

        selectedRssFile.addListener((_, _, file) -> {
            if (file == null) {
                return;
            }

            SyndFeed feed;
            try {
                feed = new SyndFeedInput().build(file);
            } catch (IOException | FeedException e) {
                System.out.println("e = " + e);
                throw new RuntimeException(e);
            }

            runLater(() -> channels.add((Channel) feed.createWireFeed()));
        });
    }

    @FXML
    private void addSubscriptionFromLink(ActionEvent event) {
    }

    @FXML
    private void addSubscriptionFromFile(ActionEvent event) {
        Window owner = addSubscriptionButton.getScene().getWindow();
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select RSS File");
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("RSS files", "*.xml", "*.rss");
        chooser.setSelectedExtensionFilter(filter);

        File file = chooser.showOpenDialog(owner);

        if (file != null) {
            selectedRssFile.set(file);
        }
    }

    private static void anchor(Node node) {
        setTopAnchor(node, 0.0D);
        setRightAnchor(node, 0.0D);
        setBottomAnchor(node, 0.0D);
        setLeftAnchor(node, 0.0D);
    }
}