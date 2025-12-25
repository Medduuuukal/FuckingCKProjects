package com.cgvsu.core.math;

import java.util.Objects;

/**
 * Класс для представления трёхмерного вектора с компонентами типа float.
 * Обеспечивает базовые операции векторной алгебры в 3D пространстве.
 *
 * <p>Класс является изменяемым (mutable) для производительности при частых операциях.
 * Для неизменяемых операций используйте статические методы, возвращающие новые объекты.</p>
 */
public class Vector3f {

    private static final float EPSILON = 1e-7f;

    private float x;
    private float y;
    private float z;

    // ==================== Конструкторы ====================

    /**
     * Создаёт нулевой вектор (0, 0, 0).
     */
    public Vector3f() {
        this.x = 0f;
        this.y = 0f;
        this.z = 0f;
    }

    /**
     * Создаёт вектор с заданными компонентами.
     * @param x компонент X
     * @param y компонент Y
     * @param z компонент Z
     */
    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Создаёт копию вектора.
     * @param other вектор для копирования
     */
    public Vector3f(Vector3f other) {
        Objects.requireNonNull(other, "Вектор не может быть null");
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }

    /**
     * Создаёт вектор из массива.
     * @param array массив из 3 элементов
     * @throws IllegalArgumentException если массив имеет неверный размер
     */
    public Vector3f(float[] array) {
        Objects.requireNonNull(array, "Массив не может быть null");
        if (array.length < 3) {
            throw new IllegalArgumentException("Массив должен содержать минимум 3 элемента");
        }
        this.x = array[0];
        this.y = array[1];
        this.z = array[2];
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

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    /**
     * Устанавливает все компоненты вектора.
     * @param x компонент X
     * @param y компонент Y
     * @param z компонент Z
     */
    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Копирует значения из другого вектора.
     * @param other исходный вектор
     */
    public void set(Vector3f other) {
        Objects.requireNonNull(other, "Вектор не может быть null");
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }

    /**
     * Получает компонент по индексу.
     * @param index индекс (0=x, 1=y, 2=z)
     * @return значение компонента
     * @throws IndexOutOfBoundsException если индекс вне диапазона [0, 2]
     */
    public float get(int index) {
        return switch (index) {
            case 0 -> x;
            case 1 -> y;
            case 2 -> z;
            default -> throw new IndexOutOfBoundsException("Индекс должен быть в диапазоне [0, 2]: " + index);
        };
    }

    /**
     * Устанавливает компонент по индексу.
     * @param index индекс (0=x, 1=y, 2=z)
     * @param value значение
     * @throws IndexOutOfBoundsException если индекс вне диапазона [0, 2]
     */
    public void set(int index, float value) {
        switch (index) {
            case 0 -> x = value;
            case 1 -> y = value;
            case 2 -> z = value;
            default -> throw new IndexOutOfBoundsException("Индекс должен быть в диапазоне [0, 2]: " + index);
        }
    }

    /**
     * Возвращает вектор в виде массива.
     * @return массив [x, y, z]
     */
    public float[] toArray() {
        return new float[]{x, y, z};
    }

    // ==================== Математические операции (изменяющие текущий объект) ====================

    /**
     * Прибавляет вектор к текущему.
     * @param other вектор для сложения
     * @return this для цепочки вызовов
     */
    public Vector3f add(Vector3f other) {
        Objects.requireNonNull(other, "Вектор не может быть null");
        this.x += other.x;
        this.y += other.y;
        this.z += other.z;
        return this;
    }

    /**
     * Прибавляет компоненты к текущему вектору.
     * @param dx смещение по X
     * @param dy смещение по Y
     * @param dz смещение по Z
     * @return this для цепочки вызовов
     */
    public Vector3f add(float dx, float dy, float dz) {
        this.x += dx;
        this.y += dy;
        this.z += dz;
        return this;
    }

    /**
     * Вычитает вектор из текущего.
     * @param other вектор для вычитания
     * @return this для цепочки вызовов
     */
    public Vector3f subtract(Vector3f other) {
        Objects.requireNonNull(other, "Вектор не может быть null");
        this.x -= other.x;
        this.y -= other.y;
        this.z -= other.z;
        return this;
    }

    /**
     * Вычитает компоненты из текущего вектора.
     * @param dx смещение по X
     * @param dy смещение по Y
     * @param dz смещение по Z
     * @return this для цепочки вызовов
     */
    public Vector3f subtract(float dx, float dy, float dz) {
        this.x -= dx;
        this.y -= dy;
        this.z -= dz;
        return this;
    }

    /**
     * Умножает вектор на скаляр.
     * @param scalar скалярный множитель
     * @return this для цепочки вызовов
     */
    public Vector3f multiply(float scalar) {
        this.x *= scalar;
        this.y *= scalar;
        this.z *= scalar;
        return this;
    }

    /**
     * Покомпонентное умножение на другой вектор.
     * @param other вектор-множитель
     * @return this для цепочки вызовов
     */
    public Vector3f multiply(Vector3f other) {
        Objects.requireNonNull(other, "Вектор не может быть null");
        this.x *= other.x;
        this.y *= other.y;
        this.z *= other.z;
        return this;
    }

    /**
     * Делит вектор на скаляр.
     * @param scalar скалярный делитель (не должен быть нулём)
     * @return this для цепочки вызовов
     * @throws ArithmeticException если scalar близок к нулю
     */
    public Vector3f divide(float scalar) {
        if (Math.abs(scalar) < EPSILON) {
            throw new ArithmeticException("Деление на ноль");
        }
        this.x /= scalar;
        this.y /= scalar;
        this.z /= scalar;
        return this;
    }

    /**
     * Нормализует вектор (делает единичным).
     * @return this для цепочки вызовов
     * @throws ArithmeticException если вектор нулевой
     */
    public Vector3f normalize() {
        float length = length();
        if (length < EPSILON) {
            throw new ArithmeticException("Невозможно нормализовать нулевой вектор");
        }
        return divide(length);
    }

    /**
     * Безопасная нормализация - возвращает нулевой вектор если длина равна нулю.
     * @return this для цепочки вызовов
     */
    public Vector3f normalizeSafe() {
        float length = length();
        if (length < EPSILON) {
            return zero();
        }
        return divide(length);
    }

    /**
     * Вычисляет векторное произведение с другим вектором и сохраняет результат в текущем.
     * @param other другой вектор
     * @return this для цепочки вызовов
     */
    public Vector3f cross(Vector3f other) {
        Objects.requireNonNull(other, "Вектор не может быть null");
        float newX = this.y * other.z - this.z * other.y;
        float newY = this.z * other.x - this.x * other.z;
        float newZ = this.x * other.y - this.y * other.x;
        this.x = newX;
        this.y = newY;
        this.z = newZ;
        return this;
    }

    /**
     * Инвертирует вектор (меняет знак компонент).
     * @return this для цепочки вызовов
     */
    public Vector3f negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        return this;
    }

    /**
     * Обнуляет вектор.
     * @return this для цепочки вызовов
     */
    public Vector3f zero() {
        this.x = 0f;
        this.y = 0f;
        this.z = 0f;
        return this;
    }

    /**
     * Устанавливает минимальные компоненты между текущим вектором и другим.
     * @param other другой вектор
     * @return this для цепочки вызовов
     */
    public Vector3f min(Vector3f other) {
        Objects.requireNonNull(other, "Вектор не может быть null");
        this.x = Math.min(this.x, other.x);
        this.y = Math.min(this.y, other.y);
        this.z = Math.min(this.z, other.z);
        return this;
    }

    /**
     * Устанавливает максимальные компоненты между текущим вектором и другим.
     * @param other другой вектор
     * @return this для цепочки вызовов
     */
    public Vector3f max(Vector3f other) {
        Objects.requireNonNull(other, "Вектор не может быть null");
        this.x = Math.max(this.x, other.x);
        this.y = Math.max(this.y, other.y);
        this.z = Math.max(this.z, other.z);
        return this;
    }

    /**
     * Ограничивает вектор между минимальным и максимальным значениями.
     * @param min минимальные значения
     * @param max максимальные значения
     * @return this для цепочки вызовов
     */
    public Vector3f clamp(Vector3f min, Vector3f max) {
        this.x = Math.max(min.x, Math.min(this.x, max.x));
        this.y = Math.max(min.y, Math.min(this.y, max.y));
        this.z = Math.max(min.z, Math.min(this.z, max.z));
        return this;
    }

    // ==================== Вычисляемые свойства ====================

    /**
     * Вычисляет длину (модуль) вектора.
     * @return длина вектора
     */
    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    /**
     * Вычисляет квадрат длины вектора (быстрее чем length()).
     * @return квадрат длины
     */
    public float lengthSquared() {
        return x * x + y * y + z * z;
    }

    /**
     * Вычисляет скалярное произведение с другим вектором.
     * @param other другой вектор
     * @return скалярное произведение
     */
    public float dot(Vector3f other) {
        Objects.requireNonNull(other, "Вектор не может быть null");
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    /**
     * Вычисляет расстояние до другого вектора (точки).
     * @param other другой вектор
     * @return расстояние
     */
    public float distanceTo(Vector3f other) {
        Objects.requireNonNull(other, "Вектор не может быть null");
        float dx = this.x - other.x;
        float dy = this.y - other.y;
        float dz = this.z - other.z;
        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    /**
     * Вычисляет квадрат расстояния до другого вектора.
     * @param other другой вектор
     * @return квадрат расстояния
     */
    public float distanceSquaredTo(Vector3f other) {
        Objects.requireNonNull(other, "Вектор не может быть null");
        float dx = this.x - other.x;
        float dy = this.y - other.y;
        float dz = this.z - other.z;
        return dx * dx + dy * dy + dz * dz;
    }

    /**
     * Вычисляет угол между текущим вектором и другим (в радианах).
     * @param other другой вектор
     * @return угол в радианах
     */
    public float angleTo(Vector3f other) {
        Objects.requireNonNull(other, "Вектор не может быть null");
        float lenProduct = this.length() * other.length();
        if (lenProduct < EPSILON) {
            return 0f;
        }
        float cos = this.dot(other) / lenProduct;
        // Ограничиваем значение косинуса для избежания ошибок округления
        cos = Math.max(-1f, Math.min(1f, cos));
        return (float) Math.acos(cos);
    }

    /**
     * Проверяет, является ли вектор нулевым.
     * @return true если вектор нулевой
     */
    public boolean isZero() {
        return Math.abs(x) < EPSILON && Math.abs(y) < EPSILON && Math.abs(z) < EPSILON;
    }

    /**
     * Проверяет, является ли вектор единичным.
     * @return true если длина вектора равна 1
     */
    public boolean isUnit() {
        return Math.abs(lengthSquared() - 1f) < EPSILON;
    }

    /**
     * Проверяет, перпендикулярен ли вектор другому.
     * @param other другой вектор
     * @return true если векторы перпендикулярны
     */
    public boolean isPerpendicularTo(Vector3f other) {
        return Math.abs(dot(other)) < EPSILON;
    }

    /**
     * Проверяет, параллелен ли вектор другому.
     * @param other другой вектор
     * @return true если векторы параллельны
     */
    public boolean isParallelTo(Vector3f other) {
        Vector3f cross = Vector3f.cross(this, other);
        return cross.isZero();
    }

    // ==================== Статические фабричные методы ====================

    /**
     * Создаёт нулевой вектор.
     * @return новый нулевой вектор
     */
    public static Vector3f createZero() {
        return new Vector3f(0f, 0f, 0f);
    }

    /**
     * Создаёт единичный вектор (1, 1, 1).
     * @return новый единичный вектор
     */
    public static Vector3f createOne() {
        return new Vector3f(1f, 1f, 1f);
    }

    /**
     * Создаёт единичный вектор по оси X.
     * @return вектор (1, 0, 0)
     */
    public static Vector3f unitX() {
        return new Vector3f(1f, 0f, 0f);
    }

    /**
     * Создаёт единичный вектор по оси Y.
     * @return вектор (0, 1, 0)
     */
    public static Vector3f unitY() {
        return new Vector3f(0f, 1f, 0f);
    }

    /**
     * Создаёт единичный вектор по оси Z.
     * @return вектор (0, 0, 1)
     */
    public static Vector3f unitZ() {
        return new Vector3f(0f, 0f, 1f);
    }

    /**
     * Создаёт вектор "вверх" (0, 1, 0).
     * @return вектор вверх
     */
    public static Vector3f up() {
        return new Vector3f(0f, 1f, 0f);
    }

    /**
     * Создаёт вектор "вниз" (0, -1, 0).
     * @return вектор вниз
     */
    public static Vector3f down() {
        return new Vector3f(0f, -1f, 0f);
    }

    /**
     * Создаёт вектор "вперёд" (0, 0, -1).
     * @return вектор вперёд
     */
    public static Vector3f forward() {
        return new Vector3f(0f, 0f, -1f);
    }

    /**
     * Создаёт вектор "назад" (0, 0, 1).
     * @return вектор назад
     */
    public static Vector3f back() {
        return new Vector3f(0f, 0f, 1f);
    }

    /**
     * Создаёт вектор "влево" (-1, 0, 0).
     * @return вектор влево
     */
    public static Vector3f left() {
        return new Vector3f(-1f, 0f, 0f);
    }

    /**
     * Создаёт вектор "вправо" (1, 0, 0).
     * @return вектор вправо
     */
    public static Vector3f right() {
        return new Vector3f(1f, 0f, 0f);
    }

    // ==================== Статические операции (возвращают новый вектор) ====================

    /**
     * Складывает два вектора и возвращает новый.
     * @param a первый вектор
     * @param b второй вектор
     * @return новый вектор-сумма
     */
    public static Vector3f add(Vector3f a, Vector3f b) {
        return new Vector3f(a.x + b.x, a.y + b.y, a.z + b.z);
    }

    /**
     * Вычитает второй вектор из первого и возвращает новый.
     * @param a первый вектор
     * @param b второй вектор
     * @return новый вектор-разность
     */
    public static Vector3f subtract(Vector3f a, Vector3f b) {
        return new Vector3f(a.x - b.x, a.y - b.y, a.z - b.z);
    }

    /**
     * Умножает вектор на скаляр и возвращает новый.
     * @param v вектор
     * @param scalar скаляр
     * @return новый масштабированный вектор
     */
    public static Vector3f multiply(Vector3f v, float scalar) {
        return new Vector3f(v.x * scalar, v.y * scalar, v.z * scalar);
    }

    /**
     * Покомпонентное умножение двух векторов.
     * @param a первый вектор
     * @param b второй вектор
     * @return новый вектор с покомпонентным произведением
     */
    public static Vector3f multiply(Vector3f a, Vector3f b) {
        return new Vector3f(a.x * b.x, a.y * b.y, a.z * b.z);
    }

    /**
     * Вычисляет векторное произведение и возвращает новый вектор.
     * @param a первый вектор
     * @param b второй вектор
     * @return новый вектор - результат векторного произведения
     */
    public static Vector3f cross(Vector3f a, Vector3f b) {
        return new Vector3f(
                a.y * b.z - a.z * b.y,
                a.z * b.x - a.x * b.z,
                a.x * b.y - a.y * b.x
        );
    }

    /**
     * Нормализует вектор и возвращает новый.
     * @param v исходный вектор
     * @return новый нормализованный вектор
     */
    public static Vector3f normalized(Vector3f v) {
        return new Vector3f(v).normalize();
    }

    /**
     * Линейная интерполяция между двумя векторами.
     * @param a начальный вектор
     * @param b конечный вектор
     * @param t параметр интерполяции [0, 1]
     * @return интерполированный вектор
     */
    public static Vector3f lerp(Vector3f a, Vector3f b, float t) {
        return new Vector3f(
                a.x + (b.x - a.x) * t,
                a.y + (b.y - a.y) * t,
                a.z + (b.z - a.z) * t
        );
    }

    /**
     * Сферическая линейная интерполяция между двумя векторами.
     * @param a начальный вектор
     * @param b конечный вектор
     * @param t параметр интерполяции [0, 1]
     * @return интерполированный вектор
     */
    public static Vector3f slerp(Vector3f a, Vector3f b, float t) {
        float dot = a.dot(b);
        dot = Math.max(-1f, Math.min(1f, dot));

        float theta = (float) Math.acos(dot) * t;
        Vector3f relativeVec = subtract(b, multiply(a, dot)).normalizeSafe();

        return add(multiply(a, (float) Math.cos(theta)),
                multiply(relativeVec, (float) Math.sin(theta)));
    }

    /**
     * Отражает вектор от плоскости с заданной нормалью.
     * @param incident падающий вектор
     * @param normal нормаль к плоскости (должна быть единичной)
     * @return отражённый вектор
     */
    public static Vector3f reflect(Vector3f incident, Vector3f normal) {
        float dotProduct = incident.dot(normal) * 2f;
        return subtract(incident, multiply(normal, dotProduct));
    }

    /**
     * Проецирует вектор на другой вектор.
     * @param v проецируемый вектор
     * @param onto вектор, на который проецируем
     * @return проекция v на onto
     */
    public static Vector3f project(Vector3f v, Vector3f onto) {
        float ontoLengthSq = onto.lengthSquared();
        if (ontoLengthSq < EPSILON) {
            return createZero();
        }
        float scale = v.dot(onto) / ontoLengthSq;
        return multiply(onto, scale);
    }

    // ==================== Object methods ====================

    /**
     * Создаёт копию вектора.
     * @return новый вектор с теми же значениями
     */
    public Vector3f copy() {
        return new Vector3f(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Vector3f other = (Vector3f) obj;
        return Math.abs(x - other.x) < EPSILON &&
                Math.abs(y - other.y) < EPSILON &&
                Math.abs(z - other.z) < EPSILON;
    }

    /**
     * Сравнивает с другим вектором с заданной точностью.
     * @param other другой вектор
     * @param epsilon точность сравнения
     * @return true если векторы равны с заданной точностью
     */
    public boolean equals(Vector3f other, float epsilon) {
        if (other == null) return false;
        return Math.abs(x - other.x) < epsilon &&
                Math.abs(y - other.y) < epsilon &&
                Math.abs(z - other.z) < epsilon;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                Float.floatToIntBits(x),
                Float.floatToIntBits(y),
                Float.floatToIntBits(z)
        );
    }

    @Override
    public String toString() {
        return String.format("Vector3f(%.4f, %.4f, %.4f)", x, y, z);
    }

    /**
     * Возвращает строковое представление с заданным количеством знаков после запятой.
     * @param decimals количество знаков после запятой
     * @return отформатированная строка
     */
    public String toString(int decimals) {
        String format = String.format("Vector3f(%%.%df, %%.%df, %%.%df)", decimals, decimals, decimals);
        return String.format(format, x, y, z);
    }
}
