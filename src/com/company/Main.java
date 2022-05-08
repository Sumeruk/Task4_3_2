//Реализовать поразрядную сортировку, сравнить время работы данной сортировки
// с встроенной в библиотеку языка Java реализацией сортировки (Arrays.sort)
// для массива примитивных целых чисел (int[]).
// Для этого построить графики зависимостей времени сортировки
// от количества элементов массива (для отображения графиков можно воспользоваться компонентом JFreeChart


package com.company;
import javax.swing.*;
import ru.vsu.cs.util.SwingUtils;
import java.awt.*;
import java.util.Locale;

import static java.awt.Frame.MAXIMIZED_BOTH;

public class Main {
    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.ROOT);

        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        SwingUtils.setDefaultFont("Arial", 20);

        EventQueue.invokeLater(() -> {
            try {
                JFrame frameMain = new Frame();
                frameMain.setVisible(true);
                frameMain.setExtendedState(MAXIMIZED_BOTH);
            } catch (Exception ex) {
                SwingUtils.showErrorMessageBox(ex);
            }
        });
    }
}
