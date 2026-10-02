package daa;

public class MinHeap {
    private int[] data = new int[4];
    private int size;
    private final Metrics metrics = new Metrics();

    private int read(int index) {
        metrics.steps++;
        return data[index];
    }

    private boolean less(int first, int second) {
        metrics.comparisons++;
        return read(first) < read(second);
    }

    private void swap(int first, int second) {
        int temporary = read(first);
        data[first] = read(second);
        data[second] = temporary;
        metrics.moves += 2;
    }

    private void growIfFull() {
        if (size == data.length) {
            int[] bigger = new int[data.length * 2];
            for (int i = 0; i < size; i++) {
                bigger[i] = read(i);
                metrics.moves++;
            }
            data = bigger;
        }
    }

    public void insert(int value) {
        growIfFull();
        int index = size;
        data[size] = value;
        size++;
        while (index > 0) {
            int parent = (index - 1) / 2;
            if (!less(index, parent)) {
                break;
            }
            swap(index, parent);
            index = parent;
        }
    }

    public int peekMin() {
        if (size == 0) { throw new IllegalStateException("Heap is empty"); }
        return read(0);
    }

    public int extractMin() {
        if (size == 0) { throw new IllegalStateException("Heap is empty"); }
        int minimum = read(0);
        size--;
        if (size == 0) { return minimum; }
        data[0] = read(size);
        metrics.moves++;
        int index = 0;
        while (2 * index + 1 < size) {
            int left = 2 * index + 1;
            int right = left + 1;
            int smaller = left;
            if (right < size && less(right, left)) {
                smaller = right;
            }
            if (!less(smaller, index)) { break; }
            swap(index, smaller);
            index = smaller;
        }
        return minimum;
    }

    boolean isValidHeap() {
        for (int child = 1; child < size; child++) {
            if (data[(child - 1) / 2] > data[child]) { return false; }
        }
        return true;
    }

    public int size() { return size; }
    public Metrics metrics() { return metrics; }
}
