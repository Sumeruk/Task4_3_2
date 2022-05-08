package com.company;

import java.lang.reflect.Array;
import java.util.function.ToIntFunction;

public class RadixSort {

    private static <T> int getMax(T[] data, ToIntFunction<T> toIntConverter) {
        int max = toIntConverter.applyAsInt(data[0]);
        for (T value : data) {
            int intValue = toIntConverter.applyAsInt(value);
            if (intValue > max) {
                max = intValue;
            }
        }
        return max;
    }

    private static <T> void countingSort(T[] data, int exp, int digitBase, ToIntFunction<T> toIntConverter)
    {
        int size = data.length;

        T[] output =  (T[]) Array.newInstance(data.getClass().getComponentType(), size); // output array
        int[] counts = new int[digitBase];

        // Подсчет количества различных разрядов для exp в count[]
        for (T value : data) {
            int intValue = toIntConverter.applyAsInt(value);
            int index = (intValue / exp) % digitBase;
            counts[index]++;
        }

        // модификация count таким образом, чтобы count[i] указывал
        // на первую позицию в output чисел с цифрой i в обрабатываемом разряде
        for (int i = 1; i < digitBase; i++) {
            counts[i] += counts[i - 1];
        }

        // Формирование результирующего массива
        for (int i = size - 1; i >= 0; i--)
        {
            int intValue = toIntConverter.applyAsInt(data[i]);
            int index = (intValue / exp) % digitBase;
            output[counts[index] - 1] = data[i];
            counts[index]--;
        }

        // Копирование отсортированных по соответствующему разряду данных в исходный массив
        System.arraycopy(output, 0, data, 0, size);
    }

    /**
     * Поразрядная сортировка (RadixSort) массива элементов, которые могут быть представлены в виде целых чисел
     * (чтобы не усложнять реализацию все числа должны быть >= 0)
     *
     * @param <T> Тип элементов массива
     * @param data Массив типа T
     * @param digitBase Размер разряда (основание системы счисления)
     * @param toIntConverter Конвертер объекта в целое число
     */
    public static <T> void sort(T[] data, int digitBase, ToIntFunction<T> toIntConverter)
    {
        int max = getMax(data, toIntConverter);

        // поразрядная сортировка для каждого разрада по основанию digitBase;
        // вместо номера разряда используется exp = digitBase ^ (номер разряда)
        for (int exp = 1; max / exp > 0; exp *= digitBase) {
            countingSort(data, exp, digitBase, toIntConverter);
        }
    }


    /**
     * Поразрядная сортировка (RadixSort) массива целых (Integer) чисел
     * (чтобы не усложнять реализацию все числа должны быть >= 0)
     *
     * @param data Массив
     * @param digitBase Размер разряда (основание системы счисления)
     */
    public static void sort(Integer[] data, int digitBase) {
        sort(data, digitBase, x -> x);
    }
}
