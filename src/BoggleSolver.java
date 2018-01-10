import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BoggleSolver {

    private static final int[] SCORE = { 0, 0, 0, 1, 1, 2, 3, 5, 11 };

    private static final int D = 5;
    private static final int R = 26;

    private static final long PREFIX = 0b01;
    private static final long KEY    = 0b10;

    private final BitTrie bt = new BitTrie();
    private final Map<CharStack, FlatTST> tsts = new HashMap<>();

    public BoggleSolver(String[] dictionary) {
        int lo = 0, hi = 1;
        while (lo < dictionary.length) {
            String key = dictionary[lo];
            int len = key.length();
            if (len > 2 && key.charAt(len - 1) != 'Q') {
                boolean isValid = true;
                boolean hasQ = false;
                for (int i = 0; i + 1 < len; ++i) {
                    if (key.charAt(i) == 'Q') {
                        hasQ = true;
                        if (key.charAt(i + 1) != 'U') {
                            isValid = false;
                            break;
                        }
                        ++i;
                    }
                }
                if (isValid) {
                    if (hasQ)
                        key = key.replace("QU", "Q");
                    bt.add(key);
                    if (key.length() > D) {
                        while (hi < dictionary.length) {
                            if (key.regionMatches(0, dictionary[hi], 0, D))
                                ++hi;
                            else break;
                        }
                        tsts.put(new CharStack(key, 0, D),
                                 new FlatTST(dictionary, lo, hi));
                    }
                }
            }
            lo = hi++;
        }
    }

    public Iterable<String> getAllValidWords(BoggleBoard board)
    { return new WordFinder(board).getAllValidWords(); }

    public int scoreOf(String word) {
        int len = word.length();
        if (len > 2) {
            CharStack prefix = new CharStack(word);
            long query = bt.query(prefix);
            if (query != 0 && prefix.length() > D) {
                FlatTST tst = tsts.get(prefix.subSequence(0, D));
                if (tst != null)
                    query = tst.query(prefix);
                else query = 0;
            }
            if (query == (KEY | PREFIX))
                return SCORE[Math.min(len, 8)];
        }
        return 0;
    }

    private class WordFinder {

        private final long[] tray;
        private final Set<String> words;

        private WordFinder(BoggleBoard board) {
            int rows = board.rows();
            int cols = board.cols();
            tray = new long[rows * cols];
            for (int i = 0; i < rows; ++i) {
                for (int j = 0; j < cols; ++j) {
                    long slot = board.getLetter(i, j);
                    for (int di = -1, k = 8; di < 2; ++di) {
                        int ai = i + di;
                        if (ai < 0 || ai >= rows)
                            continue;
                        for (int dj = -1; dj < 2; ++dj) {
                            if (di == 0 && dj == 0)
                                continue;
                            int aj = j + dj;
                            if (aj < 0 || aj >= cols)
                                continue;
                            slot |= (ai * cols + aj & 0x7fL) << k;
                            k += 7;
                        }
                    }
                    tray[i * cols + j] = slot;
                }
            }
            words = new HashSet<>();
            CharStack prefix = new CharStack();
            for (int v = 0; v < tray.length; ++v)
                findWords(v, 0, prefix, 0);
        }

        private void findWords(int v, int x, CharStack prefix, int d) {
            long slot = tray[v];
            char c = (char) (slot & 0x7f);
            tray[v] |= 1L << 7;
            prefix.push(c);
            x += c - 'A';
            long query = bt.query(x, d);
            if (query != 0) {
                if (query == (KEY | PREFIX))
                    words.add(prefix.string());
                for (slot >>>= 8; slot != 0; slot >>>= 7) {
                    int w = (int) slot & 0x7f;
                    if ((tray[w] & 1L << 7) == 0) {
                        if (d + 1 < D) {
                            findWords(w, x * R, prefix, d + 1);
                        } else {
                            FlatTST tst = tsts.get(prefix);
                            if (tst != null)
                                findWords(tst, w, 1, prefix);
                        }
                    }
                }
            }
            tray[v] &= ~(1L << 7);
            prefix.pop();
        }

        private void findWords(FlatTST tst, int v, long x, CharStack prefix) {
            long slot = tray[v];
            char c = (char) (slot & 0x7f);
            tray[v] |= 1L << 7;
            prefix.push(c);
            x = tst.get(x, c);
            long query = tst.query(x);
            if (query != 0) {
                if (query == (KEY | PREFIX))
                    words.add(prefix.string());
                for (slot >>>= 8; slot != 0; slot >>>= 7) {
                    int w = (int) slot & 0x7f;
                    if ((tray[w] & 1L << 7) == 0)
                        findWords(tst, w, x, prefix);
                }
            }
            tray[v] &= ~(1L << 7);
            prefix.pop();
        }

        private Iterable<String> getAllValidWords()
        { return words; }

    }

    private static class BitTrie {

        private final long[][] words;

        private BitTrie() {
            this.words = new long[D][];
            int d = 0, j = R;
            while (d < D) {
                words[d++] = new long[(j >> 5) + 1];
                j *= R;
            }
        }

        private void add(String key) {
            int d = 0, x = 0, len = key.length();
            while (d < D && d < len) {
                x = x * R + key.charAt(d) - 'A';
                long q = d + 1 < len ? PREFIX : PREFIX | KEY;
                words[d++][x >> 5] |= q << (x << 1);
            }
        }

        private long query(CharStack prefix) {
            long q = PREFIX;
            int d = 0, x = 0, len = prefix.length();
            while (q != 0 && d < D && d < len) {
                x = x * R + prefix.charAt(d) - 'A';
                q = query(x, d++);
            }
            return q;
        }

        private long query(int x, int d)
        { return words[d][x >> 5] >> (x << 1) & (PREFIX | KEY); }

    }

    private static class FlatTST {

        private long[] nodes = new long[4];
        private long next = 1;

        private FlatTST(String[] keys, int lo, int hi)
        { addAll(keys, lo, hi); }

        private void addAll(String[] keys, int lo, int hi) {
            if (lo < hi) {
                int mid = lo + (hi - lo >> 1);
                add(0, keys[mid], D);
                addAll(keys, lo, mid);
                addAll(keys, mid + 1, hi);
            }
        }

        private long add(long x, String key, int d) {
            char c = key.charAt(d);
            if (x == 0) {
                if (next == nodes.length)
                    nodes = Arrays.copyOf(nodes, nodes.length << 1);
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

        private long query(CharStack prefix) {
            long x = get(1, prefix, D);
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

        private long get(long x, char c) {
            if (x == 0)
                return 0;
            int cmp = c - c(x);
            if (cmp < 0)
                return get(left(x), c);
            else if (cmp > 0)
                return get(right(x), c);
            else return x;
        }

        private void setC(long x, char c)
        { nodes[(int) x] |= c; }

        private void setPrefix(long x)
        { nodes[(int) x] |= PREFIX << 0x08; }

        private void setKey(long x)
        { nodes[(int) x] |= KEY << 0x08; }

        private void setMid(long x, long mid)
        { nodes[(int) x] |= mid << 0x10; }

        private void setLeft(long x, long left)
        { nodes[(int) x] |= left << 0x20; }

        private void setRight(long x, long right)
        { nodes[(int) x] |= right << 0x30; }

        private char c(long x)
        { return (char) (nodes[(int) x] & 0xff); }

        private long query(long x)
        { return x != 0 ? nodes[(int) x] >>> 0x08 & (PREFIX | KEY) : 0; }

        private long mid(long x)
        { return nodes[(int) x] >>> 0x10 & 0xffff; }

        private long left(long x)
        { return nodes[(int) x] >>> 0x20 & 0xffff; }

        private long right(long x)
        { return nodes[(int) x] >>> 0x30 & 0xffff; }

    }

}
