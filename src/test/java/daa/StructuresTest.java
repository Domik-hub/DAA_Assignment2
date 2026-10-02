package daa;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

class StructuresTest {
    private IntList[] lists() { return new IntList[]{new DynamicArray(), new MyLinkedList()}; }

    @Test void listEdgeCases() {
        for (IntList list : lists()) {
            assertEquals(0, list.size());
            assertFalse(list.contains(10));
            assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
            assertThrows(IndexOutOfBoundsException.class, () -> list.remove(0));
            assertThrows(IndexOutOfBoundsException.class, () -> list.add(1, 10));
            assertThrows(IndexOutOfBoundsException.class, () -> list.add(-1, 10));
            list.add(0, 7);
            assertEquals(7, list.get(0));
            assertEquals(7, list.remove(0));
            list.add(5);
            list.add(0, 5);
            list.add(2, 9);
            assertEquals(5, list.get(0));
            assertEquals(9, list.get(2));
            assertTrue(list.contains(5));
            assertEquals(9, list.remove(2));
            list.add(12);
            assertEquals(12, list.get(2));
            assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
            assertThrows(IndexOutOfBoundsException.class, () -> list.get(list.size()));
            assertThrows(IndexOutOfBoundsException.class, () -> list.remove(-1));
            assertThrows(IndexOutOfBoundsException.class, () -> list.remove(list.size()));
            assertThrows(IndexOutOfBoundsException.class, () -> list.add(list.size() + 1, 1));
        }
    }

    @Test void randomListsMatchReference() {
        for (IntList list : lists()) {
            ArrayList<Integer> expected = new ArrayList<>();
            Random random = new Random(42);
            for (int operation = 0; operation < 3_000; operation++) {
                int value = random.nextInt(201) - 100;
                int choice = random.nextInt(5);
                if (choice == 0 || expected.isEmpty()) {
                    list.add(value);
                    expected.add(value);
                } else if (choice == 1) {
                    int index = random.nextInt(expected.size() + 1);
                    list.add(index, value);
                    expected.add(index, value);
                } else if (choice == 2) {
                    int index = random.nextInt(expected.size());
                    assertEquals(expected.remove(index).intValue(), list.remove(index));
                } else if (choice == 3) {
                    int index = random.nextInt(expected.size());
                    assertEquals(expected.get(index).intValue(), list.get(index));
                } else {
                    assertEquals(expected.contains(value), list.contains(value));
                }
                assertEquals(expected.size(), list.size());
                for (int i = 0; i < expected.size(); i++) {
                    assertEquals(expected.get(i).intValue(), list.get(i));
                }
            }
        }
    }

    @Test void heapEdgeCases() {
        MinHeap heap = new MinHeap();
        assertThrows(IllegalStateException.class, heap::peekMin);
        assertThrows(IllegalStateException.class, heap::extractMin);
        int[] values = {7, 7, -3, Integer.MAX_VALUE, Integer.MIN_VALUE};
        for (int value : values) {
            heap.insert(value);
            assertTrue(heap.isValidHeap());
        }
        int[] sorted = {Integer.MIN_VALUE, -3, 7, 7, Integer.MAX_VALUE};
        for (int value : sorted) {
            assertEquals(value, heap.peekMin());
            assertEquals(value, heap.extractMin());
            assertTrue(heap.isValidHeap());
        }
        heap.insert(11);
        assertEquals(11, heap.extractMin());
        assertThrows(IllegalStateException.class, heap::extractMin);
    }

    @Test void heapMatchesPriorityQueueAfterEveryOperation() {
        MinHeap heap = new MinHeap();
        PriorityQueue<Integer> expected = new PriorityQueue<>();
        Random random = new Random(42);
        for (int i = 0; i < 5_000; i++) {
            if (expected.isEmpty() || random.nextInt(3) != 0) {
                int value = random.nextInt();
                heap.insert(value);
                expected.add(value);
            } else { assertEquals(expected.remove().intValue(), heap.extractMin()); }
            assertEquals(expected.size(), heap.size());
            assertTrue(heap.isValidHeap());
            if (!expected.isEmpty()) { assertEquals(expected.peek().intValue(), heap.peekMin()); }
        }
        int previous = Integer.MIN_VALUE;
        while (!expected.isEmpty()) {
            int value = heap.extractMin();
            assertEquals(expected.remove().intValue(), value);
            assertTrue(previous <= value);
            assertTrue(heap.isValidHeap());
            previous = value;
        }
    }

    @Test void arrayCountersIncludeGrowthAndShifts() {
        DynamicArray array = new DynamicArray();
        for (int i = 0; i < 4; i++) { array.add(i); }
        array.metrics().reset();
        array.add(9);
        assertEquals(4, array.metrics().steps);
        assertEquals(4, array.metrics().moves);
        array.metrics().reset();
        array.add(0, 8);
        assertEquals(5, array.metrics().steps);
        assertEquals(5, array.metrics().moves);
        array.metrics().reset();
        array.remove(0);
        assertEquals(6, array.metrics().steps);
        assertEquals(5, array.metrics().moves);
        array.metrics().reset();
        array.get(3);
        assertEquals(1, array.metrics().steps);
        array.metrics().reset();
        assertFalse(array.contains(-1));
        assertEquals(5, array.metrics().steps);
        assertEquals(5, array.metrics().comparisons);
    }

    @Test void listCountersIncludeLinksAndTraversal() {
        MyLinkedList list = new MyLinkedList();
        list.add(1); list.add(2); list.add(3);
        list.metrics().reset();
        list.get(2);
        assertEquals(2, list.metrics().steps);
        list.metrics().reset();
        list.add(0, 7);
        list.remove(0);
        assertEquals(3, list.metrics().moves);
        assertEquals(1, list.metrics().steps);
        list.metrics().reset();
        list.add(2, 8);
        list.remove(2);
        assertEquals(3, list.metrics().moves);
        assertEquals(3, list.metrics().steps);
        list.metrics().reset();
        assertFalse(list.contains(99));
        assertEquals(3, list.metrics().steps);
        assertEquals(3, list.metrics().comparisons);
    }

    @Test void heapCountersCountComparisonsAndSwaps() {
        MinHeap heap = new MinHeap();
        heap.insert(5);
        heap.metrics().reset();
        heap.insert(2);
        assertEquals(4, heap.metrics().steps);
        assertEquals(2, heap.metrics().moves);
        assertEquals(1, heap.metrics().comparisons);
        heap.metrics().reset();
        assertEquals(2, heap.extractMin());
        assertEquals(2, heap.metrics().steps);
        assertEquals(1, heap.metrics().moves);
        assertEquals(0, heap.metrics().comparisons);
        heap.metrics().reset();
        assertEquals(5, heap.peekMin());
        assertEquals(1, heap.metrics().steps);
    }
}
