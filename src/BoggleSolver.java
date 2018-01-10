import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BoggleSolver {

    private static final int[] SCORE = { 0, 0, 0, 1, 1, 2, 3, 5, 11 };

    private final Map<String, String> trie;

    public BoggleSolver(String[] dictionary) {
        trie = new HashMap<>(dictionary.length << 1);
        for (int i = 0; i < dictionary.length; ++i) {
            String word = dictionary[i];
            int len = word.length();
            if (len > 2 && word.charAt(len - 1) != 'Q') {
                boolean isValid = true;
                boolean hasQ = false;
                int j = 0;
                while (isValid && j + 1 < len) {
                    if (word.charAt(j) == 'Q') {
                        isValid = word.charAt(++j) == 'U';
                        hasQ = true;
                    }
                    ++j;
                }
                if (isValid) {
                    if (hasQ) {
                        word = word.replace("QU", "Q");
                        len = word.length();
                    }
                    for (int d = 0; d < len; ++d)
                        trie.putIfAbsent(word.substring(0, d + 1), word);
                }
            }
        }
    }

    public Iterable<String> getAllValidWords(BoggleBoard board)
    { return new WordFinder(board).getAllValidWords(); }

    public int scoreOf(String word) {
        int len = word.length();
        word = word.replace("QU", "Q");
        if (len > 2 && word.equals(trie.get(word)))
            return SCORE[Math.min(len, 8)];
        return 0;
    }

    private class WordFinder {

        private final long[] tray;
        private final Set<String> words;
        private final CharStack prefix;

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
            prefix = new CharStack();
            for (int v = 0; v < tray.length; ++v)
                findWords(v, false);
        }

        private void findWords(int v, boolean hasQ) {
            long slot = tray[v];
            char c = (char) (slot & 0x7f);
            if (c == 'Q')
                hasQ = true;
            tray[v] |= 1L << 7;
            prefix.push(c);
            String word = trie.get(prefix);
            if (word != null) {
                if (word.length() == prefix.length())
                    words.add(!hasQ ? word : word.replace("Q", "QU"));
                for (slot >>>= 8; slot != 0; slot >>>= 7) {
                    int w = (int) slot & 0x7f;
                    if ((tray[w] & 1L << 7) == 0)
                        findWords(w, hasQ);
                }
            }
            tray[v] &= ~(1L << 7);
            prefix.pop();
        }

        private Iterable<String> getAllValidWords()
        { return words; }

    }

}
