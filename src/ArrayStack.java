import java.util.Arrays;
import java.util.NoSuchElementException;

public class ArrayStack<E> {

    private int n;
    private E[] a;

    public ArrayStack() { a = (E[]) new Object[2]; }

    public boolean isEmpty() { return n == 0; }

    public int size() { return n; }

    public void push(E e) {
        if (n == a.length)
            grow();
        a[n++] = e;
    }

    public E pop() {
        if (!isEmpty()) {
            E e = a[--n];
            a[n] = null;
            return e;
        } else throw new NoSuchElementException();
    }

    public E peek() {
        if (!isEmpty())
            return a[n - 1];
        else throw new NoSuchElementException();
    }

    private void grow() { a = Arrays.copyOf(a, a.length << 1); }

    @Override
    public String toString()
    { return Arrays.toString(Arrays.copyOf(a, n)); }

}
