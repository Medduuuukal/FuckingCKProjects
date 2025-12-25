package com.cgvsu.io.reader;

import com.cgvsu.exception.ViewerException;

/**
 * Исключение, возникающее при ошибках чтения модели.
 * Содержит информацию о месте возникновения ошибки (номер строки).
 */
public class ModelReadException extends ViewerException {

    private final int lineNumber;
    private final String fileName;

    /**
     * Создаёт исключение с сообщением об ошибке.
     * @param message сообщение об ошибке
     */
    public ModelReadException(String message) {
        super(ErrorCode.PARSE_ERROR, message);
        this.lineNumber = -1;
        this.fileName = null;
    }

    /**
     * Создаёт исключение с сообщением и номером строки.
     * @param message сообщение об ошибке
     * @param lineNumber номер строки, где произошла ошибка
     */
    public ModelReadException(String message, int lineNumber) {
        super(ErrorCode.PARSE_ERROR, formatMessage(message, lineNumber, null));
        this.lineNumber = lineNumber;
        this.fileName = null;
    }

    /**
     * Создаёт исключение с сообщением, номером строки и именем файла.
     * @param message сообщение об ошибке
     * @param lineNumber номер строки, где произошла ошибка
     * @param fileName имя файла
     */
    public ModelReadException(String message, int lineNumber, String fileName) {
        super(ErrorCode.PARSE_ERROR, formatMessage(message, lineNumber, fileName));
        this.lineNumber = lineNumber;
        this.fileName = fileName;
    }

    /**
     * Создаёт исключение с сообщением и причиной.
     * @param message сообщение об ошибке
     * @param cause причина исключения
     */
    public ModelReadException(String message, Throwable cause) {
        super(ErrorCode.PARSE_ERROR, message, cause);
        this.lineNumber = -1;
        this.fileName = null;
    }

    /**
     * Создаёт исключение с сообщением, номером строки и причиной.
     * @param message сообщение об ошибке
     * @param lineNumber номер строки, где произошла ошибка
     * @param cause причина исключения
     */
    public ModelReadException(String message, int lineNumber, Throwable cause) {
        super(ErrorCode.PARSE_ERROR, formatMessage(message, lineNumber, null), cause);
        this.lineNumber = lineNumber;
        this.fileName = null;
    }

    /**
     * Получить номер строки, где произошла ошибка.
     * @return номер строки или -1 если не указан
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Получить имя файла.
     * @return имя файла или null если не указано
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Проверить, указан ли номер строки.
     * @return true если номер строки известен
     */
    public boolean hasLineNumber() {
        return lineNumber >= 0;
    }

    /**
     * Проверить, указано ли имя файла.
     * @return true если имя файла известно
     */
    public boolean hasFileName() {
        return fileName != null && !fileName.isEmpty();
    }

    /**
     * Форматирование сообщения об ошибке.
     */
    private static String formatMessage(String message, int lineNumber, String fileName) {
        StringBuilder sb = new StringBuilder();

        if (fileName != null && !fileName.isEmpty()) {
            sb.append("Файл '").append(fileName).append("'");
            if (lineNumber >= 0) {
                sb.append(", строка ").append(lineNumber);
            }
            sb.append(": ");
        } else if (lineNumber >= 0) {
            sb.append("Строка ").append(lineNumber).append(": ");
        }

        sb.append(message);
        return sb.toString();
    }

    @Override
    public String getFullMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(getErrorCode().name()).append("] ");
        sb.append("Ошибка чтения модели");

        if (hasFileName()) {
            sb.append(" из файла '").append(fileName).append("'");
        }

        if (hasLineNumber()) {
            sb.append(" на строке ").append(lineNumber);
        }

        sb.append(": ").append(getMessage());

        return sb.toString();
    }

    // ==================== Статические фабричные методы ====================

    /**
     * Создать исключение для некорректного значения float.
     * @param lineNumber номер строки
     * @return исключение
     */
    public static ModelReadException invalidFloatValue(int lineNumber) {
        return new ModelReadException("Не удалось преобразовать значение в число с плавающей точкой", lineNumber);
    }

    /**
     * Создать исключение для некорректного значения int.
     * @param lineNumber номер строки
     * @return исключение
     */
    public static ModelReadException invalidIntValue(int lineNumber) {
        return new ModelReadException("Не удалось преобразовать значение в целое число", lineNumber);
    }

    /**
     * Создать исключение для недостаточного количества аргументов вершины.
     * @param lineNumber номер строки
     * @return исключение
     */
    public static ModelReadException tooFewVertexArguments(int lineNumber) {
        return new ModelReadException("Недостаточно аргументов для определения вершины", lineNumber);
    }

    /**
     * Создать исключение для недостаточного количества аргументов текстурной координаты.
     * @param lineNumber номер строки
     * @return исключение
     */
    public static ModelReadException tooFewTextureArguments(int lineNumber) {
        return new ModelReadException("Недостаточно аргументов для определения текстурной координаты", lineNumber);
    }

    /**
     * Создать исключение для недостаточного количества аргументов нормали.
     * @param lineNumber номер строки
     * @return исключение
     */
    public static ModelReadException tooFewNormalArguments(int lineNumber) {
        return new ModelReadException("Недостаточно аргументов для определения нормали", lineNumber);
    }

    /**
     * Создать исключение для некорректного формата полигона.
     * @param lineNumber номер строки
     * @return исключение
     */
    public static ModelReadException invalidFaceFormat(int lineNumber) {
        return new ModelReadException("Некорректный формат определения полигона", lineNumber);
    }

    /**
     * Создать исключение для недостаточного количества вершин в полигоне.
     * @param lineNumber номер строки
     * @return исключение
     */
    public static ModelReadException tooFewFaceVertices(int lineNumber) {
        return new ModelReadException("Полигон должен содержать минимум 3 вершины", lineNumber);
    }
}
