package com.github.idelstak.myrss.channels;

import com.github.idelstak.myrss.components.*;
import com.rometools.rome.feed.rss.*;
import javafx.beans.property.*;
import javafx.concurrent.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import org.apache.commons.lang3.*;

import java.net.*;

import static javafx.application.Platform.*;

public class NewChannelDialogController extends FxmlController {

    private final ObjectProperty<Channel> channel;
    @FXML
    private Button fetchButton;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private TextField titleField;
    @FXML
    private TextField descriptionField;
    @FXML
    private TextField urlField;
    @FXML
    private DialogPane dialogPane;

    public NewChannelDialogController() {channel = new SimpleObjectProperty<>();}

    @Override
    protected void initialize() {
        urlField.skinProperty().addListener(_ -> runLater(() -> urlField.requestFocus()));
        fetchButton.disableProperty().bind(urlField.textProperty().map(StringUtils::isBlank));
        fetchButton.visibleProperty().bind(progressIndicator.visibleProperty().not());
        fetchButton.skinProperty().addListener(_ -> runLater(() -> {
            Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
            if (okButton == null) {
                return;
            }

            okButton.disableProperty().bind(channel.isNull());
        }));
        titleField.textProperty().bind(channel.map(Channel::getTitle));
        descriptionField.textProperty().bind(channel.map(Channel::getDescription));
    }

    Channel channel() {
        return channel.get();
    }

    @FXML
    private void autoDiscoverSubscription(ActionEvent event) {
        Service<Channel> service = new Service<>() {
            @Override
            protected Task<Channel> createTask() {
                return new Task<>() {
                    @Override
                    protected Channel call() throws Exception {
                        runLater(() -> updateProgress(-1, -1));
                        URL url = new CleanLink(urlField.getText()).url();
                        Channel fetched = new ChannelFetch(url).channel();
                        runLater(() -> updateProgress(1, 1));
                        return fetched;
                    }
                };
            }
        };

        progressIndicator.progressProperty().bind(service.progressProperty());
        progressIndicator.visibleProperty().bind(service.runningProperty());

        service.setOnSucceeded(stateEvent -> {
            Channel fetched = (Channel) stateEvent.getSource().getValue();
            System.out.println("fetched.getTitle() = " + fetched.getTitle());
            channel.set(fetched);

            runLater(() -> {
                dialogPane.setExpanded(false);
                dialogPane.setExpandableContent(null);
            });
        });
        service.setOnFailed(stateEvent -> {
            Throwable exception = stateEvent.getSource().getException();
            System.out.println("exception = " + exception);

            Label label = new Label(exception.getMessage());
            label.setWrapText(true);
            label.setStyle("""
                           -fx-alignment: top-left;
                           -fx-text-fill: red;
                           -fx-font-weight: bold;
                           -fx-min-height: 100px;
                           """);
            runLater(() -> {
                dialogPane.setExpanded(true);
                dialogPane.setExpandableContent(label);
            });
            channel.set(null);
        });

        service.start();
    }
}