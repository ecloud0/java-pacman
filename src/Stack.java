// a custom generic Stack (LIFO) data structure implementation
public class Stack<T> {

    private Node<T> top;

    // Internal node class to hold data and the reference to the next node
    private static class Node<T> {
        private T data;
        private Node<T> next;

        public Node(T data) {
            this.data = data;
        }
    }

    // Initializes an empty stack
    public Stack() {
        this.top = null;
    }

    // Pushes a new item onto the top of the stack
    public void push(T item) {
        Node<T> newNode = new Node<>(item);
        newNode.next = top;
        top = newNode;
    }

    // Removes and returns the item at the top of the stack
    public T pop() {
        if (isEmpty()) {
            return null;
        }
        T item = top.data;
        top = top.next;
        return item;
    }

    // returns true if the stack contains no elements
    public boolean isEmpty() {
        return top == null;
    }
}