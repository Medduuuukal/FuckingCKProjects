package com.cgvsu.io.writer;

import com.cgvsu.core.model.Model;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;

/**
 * Интерфейс для записи 3D-моделей в различные форматы.
 * Реализации этого интерфейса обеспечивают поддержку конкретных форматов файлов.
 *
 * <p>Следует принципу Interface Segregation - определяет минимально необходимый
 * контракт для записи моделей.</p>
 *
 * <p>Пример использования:</p>
 * <pre>
 * ModelWriter writer = new ObjModelWriter();
 * writer.write(model, new File("output.obj"));
 * </pre>
 */
public interface ModelWriter {

    /**
     * Преобразовать модель в строку.
     * @param model модель для преобразования
     * @return строковое представление модели в целевом формате
     * @throws ModelWriteException если произошла ошибка преобразования
     */
    String writeToString(Model model) throws ModelWriteException;

    /**
     * Записать модель в файл.
     * @param model модель для записи
     * @param file файл для сохранения
     * @throws ModelWriteException если произошла ошибка записи
     * @throws IOException если файл недоступен для записи
     */
    void write(Model model, File file) throws ModelWriteException, IOException;

    /**
     * Записать модель по указанному пути.
     * @param model модель для записи
     * @param path путь к файлу
     * @throws ModelWriteException если произошла ошибка записи
     * @throws IOException если файл недоступен для записи
     */
    default void write(Model model, Path path) throws ModelWriteException, IOException {
        write(model, path.toFile());
    }

    /**
     * Записать модель в поток вывода.
     * @param model модель для записи
     * @param outputStream поток вывода
     * @throws ModelWriteException если произошла ошибка записи
     * @throws IOException если произошла ошибка ввода-вывода
     */
    void write(Model model, OutputStream outputStream) throws ModelWriteException, IOException;

    /**
     * Записать модель в файл по указанному пути (строка).
     * @param model модель для записи
     * @param filePath путь к файлу
     * @throws ModelWriteException если произошла ошибка записи
     * @throws IOException если файл недоступен для записи
     */
    default void write(Model model, String filePath) throws ModelWriteException, IOException {
        write(model, new File(filePath));
    }

    /**
     * Получить расширение файла для данного формата.
     * @return расширение файла (например, "obj")
     */
    String getFileExtension();

    /**
     * Получить описание формата.
     * @return описание формата (например, "Wavefront OBJ")
     */
    String getFormatDescription();

    /**
     * Получить MIME тип формата.
     * @return MIME тип или null если не определён
     */
    default String getMimeType() {
        return null;
    }

    /**
     * Проверить, поддерживает ли writer запись текстурных координат.
     * @return true если текстурные координаты поддерживаются
     */
    default boolean supportsTextureCoordinates() {
        return true;
    }

    /**
     * Проверить, поддерживает ли writer запись нормалей.
     * @return true если нормали поддерживаются
     */
    default boolean supportsNormals() {
        return true;
    }

    /**
     * Добавить расширение файла к пути, если оно отсутствует.
     * @param filePath исходный путь
     * @return путь с расширением
     */
    default String ensureExtension(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return "model." + getFileExtension();
        }

        String extension = "." + getFileExtension().toLowerCase();
        if (!filePath.toLowerCase().endsWith(extension)) {
            return filePath + extension;
        }
        return filePath;
    }

    /**
     * Проверить, может ли модель быть записана данным writer.
     * @param model модель для проверки
     * @return true если модель может быть записана
     */
    default boolean canWrite(Model model) {
        return model != null && !model.isEmpty();
    }
}
