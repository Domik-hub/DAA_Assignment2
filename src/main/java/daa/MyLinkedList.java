package daa;

public class MyLinkedList implements IntList {
    private static class Node {
        int value;
        Node next;
        Node(int value) { this.value = value; }
    }

    private Node head;
    private Node tail;
    private int size;
    private final Metrics metrics = new Metrics();

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index: " + index);
        }
    }

    private Node nodeAt(int index) {
        Node current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
            metrics.steps++;
        }
        return current;
    }

    public void add(int value) {
        Node node = new Node(value);
        if (size == 0) {
            head = node;
        } else {
            tail.next = node;
        }
        tail = node;
        metrics.moves += 2;
        size++;
    }

    public void add(int index, int value) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("index: " + index);
        }
        if (index == size) {
            add(value);
            return;
        }
        Node node = new Node(value);
        if (index == 0) {
            node.next = head;
            head = node;
        } else {
            Node previous = nodeAt(index - 1);
            node.next = previous.next;
            previous.next = node;
        }
        metrics.moves += 2;
        size++;
    }

    public int remove(int index) {
        checkIndex(index);
        Node removed;
        if (index == 0) {
            removed = head;
            head = head.next;
            metrics.steps++;
            metrics.moves++;
            if (size == 1) {
                tail = null;
                metrics.moves++;
            }
        } else {
            Node previous = nodeAt(index - 1);
            removed = previous.next;
            metrics.steps++;
            previous.next = removed.next;
            metrics.moves++;
            if (removed == tail) {
                tail = previous;
                metrics.moves++;
            }
        }
        size--;
        return removed.value;
    }

    public int get(int index) {
        checkIndex(index);
        return nodeAt(index).value;
    }

    public boolean contains(int value) {
        Node current = head;
        while (current != null) {
            metrics.comparisons++;
            if (current.value == value) {
                return true;
            }
            current = current.next;
            metrics.steps++;
        }
        return false;
    }

    public int size() { return size; }
    public Metrics metrics() { return metrics; }
}
