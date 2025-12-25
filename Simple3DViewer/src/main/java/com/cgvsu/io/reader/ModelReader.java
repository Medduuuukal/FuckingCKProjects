package com.cgvsu.io.reader;

import com.cgvsu.core.model.Model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * Интерфейс для чтения 3D-моделей из различных источников.
 * Реализации этого интерфейса обеспечивают поддержку конкретных форматов файлов.
 *
 * <p>Следует принципу Interface Segregation - определяет минимально необходимый
 * контракт для чтения моделей.</p>
 *
 * <p>Пример использования:</p>
 * <pre>
 * ModelReader reader = new ObjModelReader();
 * Model model = reader.read(new File("cube.obj"));
 * </pre>
 */
public interface ModelReader {

    /**
     * Прочитать модель из строки.
     * @param content содержимое файла модели
     * @return загруженная модель
     * @throws ModelReadException если произошла ошибка чтения
     */
    Model read(String content) throws ModelReadException;

    /**
     * Прочитать модель из файла.
     * @param file файл модели
     * @return загруженная модель
     * @throws ModelReadException если произошла ошибка чтения
     * @throws IOException если файл не найден или недоступен
     */
    Model read(File file) throws ModelReadException, IOException;

    /**
     * Прочитать модель из пути.
     * @param path путь к файлу модели
     * @return загруженная модель
     * @throws ModelReadException если произошла ошибка чтения
     * @throws IOException если файл не найден или недоступен
     */
    default Model read(Path path) throws ModelReadException, IOException {
        return read(path.toFile());
    }

    /**
     * Прочитать модель из потока ввода.
     * @param inputStream поток ввода
     * @return загруженная модель
     * @throws ModelReadException если произошла ошибка чтения
     * @throws IOException если произошла ошибка ввода-вывода
     */
    Model read(InputStream inputStream) throws ModelReadException, IOException;

    /**
     * Получить поддерживаемое расширение файла.
     * @return расширение файла (например, "obj")
     */
    String getSupportedExtension();

    /**
     * Получить описание формата.
     * @return описание формата (например, "Wavefront OBJ")
     */
    String getFormatDescription();

    /**
     * Проверить, поддерживается ли файл данным ридером.
     * @param file файл для проверки
     * @return true если файл поддерживается
     */
    default boolean supports(File file) {
        if (file == null || !file.exists()) {
            return false;
        }
        String name = file.getName().toLowerCase();
        return name.endsWith("." + getSupportedExtension().toLowerCase());
    }

    /**
     * Проверить, поддерживается ли расширение файла.
     * @param extension расширение для проверки
     * @return true если расширение поддерживается
     */
    default boolean supportsExtension(String extension) {
        if (extension == null) {
            return false;
        }
        String ext = extension.startsWith(".") ? extension.substring(1) : extension;
        return getSupportedExtension().equalsIgnoreCase(ext);
    }
}
