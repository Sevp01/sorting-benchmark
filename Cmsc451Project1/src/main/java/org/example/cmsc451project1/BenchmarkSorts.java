package org.example.cmsc451project1;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class BenchmarkSorts {

    // Generates a random array of given size and bound
    public static int[] generateRandomArray(int size, int bound) {
        Random rand = new Random();
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) {
            arr[i] = rand.nextInt(bound);
        }
        return arr;
    }

    public static void main(String[] args) {
        AbstractSort shellSort = new YourSort1();
        AbstractSort quickSort = new YourSort2();

        // JVM warm-up
        for (int i = 0; i < 200; i++) {
            int[] warmUp1 = generateRandomArray(100, 200);
            int[] warmUp2 = warmUp1.clone();
            try {
                shellSort.sort(warmUp1);
                quickSort.sort(warmUp2);
            } catch (UnsortedException e) {
                System.err.println("Warm-up failed: " + e.getMessage());
            }
        }

        // Benchmarking phase
        try (
                PrintWriter shellOut = new PrintWriter(new FileWriter("ShellSortResults.txt"));
                PrintWriter quickOut = new PrintWriter(new FileWriter("QuickSortResults.txt"))
        ) {
            for (int size = 100; size <= 1200; size += 100) {
                long[] shellCounts = new long[40];
                long[] shellTimes = new long[40];
                long[] quickCounts = new long[40];
                long[] quickTimes = new long[40];

                for (int trial = 0; trial < 40; trial++) {
                    int[] baseData = generateRandomArray(size, size * 2);
                    int[] data1 = baseData.clone();
                    int[] data2 = baseData.clone();

                    try {
                        shellSort.sort(data1);
                        quickSort.sort(data2);
                    } catch (UnsortedException e) {
                        System.err.println("Sort failed: " + e.getMessage());
                        return;
                    }

                    shellCounts[trial] = shellSort.getCount();
                    shellTimes[trial] = shellSort.getTime();
                    quickCounts[trial] = quickSort.getCount();
                    quickTimes[trial] = quickSort.getTime();
                }

                // Output for Shell Sort
                shellOut.print(size);
                for (int i = 0; i < 40; i++) {
                    shellOut.printf(" %d %d", shellCounts[i], shellTimes[i]);
                }
                shellOut.println();

                // Output for Quick Sort
                quickOut.print(size);
                for (int i = 0; i < 40; i++) {
                    quickOut.printf(" %d %d", quickCounts[i], quickTimes[i]);
                }
                quickOut.println();
            }

            System.out.println("Benchmarking complete. Results saved to ShellSortResults.txt and QuickSortResults.txt.");

        } catch (IOException e) {
            System.err.println("Error writing to output files: " + e.getMessage());
        }
    }
}

abstract class AbstractSort {
    private long startTime;
    private long endTime;
    private int count;

    public final void sort(int[] array) throws UnsortedException {
        count = 0;
        startSort();
        doSort(array);
        endSort();
        if (!isSorted(array)) {
            throw new UnsortedException("Array is not sorted correctly.");
        }
    }

    protected abstract void doSort(int[] array);

    protected void startSort() {
        startTime = System.nanoTime();
    }

    protected void endSort() {
        endTime = System.nanoTime();
    }

    protected void incrementCount() {
        count++;
    }

    public int getCount() {
        return count;
    }

    public long getTime() {
        return endTime - startTime;
    }

    private boolean isSorted(int[] array) {
        for (int i = 1; i < array.length; i++) {
            if (array[i - 1] > array[i]) return false;
        }
        return true;
    }
}

// Shell Sort implementation
class YourSort1 extends AbstractSort {
    @Override
    protected void doSort(int[] arr) {
        int n = arr.length;
        for (int gap = n / 2; gap > 0; gap /= 2) {
            for (int i = gap; i < n; i++) {
                int temp = arr[i];
                int j;
                for (j = i; j >= gap && arr[j - gap] > temp; j -= gap) {
                    arr[j] = arr[j - gap];
                    incrementCount();
                }
                arr[j] = temp;
            }
        }
    }
}

// Quick Sort implementation
class YourSort2 extends AbstractSort {
    @Override
    protected void doSort(int[] arr) {
        quickSort(arr, 0, arr.length - 1);
    }

    private void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            int pivot = partition(arr, low, high);
            quickSort(arr, low, pivot - 1);
            quickSort(arr, pivot + 1, high);
        }
    }

    private int partition(int[] arr, int low, int high) {
        int pivot = arr[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (arr[j] <= pivot) {
                i++;
                swap(arr, i, j);
                incrementCount();
            }
        }
        swap(arr, i + 1, high);
        return i + 1;
    }

    private void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}

class UnsortedException extends Exception {
    public UnsortedException(String message) {
        super(message);
    }
}