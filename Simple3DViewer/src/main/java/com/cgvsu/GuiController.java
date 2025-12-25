package com.cgvsu;

import com.cgvsu.model.Model;
import com.cgvsu.objreader.ObjReader;
import com.cgvsu.objreader.ObjReaderException;
import com.cgvsu.objwriter.ObjWriter;
import com.cgvsu.objwriter.ObjWriterException;
import com.cgvsu.render_engine.Camera;
import com.cgvsu.render_engine.RenderEngine;
import com.cgvsu.scene.Scene;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.vecmath.Vector3f;
import java.io.File;
import java.util.Objects;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Контроллер главного окна приложения Simple3DViewer.
 * Управляет интерфейсом, сценой с моделями, камерой и рендерингом.
 */
public class GuiController {

    // ==================== Константы ====================

    private static final float TRANSLATION = 0.5F;
    private static final String DARK_THEME_PATH = "/com/cgvsu/css/dark-theme.css";
    private static final String LIGHT_THEME_PATH = "/com/cgvsu/css/light-theme.css";

    // ==================== FXML компоненты ====================

    @FXML
    private BorderPane rootPane;

    @FXML
    private MenuBar menuBar;

    @FXML
    private AnchorPane canvasPane;

    @FXML
    private Canvas canvas;

    @FXML
    private VBox sidePanel;

    @FXML
    private ListView<String> modelListView;

    @FXML
    private TextField vertexIndicesField;

    @FXML
    private TextField polygonIndicesField;

    @FXML
    private Label statusLabel;

    @FXML
    private Label modelCountLabel;

    @FXML
    private Label activeModelLabel;

    @FXML
    private Label fpsLabel;

    @FXML
    private Label vertexCountLabel;

    @FXML
    private Label polygonCountLabel;

    @FXML
    private Label textureCountLabel;

    @FXML
    private Label normalCountLabel;

    @FXML
    private RadioMenuItem darkThemeMenuItem;

    @FXML
    private RadioMenuItem lightThemeMenuItem;

    @FXML
    private MenuItem saveMenuItem;

    @FXML
    private Button removeModelButton;

    @FXML
    private Button deleteVerticesButton;

    @FXML
    private Button deletePolygonsButton;

    // ==================== Внутренние поля ====================

    // Сцена с моделями
    private Scene scene;

    // Камера для рендеринга
    private Camera camera;

    // Таймлайн для анимации/рендеринга
    private Timeline timeline;

    // Список моделей для отображения в ListView
    private ObservableList<String> modelListItems;

    // Текущая тема
    private boolean isDarkTheme = true;

    // Для подсчёта FPS
    private long lastFrameTime = System.nanoTime();
    private int frameCount = 0;
    private double currentFps = 0;

    // ==================== Инициализация ====================

    @FXML
    private void initialize() {
        // Инициализируем сцену
        scene = new Scene();

        // Инициализируем камеру
        camera = new Camera(
                new Vector3f(0, 0, 100),
                new Vector3f(0, 0, 0),
                1.0F, 1, 0.01F, 100);

        // Инициализируем список моделей
        modelListItems = FXCollections.observableArrayList();
        modelListView.setItems(modelListItems);

        // Настраиваем множественный выбор в списке
        modelListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Слушатель выбора в списке моделей
        modelListView.getSelectionModel().selectedIndexProperty().addListener(
                (observable, oldValue, newValue) -> onModelSelectionChanged());

        // Привязка размеров канваса к размерам родительской панели
        canvasPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            canvas.setWidth(newVal.doubleValue());
        });
        canvasPane.heightProperty().addListener((obs, oldVal, newVal) -> {
            canvas.setHeight(newVal.doubleValue());
        });

        // Применяем тёмную тему по умолчанию
        applyTheme(DARK_THEME_PATH);

        // Запускаем цикл рендеринга
        startRenderLoop();

        // Обновляем UI
        updateUI();

        setStatus("Приложение готово к работе");
    }

    /**
     * Запуск цикла рендеринга.
     */
    private void startRenderLoop() {
        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        KeyFrame frame = new KeyFrame(Duration.millis(16), event -> {
            renderFrame();
            updateFps();
        });

        timeline.getKeyFrames().add(frame);
        timeline.play();
    }

    /**
     * Отрисовка одного кадра.
     */
    private void renderFrame() {
        double width = canvas.getWidth();
        double height = canvas.getHeight();

        if (width <= 0 || height <= 0) return;

        // Очищаем канвас
        canvas.getGraphicsContext2D().clearRect(0, 0, width, height);

        // Устанавливаем соотношение сторон камеры
        camera.setAspectRatio((float) (width / height));

        // Рендерим все модели на сцене
        for (Model model : scene.getAllModels()) {
            if (model != null && !model.isEmpty()) {
                RenderEngine.render(
                        canvas.getGraphicsContext2D(),
                        camera,
                        model,
                        (int) width,
                        (int) height);
            }
        }

        frameCount++;
    }

    /**
     * Обновление счётчика FPS.
     */
    private void updateFps() {
        long currentTime = System.nanoTime();
        double elapsed = (currentTime - lastFrameTime) / 1_000_000_000.0;

        if (elapsed >= 1.0) {
            currentFps = frameCount / elapsed;
            frameCount = 0;
            lastFrameTime = currentTime;

            Platform.runLater(() -> fpsLabel.setText(String.format("FPS: %.0f", currentFps)));
        }
    }

    // ==================== Обработчики меню Файл ====================

    /**
     * Открытие OBJ файла.
     */
    @FXML
    private void onOpenModelMenuItemClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("OBJ файлы (*.obj)", "*.obj"));
        fileChooser.setTitle("Открыть 3D модель");

        File file = fileChooser.showOpenDialog(getStage());
        if (file == null) {
            return;
        }

        loadModelFromFile(file);
    }

    /**
     * Загрузка модели из файла с обработкой ошибок.
     */
    private void loadModelFromFile(File file) {
        try {
            Path filePath = file.toPath();
            String fileContent = Files.readString(filePath);

            // Парсим OBJ файл
            Model model = ObjReader.read(fileContent);

            // Добавляем модель на сцену
            String modelName = file.getName();
            int index = scene.addModel(model, modelName);

            // Обновляем список
            modelListItems.add(modelName);

            // Выбираем добавленную модель
            modelListView.getSelectionModel().select(index);
            scene.setActiveModel(index);

            updateUI();
            setStatus("Модель загружена: " + modelName);

        } catch (ObjReaderException e) {
            showErrorAlert("Ошибка чтения OBJ файла",
                    "Не удалось прочитать файл модели.\n\n" + e.getMessage());
        } catch (IOException e) {
            showErrorAlert("Ошибка чтения файла",
                    "Не удалось открыть файл.\n\n" + e.getMessage());
        } catch (Exception e) {
            showErrorAlert("Неизвестная ошибка",
                    "Произошла непредвиденная ошибка при загрузке модели.\n\n" + e.getMessage());
        }
    }

    /**
     * Сохранение активной модели в OBJ файл.
     */
    @FXML
    private void onSaveModelMenuItemClick() {
        if (!scene.hasActiveModels()) {
            showWarningAlert("Нет активной модели",
                    "Выберите модель для сохранения из списка.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("OBJ файлы (*.obj)", "*.obj"));
        fileChooser.setTitle("Сохранить модель");

        // Предлагаем имя файла на основе имени модели
        int activeIndex = scene.getFirstActiveModelIndex();
        String suggestedName = scene.getModelName(activeIndex);
        if (!suggestedName.endsWith(".obj")) {
            suggestedName += ".obj";
        }
        fileChooser.setInitialFileName(suggestedName);

        File file = fileChooser.showSaveDialog(getStage());
        if (file == null) {
            return;
        }

        saveModelToFile(scene.getFirstActiveModel(), file);
    }

    /**
     * Сохранение модели в файл с обработкой ошибок.
     */
    private void saveModelToFile(Model model, File file) {
        try {
            ObjWriter.write(model, file);
            setStatus("Модель сохранена: " + file.getName());
            showInfoAlert("Сохранено", "Модель успешно сохранена в файл:\n" + file.getAbsolutePath());
        } catch (ObjWriterException e) {
            showErrorAlert("Ошибка сохранения",
                    "Не удалось сохранить модель.\n\n" + e.getMessage());
        } catch (Exception e) {
            showErrorAlert("Неизвестная ошибка",
                    "Произошла непредвиденная ошибка при сохранении.\n\n" + e.getMessage());
        }
    }

    /**
     * Выход из приложения.
     */
    @FXML
    private void onExitMenuItemClick() {
        Platform.exit();
    }

    // ==================== Обработчики меню Редактирование ====================

    /**
     * Удаление выбранных вершин активной модели.
     */
    @FXML
    private void onDeleteVerticesClick() {
        if (!scene.hasActiveModels()) {
            showWarningAlert("Нет активной модели",
                    "Выберите модель для редактирования.");
            return;
        }

        String input = vertexIndicesField.getText().trim();
        if (input.isEmpty()) {
            showWarningAlert("Нет индексов",
                    "Введите индексы вершин для удаления.\n" +
                    "Формат: 0, 1, 2 или 5-10");
            return;
        }

        try {
            List<Integer> indices = parseIndices(input);

            if (indices.isEmpty()) {
                showWarningAlert("Неверный формат",
                        "Не удалось распознать индексы.");
                return;
            }

            // Удаляем вершины из всех активных моделей
            int totalRemoved = 0;
            for (Model model : scene.getActiveModels()) {
                totalRemoved += model.removeVerticesByIndices(indices);
            }

            vertexIndicesField.clear();
            updateModelInfo();
            setStatus("Удалено вершин: " + totalRemoved);

        } catch (Exception e) {
            showErrorAlert("Ошибка",
                    "Не удалось удалить вершины.\n\n" + e.getMessage());
        }
    }

    /**
     * Удаление выбранных полигонов активной модели.
     */
    @FXML
    private void onDeletePolygonsClick() {
        if (!scene.hasActiveModels()) {
            showWarningAlert("Нет активной модели",
                    "Выберите модель для редактирования.");
            return;
        }

        String input = polygonIndicesField.getText().trim();
        if (input.isEmpty()) {
            showWarningAlert("Нет индексов",
                    "Введите индексы полигонов для удаления.\n" +
                    "Формат: 0, 1, 2 или 5-10");
            return;
        }

        try {
            List<Integer> indices = parseIndices(input);

            if (indices.isEmpty()) {
                showWarningAlert("Неверный формат",
                        "Не удалось распознать индексы.");
                return;
            }

            // Удаляем полигоны из всех активных моделей
            int totalRemoved = 0;
            for (Model model : scene.getActiveModels()) {
                totalRemoved += model.removePolygonsByIndices(indices);
            }

            polygonIndicesField.clear();
            updateModelInfo();
            setStatus("Удалено полигонов: " + totalRemoved);

        } catch (Exception e) {
            showErrorAlert("Ошибка",
                    "Не удалось удалить полигоны.\n\n" + e.getMessage());
        }
    }

    /**
     * Парсинг строки индексов.
     * Поддерживает форматы: "1, 2, 3" и "5-10"
     */
    private List<Integer> parseIndices(String input) {
        List<Integer> indices = new ArrayList<>();
        String[] parts = input.split(",");

        for (String part : parts) {
            part = part.trim();
            if (part.isEmpty()) continue;

            if (part.contains("-")) {
                // Диапазон
                String[] range = part.split("-");
                if (range.length == 2) {
                    try {
                        int start = Integer.parseInt(range[0].trim());
                        int end = Integer.parseInt(range[1].trim());
                        for (int i = start; i <= end; i++) {
                            indices.add(i);
                        }
                    } catch (NumberFormatException ignored) {}
                }
            } else {
                // Одиночный индекс
                try {
                    indices.add(Integer.parseInt(part));
                } catch (NumberFormatException ignored) {}
            }
        }

        return indices;
    }

    /**
     * Выбрать все модели.
     */
    @FXML
    private void onSelectAllModelsClick() {
        modelListView.getSelectionModel().selectAll();
        scene.selectAll();
        updateUI();
    }

    /**
     * Снять выделение со всех моделей.
     */
    @FXML
    private void onDeselectAllModelsClick() {
        modelListView.getSelectionModel().clearSelection();
        scene.clearSelection();
        updateUI();
    }

    // ==================== Обработчики панели моделей ====================

    /**
     * Обработчик изменения выбора в списке моделей.
     */
    private void onModelSelectionChanged() {
        // Синхронизируем выбор со сценой
        scene.clearSelection();
        for (Integer index : modelListView.getSelectionModel().getSelectedIndices()) {
            scene.addToSelection(index);
        }

        updateModelInfo();
        updateUI();
    }

    /**
     * Удаление выбранной модели со сцены.
     */
    @FXML
    private void onRemoveModelClick() {
        int selectedIndex = modelListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            showWarningAlert("Нет выбранной модели",
                    "Выберите модель для удаления из списка.");
            return;
        }

        // Подтверждение удаления
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Подтверждение");
        confirm.setHeaderText("Удалить модель?");
        confirm.setContentText("Вы уверены, что хотите удалить модель \"" +
                scene.getModelName(selectedIndex) + "\"?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String modelName = scene.getModelName(selectedIndex);
            scene.removeModel(selectedIndex);
            modelListItems.remove(selectedIndex);
            updateUI();
            setStatus("Модель удалена: " + modelName);
        }
    }

    /**
     * Переименование выбранной модели.
     */
    @FXML
    private void onRenameModelClick() {
        int selectedIndex = modelListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            showWarningAlert("Нет выбранной модели",
                    "Выберите модель для переименования из списка.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(scene.getModelName(selectedIndex));
        dialog.setTitle("Переименование модели");
        dialog.setHeaderText("Введите новое имя модели");
        dialog.setContentText("Имя:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            String newName = result.get().trim();
            scene.setModelName(selectedIndex, newName);
            modelListItems.set(selectedIndex, newName);
            setStatus("Модель переименована в: " + newName);
        }
    }

    // ==================== Обработчики камеры ====================

    @FXML
    public void handleCameraForward(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, 0, -TRANSLATION));
    }

    @FXML
    public void handleCameraBackward(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, 0, TRANSLATION));
    }

    @FXML
    public void handleCameraLeft(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(TRANSLATION, 0, 0));
    }

    @FXML
    public void handleCameraRight(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(-TRANSLATION, 0, 0));
    }

    @FXML
    public void handleCameraUp(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, TRANSLATION, 0));
    }

    @FXML
    public void handleCameraDown(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, -TRANSLATION, 0));
    }

    /**
     * Сброс камеры в начальную позицию.
     */
    @FXML
    public void handleCameraReset() {
        camera.setPosition(new Vector3f(0, 0, 100));
        camera.setTarget(new Vector3f(0, 0, 0));
        setStatus("Камера сброшена");
    }

    // ==================== Обработчики меню Вид ====================

    /**
     * Применение тёмной темы.
     */
    @FXML
    private void onDarkThemeSelected() {
        applyTheme(DARK_THEME_PATH);
        isDarkTheme = true;
        setStatus("Применена тёмная тема");
    }

    /**
     * Применение светлой темы.
     */
    @FXML
    private void onLightThemeSelected() {
        applyTheme(LIGHT_THEME_PATH);
        isDarkTheme = false;
        setStatus("Применена светлая тема");
    }

    /**
     * Применение CSS темы к корневому элементу.
     */
    private void applyTheme(String themePath) {
        try {
            javafx.scene.Scene fxScene = rootPane.getScene();
            if (fxScene != null) {
                fxScene.getStylesheets().clear();
                String css = Objects.requireNonNull(
                        getClass().getResource(themePath)).toExternalForm();
                fxScene.getStylesheets().add(css);
            }
        } catch (Exception e) {
            System.err.println("Не удалось применить тему: " + e.getMessage());
        }
    }

    // ==================== Обработчики меню Справка ====================

    /**
     * Отображение информации о программе.
     */
    @FXML
    private void onAboutMenuItemClick() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("О программе");
        alert.setHeaderText("Simple3DViewer");
        alert.setContentText(
                "Версия: 1.0\n\n" +
                "3D-визуализатор для просмотра OBJ моделей.\n\n" +
                "Возможности:\n" +
                "• Загрузка и сохранение OBJ файлов\n" +
                "• Управление несколькими моделями\n" +
                "• Редактирование вершин и полигонов\n" +
                "• Управление камерой\n" +
                "• Темная и светлая темы\n\n" +
                "Разработано в рамках курса компьютерной графики.");
        alert.showAndWait();
    }

    // ==================== Вспомогательные методы UI ====================

    /**
     * Обновление всего UI.
     */
    private void updateUI() {
        // Обновляем счётчики в статус баре
        modelCountLabel.setText("Моделей: " + scene.getModelCount());
        activeModelLabel.setText("Активных: " + scene.getActiveModelCount());

        // Обновляем информацию о модели
        updateModelInfo();

        // Активируем/деактивируем кнопки
        boolean hasSelection = scene.hasActiveModels();
        saveMenuItem.setDisable(!hasSelection);
        removeModelButton.setDisable(!hasSelection);
        deleteVerticesButton.setDisable(!hasSelection);
        deletePolygonsButton.setDisable(!hasSelection);
    }

    /**
     * Обновление информации о выбранной модели.
     */
    private void updateModelInfo() {
        Model model = scene.getFirstActiveModel();

        if (model != null) {
            vertexCountLabel.setText("Вершины: " + model.vertices.size());
            polygonCountLabel.setText("Полигоны: " + model.polygons.size());
            textureCountLabel.setText("Текст. коорд.: " + model.textureVertices.size());
            normalCountLabel.setText("Нормали: " + model.normals.size());
        } else {
            vertexCountLabel.setText("Вершины: -");
            polygonCountLabel.setText("Полигоны: -");
            textureCountLabel.setText("Текст. коорд.: -");
            normalCountLabel.setText("Нормали: -");
        }
    }

    /**
     * Установка сообщения в статус бар.
     */
    private void setStatus(String message) {
        statusLabel.setText(message);
    }

    /**
     * Получение текущего Stage.
     */
    private Stage getStage() {
        return (Stage) canvas.getScene().getWindow();
    }

    // ==================== Методы отображения Alert диалогов ====================

    /**
     * Показать диалог ошибки.
     */
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Показать диалог предупреждения.
     */
    private void showWarningAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Предупреждение");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Показать информационный диалог.
     */
    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ==================== Публичные методы для интеграции с коллегами ====================

    /**
     * Получить текущую сцену.
     * Для использования коллегами (рендеринг, камера).
     */
    public Scene getScene() {
        return scene;
    }

    /**
     * Получить текущую камеру.
     * Для использования коллегами.
     */
    public Camera getCamera() {
        return camera;
    }

    /**
     * Получить канвас для рендеринга.
     * Для использования коллегами.
     */
    public Canvas getCanvas() {
        return canvas;
    }

    /**
     * Принудительно перерисовать сцену.
     * Для вызова после внешних изменений моделей.
     */
    public void refresh() {
        updateUI();
    }
}
