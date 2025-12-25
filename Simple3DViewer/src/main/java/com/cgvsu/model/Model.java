package com.cgvsu.model;

import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3f;

import java.util.*;

/**
 * Класс Model представляет 3D-модель, состоящую из вершин, текстурных координат,
 * нормалей и полигонов. Поддерживает операции удаления элементов.
 */
public class Model {

    public ArrayList<Vector3f> vertices = new ArrayList<>();
    public ArrayList<Vector2f> textureVertices = new ArrayList<>();
    public ArrayList<Vector3f> normals = new ArrayList<>();
    public ArrayList<Polygon> polygons = new ArrayList<>();

    /**
     * Удалить вершины по списку индексов.
     * Автоматически обновляет индексы в полигонах и удаляет полигоны,
     * которые ссылаются на удалённые вершины.
     * @param indicesToRemove список индексов вершин для удаления
     * @return количество удалённых вершин
     */
    public int removeVerticesByIndices(List<Integer> indicesToRemove) {
        if (indicesToRemove == null || indicesToRemove.isEmpty()) {
            return 0;
        }

        // Создаём отсортированный набор уникальных индексов
        Set<Integer> uniqueIndices = new TreeSet<>(Collections.reverseOrder());
        for (Integer index : indicesToRemove) {
            if (index >= 0 && index < vertices.size()) {
                uniqueIndices.add(index);
            }
        }

        if (uniqueIndices.isEmpty()) {
            return 0;
        }

        // Сначала удаляем полигоны, содержащие удаляемые вершины
        removePolygonsWithVertices(uniqueIndices);

        // Создаём карту переназначения индексов
        Map<Integer, Integer> indexMapping = createIndexMapping(vertices.size(), uniqueIndices);

        // Обновляем индексы в оставшихся полигонах
        updatePolygonVertexIndices(indexMapping);

        // Удаляем вершины (с конца, чтобы не сбивать индексы)
        int removedCount = 0;
        for (Integer index : uniqueIndices) {
            if (index < vertices.size()) {
                vertices.remove((int) index);
                removedCount++;
            }
        }

        return removedCount;
    }

    /**
     * Удалить одну вершину по индексу.
     * @param index индекс вершины для удаления
     * @return true если вершина была удалена
     */
    public boolean removeVertex(int index) {
        return removeVerticesByIndices(Collections.singletonList(index)) > 0;
    }

    /**
     * Удалить полигоны по списку индексов.
     * @param indicesToRemove список индексов полигонов для удаления
     * @return количество удалённых полигонов
     */
    public int removePolygonsByIndices(List<Integer> indicesToRemove) {
        if (indicesToRemove == null || indicesToRemove.isEmpty()) {
            return 0;
        }

        // Сортируем индексы в обратном порядке для корректного удаления
        Set<Integer> uniqueIndices = new TreeSet<>(Collections.reverseOrder());
        for (Integer index : indicesToRemove) {
            if (index >= 0 && index < polygons.size()) {
                uniqueIndices.add(index);
            }
        }

        int removedCount = 0;
        for (Integer index : uniqueIndices) {
            if (index < polygons.size()) {
                polygons.remove((int) index);
                removedCount++;
            }
        }

        return removedCount;
    }

    /**
     * Удалить один полигон по индексу.
     * @param index индекс полигона для удаления
     * @return true если полигон был удалён
     */
    public boolean removePolygon(int index) {
        return removePolygonsByIndices(Collections.singletonList(index)) > 0;
    }

    /**
     * Удалить текстурные координаты по списку индексов.
     * ЗАГЛУШКА: базовая реализация, коллеги могут дополнить логику обновления полигонов.
     * @param indicesToRemove список индексов для удаления
     * @return количество удалённых элементов
     */
    public int removeTextureVerticesByIndices(List<Integer> indicesToRemove) {
        // TODO: Коллеги должны реализовать обновление индексов в полигонах
        return removeFromList(textureVertices, indicesToRemove);
    }

    /**
     * Удалить нормали по списку индексов.
     * ЗАГЛУШКА: базовая реализация, коллеги могут дополнить логику обновления полигонов.
     * @param indicesToRemove список индексов для удаления
     * @return количество удалённых элементов
     */
    public int removeNormalsByIndices(List<Integer> indicesToRemove) {
        // TODO: Коллеги должны реализовать обновление индексов в полигонах
        return removeFromList(normals, indicesToRemove);
    }

    /**
     * Удалить полигоны, содержащие указанные вершины.
     * @param vertexIndices множество индексов вершин
     */
    private void removePolygonsWithVertices(Set<Integer> vertexIndices) {
        Iterator<Polygon> iterator = polygons.iterator();
        while (iterator.hasNext()) {
            Polygon polygon = iterator.next();
            for (Integer vertexIndex : polygon.getVertexIndices()) {
                if (vertexIndices.contains(vertexIndex)) {
                    iterator.remove();
                    break;
                }
            }
        }
    }

    /**
     * Создать карту переназначения индексов после удаления элементов.
     * @param originalSize исходный размер списка
     * @param removedIndices удалённые индексы
     * @return карта старый_индекс -> новый_индекс
     */
    private Map<Integer, Integer> createIndexMapping(int originalSize, Set<Integer> removedIndices) {
        Map<Integer, Integer> mapping = new HashMap<>();
        int newIndex = 0;

        for (int oldIndex = 0; oldIndex < originalSize; oldIndex++) {
            if (!removedIndices.contains(oldIndex)) {
                mapping.put(oldIndex, newIndex);
                newIndex++;
            }
        }

        return mapping;
    }

    /**
     * Обновить индексы вершин во всех полигонах согласно карте переназначения.
     * @param indexMapping карта старый_индекс -> новый_индекс
     */
    private void updatePolygonVertexIndices(Map<Integer, Integer> indexMapping) {
        for (Polygon polygon : polygons) {
            ArrayList<Integer> oldIndices = polygon.getVertexIndices();
            ArrayList<Integer> newIndices = new ArrayList<>();

            for (Integer oldIndex : oldIndices) {
                Integer newIndex = indexMapping.get(oldIndex);
                if (newIndex != null) {
                    newIndices.add(newIndex);
                }
            }

            polygon.setVertexIndices(newIndices);
        }
    }

    /**
     * Универсальный метод удаления элементов из списка по индексам.
     * @param list список для удаления
     * @param indicesToRemove индексы для удаления
     * @param <T> тип элементов
     * @return количество удалённых элементов
     */
    private <T> int removeFromList(List<T> list, List<Integer> indicesToRemove) {
        if (indicesToRemove == null || indicesToRemove.isEmpty()) {
            return 0;
        }

        Set<Integer> uniqueIndices = new TreeSet<>(Collections.reverseOrder());
        for (Integer index : indicesToRemove) {
            if (index >= 0 && index < list.size()) {
                uniqueIndices.add(index);
            }
        }

        int removedCount = 0;
        for (Integer index : uniqueIndices) {
            if (index < list.size()) {
                list.remove((int) index);
                removedCount++;
            }
        }

        return removedCount;
    }

    /**
     * Получить количество вершин модели.
     * @return количество вершин
     */
    public int getVertexCount() {
        return vertices.size();
    }

    /**
     * Получить количество полигонов модели.
     * @return количество полигонов
     */
    public int getPolygonCount() {
        return polygons.size();
    }

    /**
     * Проверить, пуста ли модель.
     * @return true если модель не содержит вершин
     */
    public boolean isEmpty() {
        return vertices.isEmpty();
    }

    /**
     * Очистить модель полностью.
     */
    public void clear() {
        vertices.clear();
        textureVertices.clear();
        normals.clear();
        polygons.clear();
    }
}
