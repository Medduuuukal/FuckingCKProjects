package com.cgvsu.config;

/**
 * Конфигурация приложения Simple3DViewer.
 * Содержит все константы, настройки по умолчанию и параметры приложения.
 * Следует паттерну Singleton для единой точки доступа к конфигурации.
 */
public final class AppConfig {

    // ==================== Singleton ====================

    private static final AppConfig INSTANCE = new AppConfig();

    private AppConfig() {
        // Приватный конструктор для Singleton
    }

    public static AppConfig getInstance() {
        return INSTANCE;
    }

    // ==================== Информация о приложении ====================

    public static final String APP_NAME = "Simple3DViewer";
    public static final String APP_VERSION = "1.0.0";
    public static final String APP_DESCRIPTION = "3D-визуализатор для просмотра и редактирования OBJ моделей";

    // ==================== Настройки окна ====================

    public static final int DEFAULT_WINDOW_WIDTH = 1600;
    public static final int DEFAULT_WINDOW_HEIGHT = 900;
    public static final int MIN_WINDOW_WIDTH = 1280;
    public static final int MIN_WINDOW_HEIGHT = 720;

    // ==================== Настройки рендеринга ====================

    /** Целевой FPS */
    public static final int TARGET_FPS = 60;

    /** Интервал между кадрами в миллисекундах */
    public static final double FRAME_INTERVAL_MS = 1000.0 / TARGET_FPS;

    /** Интервал обновления счётчика FPS (в секундах) */
    public static final double FPS_UPDATE_INTERVAL = 1.0;

    // ==================== Настройки камеры ====================

    /** Скорость перемещения камеры по умолчанию */
    public static final float DEFAULT_CAMERA_TRANSLATION_SPEED = 0.5f;

    /** Скорость вращения камеры по умолчанию */
    public static final float DEFAULT_CAMERA_ROTATION_SPEED = 0.05f;

    /** Позиция камеры по умолчанию (X) */
    public static final float DEFAULT_CAMERA_POSITION_X = 0f;

    /** Позиция камеры по умолчанию (Y) */
    public static final float DEFAULT_CAMERA_POSITION_Y = 0f;

    /** Позиция камеры по умолчанию (Z) */
    public static final float DEFAULT_CAMERA_POSITION_Z = 100f;

    /** Точка фокуса камеры по умолчанию (X) */
    public static final float DEFAULT_CAMERA_TARGET_X = 0f;

    /** Точка фокуса камеры по умолчанию (Y) */
    public static final float DEFAULT_CAMERA_TARGET_Y = 0f;

    /** Точка фокуса камеры по умолчанию (Z) */
    public static final float DEFAULT_CAMERA_TARGET_Z = 0f;

    /** Угол обзора камеры по умолчанию */
    public static final float DEFAULT_CAMERA_FOV = 1.0f;

    /** Соотношение сторон камеры по умолчанию */
    public static final float DEFAULT_CAMERA_ASPECT_RATIO = 1.0f;

    /** Ближняя плоскость отсечения */
    public static final float DEFAULT_CAMERA_NEAR_PLANE = 0.01f;

    /** Дальняя плоскость отсечения */
    public static final float DEFAULT_CAMERA_FAR_PLANE = 100f;

    // ==================== Пути к ресурсам ====================

    public static final String FXML_PATH = "/com/cgvsu/fxml/";
    public static final String CSS_PATH = "/com/cgvsu/css/";
    public static final String IMAGES_PATH = "/com/cgvsu/images/";

    /** Путь к главному FXML файлу */
    public static final String MAIN_FXML = FXML_PATH + "gui.fxml";

    /** Путь к тёмной теме */
    public static final String DARK_THEME_CSS = CSS_PATH + "dark-theme.css";

    /** Путь к светлой теме */
    public static final String LIGHT_THEME_CSS = CSS_PATH + "light-theme.css";

    // ==================== Настройки UI ====================

    /** Ширина боковой панели по умолчанию */
    public static final int DEFAULT_SIDE_PANEL_WIDTH = 250;

    /** Минимальная ширина боковой панели */
    public static final int MIN_SIDE_PANEL_WIDTH = 200;

    /** Максимальная ширина боковой панели */
    public static final int MAX_SIDE_PANEL_WIDTH = 400;

    // ==================== Настройки файлов ====================

    /** Расширение OBJ файлов */
    public static final String OBJ_EXTENSION = ".obj";

    /** Описание фильтра для OBJ файлов */
    public static final String OBJ_FILTER_DESCRIPTION = "OBJ файлы (*.obj)";

    /** Паттерн для OBJ файлов */
    public static final String OBJ_FILTER_PATTERN = "*.obj";

    /** Префикс имени модели по умолчанию */
    public static final String DEFAULT_MODEL_NAME_PREFIX = "Model_";

    // ==================== Математические константы ====================

    /** Точность сравнения float значений */
    public static final float FLOAT_EPSILON = 1e-7f;

    /** Точность сравнения double значений */
    public static final double DOUBLE_EPSILON = 1e-15;

    // ==================== Настройки валидации ====================

    /** Минимальное количество вершин в полигоне */
    public static final int MIN_POLYGON_VERTICES = 3;

    /** Максимальное количество вершин в полигоне (для оптимизации) */
    public static final int MAX_POLYGON_VERTICES = 100;

    // ==================== Динамические настройки (могут изменяться) ====================

    private float cameraTranslationSpeed = DEFAULT_CAMERA_TRANSLATION_SPEED;
    private float cameraRotationSpeed = DEFAULT_CAMERA_ROTATION_SPEED;
    private boolean darkTheme = true;
    private boolean showGrid = false;
    private boolean showAxes = true;

    // ==================== Getters и Setters для динамических настроек ====================

    public float getCameraTranslationSpeed() {
        return cameraTranslationSpeed;
    }

    public void setCameraTranslationSpeed(float speed) {
        this.cameraTranslationSpeed = speed;
    }

    public float getCameraRotationSpeed() {
        return cameraRotationSpeed;
    }

    public void setCameraRotationSpeed(float speed) {
        this.cameraRotationSpeed = speed;
    }

    public boolean isDarkTheme() {
        return darkTheme;
    }

    public void setDarkTheme(boolean darkTheme) {
        this.darkTheme = darkTheme;
    }

    public boolean isShowGrid() {
        return showGrid;
    }

    public void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
    }

    public boolean isShowAxes() {
        return showAxes;
    }

    public void setShowAxes(boolean showAxes) {
        this.showAxes = showAxes;
    }

    /**
     * Получить путь к текущей теме.
     * @return путь к CSS файлу текущей темы
     */
    public String getCurrentThemePath() {
        return darkTheme ? DARK_THEME_CSS : LIGHT_THEME_CSS;
    }

    /**
     * Сбросить все настройки к значениям по умолчанию.
     */
    public void resetToDefaults() {
        this.cameraTranslationSpeed = DEFAULT_CAMERA_TRANSLATION_SPEED;
        this.cameraRotationSpeed = DEFAULT_CAMERA_ROTATION_SPEED;
        this.darkTheme = true;
        this.showGrid = false;
        this.showAxes = true;
    }

    /**
     * Получить строку с информацией о версии.
     * @return информация о версии
     */
    public static String getVersionInfo() {
        return String.format("%s v%s", APP_NAME, APP_VERSION);
    }
}
