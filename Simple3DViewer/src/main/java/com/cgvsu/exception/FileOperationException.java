package com.cgvsu.exception;

/**
 * Исключение для операций с файлами (чтение/запись).
 * Используется при ошибках загрузки или сохранения моделей.
 */
public class FileOperationException extends ViewerException {

    private final String filePath;
    private final OperationType operationType;

    /**
     * Тип операции с файлом.
     */
    public enum OperationType {
        READ("Чтение"),
        WRITE("Запись"),
        DELETE("Удаление"),
        CREATE("Создание");

        private final String description;

        OperationType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public FileOperationException(String message, String filePath, OperationType operationType) {
        super(operationType == OperationType.READ ? ErrorCode.FILE_READ_ERROR : ErrorCode.FILE_WRITE_ERROR, message);
        this.filePath = filePath;
        this.operationType = operationType;
    }

    public FileOperationException(String message, String filePath, OperationType operationType, Throwable cause) {
        super(operationType == OperationType.READ ? ErrorCode.FILE_READ_ERROR : ErrorCode.FILE_WRITE_ERROR, message, cause);
        this.filePath = filePath;
        this.operationType = operationType;
    }

    /**
     * Получить путь к файлу.
     * @return путь к файлу
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Получить тип операции.
     * @return тип операции
     */
    public OperationType getOperationType() {
        return operationType;
    }

    @Override
    public String getFullMessage() {
        return String.format("[%s] %s файла '%s': %s",
                getErrorCode().name(),
                operationType.getDescription(),
                filePath,
                getMessage());
    }
}
