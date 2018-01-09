
public class BitTrie {

    private static final int D = 5;
    private static final int R = 26;

    private static final long PREFIX = 0b01;
    private static final long KEY    = 0b10;

    private final long[][] words;

    public BitTrie() {
        this.words = new long[D][];
        int d = 0, j = R;
        while (d < D) {
            words[d++] = new long[(j >> 5) + 1];
            j *= R;
        }
    }

    public long query(CharSequence prefix) {
        long q = PREFIX;
        int d = 0, x = 0, len = prefix.length();
        while (q != 0 && d < D && d < len) {
            x = x * R + prefix.charAt(d) - 'A';
            q = words[d++][x >> 5] >> (x << 1) & (PREFIX | KEY);
        }
        return q;
    }

    public void add(String key) {
        int d = 0, x = 0, len = key.length();
        while (d < D && d < len) {
            x = x * R + key.charAt(d) - 'A';
            long q = d + 1 < len ? PREFIX : PREFIX | KEY;
            words[d++][x >> 5] |= q << (x << 1);
        }
    }

}
