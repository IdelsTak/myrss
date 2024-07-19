package com.github.idelstak.myrss.channels;

import com.rometools.rome.feed.rss.*;
import javafx.scene.control.*;
import javafx.stage.*;

import java.io.*;

import static com.github.idelstak.myrss.components.Fxml.*;

public class NewChannelDialog extends Dialog<Channel> {

    public NewChannelDialog(Window owner) throws IOException {
        initOwner(owner);
        setTitle("New Channel Subscription");
        DialogPane pane = (DialogPane) NEW_CHANNEL_DIALOG.root();
        NewChannelDialogController controller = (NewChannelDialogController) NEW_CHANNEL_DIALOG.controller();
        setDialogPane(pane);

        setResultConverter(type -> {
            if (type == ButtonType.OK) {
                return controller.channel();
            }

            return null;
        });
    }
}