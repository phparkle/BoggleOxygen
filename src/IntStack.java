import java.util.Arrays;
import java.util.NoSuchElementException;

public class IntStack {

    private int n;
    private int[] a;

    public IntStack() { a = new int[2]; }

    public boolean isEmpty() { return n == 0; }

    public int size() { return n; }

    public void push(int e) {
        if (n == a.length)
            grow();
        a[n++] = e;
    }

    public int pop() {
        if (!isEmpty())
            return a[--n];
        else throw new NoSuchElementException();
    }

    public int peek() {
        if (!isEmpty())
            return a[n - 1];
        else throw new NoSuchElementException();
    }

    private void grow() { a = Arrays.copyOf(a, a.length << 1); }

    @Override
    public String toString()
    { return Arrays.toString(Arrays.copyOf(a, n)); }

}
