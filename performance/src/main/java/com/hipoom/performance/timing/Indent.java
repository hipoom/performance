package com.hipoom.performance.timing;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ZhengHaiPeng
 * @since 2024/8/4 00:11
 */
public class Indent {

    /* ======================================================= */
    /* Fields                                                  */
    /* ======================================================= */

    private static final Map<Integer, String> indents = new HashMap<>();



    /* ======================================================= */
    /* Public Methods                                          */
    /* ======================================================= */

    public static String of(int num) {
        String cache = indents.get(num);
        if (cache != null) {
            return cache;
        }

        String res = blankString(num) + "|-- ";
        indents.put(num, res);
        return res;
    }



    /* ======================================================= */
    /* Private Methods                                         */
    /* ======================================================= */

    private static String blankString(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append("    ");
        }
        return sb.toString();
    }

}
