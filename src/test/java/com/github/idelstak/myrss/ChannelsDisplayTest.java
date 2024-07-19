package com.github.idelstak.myrss;

import com.github.idelstak.myrss.channels.*;
import com.rometools.rome.feed.rss.*;
import com.rometools.rome.feed.synd.*;
import com.rometools.rome.io.*;
import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.testfx.api.*;
import org.testfx.framework.junit5.*;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

import static com.github.idelstak.myrss.components.Fxml.*;
import static javafx.application.Platform.*;
import static javafx.collections.FXCollections.*;
import static org.junit.jupiter.api.Assertions.fail;
import static org.testfx.assertions.api.Assertions.*;
import static org.testfx.framework.junit5.utils.FXUtils.*;
import static org.testfx.util.WaitForAsyncUtils.*;

@ExtendWith(ApplicationExtension.class)
public class ChannelsDisplayTest {

    private final ObservableList<Channel> channels = observableArrayList();

    @Start
    public void setup(Stage stage) {
        runLater(() -> {
            Parent root = null;
            try {
                root = CHANNELS_TREE.root();
                ChannelsTreeController controller = (ChannelsTreeController) CHANNELS_TREE.controller();
                controller.setChannels(channels);
            } catch (IOException e) {
                fail(e);
            }
            stage.setScene(new Scene(root));
            stage.show();
            stage.toFront();
        });
        waitForFxEvents();
    }

    @BeforeEach
    public void initialize() {
        runFX(channels::clear);
    }

    @TestFx
    public void displaysStandardCategories(FxRobot robot) {
        TreeView<Object> view = robot.lookup("#channelsTree").queryAs(TreeView.class);
        TreeItem<Object> root = view.getRoot();

        assertThat(root).extracting(item -> item.getChildren().stream().map(TreeItem::getValue).toList())
                        .asList()
                        .contains("Important", "Unread", "Uncategorized");
    }

    @TestFx
    public void displaysChannels(FxRobot robot) throws Exception {
        URL filesDir = getClass().getResource("/sample/files");
        assert filesDir != null;
        List<File> files;
        try (Stream<Path> walked = Files.walk(Path.of(filesDir.toURI()))) {
            files = walked.map(Path::toFile).filter(File::isFile).toList();
            for (File file : files) {
                SyndFeed feed = new SyndFeedInput().build(file);
                Channel channel = (Channel) feed.createWireFeed();
                channels.add(channel);
            }
        }

        waitForFxEvents();

        TreeView<Object> view = robot.lookup("#channelsTree").queryAs(TreeView.class);
        TreeItem<Object> root = view.getRoot();

        assertThat(root).extracting(item -> item.getChildren()
                                                .stream()
                                                .filter(treeItem -> treeItem.getValue().equals("Uncategorized"))
                                                .map(treeItem -> treeItem.getChildren().stream())
                                                .flatMap(stream -> stream.map(TreeItem::getValue)
                                                                         .map(Channel.class::cast)
                                                                         .map(Channel::getTitle))
                                                .toList())
                        .asList()
                        .contains("All Java AND United States jobs | upwork.com",
                                  "All JavaFX jobs | upwork.com",
                                  "All Java - Payment verified jobs | upwork.com",
                                  "All Swing jobs | upwork.com");
    }
}