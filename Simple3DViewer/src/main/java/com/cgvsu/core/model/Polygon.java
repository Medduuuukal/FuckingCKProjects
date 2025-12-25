package com.cgvsu.core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Реализация полигона 3D-модели.
 * Полигон определяется списками индексов вершин, текстурных координат и нормалей.
 *
 * <p>Класс обеспечивает полную инкапсуляцию данных и защиту от изменения
 * внутреннего состояния через возвращаемые списки.</p>
 */
public class Polygon implements IPolygon {

    private static final int MIN_VERTICES = 3;

    private final List<Integer> vertexIndices;
    private final List<Integer> textureVertexIndices;
    private final List<Integer> normalIndices;

    // ==================== Конструкторы ====================

    /**
     * Создаёт пустой полигон.
     */
    public Polygon() {
        this.vertexIndices = new ArrayList<>();
        this.textureVertexIndices = new ArrayList<>();
        this.normalIndices = new ArrayList<>();
    }

    /**
     * Создаёт полигон с заданными индексами вершин.
     * @param vertexIndices список индексов вершин
     * @throws IllegalArgumentException если количество вершин меньше 3
     */
    public Polygon(List<Integer> vertexIndices) {
        this();
        setVertexIndices(vertexIndices);
    }

    /**
     * Создаёт полигон со всеми индексами.
     * @param vertexIndices список индексов вершин
     * @param textureVertexIndices список индексов текстурных координат
     * @param normalIndices список индексов нормалей
     */
    public Polygon(List<Integer> vertexIndices,
                   List<Integer> textureVertexIndices,
                   List<Integer> normalIndices) {
        this();
        setVertexIndices(vertexIndices);
        if (textureVertexIndices != null && !textureVertexIndices.isEmpty()) {
            setTextureVertexIndices(textureVertexIndices);
        }
        if (normalIndices != null && !normalIndices.isEmpty()) {
            setNormalIndices(normalIndices);
        }
    }

    /**
     * Копирующий конструктор.
     * @param other полигон для копирования
     */
    public Polygon(Polygon other) {
        Objects.requireNonNull(other, "Полигон не может быть null");
        this.vertexIndices = new ArrayList<>(other.vertexIndices);
        this.textureVertexIndices = new ArrayList<>(other.textureVertexIndices);
        this.normalIndices = new ArrayList<>(other.normalIndices);
    }

    // ==================== Реализация IPolygon - операции чтения ====================

    @Override
    public List<Integer> getVertexIndices() {
        return Collections.unmodifiableList(vertexIndices);
    }

    @Override
    public List<Integer> getTextureVertexIndices() {
        return Collections.unmodifiableList(textureVertexIndices);
    }

    @Override
    public List<Integer> getNormalIndices() {
        return Collections.unmodifiableList(normalIndices);
    }

    @Override
    public int getVertexCount() {
        return vertexIndices.size();
    }

    @Override
    public int getVertexIndex(int position) {
        if (position < 0 || position >= vertexIndices.size()) {
            throw new IndexOutOfBoundsException(
                    String.format("Позиция %d вне диапазона [0, %d)", position, vertexIndices.size()));
        }
        return vertexIndices.get(position);
    }

    @Override
    public boolean hasTextureCoordinates() {
        return !textureVertexIndices.isEmpty();
    }

    @Override
    public boolean hasNormals() {
        return !normalIndices.isEmpty();
    }

    // ==================== Реализация IPolygon - операции модификации ====================

    @Override
    public void setVertexIndices(List<Integer> vertexIndices) {
        Objects.requireNonNull(vertexIndices, "Список индексов вершин не может быть null");
        if (vertexIndices.size() < MIN_VERTICES) {
            throw new IllegalArgumentException(
                    String.format("Полигон должен содержать минимум %d вершины, получено: %d",
                            MIN_VERTICES, vertexIndices.size()));
        }
        validateIndices(vertexIndices, "вершин");
        this.vertexIndices.clear();
        this.vertexIndices.addAll(vertexIndices);
    }

    @Override
    public void setTextureVertexIndices(List<Integer> textureVertexIndices) {
        Objects.requireNonNull(textureVertexIndices, "Список индексов текстурных координат не может быть null");
        if (!textureVertexIndices.isEmpty()) {
            validateIndices(textureVertexIndices, "текстурных координат");
        }
        this.textureVertexIndices.clear();
        this.textureVertexIndices.addAll(textureVertexIndices);
    }

    @Override
    public void setNormalIndices(List<Integer> normalIndices) {
        Objects.requireNonNull(normalIndices, "Список индексов нормалей не может быть null");
        if (!normalIndices.isEmpty()) {
            validateIndices(normalIndices, "нормалей");
        }
        this.normalIndices.clear();
        this.normalIndices.addAll(normalIndices);
    }

    @Override
    public void clearTextureCoordinates() {
        textureVertexIndices.clear();
    }

    @Override
    public void clearNormals() {
        normalIndices.clear();
    }

    @Override
    public IPolygon copy() {
        return new Polygon(this);
    }

    // ==================== Дополнительные методы ====================

    /**
     * Получить изменяемый список индексов вершин.
     * Используется для внутренних операций модификации.
     * @return изменяемый список индексов вершин
     */
    public List<Integer> getVertexIndicesMutable() {
        return vertexIndices;
    }

    /**
     * Получить изменяемый список индексов текстурных координат.
     * @return изменяемый список индексов текстурных координат
     */
    public List<Integer> getTextureVertexIndicesMutable() {
        return textureVertexIndices;
    }

    /**
     * Получить изменяемый список индексов нормалей.
     * @return изменяемый список индексов нормалей
     */
    public List<Integer> getNormalIndicesMutable() {
        return normalIndices;
    }

    /**
     * Добавить индекс вершины.
     * @param vertexIndex индекс вершины
     * @throws IllegalArgumentException если индекс отрицательный
     */
    public void addVertexIndex(int vertexIndex) {
        validateIndex(vertexIndex, "вершины");
        vertexIndices.add(vertexIndex);
    }

    /**
     * Добавить индекс текстурной координаты.
     * @param textureIndex индекс текстурной координаты
     * @throws IllegalArgumentException если индекс отрицательный
     */
    public void addTextureVertexIndex(int textureIndex) {
        validateIndex(textureIndex, "текстурной координаты");
        textureVertexIndices.add(textureIndex);
    }

    /**
     * Добавить индекс нормали.
     * @param normalIndex индекс нормали
     * @throws IllegalArgumentException если индекс отрицательный
     */
    public void addNormalIndex(int normalIndex) {
        validateIndex(normalIndex, "нормали");
        normalIndices.add(normalIndex);
    }

    /**
     * Обновить индекс вершины на заданной позиции.
     * @param position позиция в полигоне
     * @param newIndex новый индекс вершины
     * @throws IndexOutOfBoundsException если позиция вне диапазона
     * @throws IllegalArgumentException если индекс отрицательный
     */
    public void updateVertexIndex(int position, int newIndex) {
        if (position < 0 || position >= vertexIndices.size()) {
            throw new IndexOutOfBoundsException(
                    String.format("Позиция %d вне диапазона [0, %d)", position, vertexIndices.size()));
        }
        validateIndex(newIndex, "вершины");
        vertexIndices.set(position, newIndex);
    }

    /**
     * Проверить, содержит ли полигон указанный индекс вершины.
     * @param vertexIndex индекс вершины для проверки
     * @return true если полигон содержит указанный индекс
     */
    public boolean containsVertexIndex(int vertexIndex) {
        return vertexIndices.contains(vertexIndex);
    }

    /**
     * Удалить все вхождения указанного индекса вершины.
     * @param vertexIndex индекс для удаления
     * @return true если был удалён хотя бы один индекс
     */
    public boolean removeVertexIndex(Integer vertexIndex) {
        return vertexIndices.removeIf(index -> index.equals(vertexIndex));
    }

    /**
     * Сместить все индексы вершин на указанное значение.
     * Используется при перестроении модели после удаления вершин.
     * @param vertexOffset смещение для индексов вершин
     * @param textureOffset смещение для индексов текстур
     * @param normalOffset смещение для индексов нормалей
     */
    public void offsetIndices(int vertexOffset, int textureOffset, int normalOffset) {
        if (vertexOffset != 0) {
            for (int i = 0; i < vertexIndices.size(); i++) {
                vertexIndices.set(i, vertexIndices.get(i) + vertexOffset);
            }
        }
        if (textureOffset != 0) {
            for (int i = 0; i < textureVertexIndices.size(); i++) {
                textureVertexIndices.set(i, textureVertexIndices.get(i) + textureOffset);
            }
        }
        if (normalOffset != 0) {
            for (int i = 0; i < normalIndices.size(); i++) {
                normalIndices.set(i, normalIndices.get(i) + normalOffset);
            }
        }
    }

    /**
     * Обновить индексы вершин согласно карте переназначения.
     * @param indexMapping карта старый_индекс -> новый_индекс
     */
    public void remapVertexIndices(java.util.Map<Integer, Integer> indexMapping) {
        Objects.requireNonNull(indexMapping, "Карта переназначения не может быть null");
        for (int i = 0; i < vertexIndices.size(); i++) {
            Integer oldIndex = vertexIndices.get(i);
            Integer newIndex = indexMapping.get(oldIndex);
            if (newIndex != null) {
                vertexIndices.set(i, newIndex);
            }
        }
    }

    /**
     * Очистить полигон полностью.
     */
    public void clear() {
        vertexIndices.clear();
        textureVertexIndices.clear();
        normalIndices.clear();
    }

    // ==================== Приватные методы валидации ====================

    /**
     * Валидация списка индексов.
     */
    private void validateIndices(List<Integer> indices, String type) {
        for (int i = 0; i < indices.size(); i++) {
            Integer index = indices.get(i);
            if (index == null) {
                throw new IllegalArgumentException(
                        String.format("Индекс %s на позиции %d не может быть null", type, i));
            }
            if (index < 0) {
                throw new IllegalArgumentException(
                        String.format("Индекс %s на позиции %d не может быть отрицательным: %d", type, i, index));
            }
        }
    }

    /**
     * Валидация одного индекса.
     */
    private void validateIndex(int index, String type) {
        if (index < 0) {
            throw new IllegalArgumentException(
                    String.format("Индекс %s не может быть отрицательным: %d", type, index));
        }
    }

    // ==================== Object methods ====================

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Polygon polygon = (Polygon) obj;
        return Objects.equals(vertexIndices, polygon.vertexIndices) &&
                Objects.equals(textureVertexIndices, polygon.textureVertexIndices) &&
                Objects.equals(normalIndices, polygon.normalIndices);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vertexIndices, textureVertexIndices, normalIndices);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Polygon{");
        sb.append("vertices=").append(vertexIndices);
        if (!textureVertexIndices.isEmpty()) {
            sb.append(", textures=").append(textureVertexIndices);
        }
        if (!normalIndices.isEmpty()) {
            sb.append(", normals=").append(normalIndices);
        }
        sb.append('}');
        return sb.toString();
    }

    // ==================== Статические фабричные методы ====================

    /**
     * Создать треугольный полигон.
     * @param v0 индекс первой вершины
     * @param v1 индекс второй вершины
     * @param v2 индекс третьей вершины
     * @return новый полигон-треугольник
     */
    public static Polygon createTriangle(int v0, int v1, int v2) {
        List<Integer> indices = new ArrayList<>(3);
        indices.add(v0);
        indices.add(v1);
        indices.add(v2);
        return new Polygon(indices);
    }

    /**
     * Создать четырёхугольный полигон.
     * @param v0 индекс первой вершины
     * @param v1 индекс второй вершины
     * @param v2 индекс третьей вершины
     * @param v3 индекс четвёртой вершины
     * @return новый полигон-четырёхугольник
     */
    public static Polygon createQuad(int v0, int v1, int v2, int v3) {
        List<Integer> indices = new ArrayList<>(4);
        indices.add(v0);
        indices.add(v1);
        indices.add(v2);
        indices.add(v3);
        return new Polygon(indices);
    }
}
