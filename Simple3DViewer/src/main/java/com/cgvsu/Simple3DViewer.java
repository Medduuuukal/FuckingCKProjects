package com.cgvsu;

import javafx.application.Application;
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

    // Путь к тёмной теме по умолчанию
    private static final String DEFAULT_THEME = "/com/cgvsu/css/dark-theme.css";

    @Override
    public void start(Stage stage) throws IOException {
        // Загружаем FXML разметку
        BorderPane root = FXMLLoader.load(
                Objects.requireNonNull(getClass().getResource("fxml/gui.fxml")));

        // Создаём сцену
        Scene scene = new Scene(root);

        // Применяем CSS тему по умолчанию
        try {
            String css = Objects.requireNonNull(
                    getClass().getResource(DEFAULT_THEME)).toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception e) {
            System.err.println("Не удалось загрузить тему: " + e.getMessage());
        }

        // Настраиваем окно
        stage.setMinWidth(1280);
        stage.setMinHeight(720);
        stage.setWidth(1600);
        stage.setHeight(900);

        // Привязываем размеры корневого элемента к размерам сцены
        root.prefWidthProperty().bind(scene.widthProperty());
        root.prefHeightProperty().bind(scene.heightProperty());

        stage.setTitle("Simple3DViewer - 3D Визуализатор");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
