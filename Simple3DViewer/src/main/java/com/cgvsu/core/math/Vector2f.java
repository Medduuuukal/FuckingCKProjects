package com.cgvsu.core.math;

import java.util.Objects;

/**
 * Класс для представления двумерного вектора с компонентами типа float.
 * Обеспечивает базовые операции векторной алгебры.
 */
public class Vector2f {

    private static final float EPSILON = 1e-7f;

    private float x;
    private float y;

    // ==================== Конструкторы ====================

    public Vector2f() {
        this.x = 0f;
        this.y = 0f;
    }

    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

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

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void set(Vector2f other) {
        Objects.requireNonNull(other, "Вектор не может быть null");
        this.x = other.x;
        this.y = other.y;
    }

    // ==================== Математические операции ====================

    public Vector2f add(Vector2f other) {
        Objects.requireNonNull(other, "Вектор не может быть null");
        this.x += other.x;
        this.y += other.y;
        return this;
    }

    public Vector2f add(float dx, float dy) {
        this.x += dx;
        this.y += dy;
        return this;
    }

    public Vector2f subtract(Vector2f other) {
        Objects.requireNonNull(other, "Вектор не может быть null");
        this.x -= other.x;
        this.y -= other.y;
        return this;
    }

    public Vector2f multiply(float scalar) {
        this.x *= scalar;
        this.y *= scalar;
        return this;
    }

    public Vector2f divide(float scalar) {
        if (Math.abs(scalar) < EPSILON) {
            throw new ArithmeticException("Деление на ноль");
        }
        this.x /= scalar;
        this.y /= scalar;
        return this;
    }

    public Vector2f normalize() {
        float length = length();
        if (length < EPSILON) {
            throw new ArithmeticException("Невозможно нормализовать нулевой вектор");
        }
        return divide(length);
    }

    public Vector2f negate() {
        this.x = -this.x;
        this.y = -this.y;
        return this;
    }

    /**
     * Обнуляет вектор.
     * @return this для цепочки вызовов
     */
    public Vector2f setZero() {
        this.x = 0f;
        this.y = 0f;
        return this;
    }

    // ==================== Вычисляемые свойства ====================

    public float length() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public float lengthSquared() {
        return x * x + y * y;
    }

    public float dot(Vector2f other) {
        Objects.requireNonNull(other, "Вектор не может быть null");
        return this.x * other.x + this.y * other.y;
    }

    public float distanceTo(Vector2f other) {
        Objects.requireNonNull(other, "Вектор не может быть null");
        float dx = this.x - other.x;
        float dy = this.y - other.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public float distanceSquaredTo(Vector2f other) {
        Objects.requireNonNull(other, "Вектор не может быть null");
        float dx = this.x - other.x;
        float dy = this.y - other.y;
        return dx * dx + dy * dy;
    }

    public boolean isZero() {
        return Math.abs(x) < EPSILON && Math.abs(y) < EPSILON;
    }

    public boolean isUnit() {
        return Math.abs(lengthSquared() - 1f) < EPSILON;
    }

    // ==================== Статические фабричные методы ====================

    public static Vector2f zero() {
        return new Vector2f(0f, 0f);
    }

    public static Vector2f unitX() {
        return new Vector2f(1f, 0f);
    }

    public static Vector2f unitY() {
        return new Vector2f(0f, 1f);
    }

    // ==================== Статические операции ====================

    public static Vector2f add(Vector2f a, Vector2f b) {
        return new Vector2f(a.x + b.x, a.y + b.y);
    }

    public static Vector2f subtract(Vector2f a, Vector2f b) {
        return new Vector2f(a.x - b.x, a.y - b.y);
    }

    public static Vector2f multiply(Vector2f v, float scalar) {
        return new Vector2f(v.x * scalar, v.y * scalar);
    }

    public static Vector2f lerp(Vector2f a, Vector2f b, float t) {
        return new Vector2f(
                a.x + (b.x - a.x) * t,
                a.y + (b.y - a.y) * t
        );
    }

    // ==================== Object methods ====================

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
