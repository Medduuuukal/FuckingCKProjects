/**
 * Модуль Simple3DViewer - 3D визуализатор моделей.
 * Оптимизирован для работы на macOS.
 */
module com.cgvsu {
    // ==================== Зависимости ====================
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires vecmath;
    requires java.desktop;

    // ==================== Основной пакет ====================
    exports com.cgvsu;
    opens com.cgvsu to javafx.fxml;

    // ==================== Legacy пакеты (рабочие) ====================
    exports com.cgvsu.model;
    opens com.cgvsu.model to javafx.fxml;

    exports com.cgvsu.math;
    opens com.cgvsu.math to javafx.fxml;

    exports com.cgvsu.objreader;
    opens com.cgvsu.objreader to javafx.fxml;

    exports com.cgvsu.objwriter;
    opens com.cgvsu.objwriter to javafx.fxml;

    exports com.cgvsu.render_engine;
    opens com.cgvsu.render_engine to javafx.fxml;

    exports com.cgvsu.scene;
    opens com.cgvsu.scene to javafx.fxml;

    // ==================== Core пакеты ====================
    exports com.cgvsu.core.math;
    opens com.cgvsu.core.math to javafx.fxml;

    exports com.cgvsu.core.model;
    opens com.cgvsu.core.model to javafx.fxml;

    exports com.cgvsu.core.scene;
    opens com.cgvsu.core.scene to javafx.fxml;

    // ==================== IO пакеты ====================
    exports com.cgvsu.io.reader;
    opens com.cgvsu.io.reader to javafx.fxml;

    exports com.cgvsu.io.writer;
    opens com.cgvsu.io.writer to javafx.fxml;

    // ==================== Service пакеты ====================
    exports com.cgvsu.service;
    opens com.cgvsu.service to javafx.fxml;

    // ==================== Config пакеты ====================
    exports com.cgvsu.config;
    opens com.cgvsu.config to javafx.fxml;

    // ==================== Exception пакеты ====================
    exports com.cgvsu.exception;
    opens com.cgvsu.exception to javafx.fxml;

    // ==================== Util пакеты ====================
    exports com.cgvsu.util;
    opens com.cgvsu.util to javafx.fxml;

    // ==================== UI компоненты ====================
    exports com.cgvsu.ui.component;
    opens com.cgvsu.ui.component to javafx.fxml;
}
