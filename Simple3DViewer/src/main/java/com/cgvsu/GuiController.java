package com.cgvsu;

import com.cgvsu.model.Model;
import com.cgvsu.objreader.ObjReader;
import com.cgvsu.objreader.ObjReaderException;
import com.cgvsu.objwriter.ObjWriter;
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
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.vecmath.Vector3f;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

/**
 * Контроллер главного окна приложения Simple3DViewer.
 * Оптимизирован для работы на macOS с JavaFX.
 */
public class GuiController {

    private static final float TRANSLATION = 0.5F;
    private static final String DARK_THEME_PATH = "/com/cgvsu/css/dark-theme.css";
    private static final String LIGHT_THEME_PATH = "/com/cgvsu/css/light-theme.css";

    // Минимальные и максимальные размеры Canvas для защиты от macOS проблем
    private static final double MIN_CANVAS_SIZE = 50.0;
    private static final double MAX_CANVAS_SIZE = 4096.0;

    @FXML private BorderPane rootPane;
    @FXML private AnchorPane canvasPane;
    @FXML private Canvas canvas;
    @FXML private VBox sidePanel;
    @FXML private ListView<String> modelListView;
    @FXML private TextField vertexIndicesField;
    @FXML private TextField polygonIndicesField;
    @FXML private Label statusLabel;
    @FXML private Label modelCountLabel;
    @FXML private Label activeModelLabel;
    @FXML private Label fpsLabel;
    @FXML private Label vertexCountLabel;
    @FXML private Label polygonCountLabel;
    @FXML private Label textureCountLabel;
    @FXML private Label normalCountLabel;
    @FXML private RadioMenuItem darkThemeMenuItem;
    @FXML private RadioMenuItem lightThemeMenuItem;
    @FXML private MenuItem saveMenuItem;
    @FXML private Button removeModelButton;
    @FXML private Button deleteVerticesButton;
    @FXML private Button deletePolygonsButton;

    private Scene scene;
    private Camera camera;
    private Timeline timeline;
    private ObservableList<String> modelListItems;
    private boolean isDarkTheme = true;
    private long lastFrameTime = System.nanoTime();
    private int frameCount = 0;

    // Флаги состояния инициализации
    private volatile boolean isCanvasReady = false;
    private volatile boolean isSceneShown = false;
    private int initializationAttempts = 0;
    private static final int MAX_INIT_ATTEMPTS = 10;

    @FXML
    private void initialize() {
        scene = new Scene();

        camera = new Camera(
                new Vector3f(0, 0, 100),
                new Vector3f(0, 0, 0),
                1.0F, 1, 0.01F, 100);

        modelListItems = FXCollections.observableArrayList();
        modelListView.setItems(modelListItems);
        modelListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        modelListView.getSelectionModel().selectedIndexProperty().addListener(
                (observable, oldValue, newValue) -> onModelSelectionChanged());

        // Многоступенчатая отложенная инициализация для macOS
        Platform.runLater(this::scheduleCanvasInitialization);
    }

    /**
     * Планирует инициализацию Canvas с задержкой для macOS.
     */
    private void scheduleCanvasInitialization() {
        // Ждём, пока сцена будет показана
        if (rootPane.getScene() == null) {
            initializationAttempts++;
            if (initializationAttempts < MAX_INIT_ATTEMPTS) {
                Platform.runLater(this::scheduleCanvasInitialization);
            }
            return;
        }

        // Слушаем событие показа окна
        if (rootPane.getScene().getWindow() != null) {
            rootPane.getScene().getWindow().showingProperty().addListener((obs, wasShowing, isShowing) -> {
                if (isShowing && !isSceneShown) {
                    isSceneShown = true;
                    // Даём дополнительную задержку после показа окна
                    Platform.runLater(() -> {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ignored) {}
                        Platform.runLater(this::initializeCanvasSafely);
                    });
                }
            });

            // Если окно уже показано
            if (rootPane.getScene().getWindow().isShowing()) {
                isSceneShown = true;
                Platform.runLater(this::initializeCanvasSafely);
            }
        } else {
            // Повторяем попытку
            initializationAttempts++;
            if (initializationAttempts < MAX_INIT_ATTEMPTS) {
                Platform.runLater(this::scheduleCanvasInitialization);
            }
        }
    }

    /**
     * Безопасная инициализация Canvas с проверками.
     */
    private void initializeCanvasSafely() {
        try {
            // Устанавливаем начальные минимальные размеры
            canvas.setWidth(MIN_CANVAS_SIZE);
            canvas.setHeight(MIN_CANVAS_SIZE);

            // Настраиваем привязку размеров
            setupCanvasBindings();

            // Начинаем с небольшой задержки для стабильности
            Timeline delayedStart = new Timeline(new KeyFrame(Duration.millis(200), e -> {
                updateCanvasSize();
                isCanvasReady = true;
                startRenderLoop();
                updateUI();
                setStatus("Приложение готово к работе");
            }));
            delayedStart.play();

        } catch (Exception e) {
            System.err.println("Ошибка инициализации Canvas: " + e.getMessage());
            setStatus("Ошибка инициализации");
        }
    }

    /**
     * Настройка привязки размеров Canvas к родительской панели.
     */
    private void setupCanvasBindings() {
        canvasPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (isCanvasReady && newVal.doubleValue() > MIN_CANVAS_SIZE) {
                Platform.runLater(this::updateCanvasSize);
            }
        });

        canvasPane.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (isCanvasReady && newVal.doubleValue() > MIN_CANVAS_SIZE) {
                Platform.runLater(this::updateCanvasSize);
            }
        });
    }

    /**
     * Безопасное обновление размеров Canvas с ограничениями.
     */
    private void updateCanvasSize() {
        double width = canvasPane.getWidth();
        double height = canvasPane.getHeight();

        // Применяем ограничения для macOS
        width = clampSize(width);
        height = clampSize(height);

        if (width > MIN_CANVAS_SIZE && height > MIN_CANVAS_SIZE) {
            canvas.setWidth(width);
            canvas.setHeight(height);
        }
    }

    /**
     * Ограничивает размер в допустимых пределах.
     */
    private double clampSize(double size) {
        if (size < MIN_CANVAS_SIZE) return MIN_CANVAS_SIZE;
        if (size > MAX_CANVAS_SIZE) return MAX_CANVAS_SIZE;
        return size;
    }

    private void startRenderLoop() {
        if (timeline != null) {
            timeline.stop();
        }

        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        // 30 FPS - более стабильно для macOS
        KeyFrame frame = new KeyFrame(Duration.millis(33), event -> {
            if (isCanvasReady) {
                safeRenderFrame();
                updateFps();
            }
        });

        timeline.getKeyFrames().add(frame);
        timeline.play();
    }

    /**
     * Безопасный рендеринг кадра с защитой от исключений.
     */
    private void safeRenderFrame() {
        try {
            renderFrame();
        } catch (Exception e) {
            // Игнорируем ошибки рендеринга - они часто временные на macOS
        }
    }

    private void renderFrame() {
        if (canvas == null) return;

        double width = canvas.getWidth();
        double height = canvas.getHeight();

        // Защита от слишком маленьких или больших размеров
        if (width < MIN_CANVAS_SIZE || height < MIN_CANVAS_SIZE ||
            width > MAX_CANVAS_SIZE || height > MAX_CANVAS_SIZE) {
            return;
        }

        GraphicsContext gc = canvas.getGraphicsContext2D();
        if (gc == null) return;

        // Очищаем канвас
        if (isDarkTheme) {
            gc.setFill(Color.web("#1e1e1e"));
        } else {
            gc.setFill(Color.web("#f0f0f0"));
        }
        gc.fillRect(0, 0, width, height);

        // Устанавливаем цвет линий
        if (isDarkTheme) {
            gc.setStroke(Color.web("#00ff00"));
        } else {
            gc.setStroke(Color.web("#000000"));
        }
        gc.setLineWidth(1.0);

        camera.setAspectRatio((float) (width / height));

        // Рендерим все модели
        for (Model model : scene.getAllModels()) {
            if (model != null && !model.isEmpty()) {
                try {
                    RenderEngine.render(gc, camera, model, (int) width, (int) height);
                } catch (Exception e) {
                    // Пропускаем ошибки рендеринга отдельных моделей
                }
            }
        }

        frameCount++;
    }

    private void updateFps() {
        long currentTime = System.nanoTime();
        double elapsed = (currentTime - lastFrameTime) / 1_000_000_000.0;

        if (elapsed >= 1.0) {
            double fps = frameCount / elapsed;
            frameCount = 0;
            lastFrameTime = currentTime;

            final double finalFps = fps;
            Platform.runLater(() -> {
                if (fpsLabel != null) {
                    fpsLabel.setText(String.format("FPS: %.0f", finalFps));
                }
            });
        }
    }

    @FXML
    private void onOpenModelMenuItemClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("OBJ файлы (*.obj)", "*.obj"));
        fileChooser.setTitle("Открыть 3D модель");

        File file = fileChooser.showOpenDialog(getStage());
        if (file == null) return;

        loadModelFromFile(file);
    }

    private void loadModelFromFile(File file) {
        try {
            String fileContent = Files.readString(file.toPath());
            Model model = ObjReader.read(fileContent);

            String modelName = file.getName();
            int index = scene.addModel(model, modelName);
            modelListItems.add(modelName);

            modelListView.getSelectionModel().select(index);
            scene.setActiveModel(index);

            updateUI();
            setStatus("Модель загружена: " + modelName);

        } catch (ObjReaderException e) {
            showErrorAlert("Ошибка чтения OBJ", e.getMessage());
        } catch (IOException e) {
            showErrorAlert("Ошибка чтения файла", e.getMessage());
        } catch (Exception e) {
            showErrorAlert("Ошибка", e.getMessage());
        }
    }

    @FXML
    private void onSaveModelMenuItemClick() {
        if (!scene.hasActiveModels()) {
            showWarningAlert("Нет активной модели", "Выберите модель для сохранения.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("OBJ файлы (*.obj)", "*.obj"));
        fileChooser.setTitle("Сохранить модель");

        int activeIndex = scene.getFirstActiveModelIndex();
        String suggestedName = scene.getModelName(activeIndex);
        if (!suggestedName.endsWith(".obj")) {
            suggestedName += ".obj";
        }
        fileChooser.setInitialFileName(suggestedName);

        File file = fileChooser.showSaveDialog(getStage());
        if (file == null) return;

        try {
            ObjWriter.write(scene.getFirstActiveModel(), file);
            setStatus("Модель сохранена: " + file.getName());
            showInfoAlert("Сохранено", "Модель сохранена в:\n" + file.getAbsolutePath());
        } catch (Exception e) {
            showErrorAlert("Ошибка сохранения", e.getMessage());
        }
    }

    @FXML
    private void onExitMenuItemClick() {
        if (timeline != null) {
            timeline.stop();
        }
        Platform.exit();
    }

    @FXML
    private void onDeleteVerticesClick() {
        if (!scene.hasActiveModels()) {
            showWarningAlert("Нет активной модели", "Выберите модель.");
            return;
        }

        String input = vertexIndicesField.getText().trim();
        if (input.isEmpty()) {
            showWarningAlert("Нет индексов", "Введите индексы вершин (например: 0, 1, 5-10)");
            return;
        }

        try {
            List<Integer> indices = parseIndices(input);
            int totalRemoved = 0;
            for (Model model : scene.getActiveModels()) {
                totalRemoved += model.removeVerticesByIndices(indices);
            }
            vertexIndicesField.clear();
            updateModelInfo();
            setStatus("Удалено вершин: " + totalRemoved);
        } catch (Exception e) {
            showErrorAlert("Ошибка", e.getMessage());
        }
    }

    @FXML
    private void onDeletePolygonsClick() {
        if (!scene.hasActiveModels()) {
            showWarningAlert("Нет активной модели", "Выберите модель.");
            return;
        }

        String input = polygonIndicesField.getText().trim();
        if (input.isEmpty()) {
            showWarningAlert("Нет индексов", "Введите индексы полигонов (например: 0, 1, 5-10)");
            return;
        }

        try {
            List<Integer> indices = parseIndices(input);
            int totalRemoved = 0;
            for (Model model : scene.getActiveModels()) {
                totalRemoved += model.removePolygonsByIndices(indices);
            }
            polygonIndicesField.clear();
            updateModelInfo();
            setStatus("Удалено полигонов: " + totalRemoved);
        } catch (Exception e) {
            showErrorAlert("Ошибка", e.getMessage());
        }
    }

    private List<Integer> parseIndices(String input) {
        List<Integer> indices = new ArrayList<>();
        String[] parts = input.split(",");

        for (String part : parts) {
            part = part.trim();
            if (part.isEmpty()) continue;

            if (part.contains("-")) {
                String[] range = part.split("-");
                if (range.length == 2) {
                    int start = Integer.parseInt(range[0].trim());
                    int end = Integer.parseInt(range[1].trim());
                    for (int i = start; i <= end; i++) {
                        indices.add(i);
                    }
                }
            } else {
                indices.add(Integer.parseInt(part));
            }
        }
        return indices;
    }

    @FXML
    private void onSelectAllModelsClick() {
        modelListView.getSelectionModel().selectAll();
        scene.selectAll();
        updateUI();
    }

    @FXML
    private void onDeselectAllModelsClick() {
        modelListView.getSelectionModel().clearSelection();
        scene.clearSelection();
        updateUI();
    }

    private void onModelSelectionChanged() {
        scene.clearSelection();
        for (Integer index : modelListView.getSelectionModel().getSelectedIndices()) {
            scene.addToSelection(index);
        }
        updateModelInfo();
        updateUI();
    }

    @FXML
    private void onRemoveModelClick() {
        int selectedIndex = modelListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            showWarningAlert("Нет выбранной модели", "Выберите модель для удаления.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Подтверждение");
        confirm.setHeaderText("Удалить модель?");
        confirm.setContentText("Модель \"" + scene.getModelName(selectedIndex) + "\" будет удалена.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String modelName = scene.getModelName(selectedIndex);
            scene.removeModel(selectedIndex);
            modelListItems.remove(selectedIndex);
            updateUI();
            setStatus("Модель удалена: " + modelName);
        }
    }

    @FXML
    private void onRenameModelClick() {
        int selectedIndex = modelListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            showWarningAlert("Нет выбранной модели", "Выберите модель для переименования.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(scene.getModelName(selectedIndex));
        dialog.setTitle("Переименование");
        dialog.setHeaderText("Введите новое имя");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            String newName = result.get().trim();
            scene.setModelName(selectedIndex, newName);
            modelListItems.set(selectedIndex, newName);
            setStatus("Переименовано в: " + newName);
        }
    }

    @FXML
    public void handleCameraForward(ActionEvent e) {
        camera.movePosition(new Vector3f(0, 0, -TRANSLATION));
    }

    @FXML
    public void handleCameraBackward(ActionEvent e) {
        camera.movePosition(new Vector3f(0, 0, TRANSLATION));
    }

    @FXML
    public void handleCameraLeft(ActionEvent e) {
        camera.movePosition(new Vector3f(TRANSLATION, 0, 0));
    }

    @FXML
    public void handleCameraRight(ActionEvent e) {
        camera.movePosition(new Vector3f(-TRANSLATION, 0, 0));
    }

    @FXML
    public void handleCameraUp(ActionEvent e) {
        camera.movePosition(new Vector3f(0, TRANSLATION, 0));
    }

    @FXML
    public void handleCameraDown(ActionEvent e) {
        camera.movePosition(new Vector3f(0, -TRANSLATION, 0));
    }

    @FXML
    public void handleCameraReset() {
        camera.setPosition(new Vector3f(0, 0, 100));
        camera.setTarget(new Vector3f(0, 0, 0));
        setStatus("Камера сброшена");
    }

    @FXML
    private void onDarkThemeSelected() {
        applyTheme(DARK_THEME_PATH);
        isDarkTheme = true;
        setStatus("Тёмная тема");
    }

    @FXML
    private void onLightThemeSelected() {
        applyTheme(LIGHT_THEME_PATH);
        isDarkTheme = false;
        setStatus("Светлая тема");
    }

    private void applyTheme(String themePath) {
        try {
            if (rootPane != null && rootPane.getScene() != null) {
                javafx.scene.Scene fxScene = rootPane.getScene();
                fxScene.getStylesheets().clear();
                String css = Objects.requireNonNull(getClass().getResource(themePath)).toExternalForm();
                fxScene.getStylesheets().add(css);
            }
        } catch (Exception e) {
            System.err.println("Ошибка темы: " + e.getMessage());
        }
    }

    @FXML
    private void onAboutMenuItemClick() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("О программе");
        alert.setHeaderText("Simple3DViewer v1.0");
        alert.setContentText("3D-визуализатор OBJ моделей\n\nОптимизировано для macOS\n\nРазработано для курса компьютерной графики");
        alert.showAndWait();
    }

    private void updateUI() {
        if (modelCountLabel != null) {
            modelCountLabel.setText("Моделей: " + scene.getModelCount());
        }
        if (activeModelLabel != null) {
            activeModelLabel.setText("Активных: " + scene.getActiveModelCount());
        }
        updateModelInfo();

        boolean hasSelection = scene.hasActiveModels();
        if (saveMenuItem != null) saveMenuItem.setDisable(!hasSelection);
        if (removeModelButton != null) removeModelButton.setDisable(!hasSelection);
        if (deleteVerticesButton != null) deleteVerticesButton.setDisable(!hasSelection);
        if (deletePolygonsButton != null) deletePolygonsButton.setDisable(!hasSelection);
    }

    private void updateModelInfo() {
        Model model = scene.getFirstActiveModel();

        if (model != null) {
            if (vertexCountLabel != null) vertexCountLabel.setText("Вершины: " + model.vertices.size());
            if (polygonCountLabel != null) polygonCountLabel.setText("Полигоны: " + model.polygons.size());
            if (textureCountLabel != null) textureCountLabel.setText("Текст. коорд.: " + model.textureVertices.size());
            if (normalCountLabel != null) normalCountLabel.setText("Нормали: " + model.normals.size());
        } else {
            if (vertexCountLabel != null) vertexCountLabel.setText("Вершины: -");
            if (polygonCountLabel != null) polygonCountLabel.setText("Полигоны: -");
            if (textureCountLabel != null) textureCountLabel.setText("Текст. коорд.: -");
            if (normalCountLabel != null) normalCountLabel.setText("Нормали: -");
        }
    }

    private void setStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }

    private Stage getStage() {
        if (canvas != null && canvas.getScene() != null) {
            return (Stage) canvas.getScene().getWindow();
        }
        if (rootPane != null && rootPane.getScene() != null) {
            return (Stage) rootPane.getScene().getWindow();
        }
        return null;
    }

    private void showErrorAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText(title);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private void showWarningAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Предупреждение");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public Scene getScene() {
        return scene;
    }

    public Camera getCamera() {
        return camera;
    }

    public Canvas getCanvas() {
        return canvas;
    }
}
