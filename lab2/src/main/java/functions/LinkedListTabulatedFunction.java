package functions;

public class LinkedListTabulatedFunction extends AbstractTabulatedFunction {
    private Node head;
    protected int count;

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

    public LinkedListTabulatedFunction(double[] xValues, double[] yValues) {
        if (xValues.length != yValues.length) {
            throw new IllegalArgumentException("Arrays x and y must have the same length");
        }
        if (xValues.length < 2) {
            throw new IllegalArgumentException("Minimum number of points: 2");
        }

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
            throw new IndexOutOfBoundsException("The index is out of range: " + index);
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
        return getNode(index).x;
    }

    @Override
    public double getY(int index) {
        return getNode(index).y;
    }

    @Override
    public void setY(int index, double value) {
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
            return 0;
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
        if (count == 1) {
            return head.y;
        }
        return interpolate(x, head.x, head.next.x, head.y, head.next.y);
    }

    @Override
    protected double extrapolateRight(double x) {
        if (count == 1) {
            return head.y;
        }
        Node last = head.prev;
        Node prevLast = last.prev;
        return interpolate(x, prevLast.x, last.x, prevLast.y, last.y);
    }

    @Override
    protected double interpolate(double x, int floorIndex) {
        if (count == 1) {
            return head.y;
        }

        if (floorIndex == count - 1) {
            floorIndex--;
        }

        Node leftNode = getNode(floorIndex);
        Node rightNode = getNode(floorIndex + 1);

        return interpolate(x, leftNode.x, rightNode.x, leftNode.y, rightNode.y);
    }
}