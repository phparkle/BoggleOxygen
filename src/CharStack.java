import java.util.Arrays;
import java.util.NoSuchElementException;

public class CharStack implements CharSequence {

    private int n;
    private char[] a;

    public CharStack() { a = new char[2]; }

    public boolean isEmpty() { return n == 0; }

    public int size() { return n; }

    public void push(char c) {
        if (n == a.length)
            grow();
        a[n++] = c;
    }

    private void grow() { a = Arrays.copyOf(a, a.length << 1); }

    public char pop() {
        if (!isEmpty()) {
            return a[--n];
        } else throw new NoSuchElementException();
    }

    public char peek() {
        if (!isEmpty())
            return a[n - 1];
        else throw new NoSuchElementException();
    }

    @Override
    public int length() { return n; }

    @Override
    public char charAt(int index) {
        if (index < n)
            return a[index];
        else throw new IndexOutOfBoundsException();
    }

    @Override
    public CharSequence subSequence(int start, int end)
    { throw new UnsupportedOperationException(); }

    @Override
    public String toString() { return String.valueOf(a, 0, n); }

}
