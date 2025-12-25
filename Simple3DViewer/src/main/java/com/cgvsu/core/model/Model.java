package com.cgvsu.core.model;

import com.cgvsu.core.math.Vector2f;
import com.cgvsu.core.math.Vector3f;
import com.cgvsu.exception.ModelException;

import java.util.*;

/**
 * Класс Model представляет 3D-модель, состоящую из вершин, текстурных координат,
 * нормалей и полигонов.
 *
 * <p>Класс обеспечивает полную инкапсуляцию данных и предоставляет безопасные методы
 * для манипуляции с геометрией модели. Поддерживает операции добавления, удаления
 * и модификации элементов модели.</p>
 *
 * <p>Пример использования:</p>
 * <pre>
 * Model model = new Model("MyModel");
 * model.addVertex(new Vector3f(0, 0, 0));
 * model.addVertex(new Vector3f(1, 0, 0));
 * model.addVertex(new Vector3f(0, 1, 0));
 * model.addPolygon(Polygon.createTriangle(0, 1, 2));
 * </pre>
 */
public class Model {

    // ==================== Поля ====================

    /** Уникальный идентификатор модели */
    private final String id;

    /** Имя модели для отображения */
    private String name;

    /** Список вершин модели */
    private final List<Vector3f> vertices;

    /** Список текстурных координат */
    private final List<Vector2f> textureVertices;

    /** Список нормалей */
    private final List<Vector3f> normals;

    /** Список полигонов */
    private final List<Polygon> polygons;

    /** Флаг модификации модели */
    private boolean modified;

    // ==================== Конструкторы ====================

    /**
     * Создаёт пустую модель с автоматически сгенерированным ID.
     */
    public Model() {
        this(UUID.randomUUID().toString(), "Unnamed");
    }

    /**
     * Создаёт пустую модель с указанным именем.
     * @param name имя модели
     */
    public Model(String name) {
        this(UUID.randomUUID().toString(), name);
    }

    /**
     * Создаёт пустую модель с указанным ID и именем.
     * @param id уникальный идентификатор
     * @param name имя модели
     */
    public Model(String id, String name) {
        this.id = Objects.requireNonNull(id, "ID модели не может быть null");
        this.name = Objects.requireNonNullElse(name, "Unnamed");
        this.vertices = new ArrayList<>();
        this.textureVertices = new ArrayList<>();
        this.normals = new ArrayList<>();
        this.polygons = new ArrayList<>();
        this.modified = false;
    }

    /**
     * Копирующий конструктор. Создаёт глубокую копию модели.
     * @param other модель для копирования
     */
    public Model(Model other) {
        Objects.requireNonNull(other, "Модель для копирования не может быть null");
        this.id = UUID.randomUUID().toString(); // Новый ID для копии
        this.name = other.name + "_copy";
        this.vertices = new ArrayList<>();
        this.textureVertices = new ArrayList<>();
        this.normals = new ArrayList<>();
        this.polygons = new ArrayList<>();

        // Глубокое копирование вершин
        for (Vector3f vertex : other.vertices) {
            this.vertices.add(new Vector3f(vertex));
        }

        // Глубокое копирование текстурных координат
        for (Vector2f texCoord : other.textureVertices) {
            this.textureVertices.add(new Vector2f(texCoord));
        }

        // Глубокое копирование нормалей
        for (Vector3f normal : other.normals) {
            this.normals.add(new Vector3f(normal));
        }

        // Глубокое копирование полигонов
        for (Polygon polygon : other.polygons) {
            this.polygons.add(new Polygon(polygon));
        }

        this.modified = false;
    }

    // ==================== Getters и Setters ====================

    /**
     * Получить уникальный идентификатор модели.
     * @return ID модели
     */
    public String getId() {
        return id;
    }

    /**
     * Получить имя модели.
     * @return имя модели
     */
    public String getName() {
        return name;
    }

    /**
     * Установить имя модели.
     * @param name новое имя модели
     */
    public void setName(String name) {
        this.name = Objects.requireNonNullElse(name, "Unnamed");
        markModified();
    }

    /**
     * Проверить, была ли модель модифицирована.
     * @return true если модель была изменена
     */
    public boolean isModified() {
        return modified;
    }

    /**
     * Сбросить флаг модификации.
     */
    public void clearModified() {
        this.modified = false;
    }

    /**
     * Пометить модель как модифицированную.
     */
    protected void markModified() {
        this.modified = true;
    }

    // ==================== Доступ к данным (неизменяемые представления) ====================

    /**
     * Получить неизменяемый список вершин.
     * @return неизменяемый список вершин
     */
    public List<Vector3f> getVertices() {
        return Collections.unmodifiableList(vertices);
    }

    /**
     * Получить неизменяемый список текстурных координат.
     * @return неизменяемый список текстурных координат
     */
    public List<Vector2f> getTextureVertices() {
        return Collections.unmodifiableList(textureVertices);
    }

    /**
     * Получить неизменяемый список нормалей.
     * @return неизменяемый список нормалей
     */
    public List<Vector3f> getNormals() {
        return Collections.unmodifiableList(normals);
    }

    /**
     * Получить неизменяемый список полигонов.
     * @return неизменяемый список полигонов
     */
    public List<Polygon> getPolygons() {
        return Collections.unmodifiableList(polygons);
    }

    // ==================== Доступ к данным (изменяемые - для внутреннего использования) ====================

    /**
     * Получить изменяемый список вершин.
     * <p>Внимание: используйте только для операций, требующих прямого доступа.</p>
     * @return изменяемый список вершин
     */
    public List<Vector3f> getVerticesMutable() {
        markModified();
        return vertices;
    }

    /**
     * Получить изменяемый список текстурных координат.
     * @return изменяемый список текстурных координат
     */
    public List<Vector2f> getTextureVerticesMutable() {
        markModified();
        return textureVertices;
    }

    /**
     * Получить изменяемый список нормалей.
     * @return изменяемый список нормалей
     */
    public List<Vector3f> getNormalsMutable() {
        markModified();
        return normals;
    }

    /**
     * Получить изменяемый список полигонов.
     * @return изменяемый список полигонов
     */
    public List<Polygon> getPolygonsMutable() {
        markModified();
        return polygons;
    }

    // ==================== Получение элементов по индексу ====================

    /**
     * Получить вершину по индексу.
     * @param index индекс вершины
     * @return вершина
     * @throws IndexOutOfBoundsException если индекс вне диапазона
     */
    public Vector3f getVertex(int index) {
        validateVertexIndex(index);
        return vertices.get(index);
    }

    /**
     * Получить текстурную координату по индексу.
     * @param index индекс текстурной координаты
     * @return текстурная координата
     * @throws IndexOutOfBoundsException если индекс вне диапазона
     */
    public Vector2f getTextureVertex(int index) {
        if (index < 0 || index >= textureVertices.size()) {
            throw new IndexOutOfBoundsException(
                    String.format("Индекс текстурной координаты %d вне диапазона [0, %d)",
                            index, textureVertices.size()));
        }
        return textureVertices.get(index);
    }

    /**
     * Получить нормаль по индексу.
     * @param index индекс нормали
     * @return нормаль
     * @throws IndexOutOfBoundsException если индекс вне диапазона
     */
    public Vector3f getNormal(int index) {
        if (index < 0 || index >= normals.size()) {
            throw new IndexOutOfBoundsException(
                    String.format("Индекс нормали %d вне диапазона [0, %d)",
                            index, normals.size()));
        }
        return normals.get(index);
    }

    /**
     * Получить полигон по индексу.
     * @param index индекс полигона
     * @return полигон
     * @throws IndexOutOfBoundsException если индекс вне диапазона
     */
    public Polygon getPolygon(int index) {
        validatePolygonIndex(index);
        return polygons.get(index);
    }

    // ==================== Счётчики ====================

    /**
     * Получить количество вершин.
     * @return количество вершин
     */
    public int getVertexCount() {
        return vertices.size();
    }

    /**
     * Получить количество текстурных координат.
     * @return количество текстурных координат
     */
    public int getTextureVertexCount() {
        return textureVertices.size();
    }

    /**
     * Получить количество нормалей.
     * @return количество нормалей
     */
    public int getNormalCount() {
        return normals.size();
    }

    /**
     * Получить количество полигонов.
     * @return количество полигонов
     */
    public int getPolygonCount() {
        return polygons.size();
    }

    // ==================== Проверки состояния ====================

    /**
     * Проверить, пуста ли модель.
     * @return true если модель не содержит вершин
     */
    public boolean isEmpty() {
        return vertices.isEmpty();
    }

    /**
     * Проверить, содержит ли модель текстурные координаты.
     * @return true если есть текстурные координаты
     */
    public boolean hasTextureCoordinates() {
        return !textureVertices.isEmpty();
    }

    /**
     * Проверить, содержит ли модель нормали.
     * @return true если есть нормали
     */
    public boolean hasNormals() {
        return !normals.isEmpty();
    }

    /**
     * Проверить, содержит ли модель полигоны.
     * @return true если есть полигоны
     */
    public boolean hasPolygons() {
        return !polygons.isEmpty();
    }

    // ==================== Операции добавления ====================

    /**
     * Добавить вершину в модель.
     * @param vertex вершина для добавления
     * @return индекс добавленной вершины
     */
    public int addVertex(Vector3f vertex) {
        Objects.requireNonNull(vertex, "Вершина не может быть null");
        vertices.add(vertex);
        markModified();
        return vertices.size() - 1;
    }

    /**
     * Добавить вершину по координатам.
     * @param x координата X
     * @param y координата Y
     * @param z координата Z
     * @return индекс добавленной вершины
     */
    public int addVertex(float x, float y, float z) {
        return addVertex(new Vector3f(x, y, z));
    }

    /**
     * Добавить текстурную координату.
     * @param textureVertex текстурная координата
     * @return индекс добавленной текстурной координаты
     */
    public int addTextureVertex(Vector2f textureVertex) {
        Objects.requireNonNull(textureVertex, "Текстурная координата не может быть null");
        textureVertices.add(textureVertex);
        markModified();
        return textureVertices.size() - 1;
    }

    /**
     * Добавить текстурную координату по компонентам.
     * @param u координата U
     * @param v координата V
     * @return индекс добавленной текстурной координаты
     */
    public int addTextureVertex(float u, float v) {
        return addTextureVertex(new Vector2f(u, v));
    }

    /**
     * Добавить нормаль.
     * @param normal нормаль
     * @return индекс добавленной нормали
     */
    public int addNormal(Vector3f normal) {
        Objects.requireNonNull(normal, "Нормаль не может быть null");
        normals.add(normal);
        markModified();
        return normals.size() - 1;
    }

    /**
     * Добавить нормаль по компонентам.
     * @param x компонент X
     * @param y компонент Y
     * @param z компонент Z
     * @return индекс добавленной нормали
     */
    public int addNormal(float x, float y, float z) {
        return addNormal(new Vector3f(x, y, z));
    }

    /**
     * Добавить полигон.
     * @param polygon полигон для добавления
     * @return индекс добавленного полигона
     */
    public int addPolygon(Polygon polygon) {
        Objects.requireNonNull(polygon, "Полигон не может быть null");
        polygons.add(polygon);
        markModified();
        return polygons.size() - 1;
    }

    /**
     * Добавить треугольный полигон.
     * @param v0 индекс первой вершины
     * @param v1 индекс второй вершины
     * @param v2 индекс третьей вершины
     * @return индекс добавленного полигона
     */
    public int addTriangle(int v0, int v1, int v2) {
        return addPolygon(Polygon.createTriangle(v0, v1, v2));
    }

    // ==================== Операции удаления ====================

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
            if (index != null && index >= 0 && index < vertices.size()) {
                uniqueIndices.add(index);
            }
        }

        if (uniqueIndices.isEmpty()) {
            return 0;
        }

        // Удаляем полигоны, содержащие удаляемые вершины
        removePolygonsWithVertices(uniqueIndices);

        // Создаём карту переназначения индексов
        Map<Integer, Integer> indexMapping = createIndexMapping(vertices.size(), uniqueIndices);

        // Обновляем индексы в оставшихся полигонах
        updatePolygonIndices(indexMapping);

        // Удаляем вершины (с конца, чтобы не сбивать индексы)
        int removedCount = 0;
        for (Integer index : uniqueIndices) {
            if (index < vertices.size()) {
                vertices.remove((int) index);
                removedCount++;
            }
        }

        markModified();
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
            if (index != null && index >= 0 && index < polygons.size()) {
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

        if (removedCount > 0) {
            markModified();
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
     * Удалить текстурные координаты по индексам.
     * @param indicesToRemove список индексов для удаления
     * @return количество удалённых элементов
     */
    public int removeTextureVerticesByIndices(List<Integer> indicesToRemove) {
        int removed = removeFromList(textureVertices, indicesToRemove);
        if (removed > 0) {
            markModified();
        }
        return removed;
    }

    /**
     * Удалить нормали по индексам.
     * @param indicesToRemove список индексов для удаления
     * @return количество удалённых элементов
     */
    public int removeNormalsByIndices(List<Integer> indicesToRemove) {
        int removed = removeFromList(normals, indicesToRemove);
        if (removed > 0) {
            markModified();
        }
        return removed;
    }

    // ==================== Вспомогательные методы для удаления ====================

    /**
     * Удаляет полигоны, содержащие указанные вершины.
     */
    private void removePolygonsWithVertices(Set<Integer> vertexIndices) {
        polygons.removeIf(polygon -> {
            for (Integer vertexIndex : polygon.getVertexIndices()) {
                if (vertexIndices.contains(vertexIndex)) {
                    return true;
                }
            }
            return false;
        });
    }

    /**
     * Создаёт карту переназначения индексов после удаления элементов.
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
     * Обновляет индексы вершин во всех полигонах.
     */
    private void updatePolygonIndices(Map<Integer, Integer> indexMapping) {
        for (Polygon polygon : polygons) {
            polygon.remapVertexIndices(indexMapping);
        }
    }

    /**
     * Универсальный метод удаления элементов из списка по индексам.
     */
    private <T> int removeFromList(List<T> list, List<Integer> indicesToRemove) {
        if (indicesToRemove == null || indicesToRemove.isEmpty()) {
            return 0;
        }

        Set<Integer> uniqueIndices = new TreeSet<>(Collections.reverseOrder());
        for (Integer index : indicesToRemove) {
            if (index != null && index >= 0 && index < list.size()) {
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

    // ==================== Валидация ====================

    /**
     * Проверить корректность индекса вершины.
     */
    private void validateVertexIndex(int index) {
        if (index < 0 || index >= vertices.size()) {
            throw new IndexOutOfBoundsException(
                    String.format("Индекс вершины %d вне диапазона [0, %d)", index, vertices.size()));
        }
    }

    /**
     * Проверить корректность индекса полигона.
     */
    private void validatePolygonIndex(int index) {
        if (index < 0 || index >= polygons.size()) {
            throw new IndexOutOfBoundsException(
                    String.format("Индекс полигона %d вне диапазона [0, %d)", index, polygons.size()));
        }
    }

    /**
     * Валидация целостности модели.
     * @return список ошибок (пустой если модель валидна)
     */
    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        // Проверка полигонов
        for (int i = 0; i < polygons.size(); i++) {
            Polygon polygon = polygons.get(i);

            // Проверка индексов вершин
            for (Integer vertexIndex : polygon.getVertexIndices()) {
                if (vertexIndex < 0 || vertexIndex >= vertices.size()) {
                    errors.add(String.format("Полигон %d: индекс вершины %d вне диапазона [0, %d)",
                            i, vertexIndex, vertices.size()));
                }
            }

            // Проверка индексов текстурных координат
            if (polygon.hasTextureCoordinates()) {
                for (Integer texIndex : polygon.getTextureVertexIndices()) {
                    if (texIndex < 0 || texIndex >= textureVertices.size()) {
                        errors.add(String.format("Полигон %d: индекс текстурной координаты %d вне диапазона [0, %d)",
                                i, texIndex, textureVertices.size()));
                    }
                }
            }

            // Проверка индексов нормалей
            if (polygon.hasNormals()) {
                for (Integer normalIndex : polygon.getNormalIndices()) {
                    if (normalIndex < 0 || normalIndex >= normals.size()) {
                        errors.add(String.format("Полигон %d: индекс нормали %d вне диапазона [0, %d)",
                                i, normalIndex, normals.size()));
                    }
                }
            }
        }

        return errors;
    }

    /**
     * Проверить, валидна ли модель.
     * @return true если модель валидна
     */
    public boolean isValid() {
        return validate().isEmpty();
    }

    // ==================== Утилитные методы ====================

    /**
     * Очистить модель полностью.
     */
    public void clear() {
        vertices.clear();
        textureVertices.clear();
        normals.clear();
        polygons.clear();
        markModified();
    }

    /**
     * Создать копию модели.
     * @return глубокая копия модели
     */
    public Model copy() {
        return new Model(this);
    }

    /**
     * Вычислить ограничивающий параллелепипед (AABB) модели.
     * @return массив [minX, minY, minZ, maxX, maxY, maxZ] или null если модель пуста
     */
    public float[] computeBoundingBox() {
        if (vertices.isEmpty()) {
            return null;
        }

        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, minZ = Float.MAX_VALUE;
        float maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE, maxZ = -Float.MAX_VALUE;

        for (Vector3f v : vertices) {
            minX = Math.min(minX, v.getX());
            minY = Math.min(minY, v.getY());
            minZ = Math.min(minZ, v.getZ());
            maxX = Math.max(maxX, v.getX());
            maxY = Math.max(maxY, v.getY());
            maxZ = Math.max(maxZ, v.getZ());
        }

        return new float[]{minX, minY, minZ, maxX, maxY, maxZ};
    }

    /**
     * Вычислить центр масс модели.
     * @return центр масс или null если модель пуста
     */
    public Vector3f computeCentroid() {
        if (vertices.isEmpty()) {
            return null;
        }

        float sumX = 0, sumY = 0, sumZ = 0;
        for (Vector3f v : vertices) {
            sumX += v.getX();
            sumY += v.getY();
            sumZ += v.getZ();
        }

        int count = vertices.size();
        return new Vector3f(sumX / count, sumY / count, sumZ / count);
    }

    /**
     * Получить статистику модели.
     * @return карта с статистическими данными
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("id", id);
        stats.put("name", name);
        stats.put("vertexCount", vertices.size());
        stats.put("textureVertexCount", textureVertices.size());
        stats.put("normalCount", normals.size());
        stats.put("polygonCount", polygons.size());
        stats.put("hasTextures", hasTextureCoordinates());
        stats.put("hasNormals", hasNormals());
        stats.put("isValid", isValid());
        stats.put("modified", modified);

        // Подсчёт треугольников и квадов
        int triangles = 0, quads = 0, other = 0;
        for (Polygon p : polygons) {
            if (p.isTriangle()) triangles++;
            else if (p.isQuad()) quads++;
            else other++;
        }
        stats.put("triangleCount", triangles);
        stats.put("quadCount", quads);
        stats.put("otherPolygonCount", other);

        return stats;
    }

    // ==================== Object methods ====================

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Model model = (Model) obj;
        return Objects.equals(id, model.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Model{id='%s', name='%s', vertices=%d, polygons=%d}",
                id, name, vertices.size(), polygons.size());
    }

    /**
     * Получить подробную информацию о модели.
     * @return многострочная строка с информацией
     */
    public String toDetailedString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Model: ").append(name).append("\n");
        sb.append("  ID: ").append(id).append("\n");
        sb.append("  Vertices: ").append(vertices.size()).append("\n");
        sb.append("  Texture coords: ").append(textureVertices.size()).append("\n");
        sb.append("  Normals: ").append(normals.size()).append("\n");
        sb.append("  Polygons: ").append(polygons.size()).append("\n");
        sb.append("  Valid: ").append(isValid()).append("\n");
        sb.append("  Modified: ").append(modified);
        return sb.toString();
    }
}
