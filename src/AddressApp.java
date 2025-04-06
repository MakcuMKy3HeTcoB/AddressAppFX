import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.Modality;
import java.util.List;

public class AddressApp extends Application {
    private final ObservableList<Person> personData = FXCollections.observableArrayList();
    private final TableView<Person> tableView = new TableView<>();

    // Поля для отображения деталей
    private final Label firstNameValue = new Label();
    private final Label lastNameValue = new Label();
    private final Label streetValue = new Label();
    private final Label cityValue = new Label();
    private final Label postalCodeValue = new Label();
    private final Label birthdayValue = new Label();

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

        // Настройка TableView
        setupTableView();

        // Панель деталей справа
        VBox detailsPane = createDetailsPane();
        contentBox.getChildren().addAll(tableView, detailsPane);
        borderPane.setCenter(contentBox);

        // Настройка размеров
        tableView.setPrefWidth(300);
        detailsPane.setPrefWidth(300);

        // Обработчик выбора в таблице
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateDetails(newVal);
            }
        });

        Scene scene = new Scene(borderPane, 650, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setupTableView() {
        // Заменяем устаревшую CONSTRAINED_RESIZE_POLICY на UNCONSTRAINED с ручным управлением шириной
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        // Увеличиваем ширину колонок
        double columnWidth = 150;
        TableColumn<Person, String> firstNameCol = new TableColumn<>("First Name");
        firstNameCol.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        firstNameCol.setSortable(false);
        firstNameCol.setResizable(false);
        firstNameCol.setPrefWidth(columnWidth);

        TableColumn<Person, String> lastNameCol = new TableColumn<>("Last Name");
        lastNameCol.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
        lastNameCol.setSortable(false);
        lastNameCol.setResizable(false);
        lastNameCol.setPrefWidth(columnWidth);

        // Используем List.of() для избежания предупреждений о generics
        tableView.getColumns().addAll(List.of(firstNameCol, lastNameCol));
        tableView.setPlaceholder(new Label("No content in table"));
        tableView.setItems(personData);

        // Увеличиваем общую ширину таблицы
        tableView.setPrefWidth(columnWidth * 2 + 20);

        // Добавляем слушатель для автоматического изменения ширины колонок
        tableView.widthProperty().addListener((obs, oldVal, newVal) -> {
            double width = newVal.doubleValue() / tableView.getColumns().size();
            tableView.getColumns().forEach(col -> col.setPrefWidth(width));
        });
    }

    private VBox createDetailsPane() {
        VBox detailsPane = new VBox();
        detailsPane.setSpacing(10);
        detailsPane.setPadding(new Insets(5));
        detailsPane.setStyle("-fx-border-color: gray; -fx-border-width: 1;");

        Label detailsLabel = new Label("Person Details:");
        detailsLabel.setStyle("-fx-font-weight: bold;");

        // Стилизация меток с данными
        String valueStyle = "-fx-padding: 2 5; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;";
        firstNameValue.setStyle(valueStyle);
        lastNameValue.setStyle(valueStyle);
        streetValue.setStyle(valueStyle);
        cityValue.setStyle(valueStyle);
        postalCodeValue.setStyle(valueStyle);
        birthdayValue.setStyle(valueStyle);

        // Поля для деталей
        GridPane detailsGrid = new GridPane();
        detailsGrid.setHgap(10);
        detailsGrid.setVgap(5);

        String[] labels = {"First Name", "Last Name", "Street", "City", "Postal Code", "Birthday"};
        detailsGrid.add(new Label(labels[0] + ":"), 0, 0);
        detailsGrid.add(firstNameValue, 1, 0);
        detailsGrid.add(new Label(labels[1] + ":"), 0, 1);
        detailsGrid.add(lastNameValue, 1, 1);
        detailsGrid.add(new Label(labels[2] + ":"), 0, 2);
        detailsGrid.add(streetValue, 1, 2);
        detailsGrid.add(new Label(labels[3] + ":"), 0, 3);
        detailsGrid.add(cityValue, 1, 3);
        detailsGrid.add(new Label(labels[4] + ":"), 0, 4);
        detailsGrid.add(postalCodeValue, 1, 4);
        detailsGrid.add(new Label(labels[5] + ":"), 0, 5);
        detailsGrid.add(birthdayValue, 1, 5);

        // Кнопки
        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        Button newButton = new Button("New address");
        newButton.setOnAction(event -> showEditDialog(null));

        Button editButton = new Button("Edit address");
        editButton.setOnAction(event -> {
            Person selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showEditDialog(selected);
            } else {
                showAlert("Please select a person to edit");
            }
        });

        Button deleteButton = new Button("Delete address");
        deleteButton.setOnAction(event -> {
            Person selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                personData.remove(selected);
                clearDetails();
            } else {
                showAlert("Please select a person to delete");
            }
        });

        buttonBox.getChildren().addAll(newButton, editButton, deleteButton);
        detailsPane.getChildren().addAll(detailsLabel, detailsGrid, buttonBox);

        return detailsPane;
    }

    private void updateDetails(Person person) {
        firstNameValue.setText(person.getFirstName());
        lastNameValue.setText(person.getLastName());
        streetValue.setText(person.getStreet());
        cityValue.setText(person.getCity());
        postalCodeValue.setText(person.getPostalCode());
        birthdayValue.setText(person.getBirthday());
    }

    private void clearDetails() {
        firstNameValue.setText("");
        lastNameValue.setText("");
        streetValue.setText("");
        cityValue.setText("");
        postalCodeValue.setText("");
        birthdayValue.setText("");
    }
    private void showEditDialog(Person person) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(person == null ? "New Person" : "Edit Person");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Поля формы
        TextField firstNameField = new TextField();
        TextField lastNameField = new TextField();
        TextField streetField = new TextField();
        TextField cityField = new TextField();
        TextField postalCodeField = new TextField();
        TextField birthdayField = new TextField();

        // Метка для отображения ошибок
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        // Заполняем поля, если редактируем существующую запись
        if (person != null) {
            firstNameField.setText(person.getFirstName());
            lastNameField.setText(person.getLastName());
            streetField.setText(person.getStreet());
            cityField.setText(person.getCity());
            postalCodeField.setText(person.getPostalCode());
            birthdayField.setText(person.getBirthday());
        }

        // Добавляем поля на форму
        grid.add(new Label("First Name:"), 0, 0);
        grid.add(firstNameField, 1, 0);
        grid.add(new Label("Last Name:"), 0, 1);
        grid.add(lastNameField, 1, 1);
        grid.add(new Label("Street:"), 0, 2);
        grid.add(streetField, 1, 2);
        grid.add(new Label("City:"), 0, 3);
        grid.add(cityField, 1, 3);
        grid.add(new Label("Postal Code:"), 0, 4);
        grid.add(postalCodeField, 1, 4);
        grid.add(new Label("Birthday:"), 0, 5);
        grid.add(birthdayField, 1, 5);
        grid.add(errorLabel, 0, 6, 2, 1);

        // Кнопки OK и Cancel
        Button okButton = new Button("OK");
        okButton.setOnAction(event -> {
            // Проверяем, что все поля заполнены
            if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty() ||
                    streetField.getText().isEmpty() || cityField.getText().isEmpty() ||
                    postalCodeField.getText().isEmpty() || birthdayField.getText().isEmpty()) {

                errorLabel.setText("Please fill in all fields!");
                return;
            }

            // Проверяем на дубликаты только при создании новой записи
            if (person == null) {
                boolean duplicateExists = personData.stream().anyMatch(p ->
                        p.getFirstName().equalsIgnoreCase(firstNameField.getText()) &&
                                p.getLastName().equalsIgnoreCase(lastNameField.getText()) &&
                                p.getStreet().equalsIgnoreCase(streetField.getText()) &&
                                p.getCity().equalsIgnoreCase(cityField.getText()) &&
                                p.getPostalCode().equalsIgnoreCase(postalCodeField.getText()) &&
                                p.getBirthday().equalsIgnoreCase(birthdayField.getText()));

                if (duplicateExists) {
                    errorLabel.setText("This address already exists!");
                    return;
                }
            }

            if (person == null) {
                // Создаем новую запись
                Person newPerson = new Person(
                        firstNameField.getText(),
                        lastNameField.getText(),
                        streetField.getText(),
                        cityField.getText(),
                        postalCodeField.getText(),
                        birthdayField.getText()
                );
                personData.add(newPerson);
            } else {
                // Обновляем существующую запись
                person.setFirstName(firstNameField.getText());
                person.setLastName(lastNameField.getText());
                person.setStreet(streetField.getText());
                person.setCity(cityField.getText());
                person.setPostalCode(postalCodeField.getText());
                person.setBirthday(birthdayField.getText());

                // Обновляем TableView
                tableView.refresh();
            }
            dialog.close();
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(event -> dialog.close());

        HBox buttonBox = new HBox(10, okButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        grid.add(buttonBox, 1, 7);

        Scene dialogScene = new Scene(grid);
        dialog.setScene(dialogScene);
        dialog.showAndWait();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}