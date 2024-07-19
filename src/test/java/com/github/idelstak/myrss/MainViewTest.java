package com.github.idelstak.myrss;

import com.github.idelstak.myrss.launch.*;
import com.rometools.rome.feed.rss.*;
import com.rometools.rome.feed.synd.*;
import com.rometools.rome.io.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.scene.Node;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.stage.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.testfx.api.*;
import org.testfx.framework.junit5.*;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

import static com.github.idelstak.myrss.components.Fxml.*;
import static javafx.application.Platform.*;
import static javafx.collections.FXCollections.*;
import static org.junit.jupiter.api.Assertions.fail;
import static org.testfx.assertions.api.Assertions.*;
import static org.testfx.framework.junit5.utils.FXUtils.*;
import static org.testfx.util.WaitForAsyncUtils.*;

@ExtendWith(ApplicationExtension.class)
public class MainViewTest {

    private final ObservableList<Channel> channels = observableArrayList();
    private final ObjectProperty<File> selectedRssFile = new SimpleObjectProperty<>();

    @Start
    public void setup(Stage stage) {
        runLater(() -> {
            Parent root = null;
            try {
                root = MAIN_VIEW.root();
                MainViewController controller = (MainViewController) MAIN_VIEW.controller();
                controller.setChannels(channels);
                controller.setSelectedRssFile(selectedRssFile);
            } catch (IOException e) {
                fail(e);
            }
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
            stage.toFront();
        });
        waitForFxEvents();
    }

    @BeforeEach
    public void initialize() {
        runFX(() -> {
            channels.clear();
            selectedRssFile.set(null);
        });
    }

    @Test
    public void initializesDisplay(FxRobot robot) throws Exception {
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

        Node node = robot.lookup("#channelsTree").query();
        assertThat(node).isNotNull();

        waitForFxEvents();

        node = robot.lookup("#itemsTable").query();
        assertThat(node).isNotNull();

        waitForFxEvents();

        node = robot.lookup("#contentView").query();
        assertThat(node).isNotNull();
    }

    @Test
    public void initialSelectionsAreMade(FxRobot robot) throws Exception {
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
        org.assertj.core.api.Assertions.assertThat(view)
                                       .extracting(TreeView::getSelectionModel)
                                       .extracting(SelectionModel::getSelectedItem)
                                       .satisfies(item -> {
                                           org.assertj.core.api.Assertions.assertThat(item).isNotNull();
                                           org.assertj.core.api.Assertions.assertThat(item)
                                                                          .extracting(TreeItem::getValue)
                                                                          .isInstanceOf(Channel.class);
                                       });

        sleep(500L, TimeUnit.MILLISECONDS);

        TableView<Object> tableView = robot.lookup("#itemsTable").queryTableView();
        org.assertj.core.api.Assertions.assertThat(tableView)
                                       .extracting(TableView::getSelectionModel)
                                       .extracting(SelectionModel::getSelectedItem)
                                       .isNotNull();
    }

    @Test
    public void addsSubscriptionFromFile(FxRobot robot) throws Exception {
        SplitMenuButton button = robot.lookup("#addSubscriptionButton").queryAs(SplitMenuButton.class);
        Node lookup = button.lookup(".arrow-button");
        robot.clickOn(lookup, MouseButton.PRIMARY).clickOn("#addSubscriptionMenuItem", MouseButton.PRIMARY);

        waitForFxEvents();

        robot.press(KeyCode.ESCAPE);

        waitForFxEvents();

        URL resource = getClass().getResource("/sample/files/swing.rss");
        assert resource != null;
        selectedRssFile.set(new File(resource.toURI()));

        waitForFxEvents();

        Node node = robot.lookup("All Swing jobs | upwork.com").query();
        waitForFxEvents();

        assertThat(node).isNotNull();
    }

    @Test
    public void addsSubscriptionFromLink(FxRobot robot) throws Exception {
        robot.clickOn("#addSubscriptionButton", MouseButton.PRIMARY);
        waitForFxEvents();

        ClipboardContent content = new ClipboardContent();
        content.putString("https://feeds.megaphone.fm/newheights");
        runFX(() -> Clipboard.getSystemClipboard().setContent(content));
        robot.rightClickOn("#urlField").clickOn("Paste", MouseButton.PRIMARY);
        waitForFxEvents();

        robot.clickOn("#fetchButton");
        waitForFxEvents();

        ProgressIndicator indicator = robot.lookup("#progressIndicator").queryAs(ProgressIndicator.class);
        waitForFxEvents();

        org.testfx.assertions.api.Assertions.assertThat(indicator).isVisible();

        CountDownLatch latch = new CountDownLatch(1);
        indicator.visibleProperty().addListener((_, _, visible) -> {
            if (visible == null) {
                return;
            }
            latch.countDown();
        });

        latch.await();

        robot.clickOn("OK");
        waitForFxEvents();

        Node node = robot.lookup("New Heights with Jason and Travis Kelce").query();
        waitForFxEvents();

        assertThat(node).isNotNull();
    }
}