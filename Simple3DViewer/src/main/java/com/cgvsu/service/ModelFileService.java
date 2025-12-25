package com.cgvsu.service;

import com.cgvsu.config.AppConfig;
import com.cgvsu.core.model.Model;
import com.cgvsu.io.reader.ModelReadException;
import com.cgvsu.io.reader.ModelReader;
import com.cgvsu.io.reader.ObjModelReader;
import com.cgvsu.io.writer.ModelWriteException;
import com.cgvsu.io.writer.ModelWriter;
import com.cgvsu.io.writer.ObjModelWriter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * Сервис для работы с файлами 3D-моделей.
 * Обеспечивает загрузку и сохранение моделей в различных форматах.
 *
 * <p>Сервис использует паттерн Strategy для поддержки различных форматов файлов.
 * По умолчанию поддерживается формат Wavefront OBJ.</p>
 *
 * <p>Пример использования:</p>
 * <pre>
 * ModelFileService service = ModelFileService.getInstance();
 * Model model = service.loadModel(new File("cube.obj"));
 * service.saveModel(model, new File("output.obj"));
 * </pre>
 */
public class ModelFileService {

    // ==================== Singleton ====================

    private static volatile ModelFileService instance;
    private static final Object LOCK = new Object();

    /**
     * Получить единственный экземпляр сервиса.
     * @return экземпляр ModelFileService
     */
    public static ModelFileService getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new ModelFileService();
                }
            }
        }
        return instance;
    }

    // ==================== Поля ====================

    /** Зарегистрированные ридеры по расширениям файлов */
    private final Map<String, ModelReader> readers;

    /** Зарегистрированные врайтеры по расширениям файлов */
    private final Map<String, ModelWriter> writers;

    /** Последняя использованная директория для диалогов */
    private File lastDirectory;

    /** История загруженных файлов */
    private final List<File> recentFiles;

    /** Максимальный размер истории */
    private static final int MAX_RECENT_FILES = 10;

    // ==================== Конструктор ====================

    /**
     * Приватный конструктор для Singleton.
     */
    private ModelFileService() {
        this.readers = new HashMap<>();
        this.writers = new HashMap<>();
        this.recentFiles = new ArrayList<>();
        this.lastDirectory = new File(System.getProperty("user.home"));

        // Регистрируем стандартные форматы
        registerDefaultFormats();
    }

    /**
     * Регистрирует стандартные форматы файлов.
     */
    private void registerDefaultFormats() {
        // OBJ формат
        registerReader("obj", new ObjModelReader());
        registerWriter("obj", new ObjModelWriter());
    }

    // ==================== Регистрация форматов ====================

    /**
     * Зарегистрировать ридер для расширения файла.
     * @param extension расширение файла (без точки)
     * @param reader ридер для формата
     */
    public void registerReader(String extension, ModelReader reader) {
        Objects.requireNonNull(extension, "Расширение не может быть null");
        Objects.requireNonNull(reader, "Ридер не может быть null");
        readers.put(extension.toLowerCase(), reader);
    }

    /**
     * Зарегистрировать врайтер для расширения файла.
     * @param extension расширение файла (без точки)
     * @param writer врайтер для формата
     */
    public void registerWriter(String extension, ModelWriter writer) {
        Objects.requireNonNull(extension, "Расширение не может быть null");
        Objects.requireNonNull(writer, "Врайтер не может быть null");
        writers.put(extension.toLowerCase(), writer);
    }

    /**
     * Проверить, поддерживается ли формат для чтения.
     * @param extension расширение файла
     * @return true если формат поддерживается
     */
    public boolean canRead(String extension) {
        if (extension == null) return false;
        String ext = extension.startsWith(".") ? extension.substring(1) : extension;
        return readers.containsKey(ext.toLowerCase());
    }

    /**
     * Проверить, поддерживается ли формат для записи.
     * @param extension расширение файла
     * @return true если формат поддерживается
     */
    public boolean canWrite(String extension) {
        if (extension == null) return false;
        String ext = extension.startsWith(".") ? extension.substring(1) : extension;
        return writers.containsKey(ext.toLowerCase());
    }

    /**
     * Получить список поддерживаемых расширений для чтения.
     * @return множество расширений
     */
    public Set<String> getSupportedReadExtensions() {
        return Collections.unmodifiableSet(readers.keySet());
    }

    /**
     * Получить список поддерживаемых расширений для записи.
     * @return множество расширений
     */
    public Set<String> getSupportedWriteExtensions() {
        return Collections.unmodifiableSet(writers.keySet());
    }

    // ==================== Загрузка моделей ====================

    /**
     * Загрузить модель из файла.
     * @param file файл с моделью
     * @return загруженная модель
     * @throws ModelReadException если произошла ошибка парсинга
     * @throws IOException если файл недоступен
     * @throws IllegalArgumentException если формат не поддерживается
     */
    public Model loadModel(File file) throws ModelReadException, IOException {
        Objects.requireNonNull(file, "Файл не может быть null");

        if (!file.exists()) {
            throw new IOException("Файл не найден: " + file.getAbsolutePath());
        }

        String extension = getFileExtension(file);
        ModelReader reader = readers.get(extension.toLowerCase());

        if (reader == null) {
            throw new IllegalArgumentException(
                    "Формат '" + extension + "' не поддерживается. " +
                    "Поддерживаемые форматы: " + readers.keySet());
        }

        Model model = reader.read(file);

        // Обновляем историю
        addToRecentFiles(file);
        lastDirectory = file.getParentFile();

        return model;
    }

    /**
     * Загрузить модель из файла по пути.
     * @param path путь к файлу
     * @return загруженная модель
     * @throws ModelReadException если произошла ошибка парсинга
     * @throws IOException если файл недоступен
     */
    public Model loadModel(Path path) throws ModelReadException, IOException {
        return loadModel(path.toFile());
    }

    /**
     * Загрузить модель из файла по строковому пути.
     * @param filePath путь к файлу
     * @return загруженная модель
     * @throws ModelReadException если произошла ошибка парсинга
     * @throws IOException если файл недоступен
     */
    public Model loadModel(String filePath) throws ModelReadException, IOException {
        return loadModel(new File(filePath));
    }

    /**
     * Загрузить модель из строки содержимого.
     * @param content содержимое файла
     * @param format формат (расширение) файла
     * @return загруженная модель
     * @throws ModelReadException если произошла ошибка парсинга
     */
    public Model loadModelFromString(String content, String format) throws ModelReadException {
        Objects.requireNonNull(content, "Содержимое не может быть null");
        Objects.requireNonNull(format, "Формат не может быть null");

        ModelReader reader = readers.get(format.toLowerCase());
        if (reader == null) {
            throw new IllegalArgumentException("Формат '" + format + "' не поддерживается");
        }

        return reader.read(content);
    }

    // ==================== Сохранение моделей ====================

    /**
     * Сохранить модель в файл.
     * @param model модель для сохранения
     * @param file файл для записи
     * @throws ModelWriteException если произошла ошибка записи
     * @throws IOException если файл недоступен
     */
    public void saveModel(Model model, File file) throws ModelWriteException, IOException {
        Objects.requireNonNull(model, "Модель не может быть null");
        Objects.requireNonNull(file, "Файл не может быть null");

        String extension = getFileExtension(file);
        ModelWriter writer = writers.get(extension.toLowerCase());

        if (writer == null) {
            throw new IllegalArgumentException(
                    "Формат '" + extension + "' не поддерживается для записи. " +
                    "Поддерживаемые форматы: " + writers.keySet());
        }

        writer.write(model, file);

        // Обновляем последнюю директорию
        lastDirectory = file.getParentFile();
    }

    /**
     * Сохранить модель в файл по пути.
     * @param model модель для сохранения
     * @param path путь к файлу
     * @throws ModelWriteException если произошла ошибка записи
     * @throws IOException если файл недоступен
     */
    public void saveModel(Model model, Path path) throws ModelWriteException, IOException {
        saveModel(model, path.toFile());
    }

    /**
     * Сохранить модель в файл по строковому пути.
     * @param model модель для сохранения
     * @param filePath путь к файлу
     * @throws ModelWriteException если произошла ошибка записи
     * @throws IOException если файл недоступен
     */
    public void saveModel(Model model, String filePath) throws ModelWriteException, IOException {
        saveModel(model, new File(filePath));
    }

    /**
     * Преобразовать модель в строку указанного формата.
     * @param model модель
     * @param format формат (расширение) файла
     * @return строковое представление модели
     * @throws ModelWriteException если произошла ошибка записи
     */
    public String modelToString(Model model, String format) throws ModelWriteException {
        Objects.requireNonNull(model, "Модель не может быть null");
        Objects.requireNonNull(format, "Формат не может быть null");

        ModelWriter writer = writers.get(format.toLowerCase());
        if (writer == null) {
            throw new IllegalArgumentException("Формат '" + format + "' не поддерживается для записи");
        }

        return writer.writeToString(model);
    }

    // ==================== История и директории ====================

    /**
     * Получить последнюю использованную директорию.
     * @return директория
     */
    public File getLastDirectory() {
        return lastDirectory;
    }

    /**
     * Установить последнюю использованную директорию.
     * @param directory директория
     */
    public void setLastDirectory(File directory) {
        if (directory != null && directory.isDirectory()) {
            this.lastDirectory = directory;
        }
    }

    /**
     * Получить список недавних файлов.
     * @return неизменяемый список недавних файлов
     */
    public List<File> getRecentFiles() {
        return Collections.unmodifiableList(recentFiles);
    }

    /**
     * Добавить файл в историю.
     * @param file файл для добавления
     */
    private void addToRecentFiles(File file) {
        // Удаляем если уже есть в списке
        recentFiles.remove(file);

        // Добавляем в начало
        recentFiles.add(0, file);

        // Ограничиваем размер истории
        while (recentFiles.size() > MAX_RECENT_FILES) {
            recentFiles.remove(recentFiles.size() - 1);
        }
    }

    /**
     * Очистить историю файлов.
     */
    public void clearRecentFiles() {
        recentFiles.clear();
    }

    // ==================== Вспомогательные методы ====================

    /**
     * Получить расширение файла.
     * @param file файл
     * @return расширение без точки
     */
    public String getFileExtension(File file) {
        String name = file.getName();
        int lastDot = name.lastIndexOf('.');
        if (lastDot > 0 && lastDot < name.length() - 1) {
            return name.substring(lastDot + 1);
        }
        return "";
    }

    /**
     * Получить имя файла без расширения.
     * @param file файл
     * @return имя без расширения
     */
    public String getFileNameWithoutExtension(File file) {
        String name = file.getName();
        int lastDot = name.lastIndexOf('.');
        if (lastDot > 0) {
            return name.substring(0, lastDot);
        }
        return name;
    }

    /**
     * Проверить, существует ли файл.
     * @param file файл для проверки
     * @return true если файл существует
     */
    public boolean fileExists(File file) {
        return file != null && file.exists() && file.isFile();
    }

    /**
     * Создать фильтр расширений для диалога открытия.
     * @return описание фильтра и паттерн
     */
    public Map.Entry<String, String> getOpenFileFilter() {
        StringBuilder pattern = new StringBuilder();
        for (String ext : readers.keySet()) {
            if (pattern.length() > 0) {
                pattern.append(";");
            }
            pattern.append("*.").append(ext);
        }

        return Map.entry(
                AppConfig.OBJ_FILTER_DESCRIPTION,
                pattern.toString()
        );
    }

    /**
     * Создать фильтр расширений для диалога сохранения.
     * @return описание фильтра и паттерн
     */
    public Map.Entry<String, String> getSaveFileFilter() {
        StringBuilder pattern = new StringBuilder();
        for (String ext : writers.keySet()) {
            if (pattern.length() > 0) {
                pattern.append(";");
            }
            pattern.append("*.").append(ext);
        }

        return Map.entry(
                AppConfig.OBJ_FILTER_DESCRIPTION,
                pattern.toString()
        );
    }

    // ==================== Object methods ====================

    @Override
    public String toString() {
        return String.format("ModelFileService{readers=%s, writers=%s, recentFiles=%d}",
                readers.keySet(), writers.keySet(), recentFiles.size());
    }
}
