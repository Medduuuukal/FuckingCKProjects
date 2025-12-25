package com.cgvsu.core.math;

import java.util.Objects;

/**
 * Класс для представления двумерного вектора с компонентами типа float.
 * Обеспечивает базовые операции векторной алгебры.
 *
 * <p>Класс является изменяемым (mutable) для производительности при частых операциях.
 * Для неизменяемых операций используйте статические методы, возвращающие новые объекты.</p>
 */
public class Vector2f {

    private static final float EPSILON = 1e-7f;

    private float x;
    private float y;

    // ==================== Конструкторы ====================

    /**
     * Создаёт нулевой вектор (0, 0).
     */
    public Vector2f() {
        this.x = 0f;
        this.y = 0f;
    }

    /**
     * Создаёт вектор с заданными компонентами.
     * @param x компонент X
     * @param y компонент Y
     */
    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Создаёт копию вектора.
     * @param other вектор для копирования
     */
    public Vector2f(Vector2f other) {
        Objects.requireNonNull(other, "Вектор не может быть null");
        this.x = other.x;
        this.y = other.y;
    }

    // ==================== Getters и Setters ====================

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    /**
     * Устанавливает обе компоненты вектора.
     * @param x компонент X
     * @param y компонент Y
     */
    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Копирует значения из другого вектора.
     * @param other исходный вектор
     */
    public void set(Vector2f other) {
        Objects.requireNonNull(other, "Вектор не может быть null");
        this.x = other.x;
        this.y = other.y;
    }

    // ==================== Математические операции (изменяющие текущий объект) ====================

    /**
     * Прибавляет вектор к текущему.
     * @param other вектор для сложения
     * @return this для цепочки вызовов
     */
    public Vector2f add(Vector2f other) {
        Objects.requireNonNull(other, "Вектор не может быть null");
        this.x += other.x;
        this.y += other.y;
        return this;
    }

    /**
     * Прибавляет компоненты к текущему вектору.
     * @param dx смещение по X
     * @param dy смещение по Y
     * @return this для цепочки вызовов
     */
    public Vector2f add(float dx, float dy) {
        this.x += dx;
        this.y += dy;
        return this;
    }

    /**
     * Вычитает вектор из текущего.
     * @param other вектор для вычитания
     * @return this для цепочки вызовов
     */
    public Vector2f subtract(Vector2f other) {
        Objects.requireNonNull(other, "Вектор не может быть null");
        this.x -= other.x;
        this.y -= other.y;
        return this;
    }

    /**
     * Умножает вектор на скаляр.
     * @param scalar скалярный множитель
     * @return this для цепочки вызовов
     */
    public Vector2f multiply(float scalar) {
        this.x *= scalar;
        this.y *= scalar;
        return this;
    }

    /**
     * Делит вектор на скаляр.
     * @param scalar скалярный делитель (не должен быть нулём)
     * @return this для цепочки вызовов
     * @throws ArithmeticException если scalar близок к нулю
     */
    public Vector2f divide(float scalar) {
        if (Math.abs(scalar) < EPSILON) {
            throw new ArithmeticException("Деление на ноль");
        }
        this.x /= scalar;
        this.y /= scalar;
        return this;
    }

    /**
     * Нормализует вектор (делает единичным).
     * @return this для цепочки вызовов
     * @throws ArithmeticException если вектор нулевой
     */
    public Vector2f normalize() {
        float length = length();
        if (length < EPSILON) {
            throw new ArithmeticException("Невозможно нормализовать нулевой вектор");
        }
        return divide(length);
    }

    /**
     * Инвертирует вектор (меняет знак компонент).
     * @return this для цепочки вызовов
     */
    public Vector2f negate() {
        this.x = -this.x;
        this.y = -this.y;
        return this;
    }

    /**
     * Обнуляет вектор.
     * @return this для цепочки вызовов
     */
    public Vector2f zero() {
        this.x = 0f;
        this.y = 0f;
        return this;
    }

    // ==================== Вычисляемые свойства ====================

    /**
     * Вычисляет длину (модуль) вектора.
     * @return длина вектора
     */
    public float length() {
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * Вычисляет квадрат длины вектора (быстрее чем length()).
     * @return квадрат длины
     */
    public float lengthSquared() {
        return x * x + y * y;
    }

    /**
     * Вычисляет скалярное произведение с другим вектором.
     * @param other другой вектор
     * @return скалярное произведение
     */
    public float dot(Vector2f other) {
        Objects.requireNonNull(other, "Вектор не может быть null");
        return this.x * other.x + this.y * other.y;
    }

    /**
     * Вычисляет расстояние до другого вектора.
     * @param other другой вектор
     * @return расстояние
     */
    public float distanceTo(Vector2f other) {
        Objects.requireNonNull(other, "Вектор не может быть null");
        float dx = this.x - other.x;
        float dy = this.y - other.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Вычисляет квадрат расстояния до другого вектора.
     * @param other другой вектор
     * @return квадрат расстояния
     */
    public float distanceSquaredTo(Vector2f other) {
        Objects.requireNonNull(other, "Вектор не может быть null");
        float dx = this.x - other.x;
        float dy = this.y - other.y;
        return dx * dx + dy * dy;
    }

    /**
     * Проверяет, является ли вектор нулевым.
     * @return true если вектор нулевой
     */
    public boolean isZero() {
        return Math.abs(x) < EPSILON && Math.abs(y) < EPSILON;
    }

    /**
     * Проверяет, является ли вектор единичным.
     * @return true если длина вектора равна 1
     */
    public boolean isUnit() {
        return Math.abs(lengthSquared() - 1f) < EPSILON;
    }

    // ==================== Статические фабричные методы ====================

    /**
     * Создаёт нулевой вектор.
     * @return новый нулевой вектор
     */
    public static Vector2f zero() {
        return new Vector2f(0f, 0f);
    }

    /**
     * Создаёт единичный вектор по оси X.
     * @return вектор (1, 0)
     */
    public static Vector2f unitX() {
        return new Vector2f(1f, 0f);
    }

    /**
     * Создаёт единичный вектор по оси Y.
     * @return вектор (0, 1)
     */
    public static Vector2f unitY() {
        return new Vector2f(0f, 1f);
    }

    // ==================== Статические операции (возвращают новый вектор) ====================

    /**
     * Складывает два вектора и возвращает новый.
     * @param a первый вектор
     * @param b второй вектор
     * @return новый вектор-сумма
     */
    public static Vector2f add(Vector2f a, Vector2f b) {
        return new Vector2f(a.x + b.x, a.y + b.y);
    }

    /**
     * Вычитает второй вектор из первого и возвращает новый.
     * @param a первый вектор
     * @param b второй вектор
     * @return новый вектор-разность
     */
    public static Vector2f subtract(Vector2f a, Vector2f b) {
        return new Vector2f(a.x - b.x, a.y - b.y);
    }

    /**
     * Умножает вектор на скаляр и возвращает новый.
     * @param v вектор
     * @param scalar скаляр
     * @return новый масштабированный вектор
     */
    public static Vector2f multiply(Vector2f v, float scalar) {
        return new Vector2f(v.x * scalar, v.y * scalar);
    }

    /**
     * Линейная интерполяция между двумя векторами.
     * @param a начальный вектор
     * @param b конечный вектор
     * @param t параметр интерполяции [0, 1]
     * @return интерполированный вектор
     */
    public static Vector2f lerp(Vector2f a, Vector2f b, float t) {
        return new Vector2f(
                a.x + (b.x - a.x) * t,
                a.y + (b.y - a.y) * t
        );
    }

    // ==================== Object methods ====================

    /**
     * Создаёт копию вектора.
     * @return новый вектор с теми же значениями
     */
    public Vector2f copy() {
        return new Vector2f(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Vector2f other = (Vector2f) obj;
        return Math.abs(x - other.x) < EPSILON && Math.abs(y - other.y) < EPSILON;
    }

    @Override
    public int hashCode() {
        return Objects.hash(Float.floatToIntBits(x), Float.floatToIntBits(y));
    }

    @Override
    public String toString() {
        return String.format("Vector2f(%.4f, %.4f)", x, y);
    }
}
