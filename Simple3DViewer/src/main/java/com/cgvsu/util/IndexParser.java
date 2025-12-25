package com.cgvsu.util;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Утилитный класс для парсинга индексов из строк.
 * Поддерживает форматы: одиночные индексы, списки через запятую, диапазоны.
 *
 * <p>Примеры поддерживаемых форматов:</p>
 * <ul>
 *   <li>"5" - одиночный индекс</li>
 *   <li>"1, 2, 3" - список индексов</li>
 *   <li>"5-10" - диапазон индексов</li>
 *   <li>"1, 3, 5-10, 15" - смешанный формат</li>
 * </ul>
 */
public final class IndexParser {

    // Паттерн для диапазона (например: 5-10)
    private static final Pattern RANGE_PATTERN = Pattern.compile("^\\s*(\\d+)\\s*-\\s*(\\d+)\\s*$");

    // Паттерн для одиночного числа
    private static final Pattern SINGLE_PATTERN = Pattern.compile("^\\s*(\\d+)\\s*$");

    /**
     * Приватный конструктор - утилитный класс.
     */
    private IndexParser() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Парсит строку с индексами в список целых чисел.
     *
     * @param input входная строка с индексами
     * @return список распознанных индексов (уникальных, отсортированных)
     * @throws IllegalArgumentException если формат строки некорректен
     */
    public static List<Integer> parse(String input) {
        if (input == null || input.trim().isEmpty()) {
            return Collections.emptyList();
        }

        Set<Integer> indices = new TreeSet<>();
        String[] parts = input.split(",");

        for (String part : parts) {
            part = part.trim();
            if (part.isEmpty()) {
                continue;
            }

            // Пробуем распознать как диапазон
            Matcher rangeMatcher = RANGE_PATTERN.matcher(part);
            if (rangeMatcher.matches()) {
                int start = Integer.parseInt(rangeMatcher.group(1));
                int end = Integer.parseInt(rangeMatcher.group(2));
                addRange(indices, start, end);
                continue;
            }

            // Пробуем распознать как одиночное число
            Matcher singleMatcher = SINGLE_PATTERN.matcher(part);
            if (singleMatcher.matches()) {
                int value = Integer.parseInt(singleMatcher.group(1));
                indices.add(value);
                continue;
            }

            // Не удалось распознать
            throw new IllegalArgumentException(
                    "Некорректный формат индекса: '" + part + "'. " +
                    "Ожидается число или диапазон (например: 5 или 5-10)");
        }

        return new ArrayList<>(indices);
    }

    /**
     * Парсит строку с индексами, возвращая пустой список при ошибке.
     * Не выбрасывает исключений.
     *
     * @param input входная строка с индексами
     * @return список распознанных индексов или пустой список при ошибке
     */
    public static List<Integer> parseSafe(String input) {
        try {
            return parse(input);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /**
     * Парсит строку и фильтрует индексы по допустимому диапазону.
     *
     * @param input входная строка с индексами
     * @param minIndex минимальный допустимый индекс (включительно)
     * @param maxIndex максимальный допустимый индекс (исключительно)
     * @return список валидных индексов
     */
    public static List<Integer> parseWithBounds(String input, int minIndex, int maxIndex) {
        List<Integer> indices = parseSafe(input);
        List<Integer> filtered = new ArrayList<>();

        for (Integer index : indices) {
            if (index >= minIndex && index < maxIndex) {
                filtered.add(index);
            }
        }

        return filtered;
    }

    /**
     * Проверяет, является ли строка валидным представлением индексов.
     *
     * @param input входная строка
     * @return true если строка может быть успешно распарсена
     */
    public static boolean isValid(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }

        try {
            List<Integer> result = parse(input);
            return !result.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Форматирует список индексов обратно в строку.
     * Группирует последовательные индексы в диапазоны.
     *
     * @param indices список индексов
     * @return отформатированная строка
     */
    public static String format(List<Integer> indices) {
        if (indices == null || indices.isEmpty()) {
            return "";
        }

        // Сортируем и удаляем дубликаты
        List<Integer> sorted = new ArrayList<>(new TreeSet<>(indices));

        StringBuilder sb = new StringBuilder();
        int rangeStart = sorted.get(0);
        int rangeEnd = rangeStart;

        for (int i = 1; i < sorted.size(); i++) {
            int current = sorted.get(i);

            if (current == rangeEnd + 1) {
                // Продолжаем диапазон
                rangeEnd = current;
            } else {
                // Записываем предыдущий диапазон и начинаем новый
                appendRange(sb, rangeStart, rangeEnd);
                rangeStart = current;
                rangeEnd = current;
            }
        }

        // Записываем последний диапазон
        appendRange(sb, rangeStart, rangeEnd);

        return sb.toString();
    }

    /**
     * Добавляет диапазон индексов в множество.
     */
    private static void addRange(Set<Integer> indices, int start, int end) {
        if (start > end) {
            // Меняем местами если порядок обратный
            int temp = start;
            start = end;
            end = temp;
        }

        // Ограничиваем размер диапазона для защиты от чрезмерного использования памяти
        int maxRangeSize = 10000;
        if (end - start > maxRangeSize) {
            throw new IllegalArgumentException(
                    "Диапазон слишком большой: " + start + "-" + end +
                    ". Максимально допустимый размер: " + maxRangeSize);
        }

        for (int i = start; i <= end; i++) {
            indices.add(i);
        }
    }

    /**
     * Добавляет диапазон в StringBuilder.
     */
    private static void appendRange(StringBuilder sb, int start, int end) {
        if (sb.length() > 0) {
            sb.append(", ");
        }

        if (start == end) {
            sb.append(start);
        } else if (end == start + 1) {
            sb.append(start).append(", ").append(end);
        } else {
            sb.append(start).append("-").append(end);
        }
    }

    /**
     * Создаёт список индексов из диапазона.
     *
     * @param start начальный индекс (включительно)
     * @param end конечный индекс (исключительно)
     * @return список индексов
     */
    public static List<Integer> range(int start, int end) {
        List<Integer> result = new ArrayList<>();
        for (int i = start; i < end; i++) {
            result.add(i);
        }
        return result;
    }

    /**
     * Возвращает описание допустимого формата для пользователя.
     *
     * @return описание формата
     */
    public static String getFormatDescription() {
        return "Введите индексы через запятую или диапазон.\n" +
               "Примеры: '5', '1, 2, 3', '5-10', '1, 3, 5-10'";
    }
}
