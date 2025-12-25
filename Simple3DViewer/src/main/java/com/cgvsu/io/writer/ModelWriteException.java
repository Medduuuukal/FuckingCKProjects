package com.cgvsu.io.writer;

import com.cgvsu.exception.ViewerException;

/**
 * Исключение, возникающее при ошибках записи модели.
 * Содержит информацию о файле и причине ошибки.
 */
public class ModelWriteException extends ViewerException {

    private final String fileName;
    private final String modelName;

    /**
     * Создаёт исключение с сообщением об ошибке.
     * @param message сообщение об ошибке
     */
    public ModelWriteException(String message) {
        super(ErrorCode.FILE_WRITE_ERROR, message);
        this.fileName = null;
        this.modelName = null;
    }

    /**
     * Создаёт исключение с сообщением и причиной.
     * @param message сообщение об ошибке
     * @param cause причина исключения
     */
    public ModelWriteException(String message, Throwable cause) {
        super(ErrorCode.FILE_WRITE_ERROR, message, cause);
        this.fileName = null;
        this.modelName = null;
    }

    /**
     * Создаёт исключение с сообщением и именем файла.
     * @param message сообщение об ошибке
     * @param fileName имя файла
     */
    public ModelWriteException(String message, String fileName) {
        super(ErrorCode.FILE_WRITE_ERROR, formatMessage(message, fileName, null));
        this.fileName = fileName;
        this.modelName = null;
    }

    /**
     * Создаёт исключение с полной информацией.
     * @param message сообщение об ошибке
     * @param fileName имя файла
     * @param modelName имя модели
     */
    public ModelWriteException(String message, String fileName, String modelName) {
        super(ErrorCode.FILE_WRITE_ERROR, formatMessage(message, fileName, modelName));
        this.fileName = fileName;
        this.modelName = modelName;
    }

    /**
     * Создаёт исключение с полной информацией и причиной.
     * @param message сообщение об ошибке
     * @param fileName имя файла
     * @param modelName имя модели
     * @param cause причина исключения
     */
    public ModelWriteException(String message, String fileName, String modelName, Throwable cause) {
        super(ErrorCode.FILE_WRITE_ERROR, formatMessage(message, fileName, modelName), cause);
        this.fileName = fileName;
        this.modelName = modelName;
    }

    /**
     * Получить имя файла.
     * @return имя файла или null если не указано
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Получить имя модели.
     * @return имя модели или null если не указано
     */
    public String getModelName() {
        return modelName;
    }

    /**
     * Проверить, указано ли имя файла.
     * @return true если имя файла известно
     */
    public boolean hasFileName() {
        return fileName != null && !fileName.isEmpty();
    }

    /**
     * Проверить, указано ли имя модели.
     * @return true если имя модели известно
     */
    public boolean hasModelName() {
        return modelName != null && !modelName.isEmpty();
    }

    /**
     * Форматирование сообщения об ошибке.
     */
    private static String formatMessage(String message, String fileName, String modelName) {
        StringBuilder sb = new StringBuilder();

        if (modelName != null && !modelName.isEmpty()) {
            sb.append("Модель '").append(modelName).append("'");
            if (fileName != null && !fileName.isEmpty()) {
                sb.append(" -> файл '").append(fileName).append("'");
            }
            sb.append(": ");
        } else if (fileName != null && !fileName.isEmpty()) {
            sb.append("Файл '").append(fileName).append("': ");
        }

        sb.append(message);
        return sb.toString();
    }

    @Override
    public String getFullMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(getErrorCode().name()).append("] ");
        sb.append("Ошибка записи модели");

        if (hasModelName()) {
            sb.append(" '").append(modelName).append("'");
        }

        if (hasFileName()) {
            sb.append(" в файл '").append(fileName).append("'");
        }

        sb.append(": ").append(getMessage());

        return sb.toString();
    }

    // ==================== Статические фабричные методы ====================

    /**
     * Создать исключение для null модели.
     * @return исключение
     */
    public static ModelWriteException nullModel() {
        return new ModelWriteException("Модель не может быть null");
    }

    /**
     * Создать исключение для пустой модели.
     * @param modelName имя модели
     * @return исключение
     */
    public static ModelWriteException emptyModel(String modelName) {
        return new ModelWriteException("Модель не содержит данных для записи", null, modelName);
    }

    /**
     * Создать исключение для null файла.
     * @return исключение
     */
    public static ModelWriteException nullFile() {
        return new ModelWriteException("Файл не может быть null");
    }

    /**
     * Создать исключение для недоступного файла.
     * @param fileName имя файла
     * @return исключение
     */
    public static ModelWriteException fileNotWritable(String fileName) {
        return new ModelWriteException("Нет прав на запись в файл", fileName);
    }

    /**
     * Создать исключение для ошибки создания директории.
     * @param dirPath путь к директории
     * @return исключение
     */
    public static ModelWriteException cannotCreateDirectory(String dirPath) {
        return new ModelWriteException("Не удалось создать директорию: " + dirPath);
    }

    /**
     * Создать исключение для ошибки ввода-вывода.
     * @param fileName имя файла
     * @param cause причина ошибки
     * @return исключение
     */
    public static ModelWriteException ioError(String fileName, Throwable cause) {
        return new ModelWriteException("Ошибка ввода-вывода", fileName, null, cause);
    }

    /**
     * Создать исключение для невалидной модели.
     * @param modelName имя модели
     * @param validationErrors описание ошибок валидации
     * @return исключение
     */
    public static ModelWriteException invalidModel(String modelName, String validationErrors) {
        return new ModelWriteException("Модель не прошла валидацию: " + validationErrors, null, modelName);
    }
}
