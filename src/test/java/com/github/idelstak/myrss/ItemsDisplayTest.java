package com.github.idelstak.myrss;

import com.github.idelstak.myrss.items.*;
import com.rometools.rome.feed.rss.*;
import com.rometools.rome.feed.synd.*;
import com.rometools.rome.io.*;
import javafx.beans.property.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.testfx.api.*;
import org.testfx.framework.junit5.*;

import java.io.*;
import java.net.*;

import static com.github.idelstak.myrss.components.Fxml.*;
import static javafx.application.Platform.*;
import static org.junit.jupiter.api.Assertions.fail;
import static org.testfx.assertions.api.Assertions.*;
import static org.testfx.util.WaitForAsyncUtils.*;

@ExtendWith(ApplicationExtension.class)
public class ItemsDisplayTest {

    private final ObjectProperty<Channel> channel;

    public ItemsDisplayTest() {channel = new SimpleObjectProperty<>();}

    @Start
    public void setup(Stage stage) {
        runLater(() -> {
            Parent root = null;
            try {
                root = ITEMS_TABLE.root();
                ItemsTableController controller = (ItemsTableController) ITEMS_TABLE.controller();
                controller.setChannel(channel);
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
    public void init() {
        runLater(() -> {
            URL resource = getClass().getResource("/sample/files/swing.rss");
            assert resource != null;

            try {
                File file = new File(resource.toURI());
                SyndFeed feed = new SyndFeedInput().build(file);
                Channel channel = (Channel) feed.createWireFeed();
                this.channel.set(null);
                this.channel.set(channel);
            } catch (URISyntaxException | FeedException | IOException e) {
                fail(e);
            }
        });
    }

    @Test
    public void displays(FxRobot robot) {
        TableView<Object> view = robot.lookup("#itemsTable").queryTableView();
        waitForFxEvents();
        assertThat(view).hasExactlyNumRows(7);
    }
}