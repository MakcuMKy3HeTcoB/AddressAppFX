import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.stage.FileChooser;
import java.io.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AddressApp extends Application {
    // Логгер для записи ошибок
    private static final Logger LOGGER = Logger.getLogger(AddressApp.class.getName());

    // Основные данные приложения - список контактов
    private final ObservableList<Person> personData = FXCollections.observableArrayList();

    // Таблица для отображения контактов
    private final TableView<Person> tableView = new TableView<>();

    // Текущий открытый файл
    private File currentFile = null;

    // Элементы для отображения деталей контакта
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

        // Основной контейнер с разметкой BorderPane
        BorderPane borderPane = new BorderPane();

        // Создаем и добавляем меню
        MenuBar menuBar = createMenuBar(primaryStage);
        borderPane.setTop(menuBar);

        // Контейнер для основного содержимого
        HBox contentBox = new HBox(10);
        contentBox.setPadding(new Insets(10));

        // Настраиваем таблицу и панель деталей
        setupTableView();
        VBox detailsPane = createDetailsPane();
        contentBox.getChildren().addAll(tableView, detailsPane);
        borderPane.setCenter(contentBox);

        // Настройка размеров
        tableView.setPrefWidth(300);
        detailsPane.setPrefWidth(300);

        // Обработчик выбора элемента в таблице
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateDetails(newVal);
            }
        });

        // Создаем сцену и настраиваем горячие клавиши
        Scene scene = new Scene(borderPane, 650, 400);
        setupKeyboardShortcuts(scene, primaryStage);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Настройка горячих клавиш приложения
     */
    private void setupKeyboardShortcuts(Scene scene, Stage primaryStage) {
        scene.addEventHandler(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
            if (new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN).match(event)) {
                newFile();
                event.consume();
            } else if (new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN).match(event)) {
                openFile(primaryStage);
                event.consume();
            } else if (new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN).match(event)) {
                saveFile(primaryStage);
                event.consume();
            } else if (new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN).match(event)) {
                saveFileAs(primaryStage);
                event.consume();
            } else if (new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN).match(event)) {
                exitApplication(primaryStage);
                event.consume();
            }
        });
    }

    /**
     * Создание меню приложения
     */
    private MenuBar createMenuBar(Stage primaryStage) {
        MenuBar menuBar = new MenuBar();

        // Меню "File"
        Menu fileMenu = new Menu("File");
        MenuItem newItem = new MenuItem("New");
        newItem.setOnAction(e -> newFile());

        MenuItem openItem = new MenuItem("Open...");
        openItem.setOnAction(e -> openFile(primaryStage));

        MenuItem saveItem = new MenuItem("Save");
        saveItem.setOnAction(e -> saveFile(primaryStage));

        MenuItem saveAsItem = new MenuItem("Save As...");
        saveAsItem.setOnAction(e -> saveFileAs(primaryStage));

        fileMenu.getItems().addAll(newItem, openItem, saveItem, saveAsItem, new SeparatorMenuItem(),
                new MenuItem("Exit") {{
                    setOnAction(e -> exitApplication(primaryStage));
                }});

        // Меню "Help"
        Menu helpMenu = new Menu("Help");
        helpMenu.getItems().add(new MenuItem("Keyboard Shortcuts") {{
            setOnAction(e -> showShortcutsDialog());
        }});

        menuBar.getMenus().addAll(fileMenu, helpMenu);
        return menuBar;
    }

    /**
     * Создание нового файла
     */
    private void newFile() {
        if (!personData.isEmpty()) {
            // Запрашиваем подтверждение, если есть несохраненные изменения
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Are you sure you want to create a new file?",
                    ButtonType.OK, ButtonType.CANCEL);
            alert.setTitle("New File");
            alert.setHeaderText("Unsaved changes will be lost");

            if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                personData.clear();
                currentFile = null;
                clearDetails();
            }
        } else {
            currentFile = null;
            clearDetails();
        }
    }

    /**
     * Сохранение файла
     */
    private void saveFile(Stage primaryStage) {
        if (currentFile != null) {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(currentFile))) {
                // Сериализуем список контактов
                oos.writeObject(new SerializablePersonList(personData));
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Could not save file", e);
                showAlert("Error", "Could not save file: " + e.getMessage());
            }
        } else {
            saveFileAs(primaryStage);
        }
    }

    /**
     * Открытие файла
     */
    private void openFile(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Address Book");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Address Book Files", "*.addr"));

        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                // Десериализуем список контактов
                SerializablePersonList data = (SerializablePersonList) ois.readObject();
                personData.setAll(data.getPersons());
                currentFile = file;
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Could not load file", e);
                showAlert("Error", "Could not load file: " + e.getMessage());
            }
        }
    }

    /**
     * Сохранение файла с указанием имени
     */
    private void saveFileAs(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Address Book");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Address Book Files", "*.addr"));

        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            // Добавляем расширение, если его нет
            if (!file.getName().endsWith(".addr")) {
                file = new File(file.getAbsolutePath() + ".addr");
            }
            currentFile = file;
            saveFile(primaryStage);
        }
    }

    /**
     * Выход из приложения
     */
    private void exitApplication(Stage primaryStage) {
        if (!personData.isEmpty()) {
            // Запрашиваем подтверждение, если есть несохраненные изменения
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Are you sure you want to exit?",
                    ButtonType.OK, ButtonType.CANCEL);
            alert.setTitle("Exit");
            alert.setHeaderText("Unsaved changes will be lost");

            if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                primaryStage.close();
            }
        } else {
            primaryStage.close();
        }
    }

    /**
     * Показ диалога с горячими клавишами
     */
    private void showShortcutsDialog() {
        String shortcuts = """
            Ctrl+N - New file
            Ctrl+O - Open file
            Ctrl+S - Save file
            Ctrl+Shift+S - Save As
            Ctrl+Q - Exit""";

        Alert alert = new Alert(Alert.AlertType.INFORMATION, shortcuts);
        alert.setTitle("Keyboard Shortcuts");
        alert.setHeaderText("Available Shortcuts");
        alert.showAndWait();
    }

    /**
     * Настройка таблицы контактов
     */
    private void setupTableView() {
        // Политика изменения размеров колонок
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        // Колонка "Имя"
        TableColumn<Person, String> firstNameCol = new TableColumn<>("First Name");
        firstNameCol.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        firstNameCol.setSortable(false);
        firstNameCol.setPrefWidth(150);

        // Колонка "Фамилия"
        TableColumn<Person, String> lastNameCol = new TableColumn<>("Last Name");
        lastNameCol.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
        lastNameCol.setSortable(false);
        lastNameCol.setPrefWidth(150);

        // Добавляем колонки в таблицу
        tableView.getColumns().add(firstNameCol);
        tableView.getColumns().add(lastNameCol);
        tableView.setPlaceholder(new Label("No content in table"));
        tableView.setItems(personData);

        // Обработчик изменения размера таблицы
        tableView.widthProperty().addListener((obs, oldVal, newVal) -> {
            double width = newVal.doubleValue() / tableView.getColumns().size();
            tableView.getColumns().forEach(col -> col.setPrefWidth(width));
        });
    }

    /**
     * Создание панели с деталями контакта
     */
    private VBox createDetailsPane() {
        GridPane detailsGrid = new GridPane();
        detailsGrid.setHgap(10);
        detailsGrid.setVgap(5);

        // Добавляем метки полей
        String[] labels = {"First Name", "Last Name", "Street", "City", "Postal Code", "Birthday"};
        for (int i = 0; i < labels.length; i++) {
            detailsGrid.add(new Label(labels[i] + ":"), 0, i);
        }

        // Настраиваем стиль для значений
        Label[] valueLabels = {firstNameValue, lastNameValue, streetValue, cityValue, postalCodeValue, birthdayValue};
        String valueStyle = "-fx-padding: 2 5; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;";
        for (int i = 0; i < valueLabels.length; i++) {
            valueLabels[i].setStyle(valueStyle);
            detailsGrid.add(valueLabels[i], 1, i);
        }

        // Кнопка "Добавить"
        Button newButton = new Button("New address");
        newButton.setOnAction(event -> showEditDialog(null));

        // Кнопка "Редактировать"
        Button editButton = new Button("Edit address");
        editButton.setOnAction(event -> {
            Person selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showEditDialog(selected);
            } else {
                showAlert("Warning", "Please select a person to edit");
            }
        });

        // Кнопка "Удалить"
        Button deleteButton = new Button("Delete address");
        deleteButton.setOnAction(event -> {
            Person selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                personData.remove(selected);
                clearDetails();
            } else {
                showAlert("Warning", "Please select a person to delete");
            }
        });

        // Контейнер для кнопок
        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(newButton, editButton, deleteButton);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        // Собираем панель деталей
        VBox detailsPane = new VBox(10,
                new Label("Person Details:") {{
                    setStyle("-fx-font-weight: bold;");
                }},
                detailsGrid,
                buttonBox
        );
        detailsPane.setPadding(new Insets(5));
        detailsPane.setStyle("-fx-border-color: gray; -fx-border-width: 1;");

        return detailsPane;
    }

    /**
     * Обновление деталей выбранного контакта
     */
    private void updateDetails(Person person) {
        firstNameValue.setText(person.getFirstName());
        lastNameValue.setText(person.getLastName());
        streetValue.setText(person.getStreet());
        cityValue.setText(person.getCity());
        postalCodeValue.setText(person.getPostalCode());
        birthdayValue.setText(person.getBirthday());
    }

    /**
     * Очистка панели деталей
     */
    private void clearDetails() {
        firstNameValue.setText("");
        lastNameValue.setText("");
        streetValue.setText("");
        cityValue.setText("");
        postalCodeValue.setText("");
        birthdayValue.setText("");
    }

    /**
     * Показ диалога редактирования/добавления контакта
     */
    private void showEditDialog(Person person) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(person == null ? "New Person" : "Edit Person");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Поля для ввода
        TextField firstNameField = new TextField();
        TextField lastNameField = new TextField();
        TextField streetField = new TextField();
        TextField cityField = new TextField();
        TextField postalCodeField = new TextField();
        TextField birthdayField = new TextField();

        // Заполняем поля, если редактируем существующий контакт
        if (person != null) {
            firstNameField.setText(person.getFirstName());
            lastNameField.setText(person.getLastName());
            streetField.setText(person.getStreet());
            cityField.setText(person.getCity());
            postalCodeField.setText(person.getPostalCode());
            birthdayField.setText(person.getBirthday());
        }

        // Добавляем поля на форму
        String[] fieldLabels = {"First Name", "Last Name", "Street", "City", "Postal Code", "Birthday"};
        TextField[] fields = {firstNameField, lastNameField, streetField, cityField, postalCodeField, birthdayField};

        for (int i = 0; i < fieldLabels.length; i++) {
            grid.add(new Label(fieldLabels[i] + ":"), 0, i);
            grid.add(fields[i], 1, i);
        }

        // Метка для отображения ошибок
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");
        grid.add(errorLabel, 0, fieldLabels.length, 2, 1);

        // Кнопка OK
        Button okButton = new Button("OK");
        okButton.setOnAction(event -> {
            // Валидация полей
            for (TextField field : fields) {
                if (field.getText().isEmpty()) {
                    errorLabel.setText("Please fill in all fields!");
                    return;
                }
            }

            if (person == null) {
                // Создаем новый контакт
                Person newPerson = new Person(
                        firstNameField.getText(),
                        lastNameField.getText(),
                        streetField.getText(),
                        cityField.getText(),
                        postalCodeField.getText(),
                        birthdayField.getText()
                );

                // Проверка на дубликаты
                if (personData.contains(newPerson)) {
                    errorLabel.setText("This address already exists!");
                    return;
                }

                personData.add(newPerson);
                // Выделяем новый контакт в таблице
                tableView.getSelectionModel().select(newPerson);
            } else {
                // Обновляем существующий контакт
                person.setFirstName(firstNameField.getText());
                person.setLastName(lastNameField.getText());
                person.setStreet(streetField.getText());
                person.setCity(cityField.getText());
                person.setPostalCode(postalCodeField.getText());
                person.setBirthday(birthdayField.getText());

                // Обновляем отображение
                tableView.refresh();
                updateDetails(person);
            }

            dialog.close();
        });

        // Кнопка Cancel
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(event -> dialog.close());

        // Контейнер для кнопок
        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(okButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        grid.add(buttonBox, 1, fieldLabels.length + 1);

        dialog.setScene(new Scene(grid));
        dialog.showAndWait();
    }

    /**
     * Показ предупреждающего сообщения
     */
    private void showAlert(String title, String message) {
        new Alert(Alert.AlertType.WARNING, message) {{
            setTitle(title);
            setHeaderText(null);
        }}.showAndWait();
    }

    /**
     * Вспомогательный класс для сериализации списка контактов
     */
    private static class SerializablePersonList implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
        private final List<Person> persons;

        public SerializablePersonList(ObservableList<Person> persons) {
            this.persons = List.copyOf(persons);
        }

        public ObservableList<Person> getPersons() {
            return FXCollections.observableArrayList(persons);
        }
    }
}