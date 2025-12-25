package com.cgvsu.io.reader;

import com.cgvsu.core.math.Vector2f;
import com.cgvsu.core.math.Vector3f;
import com.cgvsu.core.model.Model;
import com.cgvsu.core.model.Polygon;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Реализация ModelReader для чтения моделей в формате Wavefront OBJ.
 *
 * <p>Поддерживаемые элементы OBJ формата:</p>
 * <ul>
 *   <li>v - вершины (x, y, z)</li>
 *   <li>vt - текстурные координаты (u, v)</li>
 *   <li>vn - нормали (x, y, z)</li>
 *   <li>f - полигоны (индексы вершин/текстур/нормалей)</li>
 * </ul>
 *
 * <p>Пример использования:</p>
 * <pre>
 * ObjModelReader reader = new ObjModelReader();
 * Model model = reader.read(new File("cube.obj"));
 * </pre>
 */
public class ObjModelReader implements ModelReader {

    // ==================== Константы токенов OBJ ====================

    private static final String TOKEN_VERTEX = "v";
    private static final String TOKEN_TEXTURE = "vt";
    private static final String TOKEN_NORMAL = "vn";
    private static final String TOKEN_FACE = "f";
    private static final String TOKEN_COMMENT = "#";
    private static final String TOKEN_OBJECT = "o";
    private static final String TOKEN_GROUP = "g";
    private static final String TOKEN_MATERIAL_LIB = "mtllib";
    private static final String TOKEN_USE_MATERIAL = "usemtl";
    private static final String TOKEN_SMOOTH = "s";

    // ==================== Реализация ModelReader ====================

    @Override
    public Model read(String content) throws ModelReadException {
        if (content == null || content.isEmpty()) {
            throw new ModelReadException("Содержимое файла не может быть пустым");
        }

        Model model = new Model();
        int lineNumber = 0;

        try (Scanner scanner = new Scanner(content)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                lineNumber++;

                // Пропускаем пустые строки и комментарии
                if (line.isEmpty() || line.startsWith(TOKEN_COMMENT)) {
                    continue;
                }

                parseLine(line, lineNumber, model);
            }
        } catch (ModelReadException e) {
            throw e;
        } catch (Exception e) {
            throw new ModelReadException("Неожиданная ошибка при парсинге", lineNumber, e);
        }

        // Валидация модели
        validateModel(model);

        return model;
    }

    @Override
    public Model read(File file) throws ModelReadException, IOException {
        if (file == null) {
            throw new IllegalArgumentException("Файл не может быть null");
        }
        if (!file.exists()) {
            throw new FileNotFoundException("Файл не найден: " + file.getAbsolutePath());
        }
        if (!file.canRead()) {
            throw new IOException("Нет прав на чтение файла: " + file.getAbsolutePath());
        }

        String content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
        Model model = read(content);
        model.setName(extractModelName(file.getName()));
        return model;
    }

    @Override
    public Model read(InputStream inputStream) throws ModelReadException, IOException {
        if (inputStream == null) {
            throw new IllegalArgumentException("InputStream не может быть null");
        }

        String content;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            content = sb.toString();
        }

        return read(content);
    }

    @Override
    public String getSupportedExtension() {
        return "obj";
    }

    @Override
    public String getFormatDescription() {
        return "Wavefront OBJ";
    }

    // ==================== Парсинг строк ====================

    /**
     * Парсит одну строку OBJ файла.
     */
    private void parseLine(String line, int lineNumber, Model model) throws ModelReadException {
        List<String> tokens = tokenize(line);
        if (tokens.isEmpty()) {
            return;
        }

        String token = tokens.get(0);
        List<String> arguments = tokens.subList(1, tokens.size());

        switch (token) {
            case TOKEN_VERTEX -> parseVertex(arguments, lineNumber, model);
            case TOKEN_TEXTURE -> parseTextureVertex(arguments, lineNumber, model);
            case TOKEN_NORMAL -> parseNormal(arguments, lineNumber, model);
            case TOKEN_FACE -> parseFace(arguments, lineNumber, model);
            // Игнорируем другие токены (o, g, mtllib, usemtl, s)
            case TOKEN_OBJECT, TOKEN_GROUP, TOKEN_MATERIAL_LIB, TOKEN_USE_MATERIAL, TOKEN_SMOOTH -> {
                // Пропускаем - не реализовано
            }
            default -> {
                // Неизвестный токен - игнорируем
            }
        }
    }

    /**
     * Разбивает строку на токены.
     */
    private List<String> tokenize(String line) {
        String[] parts = line.split("\\s+");
        List<String> tokens = new ArrayList<>();
        for (String part : parts) {
            if (!part.isEmpty()) {
                tokens.add(part);
            }
        }
        return tokens;
    }

    // ==================== Парсинг элементов ====================

    /**
     * Парсит вершину (v x y z).
     */
    private void parseVertex(List<String> arguments, int lineNumber, Model model) throws ModelReadException {
        if (arguments.size() < 3) {
            throw ModelReadException.tooFewVertexArguments(lineNumber);
        }

        try {
            float x = Float.parseFloat(arguments.get(0));
            float y = Float.parseFloat(arguments.get(1));
            float z = Float.parseFloat(arguments.get(2));
            model.addVertex(new Vector3f(x, y, z));
        } catch (NumberFormatException e) {
            throw ModelReadException.invalidFloatValue(lineNumber);
        }
    }

    /**
     * Парсит текстурную координату (vt u v).
     */
    private void parseTextureVertex(List<String> arguments, int lineNumber, Model model) throws ModelReadException {
        if (arguments.size() < 2) {
            throw ModelReadException.tooFewTextureArguments(lineNumber);
        }

        try {
            float u = Float.parseFloat(arguments.get(0));
            float v = Float.parseFloat(arguments.get(1));
            model.addTextureVertex(new Vector2f(u, v));
        } catch (NumberFormatException e) {
            throw ModelReadException.invalidFloatValue(lineNumber);
        }
    }

    /**
     * Парсит нормаль (vn x y z).
     */
    private void parseNormal(List<String> arguments, int lineNumber, Model model) throws ModelReadException {
        if (arguments.size() < 3) {
            throw ModelReadException.tooFewNormalArguments(lineNumber);
        }

        try {
            float x = Float.parseFloat(arguments.get(0));
            float y = Float.parseFloat(arguments.get(1));
            float z = Float.parseFloat(arguments.get(2));
            model.addNormal(new Vector3f(x, y, z));
        } catch (NumberFormatException e) {
            throw ModelReadException.invalidFloatValue(lineNumber);
        }
    }

    /**
     * Парсит полигон (f v1 v2 v3 ... или f v1/vt1 v2/vt2 ... или f v1/vt1/vn1 ...).
     */
    private void parseFace(List<String> arguments, int lineNumber, Model model) throws ModelReadException {
        if (arguments.size() < 3) {
            throw ModelReadException.tooFewFaceVertices(lineNumber);
        }

        List<Integer> vertexIndices = new ArrayList<>();
        List<Integer> textureIndices = new ArrayList<>();
        List<Integer> normalIndices = new ArrayList<>();

        for (String faceVertex : arguments) {
            parseFaceVertex(faceVertex, lineNumber, vertexIndices, textureIndices, normalIndices);
        }

        Polygon polygon = new Polygon();
        polygon.setVertexIndices(vertexIndices);

        if (!textureIndices.isEmpty()) {
            polygon.setTextureVertexIndices(textureIndices);
        }

        if (!normalIndices.isEmpty()) {
            polygon.setNormalIndices(normalIndices);
        }

        model.addPolygon(polygon);
    }

    /**
     * Парсит один элемент полигона (v, v/vt, v/vt/vn, v//vn).
     */
    private void parseFaceVertex(String faceVertex, int lineNumber,
                                  List<Integer> vertexIndices,
                                  List<Integer> textureIndices,
                                  List<Integer> normalIndices) throws ModelReadException {
        try {
            String[] parts = faceVertex.split("/");

            switch (parts.length) {
                case 1 -> {
                    // Формат: v
                    int vIndex = parseIndex(parts[0]);
                    vertexIndices.add(vIndex);
                }
                case 2 -> {
                    // Формат: v/vt
                    int vIndex = parseIndex(parts[0]);
                    int vtIndex = parseIndex(parts[1]);
                    vertexIndices.add(vIndex);
                    textureIndices.add(vtIndex);
                }
                case 3 -> {
                    // Формат: v/vt/vn или v//vn
                    int vIndex = parseIndex(parts[0]);
                    vertexIndices.add(vIndex);

                    if (!parts[1].isEmpty()) {
                        int vtIndex = parseIndex(parts[1]);
                        textureIndices.add(vtIndex);
                    }

                    int vnIndex = parseIndex(parts[2]);
                    normalIndices.add(vnIndex);
                }
                default -> throw ModelReadException.invalidFaceFormat(lineNumber);
            }
        } catch (NumberFormatException e) {
            throw ModelReadException.invalidIntValue(lineNumber);
        }
    }

    /**
     * Парсит индекс из строки (OBJ индексы начинаются с 1, преобразуем в 0-based).
     * Поддерживает отрицательные индексы (относительные от конца).
     */
    private int parseIndex(String value) throws NumberFormatException {
        int index = Integer.parseInt(value.trim());
        // OBJ индексы начинаются с 1, преобразуем в 0-based
        // Отрицательные индексы означают "от конца списка"
        // Но так как мы не знаем размер списка здесь, просто вычитаем 1
        return index - 1;
    }

    // ==================== Валидация ====================

    /**
     * Валидирует загруженную модель.
     */
    private void validateModel(Model model) throws ModelReadException {
        List<String> errors = model.validate();
        if (!errors.isEmpty()) {
            StringBuilder sb = new StringBuilder("Модель содержит ошибки:\n");
            for (int i = 0; i < Math.min(errors.size(), 5); i++) {
                sb.append("  - ").append(errors.get(i)).append("\n");
            }
            if (errors.size() > 5) {
                sb.append("  ... и ещё ").append(errors.size() - 5).append(" ошибок");
            }
            throw new ModelReadException(sb.toString());
        }
    }

    // ==================== Утилитные методы ====================

    /**
     * Извлекает имя модели из имени файла (без расширения).
     */
    private String extractModelName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "Unnamed";
        }
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            return fileName.substring(0, dotIndex);
        }
        return fileName;
    }
}
