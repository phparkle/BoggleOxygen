import java.util.HashMap;
import java.util.Map;

public class SuperTrie {

    private static final int D = 5;

    private final BitTrie bt = new BitTrie();
    private final Map<CharStack, FlatTST> tsts = new HashMap<>();

    public SuperTrie(String[] keys) {
        int lo = 0, hi = 1;
        while (lo < keys.length) {
            String key = keys[lo];
            if (key.length() > 2) {
                bt.add(key);
                if (key.length() > D) {
                    while (hi < keys.length) {
                        if (key.regionMatches(0, keys[hi], 0, D))
                            ++hi;
                        else break;
                    }
                    tsts.put(new CharStack(key, 0, D),
                             new FlatTST(keys, lo, hi));
                }
            }
            lo = hi++;
        }
    }

    public long query(CharStack prefix) {
        long query = bt.query(prefix);
        if (query != 0 && prefix.length() > D) {
            FlatTST tst = tsts.get(prefix.subSequence(0, D));
            if (tst != null)
                return tst.query(prefix);
            return 0;
        }
        return query;
    }

}
