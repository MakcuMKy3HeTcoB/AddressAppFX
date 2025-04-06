import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.List;

public class AddressApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Address App");

        // Основной контейнер
        BorderPane borderPane = new BorderPane();

        // Меню
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        Menu editMenu = new Menu("Edit");
        Menu helpMenu = new Menu("Help");
        menuBar.getMenus().addAll(fileMenu, editMenu, helpMenu);
        borderPane.setTop(menuBar);

        // Основной контент - таблица и детали
        HBox contentBox = new HBox();
        contentBox.setPadding(new Insets(10));
        contentBox.setSpacing(10);

        // Таблица слева
        TableView<Object> tableView = new TableView<>(); // Здесь указываем тип данных для таблицы

        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY); // Разрешаем изменение размера столбцов

        // Слушатель изменения размера таблицы
        // Параметры obs и oldVal не используются, но требуются сигнатурой ChangeListener
        tableView.widthProperty().addListener((obs, oldVal, newVal) -> {
            // Равномерно распределяем ширину между столбцами
            double width = newVal.doubleValue() / tableView.getColumns().size();
            tableView.getColumns().forEach(col -> col.setPrefWidth(width));
        });

        TableColumn<Object, String> firstNameCol = new TableColumn<>("First Name");
        firstNameCol.setSortable(false);
        firstNameCol.setResizable(false);

        TableColumn<Object, String> lastNameCol = new TableColumn<>("Last Name");
        lastNameCol.setSortable(false);
        lastNameCol.setResizable(false);

        // Начальная ширина столбцов
        double initialColWidth = 150;
        firstNameCol.setPrefWidth(initialColWidth);
        lastNameCol.setPrefWidth(initialColWidth);

        tableView.getColumns().addAll(List.of(firstNameCol, lastNameCol));
        tableView.setPlaceholder(new Label("Kein Content in Tabelle"));

        // Панель деталей справа
        VBox detailsPane = new VBox();
        detailsPane.setSpacing(10);
        detailsPane.setPadding(new Insets(5));
        detailsPane.setStyle("-fx-border-color: gray; -fx-border-width: 1;");

        Label detailsLabel = new Label("Person Details:");
        detailsLabel.setStyle("-fx-font-weight: bold;");

        // Поля для деталей
        GridPane detailsGrid = new GridPane();
        detailsGrid.setHgap(10);
        detailsGrid.setVgap(5);

        String[] labels = {"First Name", "Last Name", "Street", "City", "Postal Code", "Birthday"};
        for (int i = 0; i < labels.length; i++) {
            detailsGrid.add(new Label(labels[i] + ":"), 0, i);
            detailsGrid.add(new Label(), 1, i);
        }

        // Кнопки
        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        Button newButton = new Button("New address");
        Button editButton = new Button("Edit address");
        Button deleteButton = new Button("Delete address");
        buttonBox.getChildren().addAll(newButton, editButton, deleteButton);

        detailsPane.getChildren().addAll(detailsLabel, detailsGrid, buttonBox);

        contentBox.getChildren().addAll(tableView, detailsPane);
        borderPane.setCenter(contentBox);

        // Настройка размеров
        tableView.setPrefWidth(300);
        detailsPane.setPrefWidth(300);

        Scene scene = new Scene(borderPane, 650, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}