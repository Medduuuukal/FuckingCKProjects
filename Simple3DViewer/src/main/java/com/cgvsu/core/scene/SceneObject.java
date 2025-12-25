package com.cgvsu.core.scene;

import com.cgvsu.core.math.Vector3f;
import com.cgvsu.core.model.Model;

import java.util.Objects;
import java.util.UUID;

/**
 * Обёртка для модели на сцене.
 * Содержит модель и её трансформации (позиция, поворот, масштаб),
 * а также флаги видимости и выбора.
 *
 * <p>SceneObject позволяет размещать одну и ту же модель несколько раз
 * на сцене с разными трансформациями.</p>
 */
public class SceneObject {

    // ==================== Поля ====================

    /** Уникальный идентификатор объекта на сцене */
    private final String id;

    /** Имя объекта для отображения в UI */
    private String name;

    /** Ссылка на модель */
    private Model model;

    /** Позиция объекта в мировых координатах */
    private final Vector3f position;

    /** Поворот объекта (углы Эйлера в радианах: pitch, yaw, roll) */
    private final Vector3f rotation;

    /** Масштаб объекта по осям */
    private final Vector3f scale;

    /** Флаг видимости объекта */
    private boolean visible;

    /** Флаг выбора (активности) объекта */
    private boolean selected;

    /** Флаг блокировки объекта от редактирования */
    private boolean locked;

    // ==================== Конструкторы ====================

    /**
     * Создаёт объект сцены с моделью.
     * @param model модель
     */
    public SceneObject(Model model) {
        this(model, model != null ? model.getName() : "Unnamed");
    }

    /**
     * Создаёт объект сцены с моделью и именем.
     * @param model модель
     * @param name имя объекта
     */
    public SceneObject(Model model, String name) {
        this.id = UUID.randomUUID().toString();
        this.model = model;
        this.name = Objects.requireNonNullElse(name, "Unnamed");
        this.position = new Vector3f(0, 0, 0);
        this.rotation = new Vector3f(0, 0, 0);
        this.scale = new Vector3f(1, 1, 1);
        this.visible = true;
        this.selected = false;
        this.locked = false;
    }

    /**
     * Копирующий конструктор.
     * @param other объект для копирования
     * @param deepCopyModel если true, создаёт копию модели
     */
    public SceneObject(SceneObject other, boolean deepCopyModel) {
        Objects.requireNonNull(other, "SceneObject не может быть null");
        this.id = UUID.randomUUID().toString();
        this.name = other.name + "_copy";
        this.model = deepCopyModel && other.model != null ? other.model.copy() : other.model;
        this.position = new Vector3f(other.position);
        this.rotation = new Vector3f(other.rotation);
        this.scale = new Vector3f(other.scale);
        this.visible = other.visible;
        this.selected = false; // Копия не выбрана по умолчанию
        this.locked = false;
    }

    // ==================== Getters и Setters ====================

    /**
     * Получить уникальный идентификатор.
     * @return ID объекта
     */
    public String getId() {
        return id;
    }

    /**
     * Получить имя объекта.
     * @return имя
     */
    public String getName() {
        return name;
    }

    /**
     * Установить имя объекта.
     * @param name новое имя
     */
    public void setName(String name) {
        this.name = Objects.requireNonNullElse(name, "Unnamed");
    }

    /**
     * Получить модель.
     * @return модель или null
     */
    public Model getModel() {
        return model;
    }

    /**
     * Установить модель.
     * @param model новая модель
     */
    public void setModel(Model model) {
        this.model = model;
    }

    /**
     * Проверить, содержит ли объект модель.
     * @return true если модель установлена
     */
    public boolean hasModel() {
        return model != null;
    }

    // ==================== Трансформации - Позиция ====================

    /**
     * Получить позицию объекта.
     * @return вектор позиции
     */
    public Vector3f getPosition() {
        return position;
    }

    /**
     * Установить позицию объекта.
     * @param x координата X
     * @param y координата Y
     * @param z координата Z
     */
    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
    }

    /**
     * Установить позицию объекта.
     * @param position новая позиция
     */
    public void setPosition(Vector3f position) {
        Objects.requireNonNull(position, "Позиция не может быть null");
        this.position.set(position);
    }

    /**
     * Переместить объект на указанное смещение.
     * @param dx смещение по X
     * @param dy смещение по Y
     * @param dz смещение по Z
     */
    public void translate(float dx, float dy, float dz) {
        position.add(dx, dy, dz);
    }

    /**
     * Переместить объект на указанное смещение.
     * @param offset вектор смещения
     */
    public void translate(Vector3f offset) {
        Objects.requireNonNull(offset, "Смещение не может быть null");
        position.add(offset);
    }

    // ==================== Трансформации - Поворот ====================

    /**
     * Получить поворот объекта (углы Эйлера в радианах).
     * @return вектор поворота (pitch, yaw, roll)
     */
    public Vector3f getRotation() {
        return rotation;
    }

    /**
     * Установить поворот объекта.
     * @param pitch поворот вокруг оси X (в радианах)
     * @param yaw поворот вокруг оси Y (в радианах)
     * @param roll поворот вокруг оси Z (в радианах)
     */
    public void setRotation(float pitch, float yaw, float roll) {
        rotation.set(pitch, yaw, roll);
    }

    /**
     * Установить поворот объекта.
     * @param rotation вектор поворота
     */
    public void setRotation(Vector3f rotation) {
        Objects.requireNonNull(rotation, "Поворот не может быть null");
        this.rotation.set(rotation);
    }

    /**
     * Повернуть объект на указанные углы.
     * @param dPitch изменение pitch
     * @param dYaw изменение yaw
     * @param dRoll изменение roll
     */
    public void rotate(float dPitch, float dYaw, float dRoll) {
        rotation.add(dPitch, dYaw, dRoll);
    }

    /**
     * Установить поворот в градусах.
     * @param pitchDegrees поворот вокруг оси X (в градусах)
     * @param yawDegrees поворот вокруг оси Y (в градусах)
     * @param rollDegrees поворот вокруг оси Z (в градусах)
     */
    public void setRotationDegrees(float pitchDegrees, float yawDegrees, float rollDegrees) {
        rotation.set(
                (float) Math.toRadians(pitchDegrees),
                (float) Math.toRadians(yawDegrees),
                (float) Math.toRadians(rollDegrees)
        );
    }

    /**
     * Получить поворот в градусах.
     * @return вектор поворота в градусах
     */
    public Vector3f getRotationDegrees() {
        return new Vector3f(
                (float) Math.toDegrees(rotation.getX()),
                (float) Math.toDegrees(rotation.getY()),
                (float) Math.toDegrees(rotation.getZ())
        );
    }

    // ==================== Трансформации - Масштаб ====================

    /**
     * Получить масштаб объекта.
     * @return вектор масштаба
     */
    public Vector3f getScale() {
        return scale;
    }

    /**
     * Установить масштаб объекта.
     * @param x масштаб по X
     * @param y масштаб по Y
     * @param z масштаб по Z
     */
    public void setScale(float x, float y, float z) {
        scale.set(x, y, z);
    }

    /**
     * Установить равномерный масштаб.
     * @param uniformScale масштаб по всем осям
     */
    public void setScale(float uniformScale) {
        scale.set(uniformScale, uniformScale, uniformScale);
    }

    /**
     * Установить масштаб объекта.
     * @param scale вектор масштаба
     */
    public void setScale(Vector3f scale) {
        Objects.requireNonNull(scale, "Масштаб не может быть null");
        this.scale.set(scale);
    }

    /**
     * Умножить масштаб на коэффициент.
     * @param factor коэффициент масштабирования
     */
    public void scaleBy(float factor) {
        scale.multiply(factor);
    }

    // ==================== Флаги состояния ====================

    /**
     * Проверить, видим ли объект.
     * @return true если объект видим
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Установить видимость объекта.
     * @param visible видимость
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Переключить видимость.
     */
    public void toggleVisible() {
        this.visible = !this.visible;
    }

    /**
     * Проверить, выбран ли объект.
     * @return true если объект выбран
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Установить состояние выбора.
     * @param selected выбран ли объект
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * Переключить состояние выбора.
     */
    public void toggleSelected() {
        this.selected = !this.selected;
    }

    /**
     * Проверить, заблокирован ли объект.
     * @return true если объект заблокирован
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * Установить блокировку объекта.
     * @param locked заблокирован ли объект
     */
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    /**
     * Переключить блокировку.
     */
    public void toggleLocked() {
        this.locked = !this.locked;
    }

    // ==================== Утилитные методы ====================

    /**
     * Сбросить трансформации к значениям по умолчанию.
     */
    public void resetTransform() {
        position.set(0, 0, 0);
        rotation.set(0, 0, 0);
        scale.set(1, 1, 1);
    }

    /**
     * Сбросить только позицию.
     */
    public void resetPosition() {
        position.set(0, 0, 0);
    }

    /**
     * Сбросить только поворот.
     */
    public void resetRotation() {
        rotation.set(0, 0, 0);
    }

    /**
     * Сбросить только масштаб.
     */
    public void resetScale() {
        scale.set(1, 1, 1);
    }

    /**
     * Проверить, можно ли редактировать объект.
     * @return true если объект не заблокирован
     */
    public boolean isEditable() {
        return !locked;
    }

    /**
     * Проверить, должен ли объект рендериться.
     * @return true если объект видим и имеет модель
     */
    public boolean shouldRender() {
        return visible && model != null && !model.isEmpty();
    }

    /**
     * Создать копию объекта сцены.
     * @param deepCopyModel если true, создаёт копию модели
     * @return копия объекта
     */
    public SceneObject copy(boolean deepCopyModel) {
        return new SceneObject(this, deepCopyModel);
    }

    // ==================== Object methods ====================

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SceneObject that = (SceneObject) obj;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("SceneObject{id='%s', name='%s', visible=%s, selected=%s, model=%s}",
                id, name, visible, selected, model != null ? model.getName() : "null");
    }

    /**
     * Получить подробную информацию об объекте.
     * @return многострочная строка с информацией
     */
    public String toDetailedString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SceneObject: ").append(name).append("\n");
        sb.append("  ID: ").append(id).append("\n");
        sb.append("  Position: ").append(position).append("\n");
        sb.append("  Rotation: ").append(getRotationDegrees()).append(" (degrees)\n");
        sb.append("  Scale: ").append(scale).append("\n");
        sb.append("  Visible: ").append(visible).append("\n");
        sb.append("  Selected: ").append(selected).append("\n");
        sb.append("  Locked: ").append(locked).append("\n");
        sb.append("  Model: ").append(model != null ? model.getName() : "null");
        return sb.toString();
    }
}
