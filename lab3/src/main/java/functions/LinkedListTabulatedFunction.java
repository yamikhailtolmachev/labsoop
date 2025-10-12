package functions;

import java.util.Iterator;
import java.util.NoSuchElementException;
import exceptions.ArrayIsNotSortedException;
import exceptions.DifferentLengthOfArraysException;
import exceptions.InterpolationException;

public class LinkedListTabulatedFunction extends AbstractTabulatedFunction implements Insertable, Removable {
    private Node head;
    protected int count;

    public static class Node {
        public Node prev;
        public Node next;
        public double x;
        public double y;

        public Node(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    private void addNode(double x, double y) {
        Node newNode = new Node(x, y);

        if (head == null) {
            head = newNode;
            head.next = head;
            head.prev = head;
        } else {
            Node last = head.prev;

            last.next = newNode;
            newNode.prev = last;

            newNode.next = head;
            head.prev = newNode;
        }
        count++;
    }

    @Override
    public void insert(double x, double y) {
        if (head == null) {
            addNode(x, y);
            return;
        }

        Node current = head;
        do {
            if (Math.abs(current.x - x) < 1e-12) {
                current.y = y;
                return;
            }

            if (x < current.x) {
                insertBefore(current, x, y);
                return;
            }

            current = current.next;
        } while (current != head);

        addNode(x, y);
    }

    private void insertBefore(Node node, double x, double y) {
        Node newNode = new Node(x, y);
        newNode.prev = node.prev;
        newNode.next = node;
        node.prev.next = newNode;
        node.prev = newNode;

        if (node == head) {
            head = newNode;
        }

        count++;
    }

    @Override
    public void remove(int index) {
        if (index < 0 || index >= count) {
            throw new IllegalArgumentException("Index: " + index + ", Size: " + count);
        }

        if (count <= 2) {
            throw new IllegalStateException("Cannot remove point - must have at least 2 points in tabulated function");
        }

        Node nodeToRemove = getNode(index);

        nodeToRemove.prev.next = nodeToRemove.next;
        nodeToRemove.next.prev = nodeToRemove.prev;

        if (nodeToRemove == head) {
            head = nodeToRemove.next;
        }

        nodeToRemove.next = null;
        nodeToRemove.prev = null;

        count--;
    }

    public LinkedListTabulatedFunction(double[] xValues, double[] yValues) {
        if (xValues.length < 2) {
            throw new IllegalArgumentException("Minimum number of points: 2");
        }

        checkLengthIsTheSame(xValues, yValues);
        checkSorted(xValues);

        this.count = 0;

        for (int i = 0; i < xValues.length; i++) {
            addNode(xValues[i], yValues[i]);
        }
    }

    public LinkedListTabulatedFunction(MathFunction source, double xFrom, double xTo, int count) {
        if (count < 2) {
            throw new IllegalArgumentException("Minimum number of points: 2");
        }

        this.count = 0;

        if (xFrom > xTo) {
            double temp = xFrom;
            xFrom = xTo;
            xTo = temp;
        }

        if (xFrom == xTo) {
            double yValue = source.apply(xFrom);
            for (int i = 0; i < count; i++) {
                addNode(xFrom, yValue);
            }
        } else {
            double step = (xTo - xFrom) / (count - 1);
            for (int i = 0; i < count; i++) {
                double x = xFrom + i * step;
                double y = source.apply(x);
                addNode(x, y);
            }
        }
    }

    private Node getNode(int index) {
        if (index < 0 || index >= count) {
            throw new IllegalArgumentException("The index is out of range: " + index);
        }

        if (index <= count / 2) {
            Node current = head;
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
            return current;
        } else {
            Node current = head.prev;
            for (int i = count - 1; i > index; i--) {
                current = current.prev;
            }
            return current;
        }
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public double getX(int index) {
        if (index < 0 || index >= count) {
            throw new IllegalArgumentException("Index out of bounds: " + index);
        }
        return getNode(index).x;
    }

    @Override
    public double getY(int index) {
        if (index < 0 || index >= count) {
            throw new IllegalArgumentException("Index out of bounds: " + index);
        }
        return getNode(index).y;
    }

    @Override
    public void setY(int index, double value) {
        if (index < 0 || index >= count) {
            throw new IllegalArgumentException("Index out of bounds: " + index);
        }
        getNode(index).y = value;
    }

    @Override
    public int indexOfX(double x) {
        if (head == null) {
            return -1;
        }

        Node current = head;
        for (int i = 0; i < count; i++) {
            if (Math.abs(current.x - x) < 1e-10) {
                return i;
            }
            current = current.next;
        }
        return -1;
    }

    @Override
    public int indexOfY(double y) {
        if (head == null) {
            return -1;
        }

        Node current = head;
        for (int i = 0; i < count; i++) {
            if (Math.abs(current.y - y) < 1e-10) {
                return i;
            }
            current = current.next;
        }
        return -1;
    }

    @Override
    public double leftBound() {
        if (head == null) {
            throw new IllegalStateException("The list is empty");
        }
        return head.x;
    }

    @Override
    public double rightBound() {
        if (head == null) {
            throw new IllegalStateException("The list is empty");
        }
        return head.prev.x;
    }

    @Override
    protected int floorIndexOfX(double x) {
        if (head == null) {
            throw new IllegalStateException("The list is empty");
        }

        if (x < head.x) {
            throw new IllegalArgumentException("x is less than left bound: " + x);
        }
        if (x > head.prev.x) {
            return count - 1;
        }

        Node current = head;
        for (int i = 0; i < count; i++) {
            if (x < current.x) {
                return i - 1;
            }
            current = current.next;
        }
        return count - 1;
    }

    @Override
    protected double extrapolateLeft(double x) {
        return interpolate(x, head.x, head.next.x, head.y, head.next.y);
    }

    @Override
    protected double extrapolateRight(double x) {
        Node last = head.prev;
        Node prevLast = last.prev;
        return interpolate(x, prevLast.x, last.x, prevLast.y, last.y);
    }

    @Override
    protected double interpolate(double x, int floorIndex) {
        if (floorIndex == count - 1) {
            floorIndex--;
        }

        Node leftNode = getNode(floorIndex);
        Node rightNode = getNode(floorIndex + 1);

        if (x < leftNode.x || x > rightNode.x) {
            throw new InterpolationException("x = " + x + " is outside interpolation interval [" + leftNode.x + ", " + rightNode.x + "]");
        }

        return interpolate(x, leftNode.x, rightNode.x, leftNode.y, rightNode.y);
    }

    @Override
    public Iterator<Point> iterator() {
        return new Iterator<Point>() {
            private Node currentNode = head;
            private boolean started = false;

            @Override
            public boolean hasNext() {
                if (head == null || (started && currentNode == head)) {
                    return false;
                }
                return true;
            }

            @Override
            public Point next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("No more elements in the tabulated function");
                }

                Point point = new Point(currentNode.x, currentNode.y);
                currentNode = currentNode.next;
                started = true;

                return point;
            }
        };
    }
}