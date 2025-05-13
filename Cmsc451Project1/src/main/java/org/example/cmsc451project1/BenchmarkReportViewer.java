package org.example.cmsc451project1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class BenchmarkReportViewer extends JFrame {

    public BenchmarkReportViewer() {



        setSize(650, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);

        if (result != JFileChooser.APPROVE_OPTION) {
            JOptionPane.showMessageDialog(this, "No file selected.");
            System.exit(0);
        }

        File file = fileChooser.getSelectedFile();
        setTitle("Benchmark Report - " + file.getName());

        String[] columnNames = {"Size", "Avg Count", "Coef Count", "Avg Time", "Coef Time"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                Scanner lineScanner = new Scanner(line);
                int size = lineScanner.nextInt();
                ArrayList<Long> counts = new ArrayList<>();
                ArrayList<Long> times = new ArrayList<>();

                while (lineScanner.hasNextLong()) {
                    counts.add(lineScanner.nextLong());
                    if (lineScanner.hasNextLong()) {
                        times.add(lineScanner.nextLong());
                    }
                }

                double avgCount = average(counts);
                double coefCount = coefficientOfVariation(counts, avgCount);
                double avgTime = average(times);
                double coefTime = coefficientOfVariation(times, avgTime);

                tableModel.addRow(new Object[]{
                        size,
                        String.format("%.2f", avgCount),
                        String.format("%.2f%%", coefCount),
                        String.format("%.2f", avgTime),
                        String.format("%.2f%%", coefTime)
                });

                lineScanner.close();
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading file: " + e.getMessage());
        }

        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    private double average(ArrayList<Long> list) {
        long sum = 0;
        for (long val : list) sum += val;
        return (double) sum / list.size();
    }

    private double coefficientOfVariation(ArrayList<Long> list, double mean) {
        double sumSq = 0;
        for (long val : list) {
            double diff = val - mean;
            sumSq += diff * diff;
        }
        double stddev = Math.sqrt(sumSq / list.size());
        return (stddev / mean) * 100.0;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BenchmarkReportViewer viewer = new BenchmarkReportViewer();
            viewer.setVisible(true);
        });
    }
}