import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

public class BoggleSolver {

    private static final long PREFIX = 0b01;
    private static final long KEY    = 0b10;

    private final FlatTST tst;

    public BoggleSolver(String[] dictionary) {
        tst = new FlatTST(dictionary, 0, dictionary.length, 0);
    }

    public Iterable<String> getAllValidWords(BoggleBoard board) {
        int rows = board.rows();
        int cols = board.cols();

        char[] letters = new char[rows * cols];
        for (int i = 0; i < rows; ++i)
            for (int j = 0; j < cols; ++j)
                letters[i * cols + j] = board.getLetter(i, j);

        int[] g = new int[rows * cols << 3];
        Arrays.fill(g, -1);
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < cols; ++j) {
                int k = i * cols + j << 3;
                if (j + 1 < cols)
                    g[k++] = i * cols + (j + 1);
                if (i + 1 < rows && j + 1 < cols)
                    g[k++] = (i + 1) * cols + (j + 1);
                if (i + 1 < rows)
                    g[k++] = (i + 1) * cols + j;
                if (i + 1 < rows && j - 1 >= 0)
                    g[k++] = (i + 1) * cols + (j - 1);
                if (j - 1 >= 0)
                    g[k++] = i * cols + (j - 1);
                if (i - 1 >= 0 && j - 1 >= 0)
                    g[k++] = (i - 1) * cols + (j - 1);
                if (i - 1 >= 0)
                    g[k++] = (i - 1) * cols + j;
                if (i - 1 >= 0 && j + 1 < cols)
                    g[k++] = (i - 1) * cols + (j + 1);
            }
        }

        Set<String> words = new TreeSet<>();
        for (int i = 0; i < rows * cols; ++i) {
            boolean[] marked = new boolean[rows * cols];
            int[] adj = new int[rows * cols];
            IntStack path = new IntStack();
            CharStack prefix = new CharStack();
            marked[i] = true;
            path.push(i);
            if (letters[i] == 'Q')
                prefix.push('U');
            prefix.push(letters[i]);
            while (!path.isEmpty()) {
                int v = path.peek();
                if (adj[v] < 8 && g[(v << 3) + adj[v]] != -1) {
                    int w = g[(v << 3) + adj[v]++];
                    if (!marked[w]) {
                        if (letters[w] == 'Q')
                            prefix.push('U');
                        prefix.push(letters[w]);
                        long query = tst.query(prefix, 0);
                        if (query != 0) {
                            if (query == (KEY | PREFIX))
                                words.add(prefix.toString());
                            marked[w] = true;
                            path.push(w);
                        } else if (prefix.pop() == 'Q') {
                            prefix.pop();
                        }
                    }
                } else {
                    marked[v] = false;
                    adj[v] = 0;
                    if (prefix.pop() == 'Q')
                        prefix.pop();
                    path.pop();
                }
            }
        }
        return words;
    }

    public int scoreOf(String word) {
        int length = word.length();
        if (length > 2 && tst.query(word, 0) == 3) {
            switch (length) {
            case 3:
            case 4:
                return 1;
            case 5:
                return 2;
            case 6:
                return 3;
            case 7:
                return 5;
            default:
                return 11;
            }
        } else return 0;
    }

}
