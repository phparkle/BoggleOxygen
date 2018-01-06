import java.util.Arrays;

public class Dictionary {

    private static final int R = 26;

    private int n;
    private final Node[] roots = new Node[R * R];

    private static class Node {
        private final char c;
        private Node left, mid, right;
        private String val;

        private Node(char c) { this.c = c; }

        @Override
        public String toString() {
            return String.format("%c%c%c%c", c,
                                 left != null ? left.c : '-',
                                 mid != null ? mid.c : '-',
                                 right != null ? right.c : '-');
        }
    }

    public Dictionary(String[] words) {
        Arrays.sort(words, (s1, s2) -> reverse(s1).compareTo(reverse(s2)));
        int lo = 0, hi = 1;
        while (lo < words.length) {
            String word = words[lo];
            while (hi < words.length) {
                String other = words[hi];
                int toffset = word.length() - 2;
                int ooffset = other.length() - 2;
                if (word.regionMatches(toffset, other, ooffset, 2))
                    ++hi;
                else break;
            }
            addAll(words, lo, hi);
            lo = hi++;
        }
    }

    private void addAll(String[] words, int lo, int hi) {
        if (lo < hi) {
            int mid = lo + (hi - lo >> 1);
            add(words[mid]);
            addAll(words, lo, mid);
            addAll(words, mid + 1, hi);
        }
    }

    public void add(String word) {
        String reverse = reverse(word);
        int index = index(reverse);
        if (roots[index] == null)
            roots[index] = new Node(reverse.charAt(2));
        Node x = roots[index];
        int d = 2;
        while (true) {
            char c = reverse.charAt(d);
            if (c < x.c) {
                if (x.left == null)
                    x.left = new Node(reverse.charAt(d));
                x = x.left;
            } else if (c > x.c) {
                if (x.right == null)
                    x.right = new Node(reverse.charAt(d));
                x = x.right;
            } else if (d + 1 < word.length()) {
                ++d;
                if (x.mid == null)
                    x.mid = new Node(reverse.charAt(d));
                x = x.mid;
            } else {
                if (x.val == null) {
                    x.val = word;
                    ++n;
                }
                break;
            }
        }
    }

    public int size() { return n; }

    public boolean contains(String word) {
        String reverse = reverse(word);
        Node x = roots[index(reverse)];
        int d = 2;
        while (x != null) {
            char c = reverse.charAt(d);
            if (c < x.c) {
                x = x.left;
            } else if (c > x.c) {
                x = x.right;
            } else if (d + 1 < word.length()) {
                ++d;
                x = x.mid;
            } else return x.val != null;
        }
        return false;
    }

    public String isReversePrefix(CharSequence prefix) {
        if (prefix.length() == 1)
            return "";
        Node x = roots[index(prefix)];
        if (prefix.length() == 2)
            return x != null ? "" : null;
        int d = 2;
        while (x != null) {
            char c = prefix.charAt(d);
            if (c < x.c) {
                x = x.left;
            } else if (c > x.c) {
                x = x.right;
            } else if (d + 1 < prefix.length()) {
                ++d;
                x = x.mid;
            } else return x.val != null ? x.val : "";
        }
        return null;
    }

    private static String reverse(String s)
    { return new StringBuilder(s).reverse().toString(); }

    private static int index(CharSequence s)
    { return (s.charAt(0) - 'A') * R + (s.charAt(1) - 'A'); }

}
