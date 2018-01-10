import java.util.Arrays;
import java.util.NoSuchElementException;

public class CharStack implements CharSequence {

    private char[] value = new char[8];
    private int[] hash = new int[9];
    private int length;

    public boolean isEmpty()
    { return length == 0; }

    public int size()
    { return length; }

    public void push(char c) {
        if (length == value.length) {
            value = Arrays.copyOf(value, length << 1);
            hash = Arrays.copyOf(hash, length << 1 | 1);
        }
        value[length++] = c;
        hash[length] = 31 * hash[length - 1] + c;
    }

    public char pop() {
        if (length != 0)
            return value[--length];
        throw new NoSuchElementException();
    }

    public char peek() {
        if (length != 0)
            return value[length - 1];
        throw new NoSuchElementException();
    }

    @Override
    public int length()
    { return length; }

    @Override
    public char charAt(int index) {
        if (index < length)
            return value[index];
        throw new IndexOutOfBoundsException();
    }

    @Override
    public CharSequence subSequence(int start, int end)
    { throw new UnsupportedOperationException(); }

    @Override
    public int hashCode()
    { return hash[length]; }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (CharSequence.class.isInstance(obj)) {
            CharSequence other = (CharSequence) obj;
            if (length != other.length())
                return false;
            for (int i = 0; i < length; ++i)
                if (value[i] != other.charAt(i))
                    return false;
            return true;
        }
        return false;
    }

    @Override
    public String toString()
    { return String.valueOf(value, 0, length); }

}
