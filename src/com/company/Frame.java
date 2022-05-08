package com.company;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.jfree.graphics2d.svg.SVGUtils;
import ru.vsu.cs.util.ArrayUtils;
import ru.vsu.cs.util.JTableUtils;
import ru.vsu.cs.util.SwingUtils;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Consumer;

public class Frame extends JFrame {
    public static final int EXPORT_WIDTH = 800;
    public static final int EXPORT_HEIGHT = 600;

    private JTable tableArr;
    private JButton buttonPerformanceTest;
    private JPanel panelPerformance;
    private JPanel panelMain;
    private JButton buttonRandom1;
    private JButton buttonRandom2;
    private JButton buttonArrSort;
    private JButton buttonRadixSort;
    private JButton buttonSaveChart;

    private JCheckBox checkBoxPartiallyOrdered;
    private JButton buttonSample1;

    private ChartPanel chartPanel = null;
    private JFileChooser fileChooserSave;


    public Frame() {
        this.setTitle("Сортировки");
        this.setContentPane(panelMain);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();

        JTableUtils.initJTableForArray(tableArr, 60, false, true, false, true);
        tableArr.setRowHeight(30);

        fileChooserSave = new JFileChooser();
        fileChooserSave.setCurrentDirectory(new File("./images"));
        FileFilter filter = new FileNameExtensionFilter("SVG images", "svg");
        fileChooserSave.addChoosableFileFilter(filter);
        fileChooserSave.setAcceptAllFileFilterUsed(false);
        fileChooserSave.setDialogType(JFileChooser.SAVE_DIALOG);
        fileChooserSave.setApproveButtonText("Save");

        buttonSaveChart.setVisible(false);

        checkBoxPartiallyOrdered.setVisible(false);
        buttonSample1.addActionListener(actionEvent -> {
            int[] arr = {6, 8, 1, 3, 2, 9, 9, 5, 0, 4};
            JTableUtils.writeArrayToJTable(tableArr, arr);
        });

        buttonRandom1.addActionListener(actionEvent -> {
            int[] arr = ArrayUtils.createRandomIntArray(10, 100);
            JTableUtils.writeArrayToJTable(tableArr, arr);
        });
        buttonRandom2.addActionListener(actionEvent -> {
            int[] arr = ArrayUtils.createRandomIntArray(500, 10000);
            JTableUtils.writeArrayToJTable(tableArr, arr);
        });

        buttonArrSort.addActionListener(actionEvent -> sortDemo(Arrays::sort));
        buttonRadixSort.addActionListener(actionEvent -> sortDemo((arr) -> RadixSort.sort(arr, 10)));

        buttonPerformanceTest.addActionListener(actionEvent -> {
            String[] sortNames = {
                    "Встроенная (Arrays.sort)",
                    "Поразрядная (RadixSort)"
            };
            @SuppressWarnings("unchecked")
            Consumer<Integer[]>[] sorts = new Consumer[]{
                    (Consumer<Integer[]>) Arrays::sort,
                    (Consumer<Integer[]>) (arr) -> RadixSort.sort(arr, 256)
            };
            int[] sizes = {
                    1000, 2000, 3000, 4000, 5000,
                    6000, 7000, 8000, 9000, 10000,
                    11000, 12000, 13000, 14000, 15000,
                    16000, 17000, 18000, 19000, 20000
            };
            performanceTestDemo(sortNames, sorts, sizes, checkBoxPartiallyOrdered.isSelected());
        });

        buttonSaveChart.addActionListener(actionEvent -> {
            if (chartPanel == null) {
                return;
            }
            try {
                if (fileChooserSave.showSaveDialog(Frame.this) == JFileChooser.APPROVE_OPTION) {
                    String filename = fileChooserSave.getSelectedFile().getPath();
                    if (!filename.toLowerCase().endsWith(".svg")) {
                        filename += ".svg";
                    }
                    saveChartIntoFile(filename);
                }
            } catch (Exception e) {
                SwingUtils.showErrorMessageBox(e);
            }
        });

        buttonSample1.doClick();
    }



    public static boolean checkSorted(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] < arr[i - 1]) {
                return false;
            }
        }
        return true;
    }


    private static double[][] performanceTest(Consumer<Integer[]>[] sorts, int[] sizes, boolean partiallyOrdered) {
        Random rnd = new Random();
        double[][] result = new double[sorts.length][sizes.length];

        // надо, по правилам, многократно тестировать, но и так сойдет
        for (int i = 0; i < sizes.length; i++) {
            Integer[] arr1 = new Integer[sizes[i]];
            for (int j = 0; j < arr1.length; j++) {
                arr1[j] = rnd.nextInt((int) 1E6);
            }

            if (partiallyOrdered) {
                Arrays.sort(arr1);
                for (int j = 0; j < arr1.length / 10; j++) {
                    arr1[rnd.nextInt(arr1.length)] = rnd.nextInt((int) 1E6);
                }
            }

            Integer[] arr2 = new Integer[sizes[i]];
            for (int j = 0; j < sorts.length; j++) {
                long moment1, moment2;
                System.arraycopy(arr1, 0, arr2, 0, arr1.length);
                System.gc();
                moment1 = System.nanoTime();
                sorts[j].accept(arr2);
                moment2 = System.nanoTime();
                result[j][i] = (moment2 - moment1) / 1E6;
            }
        }

        return result;
    }

    private void customizeChartDefault(JFreeChart chart) {
        XYPlot plot = chart.getXYPlot();
        XYDataset ds = plot.getDataset();

        for (int i = 0; i < ds.getSeriesCount(); i++) {
            chart.getXYPlot().getRenderer().setSeriesStroke(i, new BasicStroke(2));
        }

        Font font = buttonPerformanceTest.getFont();
        chart.getLegend().setItemFont(font);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.BLACK);
        plot.setRangeGridlinePaint(Color.BLACK);
        plot.getRangeAxis().setTickLabelFont(font);
        plot.getRangeAxis().setLabelFont(font);
        plot.getDomainAxis().setTickLabelFont(font);
        plot.getDomainAxis().setLabelFont(font);
    }

    private void saveChartIntoFile(String filename) throws IOException {
        JFreeChart chart = chartPanel.getChart();
        SVGGraphics2D g2 = new SVGGraphics2D(EXPORT_WIDTH, EXPORT_HEIGHT);
        Rectangle r = new Rectangle(0, 0, EXPORT_WIDTH, EXPORT_HEIGHT);
        chart.draw(g2, r);
        SVGUtils.writeToSVG(new File(filename), g2.getSVGElement());
    }

    private void performanceTestDemo(String[] sortNames, Consumer<Integer[]>[] sorts, int[] sizes, boolean partiallyOrdered) {


        double[][] result = performanceTest(sorts, sizes, partiallyOrdered);

        DefaultXYDataset ds = new DefaultXYDataset();
        double[][] data = new double[2][result.length];
        data[0] = Arrays.stream(sizes).asDoubleStream().toArray();
        for (int i = 0; i < sorts.length; i++) {
            data = data.clone();
            data[1] = result[i];
            ds.addSeries(sortNames[i], data);
        }

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Быстродействие сортировок",
                "Размерность массива, элементов",
                "Время выполнения, мс",
                ds
        );
        customizeChartDefault(chart);


        if (chartPanel == null) {
            chartPanel = new ChartPanel(chart);
            panelPerformance.setLayout(new java.awt.BorderLayout());
            panelPerformance.add(chartPanel, BorderLayout.CENTER);
            panelPerformance.updateUI();
        } else {
            chartPanel.setChart(chart);
        }

        buttonSaveChart.setVisible(true);
    }


    private void sortDemo(Consumer<Integer[]> sort) {
        try {
            Integer[] arr = ArrayUtils.toObject(JTableUtils.readIntArrayFromJTable(tableArr));

            sort.accept(arr);

            int[] primiviteArr = ArrayUtils.toPrimitive(arr);
            JTableUtils.writeArrayToJTable(tableArr, primiviteArr);

            // проверка правильности решения
            assert primiviteArr != null;
            if (!checkSorted(primiviteArr)) {
                // надеюсь, это невозможный сценарий
                SwingUtils.showInfoMessageBox("Упс... А массив-то неправильно отсортирован!");
            }
        } catch (Exception ex) {
            SwingUtils.showErrorMessageBox(ex);
        }
    }

}

