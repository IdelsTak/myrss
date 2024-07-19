package com.github.idelstak.myrss.channels;

import com.github.idelstak.myrss.components.*;
import com.rometools.rome.feed.rss.*;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import org.kordamp.ikonli.javafx.*;

import java.util.*;

import static javafx.application.Platform.*;

public class ChannelsTreeController extends FxmlController {

    private final ObservableList<Channel> channels;
    private final ObjectProperty<Channel> selectedChannel;
    @FXML
    private TreeView<Object> channelsTree;

    public ChannelsTreeController() {
        channels = FXCollections.observableArrayList();
        selectedChannel = new SimpleObjectProperty<>();
    }

    public void setChannels(ObservableList<Channel> channels) {
        Bindings.bindContentBidirectional(this.channels, channels);
    }

    public ObjectProperty<Channel> selectedChannel() {
        return selectedChannel;
    }

    @Override
    protected void initialize() {
        channelsTree.setCellFactory(_ -> new TreeCell<>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    if (item instanceof Channel) {
                        setText(((Channel) item).getTitle());
                        setGraphic(new FontIcon("bx-rss"));
                    } else {
                        setText(item.toString());
                    }
                }
            }
        });
        TreeItem<Object> root = new TreeItem<>("Channels");
        root.setExpanded(true);
        channelsTree.setRoot(root);
        channelsTree.setShowRoot(false);
        TreeItem<Object> important = new TreeItem<>("Important");
        TreeItem<Object> unread = new TreeItem<>("Unread");
        TreeItem<Object> uncategorized = new TreeItem<>("Uncategorized");
        uncategorized.setExpanded(true);
        ObservableList<TreeItem<Object>> items = root.getChildren();
        items.addAll(important, unread, uncategorized);

        channels.addListener((ListChangeListener.Change<? extends Channel> change) -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    change.getAddedSubList()
                          .stream()
                          .map(channel -> new TreeItem<Object>(channel))
                          .forEach(uncategorized.getChildren()::add);

                    if (channelsTree.getSelectionModel().getSelectedIndex() == -1) {
                        runLater(() -> {
                            Optional<Found> found = findFirstChannel(channelsTree.getRoot());
                            found.map(Found::item).ifPresent(channelsTree.getSelectionModel()::select);
                        });
                    }
                }
            }
        });

        selectedChannel.bind(channelsTree.getSelectionModel().selectedItemProperty().map(treeItem -> {
            if (treeItem.getValue() instanceof Channel channel) {
                return channel;
            }
            return null;
        }));
    }

    private Optional<Found> findFirstChannel(TreeItem<Object> item) {
        if (item.isLeaf() && item.getValue() instanceof Channel channel) {
            return Optional.of(new Found(item, channel));
        } else {
            for (TreeItem<Object> child : item.getChildren()) {
                Optional<Found> found = findFirstChannel(child);
                if (found.isPresent()) {
                    return found;
                }
            }
        }

        return Optional.empty();
    }

    private record Found(TreeItem<Object> item, Channel channel) {}
}