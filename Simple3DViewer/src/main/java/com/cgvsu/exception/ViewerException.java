package com.cgvsu.exception;

/**
 * Базовый класс исключений приложения Simple3DViewer.
 * Все специфичные исключения приложения должны наследоваться от этого класса.
 */
public class ViewerException extends RuntimeException {

    private final ErrorCode errorCode;

    /**
     * Коды ошибок приложения.
     */
    public enum ErrorCode {
        UNKNOWN("Неизвестная ошибка"),
        FILE_NOT_FOUND("Файл не найден"),
        FILE_READ_ERROR("Ошибка чтения файла"),
        FILE_WRITE_ERROR("Ошибка записи файла"),
        INVALID_FORMAT("Неверный формат данных"),
        PARSE_ERROR("Ошибка парсинга"),
        MODEL_ERROR("Ошибка модели"),
        RENDER_ERROR("Ошибка рендеринга"),
        SCENE_ERROR("Ошибка сцены"),
        VALIDATION_ERROR("Ошибка валидации");

        private final String description;

        ErrorCode(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public ViewerException(String message) {
        super(message);
        this.errorCode = ErrorCode.UNKNOWN;
    }

    public ViewerException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = ErrorCode.UNKNOWN;
    }

    public ViewerException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ViewerException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * Получить код ошибки.
     * @return код ошибки
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }

    /**
     * Получить полное сообщение с кодом ошибки.
     * @return форматированное сообщение
     */
    public String getFullMessage() {
        return String.format("[%s] %s: %s",
                errorCode.name(),
                errorCode.getDescription(),
                getMessage());
    }

    @Override
    public String toString() {
        return getFullMessage();
    }
}
