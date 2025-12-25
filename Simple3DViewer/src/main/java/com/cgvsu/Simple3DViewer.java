package com.cgvsu;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Главный класс приложения Simple3DViewer.
 * Инициализирует JavaFX приложение и загружает основной интерфейс.
 */
public class Simple3DViewer extends Application {

    private static final String DEFAULT_THEME = "/com/cgvsu/css/dark-theme.css";

    @Override
    public void start(Stage stage) throws IOException {
        // Загружаем FXML разметку
        BorderPane root = FXMLLoader.load(
                Objects.requireNonNull(getClass().getResource("fxml/gui.fxml")));

        // Создаём сцену с начальными размерами
        Scene scene = new Scene(root, 1280, 720);

        // Применяем CSS тему
        try {
            String css = Objects.requireNonNull(
                    getClass().getResource(DEFAULT_THEME)).toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception e) {
            System.err.println("Не удалось загрузить тему: " + e.getMessage());
        }

        stage.setTitle("Simple3DViewer - 3D Визуализатор");
        stage.setScene(scene);

        // Сначала показываем окно
        stage.show();

        // Затем устанавливаем размеры (для macOS)
        Platform.runLater(() -> {
            stage.setWidth(1280);
            stage.setHeight(720);
        });
    }

    public static void main(String[] args) {
        launch();
    }
}
