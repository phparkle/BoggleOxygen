import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Out;

public class DictionaryAnalysis {

    public static void main(String[] args) {
        In in = new In(args[0]);
        boolean isPrefix = false;
        int[] stems = new int[26 * 26 * 26];
        while (in.hasNextLine()) {
            String word = in.readLine();
            if (word.length() >= 3) {
                int a = 0;
                for (int i = 0; i < 3; ++i) {
                    a *= 26;
                    a += word.charAt(isPrefix ? i : word.length() - 3 + i) - 'A';
                }
                ++stems[a];
            }
        }
        String format = isPrefix ? "%s_prefix.csv" : "%s_suffix.csv";
        Out out = new Out(String.format(format, args[0]));
        int i = 0;
        for (char c0 = 'A'; c0 <= 'Z'; ++c0) {
            for (char c1 = 'A'; c1 <= 'Z'; ++c1) {
                for (char c2 = 'A'; c2 <= 'Z'; ++c2) {
                    int freq = stems[i++];
                    out.print(String.format("%c%c%c,%d\n", c0, c1, c2, freq));
                }
            }
        }
    }

}
