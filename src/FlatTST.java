import java.util.Arrays;

public class FlatTST {

    private static final int INIT_CAPACITY = 4;
    private static final int D = 5;

    private static final long PREFIX = 0b01;
    private static final long KEY    = 0b10;

    private static final int C = 0x00;
    private static final int Q = 0x08;
    private static final int M = 0x10;
    private static final int L = 0x20;
    private static final int R = 0x30;

    private long[] nodes = new long[INIT_CAPACITY];
    private long root = 0;
    private long next = 1;

    public FlatTST(String[] keys, int lo, int hi)
    { addAll(keys, lo, hi); }

    public long query(CharStack prefix) {
        long x = get(root, prefix, D);
        return x != 0 ? query(x) : 0;
    }

    private long get(long x, CharStack prefix, int d) {
        if (x == 0)
            return 0;
        char c = prefix.charAt(d);
        int cmp = c - c(x);
        if (cmp < 0)
            return get(left(x), prefix, d);
        else if (cmp > 0)
            return get(right(x), prefix, d);
        else if (d + 1 < prefix.length())
            return get(mid(x), prefix, d + 1);
        else return x;
    }

    private void addAll(String[] keys, int lo, int hi) {
        if (lo < hi) {
            int mid = lo + (hi - lo >> 1);
            add(keys[mid]);
            addAll(keys, lo, mid);
            addAll(keys, mid + 1, hi);
        }
    }

    private void add(String key)
    { root = add(root, key, D); }

    private long add(long x, String key, int d) {
        char c = key.charAt(d);
        if (x == 0) {
            if (next == nodes.length)
                grow();
            x = next++;
            setC(x, c);
            setPrefix(x);
        }
        int cmp = c - c(x);
        if (cmp < 0)
            setLeft(x, add(left(x), key, d));
        else if (cmp > 0)
            setRight(x, add(right(x), key, d));
        else if (d + 1 < key.length())
            setMid(x, add(mid(x), key, d + 1));
        else setKey(x);
        return x;
    }

    private void setC(long x, char c)
    { nodes[(int) x] |= c; }

    private void setPrefix(long x)
    { nodes[(int) x] |= PREFIX << Q; }

    private void setKey(long x)
    { nodes[(int) x] |= KEY << Q; }

    private void setMid(long x, long mid)
    { nodes[(int) x] |= mid << M; }

    private void setLeft(long x, long left)
    { nodes[(int) x] |= left << L; }

    private void setRight(long x, long right)
    { nodes[(int) x] |= right << R; }

    private char c(long x)
    { return (char) (nodes[(int) x] >>> C & 0xff); }

    private long query(long x)
    { return nodes[(int) x] >>> Q & 0b11; }

    private long mid(long x)
    { return nodes[(int) x] >>> M & 0xffff; }

    private long left(long x)
    { return nodes[(int) x] >>> L & 0xffff; }

    private long right(long x)
    { return nodes[(int) x] >>> R & 0xffff; }

    private void grow()
    { nodes = Arrays.copyOf(nodes, nodes.length << 1); }

}
