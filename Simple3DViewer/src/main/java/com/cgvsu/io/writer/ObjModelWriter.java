package com.cgvsu.io.writer;

import com.cgvsu.core.math.Vector2f;
import com.cgvsu.core.math.Vector3f;
import com.cgvsu.core.model.Model;
import com.cgvsu.core.model.Polygon;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;

/**
 * Реализация ModelWriter для записи моделей в формате Wavefront OBJ.
 *
 * <p>Записываемые элементы OBJ формата:</p>
 * <ul>
 *   <li>v - вершины (x, y, z)</li>
 *   <li>vt - текстурные координаты (u, v)</li>
 *   <li>vn - нормали (x, y, z)</li>
 *   <li>f - полигоны (индексы вершин/текстур/нормалей)</li>
 * </ul>
 *
 * <p>Пример использования:</p>
 * <pre>
 * ObjModelWriter writer = new ObjModelWriter();
 * writer.write(model, new File("output.obj"));
 *
 * // Или получить строку
 * String objContent = writer.writeToString(model);
 * </pre>
 */
public class ObjModelWriter implements ModelWriter {

    // ==================== Константы токенов OBJ ====================

    private static final String TOKEN_VERTEX = "v";
    private static final String TOKEN_TEXTURE = "vt";
    private static final String TOKEN_NORMAL = "vn";
    private static final String TOKEN_FACE = "f";
    private static final String TOKEN_COMMENT = "#";

    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final String SPACE = " ";
    private static final String SLASH = "/";

    // ==================== Настройки форматирования ====================

    /** Количество знаков после запятой для float значений */
    private int floatPrecision = 6;

    /** Записывать ли заголовок с комментариями */
    private boolean writeHeader = true;

    /** Записывать ли статистику модели в заголовке */
    private boolean writeStatistics = true;

    // ==================== Конструкторы ====================

    /**
     * Создаёт writer с настройками по умолчанию.
     */
    public ObjModelWriter() {
    }

    /**
     * Создаёт writer с указанной точностью float значений.
     * @param floatPrecision количество знаков после запятой
     */
    public ObjModelWriter(int floatPrecision) {
        setFloatPrecision(floatPrecision);
    }

    // ==================== Настройки ====================

    /**
     * Получить точность float значений.
     * @return количество знаков после запятой
     */
    public int getFloatPrecision() {
        return floatPrecision;
    }

    /**
     * Установить точность float значений.
     * @param precision количество знаков после запятой (1-10)
     */
    public void setFloatPrecision(int precision) {
        this.floatPrecision = Math.max(1, Math.min(10, precision));
    }

    /**
     * Проверить, записывается ли заголовок.
     * @return true если заголовок записывается
     */
    public boolean isWriteHeader() {
        return writeHeader;
    }

    /**
     * Установить запись заголовка.
     * @param writeHeader записывать ли заголовок
     */
    public void setWriteHeader(boolean writeHeader) {
        this.writeHeader = writeHeader;
    }

    /**
     * Проверить, записывается ли статистика.
     * @return true если статистика записывается
     */
    public boolean isWriteStatistics() {
        return writeStatistics;
    }

    /**
     * Установить запись статистики.
     * @param writeStatistics записывать ли статистику
     */
    public void setWriteStatistics(boolean writeStatistics) {
        this.writeStatistics = writeStatistics;
    }

    // ==================== Реализация ModelWriter ====================

    @Override
    public String writeToString(Model model) throws ModelWriteException {
        validateModel(model);

        StringBuilder sb = new StringBuilder();

        // Записываем заголовок
        if (writeHeader) {
            writeHeader(sb, model);
        }

        // Записываем вершины
        writeVertices(sb, model.getVertices());

        // Записываем текстурные координаты
        writeTextureVertices(sb, model.getTextureVertices());

        // Записываем нормали
        writeNormals(sb, model.getNormals());

        // Записываем полигоны
        writePolygons(sb, model);

        return sb.toString();
    }

    @Override
    public void write(Model model, File file) throws ModelWriteException, IOException {
        Objects.requireNonNull(file, "Файл не может быть null");

        validateModel(model);

        // Проверяем и создаём родительскую директорию если нужно
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                throw ModelWriteException.cannotCreateDirectory(parentDir.getAbsolutePath());
            }
        }

        // Проверяем возможность записи
        if (file.exists() && !file.canWrite()) {
            throw ModelWriteException.fileNotWritable(file.getAbsolutePath());
        }

        String content = writeToString(model);

        try {
            Files.writeString(file.toPath(), content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw ModelWriteException.ioError(file.getAbsolutePath(), e);
        }
    }

    @Override
    public void write(Model model, OutputStream outputStream) throws ModelWriteException, IOException {
        Objects.requireNonNull(outputStream, "OutputStream не может быть null");

        validateModel(model);

        String content = writeToString(model);

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {
            writer.write(content);
            writer.flush();
        }
    }

    @Override
    public String getFileExtension() {
        return "obj";
    }

    @Override
    public String getFormatDescription() {
        return "Wavefront OBJ";
    }

    @Override
    public String getMimeType() {
        return "model/obj";
    }

    // ==================== Методы записи элементов ====================

    /**
     * Записывает заголовок файла.
     */
    private void writeHeader(StringBuilder sb, Model model) {
        sb.append(TOKEN_COMMENT).append(" Wavefront OBJ File").append(LINE_SEPARATOR);
        sb.append(TOKEN_COMMENT).append(" Generated by Simple3DViewer").append(LINE_SEPARATOR);

        if (model.getName() != null && !model.getName().isEmpty()) {
            sb.append(TOKEN_COMMENT).append(" Model: ").append(model.getName()).append(LINE_SEPARATOR);
        }

        if (writeStatistics) {
            sb.append(TOKEN_COMMENT).append(LINE_SEPARATOR);
            sb.append(TOKEN_COMMENT).append(" Vertices: ").append(model.getVertexCount()).append(LINE_SEPARATOR);
            sb.append(TOKEN_COMMENT).append(" Texture coords: ").append(model.getTextureVertexCount()).append(LINE_SEPARATOR);
            sb.append(TOKEN_COMMENT).append(" Normals: ").append(model.getNormalCount()).append(LINE_SEPARATOR);
            sb.append(TOKEN_COMMENT).append(" Polygons: ").append(model.getPolygonCount()).append(LINE_SEPARATOR);
        }

        sb.append(LINE_SEPARATOR);
    }

    /**
     * Записывает вершины модели.
     */
    private void writeVertices(StringBuilder sb, List<Vector3f> vertices) {
        if (vertices.isEmpty()) {
            return;
        }

        sb.append(TOKEN_COMMENT).append(" Vertices").append(LINE_SEPARATOR);

        for (Vector3f vertex : vertices) {
            sb.append(TOKEN_VERTEX)
                    .append(SPACE).append(formatFloat(vertex.getX()))
                    .append(SPACE).append(formatFloat(vertex.getY()))
                    .append(SPACE).append(formatFloat(vertex.getZ()))
                    .append(LINE_SEPARATOR);
        }

        sb.append(LINE_SEPARATOR);
    }

    /**
     * Записывает текстурные координаты модели.
     */
    private void writeTextureVertices(StringBuilder sb, List<Vector2f> textureVertices) {
        if (textureVertices.isEmpty()) {
            return;
        }

        sb.append(TOKEN_COMMENT).append(" Texture coordinates").append(LINE_SEPARATOR);

        for (Vector2f texCoord : textureVertices) {
            sb.append(TOKEN_TEXTURE)
                    .append(SPACE).append(formatFloat(texCoord.getX()))
                    .append(SPACE).append(formatFloat(texCoord.getY()))
                    .append(LINE_SEPARATOR);
        }

        sb.append(LINE_SEPARATOR);
    }

    /**
     * Записывает нормали модели.
     */
    private void writeNormals(StringBuilder sb, List<Vector3f> normals) {
        if (normals.isEmpty()) {
            return;
        }

        sb.append(TOKEN_COMMENT).append(" Normals").append(LINE_SEPARATOR);

        for (Vector3f normal : normals) {
            sb.append(TOKEN_NORMAL)
                    .append(SPACE).append(formatFloat(normal.getX()))
                    .append(SPACE).append(formatFloat(normal.getY()))
                    .append(SPACE).append(formatFloat(normal.getZ()))
                    .append(LINE_SEPARATOR);
        }

        sb.append(LINE_SEPARATOR);
    }

    /**
     * Записывает полигоны модели.
     */
    private void writePolygons(StringBuilder sb, Model model) {
        List<Polygon> polygons = model.getPolygons();
        if (polygons.isEmpty()) {
            return;
        }

        sb.append(TOKEN_COMMENT).append(" Faces").append(LINE_SEPARATOR);

        for (Polygon polygon : polygons) {
            writeFace(sb, polygon);
        }
    }

    /**
     * Записывает один полигон.
     */
    private void writeFace(StringBuilder sb, Polygon polygon) {
        sb.append(TOKEN_FACE);

        List<Integer> vertexIndices = polygon.getVertexIndices();
        List<Integer> textureIndices = polygon.getTextureVertexIndices();
        List<Integer> normalIndices = polygon.getNormalIndices();

        boolean hasTextures = !textureIndices.isEmpty();
        boolean hasNormals = !normalIndices.isEmpty();

        for (int i = 0; i < vertexIndices.size(); i++) {
            sb.append(SPACE);

            // OBJ индексы начинаются с 1
            int vIndex = vertexIndices.get(i) + 1;

            if (hasTextures && hasNormals) {
                // Формат: v/vt/vn
                int vtIndex = i < textureIndices.size() ? textureIndices.get(i) + 1 : 0;
                int vnIndex = i < normalIndices.size() ? normalIndices.get(i) + 1 : 0;

                sb.append(vIndex).append(SLASH);
                if (vtIndex > 0) {
                    sb.append(vtIndex);
                }
                sb.append(SLASH).append(vnIndex);

            } else if (hasTextures) {
                // Формат: v/vt
                int vtIndex = i < textureIndices.size() ? textureIndices.get(i) + 1 : 0;
                sb.append(vIndex).append(SLASH);
                if (vtIndex > 0) {
                    sb.append(vtIndex);
                }

            } else if (hasNormals) {
                // Формат: v//vn
                int vnIndex = i < normalIndices.size() ? normalIndices.get(i) + 1 : 0;
                sb.append(vIndex).append(SLASH).append(SLASH).append(vnIndex);

            } else {
                // Формат: v
                sb.append(vIndex);
            }
        }

        sb.append(LINE_SEPARATOR);
    }

    // ==================== Вспомогательные методы ====================

    /**
     * Валидирует модель перед записью.
     */
    private void validateModel(Model model) throws ModelWriteException {
        if (model == null) {
            throw ModelWriteException.nullModel();
        }

        if (model.isEmpty()) {
            throw ModelWriteException.emptyModel(model.getName());
        }

        if (!model.isValid()) {
            List<String> errors = model.validate();
            String errorMsg = String.join("; ", errors.subList(0, Math.min(3, errors.size())));
            throw ModelWriteException.invalidModel(model.getName(), errorMsg);
        }
    }

    /**
     * Форматирует float значение с заданной точностью.
     * Удаляет лишние нули после запятой.
     */
    private String formatFloat(float value) {
        // Проверяем, является ли значение целым числом
        if (value == (int) value) {
            return String.valueOf((int) value);
        }

        // Форматируем с заданной точностью
        String format = "%." + floatPrecision + "f";
        String formatted = String.format(format, value);

        // Удаляем trailing zeros
        formatted = formatted.replaceAll("0+$", "");

        // Удаляем точку если после неё ничего не осталось
        if (formatted.endsWith(".")) {
            formatted = formatted.substring(0, formatted.length() - 1);
        }

        return formatted;
    }

    // ==================== Object methods ====================

    @Override
    public String toString() {
        return String.format("ObjModelWriter{precision=%d, writeHeader=%s, writeStatistics=%s}",
                floatPrecision, writeHeader, writeStatistics);
    }
}
