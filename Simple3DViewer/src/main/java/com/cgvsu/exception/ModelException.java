package com.cgvsu.exception;

/**
 * Исключение для ошибок, связанных с 3D-моделями.
 * Используется при некорректных операциях с моделью (удаление, модификация).
 */
public class ModelException extends ViewerException {

    private final String modelName;
    private final ModelOperation operation;

    /**
     * Тип операции над моделью.
     */
    public enum ModelOperation {
        CREATE("Создание"),
        DELETE("Удаление"),
        MODIFY("Модификация"),
        VALIDATE("Валидация"),
        TRANSFORM("Трансформация"),
        VERTEX_OPERATION("Операция с вершинами"),
        POLYGON_OPERATION("Операция с полигонами"),
        NORMAL_OPERATION("Операция с нормалями"),
        TEXTURE_OPERATION("Операция с текстурами");

        private final String description;

        ModelOperation(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public ModelException(String message) {
        super(ErrorCode.MODEL_ERROR, message);
        this.modelName = null;
        this.operation = null;
    }

    public ModelException(String message, Throwable cause) {
        super(ErrorCode.MODEL_ERROR, message, cause);
        this.modelName = null;
        this.operation = null;
    }

    public ModelException(String message, String modelName, ModelOperation operation) {
        super(ErrorCode.MODEL_ERROR, message);
        this.modelName = modelName;
        this.operation = operation;
    }

    public ModelException(String message, String modelName, ModelOperation operation, Throwable cause) {
        super(ErrorCode.MODEL_ERROR, message, cause);
        this.modelName = modelName;
        this.operation = operation;
    }

    /**
     * Получить имя модели.
     * @return имя модели или null
     */
    public String getModelName() {
        return modelName;
    }

    /**
     * Получить тип операции.
     * @return тип операции или null
     */
    public ModelOperation getOperation() {
        return operation;
    }

    @Override
    public String getFullMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(getErrorCode().name()).append("] ");

        if (operation != null) {
            sb.append(operation.getDescription());
        } else {
            sb.append("Ошибка модели");
        }

        if (modelName != null && !modelName.isEmpty()) {
            sb.append(" '").append(modelName).append("'");
        }

        sb.append(": ").append(getMessage());

        return sb.toString();
    }

    /**
     * Создать исключение для некорректного индекса вершины.
     * @param index некорректный индекс
     * @param maxIndex максимальный допустимый индекс
     * @return исключение
     */
    public static ModelException invalidVertexIndex(int index, int maxIndex) {
        return new ModelException(
                String.format("Индекс вершины %d выходит за границы [0, %d]", index, maxIndex),
                null,
                ModelOperation.VERTEX_OPERATION
        );
    }

    /**
     * Создать исключение для некорректного индекса полигона.
     * @param index некорректный индекс
     * @param maxIndex максимальный допустимый индекс
     * @return исключение
     */
    public static ModelException invalidPolygonIndex(int index, int maxIndex) {
        return new ModelException(
                String.format("Индекс полигона %d выходит за границы [0, %d]", index, maxIndex),
                null,
                ModelOperation.POLYGON_OPERATION
        );
    }

    /**
     * Создать исключение для пустой модели.
     * @return исключение
     */
    public static ModelException emptyModel() {
        return new ModelException(
                "Модель не содержит вершин",
                null,
                ModelOperation.VALIDATE
        );
    }

    /**
     * Создать исключение для null модели.
     * @return исключение
     */
    public static ModelException nullModel() {
        return new ModelException(
                "Модель не может быть null",
                null,
                ModelOperation.VALIDATE
        );
    }
}
