package com.cgvsu.scene;

import com.cgvsu.model.Model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Класс Scene управляет коллекцией 3D-моделей на сцене.
 * Поддерживает множественный выбор активных моделей для трансформаций.
 */
public class Scene {

    // Список всех моделей на сцене
    private final List<Model> models;

    // Множество индексов активных (выбранных) моделей
    private final Set<Integer> activeModelIndices;

    // Имена моделей для отображения в UI
    private final List<String> modelNames;

    public Scene() {
        this.models = new ArrayList<>();
        this.activeModelIndices = new HashSet<>();
        this.modelNames = new ArrayList<>();
    }

    /**
     * Добавляет модель на сцену с автоматическим именем.
     * @param model модель для добавления
     * @return индекс добавленной модели
     */
    public int addModel(Model model) {
        return addModel(model, "Model_" + models.size());
    }

    /**
     * Добавляет модель на сцену с указанным именем.
     * @param model модель для добавления
     * @param name имя модели для отображения
     * @return индекс добавленной модели
     */
    public int addModel(Model model, String name) {
        if (model == null) {
            throw new IllegalArgumentException("Модель не может быть null");
        }
        models.add(model);
        modelNames.add(name);
        return models.size() - 1;
    }

    /**
     * Удаляет модель со сцены по индексу.
     * @param index индекс модели для удаления
     * @return true если модель была удалена
     */
    public boolean removeModel(int index) {
        if (!isValidIndex(index)) {
            return false;
        }

        models.remove(index);
        modelNames.remove(index);
        activeModelIndices.remove(index);

        // Обновляем индексы активных моделей после удаления
        Set<Integer> updatedIndices = new HashSet<>();
        for (Integer activeIndex : activeModelIndices) {
            if (activeIndex > index) {
                updatedIndices.add(activeIndex - 1);
            } else {
                updatedIndices.add(activeIndex);
            }
        }
        activeModelIndices.clear();
        activeModelIndices.addAll(updatedIndices);

        return true;
    }

    /**
     * Получить модель по индексу.
     * @param index индекс модели
     * @return модель или null если индекс некорректен
     */
    public Model getModel(int index) {
        if (!isValidIndex(index)) {
            return null;
        }
        return models.get(index);
    }

    /**
     * Получить имя модели по индексу.
     * @param index индекс модели
     * @return имя модели
     */
    public String getModelName(int index) {
        if (!isValidIndex(index)) {
            return null;
        }
        return modelNames.get(index);
    }

    /**
     * Установить имя модели.
     * @param index индекс модели
     * @param name новое имя
     */
    public void setModelName(int index, String name) {
        if (isValidIndex(index)) {
            modelNames.set(index, name);
        }
    }

    /**
     * Получить все модели на сцене.
     * @return неизменяемая копия списка моделей
     */
    public List<Model> getAllModels() {
        return new ArrayList<>(models);
    }

    /**
     * Получить список всех имён моделей.
     * @return копия списка имён
     */
    public List<String> getAllModelNames() {
        return new ArrayList<>(modelNames);
    }

    /**
     * Получить количество моделей на сцене.
     * @return количество моделей
     */
    public int getModelCount() {
        return models.size();
    }

    // ==================== Управление активными моделями ====================

    /**
     * Установить модель как активную (единственную).
     * Снимает выделение со всех остальных моделей.
     * @param index индекс модели
     */
    public void setActiveModel(int index) {
        activeModelIndices.clear();
        if (isValidIndex(index)) {
            activeModelIndices.add(index);
        }
    }

    /**
     * Добавить модель к выбранным (множественный выбор).
     * @param index индекс модели
     */
    public void addToSelection(int index) {
        if (isValidIndex(index)) {
            activeModelIndices.add(index);
        }
    }

    /**
     * Убрать модель из выбранных.
     * @param index индекс модели
     */
    public void removeFromSelection(int index) {
        activeModelIndices.remove(index);
    }

    /**
     * Переключить состояние выбора модели.
     * @param index индекс модели
     */
    public void toggleSelection(int index) {
        if (!isValidIndex(index)) {
            return;
        }
        if (activeModelIndices.contains(index)) {
            activeModelIndices.remove(index);
        } else {
            activeModelIndices.add(index);
        }
    }

    /**
     * Снять выделение со всех моделей.
     */
    public void clearSelection() {
        activeModelIndices.clear();
    }

    /**
     * Выбрать все модели.
     */
    public void selectAll() {
        activeModelIndices.clear();
        for (int i = 0; i < models.size(); i++) {
            activeModelIndices.add(i);
        }
    }

    /**
     * Проверить, является ли модель активной.
     * @param index индекс модели
     * @return true если модель активна
     */
    public boolean isModelActive(int index) {
        return activeModelIndices.contains(index);
    }

    /**
     * Получить индексы всех активных моделей.
     * @return множество индексов активных моделей
     */
    public Set<Integer> getActiveModelIndices() {
        return new HashSet<>(activeModelIndices);
    }

    /**
     * Получить список всех активных моделей.
     * @return список активных моделей
     */
    public List<Model> getActiveModels() {
        List<Model> activeModels = new ArrayList<>();
        for (Integer index : activeModelIndices) {
            if (isValidIndex(index)) {
                activeModels.add(models.get(index));
            }
        }
        return activeModels;
    }

    /**
     * Получить первую активную модель (для операций с единственной моделью).
     * @return первая активная модель или null
     */
    public Model getFirstActiveModel() {
        if (activeModelIndices.isEmpty()) {
            return null;
        }
        int firstIndex = activeModelIndices.iterator().next();
        return models.get(firstIndex);
    }

    /**
     * Получить индекс первой активной модели.
     * @return индекс или -1 если нет активных
     */
    public int getFirstActiveModelIndex() {
        if (activeModelIndices.isEmpty()) {
            return -1;
        }
        return activeModelIndices.iterator().next();
    }

    /**
     * Проверить, есть ли активные модели.
     * @return true если есть хотя бы одна активная модель
     */
    public boolean hasActiveModels() {
        return !activeModelIndices.isEmpty();
    }

    /**
     * Получить количество активных моделей.
     * @return количество активных моделей
     */
    public int getActiveModelCount() {
        return activeModelIndices.size();
    }

    // ==================== Вспомогательные методы ====================

    /**
     * Проверить корректность индекса.
     * @param index индекс для проверки
     * @return true если индекс корректен
     */
    private boolean isValidIndex(int index) {
        return index >= 0 && index < models.size();
    }

    /**
     * Очистить сцену (удалить все модели).
     */
    public void clear() {
        models.clear();
        modelNames.clear();
        activeModelIndices.clear();
    }

    /**
     * Проверить, пуста ли сцена.
     * @return true если на сцене нет моделей
     */
    public boolean isEmpty() {
        return models.isEmpty();
    }
}
