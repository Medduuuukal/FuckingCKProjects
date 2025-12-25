package com.cgvsu.core.scene;

import com.cgvsu.core.model.Model;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Класс Scene управляет коллекцией объектов сцены (SceneObject).
 * Поддерживает операции добавления, удаления, выбора и поиска объектов.
 *
 * <p>Сцена использует паттерн Observer для уведомления слушателей
 * об изменениях в коллекции объектов.</p>
 *
 * <p>Пример использования:</p>
 * <pre>
 * Scene scene = new Scene("MyScene");
 * scene.addChangeListener(event -> System.out.println("Scene changed: " + event));
 *
 * Model model = loadModel("cube.obj");
 * SceneObject obj = scene.addModel(model, "Cube");
 * scene.setSelected(obj, true);
 *
 * for (SceneObject selected : scene.getSelectedObjects()) {
 *     // Работа с выбранными объектами
 * }
 * </pre>
 */
public class Scene {

    // ==================== События сцены ====================

    /**
     * Тип события изменения сцены.
     */
    public enum ChangeEventType {
        OBJECT_ADDED,
        OBJECT_REMOVED,
        SELECTION_CHANGED,
        OBJECT_MODIFIED,
        SCENE_CLEARED
    }

    /**
     * Событие изменения сцены.
     */
    public static class ChangeEvent {
        private final ChangeEventType type;
        private final SceneObject object;
        private final String description;

        public ChangeEvent(ChangeEventType type, SceneObject object, String description) {
            this.type = type;
            this.object = object;
            this.description = description;
        }

        public ChangeEventType getType() {
            return type;
        }

        public SceneObject getObject() {
            return object;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return String.format("ChangeEvent{type=%s, object=%s, description='%s'}",
                    type, object != null ? object.getName() : "null", description);
        }
    }

    /**
     * Интерфейс слушателя изменений сцены.
     */
    @FunctionalInterface
    public interface ChangeListener {
        void onSceneChanged(ChangeEvent event);
    }

    // ==================== Поля ====================

    /** Уникальный идентификатор сцены */
    private final String id;

    /** Имя сцены */
    private String name;

    /** Список объектов на сцене */
    private final List<SceneObject> objects;

    /** Множество выбранных объектов (по ID) */
    private final Set<String> selectedObjectIds;

    /** Слушатели изменений сцены */
    private final List<ChangeListener> changeListeners;

    /** Флаг приостановки уведомлений (для пакетных операций) */
    private boolean notificationsSuspended;

    /** Очередь отложенных событий */
    private final List<ChangeEvent> pendingEvents;

    // ==================== Конструкторы ====================

    /**
     * Создаёт пустую сцену с автоматическим именем.
     */
    public Scene() {
        this("Scene_" + System.currentTimeMillis());
    }

    /**
     * Создаёт пустую сцену с указанным именем.
     * @param name имя сцены
     */
    public Scene(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = Objects.requireNonNullElse(name, "Unnamed Scene");
        this.objects = new ArrayList<>();
        this.selectedObjectIds = new LinkedHashSet<>();
        this.changeListeners = new CopyOnWriteArrayList<>();
        this.notificationsSuspended = false;
        this.pendingEvents = new ArrayList<>();
    }

    // ==================== Getters и Setters ====================

    /**
     * Получить уникальный идентификатор сцены.
     * @return ID сцены
     */
    public String getId() {
        return id;
    }

    /**
     * Получить имя сцены.
     * @return имя сцены
     */
    public String getName() {
        return name;
    }

    /**
     * Установить имя сцены.
     * @param name новое имя
     */
    public void setName(String name) {
        this.name = Objects.requireNonNullElse(name, "Unnamed Scene");
    }

    // ==================== Управление объектами ====================

    /**
     * Добавить модель на сцену.
     * @param model модель для добавления
     * @return созданный объект сцены
     */
    public SceneObject addModel(Model model) {
        return addModel(model, model != null ? model.getName() : "Unnamed");
    }

    /**
     * Добавить модель на сцену с указанным именем.
     * @param model модель
     * @param name имя объекта на сцене
     * @return созданный объект сцены
     */
    public SceneObject addModel(Model model, String name) {
        Objects.requireNonNull(model, "Модель не может быть null");
        SceneObject sceneObject = new SceneObject(model, name);
        return addObject(sceneObject);
    }

    /**
     * Добавить объект на сцену.
     * @param sceneObject объект для добавления
     * @return добавленный объект
     */
    public SceneObject addObject(SceneObject sceneObject) {
        Objects.requireNonNull(sceneObject, "Объект сцены не может быть null");

        // Проверяем, нет ли уже такого объекта
        if (containsObject(sceneObject.getId())) {
            throw new IllegalArgumentException("Объект с ID " + sceneObject.getId() + " уже существует на сцене");
        }

        objects.add(sceneObject);
        fireChangeEvent(new ChangeEvent(ChangeEventType.OBJECT_ADDED, sceneObject,
                "Добавлен объект: " + sceneObject.getName()));

        return sceneObject;
    }

    /**
     * Удалить объект со сцены по ID.
     * @param objectId ID объекта для удаления
     * @return true если объект был удалён
     */
    public boolean removeObject(String objectId) {
        Objects.requireNonNull(objectId, "ID объекта не может быть null");

        Optional<SceneObject> toRemove = findById(objectId);
        if (toRemove.isEmpty()) {
            return false;
        }

        SceneObject object = toRemove.get();
        objects.remove(object);
        selectedObjectIds.remove(objectId);

        fireChangeEvent(new ChangeEvent(ChangeEventType.OBJECT_REMOVED, object,
                "Удалён объект: " + object.getName()));

        return true;
    }

    /**
     * Удалить объект со сцены.
     * @param sceneObject объект для удаления
     * @return true если объект был удалён
     */
    public boolean removeObject(SceneObject sceneObject) {
        if (sceneObject == null) {
            return false;
        }
        return removeObject(sceneObject.getId());
    }

    /**
     * Удалить объект по индексу.
     * @param index индекс объекта
     * @return удалённый объект или null
     */
    public SceneObject removeObjectAt(int index) {
        if (index < 0 || index >= objects.size()) {
            return null;
        }

        SceneObject object = objects.get(index);
        removeObject(object.getId());
        return object;
    }

    /**
     * Удалить все выбранные объекты.
     * @return количество удалённых объектов
     */
    public int removeSelectedObjects() {
        List<String> idsToRemove = new ArrayList<>(selectedObjectIds);
        int removed = 0;

        suspendNotifications();
        try {
            for (String id : idsToRemove) {
                if (removeObject(id)) {
                    removed++;
                }
            }
        } finally {
            resumeNotifications();
        }

        return removed;
    }

    /**
     * Проверить, содержит ли сцена объект с указанным ID.
     * @param objectId ID объекта
     * @return true если объект существует
     */
    public boolean containsObject(String objectId) {
        return findById(objectId).isPresent();
    }

    // ==================== Доступ к объектам ====================

    /**
     * Получить объект по индексу.
     * @param index индекс объекта
     * @return объект
     * @throws IndexOutOfBoundsException если индекс вне диапазона
     */
    public SceneObject getObject(int index) {
        if (index < 0 || index >= objects.size()) {
            throw new IndexOutOfBoundsException(
                    String.format("Индекс %d вне диапазона [0, %d)", index, objects.size()));
        }
        return objects.get(index);
    }

    /**
     * Найти объект по ID.
     * @param objectId ID объекта
     * @return Optional с объектом или пустой
     */
    public Optional<SceneObject> findById(String objectId) {
        if (objectId == null) {
            return Optional.empty();
        }
        return objects.stream()
                .filter(obj -> obj.getId().equals(objectId))
                .findFirst();
    }

    /**
     * Найти объект по имени.
     * @param name имя объекта
     * @return Optional с первым найденным объектом или пустой
     */
    public Optional<SceneObject> findByName(String name) {
        if (name == null) {
            return Optional.empty();
        }
        return objects.stream()
                .filter(obj -> name.equals(obj.getName()))
                .findFirst();
    }

    /**
     * Найти все объекты по имени.
     * @param name имя объекта
     * @return список объектов с указанным именем
     */
    public List<SceneObject> findAllByName(String name) {
        if (name == null) {
            return Collections.emptyList();
        }
        return objects.stream()
                .filter(obj -> name.equals(obj.getName()))
                .collect(Collectors.toList());
    }

    /**
     * Найти объекты по предикату.
     * @param predicate условие поиска
     * @return список найденных объектов
     */
    public List<SceneObject> findAll(Predicate<SceneObject> predicate) {
        Objects.requireNonNull(predicate, "Предикат не может быть null");
        return objects.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    /**
     * Получить неизменяемый список всех объектов.
     * @return неизменяемый список объектов
     */
    public List<SceneObject> getObjects() {
        return Collections.unmodifiableList(objects);
    }

    /**
     * Получить список всех моделей на сцене.
     * @return список моделей
     */
    public List<Model> getAllModels() {
        return objects.stream()
                .map(SceneObject::getModel)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Получить индекс объекта.
     * @param sceneObject объект
     * @return индекс или -1 если не найден
     */
    public int indexOf(SceneObject sceneObject) {
        return objects.indexOf(sceneObject);
    }

    /**
     * Получить количество объектов на сцене.
     * @return количество объектов
     */
    public int getObjectCount() {
        return objects.size();
    }

    /**
     * Проверить, пуста ли сцена.
     * @return true если сцена не содержит объектов
     */
    public boolean isEmpty() {
        return objects.isEmpty();
    }

    // ==================== Управление выбором ====================

    /**
     * Выбрать объект (сделать активным).
     * @param sceneObject объект для выбора
     * @param selected состояние выбора
     */
    public void setSelected(SceneObject sceneObject, boolean selected) {
        if (sceneObject == null || !containsObject(sceneObject.getId())) {
            return;
        }

        boolean changed = false;
        if (selected) {
            changed = selectedObjectIds.add(sceneObject.getId());
        } else {
            changed = selectedObjectIds.remove(sceneObject.getId());
        }

        sceneObject.setSelected(selected);

        if (changed) {
            fireChangeEvent(new ChangeEvent(ChangeEventType.SELECTION_CHANGED, sceneObject,
                    (selected ? "Выбран: " : "Снят выбор: ") + sceneObject.getName()));
        }
    }

    /**
     * Выбрать объект по индексу.
     * @param index индекс объекта
     * @param selected состояние выбора
     */
    public void setSelectedAt(int index, boolean selected) {
        if (index >= 0 && index < objects.size()) {
            setSelected(objects.get(index), selected);
        }
    }

    /**
     * Выбрать только один объект (снять выбор со всех остальных).
     * @param sceneObject объект для выбора
     */
    public void selectOnly(SceneObject sceneObject) {
        suspendNotifications();
        try {
            clearSelection();
            if (sceneObject != null) {
                setSelected(sceneObject, true);
            }
        } finally {
            resumeNotifications();
        }
    }

    /**
     * Выбрать только один объект по индексу.
     * @param index индекс объекта
     */
    public void selectOnlyAt(int index) {
        if (index >= 0 && index < objects.size()) {
            selectOnly(objects.get(index));
        }
    }

    /**
     * Переключить состояние выбора объекта.
     * @param sceneObject объект
     */
    public void toggleSelection(SceneObject sceneObject) {
        if (sceneObject == null) {
            return;
        }
        setSelected(sceneObject, !sceneObject.isSelected());
    }

    /**
     * Выбрать все объекты.
     */
    public void selectAll() {
        suspendNotifications();
        try {
            for (SceneObject obj : objects) {
                setSelected(obj, true);
            }
        } finally {
            resumeNotifications();
        }
    }

    /**
     * Снять выбор со всех объектов.
     */
    public void clearSelection() {
        suspendNotifications();
        try {
            List<String> ids = new ArrayList<>(selectedObjectIds);
            for (String id : ids) {
                findById(id).ifPresent(obj -> setSelected(obj, false));
            }
        } finally {
            resumeNotifications();
        }
    }

    /**
     * Инвертировать выбор.
     */
    public void invertSelection() {
        suspendNotifications();
        try {
            for (SceneObject obj : objects) {
                toggleSelection(obj);
            }
        } finally {
            resumeNotifications();
        }
    }

    /**
     * Получить список выбранных объектов.
     * @return список выбранных объектов
     */
    public List<SceneObject> getSelectedObjects() {
        return objects.stream()
                .filter(SceneObject::isSelected)
                .collect(Collectors.toList());
    }

    /**
     * Получить первый выбранный объект.
     * @return Optional с первым выбранным объектом
     */
    public Optional<SceneObject> getFirstSelectedObject() {
        return objects.stream()
                .filter(SceneObject::isSelected)
                .findFirst();
    }

    /**
     * Проверить, есть ли выбранные объекты.
     * @return true если есть хотя бы один выбранный объект
     */
    public boolean hasSelectedObjects() {
        return !selectedObjectIds.isEmpty();
    }

    /**
     * Получить количество выбранных объектов.
     * @return количество выбранных объектов
     */
    public int getSelectedCount() {
        return selectedObjectIds.size();
    }

    /**
     * Проверить, выбран ли объект.
     * @param sceneObject объект для проверки
     * @return true если объект выбран
     */
    public boolean isSelected(SceneObject sceneObject) {
        return sceneObject != null && selectedObjectIds.contains(sceneObject.getId());
    }

    // ==================== Видимость объектов ====================

    /**
     * Получить список видимых объектов.
     * @return список видимых объектов
     */
    public List<SceneObject> getVisibleObjects() {
        return objects.stream()
                .filter(SceneObject::isVisible)
                .collect(Collectors.toList());
    }

    /**
     * Получить объекты для рендеринга (видимые с непустыми моделями).
     * @return список объектов для рендеринга
     */
    public List<SceneObject> getRenderableObjects() {
        return objects.stream()
                .filter(SceneObject::shouldRender)
                .collect(Collectors.toList());
    }

    /**
     * Показать все объекты.
     */
    public void showAll() {
        objects.forEach(obj -> obj.setVisible(true));
    }

    /**
     * Скрыть все объекты.
     */
    public void hideAll() {
        objects.forEach(obj -> obj.setVisible(false));
    }

    /**
     * Показать только выбранные объекты.
     */
    public void showOnlySelected() {
        for (SceneObject obj : objects) {
            obj.setVisible(obj.isSelected());
        }
    }

    // ==================== Операции над сценой ====================

    /**
     * Очистить сцену (удалить все объекты).
     */
    public void clear() {
        objects.clear();
        selectedObjectIds.clear();
        fireChangeEvent(new ChangeEvent(ChangeEventType.SCENE_CLEARED, null, "Сцена очищена"));
    }

    /**
     * Выполнить действие для каждого объекта.
     * @param action действие
     */
    public void forEach(Consumer<SceneObject> action) {
        Objects.requireNonNull(action, "Действие не может быть null");
        objects.forEach(action);
    }

    /**
     * Выполнить действие для каждого выбранного объекта.
     * @param action действие
     */
    public void forEachSelected(Consumer<SceneObject> action) {
        Objects.requireNonNull(action, "Действие не может быть null");
        getSelectedObjects().forEach(action);
    }

    /**
     * Дублировать выбранные объекты.
     * @param deepCopyModels если true, создаёт копии моделей
     * @return список созданных копий
     */
    public List<SceneObject> duplicateSelected(boolean deepCopyModels) {
        List<SceneObject> copies = new ArrayList<>();
        List<SceneObject> selected = getSelectedObjects();

        suspendNotifications();
        try {
            for (SceneObject obj : selected) {
                SceneObject copy = obj.copy(deepCopyModels);
                // Немного сместим копию, чтобы она была видна
                copy.translate(1f, 0f, 0f);
                addObject(copy);
                copies.add(copy);
            }
        } finally {
            resumeNotifications();
        }

        return copies;
    }

    // ==================== Слушатели изменений ====================

    /**
     * Добавить слушателя изменений.
     * @param listener слушатель
     */
    public void addChangeListener(ChangeListener listener) {
        if (listener != null && !changeListeners.contains(listener)) {
            changeListeners.add(listener);
        }
    }

    /**
     * Удалить слушателя изменений.
     * @param listener слушатель
     */
    public void removeChangeListener(ChangeListener listener) {
        changeListeners.remove(listener);
    }

    /**
     * Удалить всех слушателей.
     */
    public void removeAllChangeListeners() {
        changeListeners.clear();
    }

    /**
     * Приостановить уведомления (для пакетных операций).
     */
    public void suspendNotifications() {
        notificationsSuspended = true;
    }

    /**
     * Возобновить уведомления и отправить накопленные события.
     */
    public void resumeNotifications() {
        notificationsSuspended = false;
        if (!pendingEvents.isEmpty()) {
            // Отправляем одно обобщённое событие
            fireChangeEvent(new ChangeEvent(ChangeEventType.OBJECT_MODIFIED, null,
                    "Пакетное обновление: " + pendingEvents.size() + " изменений"));
            pendingEvents.clear();
        }
    }

    /**
     * Отправить событие изменения.
     */
    private void fireChangeEvent(ChangeEvent event) {
        if (notificationsSuspended) {
            pendingEvents.add(event);
            return;
        }

        for (ChangeListener listener : changeListeners) {
            try {
                listener.onSceneChanged(event);
            } catch (Exception e) {
                // Логируем ошибку, но продолжаем уведомлять остальных
                System.err.println("Ошибка в слушателе сцены: " + e.getMessage());
            }
        }
    }

    // ==================== Object methods ====================

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Scene scene = (Scene) obj;
        return Objects.equals(id, scene.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Scene{id='%s', name='%s', objects=%d, selected=%d}",
                id, name, objects.size(), selectedObjectIds.size());
    }

    /**
     * Получить подробную информацию о сцене.
     * @return многострочная строка с информацией
     */
    public String toDetailedString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Scene: ").append(name).append("\n");
        sb.append("  ID: ").append(id).append("\n");
        sb.append("  Objects: ").append(objects.size()).append("\n");
        sb.append("  Selected: ").append(selectedObjectIds.size()).append("\n");
        sb.append("  Listeners: ").append(changeListeners.size()).append("\n");

        if (!objects.isEmpty()) {
            sb.append("  Object list:\n");
            for (int i = 0; i < objects.size(); i++) {
                SceneObject obj = objects.get(i);
                sb.append(String.format("    [%d] %s%s%s\n",
                        i,
                        obj.getName(),
                        obj.isSelected() ? " [SELECTED]" : "",
                        obj.isVisible() ? "" : " [HIDDEN]"));
            }
        }

        return sb.toString();
    }
}
