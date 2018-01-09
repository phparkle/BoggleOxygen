import java.util.HashSet;
import java.util.Set;

public class BoggleSolver {

    private static final int[] SCORE = { 0, 0, 0, 1, 1, 2, 3, 5, 11 };

    private static final long PREFIX = 0b01;
    private static final long KEY    = 0b10;

    private final SuperTrie trie;

    public BoggleSolver(String[] dictionary) {
        trie = new SuperTrie(dictionary);
    }

    public Iterable<String> getAllValidWords(BoggleBoard board)
    { return new WordFinder(board).getAllValidWords(); }

    private class WordFinder {

        private final long[] tray;
        private final boolean[] marked;
        private final Set<String> words;

        private WordFinder(BoggleBoard board) {
            int rows = board.rows();
            int cols = board.cols();
            tray = new long[rows * cols];
            marked = new boolean[rows * cols];
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
                findWords(v, prefix);
        }

        private void findWords(int v, CharStack prefix) {
            marked[v] = true;
            long slot = tray[v];
            char c = (char) (slot & 0xff);
            prefix.push(c);
            if (c == 'Q')
                prefix.push('U');
            long query = trie.query(prefix);
            if (query != 0) {
                if (query == (KEY | PREFIX))
                    words.add(prefix.string());
                for (slot >>>= 8; slot != 0; slot >>>= 7) {
                    int w = (int) slot & 0x7f;
                    if (!marked[w])
                        findWords(w, prefix);
                }
            }
            marked[v] = false;
            if (c == 'Q')
                prefix.pop();
            prefix.pop();
        }

        private Iterable<String> getAllValidWords()
        { return words; }

    }

    public int scoreOf(String word) {
        int len = word.length();
        if (len > 2 && trie.query(new CharStack(word)) == (KEY | PREFIX))
            return SCORE[Math.min(len, 8)];
        return 0;
    }

}
