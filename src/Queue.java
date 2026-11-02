// A generic FIFO queue implemented using a simple array with shifting logic
public class Queue<T> {
    private Object[] array;
    private int rear; // Points to the next available empty slot
    private int size;
    private int capacity;

    // Constructor to initialize the queue with default capacity
    public Queue() {
        this.capacity = 100;
        this.array = new Object[capacity];
        this.rear = 0;
        this.size = 0;
    }

    // Adds an element to the back of the queue
    public void enqueue(T item) {
        // Double the capacity if the array is full
        if (size == capacity) {
            resize(capacity * 2);
        }

        array[rear] = item;
        rear++;
        size++;
    }

    // Removes and returns the element at the front (index 0)
    @SuppressWarnings("unchecked") // Telling compiler that we are sure about the type cast to T
    public T dequeue() {
        if (isEmpty()) return null;

        // get the item at the very front
        T item = (T) array[0];

        // shifting, move all elements one position forward
        for (int i = 0; i < rear - 1; i++) {
            array[i] = array[i + 1];
        }

        // 3. Update the rear pointer and size
        rear--;
        array[rear] = null; // Clear the last slot
        size--;

        return item;
    }

    // Checks if the queue is empty
    public boolean isEmpty() {
        return size == 0;
    }

    // Returns current number of elements
    public int size() {
        return size;
    }

    // Resizes the array when capacity is reached
    private void resize(int newCapacity) {
        Object[] newArray = new Object[newCapacity];

        // Copy elements to the new larger array
        for (int i = 0; i < size; i++) {
            newArray[i] = array[i];
        }

        array = newArray;
        capacity = newCapacity;
    }
}