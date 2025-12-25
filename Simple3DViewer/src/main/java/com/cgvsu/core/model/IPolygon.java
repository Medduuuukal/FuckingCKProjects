package com.cgvsu.core.model;

import java.util.List;

/**
 * Интерфейс для полигона 3D-модели.
 * Определяет контракт для работы с полигонами независимо от их реализации.
 * Следует принципу Interface Segregation - разделяет операции чтения и модификации.
 */
public interface IPolygon {

    // ==================== Операции чтения ====================

    /**
     * Получить список индексов вершин полигона.
     * @return неизменяемый список индексов вершин
     */
    List<Integer> getVertexIndices();

    /**
     * Получить список индексов текстурных координат полигона.
     * @return неизменяемый список индексов текстурных координат
     */
    List<Integer> getTextureVertexIndices();

    /**
     * Получить список индексов нормалей полигона.
     * @return неизменяемый список индексов нормалей
     */
    List<Integer> getNormalIndices();

    /**
     * Получить количество вершин в полигоне.
     * @return количество вершин
     */
    int getVertexCount();

    /**
     * Получить индекс вершины по позиции в полигоне.
     * @param position позиция в полигоне (0-based)
     * @return индекс вершины
     * @throws IndexOutOfBoundsException если позиция вне диапазона
     */
    int getVertexIndex(int position);

    /**
     * Проверить, содержит ли полигон текстурные координаты.
     * @return true если есть текстурные координаты
     */
    boolean hasTextureCoordinates();

    /**
     * Проверить, содержит ли полигон нормали.
     * @return true если есть нормали
     */
    boolean hasNormals();

    /**
     * Проверить, является ли полигон треугольником.
     * @return true если полигон имеет ровно 3 вершины
     */
    default boolean isTriangle() {
        return getVertexCount() == 3;
    }

    /**
     * Проверить, является ли полигон четырёхугольником.
     * @return true если полигон имеет ровно 4 вершины
     */
    default boolean isQuad() {
        return getVertexCount() == 4;
    }

    /**
     * Проверить валидность полигона.
     * @return true если полигон валиден (минимум 3 вершины)
     */
    default boolean isValid() {
        return getVertexCount() >= 3;
    }

    // ==================== Операции модификации ====================

    /**
     * Установить индексы вершин полигона.
     * @param vertexIndices список индексов вершин (минимум 3)
     * @throws IllegalArgumentException если количество вершин меньше 3
     */
    void setVertexIndices(List<Integer> vertexIndices);

    /**
     * Установить индексы текстурных координат полигона.
     * @param textureVertexIndices список индексов текстурных координат
     */
    void setTextureVertexIndices(List<Integer> textureVertexIndices);

    /**
     * Установить индексы нормалей полигона.
     * @param normalIndices список индексов нормалей
     */
    void setNormalIndices(List<Integer> normalIndices);

    /**
     * Очистить текстурные координаты.
     */
    void clearTextureCoordinates();

    /**
     * Очистить нормали.
     */
    void clearNormals();

    /**
     * Создать копию полигона.
     * @return новый полигон с теми же данными
     */
    IPolygon copy();
}
