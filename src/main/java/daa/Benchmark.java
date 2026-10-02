package daa;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Random;

public class Benchmark {
    private static final int WARMUP = 10;
    private static final int REPEATS = 5;
    private static volatile long sink;

    private static class Result {
        long time;
        long steps;
        long moves;
        long comparisons;
        Result(long time, Metrics metrics) {
            this.time = time;
            steps = metrics.steps;
            moves = metrics.moves;
            comparisons = metrics.comparisons;
        }
    }

    private static int[] makeData(int n) {
        Random random = new Random(42);
        int[] data = new int[n];
        for (int i = 0; i < n; i++) { data[i] = random.nextInt(1_000_000); }
        return data;
    }

    private static Result run(String workload, String variant, String structure, int[] data) {
        int n = data.length;
        if (workload.equals("W4")) {
            MinHeap heap = new MinHeap();
            int[] sorted = new int[n];
            long start = System.nanoTime();
            for (int value : data) { heap.insert(value); }
            for (int i = 0; i < n; i++) { sorted[i] = heap.extractMin(); }
            long elapsed = System.nanoTime() - start;
            for (int i = 1; i < n; i++) {
                if (sorted[i - 1] > sorted[i]) { throw new AssertionError("Unsorted heap output"); }
            }
            sink = sorted[n - 1];
            return new Result(elapsed, heap.metrics());
        }

        IntList list;
        if (structure.equals("DynamicArray")) { list = new DynamicArray(); }
        else { list = new MyLinkedList(); }
        for (int value : data) { list.add(value); }

        Random random = new Random(42);
        int[] indices = new int[10_000];
        int[] queries = new int[1_000];
        for (int i = 0; i < indices.length; i++) { indices[i] = random.nextInt(n); }
        for (int i = 0; i < queries.length; i++) {
            if (i % 2 == 0) { queries[i] = data[random.nextInt(n)]; }
            else { queries[i] = -1 - random.nextInt(1_000_000); }
        }
        list.metrics().reset();
        long checksum = 0;
        long start = System.nanoTime();
        if (workload.equals("W1")) {
            for (int index : indices) { checksum += list.get(index); }
        } else if (workload.equals("W2")) {
            for (int value : queries) {
                if (list.contains(value)) { checksum++; }
            }
        } else {
            int index = variant.equals("head") ? 0 : n / 2;
            for (int i = 0; i < 1_000; i++) { list.add(index, i); }
            for (int i = 0; i < 1_000; i++) { checksum += list.remove(index); }
        }
        long elapsed = System.nanoTime() - start;
        sink = checksum;
        Result result = new Result(elapsed, list.metrics());
        if (workload.equals("W2") && checksum != 500) { throw new AssertionError("Search count"); }
        if (workload.equals("W3")) {
            if (list.size() != n || checksum != 499_500) { throw new AssertionError("W3 result"); }
        }
        return result;
    }

    private static void measure(PrintWriter csv, String workload, String variant,
                                String structure, int[] data) {
        for (int i = 0; i < WARMUP; i++) { run(workload, variant, structure, data); }
        Result[] results = new Result[REPEATS];
        for (int i = 0; i < REPEATS; i++) {
            results[i] = run(workload, variant, structure, data);
            if (results[i].steps != results[0].steps || results[i].moves != results[0].moves
                    || results[i].comparisons != results[0].comparisons) {
                throw new AssertionError("Counters differ between repeated runs");
            }
        }
        for (int i = 1; i < REPEATS; i++) {
            Result current = results[i];
            int j = i - 1;
            while (j >= 0 && results[j].time > current.time) {
                results[j + 1] = results[j];
                j--;
            }
            results[j + 1] = current;
        }
        Result median = results[REPEATS / 2];
        csv.printf(Locale.US, "%s,%s,%s,%d,%.6f,%d,%d,%d%n", workload, variant,
                structure, data.length, median.time / 1_000_000.0,
                median.steps, median.moves, median.comparisons);
        System.out.println(workload + " " + variant + " " + structure + " n=" + data.length);
    }

    public static void main(String[] args) throws IOException {
        Files.createDirectories(Path.of("results"));
        try (PrintWriter csv = new PrintWriter(Files.newBufferedWriter(Path.of("results/results.csv")))) {
            csv.println("workload,variant,structure,n,time_ms,steps,moves,comparisons");
            int[] sizes = {100, 1_000, 10_000, 100_000};
            String[] structures = {"DynamicArray", "MyLinkedList"};
            for (int n : sizes) {
                int[] data = makeData(n);
                for (String structure : structures) {
                    measure(csv, "W1", "-", structure, data);
                    measure(csv, "W2", "-", structure, data);
                    measure(csv, "W3", "head", structure, data);
                    measure(csv, "W3", "middle", structure, data);
                }
                measure(csv, "W4", "-", "MinHeap", data);
            }
        }
        System.out.println("Saved results/results.csv");
    }
}
