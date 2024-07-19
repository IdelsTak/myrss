package com.github.idelstak.myrss.items;

import com.github.idelstak.myrss.components.*;
import com.rometools.rome.feed.rss.*;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.control.skin.*;
import javafx.scene.layout.*;

import java.text.*;
import java.util.*;

import static javafx.application.Platform.*;

public class ItemsTableController extends FxmlController {

    private final ObjectProperty<Channel> channel;
    private final ObservableList<Item> items;
    @FXML
    private TableView<Item> itemsTable;
    @FXML
    private TableColumn<Item, Void> countColumn;
    @FXML
    private TableColumn<Item, String> titleColumn;
    @FXML
    private TableColumn<Item, Date> dateColumn;

    public ItemsTableController() {
        channel = new SimpleObjectProperty<>();
        items = FXCollections.observableArrayList();
    }

    public void setChannel(ObjectProperty<Channel> channel) {
        this.channel.bind(channel);
    }

    @Override
    protected void initialize() {
        itemsTable.skinProperty().addListener(_ -> runLater(() -> {
            Pane showColumnsButton = (Pane) itemsTable.lookup(".show-hide-columns-button");
            ReadOnlyDoubleProperty tableWidth = itemsTable.widthProperty();
            double buttonWidth = showColumnsButton.getWidth();
            DoubleBinding subtracted = tableWidth.subtract(buttonWidth);
            countColumn.maxWidthProperty().bind(subtracted.multiply(0.08));
            dateColumn.maxWidthProperty().bind(subtracted.multiply(0.15));

            setAlignment(countColumn.getId(), Pos.CENTER_RIGHT);
            setAlignment(titleColumn.getId(), Pos.CENTER_LEFT);
            setAlignment(dateColumn.getId(), Pos.CENTER_RIGHT);
        }));

        countColumn.setCellFactory(_ -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                setAlignment(Pos.CENTER_RIGHT);

                if (empty) {
                    setText(null);
                } else {
                    int index = getTableRow().getIndex();
                    setText(Integer.toString(index + 1));
                }
            }
        });
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("pubDate"));
        dateColumn.setCellFactory(_ -> new TableCell<>() {
            @Override
            protected void updateItem(Date date, boolean empty) {
                super.updateItem(date, empty);

                setAlignment(Pos.CENTER_RIGHT);

                if (empty || date == null) {
                    setText(null);
                } else {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                    boolean isCurrentWeek = date.after(calendar.getTime()) &&
                                            date.before(nextWeekStart(calendar.getTime()));

                    DateFormat formatter;
                    if (isCurrentWeek) {
                        formatter = new SimpleDateFormat("EEE HH:mm");
                    } else {
                        formatter = new SimpleDateFormat("MMM dd HH:mm");
                    }

                    String formattedDate = formatter.format(date);
                    setText(formattedDate);
                }
            }

            private static Date nextWeekStart(Date date) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(Calendar.DAY_OF_YEAR, 7);
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                return calendar.getTime();
            }
        });

        channel.addListener((_, _, channel) -> {
            if (channel == null) {
                return;
            }

            runLater(() -> items.setAll(channel.getItems()));
        });

        itemsTable.setItems(items);
    }

    private void setAlignment(String id, Pos pos) {
        Set<Node> nodes = itemsTable.lookupAll(".table-column");
        Label titleLabel = nodes.stream()
                                .filter(node -> Objects.equals(node.getId(), id))
                                .map(TableColumnHeader.class::cast)
                                .map(header -> header.lookup(".label"))
                                .map(Label.class::cast)
                                .findFirst()
                                .orElseThrow();
        titleLabel.setAlignment(pos);
    }
}