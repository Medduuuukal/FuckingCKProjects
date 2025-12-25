package com.cgvsu.ui.component;

import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Optional;

/**
 * Вспомогательный класс для создания и отображения диалоговых окон.
 * Предоставляет удобные методы для показа Alert-ов, диалогов ввода,
 * выбора файлов и других UI компонентов.
 *
 * <p>Пример использования:</p>
 * <pre>
 * DialogHelper.showInfo("Готово", "Модель успешно загружена");
 *
 * Optional<String> name = DialogHelper.showTextInput("Имя модели", "Cube");
 *
 * if (DialogHelper.showConfirmation("Удалить?", "Вы уверены?")) {
 *     // удаляем
 * }
 * </pre>
 */
public final class DialogHelper {

    /**
     * Приватный конструктор - утилитный класс.
     */
    private DialogHelper() {
        throw new UnsupportedOperationException("Utility class");
    }

    // ==================== Информационные диалоги ====================

    /**
     * Показать информационное сообщение.
     * @param title заголовок
     * @param message сообщение
     */
    public static void showInfo(String title, String message) {
        showAlert(AlertType.INFORMATION, "Информация", title, message);
    }

    /**
     * Показать информационное сообщение с владельцем.
     * @param owner окно-владелец
     * @param title заголовок
     * @param message сообщение
     */
    public static void showInfo(Window owner, String title, String message) {
        showAlert(owner, AlertType.INFORMATION, "Информация", title, message);
    }

    // ==================== Предупреждения ====================

    /**
     * Показать предупреждение.
     * @param title заголовок
     * @param message сообщение
     */
    public static void showWarning(String title, String message) {
        showAlert(AlertType.WARNING, "Предупреждение", title, message);
    }

    /**
     * Показать предупреждение с владельцем.
     * @param owner окно-владелец
     * @param title заголовок
     * @param message сообщение
     */
    public static void showWarning(Window owner, String title, String message) {
        showAlert(owner, AlertType.WARNING, "Предупреждение", title, message);
    }

    // ==================== Ошибки ====================

    /**
     * Показать сообщение об ошибке.
     * @param title заголовок
     * @param message сообщение
     */
    public static void showError(String title, String message) {
        showAlert(AlertType.ERROR, "Ошибка", title, message);
    }

    /**
     * Показать сообщение об ошибке с владельцем.
     * @param owner окно-владелец
     * @param title заголовок
     * @param message сообщение
     */
    public static void showError(Window owner, String title, String message) {
        showAlert(owner, AlertType.ERROR, "Ошибка", title, message);
    }

    /**
     * Показать сообщение об ошибке с подробностями исключения.
     * @param title заголовок
     * @param message сообщение
     * @param exception исключение для отображения stack trace
     */
    public static void showError(String title, String message, Throwable exception) {
        Alert alert = createAlert(AlertType.ERROR, "Ошибка", title, message);

        if (exception != null) {
            // Создаём расширяемую область с stack trace
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            exception.printStackTrace(pw);
            String exceptionText = sw.toString();

            Label label = new Label("Подробности ошибки:");

            TextArea textArea = new TextArea(exceptionText);
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);

            GridPane expContent = new GridPane();
            expContent.setMaxWidth(Double.MAX_VALUE);
            expContent.add(label, 0, 0);
            expContent.add(textArea, 0, 1);

            alert.getDialogPane().setExpandableContent(expContent);
        }

        alert.showAndWait();
    }

    // ==================== Подтверждения ====================

    /**
     * Показать диалог подтверждения.
     * @param title заголовок
     * @param message сообщение
     * @return true если пользователь нажал OK
     */
    public static boolean showConfirmation(String title, String message) {
        Alert alert = createAlert(AlertType.CONFIRMATION, "Подтверждение", title, message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Показать диалог подтверждения с владельцем.
     * @param owner окно-владелец
     * @param title заголовок
     * @param message сообщение
     * @return true если пользователь нажал OK
     */
    public static boolean showConfirmation(Window owner, String title, String message) {
        Alert alert = createAlert(AlertType.CONFIRMATION, "Подтверждение", title, message);
        alert.initOwner(owner);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Показать диалог подтверждения с кастомными кнопками.
     * @param title заголовок
     * @param message сообщение
     * @param yesText текст кнопки "Да"
     * @param noText текст кнопки "Нет"
     * @return true если пользователь нажал "Да"
     */
    public static boolean showConfirmation(String title, String message, String yesText, String noText) {
        Alert alert = createAlert(AlertType.CONFIRMATION, "Подтверждение", title, message);

        ButtonType yesButton = new ButtonType(yesText, ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType(noText, ButtonBar.ButtonData.NO);

        alert.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == yesButton;
    }

    /**
     * Показать диалог с тремя вариантами: Да/Нет/Отмена.
     * @param title заголовок
     * @param message сообщение
     * @return Optional с ButtonType (YES, NO, CANCEL)
     */
    public static Optional<ButtonType> showYesNoCancel(String title, String message) {
        Alert alert = createAlert(AlertType.CONFIRMATION, "Подтверждение", title, message);

        ButtonType yesButton = new ButtonType("Да", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("Нет", ButtonBar.ButtonData.NO);
        ButtonType cancelButton = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(yesButton, noButton, cancelButton);

        return alert.showAndWait();
    }

    // ==================== Диалоги ввода ====================

    /**
     * Показать диалог ввода текста.
     * @param title заголовок
     * @param promptText подсказка в поле ввода
     * @return Optional с введённым текстом
     */
    public static Optional<String> showTextInput(String title, String promptText) {
        return showTextInput(title, null, promptText, "");
    }

    /**
     * Показать диалог ввода текста с начальным значением.
     * @param title заголовок
     * @param promptText подсказка в поле ввода
     * @param defaultValue значение по умолчанию
     * @return Optional с введённым текстом
     */
    public static Optional<String> showTextInput(String title, String promptText, String defaultValue) {
        return showTextInput(title, null, promptText, defaultValue);
    }

    /**
     * Показать диалог ввода текста с полными параметрами.
     * @param title заголовок
     * @param header заголовок содержимого
     * @param promptText подсказка в поле ввода
     * @param defaultValue значение по умолчанию
     * @return Optional с введённым текстом
     */
    public static Optional<String> showTextInput(String title, String header, String promptText, String defaultValue) {
        TextInputDialog dialog = new TextInputDialog(defaultValue);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(promptText);
        return dialog.showAndWait();
    }

    // ==================== Выбор из списка ====================

    /**
     * Показать диалог выбора из списка.
     * @param title заголовок
     * @param message сообщение
     * @param choices варианты выбора
     * @param defaultChoice выбор по умолчанию
     * @param <T> тип элементов
     * @return Optional с выбранным элементом
     */
    public static <T> Optional<T> showChoice(String title, String message, List<T> choices, T defaultChoice) {
        ChoiceDialog<T> dialog = new ChoiceDialog<>(defaultChoice, choices);
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setContentText(message);
        return dialog.showAndWait();
    }

    /**
     * Показать диалог выбора из массива.
     * @param title заголовок
     * @param message сообщение
     * @param choices варианты выбора
     * @param <T> тип элементов
     * @return Optional с выбранным элементом
     */
    @SafeVarargs
    public static <T> Optional<T> showChoice(String title, String message, T... choices) {
        if (choices == null || choices.length == 0) {
            return Optional.empty();
        }
        ChoiceDialog<T> dialog = new ChoiceDialog<>(choices[0], choices);
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setContentText(message);
        return dialog.showAndWait();
    }

    // ==================== Диалоги выбора файлов ====================

    /**
     * Показать диалог открытия файла.
     * @param owner окно-владелец
     * @param title заголовок диалога
     * @param initialDirectory начальная директория
     * @param filters фильтры расширений
     * @return выбранный файл или null
     */
    public static File showOpenFileDialog(Window owner, String title, File initialDirectory,
                                           FileChooser.ExtensionFilter... filters) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);

        if (initialDirectory != null && initialDirectory.isDirectory()) {
            fileChooser.setInitialDirectory(initialDirectory);
        }

        if (filters != null && filters.length > 0) {
            fileChooser.getExtensionFilters().addAll(filters);
        }

        return fileChooser.showOpenDialog(owner);
    }

    /**
     * Показать диалог открытия OBJ файла.
     * @param owner окно-владелец
     * @param initialDirectory начальная директория
     * @return выбранный файл или null
     */
    public static File showOpenObjDialog(Window owner, File initialDirectory) {
        FileChooser.ExtensionFilter objFilter =
            new FileChooser.ExtensionFilter("OBJ файлы (*.obj)", "*.obj");
        FileChooser.ExtensionFilter allFilter =
            new FileChooser.ExtensionFilter("Все файлы (*.*)", "*.*");

        return showOpenFileDialog(owner, "Открыть 3D модель", initialDirectory, objFilter, allFilter);
    }

    /**
     * Показать диалог сохранения файла.
     * @param owner окно-владелец
     * @param title заголовок диалога
     * @param initialDirectory начальная директория
     * @param initialFileName начальное имя файла
     * @param filters фильтры расширений
     * @return выбранный файл или null
     */
    public static File showSaveFileDialog(Window owner, String title, File initialDirectory,
                                           String initialFileName, FileChooser.ExtensionFilter... filters) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);

        if (initialDirectory != null && initialDirectory.isDirectory()) {
            fileChooser.setInitialDirectory(initialDirectory);
        }

        if (initialFileName != null && !initialFileName.isEmpty()) {
            fileChooser.setInitialFileName(initialFileName);
        }

        if (filters != null && filters.length > 0) {
            fileChooser.getExtensionFilters().addAll(filters);
        }

        return fileChooser.showSaveDialog(owner);
    }

    /**
     * Показать диалог сохранения OBJ файла.
     * @param owner окно-владелец
     * @param initialDirectory начальная директория
     * @param suggestedName предлагаемое имя файла
     * @return выбранный файл или null
     */
    public static File showSaveObjDialog(Window owner, File initialDirectory, String suggestedName) {
        FileChooser.ExtensionFilter objFilter =
            new FileChooser.ExtensionFilter("OBJ файлы (*.obj)", "*.obj");

        String fileName = suggestedName;
        if (fileName != null && !fileName.toLowerCase().endsWith(".obj")) {
            fileName += ".obj";
        }

        return showSaveFileDialog(owner, "Сохранить модель", initialDirectory, fileName, objFilter);
    }

    // ==================== Приватные методы ====================

    /**
     * Создать Alert с заданными параметрами.
     */
    private static Alert createAlert(AlertType type, String dialogTitle, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(dialogTitle);
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert;
    }

    /**
     * Показать Alert.
     */
    private static void showAlert(AlertType type, String dialogTitle, String header, String content) {
        Alert alert = createAlert(type, dialogTitle, header, content);
        alert.showAndWait();
    }

    /**
     * Показать Alert с владельцем.
     */
    private static void showAlert(Window owner, AlertType type, String dialogTitle, String header, String content) {
        Alert alert = createAlert(type, dialogTitle, header, content);
        alert.initOwner(owner);
        alert.showAndWait();
    }
}
