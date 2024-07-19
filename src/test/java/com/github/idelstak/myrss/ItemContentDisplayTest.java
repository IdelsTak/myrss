package com.github.idelstak.myrss;

import com.github.idelstak.myrss.items.*;
import com.rometools.rome.feed.rss.*;
import com.rometools.rome.feed.synd.*;
import com.rometools.rome.io.*;
import javafx.beans.property.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.web.*;
import javafx.stage.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.testfx.api.*;
import org.testfx.framework.junit5.*;
import org.testfx.framework.junit5.utils.*;
import org.w3c.dom.*;

import java.io.*;
import java.net.*;

import static com.github.idelstak.myrss.components.Fxml.*;
import static javafx.application.Platform.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.testfx.assertions.api.Assertions.assertThat;
import static org.testfx.util.WaitForAsyncUtils.*;

@ExtendWith(ApplicationExtension.class)
public class ItemContentDisplayTest {

    private final ObjectProperty<Item> item = new SimpleObjectProperty<>();

    @Start
    public void setup(Stage stage) {
        runLater(() -> {
            Parent root = null;
            try {
                root = ITEMS_CONTENT.root();
                ItemContentController controller = (ItemContentController) ITEMS_CONTENT.controller();
                controller.setItem(item);
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
                item.set(null);
                item.set(channel.getItems().getFirst());
            } catch (URISyntaxException | FeedException | IOException e) {
                fail(e);
            }
        });
        waitForFxEvents();
    }

    @Test
    public void displaysTitle(FxRobot robot) {
        Labeled labeled = robot.lookup("#titleLabel").queryLabeled();
        waitForFxEvents();
        assertThat(labeled).hasText(item.get().getTitle());
    }

    @Test
    public void displaysContent(FxRobot robot) throws Exception {
        WebView view = robot.lookup("#contentView").queryAs(WebView.class);
        waitForFxEvents();
        FXUtils.runFX(() -> {
            Document document = view.getEngine().getDocument();
            assertThat(document).isNotNull();
        });
    }
}