import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BoggleSolver {

    private static final int[] SCORE = { 0, 0, 0, 1, 1, 2, 3, 5, 11 };
    private static final String EMPTY = "";

    private final Map<CharStack, String> trie;

    public BoggleSolver(String[] dictionary) {
        trie = new HashMap<>(dictionary.length << 4);
        for (int i = 0; i < dictionary.length; ++i) {
            String word = dictionary[i];
            if (isValid(word)) {
                CharStack key = new CharStack();
                int len = word.length();
                int d = 0;
                while (d < len) {
                    char c = word.charAt(d);
                    key.push(c);
                    trie.putIfAbsent(new CharStack(key), EMPTY);
                    d += c == 'Q' ? 2 : 1;
                }
                trie.put(key, word);
            }
        }
        assert Boolean.TRUE;
    }

    private boolean isValid(String word) {
        int len = word.length();
        boolean isValid = len > 2 && word.charAt(len - 1) != 'Q';
        int i = 0;
        while (isValid && i + 1 < len)
            if (word.charAt(i++) == 'Q')
                isValid = word.charAt(i++) == 'U';
        return isValid;
    }

    public Iterable<String> getAllValidWords(BoggleBoard board)
    { return new WordFinder(board).getAllValidWords(); }

    public int scoreOf(String word) {
        int len = word.length();
        if (len > 2 && trie.getOrDefault(key(word), EMPTY) != EMPTY)
            return SCORE[Math.min(len, 8)];
        return 0;
    }

    private CharStack key(String word) {
        CharStack key = new CharStack();
        int len = word.length();
        int d = 0;
        while (d < len) {
            char c = word.charAt(d);
            key.push(c);
            d += c == 'Q' ? 2 : 1;
        }
        return key;
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
            for (int v = 0; v < tray.length; ++v)
                findWords(v, new CharStack());
        }

        private void findWords(int v, CharStack prefix) {
            long slot = tray[v];
            char c = (char) (slot & 0x7f);
            tray[v] |= 1L << 7;
            prefix.push(c);
            String value = trie.get(prefix);
            if (value != null) {
                if (value != EMPTY)
                    words.add(value);
                for (slot >>>= 8; slot != 0; slot >>>= 7) {
                    int w = (int) slot & 0x7f;
                    if ((tray[w] & 1L << 7) == 0)
                        findWords(w, new CharStack(prefix));
                }
            }
            tray[v] &= ~(1L << 7);
        }

        private Iterable<String> getAllValidWords()
        { return words; }

    }

}
