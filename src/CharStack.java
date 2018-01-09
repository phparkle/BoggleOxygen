import java.util.Arrays;
import java.util.NoSuchElementException;

public class CharStack implements CharSequence {

    private char[] value;
    private int offset;
    private int length;
    private int hash;

    public CharStack()
    { value = new char[8]; }

    public CharStack(CharStack other, int offset, int count) {
        this.value = other.value;
        this.offset = offset;
        this.length = count;
    }

    public CharStack(String str)
    { this(str, 0, str.length()); }

    public CharStack(String str, int offset, int count) {
        this.value = str.toCharArray();
        this.offset = offset;
        this.length = count;
    }

    public boolean isEmpty()
    { return length == 0; }

    public int size()
    { return length; }

    public void push(char c) {
        if (offset + length == value.length)
            value = Arrays.copyOf(value, value.length << 1);
        value[offset + length++] = c;
        hash = 0;
    }

    public char pop() {
        if (length != 0) {
            hash = 0;
            return value[offset + --length];
        }
        throw new NoSuchElementException();
    }

    public char peek() {
        if (length != 0)
            return value[offset + length - 1];
        throw new NoSuchElementException();
    }

    public String string()
    { return String.valueOf(value, offset, length); }

    @Override
    public int length()
    { return length; }

    @Override
    public char charAt(int index) {
        if (index < length)
            return value[offset + index];
        throw new IndexOutOfBoundsException();
    }

    @Override
    public CharSequence subSequence(int start, int end)
    { return new CharStack(this, start, end - start); }

    @Override
    public int hashCode() {
        int h = hash;
        if (h == 0 && length > 0) {
            for (int i = offset; i < offset + length; ++i)
                h = 31 * h + value[i];
            hash = h;
        }
        return h;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (obj.getClass() == this.getClass()) {
            CharStack other = (CharStack) obj;
            if (other.length == length) {
                int i = other.offset;
                int j = offset;
                for (int n = length; n > 0; --n, ++i, ++j)
                    if (other.value[i] != value[j])
                        return false;
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString()
    { return string(); }

}
