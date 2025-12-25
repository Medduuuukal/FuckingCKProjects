package com.cgvsu.objwriter;

import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * Класс для записи 3D-модели в формат OBJ.
 * Поддерживает экспорт вершин, текстурных координат, нормалей и полигонов.
 */
public class ObjWriter {

    private static final String OBJ_VERTEX_TOKEN = "v";
    private static final String OBJ_TEXTURE_TOKEN = "vt";
    private static final String OBJ_NORMAL_TOKEN = "vn";
    private static final String OBJ_FACE_TOKEN = "f";
    private static final String LINE_SEPARATOR = System.lineSeparator();

    /**
     * Записать модель в файл OBJ.
     * @param model модель для записи
     * @param file файл для сохранения
     * @throws ObjWriterException если произошла ошибка записи
     */
    public static void write(Model model, File file) throws ObjWriterException {
        if (model == null) {
            throw new ObjWriterException("Модель не может быть null");
        }
        if (file == null) {
            throw new ObjWriterException("Файл не может быть null");
        }

        try (FileWriter writer = new FileWriter(file)) {
            writeToWriter(model, writer);
        } catch (IOException e) {
            throw new ObjWriterException("Ошибка записи в файл: " + e.getMessage());
        }
    }

    /**
     * Записать модель в файл по указанному пути.
     * @param model модель для записи
     * @param filePath путь к файлу
     * @throws ObjWriterException если произошла ошибка записи
     */
    public static void write(Model model, String filePath) throws ObjWriterException {
        write(model, new File(filePath));
    }

    /**
     * Преобразовать модель в строку формата OBJ.
     * @param model модель для преобразования
     * @return строка в формате OBJ
     */
    public static String writeToString(Model model) {
        if (model == null) {
            throw new ObjWriterException("Модель не может быть null");
        }

        StringBuilder sb = new StringBuilder();

        // Комментарий с информацией о модели
        sb.append("# Simple3DViewer OBJ Export").append(LINE_SEPARATOR);
        sb.append("# Vertices: ").append(model.vertices.size()).append(LINE_SEPARATOR);
        sb.append("# Texture coords: ").append(model.textureVertices.size()).append(LINE_SEPARATOR);
        sb.append("# Normals: ").append(model.normals.size()).append(LINE_SEPARATOR);
        sb.append("# Polygons: ").append(model.polygons.size()).append(LINE_SEPARATOR);
        sb.append(LINE_SEPARATOR);

        // Записываем вершины
        writeVertices(model.vertices, sb);

        // Записываем текстурные координаты
        writeTextureVertices(model.textureVertices, sb);

        // Записываем нормали
        writeNormals(model.normals, sb);

        // Записываем полигоны
        writePolygons(model, sb);

        return sb.toString();
    }

    /**
     * Записать модель через Writer.
     */
    private static void writeToWriter(Model model, Writer writer) throws IOException {
        writer.write(writeToString(model));
    }

    /**
     * Записать вершины модели.
     */
    private static void writeVertices(List<Vector3f> vertices, StringBuilder sb) {
        for (Vector3f vertex : vertices) {
            sb.append(OBJ_VERTEX_TOKEN)
                    .append(" ")
                    .append(formatFloat(vertex.x))
                    .append(" ")
                    .append(formatFloat(vertex.y))
                    .append(" ")
                    .append(formatFloat(vertex.z))
                    .append(LINE_SEPARATOR);
        }
        if (!vertices.isEmpty()) {
            sb.append(LINE_SEPARATOR);
        }
    }

    /**
     * Записать текстурные координаты модели.
     */
    private static void writeTextureVertices(List<Vector2f> textureVertices, StringBuilder sb) {
        for (Vector2f texVertex : textureVertices) {
            sb.append(OBJ_TEXTURE_TOKEN)
                    .append(" ")
                    .append(formatFloat(texVertex.x))
                    .append(" ")
                    .append(formatFloat(texVertex.y))
                    .append(LINE_SEPARATOR);
        }
        if (!textureVertices.isEmpty()) {
            sb.append(LINE_SEPARATOR);
        }
    }

    /**
     * Записать нормали модели.
     */
    private static void writeNormals(List<Vector3f> normals, StringBuilder sb) {
        for (Vector3f normal : normals) {
            sb.append(OBJ_NORMAL_TOKEN)
                    .append(" ")
                    .append(formatFloat(normal.x))
                    .append(" ")
                    .append(formatFloat(normal.y))
                    .append(" ")
                    .append(formatFloat(normal.z))
                    .append(LINE_SEPARATOR);
        }
        if (!normals.isEmpty()) {
            sb.append(LINE_SEPARATOR);
        }
    }

    /**
     * Записать полигоны модели.
     */
    private static void writePolygons(Model model, StringBuilder sb) {
        for (Polygon polygon : model.polygons) {
            sb.append(OBJ_FACE_TOKEN);

            List<Integer> vertexIndices = polygon.getVertexIndices();
            List<Integer> textureIndices = polygon.getTextureVertexIndices();
            List<Integer> normalIndices = polygon.getNormalIndices();

            boolean hasTextures = !textureIndices.isEmpty();
            boolean hasNormals = !normalIndices.isEmpty();

            for (int i = 0; i < vertexIndices.size(); i++) {
                sb.append(" ");
                // OBJ индексы начинаются с 1, поэтому добавляем 1
                int vIndex = vertexIndices.get(i) + 1;

                if (hasTextures && hasNormals) {
                    // Формат: v/vt/vn
                    int vtIndex = textureIndices.get(i) + 1;
                    int vnIndex = normalIndices.get(i) + 1;
                    sb.append(vIndex).append("/").append(vtIndex).append("/").append(vnIndex);
                } else if (hasTextures) {
                    // Формат: v/vt
                    int vtIndex = textureIndices.get(i) + 1;
                    sb.append(vIndex).append("/").append(vtIndex);
                } else if (hasNormals) {
                    // Формат: v//vn
                    int vnIndex = normalIndices.get(i) + 1;
                    sb.append(vIndex).append("//").append(vnIndex);
                } else {
                    // Формат: v
                    sb.append(vIndex);
                }
            }
            sb.append(LINE_SEPARATOR);
        }
    }

    /**
     * Форматировать float в строку с достаточной точностью.
     */
    private static String formatFloat(float value) {
        // Убираем лишние нули после запятой для чистоты файла
        if (value == (int) value) {
            return String.valueOf((int) value);
        }
        // Используем до 6 знаков после запятой
        String formatted = String.format("%.6f", value);
        // Удаляем trailing zeros
        formatted = formatted.replaceAll("0+$", "");
        if (formatted.endsWith(".")) {
            formatted = formatted.substring(0, formatted.length() - 1);
        }
        return formatted;
    }
}
